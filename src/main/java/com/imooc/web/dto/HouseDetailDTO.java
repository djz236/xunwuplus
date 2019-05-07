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
package com.imooc.web.dto;

/**   
 * @ClassName:  HouseDetailDTO   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年4月27日 下午4:51:40   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public class HouseDetailDTO {
	 private String description;

	    private String layoutDesc;

	    private String traffic;

	    private String roundService;

	    private int rentWay;

	    private Long adminId;

	    private String address;

	    private int subwayLineId;

	    private int subwayStationId;

	    private String subwayLineName;

	    private String subwayStationName;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getLayoutDesc() {
			return layoutDesc;
		}

		public void setLayoutDesc(String layoutDesc) {
			this.layoutDesc = layoutDesc;
		}

		public String getTraffic() {
			return traffic;
		}

		public void setTraffic(String traffic) {
			this.traffic = traffic;
		}

		public String getRoundService() {
			return roundService;
		}

		public void setRoundService(String roundService) {
			this.roundService = roundService;
		}

		public int getRentWay() {
			return rentWay;
		}

		public void setRentWay(int rentWay) {
			this.rentWay = rentWay;
		}

		public Long getAdminId() {
			return adminId;
		}

		public void setAdminId(Long adminId) {
			this.adminId = adminId;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public int getSubwayLineId() {
			return subwayLineId;
		}

		public void setSubwayLineId(int subwayLineId) {
			this.subwayLineId = subwayLineId;
		}

		public int getSubwayStationId() {
			return subwayStationId;
		}

		public void setSubwayStationId(int subwayStationId) {
			this.subwayStationId = subwayStationId;
		}

		public String getSubwayLineName() {
			return subwayLineName;
		}

		public void setSubwayLineName(String subwayLineName) {
			this.subwayLineName = subwayLineName;
		}

		public String getSubwayStationName() {
			return subwayStationName;
		}

		public void setSubwayStationName(String subwayStationName) {
			this.subwayStationName = subwayStationName;
		}
	    
	    
}
