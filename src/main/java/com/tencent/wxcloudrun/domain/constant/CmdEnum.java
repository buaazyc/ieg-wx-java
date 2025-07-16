package com.tencent.wxcloudrun.domain.constant;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 场景枚举值
 * @author zhangyichuan
 * @date 2025/7/15
 */
@Getter
public enum CmdEnum {
    /** 保存用户信息 */
    SAVE_USER_INFO("保存用户信息", "保存用户信息"),
    /** 七夕 */
    PICK_ONE("七夕", "七夕"),

    /** 默认 */
    DEFAULT("", "发送邮件"),
    ;

    private final String cmd;
    private final String desc;

    CmdEnum(String cmd, String desc) {
        this.cmd = cmd;
        this.desc = desc;
    }

    public static CmdEnum getCmdEnum(String content) {
         for (CmdEnum cmdEnum : CmdEnum.values()) {
             if (content.startsWith(cmdEnum.getCmd())) {
                 return cmdEnum;
             }
        }
        return DEFAULT;
    }
}
