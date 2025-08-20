package com.tencent.wxcloudrun.dao.mapper;

import com.tencent.wxcloudrun.dao.dataobject.ReadBookActDO;
import java.util.List;

/**
 * @author zhangyichuan
 */
public interface ReadBookActMapper {
    void saveReadBookAct(ReadBookActDO readBookActDO);

    ReadBookActDO getCurReadBookAct(String date);

    List<ReadBookActDO> getReadBookActList();
}
