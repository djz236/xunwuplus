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

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

/**   
 * @ClassName:  WebFileUploadConfig   
 * @Description: 文件上传配置  
 * @author: 公司名称 
 * @date:   2019年5月7日 上午11:51:58   
 *     @ConditionalOnBean（仅仅在当前上下文中存在某个对象时，才会实例化一个Bean）
@ConditionalOnClass（某个class位于类路径上，才会实例化一个Bean）
@ConditionalOnExpression（当表达式为true的时候，才会实例化一个Bean）
@ConditionalOnMissingBean（仅仅在当前上下文中不存在某个对象时，才会实例化一个Bean）
@ConditionalOnMissingClass（某个class类路径上不存在的时候，才会实例化一个Bean）
@ConditionalOnNotWebApplication（不是web应用）
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
@Configuration
@ConditionalOnClass({Servlet.class,StandardServletMultipartResolver.class,MultipartConfigElement.class})
@ConditionalOnProperty(prefix="spring.http.multipart",name="enable",matchIfMissing=true)
@EnableConfigurationProperties(MultipartProperties.class)
public class WebFileUploadConfig {
	private final MultipartProperties multipartProperties;

	public WebFileUploadConfig(MultipartProperties multipartProperties) {
		this.multipartProperties = multipartProperties;
	}
	
	 /**
     * 上传配置
     */
	@Bean
	@ConditionalOnMissingBean
	public MultipartConfigElement multipartConfigElement(){
		return multipartProperties.createMultipartConfig();
	}
	/**
     * 注册解析器
     */
	@Bean(name=DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
	@ConditionalOnMissingBean
	public StandardServletMultipartResolver multipartResolver(){
		StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
		multipartResolver.setResolveLazily(multipartProperties.isResolveLazily());
		return multipartResolver;
	}
	 /**
     * 华东机房
     */
	@Bean
	public com.qiniu.storage.Configuration qiniuConfig(){
		return new com.qiniu.storage.Configuration(Zone.zone0());
	}
	/**
     * 构建一个七牛上传工具实例
     */
	@Bean
	public UploadManager uploadManager(){
		return new UploadManager(qiniuConfig());
	}
	@Value("${qiniu.AccessKey}")
	private String accessKey;
	@Value("${qiniu.SecretKey}")
	private String secretKey;
	/**
     * 认证信息实例
     * @return
     */
	@Bean
	public Auth auth(){
		return Auth.create(accessKey, secretKey);
	}
	 /**
     * 构建七牛空间管理实例
     */
	@Bean
	public BucketManager bucketMananger(){
		return new BucketManager(auth(), qiniuConfig());
	}
	
	@Bean
	public Gson gson(){
		return new Gson();
	}
}
/*
@Configuration
@ConditionalOnProperty(value = "parentName.sonName")
public class parentNameConfig {
}
.yml配置如下：
parentName:
    sonName: true     //正常
parentName:
    sonName:          //正常，空字符时	
parentName:
    sonName: false 	  //失败
parentName:
    sonName: null	  //正常
parentName:
    sonName: 123	  //正常
	
把第二行换成：
@ConditionalOnProperty(value = "parentName.sonName",havingValue = "123")	
.yml配置如下：
parentName:
    sonName: 123      //正常
parentName:
    sonName: 1234	  //失败
parentName:
    sonName: false	  //失败
	
把第二行换成：
@ConditionalOnProperty(value = "parentName.sonName",havingValue = "false")	
.yml配置如下：
parentName:
    sonName: false	  //正常
	
把第二行换成：
@ConditionalOnProperty(prefix = "parentName",name = "sonName",havingValue = "123")
.yml配置如下：
parentName:
    sonName: 123      //正常	
parentName:
    sonName: 1234	  //失败，与havingValue给定的值不一致
	
把第二行换成：
@ConditionalOnProperty(prefix = "parentName",name = "sonName",havingValue = "123",matchIfMissing = true)
.yml配置如下：	
不配置相关参数       //正常，当matchIfMissing = true时，即使没有该parentName.sonName属性也会加载正常
 
 
把第二行换成：
@ConditionalOnProperty(prefix = "parentName",name = "sonName",havingValue = "123",matchIfMissing = false) //这里matchIfMissing默认为false，写不写都行
.yml配置如下：	 
不配置相关参数       //失败，当matchIfMissing = false时，必须要有对应的property
parentName:
    sonName: 123     //正常	
 
把第二行换成：
@ConditionalOnProperty(prefix = "parentName.", name = "sonName")//prefix带了.（点）
.yml配置如下：	 
parentName:
    sonName: true    //正常
parentName:
    sonName: 123	 //正常
	
把第二行换成：	
@ConditionalOnProperty(prefix = "parentName",value = {"sonName2"},name = {"sonName"})
.yml配置如下：	 
parentName:
    sonName: true    //项目Debug启动失败，The name and value attributes of @ConditionalOnProperty are exclusive
	                 //@ConditionalOnProperty的name和value属性是互斥的，不能同时出现
 
把第二行换成：	
@ConditionalOnProperty(prefix = "parentName",name = {"sonName"})	
.yml配置如下：	 
parentName:
    sonName: true    //正常	
 
把第二行换成：
@ConditionalOnProperty(prefix = "parentName",name = {"sonName","flag"})  //name中的属性需要两个都存在且都不为false才会加载正常
.yml配置如下：	 
parentName:
    sonName: true
    flag: true       //正常
parentName:
    sonName: true
    flag: 123        //正常
parentName:
    sonName: true
    flag: false      //失败	
parentName:
    sonName: false 
    flag: false      //失败	
 
把第二行换成：
@ConditionalOnProperty(prefix = "parentName", name = {"sonName", "flag"}, havingValue = "false")
parentName:
    sonName: false 
    flag: false      //正常	
 
把第二行换成：
@ConditionalOnProperty(prefix = "parentName", name = {"sonName", "flag"}, havingValue = "123")//parentName.sonName和parentName.flag的值都要与havingValue的一致才行
parentName:
    sonName: 123
    flag: 1234       //失败	
parentName:
    sonName: 123
    flag: 123	     //正常
parentName:
    sonName: 123	 //失败，缺少parentName.flag
	
把第二行换成：	
@ConditionalOnProperty(prefix = "parentName", name = {"sonName", "flag"}, havingValue = "123",matchIfMissing = true)//matchIfMissing = true允许缺少	
parentName:
    sonName: 123	 //正常	
.yml配置如下：	 
不配置相关参数      //正常	
 
把第二行换成：	
@ConditionalOnProperty(prefix = "parentName", name = {"sonName", "flag"}, havingValue = "123")
.yml配置如下：	 
不配置相关参数      //失败	
--------------------- 
作者：二十六画生的博客 
来源：CSDN 
原文：https://blog.csdn.net/u010002184/article/details/79353696 
版权声明：本文为博主原创文章，转载请附上博文链接！*/
