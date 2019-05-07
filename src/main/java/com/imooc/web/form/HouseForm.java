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
package com.imooc.web.form;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.alibaba.fastjson.JSON;

/**
 * @ClassName: HouseForm
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月28日 下午5:11:48
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class HouseForm {

	private int id;

	@NotNull(message = "大标题不允许为空")
	@Size(min = 1, max = 30, message = "标题长度必须在1-30之间")
	private String title;

	@NotNull(message = "必须选中一个城市")
	@Size(min = 1, message = "非法的城市")
	private String cityEnName;

	@NotNull(message = "必须选中一个地区")
	@Size(min = 1, message = "非法的地区")
	private String regionEnName;

	@NotNull(message = "必须填写街道")
	@Size(min = 1, message = "非法的街道")
	private String street;

	@NotNull(message = "必须填写小区")
	private String distinct;

	@NotNull(message = "详细地址不允许为空！")
	@Size(min = 1, max = 30, message = "详细地址长度必须在1-30之间")
	private String detailAddress;
	
	@NotNull(message="必须填写卧室数量")
	@Min(value=1,message="非法的卧室数量")
	private String room;
	
	
	private String parlour;
	
	@NotNull(message="必须填写所属楼层")
	private Integer floor;
	
	@NotNull(message="必须填写总楼层")
	private Integer totalFloor;
	
	@NotNull(message="必须填写房屋朝向")
	private Integer direction;
	
	@NotNull(message="必须填写建筑起始时间")
	@Min(value=1900,message="非法的建筑起始时间")
	private Integer buildYear;
	
	@NotNull(message="必须填写面积")
	@Min(value=1)
	private Integer area;
	
	@NotNull(message="必须填写租赁价格")
	@Min(value=1)
	private Integer price;
	
	@NotNull(message="必须选中一个租赁方式")
	@Min(value=0)
	@Max(value=1)
	private Integer rentWay;
	
	private int subwayLineId;
	private int subwayStationId;
	private int distanceToSubway = -1;
	private String layoutDesc;
	private String roundService;
	private String traffic;
	
	@Size(max=255)
	private String description;
	private String cover;
	private List<String> tags;
	private List<PhotoForm> photos;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCityEnName() {
		return cityEnName;
	}

	public void setCityEnName(String cityEnName) {
		this.cityEnName = cityEnName;
	}

	public String getRegionEnName() {
		return regionEnName;
	}

	public void setRegionEnName(String regionEnName) {
		this.regionEnName = regionEnName;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getDistinct() {
		return distinct;
	}

	public void setDistinct(String distinct) {
		this.distinct = distinct;
	}

	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getParlour() {
		return parlour;
	}

	public void setParlour(String parlour) {
		this.parlour = parlour;
	}

	public Integer getFloor() {
		return floor;
	}

	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	public Integer getTotalFloor() {
		return totalFloor;
	}

	public void setTotalFloor(Integer totalFloor) {
		this.totalFloor = totalFloor;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public Integer getBuildYear() {
		return buildYear;
	}

	public void setBuildYear(Integer buildYear) {
		this.buildYear = buildYear;
	}

	public Integer getArea() {
		return area;
	}

	public void setArea(Integer area) {
		this.area = area;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getRentWay() {
		return rentWay;
	}

	public void setRentWay(Integer rentWay) {
		this.rentWay = rentWay;
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

	public int getDistanceToSubway() {
		return distanceToSubway;
	}

	public void setDistanceToSubway(int distanceToSubway) {
		this.distanceToSubway = distanceToSubway;
	}

	public String getLayoutDesc() {
		return layoutDesc;
	}

	public void setLayoutDesc(String layoutDesc) {
		this.layoutDesc = layoutDesc;
	}

	public String getRoundService() {
		return roundService;
	}

	public void setRoundService(String roundService) {
		this.roundService = roundService;
	}

	public String getTraffic() {
		return traffic;
	}

	public void setTraffic(String traffic) {
		this.traffic = traffic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<PhotoForm> getPhotos() {
		return photos;
	}

	public void setPhotos(List<PhotoForm> photos) {
		this.photos = photos;
	}

	/**
	 * <p>
	 * Title: toString
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
