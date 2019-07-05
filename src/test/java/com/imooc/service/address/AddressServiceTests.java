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
package com.imooc.service.address;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.XunwuplusApplicationTests;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IAddressService;
import com.imooc.service.search.BaiduMapLocation;

import junit.framework.Assert;

/**   
 * @ClassName:  AddressServiceTests   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年5月13日 下午3:46:02   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public class AddressServiceTests extends XunwuplusApplicationTests {

	@Autowired
	private IAddressService addressService;
	@Test
	public void testGetMapLocation(){
		String city="北京";
		String address ="北京市昌平区巩华家园1号楼2单元";
		ServiceResult<BaiduMapLocation> location = addressService.getBaiduMapLocation(city, address);
		Assert.assertTrue(location.isSuccess());
		Assert.assertTrue(location.getResult().getLongitude()>0);
		Assert.assertTrue(location.getResult().getLatitude()>0);
		System.out.println(location.getResult().getLatitude());
		System.out.println(location.getResult().getLongitude());
	}
	@Test
	public void testUpdateLbs(){
		String city="北京";
		String address ="北京市昌平区巩华家园1号楼2单元";
		ServiceResult<BaiduMapLocation> location = addressService.updateLbs(50);
		/*Assert.assertTrue(location.isSuccess());
		Assert.assertTrue(location.getResult().getLongitude()>0);
		Assert.assertTrue(location.getResult().getLatitude()>0);
		System.out.println(location.getResult().getLatitude());
		System.out.println(location.getResult().getLongitude());*/
	}
}
