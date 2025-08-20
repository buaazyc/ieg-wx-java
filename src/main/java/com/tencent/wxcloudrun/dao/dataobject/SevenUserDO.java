package com.tencent.wxcloudrun.dao.dataobject;

import com.tencent.wxcloudrun.domain.constant.Constants;
import java.io.Serializable;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
@Data
public class SevenUserDO implements Serializable {

  private String gender;

  private String userName;

  private String email;

  private String bookList;

  private String queryList;

  private String wxId;

  private String docName;

  public SevenUserDO(String userName, String gender, String email, String bookList, String queryList) {
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
    return "笔名：" + userName  + "\n性别：" + gender + "\n书单：" + bookList + "\n问题：" + queryList +
            "\n\n快来给TA留言吧！\n"
            + Constants.sendMailHelper();
  }
}
