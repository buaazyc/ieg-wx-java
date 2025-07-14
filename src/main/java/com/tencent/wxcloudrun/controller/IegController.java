package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.dao.mapper.IegUserMapper;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.entity.IegEntity;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.provider.WxResponse;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * @author zhangyichuan
 * @date 2025/5/22
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/ieg")
public class IegController {

  private final EmailService emailService;

  private final IegUserMapper iegUserMapper;

  /** 初始化 */
  @PostConstruct
  public void runOnceOnStartup() {
    List<IegUserDO> iegUserList = iegUserMapper.getIegUserList();
    iegUserList.forEach(
        iegUserDO -> {
          USER_EMAIL_MAP.put(iegUserDO.getUserName(), iegUserDO);
        });
    log.info("USER_EMAIL_MAP = {}", USER_EMAIL_MAP);
  }

  /** 用户到邮箱的映射 */
  private static final Map<String, IegUserDO> USER_EMAIL_MAP =
      new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  @PostMapping("/index")
  public WxResponse index(@RequestHeader Map<String, String> headers, @RequestBody WxRequest req) {
    WxResponse rsp = new WxResponse();
    if (req == null || req.getContent() == null) {
      return rsp;
    }
    MDC.put("traceid", req.getMsgId());
    rsp.setToUserName(req.getFromUserName());
    rsp.setFromUserName(req.getToUserName());
    rsp.setCreateTime(req.getCreateTime());
    log.info("get req={}", req);

    // 从req中解析出收件人
    IegEntity iegEntity = new IegEntity(req.getContent());
    if (!iegEntity.isOk()) {
      log.error("parse err: {}", req.getContent());
      rsp.setContent("格式错误，请注意使用中文逗号分隔笔名和留言。\n" + helper());
      return rsp;
    }
    log.info("parse success iegEntity = {}", iegEntity);

    IegUserDO iegUserDO = USER_EMAIL_MAP.get(iegEntity.getReceiver());
    if (iegUserDO == null || iegUserDO.getEmail() == null) {
      log.error("getReceiver not in map : {}", iegEntity.getReceiver());
      rsp.setContent("收件人不存在，请确保笔名和格式正确。\n" + helper());
      return rsp;
    }
    log.info("iegUserDO = {}", iegUserDO);

    // 发送邮件
    try {
      emailService.sendEmailForIeg(iegUserDO.getEmail(), iegEntity.getContent());
    } catch (Exception e) {
      log.error("sendEmailForIeg error", e);
      rsp.setContent("邮件发送失败，请重试。多次失败时，请在微信活动群中联系管理员。");
      return rsp;
    }

    // 回复消息发送成功
    rsp.setContent("已成功将留言发送给【" + iegEntity.getReceiver() + "】的邮箱");
    log.info("send success = {}", rsp);
    return rsp;
  }

  private String helper() {
    return "输入格式为：收件人笔名，留言内容。\n"
        + "例如：张三，你书单中的《三体》我也很喜欢。\n"
        + "注意：\n"
        + "1. 收件人笔名确保与公布的笔名完全一致；\n"
        + "2. 请使用“中文逗号”分隔笔名和留言；\n"
        + "3. 内容请使用纯文本，不要包含表情；\n"
        + "4. 邮件发送人为协会统一邮箱；\n";
  }
}
