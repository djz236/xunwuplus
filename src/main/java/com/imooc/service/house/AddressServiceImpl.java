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

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.entity.Subway;
import com.imooc.entity.SubwayStation;
import com.imooc.entity.SupportAddress;
import com.imooc.entity.SupportAddress.Level;
import com.imooc.repository.SubwayRepository;
import com.imooc.repository.SubwayStationRepository;
import com.imooc.repository.SupportAddressRepository;
import com.imooc.service.ServiceResult;
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
	@Autowired
	private SubwayRepository subwayRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private SupportAddressRepository supportAddressRepository;
	 @Autowired
	private SubwayStationRepository subwayStationRepository;
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

}
