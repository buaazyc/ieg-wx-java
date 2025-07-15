package com.tencent.wxcloudrun.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.dao.mapper.IegUserMapper;
import com.tencent.wxcloudrun.domain.entity.IegEntity;
import com.tencent.wxcloudrun.domain.entity.SaveIegEntity;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.provider.WxResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

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

  /** 缓存1month */
  private final Cache<String, String> cache =
          Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();

  /** 用户到邮箱的映射 */
  private static final Map<String, IegUserDO> USER_EMAIL_MAP =
          new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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

    // 特殊命令：添加用户
    String res = saveIegUser(req);
    if (!res.isEmpty()) {
      rsp.setContent(res);
      return rsp;
    }

    // 特殊命令：盲盒获取
    res = getOne(req);
    if (!res.isEmpty()) {
      rsp.setContent(res);
      return rsp;
    }

    // 从req中解析出收件人
    IegEntity iegEntity = new IegEntity(req.getContent());
    if (!iegEntity.isOk()) {
      log.error("parse err: {}", req.getContent());
      rsp.setContent("格式错误，请注意使用中文逗号分隔笔名和留言。\n" + helper());
      return rsp;
    }
    log.info("parse success iegEntity = {}", iegEntity);

    IegUserDO iegUserDO = USER_EMAIL_MAP.get(iegEntity.getReceiver());
    if (iegUserDO == null) {
      log.error("getReceiver not in map : {}", iegEntity.getReceiver());
      rsp.setContent("收件人不存在，请确保笔名和格式正确。\n" + helper());
      return rsp;
    }
    if (iegUserDO.getEmail().isEmpty()) {
      log.error("getReceiver email is empty : {}", iegEntity.getReceiver());
      rsp.setContent("收件人对应邮箱未填写，请告知管理员。");
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

  private String saveIegUser(WxRequest req) {
    // 前缀是“保存用户信息\n”
    if (!req.getContent().startsWith("保存用户信息")) {
      return "";
    }
    // 必须是管理员
    if (!Objects.equals(req.getFromUserName(), "oOizA7Q_VouFHoyphLoaS4rCYtJI") && !Objects.equals(req.getFromUserName(), "oOizA7VK4kxtxDAbzDSwHE6M6DTs")) {
      return "";
    }

    // 解析出请求体
    SaveIegEntity saveIegEntity = new SaveIegEntity(req.getContent());
    log.info("saveIegEntity = {}", saveIegEntity);
    if (!saveIegEntity.isOk()) {
      return "格式错误" + saveIegUserHelper();
    }

    IegUserDO iegUserDO = new IegUserDO(
            saveIegEntity.getUserName(),
            saveIegEntity.getGender(),
            saveIegEntity.getEmail(),
            saveIegEntity.getBookList(),
            saveIegEntity.getQueryList()
    );
    iegUserMapper.saveIegUser(iegUserDO);
    USER_EMAIL_MAP.put(iegUserDO.getUserName(), iegUserDO);
    return "保存用户信息成功\n" + iegUserDO.print();
  }

  private String saveIegUserHelper() {
    return "输入格式为：保存用户信息 + 笔名 + 性别 + 邮箱 + 书单 + 问题。\n"
        + "注意注意：每一个元素使用换行分隔\n"
        + "例如："
        + "保存用户信息\n"
        + "张三\n"
        + "独立男生\n"
        + "123@qq.com\n"
        + "《三体》\n"
        + "你喜欢《三体》吗？";
  }

  private String getOne(WxRequest req) {
    if (!req.getContent().equals("七夕男") && !req.getContent().equals("七夕女")) {
      return "";
    }

    // 频控逻辑
    String cacheKey = req.getFromUserName() + "-" + java.time.LocalDate.now();
    if (cache.getIfPresent(cacheKey) != null) {
      return "您今天已经参与过了，请明天再来！先好好看一下TA的书和问题吧";
    }
    cache.put(cacheKey, "true");

    IegUserDO target = null;
    List<IegUserDO> userList = new ArrayList<>(USER_EMAIL_MAP.values());
    Collections.shuffle(userList);
    for(IegUserDO iegUserDO : userList) {
      if (iegUserDO.getEmail().isEmpty()) {
        continue;
      }
      if (iegUserDO.getBookList().isEmpty() && iegUserDO.getQueryList().isEmpty()) {
        continue;
      }
      if (iegUserDO.getGender().equals("独立男生") && req.getContent().equals("七夕男")
      || iegUserDO.getGender().equals("独立女生") && req.getContent().equals("七夕女")) {
        target = iegUserDO;
        log.info("target = {}", target);
        break;
      }
    }
    return target != null ? target.printBox() : "";
  }
}