package com.tencent.wxcloudrun.dao.mapper;

import com.tencent.wxcloudrun.dao.dataobject.UserDO;
import java.util.List;

/**
 * @author zhangyichuan
 */
public interface UserMapper {
    void insertUser(UserDO userDO);

    List<UserDO> getUserList();
}
