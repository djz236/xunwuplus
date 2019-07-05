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
package com.imooc.repository;

import org.springframework.data.repository.CrudRepository;

import com.imooc.entity.User;

/**   
 * @ClassName:  UserRepository   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @param <T>
 * @date:   2019年4月26日 下午10:07:33   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public interface UserRepository  extends CrudRepository<User,Integer> {

	User findByName(String userName);

	/**   
	 * @Title: findUserByPhoneNumber   
	 * @Description: TODO(这里用一句话描述这个方法的作用)   
	 * @param: @param telephone      
	 * @return: void      
	 * @throws   
	 */
	User findUserByPhoneNumber(String telephone);
}
