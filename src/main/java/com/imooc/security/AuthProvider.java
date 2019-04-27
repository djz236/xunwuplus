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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.imooc.entity.User;
import com.imooc.service.IUserService;

/**   
 * @ClassName:  AuthProvider   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年4月26日 下午10:04:08   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public class AuthProvider implements AuthenticationProvider {

	private final Md5PasswordEncoder passwordEncoder=new Md5PasswordEncoder();
	@Autowired
	private IUserService userService;
	/**   
	 * <p>Title: authenticate</p>   
	 * <p>Description: </p>   
	 * @param authentication
	 * @return
	 * @throws AuthenticationException   
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)   
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		String userName = authentication.getName();
		String inputPassword = (String) authentication.getCredentials();
		User user = userService.findUserByName(userName);
		if(user==null){
			throw new AuthenticationCredentialsNotFoundException("authError");
			
		}
		if(passwordEncoder.isPasswordValid(
				user.getPassword(), inputPassword, 
				user.getId())){
			return new UsernamePasswordAuthenticationToken(
					user, null,user.getAuthorities());
		}
		throw new BadCredentialsException("authError");
	}

	/**   
	 * <p>Title: supports</p>   
	 * <p>Description: </p>   
	 * @param authentication
	 * @return   
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)   
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

}
