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
package com.imooc.web.controller.house;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.base.ApiResponse;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.house.IAddressService;
import com.imooc.web.dto.SubwayDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;

/**
 * @ClassName: HouseController
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月27日 下午4:10:04
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Controller
public class HouseController {

	@Autowired
	private IAddressService addressService;
	
	/**
     * 获取对应地铁线路所支持的地铁站点
     * @param subwayId
     * @return
     */
	//address/support/subway/station
	@GetMapping("/address/support/subway/station")
	@ResponseBody
	public ApiResponse getSupportSubwayStation(@RequestParam(name="subway_id")int subwayId){
		List<SubwayStationDTO> stationDTOS = addressService.findAllStationBySubway(subwayId);
		if(stationDTOS.isEmpty()){
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
		}
		return ApiResponse.ofSucess(stationDTOS);
	}
//	/address/support/subway/line
	/**
     * 获取具体城市所支持的地铁线路
     * @param cityEnName
     * @return
     */
	@GetMapping("address/support/subway/line")
	@ResponseBody
	public ApiResponse getSupportSubwayLine(
			@RequestParam(name="city_name")String cityEnName){
		List<SubwayDTO> subways = addressService.findAllSubwayByCity(cityEnName);
		if(subways.isEmpty()){
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
		}
		return ApiResponse.ofSucess(subways);
	}
	
	@GetMapping("address/support/cities")
	@ResponseBody
	public ApiResponse getSupportCities(){
		ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
		if(result.getResultSize()==0){
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
		}
		return ApiResponse.ofSucess(result.getResult());
	}
 
	
	/**   
	 * @Title: getSupportRegion   
	 * @Description: 获取对应城市支持区域列表   
	 * @param: @param cityEnName
	 * @param: @return      
	 * @return: ApiResponse      
	 * @throws   
	 */
	@GetMapping("address/support/regions")
	@ResponseBody
	public ApiResponse getSupportRegion(
			@RequestParam(name = "city_name") String cityEnName) {
		ServiceMultiResult result = addressService.findAllRegionsByCityName(cityEnName);
		if(result.getResult()==null||result.getTotal()<1){
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
		}
		return ApiResponse.ofSucess(result.getResult());
	}
}
