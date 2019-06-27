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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.entity.Subway;
import com.imooc.entity.SubwayStation;
import com.imooc.entity.SupportAddress;
import com.imooc.entity.SupportAddress.Level;
import com.imooc.repository.SubwayRepository;
import com.imooc.repository.SubwayStationRepository;
import com.imooc.repository.SupportAddressRepository;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.search.BaiduMapLocation;
import com.imooc.web.dto.SubwayDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;

/**
 * @ClassName: AddressService
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月28日 下午5:43:52
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Service
public class AddressServiceImpl implements IAddressService {
	private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);
	@Autowired
	private SubwayRepository subwayRepository;
	@Autowired
	private ModelMapper modelMapper;
	private static final String BAIDU_MAP_KEY = "GmXQwiAOqPm5DUj5SGxjmyayfzWc7KyC";
	private static final String BAIDU_MAP_GEOCONV_API = "http://api.map.baidu.com/geocoder/v2/?";
	@Autowired
	private SupportAddressRepository supportAddressRepository;
	 @Autowired
	private SubwayStationRepository subwayStationRepository;
	 @Autowired
	 private ObjectMapper objectMapper;
	/**
	 * <p>
	 * Title: findCityAndRegion
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param cityEnName
	 * @param regionEnName
	 * @return
	 * @see com.imooc.service.house.IAddressService#findCityAndRegion(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Map<Level, SupportAddressDTO> findCityAndRegion(String cityEnName,
			String regionEnName) {
		Map<SupportAddress.Level, SupportAddressDTO> result = new HashMap<>();
		
		
		SupportAddress city = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());

		SupportAddress region = supportAddressRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());

		result.put(SupportAddress.Level.CITY,modelMapper.map(city, SupportAddressDTO.class));
		result.put(SupportAddress.Level.REGION,modelMapper.map(region, SupportAddressDTO.class));

		return result;
	}

	/**   
	 * <p>Title: findSubway</p>   
	 * <p>Description: </p>   
	 * @param subwayId
	 * @return   
	 * @see com.imooc.service.house.IAddressService#findSubway(java.lang.Long)   
	 */
	@Override
	public ServiceResult<SubwayDTO> findSubway(Integer subwayId) {
		if (subwayId == null) {
            return ServiceResult.notFound();
        }
        Subway subway = subwayRepository.findOne(subwayId);
        if (subway == null) {
            return ServiceResult.notFound();
        }
        return ServiceResult.of(modelMapper.map(subway, SubwayDTO.class));
	}

	/**   
	 * <p>Title: findSubwayStation</p>   
	 * <p>Description: </p>   
	 * @param stationId
	 * @return   
	 * @see com.imooc.service.house.IAddressService#findSubwayStation(java.lang.Long)   
	 */
	@Override
	public ServiceResult<SubwayStationDTO> findSubwayStation(Integer stationId) {
		 if (stationId == null) {
	            return ServiceResult.notFound();
	        }
	        SubwayStation station = subwayStationRepository.findOne(stationId);
	        if (station == null) {
	            return ServiceResult.notFound();
	        }
	        return ServiceResult.of(modelMapper.map(station, SubwayStationDTO.class));
	}

	/**   
	 * <p>Title: findAllcities</p>   
	 * <p>Description: </p>   
	 * @return   
	 * @see com.imooc.service.house.IAddressService#findAllcities()   
	 */
	@Override
	public ServiceMultiResult<SupportAddressDTO> findAllCities() {
		List<SupportAddress> addresses = supportAddressRepository.findAllByLevel(SupportAddress.Level.CITY.getValue());
		List<SupportAddressDTO> addressDTOS=new ArrayList<>();
		for(SupportAddress supportAddress: addresses){
			SupportAddressDTO dto = modelMapper.map(supportAddress, SupportAddressDTO.class);
			addressDTOS.add(dto);
		}
		return new ServiceMultiResult<>(addressDTOS.size(), addressDTOS);
	}

	/**   
	 * <p>Title: findAllRegionsByCityName</p>   
	 * <p>Description: </p>   
	 * @param cityName
	 * @return   
	 * @see com.imooc.service.house.IAddressService#findAllRegionsByCityName(java.lang.String)   
	 */
	@Override
	public ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityName) {
		if(cityName==null){
			return new ServiceMultiResult<>(0, null);
		}
		List<SupportAddressDTO> result=new ArrayList<>();
		List<SupportAddress> regions = supportAddressRepository.findAllByLevelAndBelongTo(SupportAddress.Level.REGION.getValue(),
				cityName);
		for(SupportAddress region:regions){
			result.add(modelMapper.map(region,SupportAddressDTO.class));
		}
		return new ServiceMultiResult<>(regions.size(), result);
	}

	/**   
	 * <p>Title: findAllSubwayByCity</p>   
	 * <p>Description: </p>   
	 * @param cityEnName
	 * @return   
	 * @see com.imooc.service.house.IAddressService#findAllSubwayByCity(java.lang.String)   
	 */
	@Override
	public List<SubwayDTO> findAllSubwayByCity(String cityEnName) {
		
		List<SubwayDTO> result=new ArrayList<>();
		List<Subway> subways = subwayRepository.findAllByCityEnName(cityEnName);
		if(subways.isEmpty()){
			return result;
		}
		subways.forEach(subway->result.add(
				modelMapper.map(subway,SubwayDTO.class)));
		return result;
	}

	/**   
	 * <p>Title: findAllStationBySubway</p>   
	 * <p>Description: </p>   
	 * @param subwayId
	 * @return   
	 * @see com.imooc.service.house.IAddressService#findAllStationBySubway(int)   
	 */
	@Override
	public List<SubwayStationDTO> findAllStationBySubway(int subwayId) {
		List<SubwayStationDTO> result=new ArrayList<>();
		List<SubwayStation> stations = subwayStationRepository.findAllBySubwayId(subwayId);
		if(stations.isEmpty()){
			return result;
		}
		stations.forEach(station->result.add(modelMapper.map(station, SubwayStationDTO.class)));
		return result;
	}

	/**   
	 * <p>Title: findCity</p>   
	 * <p>Description: </p>   
	 * @param cityEnName
	 * @return   
	 * @see com.imooc.service.house.IAddressService#findCity(java.lang.String)   
	 */
	@Override
	public ServiceResult<SupportAddressDTO> findCity(String cityEnName) {
		if(cityEnName==null){
			return ServiceResult.notFound();
		}
		
		SupportAddress supportAddress = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
		if(supportAddress==null){
			return ServiceResult.notFound();
		}
		SupportAddressDTO addressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
		
		return ServiceResult.of(addressDTO);
	}

	@Override
	public ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city, String address) {
		String encodeAddress;
		String encodeCity;
		try {
			encodeAddress = URLEncoder.encode(address,"UTF-8");
			encodeCity=URLEncoder.encode(city, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Error to encode house address",e);
			return new ServiceResult<BaiduMapLocation>(
					false,"Error to encode house address");
		}
		HttpClient httpClient=HttpClients.createDefault();
		StringBuilder sb = new StringBuilder(BAIDU_MAP_GEOCONV_API);
		sb.append("address=").append(encodeAddress)
		.append("&").append("city=").append(encodeCity)
		.append("&").append("output=json&").append("ak=")
		.append(BAIDU_MAP_KEY);
		HttpGet get=new HttpGet(sb.toString());
		
		try {
			HttpResponse response = httpClient.execute(get);
			if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
				return new ServiceResult<BaiduMapLocation>(
						false,"Can not get baidu map location");
			}
			String result = EntityUtils.toString(response.getEntity(),"UTF-8");
			JsonNode jsonNode = objectMapper.readTree(result);
			int status =jsonNode.get("status").asInt();
			if(status!=0){
				return new ServiceResult<>(false,
						"Error to get map location for status:"+status);
			}else{
				BaiduMapLocation location = new BaiduMapLocation();
				JsonNode jsonLocation = jsonNode.get("result").get("location");
				
				location.setLongitude(jsonLocation.get("lng").asDouble());
				location.setLatitude(jsonLocation.get("lat").asDouble());
				return ServiceResult.of(location);
			}
			
		} catch (Exception e) {
			logger.error("Error to fetch baidumap api",e);
			return new ServiceResult<BaiduMapLocation>(false, "Error to fetch baidumap api");
		}
	}

}
