package com.tencent.wxcloudrun.domain.entity;

import com.tencent.wxcloudrun.domain.constant.CmdEnum;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/7/21
 */
@Data
public class ClockEntity {
    private String userName;

    private String thinking;

    private boolean ok;

    public ClockEntity(String content) {
        if (content == null || content.isEmpty()) {
            ok = false;
            return;
        }
        String[] parts = content.split("，", 3);
        if (parts.length != 3 || !parts[0].equals(CmdEnum.CLOCK.getCmd())) {
            ok = false;
            return;
        }
        userName = parts[1];
        thinking = parts[2];
        ok = true;
    }
}
