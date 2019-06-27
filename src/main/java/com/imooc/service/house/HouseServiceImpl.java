/*********************************************************
 *********************************************************
 ********************                  *******************
 *************                                ************
 *******                  _oo0oo_                  *******
 ***                     o8888888o                     ***
 *                       88" . "88                       *
 *                       (| -_- |)                       *
 *                       0\  =  /0                       *
 *                     ___/`---'\___                     *
 *                   .' \\|     |// '.                   *
 *                  / \\|||  :  |||// \                  *
 *                 / _||||| -:- |||||- \                 *
 *                |   | \\\  -  /// |   |                *
 *                | \_|  ''\---/''  |_/ |                *
 *                \  .-\__  '-'  ___/-. /                *
 *              ___'. .'  /--.--\  `. .'___              *
 *           ."" '<  `.___\_<|>_/___.' >' "".            *
 *          | | :  `- \`.;`\ _ /`;.`/ - ` : | |          *
 *          \  \ `_.   \_ __\ /__ _/   .-` /  /          *
 *      =====`-.____`.___ \_____/___.-`___.-'=====       *
 *                        `=---='                        *
 *      ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~      *
 *********__佛祖保佑__永无BUG__验收通过__钞票多多__*********
 *********************************************************/
package com.imooc.service.house;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.imooc.base.HouseSort;
import com.imooc.base.HouseStatus;
import com.imooc.base.LoginUserUtil;
import com.imooc.entity.House;
import com.imooc.entity.HouseDetail;
import com.imooc.entity.HousePicture;
import com.imooc.entity.HouseSubscribe;
import com.imooc.entity.HouseTag;
import com.imooc.entity.Subway;
import com.imooc.entity.SubwayStation;
import com.imooc.repository.HouseDetailRepository;
import com.imooc.repository.HousePictureRepository;
import com.imooc.repository.HouseRepository;
import com.imooc.repository.HouseSubscribeRespository;
import com.imooc.repository.HouseTagRepository;
import com.imooc.repository.SubwayRepository;
import com.imooc.repository.SubwayStationRepository;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.search.ISearchService;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.dto.HouseDetailDTO;
import com.imooc.web.dto.HousePictureDTO;
import com.imooc.web.form.DatatableSearch;
import com.imooc.web.form.HouseForm;
import com.imooc.web.form.MapSearch;
import com.imooc.web.form.PhotoForm;
import com.imooc.web.form.RentSearch;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

