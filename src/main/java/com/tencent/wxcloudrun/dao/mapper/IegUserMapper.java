package com.tencent.wxcloudrun.dao.mapper;

import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;

import java.util.List;

/**
 * @author zhangyichuan
 */
public interface IegUserMapper {
    List<IegUserDO> getIegUserList();

    /**
     * 保存用户信息，如果用户不存在，则新增，如果用户存在，则更新
     */
    void saveIegUser(IegUserDO iegUserDO);
}
