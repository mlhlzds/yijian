package com.easy.controller;

import com.easy.common.ReturnMsg;
import com.easy.entity.Role;
import com.easy.entity.User;
import com.easy.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/user")

public class UserController extends BaseController {
    Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param param
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public ReturnMsg login(@RequestBody ModelMap param) {
        ReturnMsg ret = new ReturnMsg();
        UsernamePasswordToken token = new UsernamePasswordToken((String) param.get("username"), (String) param.get("password"));
        Subject subject = SecurityUtils.getSubject();

        try {
            subject.login(token);
            User user = userService.login(param);
            if("3".equals(user.getActive())){
                ret.setCode(104);
                ret.setMsg("用户已离职，无法登陆！");
            }else{
                subject.getSession().setAttribute("user", user);
                for(Role role : user.getRoles()){
                    if("2".equals(role.getRid())){
                        ret.setData(user);
                        ret.setCode(105);
                        return ret;
                    };
                }
                ret.setData(user);
                ret.setCode(100);
                ret.setMsg("登录成功！");
            }
        } catch (IncorrectCredentialsException ice) {
            // 捕获密码错误异常
            ret.setCode(101);
            ret.setMsg("用户名或密码错误！");
        } catch (UnknownAccountException uae) {
            // 捕获未知用户名异常
            ret.setCode(102);
            ret.setMsg("用户名或密码错误！");
        } catch (ExcessiveAttemptsException eae) {
            // 捕获错误登录过多的异常
            ret.setCode(103);
            ret.setMsg("错误登录次数过多！");
        } catch (Exception e) {
            e.printStackTrace();
            ret.setCode(200);
            ret.setMsg("系统异常");
        }
        return ret;
    }