/**
 * @ClassName: HouseService
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月27日 下午4:18:55
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
public class HouseServiceImpl implements IHouseService {
	@Autowired
	private HouseTagRepository houseTagRepository;
	@Autowired
	private HouseDetailRepository houseDetailRepository;
	@Autowired
	private HousePictureRepository housePictureRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private HouseRepository houseRepository;
	@Value("${qiniu.cdn.prefix}")
	private String cdnPrefix;
	@Autowired
	private SubwayRepository subwayRepository;
	@Autowired
	private SubwayStationRepository subwayStationRepository;
	@Autowired
	private HouseSubscribeRespository subscribeRespository;
	
	@Autowired
	private IQiNiuService qiNiuService;
	
	@Autowired
	private ISearchService searchService;
	/**
	 * <p>
	 * Title: adminQuery
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param searchBody
	 * @return
	 * @see com.imooc.service.house.IHouseService#adminQuery(com.imooc.web.form.DatatableSearch)
	 */
	@Override
	public ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody) {
		List<HouseDTO> houseDTOS = new ArrayList<>();

		Sort sort = new Sort(
				Sort.Direction.fromString(searchBody.getDirection()),
				searchBody.getOrderBy());

		int page = searchBody.getStart() / searchBody.getLength();
		Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);
		// Specification查询构造器
		Specification<House> specification = (root, query, cb) -> {
			Predicate predicate = cb.equal(root.get("adminId"),
					LoginUserUtil.getLoginUserId());
			predicate = cb.and(predicate, cb.notEqual(root.get("status"),
					HouseStatus.DELETED.getValue()));

			if (searchBody.getCity() != null) {
				predicate = cb.and(predicate,
						cb.equal(root.get("cityEnName"), searchBody.getCity()));
			}
			if (searchBody.getStatus() != null) {
				predicate = cb.and(predicate,
						cb.equal(root.get("status"), searchBody.getStatus()));
			}
			if (searchBody.getCreateTimeMin() != null) {
				predicate = cb.and(predicate, cb.greaterThanOrEqualTo(
						root.get("createTime"), searchBody.getCreateTimeMin()));
			}
			if (searchBody.getCreateTimeMax() != null) {
				predicate = cb.and(predicate, cb.lessThanOrEqualTo(
						root.get("createTime"), searchBody.getCreateTimeMax()));
			}
			if (searchBody.getTitle() != null) {
				predicate = cb.and(predicate, cb.like(root.get("title"),
						"%" + searchBody.getTitle() + "%"));
			}
			return predicate;
		};

		Page<House> houses = houseRepository.findAll(specification, pageable);
		houses.forEach(house -> {
			HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
			houseDTO.setCover(this.cdnPrefix + house.getCover());
			houseDTOS.add(houseDTO);
		});
		ServiceMultiResult<HouseDTO> result = new ServiceMultiResult<>(
				houses.getTotalElements(), houseDTOS);
		return result;
	}

	/**   
	 * <p>Title: update</p>   
	 * <p>Description: </p>   
	 * @param houseForm
	 * @return   
	 * @see com.imooc.service.house.IHouseService#update(com.imooc.web.form.HouseForm)   
	 */
	@Override
	@Transactional
	public ServiceResult update(HouseForm houseForm) {
		House house = houseRepository.findOne(houseForm.getId());
		if(house==null){
			return ServiceResult.notFound();
		}
		HouseDetail detail = houseDetailRepository.findByHouseId(house.getId());
		
		
		if(detail==null){
			return ServiceResult.notFound();
		}
		ServiceResult<HouseDTO> wrapperResult = wrapperDetailInfo(detail,houseForm);
		if(wrapperResult!=null){
			return wrapperResult;
		}
		houseDetailRepository.save(detail);
		
		List<HousePicture> pictures = generatePictures(houseForm, houseForm.getId());
		
		housePictureRepository.save(pictures);
		if(houseForm.getCover()==null){
			houseForm.setCover(house.getCover());
		}
		modelMapper.map(houseForm, house);
		house.setLastUpdateTime(new Date());
		houseRepository.save(house);
		if(house.getStatus()==HouseStatus.PASSES.getValue()){
			searchService.index(house.getId());
		}
		 
		return ServiceResult.success();
	}

	/**
	 * 图片对象列表信息填充
	 * 
	 * @param form
	 * @param houseId
	 * @return
	 */
	private List<HousePicture> generatePictures(HouseForm houseForm,
			int houseId) {
		ArrayList<HousePicture> pictures = new ArrayList<>();
		if (houseForm.getPhotos() == null || houseForm.getPhotos().isEmpty()) {
			return pictures;
		}
		for(PhotoForm photoForm:houseForm.getPhotos()){
			HousePicture picture = new HousePicture();
			picture.setHouseId(houseId);
			picture.setCdnPrefix(cdnPrefix);
			picture.setPath(photoForm.getPath());
			picture.setWidth(photoForm.getWidth());
			picture.setHeight(photoForm.getHeight());
			pictures.add(picture);
		}
		return pictures;
	}

	/**   
	 * @Title: wrapperDetailInfo   
	 * @Description: 房源详细信息对象填充  
	 * @param: @param houseDetail
	 * @param: @param houseForm
	 * @param: @return      
	 * @return: ServiceResult<HouseDTO>      
	 * @throws   
	 */
	private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail houseDetail,
			HouseForm houseForm) {
		Subway subway = subwayRepository.findOne(houseForm.getSubwayLineId());
		if (subway == null) {
			return new ServiceResult<>(false, "Not valid subway line!");
		}

		SubwayStation subwayStation = subwayStationRepository
				.findOne(houseForm.getSubwayStationId());
		if (subwayStation == null
				|| subway.getId() != subwayStation.getSubwayId()) {
			return new ServiceResult<>(false, "Not valid subway station!");
		}

		houseDetail.setSubwayLineId(subway.getId());
		houseDetail.setSubwayLineName(subway.getName());

		houseDetail.setSubwayStationId(subwayStation.getId());
		houseDetail.setSubwayStationName(subwayStation.getName());

		houseDetail.setDescription(houseForm.getDescription());
		houseDetail.setDetailAddress(houseForm.getDetailAddress());
		houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
		houseDetail.setRentWay(houseForm.getRentWay());
		houseDetail.setRoundService(houseForm.getRoundService());
		houseDetail.setTraffic(houseForm.getTraffic());
		return null;
	}

	/**   
	 * <p>Title: findCompleteOne</p>   
	 * <p>Description: </p>   
	 * @param id
	 * @return   
	 * @see com.imooc.service.house.IHouseService#findCompleteOne(java.lang.Long)   
	 */
	@Override
	public ServiceResult<HouseDTO> findCompleteOne(int id) {
		House house = houseRepository.findOne(id);
		if(house==null){
			return ServiceResult.notFound();
		}
		
		HouseDetail detail = houseDetailRepository.findByHouseId(id);
		List<HousePicture> pictures = housePictureRepository.findAllByHouseId(id);
		HouseDetailDTO detailDTO = modelMapper.map(detail, HouseDetailDTO.class);
		List<HousePictureDTO> pictureDTOS=new ArrayList<>();
		for(HousePicture picture:pictures){
			HousePictureDTO pictureDTO = modelMapper.map(picture, HousePictureDTO.class);
			pictureDTOS.add(pictureDTO);
		}
		List<HouseTag> tags=houseTagRepository.findAllByHouseId(id);
		ArrayList<String> tagList = new ArrayList<>();
		for(HouseTag tag:tags){
			tagList.add(tag.getName());
		}
		HouseDTO result = modelMapper.map(house, HouseDTO.class);
		result.setHouseDetail(detailDTO);
		result.setPictures(pictureDTOS);
		result.setTags(tagList);
		if(LoginUserUtil.getLoginUserId()>0){//已登录用户
			HouseSubscribe subscribe = subscribeRespository.findByHouseIdAndUserId(house.getId(), LoginUserUtil.getLoginUserId());
			if(subscribe!=null){
				result.setSubscribeStatus(subscribe.getStatus());
			}
		}
		
		return ServiceResult.of(result);
	}

	/**   
	 * <p>Title: addTag</p>   
	 * <p>Description: </p>   
	 * @param houseId
	 * @param tag
	 * @return   
	 * @see com.imooc.service.house.IHouseService#addTag(java.lang.Long, java.lang.String)   
	 */
	@Override
	@Transactional
	public ServiceResult addTag(int houseId, String tag) {
		House house = houseRepository.findOne(houseId);
		if(house==null){
			return ServiceResult.notFound();
		}
		HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tag, houseId);
		if(houseTag!=null){
			return new ServiceResult(false,"标签已存在");
		}
		houseTagRepository.save(new HouseTag(houseId,tag));
		
		return ServiceResult.success();
	}

	/**   
	 * <p>Title: removeTag</p>   
	 * <p>Description: </p>   
	 * @param houseId
	 * @param tag
	 * @return   
	 * @see com.imooc.service.house.IHouseService#removeTag(int, java.lang.String)   
	 */
	@Override
	@Transactional
	public ServiceResult removeTag(int houseId, String tag) {
		House house = houseRepository.findOne(houseId);
		if(house==null){
			return ServiceResult.notFound();
		}
		HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tag, houseId);
		if(houseTag==null){
			return new ServiceResult(false,"标签不存在");
		}
		
		houseTagRepository.delete(houseTag.getId());
		return ServiceResult.success();
	}

	/**   
	 * <p>Title: removePhoto</p>   
	 * <p>Description: </p>   
	 * @param id
	 * @return   
	 * @see com.imooc.service.house.IHouseService#removePhoto(int)   
	 */
	@Override
	public ServiceResult removePhoto(int id) {
		HousePicture picture = housePictureRepository.findOne(id);
		if(picture==null){
			return ServiceResult.notFound();
		}
		try {
			Response response = qiNiuService.delete(picture.getPath());
			if(response.isOK()){
				housePictureRepository.delete(id);
				return ServiceResult.success();
			}else{
				return new ServiceResult(false,response.error);
			}
		} catch (QiniuException e) {
			e.printStackTrace();
			return new ServiceResult(false,e.getMessage());
		}
	}

	/**   
	 * <p>Title: updateCover</p>   
	 * <p>Description: </p>   
	 * @param coverId
	 * @param targetId
	 * @return   
	 * @see com.imooc.service.house.IHouseService#updateCover(java.lang.Long, java.lang.Long)   
	 */
	@Override
	@Transactional
	public ServiceResult updateCover(int coverId, int targetId) {
		HousePicture cover = housePictureRepository.findOne(coverId);
		if(cover==null){
			return ServiceResult.notFound();
		}
		houseRepository.updateCover(targetId, cover.getPath());
		return ServiceResult.success();
	}

	/**   
	 * <p>Title: save</p>   
	 * <p>Description: </p>   
	 * @param houseForm
	 * @return   
	 * @see com.imooc.service.house.IHouseService#save(com.imooc.web.form.HouseForm)   
	 */
	@Override
	public ServiceResult<HouseDTO> save(HouseForm houseForm) {
		HouseDetail detail = new HouseDetail();
		 ServiceResult<HouseDTO> subwayValidtionResult =wrapperDetailInfo(detail,houseForm);
		if(subwayValidtionResult!=null){
			return subwayValidtionResult;
		}
		House house = new House();
		modelMapper.map(houseForm, house);
		Date now=new Date();
		house.setCreateTime(now);
		house.setLastUpdateTime(now);
		house.setAdminId(LoginUserUtil.getLoginUserId());
		house = houseRepository.save(house);
		
		detail.setHouseId(house.getId());
		detail=houseDetailRepository.save(detail);
		
		List<HousePicture> pictures = generatePictures(houseForm, house.getId());
		Iterable<HousePicture> housePictures = housePictureRepository.save(pictures);
		HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
		
		HouseDetailDTO houseDetailDTO = modelMapper.map(detail, HouseDetailDTO.class);
		houseDTO.setHouseDetail(houseDetailDTO);
		List<HousePictureDTO> pictureDTOS=new ArrayList<>();
		
		housePictures.forEach(housePicture->pictureDTOS.add(
				modelMapper.map(housePicture,
						HousePictureDTO.class)));
		houseDTO.setPictures(pictureDTOS);
		houseDTO.setCover(cdnPrefix+houseDTO.getCover());
		List<String> tags = houseForm.getTags();
		if(tags!=null&&!tags.isEmpty()){
			List<HouseTag> houseTags=new ArrayList<>();
			for(String tag:tags){
				houseTags.add(new HouseTag(house.getId(),tag));
			}
			houseTagRepository.save(houseTags);
			houseDTO.setTags(tags);
		}
		
		return new ServiceResult<HouseDTO>(true,null,houseDTO);
	}

	/**   
	 * <p>Title: updateStatus</p>   
	 * <p>Description: </p>   
	 * @param id
	 * @param status
	 * @return   
	 * @see com.imooc.service.house.IHouseService#updateStatus(int, int)   
	 */
	@Override
	@Transactional
	public ServiceResult updateStatus(int id, int status) {
		House house = houseRepository.findOne(id);
		if(house==null){
			return ServiceResult.notFound();
		}
		if(house.getStatus()==status){
			return new ServiceResult(false,"状态没有变化");
		}
		
		if(house.getStatus()==HouseStatus.RENTED.getValue()){
			return new ServiceResult(false,"已出租的房源不允许修改状态");
		}
		
		if(house.getStatus()==HouseStatus.DELETED.getValue()){
			return new ServiceResult(false,"已删除的资源不允许操作");
		}
		houseRepository.updateStatus(id, status);
		//上架更新索引其他情况都要删除索引
		if(status==HouseStatus.PASSES.getValue()){
			searchService.index(id);
		}else{
			searchService.remove(id);
		}
		return ServiceResult.success();
	}

	/**   
	 * <p>Title: query</p>   
	 * <p>Description: </p>   
	 * @param rentSearch
	 * @return   
	 * @see com.imooc.service.house.IHouseService#query(com.imooc.web.form.RentSearch)   
	 */
	@Override
	public ServiceMultiResult<HouseDTO> query(RentSearch rentSearch) {
		if(rentSearch.getKeywords()!=null&&!rentSearch.getKeywords().isEmpty()){
			ServiceMultiResult<Integer> serviceResult=searchService.query(rentSearch);
			if(serviceResult.getTotal()==0){
				return new ServiceMultiResult<>(0, new ArrayList<>());
			}
			return new ServiceMultiResult<>(serviceResult.getTotal(), wrapperHouseResult(serviceResult.getResult()));
		}
		
		
		return simpleQuery(rentSearch);
	}

	/**   
	 * @Title: simpleQuery   
	 * @Description: TODO(这里用一句话描述这个方法的作用)   
	 * @param: @param rentSearch
	 * @param: @return      
	 * @return: ServiceMultiResult<HouseDTO>      
	 * @throws   
	 */
	private ServiceMultiResult<HouseDTO> simpleQuery(RentSearch rentSearch) {
		Sort sort = HouseSort.generateSort(rentSearch.getOrderBy(), rentSearch.getOrderDirection());
		int page=rentSearch.getStart()/rentSearch.getSize();
		Pageable pageable = new PageRequest(page,rentSearch.getSize(),sort);
		Specification<House> specification=(root,criteriaQuery,criteriaBuilder)->{
			Predicate predicate = criteriaBuilder.equal(root.get("status"), HouseStatus.PASSES.getValue());
			
			predicate=criteriaBuilder.and(predicate,criteriaBuilder.equal(root.get("cityEnName"), rentSearch.getCityEnName()));
			if(HouseSort.DISTANCE_TO_SUBWAY_KEY.equals(rentSearch.getOrderBy())){
				predicate=criteriaBuilder.and(predicate,criteriaBuilder.gt(root.get(HouseSort.DISTANCE_TO_SUBWAY_KEY),-1));
			}
			return predicate;
		};
		
		Page<House> houses = houseRepository.findAll(specification,pageable);
		List<HouseDTO> houseDTOS = new ArrayList<>();
		
		ArrayList<Integer> houseIds = new ArrayList<>();
		HashMap<Integer, HouseDTO> idToHouseMap = Maps.newHashMap();
		houses.forEach(house->{
			HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
			houseDTO.setCover(cdnPrefix+house.getCover());
			houseDTOS.add(houseDTO);
			houseIds.add(house.getId());
			idToHouseMap.put(house.getId(), houseDTO);
		});
		
		wrapperHouseList(houseIds,idToHouseMap);
		return new ServiceMultiResult<HouseDTO>(houses.getTotalElements(), houseDTOS);
	}
	   /**
     * 渲染详细信息 及 标签
     * @param houseIds
     * @param idToHouseMap
     */
    private void wrapperHouseList(List<Integer> houseIds, Map<Integer, HouseDTO> idToHouseMap) {
        List<HouseDetail> details = houseDetailRepository.findAllByHouseIdIn(houseIds);
        details.forEach(houseDetail -> {
            HouseDTO houseDTO = idToHouseMap.get(houseDetail.getHouseId());
            HouseDetailDTO detailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
            houseDTO.setHouseDetail(detailDTO);
        });

        List<HouseTag> houseTags = houseTagRepository.findAllByHouseIdIn(houseIds);
       houseTags.forEach(houseTag -> {
            HouseDTO house = idToHouseMap.get(houseTag.getHouseId());
            house.getTags().add(houseTag.getName());
        });
       /* for(HouseTag ht:houseTags){
        	HouseDTO houseDTO = idToHouseMap.get(ht.getHouseId());
        	 String name = ht.getName();
        	 List<String> tags = houseDTO.getTags();
        	 tags.add(name);
        }*/
    }

	/**   
	 * @Title: wrapperHouseResult   
	 * @Description: TODO(这里用一句话描述这个方法的作用)   
	 * @param: @param result
	 * @param: @return      
	 * @return: Object      
	 * @throws   
	 */
	private  List<HouseDTO> wrapperHouseResult(List<Integer> houseIds) {
		List<HouseDTO> result = new ArrayList<>();

        Map<Integer, HouseDTO> idToHouseMap = new HashMap<>();
        Iterable<House> houses = houseRepository.findAll(houseIds);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            idToHouseMap.put(house.getId(), houseDTO);
        });

        wrapperHouseList(houseIds, idToHouseMap);

        // 矫正顺序
        for (int houseId : houseIds) {
            result.add(idToHouseMap.get(houseId));
        }
        return result;
	}

	@Override
	public ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch) {
		searchService.mapQuery(mapSearch);
		return null;
	}
	  

}
