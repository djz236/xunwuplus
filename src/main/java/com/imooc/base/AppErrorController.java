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
package com.imooc.base;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.aliyuncs.http.HttpResponse;

/**
 * @ClassName: AppErrorController
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月25日 下午5:52:05
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Controller
public class AppErrorController implements ErrorController {
	private static final String ERROR_PATH = "/error";
	private ErrorAttributes errorAttaributes;

	/**
	 * <p>
	 * Title: getErrorPath
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return
	 * @see org.springframework.boot.autoconfigure.web.ErrorController#getErrorPath()
	 */
	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	public AppErrorController(ErrorAttributes errorAttaributes) {
		this.errorAttaributes = errorAttaributes;
	}

	
	/**   
	 * @Title: errorPageHandler   
	 * @Description: Web页面错误处理   
	 * @param: @param request
	 * @param: @param response
	 * @param: @return      
	 * @return: String      
	 * @throws   
	 */
	@RequestMapping(value=ERROR_PATH,produces="text/html")
	public String errorPageHandler(HttpServletRequest request,
			HttpServletResponse response){
		int status=response.getStatus();
		switch (status) {
		case 403:
			return "403";
		case 404:
			return "404";
		case 500:
			return "500";
		}
		return "index";
	}
	
	
	@RequestMapping(value=ERROR_PATH)
	@ResponseBody
	public ApiResponse errorApiHandler(HttpServletRequest request){
		RequestAttributes requestAttributes=
				new ServletRequestAttributes(request);
		Map<String, Object> attributes = this.errorAttaributes.getErrorAttributes(requestAttributes, false);
		int status=getStatus(request);
		
		return ApiResponse.ofMessage(status, String.valueOf(attributes.getOrDefault("message", "error")));
	}

	/**   
	 * @Title: getStatus   
	 * @Description: TODO(这里用一句话描述这个方法的作用)   
	 * @param: @param request
	 * @param: @return      
	 * @return: int      
	 * @throws   
	 */
	private int getStatus(HttpServletRequest request) {
		  Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
	        if (status != null) {
	            return status;
	        }

	        return 500;
	}
	
}
