package com.tencent.wxcloudrun.domain.entity;

import com.tencent.wxcloudrun.domain.constant.CmdEnum;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/7/21
 */
@Data
public class ClockEntity {
    private String thinking;

    private boolean ok;

    public ClockEntity(String content) {
        if (content == null || content.isEmpty()) {
            ok = false;
            return;
        }
        String[] parts = content.split("，", 2);
        if (parts.length != 2 || !parts[0].equals(CmdEnum.CLOCK.getCmd())) {
            ok = false;
            return;
        }
        thinking = parts[1];
        ok = true;
    }
}
