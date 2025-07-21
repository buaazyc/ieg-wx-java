package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.dao.mapper.IegUserMapper;
import com.tencent.wxcloudrun.domain.constant.CmdEnum;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.entity.SaveIegEntity;
import com.tencent.wxcloudrun.manager.PickOneManager;
import com.tencent.wxcloudrun.manager.ReadBookActManager;
import com.tencent.wxcloudrun.manager.SendEmailManager;
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

  private final PickOneManager pickOneManager;

  private final ReadBookActManager readBookActManager;

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
    readBookActManager.update();
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
        res = saveIegUser(req);
        break;
      case SAVE_ACT:
          res = readBookActManager.saveReadBookAct(req);
          break;
      case CLOCK:
          res = readBookActManager.clockIn(req);
          break;
      case DEFAULT:
      default:
        res = sendEmailManager.sendEmail(req, USER_EMAIL_MAP);
        break;
    }
    rsp.setContent(res);
    return rsp;
  }

  public String saveIegUser(WxRequest req) {
    // 必须是管理员
    if (!Constants.isAdmin(req.getFromUserName())) {
      return "非管理员无法使用该功能";
    }

    // 解析出请求体
    SaveIegEntity saveIegEntity = new SaveIegEntity(req.getContent());
    log.info("saveIegEntity = {}", saveIegEntity);
    if (!saveIegEntity.isOk()) {
      return "格式错误" + Constants.saveIegUserHelper();
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
}