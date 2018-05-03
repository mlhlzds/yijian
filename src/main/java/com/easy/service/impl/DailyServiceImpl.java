package com.easy.service.impl;

import com.easy.common.MD5;
import com.easy.common.ReturnMsg;
import com.easy.dao.DailyDao;
import com.easy.dao.UserDao;
import com.easy.entity.Menu;
import com.easy.entity.User;
import com.easy.service.DailyService;
import com.easy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DailyServiceImpl implements DailyService{

	private static Logger log = LoggerFactory.getLogger(DailyServiceImpl.class);
	
	@Autowired
	private DailyDao dailyDao;

	@Override
	public ReturnMsg addDaily(Map<String, Object> map) {
		ReturnMsg ret = new ReturnMsg();
		try {
			int i=dailyDao.addDaily(map);
			if(i>0){
				ret.setData(map);
				ret.setCode(100);
				ret.setMsg("添加成功！");
			}
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setCode(999);
			ret.setMsg("系统异常，修改失败");
		}
		return ret;
	}

	@Override
	public ReturnMsg queryDaily(Map<String, Object> param) {

		ReturnMsg ret = new ReturnMsg();
		try {
			List<Map<String, Object>> userList = dailyDao.queryDaily(param);// 查询日报
			if(userList.size()>0){
				ret.setData(userList);
				ret.setCode(100);
				ret.setMsg("查询日报成功");
				ret.setCount(dailyDao.countDaily(param));
			}else if(userList.size()==0){
				ret.setCode(120);
				ret.setMsg("无数据");
			}
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setMsg("查询失败");
		}
		return ret;
	}

	@Override
	public ReturnMsg querySingleDaily(Map<String, Object> param) {

		ReturnMsg ret = new ReturnMsg();
		try {
			List<Map<String, Object>> userList = dailyDao.querySingleDaily(param);// 查询日报
			if(userList.size()>0){
				ret.setData(userList.get(0));
				ret.setCode(100);
				ret.setMsg("查询日报成功");
			}else{
				ret.setCode(101);
				ret.setMsg("未发送日报");
			}
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setMsg("查询失败");
		}
		return ret;
	}

	@Override
	public ReturnMsg updateDaily(Map<String, Object> map) {
		ReturnMsg ret = new ReturnMsg();
		try {
			int count=dailyDao.updateDaily(map);
			if(count==1){
				ret.setCode(120);
				ret.setMsg("修改成功");
			}else{
				ret.setCode(200);
				ret.setMsg("修改失败");
			}

		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setCode(999);
			ret.setMsg("系统异常，修改失败");
		}

		return ret;
	}

	@Override
	public ReturnMsg updateFlag(List list) {
		ReturnMsg ret = new ReturnMsg();
		try {
			dailyDao.updateFlag(list);
			ret.setCode(100);
			ret.setMsg("修改这状态成功");
		} catch (Exception e) {
			log.info("系统异常: ", e);
			ret.setCode(999);
			ret.setMsg("系统异常，修改失败");
		}

		return ret;
	}

	@Override
	public void deleteDaily(Map<String, Object> param) {
		dailyDao.deleteDaily(param);
	}

}
