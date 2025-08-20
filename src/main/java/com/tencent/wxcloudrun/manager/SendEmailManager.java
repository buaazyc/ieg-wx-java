package com.tencent.wxcloudrun.manager;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.dao.dataobject.SevenUserDO;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.entity.SevenMessageEntity;
import com.tencent.wxcloudrun.provider.WxRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zhangyichuan
 * @date 2025/7/15
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SendEmailManager {

    private final EmailService emailService;

    public String sendEmail(WxRequest req, Map<String, SevenUserDO> userMap) {
        // 从req中解析出收件人
        SevenMessageEntity sevenMessageEntity = new SevenMessageEntity(req.getContent());
        if (!sevenMessageEntity.isOk()) {
            log.error("parse err: {}", req.getContent());
            return "格式错误，请注意使用中文逗号分隔笔名和留言。\n" + Constants.sendMailHelper();
        }
        log.info("parse success iegEntity = {}", sevenMessageEntity);

        SevenUserDO sevenUserDO = userMap.get(sevenMessageEntity.getReceiver());
        if (sevenUserDO == null) {
            log.error("getReceiver not in map : {}", sevenMessageEntity.getReceiver());
        return "收件人【" + sevenMessageEntity.getReceiver() + "】不存在，请确保笔名和格式正确。\n" + Constants.sendMailHelper();

        }
        if (sevenUserDO.getEmail().isEmpty()) {
            log.error("getReceiver email is empty : {}", sevenMessageEntity.getReceiver());
            return "收件人【" + sevenMessageEntity.getReceiver() + "】未填写邮箱地址";
        }
        log.info("iegUserDO = {}", sevenUserDO);

        // 发送邮件
        try {
            emailService.sendEmailForIeg(sevenUserDO.getEmail(), sevenMessageEntity.getContent());
        } catch (Exception e) {
            log.error("sendEmailForIeg error", e);
            return "邮件发送失败，请尝试重试。多次失败时，请在微信活动群中联系管理员。";
        }

        // 回复消息发送成功
        log.info("send success = {}", sevenMessageEntity);
        return "已成功将留言发送给【" + sevenMessageEntity.getReceiver() + "】的邮箱";
    }
}
