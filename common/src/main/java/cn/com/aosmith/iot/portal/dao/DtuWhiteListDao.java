package cn.com.aosmith.iot.portal.dao;

import cn.com.aosmith.iot.portal.entity.DtuWhiteList;

public interface DtuWhiteListDao {
    int deleteByPrimaryKey(Long id);

    int insert(DtuWhiteList record);

    int insertSelective(DtuWhiteList record);

    DtuWhiteList selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DtuWhiteList record);

    int updateByPrimaryKey(DtuWhiteList record);
}