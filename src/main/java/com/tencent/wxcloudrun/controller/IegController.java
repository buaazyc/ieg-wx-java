package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.dao.dataobject.SevenUserDO;
import com.tencent.wxcloudrun.dao.mapper.SevenUserMapper;
import com.tencent.wxcloudrun.domain.constant.CmdEnum;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.entity.SaveIegEntity;
import com.tencent.wxcloudrun.manager.SevenPickManager;
import com.tencent.wxcloudrun.manager.ReadBookActManager;
import com.tencent.wxcloudrun.manager.SendEmailManager;
import com.tencent.wxcloudrun.manager.UserManager;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.provider.WxResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangyichuan
 * @date 2025/5/22
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/ieg")
@EnableScheduling
public class IegController {

  private final SevenUserMapper sevenUserMapper;

  private final SendEmailManager sendEmailManager;

  private final SevenPickManager sevenPickManager;

  private final ReadBookActManager readBookActManager;

  private final UserManager userManager;

  /** 用户到邮箱的映射 */
  private static final Map<String, SevenUserDO> SEVEN_USER_EMAIL_MAP =
      new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  /** 初始化 */
  @PostConstruct
  public void runOnceOnStartup() {
    update();
  }

  private static final String TOKEN = "123";

  @GetMapping("/index")
  public String index(
      @RequestParam("signature") String signature,
      @RequestParam("timestamp") String timestamp,
      @RequestParam("nonce") String nonce,
      @RequestParam("echostr") String echostr) {
    // 1. 将 token、timestamp、nonce 放入数组并排序
    String[] arr = {TOKEN, timestamp, nonce};
    Arrays.sort(arr);
    // 2. 拼接成字符串并进行 SHA-1 加密
    StringBuilder sb = new StringBuilder();
    for (String str : arr) {
      sb.append(str);
    }
    // 3. 计算sha1
    String encrypted = sha1(sb.toString());
    log.info(
        "signature={}, timestamp={}, nonce={}, echostr={}", signature, timestamp, nonce, echostr);
    if (encrypted.equals(signature)) {
      return echostr;
    }
    return "hello world";
  }

  @Scheduled(cron = "0 */10 * * * ?")
  private void update() {
    List<SevenUserDO> sevenUserList = sevenUserMapper.getSevenUserList();
    // 活动结束，定时任务关闭
//    sevenUserList.forEach(
//            sevenUserDO -> {
//          SEVEN_USER_EMAIL_MAP.put(sevenUserDO.getUserName(), sevenUserDO);
//        });
//    log.info("SEVEN_USER_EMAIL_MAP = {}", SEVEN_USER_EMAIL_MAP);
    readBookActManager.update();
    userManager.update();
  }

  @PostMapping(value = "/index", produces = "text/xml;charset=UTF-8")
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
          res = "";
//        res = sevenPickManager.getOne(req, SEVEN_USER_EMAIL_MAP);
        break;
      case SAVE_USER_INFO:
          res = "";
//        res = saveIegUser(req);
        break;
      case SAVE_ACT:
        res = readBookActManager.saveReadBookAct(req);
        break;
      case CLOCK:
        res = readBookActManager.clockIn(req);
        break;
      case REGISTER:
        res = userManager.register(req);
        break;
      case MESSAGE:
        res = "";
        // res = sendEmailManager.sendEmail(req, USER_EMAIL_MAP);
        break;
      default:
        res = "";
        break;
    }
    log.info("res = {}", res);
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

    SevenUserDO sevenUserDO =
        new SevenUserDO(
            saveIegEntity.getUserName(),
            saveIegEntity.getGender(),
            saveIegEntity.getEmail(),
            saveIegEntity.getBookList(),
            saveIegEntity.getQueryList());
    sevenUserMapper.saveSevenUser(sevenUserDO);
    SEVEN_USER_EMAIL_MAP.put(sevenUserDO.getUserName(), sevenUserDO);
    return "保存用户信息成功\n" + sevenUserDO.print();
  }

  /** 计算 SHA-1 哈希值 */
  private String sha1(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : bytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException("SHA-1 calculation error", e);
    }
  }
}
