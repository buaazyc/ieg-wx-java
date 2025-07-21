package com.tencent.wxcloudrun.dao.mapper;

import com.tencent.wxcloudrun.dao.dataobject.ReadBookUserDO;

import java.util.List;

/**
 * @author zhangyichuan
 */
public interface ReadBookUserMapper {
    void insertReadBookUser(ReadBookUserDO readBookUserDO);

    List<ReadBookUserDO> getReadBookUser(String bookName);
}
