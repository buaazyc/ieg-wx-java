package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.dao.mapper.IegUserMapper;
import com.tencent.wxcloudrun.domain.constant.CmdEnum;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.provider.WxResponse;

import java.util.*;

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

  private final IegUserMapper iegUserMapper;

  private final SendEmailManager sendEmailManager;

  private final UserInfoManager userInfoManager;

  private final PickOneManager pickOneManager;

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

    String res;
    CmdEnum cmdEnum = CmdEnum.getCmdEnum(req.getContent());
    log.info("cmdEnum = {}", cmdEnum);
    switch (cmdEnum) {
      case PICK_ONE:
        res = pickOneManager.getOne(req, USER_EMAIL_MAP);
        break;
      case SAVE_USER_INFO:
        res = userInfoManager.saveIegUser(req, USER_EMAIL_MAP);
        break;
      case DEFAULT:
      default:
        res = sendEmailManager.sendEmail(req, USER_EMAIL_MAP);
        break;
    }
    rsp.setContent(res);
    return rsp;
  }
}