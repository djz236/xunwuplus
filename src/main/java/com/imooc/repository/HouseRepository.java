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

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.imooc.entity.House;

/**   
 * @ClassName:  HouseRepository   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年4月27日 下午6:22:47   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public interface HouseRepository extends PagingAndSortingRepository<House, Integer>, JpaSpecificationExecutor<House> {

	@Modifying
	@Query("update House as house set house.cover=:cover where house.id=:id")
	void updateCover(@Param(value="id") int id,@Param(value="cover")String cover);
	
	@Modifying
	@Query("update House as house set house.status=:status where house.id=:id")
	void updateStatus (@Param(value="id") int id,
			@Param(value="status") int status);

	/**   
	 * @Title: findAll   
	 * @Description: TODO(这里用一句话描述这个方法的作用)   
	 * @param: @param houseIds
	 * @param: @return      
	 * @return: Iterable<House>      
	 * @throws   
	 */
	//Iterable<House> findAll(List<Integer> houseIds);
	
}
