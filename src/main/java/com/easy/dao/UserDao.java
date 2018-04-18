package com.easy.dao;

import com.easy.entity.Menu;
import com.easy.entity.Permission;
import com.easy.entity.Role;
import com.easy.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserDao {
    public List<Menu> queryAllMenus();
    public List<Role> queryAllRoles();
    public List<Permission> queryAllPermissions();

    /**
     * 查询用户
     * @param param username
     * @return
     */
    public User selectUserByName(Map<String, Object> param);

    /**
     * 添加用户 t_user
     * @param param username
     * @return
     */
    public int addUser(Map<String, Object> param);

    /**
     * 添加用户和角色表 t_user_role
     * @param param uid roleId
     * @return
     */
    public int addUserAndRole(Map<String, Object> param);

    /**
     * 修改密码
     * @param param password uid
     * @return
     */
    public int passwordUpdate(Map<String,Object> param);

    /**
     * 通过用户id查询菜单列表
     * @param id
     * @return
     */
    public List<Menu> selectMenuByUid(String id);

    /**
     * 查询角色
     * @param param rid pageStart limit
     * @return
     */
    public List<Map<String, Object>> queryRoles(Map<String,Object> param);

    /**
     *
     * @param uid
     * @return
     */
    public List<Role> queryRolesByUser(String uid);

    /**
     *
     * @param id
     * @return
     */
    public List<Permission> queryPermissionsByUser(String id);

    /**
     *
     * @param param username roleId
     * @return
     */
    public int countUser(Map<String, Object> param);

    /**
     * 查询用户列表
     * @param param username roleId pageStart limit
     * @return
     */
    public List<Map<String,Object>> queryUser(Map<String,Object> param);

    /**
     *
     * @param paMap
     * @return
     */
    public int deleteUser(Map<String, Object> paMap);

    /**
     *
     * @param paMap
     * @return
     */
    public int deleteUserAndRole(Map<String, Object> paMap);

    /**
     *
     * @param map
     * @return
     */
    public int countUserAndRole(Map<String, Object> map);

    /**
     *
     * @param map
     * @return
     */
    public int deleteRole(Map<String, Object> map);

    /**
     *
     * @param map
     * @return
     */
    public int deleteRoleAndMenuByRid(Map<String, Object> map);

    /**
     *
     * @param map
     * @return
     */
    public int updateRole(Map<String, Object> map);

    /**
     *
     * @param map
     * @return
     */
    public int updateState(Map<String, Object> map);

    /**
     *
     * @param map
     * @return
     */
    public int addRole(Map<String, Object> map);

    /**
     *
     * @param map
     * @return
     */
    public int countRole(Map<String, Object> map);

    /**
     *
     * @param map
     * @return
     */
    public List<Map<String, Object>> queryMenuByRid(Map<String, Object> map);

    /**
     *
     * @param map
     */
    public int insertRoleAndMenus(Map<String, Object> map);

    /**
     *
     * @param map
     */
    public int deleteRoleAndMenus(Map<String, Object> map);

    public List<Map<String, Object>> queryPagesRoles(Map<String, Object> param);

    public List<Map<String, Object>> queryPagesMenus(Map<String, Object> map);

    public int countMenus(Map<String, Object> map);

    public List<Map<String, Object>> queryPagesPrms(Map<String, Object> map);

    public int countPrms(Map<String, Object> map);

    /**
     * 添加权限
     * @param map
     */
    public void addPrm(Map<String, Object> map);


}
