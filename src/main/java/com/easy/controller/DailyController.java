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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            ret=dailyService.addDaily(param);
        } catch (Exception e) {
            ret.setCode(200);
            ret.setMsg("添加日报失败");
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
    @RequestMapping(value = "/queryDaily", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMsg queryDaily(@RequestBody ModelMap param , HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        int limit=5;
        map.put("pageStart",
                (Integer.parseInt(param.get("i").toString()) - 1) * limit);//Integer.parseInt(limit))
        map.put("limit", limit);
        ReturnMsg ret = dailyService.queryDaily(map);
        int count=ret.getCount();
        if(((Integer.parseInt(param.get("i").toString())) * limit)==count){
            ret.setInfo("last");
        }
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
    public ReturnMsg updateRole(@RequestBody ModelMap map,
                                HttpServletRequest request, HttpServletResponse response) {
        ReturnMsg ret = dailyService.updateDaily(map);
        return ret;
    }

    @RequestMapping(value = "/exportExcel")
    @RequiresPermissions("/daily/exportExcel")
    @ResponseBody
    public void queryMessage2(String page, String limit,String username,
                              String date1, String date2, HttpServletRequest request,
                              HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("pageStart",Integer.valueOf("0"));
        map.put("limit", Integer.valueOf("9999999"));
        map.put("username", username);
        map.put("date1", date1);
        map.put("date2", date2);
        map.put("flag","1");
        ReturnMsg ret = dailyService.queryDaily(map);


        Map<String, String> titles = new LinkedHashMap<String, String>();
        titles.put("userName", "姓名");
        titles.put("title", "标题");
        titles.put("daily", "日报");
        titles.put("date", "时间");

        //titles 表格的列名，可以不手动添加，自动取list第一行数据，生成;
        //ExcelUtil.expExcel("留言",null,(List)ret.getData(),response);
        ExcelUtil.expExcel("日报表",titles,(List)ret.getData(),response);
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
            ret.setMsg("删除日报成功");
        } catch (Exception e) {
            ret.setCode(200);
            ret.setMsg("删除日报失败");
        }

        return ret;
    }
}
