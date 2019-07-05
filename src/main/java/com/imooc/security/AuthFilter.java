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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.google.common.base.Objects;
import com.imooc.entity.User;
import com.imooc.service.ISmsService;
import com.imooc.service.IUserService;

import joptsimple.internal.Strings;

/**   
 * @ClassName:  AuthFilter   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年7月1日 下午8:50:26   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public class AuthFilter extends UsernamePasswordAuthenticationFilter{

	@Autowired
	private IUserService userService;
	@Autowired
	private ISmsService smsService;
	/**   
	 * <p>Title: attemptAuthentication</p>   
	 * <p>Description: </p>   
	 * @param request
	 * @param response
	 * @return
	 * @throws AuthenticationException   
	 * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)   
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		String name=obtainUsername(request);
		if(Strings.isNullOrEmpty(name)){
			request.setAttribute("username",name);
			return super.attemptAuthentication(request, response);
		}
		String telephone = request.getParameter("telephone");
		User user = userService.findUserByTelephone(telephone);
		String inputCode=request.getParameter("smsCode");
		String sessionCode=smsService.getSmsCode(telephone);
		if(Objects.equal(inputCode, sessionCode)){
			if(user==null){//如果用户第一次用手机登陆 则自动注册该用户
				user=userService.addUserByPhone(telephone);
			}	
			return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		}else{
			throw new BadCredentialsException("smsCodeError");
		}
	}
}
