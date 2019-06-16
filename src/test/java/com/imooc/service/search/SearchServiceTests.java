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
package com.imooc.service.search;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.XunwuplusApplicationTests;
import com.imooc.service.ServiceMultiResult;
import com.imooc.web.form.RentSearch;

/**   
 * @ClassName:  SearchServiceTests   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年5月13日 下午9:56:06   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public class SearchServiceTests extends XunwuplusApplicationTests {

	@Autowired
	private ISearchService searchService;
	@Test
	public void testIndex(){
		
	searchService.index(15);
//		Assert.assertTrue(success);
	}
	@Test
	public void testRemove(){
		searchService.remove(15);
		
	}
	@Test
	public void testQuery(){
		RentSearch rentSearch = new RentSearch();
		rentSearch.setCityEnName("bj");
		rentSearch.setKeywords("光");
		rentSearch.setStart(0);
		rentSearch.setSize(10);
		ServiceMultiResult<Integer> query = searchService.query(rentSearch);
		Assert.assertEquals(8, query.getTotal());
	}
	
	
	
	
	
}
