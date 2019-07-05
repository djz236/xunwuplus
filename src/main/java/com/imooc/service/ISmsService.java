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
package com.imooc.service;

/**   
 * @ClassName:  ISmsService   
 * @Description:验证码服务  
 * @author: 公司名称 
 * @date:   2019年7月1日 下午8:40:53   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public interface ISmsService {

	
	
	/**   
	 * @Title: sendSms   
	 * @Description: 发送验证码到制定手机 并 缓存验证码 10分钟 及 请求建个时间一分钟   
	 * @param: @param telephone 手机号码
	 * @param: @return      
	 * @return: ServiceResult<String>      
	 * @throws   
	 */
	ServiceResult<String> sendSms(String telephone);
	/**   
	 * @Title: getSmsCode   
	 * @Description: 获取缓存中的验证码 
	 * @param: @param telephone 手机号码
	 * @param: @return      
	 * @return: String      
	 * @throws   
	 */
	String getSmsCode(String telephone);
	
	/**   
	 * @Title: remove   
	 * @Description:  移除制定手机号的验证码缓存  
	 * @param: @param telephone   手机号码
	 * @return: void      
	 * @throws   
	 */
	void remove(String telephone);
}
