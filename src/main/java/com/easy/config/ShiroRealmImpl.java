package com.easy.config;


import com.easy.entity.Permission;
import com.easy.entity.Role;
import com.easy.entity.User;
import com.easy.dao.UserDao;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ShiroRealmImpl extends AuthorizingRealm {

    // 用户对应的角色信息与权限信息都保存在数据库中，通过userDao获取数据
    @Autowired
    private UserDao userDao ;

    /**
     * 提供用户信息返回权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        Set<String> roleNames = new HashSet<String>();//可使用userDao查数据库取得
        for (Role role : user.getRoles()) {
            roleNames.add(role.getRole());
        }
        // 将角色名称提供给info
        authorizationInfo.setRoles(roleNames);
        Set<String> permissionNames = new HashSet<String>();//可使用userDao查数据库取得
        for (Permission permission : user.getPermissions()) {
            permissionNames.add(permission.getPermission());
        }
        // 将权限名称提供给info
        authorizationInfo.setStringPermissions(permissionNames);

        return authorizationInfo;
    }

    /**
     * 提供账户信息返回认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException{
        UsernamePasswordToken uptoken = (UsernamePasswordToken) token;
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("username", uptoken.getUsername());
        System.err.println(uptoken.getUsername());
        User user = userDao.selectUserByName(param);
        if(user == null){
            throw new UnknownAccountException();
        }
        return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), ByteSource.Util.bytes(user.getUid()), getName());
    }
}