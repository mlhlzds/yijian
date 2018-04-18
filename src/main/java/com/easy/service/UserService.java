package com.easy.service;

import java.util.List;
import java.util.Map;

import com.easy.common.ReturnMsg;
import com.easy.entity.User;
import org.springframework.ui.ModelMap;



public interface UserService {
	
	/**
	 * 用户登陆
	 * @param param
	 * @return
	 */
	public User login(Map<String, Object> param);

	/**
	 * 添加用户
	 * @param param
	 * @return
	 */
	public int addUser(Map<String, Object> param);
	
	public List getMenuByUid(String id);

	/**
	 * 修改密码
	 * @param param
	 * @return
	 */
	public ReturnMsg updatePassword(Map<String, Object> param);
	
	/**
	 * 查询所有角色
	 * @return
	 */
	public ReturnMsg queryRoles(Map<String, Object> param);

	/**
	 * 重置密码
	 * @param param
	 * @return
	 */
	public ReturnMsg resetPassword(Map<String, Object> param);
	/**
	 * 查询用户列表
	 * @param param
	 * @return
	 */
	public ReturnMsg queryUser(Map<String, Object> param);
	
	/**
	 * 删除用户
	 * @param param
	 */
	public void deleteUser(Map<String, Object> param);

	/**
	 * 
	 * @param map
	 * @return
	 */
	public ReturnMsg deleteRole(Map<String, Object> map);

	/**
	 * 
	 * @param map
	 * @return
	 */
	public ReturnMsg updateRole(Map<String, Object> map);

	/**
	 *
	 * @param map
	 * @return
	 */
	public ReturnMsg updateState(Map<String, Object> map);

	/**
	 * 
	 * @param map
	 * @return
	 */
	public ReturnMsg addRole(Map<String, Object> map);

	/**
	 * 
	 * @param map
	 * @return
	 */
	public ReturnMsg queryRoleAndMenu(Map<String, Object> map);

	/**
	 * 分页查询角色
	 * @param param
	 * @return
	 */
	ReturnMsg queryPagesRoles(Map<String, Object> param);

	/**
	 * 分页查询菜单
	 * @param map
	 * @return
	 */
	public ReturnMsg queryPagesMenus(Map<String, Object> map);

	/**
	 * 分页查询权限
	 * @param map
	 * @return
	 */
	public ReturnMsg queryPagesPrms(Map<String, Object> map);
	
	/**
	 * 新增权限
	 * @param map
	 * @return
	 */
	public ReturnMsg addPrm(Map<String, Object> map);
	

}