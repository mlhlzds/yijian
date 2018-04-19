package com.easy.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easy.common.MD5;
import com.easy.common.ReturnMsg;
import com.easy.entity.Menu;
import com.easy.entity.User;
import com.easy.dao.UserDao;
import com.easy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public User login(Map<String, Object> param) {
		User user = userDao.selectUserByName(param);
		user.setRoles(userDao.queryRolesByUser(user.getUid()));
		user.setPermissions(userDao.queryPermissionsByUser(user.getUid()));
		return user;
	}

	@Override
	public int addUser(Map<String, Object> param) {
		int i = userDao.addUser(param);
		if (i == 1) {
			String password = MD5.shiroEncrypt(
					(String) param.get("password"),
					String.valueOf(param.get("uid")),
					3, 
					"md5");
			param.put("password", password);
			userDao.passwordUpdate(param);
			userDao.addUserAndRole(param);
		}
		return i;
	}

	
	@Override
	public List getMenuByUid(String id) {
		List<Menu> menuList = userDao.selectMenuByUid(id);// 查询用户
		List<Map<String, Object>> mpLst = new ArrayList<Map<String,Object>>();
		Map<String, Map<String, Object>> mpMap = new HashMap<String, Map<String,Object>>();
		Map<String, Object> mp = null;
		Map<String, Object> mc = null;
		
		for (Menu menu : menuList) {
			if(menu.getParentId() == null){
				mp = new HashMap<String, Object>();
				mp.put("menuName", menu.getMenuName());
				mp.put("menuChild", new ArrayList<Map<String,Object>>() );
				mpLst.add(mp);
				
				mpMap.put(menu.getMid(), mp);
			} 
		}
		
		for (Menu menu : menuList) {
			if(menu.getParentId() != null){
				mc = new HashMap<String, Object>();
				mc.put("menuName", menu.getMenuName());
				mc.put("menuUrl", menu.getMenuUrl());
				((List<Map<String,Object>>)mpMap.get(menu.getParentId()).get("menuChild")).add(mc);
			}
		}
		return mpLst;
	}
	
	@Override
	public ReturnMsg updatePassword(Map<String, Object> param) {
		ReturnMsg ret = new ReturnMsg();
		User user = (User) param.get("user");
		String oldPassword = MD5.shiroEncrypt(
				(String) param.get("oldPassword"), 
				String.valueOf(user.getUid()),
				3, 
				"md5");
		if (user != null && oldPassword.equals(user.getPassword())) {
			String password = MD5.shiroEncrypt(
					(String) param.get("newPassword"), 
					String.valueOf(user.getUid()),
					3, 
					"md5");
			param.put("uid", user.getUid());
			param.put("password", password);
			user.setPassword(password);
			userDao.passwordUpdate(param);
			ret.setCode(100);
			ret.setMsg("修改成功");
		} else if (!oldPassword.equals(user.getPassword())) {
			ret.setCode(999);
			ret.setMsg("旧密码输入密码错误!");
		} else {
			ret.setCode(999);
			ret.setMsg("未知错误");
		}
		return ret;
	}
	
	@Override
	public ReturnMsg resetPassword(Map<String, Object> param) {
		ReturnMsg ret = new ReturnMsg();
		try{
			String password = MD5.shiroEncrypt(
					"123456", 
					String.valueOf(param.get("uid")), 
					3, 
					"md5");
			param.put("username", param.get("username"));
			param.put("password", password);
			int flag=userDao.passwordUpdate(param);
			if(flag==0){
				ret.setMsg("重置失败");
			}else{
				ret.setCode(100);
				ret.setMsg("重置成功");
			}
		} catch (Exception e){
			log.info("系统异常: ", e);
			ret.setCode(100);
			ret.setMsg("系统异常");
		}
		return ret;
	}
	
	@Override
	public ReturnMsg queryRoles(Map<String,Object> param) {
		ReturnMsg ret = new ReturnMsg();
		try{
			List<Map<String, Object>> roleList = userDao.queryRoles(param);
			ret.setData(roleList);
			ret.setCode(100);
			ret.setMsg("获取角色成功！");
		} catch (Exception e){
			log.info("系统异常: ", e);
			ret.setCode(200);
			ret.setMsg("系统异常！");
		}
		return ret;
	}
	
	@Override
	public ReturnMsg queryPagesRoles(Map<String,Object> param) {
		ReturnMsg ret = new ReturnMsg();
		try{
			List<Map<String, Object>> roleList = userDao.queryPagesRoles(param);
			int cnt = userDao.countRole(param);
			ret.setData(roleList);
			ret.setCount(cnt);
			ret.setCode(100);
			ret.setMsg("获取角色成功！");
		} catch (Exception e){
			log.info("系统异常: ", e);
			ret.setCode(200);
			ret.setMsg("系统异常！");
		}
		return ret;
	}
	
	@Override
	public ReturnMsg queryUser(Map<String, Object> param) {
		ReturnMsg ret = new ReturnMsg();
		try {
			List<Map<String, Object>> userList = userDao.queryUser(param);// 查询用户
			ret.setData(userList);
			ret.setCode(100);
			ret.setMsg("查询用户成功");
			ret.setCount(userDao.countUser(param));
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setMsg("查询失败");
		}
		return ret;
	}
	
	@Override
	public void deleteUser(Map<String, Object> param) {
		userDao.deleteUserAndRole(param);
		userDao.deleteUser(param);
	}

	@Override
	public ReturnMsg deleteRole(Map<String, Object> map) {
		ReturnMsg ret = new ReturnMsg();
		try {
			int i = userDao.countUserAndRole(map);
			if(i > 0){
				ret.setCode(108);
				ret.setMsg("此角色正在使用中,不可删除");
			} else {
				userDao.deleteRole(map);
				userDao.deleteRoleAndMenuByRid(map);
				ret.setCode(100);
				ret.setMsg("删除成功");
			}
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setCode(999);
			ret.setMsg("系统异常，删除失败");
		}
		
		return ret;
	}

	@Override
	public ReturnMsg updateRole(Map<String, Object> map) {
		ReturnMsg ret = new ReturnMsg();
		try {
			userDao.deleteRoleAndMenus(map);
			userDao.insertRoleAndMenus(map);
			userDao.updateRole(map);
			ret.setCode(100);
			ret.setMsg("修改成功");
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setCode(999);
			ret.setMsg("系统异常，修改失败");
		}
		
		return ret;
	}

	@Override
	public ReturnMsg updateState(Map<String, Object> map) {
		ReturnMsg ret = new ReturnMsg();
		try {
			userDao.updateState(map);
			ret.setCode(100);
			ret.setMsg("修改成功");
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setCode(999);
			ret.setMsg("系统异常，修改失败");
		}

		return ret;
	}

	@Override
	public ReturnMsg addRole(Map<String, Object> map) {
		ReturnMsg ret = new ReturnMsg();
		try {
			userDao.addRole(map);
			userDao.insertRoleAndMenus(map);
			ret.setCode(100);
			ret.setMsg("添加成功");
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setCode(999);
			ret.setMsg("系统异常，添加失败");
		}
		return ret;
	}

	@Override
	public ReturnMsg queryRoleAndMenu(Map<String, Object> map) {
		ReturnMsg ret = new ReturnMsg();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			if(map.get("rid") != null && !"".equals(map.get("rid"))){
				List<Map<String, Object>> roleList = userDao.queryRoles(map);
				if(roleList.size() > 0){
					dataMap = roleList.get(0);
				} else {
					ret.setCode(109);
					ret.setMsg("获取角色信息失败");
					return ret;
				}
			} else {
				dataMap.put("role", "");
				dataMap.put("roleName", "");
			}
				
			List<Map<String, Object>> menuList = userDao.queryMenuByRid(map);
			
			List<Map<String, Object>> mpLst = new ArrayList<Map<String,Object>>();
			Map<String, Map<String, Object>> mpMap = new HashMap<String, Map<String,Object>>();
			Map<String, Object> mp = null;
			Map<String, Object> mc = null;
			
			for (Map<String, Object> menu : menuList) {
				if(menu.get("parent_id") == null){
					mp = new HashMap<String, Object>();
					mp.put("mid", menu.get("m_id"));
					mp.put("menuName", menu.get("menu_name"));
					if(menu.get("flag") != null){
						mp.put("flag", "checked=''");
					} else {
						mp.put("flag", "");
					}
					mp.put("mchild", new ArrayList<Map<String,Object>>() );
					mpLst.add(mp);
					
					mpMap.put(String.valueOf(menu.get("m_id")), mp);
				} 
			}
			
			for (Map<String, Object> menu : menuList) {
				if(menu.get("parent_id") != null){
					mc = new HashMap<String, Object>();
					mc.put("mid", menu.get("m_id"));
					mc.put("menuName", menu.get("menu_name"));
					if(menu.get("flag") != null){
						mc.put("flag", "checked=''");
					} else {
						mc.put("flag", "");
					}
					((List<Map<String,Object>>)mpMap.get(String.valueOf(menu.get("parent_id"))).get("mchild")).add(mc);
				}
			}
			
			dataMap.put("menus", mpLst);
			
			ret.setData(dataMap);
			ret.setCode(100);
			ret.setMsg("获取角色信息成功");
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setCode(999);
			ret.setMsg("系统异常获取信息失败");
		}
		return ret;
	}

	@Override
	public ReturnMsg queryPagesMenus(Map<String, Object> map) {
		ReturnMsg ret = new ReturnMsg();
		try{
			List<Map<String, Object>> menuList = userDao.queryPagesMenus(map);
			ret.setData(menuList);
			ret.setCount(userDao.countMenus(map));
			ret.setCode(100);
			ret.setMsg("获取菜单成功！");
		} catch (Exception e){
			log.info("系统异常: ", e);
			ret.setCode(200);
			ret.setMsg("系统异常！");
		}
		return ret;
	}

	@Override
	public ReturnMsg queryPagesPrms(Map<String, Object> map) {
		ReturnMsg ret = new ReturnMsg();
		try{
			List<Map<String, Object>> menuList = userDao.queryPagesPrms(map);
			ret.setData(menuList);
			ret.setCount(userDao.countPrms(map));
			ret.setCode(100);
			ret.setMsg("获取菜单成功！");
		} catch (Exception e){
			log.info("系统异常: ", e);
			ret.setCode(200);
			ret.setMsg("系统异常！");
		}
		return ret;
	}

	@Override
	public ReturnMsg addPrm(Map<String, Object> map) {
		ReturnMsg ret = new ReturnMsg();
		try {
			userDao.addPrm(map);
			ret.setCode(100);
			ret.setMsg("添加成功");
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setCode(999);
			ret.setMsg("系统异常，添加失败");
		}
		return ret;
	}
	
}
