package com.tencent.wxcloudrun.domain.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/7/14
 */
@Data
public class SaveIegEntity implements Serializable {
    private String cmd;

    private String userName;

    private String gender;

    private String email;

    private String bookList;

    private String queryList;

    private boolean ok;

    public SaveIegEntity(String content) {
        if (content == null || content.isEmpty()) {
            ok = false;
            return;
        }
        String[] parts = content.split("\n", 6);
        if (parts.length != 6) {
            ok = false;
            return;
        }
        cmd = parts[0];
        userName = parts[1];
        gender = parts[2];
        email = parts[3];
        bookList = parts[4];
        queryList = parts[5];
        ok = true;
    }
}
