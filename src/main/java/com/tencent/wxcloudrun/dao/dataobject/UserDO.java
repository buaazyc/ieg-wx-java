package com.tencent.wxcloudrun.dao.dataobject;

import com.tencent.wxcloudrun.domain.constant.CmdEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyichuan
 * @date 2025/7/23
 */
@Data
public class UserDO implements Serializable {
    /**
     * 微信openId，直接在接口中获取的
     */
    private String wxOpenId;

    /**
     * 笔名
     */
    private String penName;


    public UserDO(String wxOpenId, String penName) {
        this.wxOpenId = wxOpenId;
        this.penName = penName;
    }
}
