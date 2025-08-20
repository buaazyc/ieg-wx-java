package com.tencent.wxcloudrun.dao.mapper;

import com.tencent.wxcloudrun.dao.dataobject.SevenUserDO;
import java.util.List;

/**
 * @author zhangyichuan
 */
public interface SevenUserMapper {
    List<SevenUserDO> getSevenUserList();

    /**
     * 保存用户信息，如果用户不存在，则新增，如果用户存在，则更新
     */
    void saveSevenUser(SevenUserDO sevenUserDO);
}
