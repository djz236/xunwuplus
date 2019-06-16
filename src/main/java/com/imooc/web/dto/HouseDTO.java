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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * @ClassName: HouseDTO
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年4月27日 下午4:42:15
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class HouseDTO implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = -1467059365613367737L;

	private Long id;
	private String title;
	private int price;
	private int area;
	private int direction;
	private int room;
	private int parlour;
	private int bathroom;
	private int floor;
	private Integer adminId;

	private String district;

	private int totalFloor;

	private int watchTimes;

	private int buildYear;

	private int status;

	private Date createTime;

	private Date lastUpdateTime;

	private String cityEnName;

	private String regionEnName;

	private String street;

	private String cover;

	private int distanceToSubway;

	private HouseDetailDTO houseDetail;

	private List<String> tags;

	private List<HousePictureDTO> pictures;

	private int subscribeStatus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getRoom() {
		return room;
	}

	public void setRoom(int room) {
		this.room = room;
	}

	public int getParlour() {
		return parlour;
	}

	public void setParlour(int parlour) {
		this.parlour = parlour;
	}

	public int getBathroom() {
		return bathroom;
	}

	public void setBathroom(int bathroom) {
		this.bathroom = bathroom;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public int getTotalFloor() {
		return totalFloor;
	}

	public void setTotalFloor(int totalFloor) {
		this.totalFloor = totalFloor;
	}

	public int getWatchTimes() {
		return watchTimes;
	}

	public void setWatchTimes(int watchTimes) {
		this.watchTimes = watchTimes;
	}

	public int getBuildYear() {
		return buildYear;
	}

	public void setBuildYear(int buildYear) {
		this.buildYear = buildYear;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
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

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public int getDistanceToSubway() {
		return distanceToSubway;
	}

	public void setDistanceToSubway(int distanceToSubway) {
		this.distanceToSubway = distanceToSubway;
	}

	public HouseDetailDTO getHouseDetail() {
		return houseDetail;
	}

	public void setHouseDetail(HouseDetailDTO houseDetail) {
		this.houseDetail = houseDetail;
	}

	public List<String> getTags() {
		if (this.tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<HousePictureDTO> getPictures() {
		return pictures;
	}

	public void setPictures(List<HousePictureDTO> pictures) {
		this.pictures = pictures;
	}

	public int getSubscribeStatus() {
		return subscribeStatus;
	}

	public void setSubscribeStatus(int subscribeStatus) {
		this.subscribeStatus = subscribeStatus;
	}

/*	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}*/

	 @Override
	    public String toString() {
	        return "HouseDTO{" +
	                "id=" + id +
	                ", title='" + title + '\'' +
	                ", price=" + price +
	                ", area=" + area +
	                ", floor=" + floor +
	                ", totalFloor=" + totalFloor +
	                ", watchTimes=" + watchTimes +
	                ", buildYear=" + buildYear +
	                ", status=" + status +
	                ", createTime=" + createTime +
	                ", lastUpdateTime=" + lastUpdateTime +
	                ", cityEnName='" + cityEnName + '\'' +
	                ", cover='" + cover + '\'' +
	                ", houseDetail=" + houseDetail +
	                ", pictures=" + pictures +
	                '}';
	    }
	
	
	
}
