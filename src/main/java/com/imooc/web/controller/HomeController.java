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
package com.imooc.web.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.imooc.base.RentValueBlock;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IAddressService;
import com.imooc.service.house.IHouseService;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.dto.SupportAddressDTO;
import com.imooc.web.form.RentSearch;

/**
 * @ClassName: HomeController
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月25日 下午9:41:28
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Controller
public class HomeController {
	@Autowired
	private IAddressService addressService;
	
	@Autowired
	private IHouseService houseService;
	
	@GetMapping(value = { "/", "/index" })
	public String index() {
		return "index";
	}

	@GetMapping("/404")
	public String notFoundPage() {
		return "/404";
	}

	@GetMapping("/403")
	public String accessError() {
		return "/403";
	}

	@GetMapping("/500")
	public String internalError() {
		return "/500";
	}

	@GetMapping("/logout/page")
	public String logoutPage() {
		return "/logout";
	}
	/**   
	 * @Title: rentHousePage   
	 * @Description: TODO(这里用一句话描述这个方法的作用)   
	 * @param: @param rentSearch
	 * @param: @param model
	 * @param: @param session
	 * @param: @param redirectAttributes
	 * @param: @return      
	 * @return: String      
	 * @throws   
	 */
	@GetMapping("rent/house")
	public String rentHousePage(@ModelAttribute RentSearch rentSearch,
			Model model,HttpSession session,
			RedirectAttributes redirectAttributes){
		if(rentSearch.getCityEnName()==null){
			String  cityEnNameInSession= (String) session.getAttribute("cityEnName");
			if(cityEnNameInSession==null){
				redirectAttributes.addAttribute("msg","must_chose_city");
				return "redirect:/index";
			}else{
				rentSearch.setCityEnName(cityEnNameInSession);
			}
		}else{
			session.setAttribute("cityEnName", rentSearch.getCityEnName());
		}
		ServiceResult<SupportAddressDTO> city = addressService.findCity(rentSearch.getCityEnName());
		if(!city.isSuccess()){
			redirectAttributes.addAttribute("msg","must_chose_city");
			return "redirect:/index";
		}
		model.addAttribute("currentCity", city.getResult());
		ServiceMultiResult<SupportAddressDTO> addressResult = addressService.findAllRegionsByCityName(rentSearch.getCityEnName());
		if(addressResult.getResult()==null||addressResult.getTotal()<1){
			redirectAttributes.addAttribute("msg","must_chose_city");
			return "redirect:/index";
		}
		
		ServiceMultiResult<HouseDTO> serviceMultiResult = houseService.query(rentSearch);
		model.addAttribute("total",serviceMultiResult.getTotal());
		model.addAttribute("houses",serviceMultiResult.getResult());
		
		if(rentSearch.getRegionEnName()==null){
			rentSearch.setRegionEnName("*");
		}
		
		model.addAttribute("searchBody",rentSearch);
		model.addAttribute("regions",addressResult.getResult());
		model.addAttribute("priceBlocks",RentValueBlock.PRICE_BLOCK);
		model.addAttribute("areaBlocks",RentValueBlock.AREA_BLOCK);
		model.addAttribute("currentPriceBlock", RentValueBlock.matchPrice(rentSearch.getPriceBlock()));
		model.addAttribute("currentAreaBlock", RentValueBlock.matchPrice(rentSearch.getAreaBlock()));
		
		return "rent-list";
	}
}
