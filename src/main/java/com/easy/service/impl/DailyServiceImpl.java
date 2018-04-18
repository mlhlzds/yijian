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
				ret.setCode(100);
				ret.setMsg("添加日报成功！");
			}
		} catch (Exception e) {
			log.info("系统异常: ", e);
		}
		return ret;
	}

	@Override
	public ReturnMsg queryDaily(Map<String, Object> param) {

		ReturnMsg ret = new ReturnMsg();
		try {
			List<Map<String, Object>> userList = dailyDao.queryDaily(param);// 查询日报
			ret.setData(userList);
			ret.setCode(100);
			ret.setMsg("查询用户成功");
			ret.setCount(dailyDao.countDaily(param));
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
			ret.setData(userList.get(0));
			ret.setCode(100);
			ret.setMsg("查询日报成功");
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
			dailyDao.updateDaily(map);
			ret.setCode(100);
			ret.setMsg("修改日报成功");
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
