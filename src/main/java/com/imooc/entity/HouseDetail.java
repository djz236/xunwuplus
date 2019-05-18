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
package com.imooc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName: HouseDetail
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月29日 下午5:20:56
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Entity
@Table(name = "house_detail")
public class HouseDetail {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(name="house_id")
	private int houseId;
	
	private String description;
	@Column(name="layout_desc")
	private String layoutDesc;
	private String traffic;
	@Column(name="round_service")
	private String roundService;
	@Column(name="rent_way")
	private int rentWay;
	@Column(name="address")
	private String detailAddress;
	@Column(name="subway_line_id")
	private int subwayLineId;
	@Column(name="subway_station_id")
	private int subwayStationId;
	@Column(name="subway_line_name")
	private String subwayLineName;
	@Column(name="subway_station_name")
	private String subwayStationName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getHouseId() {
		return houseId;
	}
	public void setHouseId(int houseId) {
		this.houseId = houseId;
	}
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
	public String getDetailAddress() {
		return detailAddress;
	}
	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
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
