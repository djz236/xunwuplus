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
package com.imooc.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * @ClassName: LoginAuthFailHandler
 * @Description:AuthenticationFailureHandler接口定义了Spring Security
 *                                                      Web在遇到认证错误时所使用的处理策略。
 *                                                      典型做法一般是将用户重定向到认证页面(
 *                                                      比如认证机制是用户名表单认证的情况)
 *                                                      让用户再次认证。
 *                                                      当然具体实现类可以根据需求实现更复杂的逻辑，
 *                                                      比如根据异常做不同的处理等等。举个例子，
 *                                                      如果遇到CredentialsExpiredException异常
 *                                                      (
 *                                                      AuthenticationException异常的一种
 *                                                      ，表示密码过期失效)，
 *                                                      可以将用户重定向到修改密码页面而不是登录认证页面。
 * @author: 公司名称
 * @date: 2019年4月27日 下午3:44:13
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class LoginAuthFailHandler
		extends SimpleUrlAuthenticationFailureHandler {
	private final LoginUrlEntryPoint urlEntryPoint;

	/**
	 * @Title: LoginAuthFailHandler @Description:
	 * TODO(这里用一句话描述这个方法的作用) @param: @throws
	 */
	public LoginAuthFailHandler(LoginUrlEntryPoint urlEntryPoint) {
		this.urlEntryPoint = urlEntryPoint;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		String targetUrl = urlEntryPoint
				.determineUrlToUseForThisRequest(request, response, exception);
		targetUrl+="?"+exception.getMessage();
		super.setDefaultFailureUrl(targetUrl);
		super.onAuthenticationFailure(request, response, exception);
	}

}
