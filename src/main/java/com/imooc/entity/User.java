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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**   
 * @ClassName:  User   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年4月26日 下午10:09:10   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
@Entity
@Table(name="user")
public class User implements UserDetails {

	
	/**   
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)   
	 */
	private static final long serialVersionUID = -563540420018485522L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String name;
	private String password;
	private String email;
	@Column(name="phone_number")
	private String phoneNumber;
	private int status;
	@Column(name="create_time")
	private Date createTime;
	@Column(name="last_login_time")
	private Date lastLoginTime;
	@Column(name="last_update_time")
	private Date lastUpdateTime;
	
	/**   
	 * @Fields avatar : 头像   
	 */
	private String avatar;
	
	 @Transient //透明 不被验证
	private List<GrantedAuthority> authorityList;
	
	public List<GrantedAuthority> getAuthorityList() {
		return authorityList;
	}

	public void setAuthorityList(List<GrantedAuthority> authorityList) {
		this.authorityList = authorityList;
	}
	 
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**   
	 * <p>Title: getAuthorities</p>   
	 * <p>Description: </p>   
	 * @return   
	 * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()   
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorityList;
	}

	/**   
	 * <p>Title: getPassword</p>   
	 * <p>Description: </p>   
	 * @return   
	 * @see org.springframework.security.core.userdetails.UserDetails#getPassword()   
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**   
	 * <p>Title: getUsername</p>   
	 * <p>Description: </p>   
	 * @return   
	 * @see org.springframework.security.core.userdetails.UserDetails#getUsername()   
	 */
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return name;
	}

	/**   
	 * <p>Title: isAccountNonExpired</p>   
	 * <p>Description: </p>   
	 * @return   
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()   
	 */
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	/**   
	 * <p>Title: isAccountNonLocked</p>   
	 * <p>Description: </p>   
	 * @return   
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()   
	 */
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	/**   
	 * <p>Title: isCredentialsNonExpired</p>   
	 * <p>Description: </p>   
	 * @return   
	 * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()   
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	/**   
	 * <p>Title: isEnabled</p>   
	 * <p>Description: </p>   
	 * @return   
	 * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()   
	 */
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
