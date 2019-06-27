package com.imooc.service.search;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.soap.Detail;

import org.apache.logging.log4j.core.util.Integers;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
//import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.imooc.base.HouseSort;
import com.imooc.base.RentValueBlock;
import com.imooc.entity.House;
import com.imooc.entity.HouseDetail;
import com.imooc.entity.HouseTag;
import com.imooc.entity.SupportAddress;
import com.imooc.repository.HouseDetailRepository;
import com.imooc.repository.HouseRepository;
import com.imooc.repository.HouseTagRepository;
import com.imooc.repository.SupportAddressRepository;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.AddressServiceImpl;
import com.imooc.service.house.IAddressService;
import com.imooc.web.dto.HouseBucketDTO;
import com.imooc.web.form.MapSearch;
import com.imooc.web.form.RentSearch;

/**
 * Created by 瓦力.
 */
@Service
public class SearchServiceImpl implements ISearchService {
	private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);
	

	private static final String INDEX_NAME = "xunwuplus";

	private static final String INDEX_TYPE = "house";

	private static final String INDEX_TOPIC = "house_build";
	
	@Autowired
	private HouseRepository houseRepository;

	@Autowired
	private HouseDetailRepository houseDetailRepository;

	@Autowired
	private HouseTagRepository tagRepository;

	@Autowired
	private SupportAddressRepository supportAddressRepository;

	@Autowired
	private IAddressService addressService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private TransportClient esClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@KafkaListener(topics = INDEX_TOPIC)
	private void handleMessage(String content) {
		try {
			HouseIndexMessage message = objectMapper.readValue(content, HouseIndexMessage.class);
			switch (message.getOperation()) {
			case HouseIndexMessage.INDEX:
				createOrUpdateIndex(message);
				break;
			case HouseIndexMessage.REMOVE:
				removeIndex(message);
				break;
			default:
				logger.warn("Not support mesage content " + content);
				break;
			}
		} catch (IOException e) {
			logger.error("Cannot parse json for " + content, e);
		}
	}

	private void createOrUpdateIndex(HouseIndexMessage message) {
		int houseId = message.getHouseId();
		House house = houseRepository.findOne(houseId);
		if (house == null) {
			logger.error("Index house {} " + "dose not exist!", houseId);
			index(houseId, message.getRetry() + 1);
			return;
		}
		HouseIndexTemplate template = new HouseIndexTemplate();
		modelMapper.map(house, template);
		HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
		if (detail == null) {
			// TODO 异常情况
		}

		modelMapper.map(detail, template);
		SupportAddress city = supportAddressRepository.findByEnNameAndLevel(house.getCityEnName(),
				SupportAddress.Level.CITY.getValue());
		SupportAddress region = supportAddressRepository.findByEnNameAndLevel(house.getRegionEnName(),
				SupportAddress.Level.REGION.getValue());
		String address = city.getCnName() + region.getCnName() + house.getStreet() + house.getDistrict()
				+ detail.getDetailAddress();

		ServiceResult<BaiduMapLocation> location = addressService.getBaiduMapLocation(city.getCnName(), address);
		if (!location.isSuccess()) {
			index(message.getHouseId(), message.getRetry() + 1);
			return;
		}

		template.setLocation(location.getResult());

		List<HouseTag> tags = tagRepository.findAllByHouseId(houseId);

		if (tags != null && !tags.isEmpty()) {
			List<String> tagStrings = new ArrayList<>();

			tags.forEach(houseTag -> tagStrings.add(houseTag.getName()));
			template.setTags(tagStrings);
		}

		SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));

		logger.debug(requestBuilder.toString());

		SearchResponse response = requestBuilder.get();
		boolean success;
		long totalHits = response.getHits().getTotalHits();
		if (totalHits == 0) {
			success = create(template);
		} else if (totalHits == 1) {
			String esid = response.getHits().getAt(0).getId();
			success = update(esid, template);
		} else {
			success = deletedAndCreate(totalHits, template);
		}
		if (success) {
			logger.debug("Index success with house " + houseId);
		}
		// return success;
	}

	private void removeIndex(HouseIndexMessage message) {
		int houseId = message.getHouseId();
		DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
				.filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId)).source(INDEX_NAME);
		logger.debug("Delete by query for house:" + builder);
		BulkByScrollResponse response = builder.get();
		long deleted = response.getDeleted();
		logger.debug("Dlete total " + deleted);
		if (deleted <= 0) {
			remove(houseId, message.getRetry() + 1);
		}
	}

	/*
	 * @Override public void remove(int houseId) {//使用kafaka 之前
	 * DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
	 * .newRequestBuilder(esClient).filter(QueryBuilders
	 * .termQuery(HouseIndexKey.HOUSE_ID, houseId)) .source(INDEX_NAME);
	 * logger.debug("Delete by query for house:" + builder);
	 * BulkByScrollResponse response = builder.get(); long deleted =
	 * response.getDeleted(); logger.debug("Dlete total "+deleted); }
	 */
	@Override
	public void remove(int houseId) {// 使用kafaka 之后
		remove(houseId, 0);
	}

	public void remove(int houseId, int retry) {// 使用kafaka 之后

		if (retry > HouseIndexMessage.MAX_RETRY) {
			logger.error("Retry remove times over 3 for house" + houseId + "Please check it!");
			return;
		}

		HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry);
		try {
			kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			logger.error("Cannot encode json for " + message, e);
		}
	}

	@Override
	public ServiceMultiResult<Integer> query(RentSearch rentSearch) {
		// QueryBuilder 是es中提供的一个查询接口, 可以对其进行参数设置来进行查用
		// .matchAllQuery()
		//
		// matchAllQuery()方法用来匹配全部文档
		// matchQuery("filedname","value")匹配单个字段，匹配字段名为filedname,值为value的文档
		// .multiMatchQuery(Object text, String... fieldNames)
		//
		// 多个字段匹配某一个值
		// wildcardQuery()模糊查询
		//
		// 模糊查询，?匹配单个字符，*匹配多个字符
		// 使用BoolQueryBuilder进行复合查询
		//
		// 使用must
		// 通过from和size参数进行分页。From定义查询结果开始位置，size定义返回的hits（一条hit对应一条记录）最大数量。
		/**
		 * boolQuery 组合查询 must(QueryBuilders) : AND mustNot(QueryBuilders): NOT
		 * should: : OR
		 */

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		/*
		 * Elasticsearch在2.x版本的时候把filter查询给摘掉了，因此在query dsl里面已经找不到filter query了。
		 * 其实es并没有完全抛弃filter query，而是它的设计与之前的query太重复了。因此直接给转移到了bool查询中。
		 * Bool查询现在包括四种子句，must，filter,should,must_not。
		 * 看上面的流程图就能很明显的看到，filter与query还是有很大的区别的。
		 * 
		 * 比如，query的时候，会先比较查询条件，然后计算分值，最后返回文档结果；
		 * 
		 * 而filter则是先判断是否满足查询条件，如果不满足，会缓存查询过程（记录该文档不满足结果）；满足的话，就直接缓存结果。
		 * 
		 * 综上所述，filter快在两个方面：
		 * 
		 * 1 对结果进行缓存 2 避免计算分值
		 */

		/*
		 * bool查询的使用 Bool查询对应Lucene中的BooleanQuery，它由一个或者多个子句组成，每个子句都有特定的类型。
		 * 
		 * must 返回的文档必须满足must子句的条件，并且参与计算分值
		 * 
		 * filter 返回的文档必须满足filter子句的条件。但是不会像Must一样，参与计算分值
		 * 
		 * should
		 * 返回的文档可能满足should子句的条件。在一个Bool查询中，如果没有must或者filter，有一个或者多个should子句，
		 * 那么只要满足一个就可以返回。minimum_should_match参数定义了至少满足几个子句。
		 * 
		 * must_nout 返回的文档必须不满足must_not定义的条件。
		 * 
		 * match query搜索的时候，首先会解析查询字符串，进行分词，然后查询，而term
		 * query,输入的查询内容是什么，就会按照什么去查询，并不会解析查询内容，对它分词。
		 */
		boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, rentSearch.getCityEnName()));
		if (rentSearch.getRegionEnName() != null && !"*".equals(rentSearch.getRegionEnName())) {
			boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, rentSearch.getRegionEnName()));
		}
		RentValueBlock area = RentValueBlock.matchArea(rentSearch.getAreaBlock());
		if (!RentValueBlock.ALL.equals(area)) {
			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.AREA);
			if (area.getMax() > 0) {
				rangeQueryBuilder.lte(area.getMax());
			}
			if (area.getMin() > 0) {
				rangeQueryBuilder.gte(area.getMin());
			}
			boolQuery.filter(rangeQueryBuilder);
		}
		RentValueBlock price = RentValueBlock.matchPrice(rentSearch.getPriceBlock());
		if (!RentValueBlock.ALL.equals(price)) {
			RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(HouseIndexKey.PRICE);
			if (price.getMax() > 0) {
				rangeQuery.lte(price.getMax());
			}
			if (price.getMin() > 0) {
				rangeQuery.gte(price.getMin());
			}
			boolQuery.filter(rangeQuery);
		}
		if (rentSearch.getDirection() > 0) {
			boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.DIRECTION, rentSearch.getDirection()));
		}
		if (rentSearch.getRentWay() > -1) {
			boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.RENT_WAY, rentSearch.getRentWay()));
		}
		/*
		 * 优化 boolQuery.must(QueryBuilders.matchQuery(HouseIndexKey.TITLE,
		 * rentSearch.getKeywords()).boost(2.0f));
		 * boolQuery.should(QueryBuilders.matchQuery(HouseIndexKey.TITLE,
		 * rentSearch.getKeywords()).boost(2.0f));
		 * 
		 * boolQuery.should(
		 * QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
		 * HouseIndexKey.TITLE, HouseIndexKey.TRAFFIC, HouseIndexKey.DISTRICT,
		 * HouseIndexKey.ROUND_SERVICE, HouseIndexKey.SUBWAY_LINE_NAME,
		 * HouseIndexKey.SUBWAY_STATION_NAME ));
		 */

		/*
		 * 相对于matchQuery，multiMatchQuery针对的是多个field，
		 * 也就是说，当multiMatchQuery中，fieldNames参数只有一个时，其作用与matchQuery相当；
		 * 而当fieldNames有多个参数时，如field1和field2，那查询的结果中，要么field1中包含text，
		 * 要么field2中包含text。
		 */
		boolQuery.must(QueryBuilders.multiMatchQuery(rentSearch.getKeywords(), HouseIndexKey.TITLE,
				HouseIndexKey.TRAFFIC, HouseIndexKey.DISTRICT, HouseIndexKey.ROUND_SERVICE,
				HouseIndexKey.SUBWAY_LINE_NAME, HouseIndexKey.SUBWAY_STATION_NAME));

		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQuery)
				.addSort(HouseSort.getSortKey(rentSearch.getOrderBy()),
						SortOrder.fromString(rentSearch.getOrderDirection()))
				.setFrom(rentSearch.getStart()).setSize(rentSearch.getSize())
				.setFetchSource(HouseIndexKey.HOUSE_ID, null)// 防止返回的数据集过大的情况
																// 只返回关键字段
		;

		System.out.println(requestBuilder.toString());
		logger.debug(requestBuilder.toString());
		List<Integer> houseIds = new ArrayList<>();
		SearchResponse response = requestBuilder.get();

		if (response.status() != RestStatus.OK) {
			logger.warn("Search status is no ok for " + requestBuilder);
			return new ServiceMultiResult<>(0, houseIds);
		}
		for (SearchHit hit : response.getHits()) {
			System.out.println(hit.getSource());
			houseIds.add(Integers.parseInt(String.valueOf(hit.getSource().get(HouseIndexKey.HOUSE_ID))));
		}

		return new ServiceMultiResult<>(response.getHits().totalHits, houseIds);
	}

	@Override
	public ServiceResult<List<String>> suggest(String prefix) {
		CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest").prefix(prefix).size(5);

		SuggestBuilder suggestBuilder = new SuggestBuilder();
		suggestBuilder.addSuggestion("autocomplete", suggestion);

		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)

				.setTypes(INDEX_TYPE).suggest(suggestBuilder);

		logger.debug(requestBuilder.toString());

		SearchResponse response = requestBuilder.get();
		Suggest suggest = response.getSuggest();
		if (suggest == null) {
			return ServiceResult.of(new ArrayList<>());
		}
		Suggest.Suggestion result = suggest.getSuggestion("autocomplete");

		int maxSuggest = 0;
		Set<String> suggestSet = new HashSet<>();

		for (Object term : result.getEntries()) {
			if (term instanceof CompletionSuggestion.Entry) {
				CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;

				if (item.getOptions().isEmpty()) {
					continue;
				}

				for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
					String tip = option.getText().string();
					if (suggestSet.contains(tip)) {
						continue;
					}
					suggestSet.add(tip);
					maxSuggest++;
				}
			}

			if (maxSuggest > 5) {
				break;
			}
		}
		List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[] {}));
		return ServiceResult.of(suggests);
	}

	/**
	 * <p>
	 * Title: index
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param houseId
	 * @see com.imooc.service.search.ISearchService#index(int)
	 */
	/*
	 * @Override public boolean index(int houseId) { //没有使用kafka之前 由程序直接调用创建
	 * House house = houseRepository.findOne(houseId); if (house == null) {
	 * logger.error("Index house {} " + "dose not exist!", houseId); return
	 * false; } HouseIndexTemplate template = new HouseIndexTemplate();
	 * modelMapper.map(house, template);
	 * 
	 * HouseDetail detail = houseDetailRepository.findByHouseId(houseId); if
	 * (detail == null) { // TODO 异常情况 }
	 * 
	 * modelMapper.map(detail, template); List<HouseTag> tags =
	 * tagRepository.findAllByHouseId(houseId);
	 * 
	 * if (tags != null && !tags.isEmpty()) { List<String> tagStrings = new
	 * ArrayList<>();
	 * 
	 * tags.forEach(houseTag -> tagStrings.add(houseTag.getName()));
	 * template.setTags(tagStrings); }
	 * 
	 * SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME)
	 * .setTypes(INDEX_TYPE).setQuery(QueryBuilders
	 * .termQuery(HouseIndexKey.HOUSE_ID, houseId));
	 * 
	 * logger.debug(requestBuilder.toString());
	 * 
	 * SearchResponse response = requestBuilder.get(); boolean success; long
	 * totalHits = response.getHits().getTotalHits(); if (totalHits == 0) {
	 * success = create(template); } else if (totalHits == 1) { String esid =
	 * response.getHits().getAt(0).getId(); success = update(esid, template); }
	 * else { success = deletedAndCreate(totalHits, template); } if (success) {
	 * logger.debug("Index success with house " + houseId); } return success; }
	 */
	@Override
	public void index(int houseId) { // 使用了kafaka之后
		this.index(houseId, 0);

	}

	public void index(int houseId, int retry) { // 使用了kafaka之后

		if (retry > HouseIndexMessage.MAX_RETRY) {
			logger.error("Retry index times over 3 for house:" + houseId + " Please chek it!");
			return;
		}
		HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, retry);
		try {
			kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			logger.error("JSON encode error for " + message);
		}
	}

	private boolean create(HouseIndexTemplate indexTemplate) {
		boolean updateSuggest = updateSuggest(indexTemplate);

		if (!updateSuggest) {
			return false;
		}
		try {
			IndexResponse response = esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
					.setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
			logger.debug("Create index with house: " + indexTemplate.getHouseId());
			if (response.status() == RestStatus.CREATED) {
				return true;
			} else {
				return false;
			}
		} catch (JsonProcessingException e) {
			logger.error("Error to index huse:" + indexTemplate.getHouseId(), e);
			return false;
		}
	}

	private boolean update(String esid, HouseIndexTemplate indexTemplate) {
		boolean updateSuggest = updateSuggest(indexTemplate);

		if (!updateSuggest) {
			return false;
		}
		try {
			UpdateResponse response = esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esid)
					.setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
			logger.debug("Update index with house: " + indexTemplate.getHouseId());
			if (response.status() == RestStatus.OK) {
				return true;
			} else {
				return false;
			}
		} catch (JsonProcessingException e) {
			logger.error("Error to index huse:" + indexTemplate.getHouseId(), e);
			return false;
		}
	}

	private boolean deletedAndCreate(long totalHit, HouseIndexTemplate indexTemplate) {
		DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
				.filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, indexTemplate.getHouseId())).source(INDEX_NAME);
		logger.debug("Delete by query for house:" + builder);
		BulkByScrollResponse response = builder.get();
		long deleted = response.getDeleted();
		if (deleted != totalHit) {
			logger.warn("Need delete {},but {} was deleted!", totalHit, deleted);
			return false;
		} else {
			return create(indexTemplate);
		}
	}

	public boolean updateSuggest(HouseIndexTemplate indexTemplate) {
		AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(this.esClient, AnalyzeAction.INSTANCE,
				INDEX_NAME, indexTemplate.getTitle(), indexTemplate.getLayoutDesc(), indexTemplate.getRoundService(),
				indexTemplate.getDescription(), indexTemplate.getSubwayLineName(),
				indexTemplate.getSubwayStationName());

		requestBuilder.setAnalyzer("ik_smart");

		AnalyzeResponse response = requestBuilder.get();
		List<AnalyzeToken> tokens = response.getTokens();
		if (tokens == null) {
			logger.warn("Can not analyze token for house:" + indexTemplate.getHouseId());
			return false;
		}

		List<HouseSuggest> suggests = new ArrayList<>();
		for (AnalyzeResponse.AnalyzeToken token : tokens) {
			// 排序数字类型 &小于2个字符的分词结果
			if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
				continue;
			}
			HouseSuggest suggest = new HouseSuggest();
			suggest.setInput(token.getTerm());
			suggests.add(suggest);
		}

		// 定制化小区自动补全 ？？？？？？？？？？？？？？？？？
		HouseSuggest suggest = new HouseSuggest();
		suggest.setInput(indexTemplate.getDistrict());
		suggests.add(suggest);

		indexTemplate.setSuggest(suggests);

		return true;
	}

	/**
	 * <p>
	 * Title: aggregateDistrictHouse
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param cityEnName
	 * @param regionEnName
	 * @param district
	 * @return
	 * @see com.imooc.service.search.ISearchService#aggregateDistrictHouse(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public ServiceResult<Integer> aggregateDistrictHouse(String cityEnName, String regionEnName, String district) {

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
				.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName))
				.filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, regionEnName))
				.filter(QueryBuilders.termQuery(HouseIndexKey.DISTRICT, district));

		SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQuery)
				.addAggregation(AggregationBuilders.terms(HouseIndexKey.AGG_DISTRICT).field(HouseIndexKey.DISTRICT));

		logger.debug(requestBuilder.toString());
		SearchResponse response = requestBuilder.get();
		if (response.status() == RestStatus.OK) {
			Terms terms = response.getAggregations().get(HouseIndexKey.AGG_DISTRICT);
			if (terms.getBuckets() != null && !terms.getBuckets().isEmpty()) {
				return ServiceResult.of(Integer.valueOf(terms.getBucketByKey(district).getDocCount() + ""));
			}
		} else {
			logger.warn("Fail to Aggregate for " + HouseIndexKey.AGG_DISTRICT);
		}
		return ServiceResult.of(0);
	}

	/**
	 * <p>
	 * Title: mapAggregate
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param cityEnName
	 * @return
	 * @see com.imooc.service.search.ISearchService#mapAggregate(java.lang.String)
	 */
	@Override
	public ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName) {

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName));

		AggregationBuilder builder = AggregationBuilders.terms(HouseIndexKey.AGG_REGION)
				.field(HouseIndexKey.REGION_EN_NAME);
		SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQuery).addAggregation(builder);

		logger.debug(requestBuilder.toString());
		SearchResponse response = requestBuilder.get();
		ArrayList<HouseBucketDTO> list = new ArrayList<HouseBucketDTO>();
		if (response.status() != RestStatus.OK) {
			logger.warn("Aggregate status is not ok for " + requestBuilder);
			return new ServiceMultiResult<>(0, list);

		}

		Terms terms = response.getAggregations().get(HouseIndexKey.AGG_REGION);
		for (Terms.Bucket buket : terms.getBuckets()) {
			list.add(new HouseBucketDTO(buket.getKeyAsString(), buket.getDocCount()));
		}

		return new ServiceMultiResult<>(response.getHits().getTotalHits(), list);
	}

	/**
	 * <p>
	 * Title: mapQuery
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param cityEnName
	 * @param orderBy
	 * @param orderDirection
	 * @param start
	 * @param size
	 * @return
	 * @see com.imooc.service.search.ISearchService#mapQuery(java.lang.String,
	 *      java.lang.String, java.lang.String, int, int)
	 */
	@Override
	public ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy, String orderDirection, int start,
			int size) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * <p>
	 * Title: mapQuery
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param mapSearch
	 * @return
	 * @see com.imooc.service.search.ISearchService#mapQuery(com.imooc.web.form.MapSearch)
	 */
	@Override
	public ServiceMultiResult<Long> mapQuery(MapSearch mapSearch) {
	return null;
	}

}
