package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.domain.entity.IegEntity;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.provider.WxResponse;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

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

  /** 用户到邮箱的映射 */
  private static final Map<String, String> USER_EMAIL_MAP =
      new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  static {
    // 测试
    USER_EMAIL_MAP.put("艺川", "443739352@qq.com");
    USER_EMAIL_MAP.put("Echuan", "443739352@qq.com");

    // 正式
    USER_EMAIL_MAP.put("君不见", "mazhiyuan3000@foxmail.com");
    USER_EMAIL_MAP.put("Helen", "caoyuting1022@163.com");
    USER_EMAIL_MAP.put("jessie", "285586775@qq.com");
    USER_EMAIL_MAP.put("洋葱", "youdiyu@tencent.com");
    USER_EMAIL_MAP.put("Chanelle", "2859598737@qq.com");
    USER_EMAIL_MAP.put("May", "ustb_meimei@126.com");
    USER_EMAIL_MAP.put("宋清尘", "xwq20141108@163.com");
    USER_EMAIL_MAP.put("心玥", "1019225835@qq.com");
    USER_EMAIL_MAP.put("云岫", "563078963@qq.com");
    USER_EMAIL_MAP.put("迪迦", "15816706751@163.com");
    USER_EMAIL_MAP.put("SooHyunPark", "13717821278@163.com");
    USER_EMAIL_MAP.put("Air", "2285658805@qq.com");
    USER_EMAIL_MAP.put("Vivi", "1347858825@qq.com");
    USER_EMAIL_MAP.put("lenny", "1741570617@qq.com");
    USER_EMAIL_MAP.put("rachel", "rachelzhengyx@163.com");
    USER_EMAIL_MAP.put("latent", "1120345756@qq.com");
    USER_EMAIL_MAP.put("冰美式", "guoqi45@crcgas.com");
    USER_EMAIL_MAP.put("楠登", "915534415@qq.com");
    USER_EMAIL_MAP.put("道蕴", "625501049@qq.com");
    USER_EMAIL_MAP.put("葱头", "wing.88@163.com");
    USER_EMAIL_MAP.put("小俞", "yuwenyi611@qq.com");
    USER_EMAIL_MAP.put("木木", "1104100336@qq.com");
    USER_EMAIL_MAP.put("momo", "joliemomo@163.com");
    USER_EMAIL_MAP.put("星舾", "2373187028@qq.com");
    USER_EMAIL_MAP.put("Tina", "974226244@qq.com");
    USER_EMAIL_MAP.put("小熊珍妮", "870951125@qq.com");
    USER_EMAIL_MAP.put("陆晓风", "1225240807@qq.com");
    USER_EMAIL_MAP.put("水滴", "31397797@qq.com");
    USER_EMAIL_MAP.put("海露", "chentong529@126.com");
    USER_EMAIL_MAP.put("haluo123", "1072116453@qq.com");
    USER_EMAIL_MAP.put("derek", "hsx547@live.com");
    USER_EMAIL_MAP.put("深海鱼", "845952166@qq.com");
    USER_EMAIL_MAP.put("mia", "1720653783@qq.com");
    USER_EMAIL_MAP.put("云乞", "1316892168@qq.com");
    USER_EMAIL_MAP.put("123", "2414550385@qq.com");
    USER_EMAIL_MAP.put("猫鹅", "1045703639@qq.com");
    USER_EMAIL_MAP.put("买买提烤肉王", "806426248@qq.com");
    USER_EMAIL_MAP.put("阿信", "mayyyang@tencent.com");
    USER_EMAIL_MAP.put("西弗绪斯", "1095742619@qq.com");
    USER_EMAIL_MAP.put("zen", "931437730@qq.com");
    USER_EMAIL_MAP.put("Min", "516888115@qq.com");
    USER_EMAIL_MAP.put("卷卷", "2192621929@qq.com");
    USER_EMAIL_MAP.put("忱梦眠", "armondwang@tencent.com");
    USER_EMAIL_MAP.put("刘熙和", "ishmaellzq@gmail.com");
    USER_EMAIL_MAP.put("今天吃肉了吗", "fengzhiwing@outlook.com");
    USER_EMAIL_MAP.put("番茄", "2754192847@qq.com");
    USER_EMAIL_MAP.put("黑眼圈不能熬夜", "1302940291@qq.com");
  }

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

    String receiverEmail = USER_EMAIL_MAP.get(iegEntity.getReceiver());
    if (receiverEmail == null) {
      log.error("getReceiver not in map : {}", iegEntity.getReceiver());
      rsp.setContent("收件人不存在，请确保笔名和格式正确。\n" + helper());
      return rsp;
    }
    log.info("receiverEmail = {}", receiverEmail);

    // 发送邮件
    try {
      emailService.sendEmailForIeg(receiverEmail, iegEntity.getContent());
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
