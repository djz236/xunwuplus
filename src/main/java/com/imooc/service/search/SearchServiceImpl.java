package com.imooc.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
//import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
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
import com.imooc.service.house.IAddressService;
import com.imooc.web.dto.HouseBucketDTO;
import com.imooc.web.form.MapSearch;
import com.imooc.web.form.RentSearch;

/**
 * Created by 瓦力.
 */
@Service
public class SearchServiceImpl implements ISearchService {
	private static final Logger logger = LoggerFactory
			.getLogger(ISearchService.class);

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
				logger.warn("Not support mesage content "+content);
				break;
			}
		} catch (IOException e) {
			logger.error("Cannot parse json for "+content,e);
		}
	}

	private void createOrUpdateIndex(HouseIndexMessage message){
		int houseId=message.getHouseId();
		House house = houseRepository.findOne(houseId);
		if (house == null) {
			logger.error("Index house {} " + "dose not exist!", houseId);
			index(houseId,message.getRetry()+1);
			return;
		}
		HouseIndexTemplate template = new HouseIndexTemplate();
		modelMapper.map(house, template);

		HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
		if (detail == null) {
			// TODO 异常情况
		}

		modelMapper.map(detail, template);
		List<HouseTag> tags = tagRepository.findAllByHouseId(houseId);

		if (tags != null && !tags.isEmpty()) {
			List<String> tagStrings = new ArrayList<>();

			tags.forEach(houseTag -> tagStrings.add(houseTag.getName()));
			template.setTags(tagStrings);
		}

		SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME)
				.setTypes(INDEX_TYPE).setQuery(QueryBuilders
						.termQuery(HouseIndexKey.HOUSE_ID, houseId));

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
//		return success;
	}
	private void removeIndex(HouseIndexMessage message){
		int houseId=message.getHouseId();
		DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
				.newRequestBuilder(esClient).filter(QueryBuilders
						.termQuery(HouseIndexKey.HOUSE_ID, houseId))
				.source(INDEX_NAME);
		logger.debug("Delete by query for house:" + builder);
		BulkByScrollResponse response = builder.get();
		long deleted = response.getDeleted();
		logger.debug("Dlete total "+deleted);
		if(deleted<=0){
			remove(houseId,message.getRetry()+1);
		}
	}
	/*@Override
	public void remove(int houseId) {//使用kafaka 之前
		DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
				.newRequestBuilder(esClient).filter(QueryBuilders
						.termQuery(HouseIndexKey.HOUSE_ID, houseId))
				.source(INDEX_NAME);
		logger.debug("Delete by query for house:" + builder);
		BulkByScrollResponse response = builder.get();
		long deleted = response.getDeleted();
		logger.debug("Dlete total "+deleted);
	}*/
	@Override
	public void remove(int houseId) {//使用kafaka 之后
		 remove(houseId, 0);
	}
	
	public void remove(int houseId,int retry) {//使用kafaka 之后
		
		if(retry>HouseIndexMessage.MAX_RETRY){
			logger.error("Retry remove times over 3 for house"+houseId+"Please check it!");
			return ;
		}
		
		HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry);
		try {
			kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			logger.error("Cannot encode json for "+message,e);
		}
	}

	@Override
	public ServiceMultiResult<Long> query(RentSearch rentSearch) {

		return null;
	}

	@Override
	public ServiceResult<List<String>> suggest(String prefix) {
		CompletionSuggestionBuilder suggestion = SuggestBuilders
				.completionSuggestion("suggest").prefix(prefix).size(5);

		SuggestBuilder suggestBuilder = new SuggestBuilder();
		suggestBuilder.addSuggestion("autocomplete", suggestion);

		SearchRequestBuilder requestBuilder = null;// this.esClient.prepareSearch(INDEX_NAME)
		/*
		 * .setTypes(INDEX_TYPE) .suggest(suggestBuilder);
		 */
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

				for (CompletionSuggestion.Entry.Option option : item
						.getOptions()) {
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
		List<String> suggests = Lists
				.newArrayList(suggestSet.toArray(new String[] {}));
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
	/*@Override
	public boolean index(int houseId) { //没有使用kafka之前 由程序直接调用创建
		House house = houseRepository.findOne(houseId);
		if (house == null) {
			logger.error("Index house {} " + "dose not exist!", houseId);
			return false;
		}
		HouseIndexTemplate template = new HouseIndexTemplate();
		modelMapper.map(house, template);

		HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
		if (detail == null) {
			// TODO 异常情况
		}

		modelMapper.map(detail, template);
		List<HouseTag> tags = tagRepository.findAllByHouseId(houseId);

		if (tags != null && !tags.isEmpty()) {
			List<String> tagStrings = new ArrayList<>();

			tags.forEach(houseTag -> tagStrings.add(houseTag.getName()));
			template.setTags(tagStrings);
		}

		SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME)
				.setTypes(INDEX_TYPE).setQuery(QueryBuilders
						.termQuery(HouseIndexKey.HOUSE_ID, houseId));

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
		return success;
	}
*/
	@Override
	public void index(int houseId) { //使用了kafaka之后
		this.index(houseId,0);
		
	}
	public void index(int houseId,int retry) { //使用了kafaka之后
		
		if(retry>HouseIndexMessage.MAX_RETRY){
			logger.error("Retry index times over 3 for house:"+houseId+" Please chek it!");
			return ;
		}
		HouseIndexMessage message = new HouseIndexMessage(houseId,HouseIndexMessage.INDEX,retry);
		try {
			kafkaTemplate.send(INDEX_TOPIC,objectMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			logger.error("JSON encode error for "+message);
		}
	}
	private boolean create(HouseIndexTemplate indexTemplate) {
		try {
			IndexResponse response = esClient
					.prepareIndex(INDEX_NAME, INDEX_TYPE)
					.setSource(objectMapper.writeValueAsBytes(indexTemplate),
							XContentType.JSON)
					.get();
			logger.debug(
					"Create index with house: " + indexTemplate.getHouseId());
			if (response.status() == RestStatus.CREATED) {
				return true;
			} else {
				return false;
			}
		} catch (JsonProcessingException e) {
			logger.error("Error to index huse:" + indexTemplate.getHouseId(),
					e);
			return false;
		}
	}

	private boolean update(String esid, HouseIndexTemplate indexTemplate) {
		try {
			UpdateResponse response = esClient
					.prepareUpdate(INDEX_NAME, INDEX_TYPE, esid)
					.setDoc(objectMapper.writeValueAsBytes(indexTemplate),
							XContentType.JSON)
					.get();
			logger.debug(
					"Update index with house: " + indexTemplate.getHouseId());
			if (response.status() == RestStatus.OK) {
				return true;
			} else {
				return false;
			}
		} catch (JsonProcessingException e) {
			logger.error("Error to index huse:" + indexTemplate.getHouseId(),
					e);
			return false;
		}
	}

	private boolean deletedAndCreate(long totalHit,
			HouseIndexTemplate indexTemplate) {
		DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
				.newRequestBuilder(esClient)
				.filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID,
						indexTemplate.getHouseId()))
				.source(INDEX_NAME);
		logger.debug("Delete by query for house:" + builder);
		BulkByScrollResponse response = builder.get();
		long deleted = response.getDeleted();
		if (deleted != totalHit) {
			logger.warn("Need delete {},but {} was deleted!", totalHit,
					deleted);
			return false;
		} else {
			return create(indexTemplate);
		}
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
	public ServiceResult<Long> aggregateDistrictHouse(String cityEnName,
			String regionEnName, String district) {
		// TODO Auto-generated method stub
		return null;
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
	public ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy,
			String orderDirection, int start, int size) {
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
		// TODO Auto-generated method stub
		return null;
	}

}
