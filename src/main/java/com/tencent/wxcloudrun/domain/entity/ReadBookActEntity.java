package com.tencent.wxcloudrun.domain.entity;

import com.tencent.wxcloudrun.domain.constant.CmdEnum;
import java.io.Serializable;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/7/21
 */
@Data
public class ReadBookActEntity implements Serializable {
    private String bookName;

    private String beginDate;

    private String endDate;

    private boolean ok;

    public ReadBookActEntity(String content) {
        if (content == null || content.isEmpty()) {
            ok = false;
            return;
        }
        String[] parts = content.split("\n", 4);
        if (parts.length != 4 || !parts[0].equals(CmdEnum.SAVE_ACT.getCmd())) {
            ok = false;
            return;
        }
        bookName = parts[1];
        beginDate = parts[2];
        endDate = parts[3];
        ok = true;
    }
}
