package com.tencent.wxcloudrun.dao.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
@Data
public class IegUserDO implements Serializable {

  private String gender;

  private String userName;

  private String email;

  private String wxId;

  private String bookList;

  private String queryList;

  private String docName;
}
