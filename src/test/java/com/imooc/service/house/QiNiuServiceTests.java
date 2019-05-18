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
package com.imooc.service.house;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.XunwuplusApplicationTests;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

/**   
 * @ClassName:  QiNiuServiceTests   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年5月13日 下午3:48:34   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public class QiNiuServiceTests extends XunwuplusApplicationTests {

	@Autowired
	private IQiNiuService qiNiuService;
	@Test
	public void testUploadFile(){
		String fileName="E:/1.png";
		
		File file = new File(fileName);
		Assert.assertTrue(file.exists());
		try {
			Response response = qiNiuService.uploadFile(file);
			Assert.assertTrue(response.isOK());
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testDelete(){
		String key="Fvq8iSxI-Oi_aXpoq9PS76H9RhtV";
		Response response;
		try {
			response = qiNiuService.delete(key);
			Assert.assertTrue(response.isOK());
		} catch (QiniuException e) {
			e.printStackTrace();
		}
		
	}
}
