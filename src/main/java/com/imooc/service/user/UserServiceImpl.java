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
package com.imooc.service.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.imooc.entity.Role;
import com.imooc.entity.User;
import com.imooc.repository.RoleRepository;
import com.imooc.repository.UserRepository;
import com.imooc.service.IUserService;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.UserDTO;

/**   
 * @ClassName:  UserServiceImpl   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: 公司名称 
 * @date:   2019年4月26日 下午10:12:32   
 *     
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved. 
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目 
 */
@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository; 
	  @Autowired
	    private ModelMapper modelMapper;
	/**   
	 * <p>Title: findUserByName</p>   
	 * <p>Description: </p>   
	 * @param userName
	 * @return   
	 * @see com.imooc.service.IUserService#findUserByName(java.lang.String)   
	 */
	@Override
	public User findUserByName(String userName) {
		User user = userRepository.findByName(userName);
		if(user ==null){
			return null;
		}
	 	List<Role> roles = roleRepository.findRolesByUserId(user.getId());
		if(roles==null||roles.isEmpty()){
			throw new DisabledException("权限非法");
		} 
		List<GrantedAuthority> authorities=new ArrayList<>();
	/*	for(Role r:roles){
			authorities.add(new SimpleGrantedAuthority("ROLE_"+r.getName()));
		}*/
		 roles.forEach(role->authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName())));
	 	user.setAuthorityList(authorities);
		return user;
	}
	/**   
	 * <p>Title: findById</p>   
	 * <p>Description: </p>   
	 * @param userId
	 * @return   
	 * @see com.imooc.service.IUserService#findById(java.lang.Long)   
	 */
	@Override
	public ServiceResult<UserDTO> findById(Integer userId) {
		 User user = userRepository.findOne(userId);
	        if (user == null) {
	            return ServiceResult.notFound();
	        }
	        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
	        return ServiceResult.of(userDTO);
	}
	/**   
	 * <p>Title: findUserByTelephone</p>   
	 * <p>Description: </p>   
	 * @param telephone
	 * @return   
	 * @see com.imooc.service.IUserService#findUserByTelephone(java.lang.String)   
	 */
	@Override
	public User findUserByTelephone(String telephone) {
		User user = userRepository.findUserByPhoneNumber(telephone);
		if(user == null){
			return null;
		}
		 
		List<Role> roles = roleRepository.findRolesByUserId(user.getId());
		if(roles==null || roles.isEmpty()){
			throw new DisabledException("权限非法");
		}
		
		List<GrantedAuthority> anthorities=new ArrayList<>();
		roles.forEach(role->anthorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName())));
		user.setAuthorityList(anthorities);
		return user;
		
	}
	/**   
	 * <p>Title: addUserByPhone</p>   
	 * <p>Description: </p>   
	 * @param telephone
	 * @return   
	 * @see com.imooc.service.IUserService#addUserByPhone(java.lang.String)   
	 */
	@Override
	@Transactional
	public User addUserByPhone(String telephone) {
		User user=new User();
		user.setPhoneNumber(telephone);
		user.setName(telephone.substring(0, 3)+"****"+telephone.substring(7, telephone.length()));
		Date now=new Date();
		user.setLastLoginTime(now);
		user.setCreateTime(now);
		user.setLastUpdateTime(now);
		user = userRepository.save(user);
		Role role = new Role();
		role.setName("USER");
		role.setUserId(user.getId());
		roleRepository.save(role);
		user.setAuthorityList(Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER")));
		return user;
	}

}
