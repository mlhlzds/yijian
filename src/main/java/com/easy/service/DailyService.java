package com.easy.service;

import com.easy.common.ReturnMsg;
import com.easy.entity.User;

import java.util.List;
import java.util.Map;


public interface DailyService {

	/**
	 * 添加日报
	 * @param param
	 * @return
	 */
	public ReturnMsg addDaily(Map<String, Object> param);

	/**
	 * 查询日报
	 * @param param
	 * @return
	 */
	public ReturnMsg queryDaily(Map<String, Object> param);

	/**
	 * 查询日报
	 * @param param
	 * @return
	 */
	public ReturnMsg querySingleDaily(Map<String, Object> param);

	/**
	 *
	 * @param map
	 * @return
	 */
	public ReturnMsg updateDaily(Map<String, Object> map);

	/**
	 *
	 * @param list
	 * @return
	 */
	public ReturnMsg updateFlag(List list);

	/**
	 * 删除日报
	 * @param param
	 */
	public void deleteDaily(Map<String, Object> param);
}