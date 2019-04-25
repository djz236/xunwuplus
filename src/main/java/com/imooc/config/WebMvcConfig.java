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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName: WebMvcConfig
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月25日 下午3:43:52
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class WebMvcConfig extends WebMvcConfigurerAdapter
		implements ApplicationContextAware {

	/**   
	 * <p>Title: setApplicationContext</p>   
	 * <p>Description: </p>   
	 * @param arg0
	 * @throws BeansException   
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)   
	 */
	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		// TODO Auto-generated method stub
		
	}// 帮助获取spring上下文

}
