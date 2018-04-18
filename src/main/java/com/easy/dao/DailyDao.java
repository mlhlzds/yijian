package com.easy.dao;

import com.easy.entity.Menu;
import com.easy.entity.Permission;
import com.easy.entity.Role;
import com.easy.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DailyDao {

    /**
     *
     * @param map
     * @return
     */
    public int addDaily(Map<String, Object> map);

    /**
     * 查询日志
     * @param param
     * @return
     */
    public List<Map<String,Object>> queryDaily(Map<String,Object> param);

    /**
     * 查询单个日志
     * @param param
     * @return
     */
    public List<Map<String,Object>> querySingleDaily(Map<String,Object> param);

    /**
     *
     * @param param
     * @return
     */
    public int countDaily(Map<String, Object> param);

    /**
     *
     * @param map
     * @return
     */
    public int updateDaily(Map<String, Object> map);

    /**
     *
     * @param paMap
     * @return
     */
    public int deleteDaily(Map<String, Object> paMap);

}
