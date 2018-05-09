package com.easy.controller;

import com.easy.common.ExcelUtil;
import com.easy.common.ReturnMsg;
import com.easy.entity.User;
import com.easy.service.DailyService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EnableTransactionManagement
@Controller
@RequestMapping(value = "/daily")
public class DailyController  extends BaseController {
    Logger log = LoggerFactory.getLogger(DailyController.class);

    @Autowired
    private DailyService dailyService;

    /**
     * 添加日报
     * @param param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addDaily")
    @ResponseBody
    public ReturnMsg addDaily(@RequestBody ModelMap param,
                              HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = new ReturnMsg();
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        param.put("uid",user.getUid());
        try {
        ret=dailyService.querySingleDaily(param);
        if(ret.getCode()!=101){
            ret.setCode(110);
            ret.setMsg("日报或周报已发送，请进行修改");
            return ret;
        }
            ret=dailyService.addDaily(param);
        } catch (Exception e) {
            ret.setCode(200);
            ret.setMsg("添加失败");
            return ret;
        }
        return ret;
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/querySuperDaily", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg querySuperDaily(String page, String limit, String username, String date1, String date2, String dstatus,
                                     HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
            map.put("pageStart",
                    (Integer.parseInt(page)-1) * Integer.parseInt(limit));
            map.put("limit", limit);
            map.put("username", username);
            map.put("date1", date1);
            map.put("date2", date2);
            map.put("dstatus", dstatus);

        ReturnMsg ret = dailyService.queryDaily(map);
        return ret;
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryNormalDaily", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg queryNormalDaily(@RequestBody ModelMap param , HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        int limit=5;
        map.put("pageStart",(Integer.parseInt(param.get("i").toString()) - 1) * limit);//Integer.parseInt(limit))
        map.put("limit", limit);

        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        map.put("uid",user.getUid());

        ReturnMsg ret = dailyService.queryDaily(map);
        if(ret.getCode()==100){
            int count=ret.getCount();
            if(param.get("i")!=null&&param.get("i")!=""){
                    if(Integer.parseInt(param.get("i").toString())==(count+limit)/limit){
                        ret.setInfo("last");
                    }
            }
        };
        return ret;
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/querySingleDaily", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg queryUser(@RequestBody ModelMap param, HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = dailyService.querySingleDaily(param);
        return ret;
    }

    @RequestMapping(value = "/updateDaily", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg updateDaily(@RequestBody ModelMap map,
                                HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = new ReturnMsg();
        try {
            User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
            map.put("uid",user.getUid());
            dailyService.deleteDaily(map);
            ret = dailyService.addDaily(map);
            ret.setCode(100);
            ret.setMsg("修改成功！");
        } catch (Exception e) {
            ret.setCode(200);
            ret.setMsg("修改失败");
            return ret;
        }

        return ret;
    }

    @RequestMapping(value = "/updateFalg", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg updateFalg(@RequestBody ModelMap map,
                                HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret =null;
        map.put("pageStart",Integer.valueOf("0"));
        map.put("limit", Integer.valueOf("9999999"));
        try{
            ret = dailyService.queryDaily(map);
            if (ret.getCode()==120) {
                ret.setCode(110);
                ret.setMsg("无数据");
                return ret;
            }
            ret=dailyService.updateFlag((List<Map<String, Object>>)ret.getData());
        }catch(Exception e){
            e.printStackTrace();
            ret.setCode(200);
            ret.setMsg("系统异常");
        }
        return ret;
    }

    @Transactional//(value="txManager1")
    @RequestMapping(value = "/exportExcel")
    @RequiresPermissions("/daily/exportExcel")
    @ResponseBody
    public ReturnMsg queryMessage2(String username, String dstatus,
                              String date1, String date2, HttpServletRequest request,
                              HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("pageStart",Integer.valueOf("0"));
        map.put("limit", Integer.valueOf("9999999"));
        map.put("username", username);
        map.put("date1", date1);
        map.put("date2", date2);
        map.put("flag","1");
        map.put("dstatus", dstatus);
        ReturnMsg ret=null;
        try{
            ret = dailyService.queryDaily(map);

            Map<String, String> titles = new LinkedHashMap<String, String>();
            titles.put("userName", "姓名");
            titles.put("title", "标题");
            titles.put("daily", "内容");
            titles.put("dstatus", "类型");
            titles.put("date", "时间");

            //titles 表格的列名，可以不手动添加，自动取list第一行数据，生成;
            //ExcelUtil.expExcel("留言",null,(List)ret.getData(),response);
            ExcelUtil.expExcel("日报表",titles,(List)ret.getData(),response);
            ret.setCode(100);
            ret.setMsg("导出成功");
        }catch(Exception e){
            e.printStackTrace();
            ret.setCode(200);
            ret.setMsg("系统异常");
        }finally {
            return ret;
        }
    }

    /**
     * 删除日报
     *
     * @param map
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/deleteDaily", method = RequestMethod.POST)
    @RequiresPermissions("/daily/deleteDaily")
    @ResponseBody
    public ReturnMsg deleteUser(@RequestBody ModelMap map,
                                HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = new ReturnMsg();
        try {
            dailyService.deleteDaily(map);
            ret.setCode(100);
            ret.setMsg("删除成功");
        } catch (Exception e) {
            ret.setCode(200);
            ret.setMsg("删除失败");
        }
        return ret;
    }
}
