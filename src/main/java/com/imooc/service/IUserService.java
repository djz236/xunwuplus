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

import com.imooc.entity.User;
import com.imooc.web.dto.UserDTO;

/**
 * @ClassName: IUserService
 * @Description:用户服务
 * @author: 公司名称
 * @date: 2019年4月26日 下午10:10:11
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public interface IUserService {

	User findUserByName(String userName);

	ServiceResult<UserDTO> findById(Integer userId);

	/**   
	 * @Title: findUserByTelephone   
	 * @Description: 根据电话号码寻找用户   
	 * @param: @param telephone
	 * @param: @return      
	 * @return: User      
	 * @throws   
	 */
	User findUserByTelephone(String telephone);
	
	/**   
	 * @Title: addUserByPhone   
	 * @Description: 通过手机号注册用户   
	 * @param: @param telephone
	 * @param: @return      
	 * @return: User      
	 * @throws   
	 */
	User addUserByPhone(String telephone);
}
