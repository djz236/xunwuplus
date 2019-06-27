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
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.imooc.base.ApiResponse;
import com.imooc.entity.SupportAddress;
import com.imooc.service.IUserService;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IAddressService;
import com.imooc.service.house.IHouseService;
import com.imooc.service.search.ISearchService;
import com.imooc.web.dto.HouseBucketDTO;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.dto.SubwayDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;
import com.imooc.web.dto.UserDTO;
import com.imooc.web.form.MapSearch;

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
	@Autowired
	private ISearchService searchService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IHouseService houseService;

	@GetMapping("rent/house/autocomplete")
	@ResponseBody
	public ApiResponse autoComplete(@RequestParam(value = "prefix") String prefix) {
		if (prefix.isEmpty()) {
			return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
		}

		ServiceResult<List<String>> result = searchService.suggest(prefix);

		return ApiResponse.ofSucess(result.getResult());
	}

	/**
	 * 获取对应地铁线路所支持的地铁站点
	 * 
	 * @param subwayId
	 * @return
	 */
	// address/support/subway/station
	@GetMapping("/address/support/subway/station")
	@ResponseBody
	public ApiResponse getSupportSubwayStation(@RequestParam(name = "subway_id") int subwayId) {
		List<SubwayStationDTO> stationDTOS = addressService.findAllStationBySubway(subwayId);
		if (stationDTOS.isEmpty()) {
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
		}
		return ApiResponse.ofSucess(stationDTOS);
	}

	// /address/support/subway/line
	/**
	 * 获取具体城市所支持的地铁线路
	 * 
	 * @param cityEnName
	 * @return
	 */
	@GetMapping("address/support/subway/line")
	@ResponseBody
	public ApiResponse getSupportSubwayLine(@RequestParam(name = "city_name") String cityEnName) {
		List<SubwayDTO> subways = addressService.findAllSubwayByCity(cityEnName);
		if (subways.isEmpty()) {
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
		}
		return ApiResponse.ofSucess(subways);
	}

	@GetMapping("address/support/cities")
	@ResponseBody
	public ApiResponse getSupportCities() {
		ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
		if (result.getResultSize() == 0) {
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
		}
		return ApiResponse.ofSucess(result.getResult());
	}

	@GetMapping("rent/house/show/{id}")
	public String show(@PathVariable(value = "id") Integer houseId, Model model) {
		if (houseId <= 0) {
			return "404";
		}

		ServiceResult<HouseDTO> serviceResult = houseService.findCompleteOne(houseId);
		if (!serviceResult.isSuccess()) {
			return "404";
		}

		HouseDTO houseDTO = serviceResult.getResult();
		Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService
				.findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());

		SupportAddressDTO city = addressMap.get(SupportAddress.Level.CITY);
		SupportAddressDTO region = addressMap.get(SupportAddress.Level.REGION);

		model.addAttribute("city", city);
		model.addAttribute("region", region);

		ServiceResult<UserDTO> userDTOServiceResult = userService.findById(houseDTO.getAdminId());
		model.addAttribute("agent", userDTOServiceResult.getResult());
		model.addAttribute("house", houseDTO);

		ServiceResult<Integer> aggResult = searchService.aggregateDistrictHouse(city.getEnName(), region.getEnName(),
				houseDTO.getDistrict());
		model.addAttribute("houseCountInDistrict", aggResult.getResult());

		return "house-detail";
	}

	/**
	 * @Title: getSupportRegion @Description: 获取对应城市支持区域列表 @param: @param
	 * cityEnName @param: @return @return: ApiResponse @throws
	 */
	@GetMapping("address/support/regions")
	@ResponseBody
	public ApiResponse getSupportRegion(@RequestParam(name = "city_name") String cityEnName) {
		ServiceMultiResult result = addressService.findAllRegionsByCityName(cityEnName);
		if (result.getResult() == null || result.getTotal() < 1) {
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
		}
		return ApiResponse.ofSucess(result.getResult());
	}

	@GetMapping("rent/house/map")
	public String rentMapPage(@RequestParam(value = "cityEnName") String cityEnName, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {

		ServiceResult<SupportAddressDTO> city = addressService.findCity(cityEnName);

		if (!city.isSuccess()) {
			redirectAttributes.addAttribute("msg", "must_chose_city");
			return "redirect:/index";
		} else {
			session.setAttribute("cityName", cityEnName);
			model.addAttribute("city", city.getResult());
		}

		ServiceMultiResult<SupportAddressDTO> regions = addressService.findAllRegionsByCityName(cityEnName);

		ServiceMultiResult<HouseBucketDTO> serviceResult = searchService.mapAggregate(cityEnName);

		model.addAttribute("total", serviceResult.getTotal());
		model.addAttribute("regions", regions.getResult());
		model.addAttribute("aggData", serviceResult.getResult());

		return "rent-map";
	}

	@GetMapping("rent/house/map/houses")
	@ResponseBody
	public ApiResponse rentMapHouse(@ModelAttribute MapSearch mapSearch) {

		if (mapSearch.getCityEnName() == null) {
			return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须选择城市");
		}
		ServiceMultiResult<HouseDTO> serviceMutiResult = houseService.wholeMapQuery(mapSearch);
		ApiResponse response=ApiResponse.ofSucess(
				serviceMutiResult.getResult());

		response.setMore(serviceMutiResult.getTotal()>(mapSearch.getStart()+mapSearch.getSize()));
		return response;
	}

}
