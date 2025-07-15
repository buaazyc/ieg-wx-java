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

  private String bookList;

  private String queryList;

  private String wxId;

  private String docName;

  public IegUserDO(String userName, String gender, String email, String bookList, String queryList) {
    this.userName = userName;
    this.gender = gender;
    this.email = email;
    this.bookList = bookList;
    this.queryList = queryList;
  }

  public String print() {
    return "笔名：" + userName + "\n性别：" + gender + "\n邮箱：" + email + "\n书单：" + bookList + "\n问题：" + queryList;
  }

  public String printBox() {
    return "笔名：" + userName  + "\n书单：" + bookList + "\n问题：" + queryList;
  }
}
