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

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

/**
 * @ClassName: WebMvcConfig
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月25日 下午3:43:52
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter
		implements ApplicationContextAware {// 帮助获取spring上下文

	private ApplicationContext applicationContext;
	@Value("${spring.thymeleaf.cache}")
	private boolean cacheable=true;
	/**   
	 * <p>Title: setApplicationContext</p>   
	 * <p>Description: </p>   
	 * @param arg0
	 * @throws BeansException   
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)   
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		 this.applicationContext=applicationContext;
	}
	/**   
	 * <p>Title: addResourceHandlers</p>   
	 * <p>Description: * 静态资源文件映射配置  
	 * @param registry   
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry)   
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		 // 注意 :
        // 1. addResourceHandler 参数可以有多个
        // 2. addResourceLocations 参数可以是多个，可以混合使用 file: 和 classpath : 资源路径
        // 3. addResourceLocations 参数中资源路径必须使用 / 结尾，如果没有此结尾则访问不到

        // 映射到文件系统中的静态文件(应用运行时，这些文件无业务逻辑，但可能被替换或者修改)
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}
	
	/**   
	 * @Title: templateResolver   
	 * @Description: 模板资源解析器
	 * @param: @return      
	 * @return: SpringResourceTemplateResolver      
	 * @throws   
	 */
	@Bean
	@ConfigurationProperties(prefix="spring.thymeleaf")
	public SpringResourceTemplateResolver templateResolver(){
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(this.applicationContext);
		resolver.setCharacterEncoding("UTF-8");
		
		//能保证不用每次重启项目就能在前端看见 更新或者修改的页面  但是在服务器上最好是要求利用缓存 这样的话性能会更好一些
		resolver.setCacheable(cacheable);
		return resolver;
	}
	/**   
	 * @Title: templateEngine   
	 * @Description: thymeleaf 标准方言解释器 
	 * @param: @return      
	 * @return: SpringTemplateEngine      
	 * @throws   
	 */
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		//支持spring El表达式
		templateEngine.setEnableSpringELCompiler(true);
		//支持springSecurity方言
		SpringSecurityDialect securityDialect = new SpringSecurityDialect();
		templateEngine.addDialect(securityDialect);
		return templateEngine;
	}
	
	/**   
	 * @Title: viewResolver   
	 * @Description:ThymeleafViewResolver : 将逻辑视图名称解析为Thymeleaf模板视图
SpringTemplateEngine: 处理模板并渲染结果
TemplateResolver: 加载Thymeleaf模板 
	 * @param: @return      
	 * @return: ThymeleafViewResolver      
	 * @throws   
	 */
	@Bean
	public ThymeleafViewResolver viewResolver(){
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		
		viewResolver.setTemplateEngine(templateEngine());
		return viewResolver;
	}

	/**   
	 * @Title: modelMapper   
	 * @Description: Bean Util 在项目中很多时候需要把Model和DTO两个模型类来回转换,保证Model对外是隐私的,同时类似密码之类的属性也能很好地避免暴露在外了.
那么ModelMapper就是为了方便转换而实现的一个类库,
	 * @param: @return      
	 * @return: ModelMapper      
	 * @throws   
	 */
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
	
}