    /**
     * 注销用户
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg logout(HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = new ReturnMsg();
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout();
            ret.setCode(100);
            ret.setMsg("退出成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ret.setCode(200);
            ret.setMsg("系统异常");
        }
        return ret;
    }

    /**
     * 新增用户
     * @param param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addUser")
    @RequiresPermissions("/user/addUser")
    @ResponseBody
    public ReturnMsg addUser(@RequestBody ModelMap param,
                             HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = new ReturnMsg();
        try {
            param.put("password","123456");
            userService.addUser(param);
            ret.setCode(100);
            ret.setMsg("添加用户成功！");

        } catch (Exception e) {
            ret.setCode(200);
            ret.setMsg("添加用户失败,请检查是否有重名用户");
            return ret;
        }
        return ret;
    }

    /**
     * 查询权限
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/menu", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg getMenu(HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = new ReturnMsg();
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
            data.put("loginName", user.getUsername());
            List menuList = userService.getMenuByUid(user.getUid());
            data.put("menu", menuList);
            ret.setData(data);
            ret.setCode(100);
            ret.setMsg("获取菜单成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
            ret.setCode(200);
            ret.setMsg("系统异常！");
        }
        return ret;
    }

    /**
     * 修改密码
     * @param param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg updatePassword(@RequestBody ModelMap param,
                                    HttpServletRequest request, HttpServletResponse response) {
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        param.put("user", user);
        ReturnMsg ret = userService.updatePassword(param);
        return ret;
    }

    /**
     * 重置密码
     * @param param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @RequiresPermissions("/user/resetPassword")
    @ResponseBody
    public ReturnMsg resetPassword(@RequestBody ModelMap param,
                                   HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = userService.resetPassword(param);
        return ret;
    }

    /**
     * 根据条件查询用户列表
     * @param page
     * @param limit
     * @param username
     * @param roleId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryUser", method = RequestMethod.POST)
    @RequiresPermissions("/user/queryUser")
    @ResponseBody
    public ReturnMsg queryUser(String page, String limit, String username,
                               String roleId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pageStart",
                (Integer.parseInt(page) - 1) * Integer.parseInt(limit));
        map.put("limit", Integer.valueOf(limit));
        map.put("username", username);
        map.put("roleId", roleId);
        ReturnMsg ret = userService.queryUser(map);
        return ret;
    }

    /**
     * 查询所有角色
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryRoles", method = RequestMethod.POST)
    @RequiresPermissions("/user/queryRoles")
    @ResponseBody
    public ReturnMsg queryRoles(HttpServletRequest request,
                                HttpServletResponse response) {
        ReturnMsg ret = userService.queryRoles(new HashMap<String, Object>());
        return ret;
    }

    /**
     * 分页查询所有角色
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryPagesRoles", method = RequestMethod.POST)
    @RequiresPermissions("/user/queryPagesRoles")
    @ResponseBody
    public ReturnMsg queryPagesRoles(String page, String limit, String rid, HttpServletRequest request,
                                     HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pageStart",
                (Integer.parseInt(page) - 1) * Integer.parseInt(limit));
        map.put("limit", Integer.valueOf(limit));
        map.put("rid", rid);
        ReturnMsg ret = userService.queryPagesRoles(map);
        return ret;
    }

    /**
     * 删除用户
     *
     * @param map
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    @RequiresPermissions("/user/deleteUser")
    @ResponseBody
    public ReturnMsg deleteUser(@RequestBody ModelMap map,
                                HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = new ReturnMsg();
        try {
            userService.deleteUser(map);
            ret.setCode(100);
            ret.setMsg("删除用户成功");
        } catch (Exception e) {
            ret.setCode(200);
            ret.setMsg("删除用户失败");
        }

        return ret;
    }

    @RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
    @RequiresPermissions("/user/deleteRole")
    @ResponseBody
    public ReturnMsg deleteRole(@RequestBody ModelMap map,
                                HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = userService.deleteRole(map);
        return ret;
    }

    @RequestMapping(value = "/updateRole", method = RequestMethod.POST)
    @RequiresPermissions("/user/updateRole")
    @ResponseBody
    public ReturnMsg updateRole(@RequestBody ModelMap map,
                                HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = userService.updateRole(map);
        return ret;
    }

    @RequestMapping(value = "/updateState", method = RequestMethod.POST)
    @RequiresPermissions("/user/updateState")
    @ResponseBody
    public ReturnMsg updateState(@RequestBody ModelMap map,
                                HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret=null;
        try {
           ret = userService.updateState(map);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    @RequestMapping(value = "/addRole", method = RequestMethod.POST)
    @RequiresPermissions("/user/addRole")
    @ResponseBody
    public ReturnMsg addRole(@RequestBody ModelMap map,
                             HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = userService.addRole(map);
        return ret;
    }

    /**
     * 根据id查询角色和相关菜单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryRoleAndMenu", method = RequestMethod.POST)
    @RequiresPermissions("/user/queryRoleAndMenu")
    @ResponseBody
    public ReturnMsg queryRoleAndMenu(@RequestBody ModelMap map, HttpServletRequest request,
                                      HttpServletResponse response) {
        ReturnMsg ret = userService.queryRoleAndMenu(map);
        return ret;
    }

    /**
     * 分页查询所有菜单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryPagesMenus", method = RequestMethod.POST)
    @RequiresPermissions("/user/queryPagesMenus")
    @ResponseBody
    public ReturnMsg queryPagesMenus(String page, String limit, HttpServletRequest request,
                                     HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pageStart",
                (Integer.parseInt(page) - 1) * Integer.parseInt(limit));
        map.put("limit", Integer.valueOf(limit));
        ReturnMsg ret = userService.queryPagesMenus(map);
        return ret;
    }

    /**
     * 分页查询所有权限
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryPagesPrms", method = RequestMethod.POST)
    @RequiresPermissions("/user/queryPagesPrms")
    @ResponseBody
    public ReturnMsg queryPagesPrms(String page, String limit, HttpServletRequest request,
                                    HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pageStart",
                (Integer.parseInt(page) - 1) * Integer.parseInt(limit));
        map.put("limit", Integer.valueOf(limit));
        ReturnMsg ret = userService.queryPagesPrms(map);
        return ret;
    }

    @RequestMapping(value = "/deleteHoliday", method = RequestMethod.POST)
    @RequiresPermissions("/user/deleteHoliday")
    @ResponseBody
    public ReturnMsg deleteHoliday(@RequestBody ModelMap map,
                                HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = userService.deleteRole(map);
        return ret;
    }

    @RequestMapping(value = "/updateHoliday", method = RequestMethod.POST)
    @RequiresPermissions("/user/updateHoliday")
    @ResponseBody
    public ReturnMsg updateHoliday(@RequestBody ModelMap map,
                                HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = userService.updateRole(map);
        return ret;
    }

}