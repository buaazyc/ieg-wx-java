package com.tencent.wxcloudrun.dao.mapper;

import com.tencent.wxcloudrun.dao.dataobject.ReadBookUserTkDO;
import java.util.List;

/**
 * @author zhangyichuan
 */
public interface ReadBookUserTkMapper {
    void insertReadBookUserTk(ReadBookUserTkDO readBookUserTkDO);

    List<ReadBookUserTkDO> getReadBookUserTk(String bookName);
}
