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

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.base.ApiDataTableResponse;
import com.imooc.base.ApiResponse;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.house.IHouseService;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.form.DatatableSearch;
import com.imooc.web.form.HouseForm;

/**   
 * @ClassName:  AdminController   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年4月26日 下午9:43:32   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
@Controller
public class AdminController {

	@Autowired
	private IHouseService houseService;
	@GetMapping("/admin/center")
	public String adminCenterPage(){
		return "admin/center";
	}
	
	/**
     * 欢迎页
     * @return
     */
    @GetMapping("/admin/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }
	@GetMapping("/admin/login")
	public String adminLoginPage(){
		return "admin/login";
	}
	/**
     * 房源列表页
     * @return
     */
	@GetMapping("admin/house/list")
	public String houseListPage(){
		return "admin/house-list";
		
	}
	@PostMapping("admin/houses")
    @ResponseBody
	public ApiDataTableResponse house(@ModelAttribute DatatableSearch searchBody){
		ServiceMultiResult<HouseDTO> result = houseService.adminQuery(searchBody);
		ApiDataTableResponse response = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
		response.setData(result.getResult());
		response.setRecordsFiltered(result.getTotal());
		response.setRecordsTotal(result.getTotal());
		response.setDraw(searchBody.getDraw());
		return response;
	}
/*	  if (bindingResult.hasErrors()) {
          return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
      }
      Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
      if (addressMap.keySet().size() != 2) {
          return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
      }
      ServiceResult result = houseService.update(houseForm);
      if (result.isSuccess()) {
          return ApiResponse.ofSuccess(null);
      }
      ApiResponse response = ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
      response.setMessage(result.getMessage());
      return response;*/
		/**   
	 * @Title: saveHouse   
	 * @Description:编辑接口   
	 * @param: @param houseForm
	 * @param: @param bindingResult
	 * @param: @return      
	 * @return: ApiResponse      
	 * @throws   
	 */
	@PostMapping("admin/house/edit")
	@ResponseBody
	public ApiResponse saveHouse (@Valid @ModelAttribute("form-house-edit") HouseForm houseForm, BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			return new ApiResponse(HttpStatus.BAD_REQUEST.value(),bindingResult.getAllErrors().get(0).getDefaultMessage(),null);
		}
		address
		
		return null;
	}
	
}
