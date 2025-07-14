package com.tencent.wxcloudrun.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhangyichuan
 */
@Data
public class WxResponse {

  private Integer code;
  private String errorMsg;

  @JsonProperty("ToUserName")
  private String toUserName;

  @JsonProperty("FromUserName")
  private String fromUserName;

  @JsonProperty("CreateTime")
  private Integer createTime;

  @JsonProperty("MsgType")
  private String msgType;

  @JsonProperty("Content")
  private String content;

  public WxResponse() {
    this.code = 200;
    this.msgType = "text";
  }
}
