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

import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.form.DatatableSearch;
import com.imooc.web.form.HouseForm;

/**
 * @ClassName: IHouseService
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月27日 下午4:17:45
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public interface IHouseService {
	ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody);
	ServiceResult update(HouseForm houseForm);
	
	/**   
	 * @Title: findCompleteOne   
	 * @Description: 查询完整房源信息   
	 * @param: @param id
	 * @param: @return      
	 * @return: ServiceResult<HouseDTO>      
	 * @throws   
	 */
	ServiceResult<HouseDTO> findCompleteOne(int id);
	 /**
     * 新增标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult addTag(int houseId, String tag);
	
	/**   
	 * @Title: removeTag   
	 * @Description: 移除标签
	 * @param: @param houseId
	 * @param: @param tag
	 * @param: @return      
	 * @return: ServiceResult      
	 * @throws   
	 */
	ServiceResult removeTag(int houseId,String tag);
	/**   
	 * @Title: removeTag   
	 * @Description: 移除图片
	 * @param: @param houseId
	 * @param: @param tag
	 * @param: @return      
	 * @return: ServiceResult      
	 * @throws   
	 */
	ServiceResult removePhoto(int id);
}
