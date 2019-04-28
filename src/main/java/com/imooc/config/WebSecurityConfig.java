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
package com.imooc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.imooc.security.AuthProvider;
import com.imooc.security.LoginAuthFailHandler;
import com.imooc.security.LoginUrlEntryPoint;

/**
 * @ClassName: WebSecurityConfig
 * @Description:Security权限验证配置
 * @author: 公司名称
 * @date: 2019年4月26日 下午9:22:07
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 资源访问权限
		http.authorizeRequests()
		.antMatchers("/admin/login").permitAll()
				.antMatchers("/static/**").permitAll()
				.antMatchers("/user/login").permitAll()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/user/**")
				.hasAnyRole("ADMIN", "USER").antMatchers("/api/user/**")
				.hasAnyRole("ADMIN", "USER")
				.and().formLogin()
				.loginProcessingUrl("/login") // 配置角色登录处理入口
				.failureHandler(authFailHandler())// 登陆失败的相应
				.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/logout/page")
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true)
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(urlEntryPoint())
				.accessDeniedPage("/403")
				
				;
		 http.csrf().disable();
	        http.headers().frameOptions().sameOrigin();
	}
	 /**
     * @throws Exception    
     * @Title: configGloable   
     * @Description: 自定义认证策略   
     * @param: @param builder      
     * @return: void      
     * @throws   
     */
    @Autowired
    public void configGloable (AuthenticationManagerBuilder auth) throws Exception{
  //  auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN").and();//内存中配置的登陆用户信息
    	auth.authenticationProvider( authProvider()).eraseCredentials(true);
    }
	 
    @Bean
    public AuthProvider authProvider(){
    	return new AuthProvider();
    }
    @Bean
    public LoginUrlEntryPoint urlEntryPoint(){
    	return new LoginUrlEntryPoint("/user/login");
    }
    @Bean
    public LoginAuthFailHandler  authFailHandler(){
    	return new LoginAuthFailHandler(urlEntryPoint());
    } 
}
