package com.tencent.wxcloudrun.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhangyichuan
 */
@Data
public class WxRequest {
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

  @JsonProperty("MsgId")
  private String msgId;
}
