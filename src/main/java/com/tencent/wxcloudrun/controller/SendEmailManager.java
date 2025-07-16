package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.domain.entity.IegEntity;
import com.tencent.wxcloudrun.provider.WxRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author zhangyichuan
 * @date 2025/7/15
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SendEmailManager {

    private final EmailService emailService;

    public String sendEmail(WxRequest req, Map<String, IegUserDO> userMap) {
        // 从req中解析出收件人
        IegEntity iegEntity = new IegEntity(req.getContent());
        if (!iegEntity.isOk()) {
            log.error("parse err: {}", req.getContent());
            return "格式错误，请注意使用中文逗号分隔笔名和留言。\n" + helper();
        }
        log.info("parse success iegEntity = {}", iegEntity);

        IegUserDO iegUserDO = userMap.get(iegEntity.getReceiver());
        if (iegUserDO == null) {
            log.error("getReceiver not in map : {}", iegEntity.getReceiver());
            return "收件人不存在，请确保笔名和格式正确。\n" + helper();
        }
        if (iegUserDO.getEmail().isEmpty()) {
            log.error("getReceiver email is empty : {}", iegEntity.getReceiver());
            return "收件人对应邮箱未填写，请告知管理员。";
        }
        log.info("iegUserDO = {}", iegUserDO);

        // 发送邮件
        try {
            emailService.sendEmailForIeg(iegUserDO.getEmail(), iegEntity.getContent());
        } catch (Exception e) {
            log.error("sendEmailForIeg error", e);
            return "邮件发送失败，请重试。多次失败时，请在微信活动群中联系管理员。";
        }

        // 回复消息发送成功
        log.info("send success = {}", iegEntity);
        return "已成功将留言发送给【" + iegEntity.getReceiver() + "】的邮箱";
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
