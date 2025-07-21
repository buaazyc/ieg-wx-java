package com.tencent.wxcloudrun.dao.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyichuan
 * @date 2025/7/21
 */
@Data
public class ReadBookActDO implements Serializable {
    private String bookName;

    private String beginDate;

    private String endDate;

    public ReadBookActDO(String bookName, String beginDate, String endDate ) {
        this.bookName = bookName;
        this.beginDate = beginDate;
        this.endDate = endDate;

    }
}
