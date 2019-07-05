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

import java.util.List;
import java.util.Map;

import com.imooc.entity.SupportAddress;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.search.BaiduMapLocation;
import com.imooc.web.dto.SubwayDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;

/**
 * @ClassName: IAddressService
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月28日 下午5:43:28
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public interface IAddressService {
	/**
	 * 获取所有支持的城市列表
	 * 
	 * @return
	 */
	ServiceMultiResult<SupportAddressDTO> findAllCities();

	/**
	 * @Title: findCityAndRegion @Description:
	 *         根据英文简写获取具体区域的信息 @param: @return @return: Map<> @throws
	 */
	Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(
			String cityEnName, String regionEnName);

	ServiceResult<SubwayDTO> findSubway(Integer subwayId);

	/**
	 * 获取地铁站点信息
	 * 
	 * @param stationId
	 * @return
	 */
	ServiceResult<SubwayStationDTO> findSubwayStation(Integer stationId);

	/**
	 * 根据城市英文简写获取该城市所有支持的区域信息
	 * 
	 * @param cityName
	 * @return
	 */
	ServiceMultiResult findAllRegionsByCityName(String cityName);
	/**
     * 获取该城市所有的地铁线路
     * @param cityEnName
     * @return
     */
	List<SubwayDTO> findAllSubwayByCity(String cityEnName);
	
	 /**
     * 获取地铁线路所有的站点
     * @param subwayId
     * @return
     */
	List<SubwayStationDTO>  findAllStationBySubway(int subwayId);
	
	/**   
	 * @Title: findCity   
	 * @Description: 根据城市英文简写获取城市详细信息   
	 * @param: @param cityEnName
	 * @param: @return      
	 * @return: ServiceResult<SupportAddressDTO>      
	 * @throws   
	 */
	ServiceResult<SupportAddressDTO> findCity(String cityEnName);
	
	ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city,String address); 
	
	/**   
	 * 上传百度LBS数据
	 * @Title: lbsUpload   
	 * @Description: TODO(这里用一句话描述这个方法的作用)   
	 * @param:       
	 * @return: void      
	 * @throws   
	 */
	ServiceResult lbsUpload(BaiduMapLocation location,
			String title,String address,int houseId,int price,int area);
	
	/**   
	 * 移除百度LBS 数据
	 * @Title: remove   
	 * @Description: TODO(这里用一句话描述这个方法的作用)   
	 * @param: @param houseId
	 * @param: @return      
	 * @return: ServiceResult      
	 * @throws   
	 */
	ServiceResult removeLbs (int houseId);
	
	 ServiceResult updateLbs(int houseId);
}
