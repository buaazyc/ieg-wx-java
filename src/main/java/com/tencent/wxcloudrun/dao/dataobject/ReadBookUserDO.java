package com.tencent.wxcloudrun.dao.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyichuan
 * @date 2025/7/21
 */
@Data
public class ReadBookUserDO implements Serializable {
    private Integer id;

    private String userName;

    private String wxId;

    private String bookName;

    private String thinking;

    private String date;

    public ReadBookUserDO(String userName, String wxId, String bookName, String thinking, String date) {
        this.userName = userName;
        this.wxId = wxId;
        this.bookName = bookName;
        this.thinking = thinking;
        this.date = date;
    }
}
