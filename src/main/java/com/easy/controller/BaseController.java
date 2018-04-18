package com.easy.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.easy.common.JsonUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class BaseController {

    /**
     * 登录认证异常
     */
    @ExceptionHandler({ UnauthenticatedException.class, AuthenticationException.class})
    public void authenticationException(HttpServletRequest request, HttpServletResponse response) {
        // 输出JSON
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("code", "109");
        map.put("msg", "身份认证失败！");
        writeJson(map, response);
    }

    /**
     * 权限异常
     */
    @ExceptionHandler({ UnauthorizedException.class, AuthorizationException.class})
    public void authorizationException(HttpServletRequest request, HttpServletResponse response) {
        // 输出JSON
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("code", "109");
        map.put("msg", "用户没有权限！");
        writeJson(map, response);
    }

    /**
     * 输出JSON
     *
     * @param response
     * @author SHANHY
     * @create 2017年4月4日
     */
    private void writeJson(Map<String,Object> map, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            out = response.getWriter();
            out.write(JsonUtil.map2json(map));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
