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
package com.imooc.web.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.validation.Valid;

import org.apache.kafka.common.protocol.ApiKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.imooc.base.ApiDataTableResponse;
import com.imooc.base.ApiResponse;
import com.imooc.entity.SupportAddress;
import com.imooc.entity.SupportAddress.Level;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IAddressService;
import com.imooc.service.house.IHouseService;
import com.imooc.service.house.IQiNiuService;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.dto.HouseDetailDTO;
import com.imooc.web.dto.QiNiuPutRet;
import com.imooc.web.dto.SubwayDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;
import com.imooc.web.form.DatatableSearch;
import com.imooc.web.form.HouseForm;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

/**
 * @ClassName: AdminController
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月26日 下午9:43:32
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Controller
public class AdminController {

	@Autowired
	private IAddressService addressService;

	@Autowired
	private IHouseService houseService;
	@Autowired
	private IQiNiuService qiNiuService;

	@Autowired
	private Gson gson;
	@GetMapping("/admin/center")
	public String adminCenterPage() {
		return "admin/center";
	}

	/**
	 * 欢迎页
	 * 
	 * @return
	 */
	@GetMapping("/admin/welcome")
	public String welcomePage() {
		return "admin/welcome";
	}

	@GetMapping("/admin/login")
	public String adminLoginPage() {
		return "admin/login";
	}

	/**
	 * 房源列表页
	 * 
	 * @return
	 */
	@GetMapping("admin/house/list")
	public String houseListPage() {
		return "admin/house-list";

	}

	@PostMapping("admin/houses")
	@ResponseBody
	public ApiDataTableResponse house(
			@ModelAttribute DatatableSearch searchBody) {
		ServiceMultiResult<HouseDTO> result = houseService
				.adminQuery(searchBody);
		ApiDataTableResponse response = new ApiDataTableResponse(
				ApiResponse.Status.SUCCESS);
		response.setData(result.getResult());
		response.setRecordsFiltered(result.getTotal());
		response.setRecordsTotal(result.getTotal());
		response.setDraw(searchBody.getDraw());
		return response;
	}

	/**
	 * @Title: houseEditPage @Description: 房源信息编辑页 @param: @param
	 * id @param: @param model @param: @return @return: String @throws
	 */
	@GetMapping("admin/house/edit")
	public String houseEditPage(@RequestParam(value = "id") Integer id,
			Model model) {
		if (id == null || id < 1) {
			return "404";
		}

		ServiceResult<HouseDTO> serviceResult = houseService.findCompleteOne(id);
		if(!serviceResult.isSuccess()){
			return "404";
		}
		HouseDTO result = serviceResult.getResult();
		model.addAttribute("house",result);
		
		Map<Level, SupportAddressDTO> addressMap = addressService.findCityAndRegion(result.getCityEnName(),result.getRegionEnName());
		model.addAttribute("city",addressMap.get(SupportAddress.Level.CITY));
		model.addAttribute("region",addressMap.get(SupportAddress.Level.REGION));
		HouseDetailDTO detailDTO = result.getHouseDetail();
		ServiceResult<SubwayDTO> subwayServiceResult = addressService.findSubway(detailDTO.getSubwayLineId());
		if(subwayServiceResult.isSuccess()){
			model.addAttribute("subway", subwayServiceResult.getResult());
		}
		
		ServiceResult<SubwayStationDTO> subwayStationServiceResult = addressService.findSubwayStation(detailDTO.getSubwayStationId());
		if(subwayServiceResult.isSuccess()){
			model.addAttribute("station", subwayStationServiceResult.getResult());
		}
		
		return "admin/house-edit";
	}

	/**
	 * @Title: saveHouse @Description:编辑接口 @param: @param
	 *         houseForm @param: @param bindingResult @param: @return @return:
	 *         ApiResponse @throws
	 */
	@PostMapping("admin/house/edit")
	@ResponseBody
	public ApiResponse saveHouse(
			@Valid @ModelAttribute("form-house-edit") HouseForm houseForm,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ApiResponse(HttpStatus.BAD_REQUEST.value(),
					bindingResult.getAllErrors().get(0).getDefaultMessage(),
					null);
		}
		Map<Level, SupportAddressDTO> addressMap = addressService
				.findCityAndRegion(houseForm.getCityEnName(),
						houseForm.getRegionEnName());
		if (addressMap.keySet().size() != 2) {
			return ApiResponse.ofSucess(ApiResponse.Status.NOT_VALID_PARAM);
		}
		ServiceResult<?> result = houseService.update(houseForm);
		if (result.isSuccess()) {
			return ApiResponse.ofSucess(null);
		}
		ApiResponse response = ApiResponse
				.ofStatus(ApiResponse.Status.BAD_REQUEST);
		response.setMessage(result.getMessage());

		return response;
	}

	/**   
	 * @Title: addHouseTag   
	 * @Description: 增加标签接口   
	 * @param: @return      
	 * @return: ApiResponse      
	 * @throws   
	 */
	@PostMapping("admin/house/tag")
	@ResponseBody
	public ApiResponse addHouseTag(
			@RequestParam(value="house_id")int houseId,
			@RequestParam(value="tag")String tag){
		if(houseId<1||Strings.isNullOrEmpty(tag)){
			return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
		}
		ServiceResult serviceResult = houseService.addTag(houseId, tag);
		if(serviceResult.isSuccess()){
			return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
		}else{
			return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),
					serviceResult.getMessage());
		}
	}
	
	/**   
	 * @Title: removeHouseTag   
	 * @Description: 移除标签接口   
	 * @param: @return      
	 * @return: ApiResponse      
	 * @throws   
	 */
	@DeleteMapping("admin/house/tag")
	@ResponseBody
	public ApiResponse removeHouseTag(
			@RequestParam(value="house_id") int houseId,
			@RequestParam(value="tag") String tag){
		if(houseId<1||Strings.isNullOrEmpty(tag)){
			return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
		}
		ServiceResult serviceResult = houseService.removeTag(houseId, tag);
		if(serviceResult.isSuccess()){
			return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
		}else{
			return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), serviceResult.getMessage());
		}
	}
	/**   
	 * @Title: removeHousePhoto   
	 * @Description: 移除图片接口  
	 * @param: @return      
	 * @return: ApiResponse      
	 * @throws   
	 */
	@DeleteMapping("/admin/house/photo")
	@ResponseBody
	public ApiResponse removeHousePhoto(
			@RequestParam(value="id") int houseId){
		 
//		houseService.remove
		return null;
	}
	
	/**   
	 * @Title: uploadPhoto   
	 * @Description: 上传图片接口   
	 * @param: @param file
	 * @param: @return      
	 * @return: ApiResponse      
	 * @throws   
	 */
	@PostMapping(value="/admin/upload/photo",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file){
		if(file.isEmpty()){
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
		}
		
		String filename = file.getOriginalFilename();
		
		try {
			InputStream inputStream = file.getInputStream();
			Response response = qiNiuService.uploadFile(inputStream);
			if(response.isOK()){
				QiNiuPutRet ret = gson.fromJson(response.bodyString(),QiNiuPutRet.class);
				return ApiResponse.ofSucess(ret);
			}else{
				return ApiResponse.ofMessage(response.statusCode,
						response.getInfo());
			}
		} catch (QiniuException e) {
			Response response = e.response;
			try {
				return ApiResponse.ofMessage(response.statusCode,
						response.bodyString());
			} catch (QiniuException e1) {
				e1.printStackTrace();
				return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
			}
		}catch (IOException e) {
			return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
}
