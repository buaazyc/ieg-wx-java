package com.tencent.wxcloudrun.dao.dataobject;

import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.provider.WxResponse;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 * 访问记录实体类
 *
 * @author zhangyichuan
 */
@Data
public class AccessDO implements Serializable {

  /** 消息ID */
  private String msgId;

  /** 消息类型 */
  private String msgType;

  /** 发送者用户名 */
  private String fromUserName;

  /** 接收者用户名 */
  private String toUserName;

  /** 创建时间 */
  private Integer createTime;

  /** 请求内容 */
  private String req;

  /** 响应内容 */
  private String rsp;

  private String accessType;

  private String accessKey;

  public AccessDO(
      Map<String, String> headers,
      WxRequest req,
      WxResponse rsp,
      String access_type,
      String access_key) {
    this.msgId = req.getMsgId();
    this.msgType = req.getMsgType();
    this.fromUserName = req.getFromUserName();
    this.toUserName = req.getToUserName();
    this.createTime = req.getCreateTime();
    this.req = req.getContent();
    this.rsp = rsp.getContent();
    this.accessType = access_type;
    this.accessKey = access_key;
  }
}
