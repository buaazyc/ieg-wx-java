package com.tencent.wxcloudrun.domain.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyichuan
 * @date 2025/5/21
 */
public class Constants {

  public static boolean isAdmin(String fromUserName) {
    Map<String, String> adminList = new HashMap<>();
    adminList.put("oOizA7cJJZDwqLBZ4a77gwhphe6I", "文杰");
    adminList.put("oOizA7Q_VouFHoyphLoaS4rCYtJI", "艺川");
    adminList.put("oOizA7VK4kxtxDAbzDSwHE6M6DTs", "妙咔");
    adminList.put("oOizA7fJCLfSRHhoEfx9y945zVj8", "林满");
    return adminList.containsKey(fromUserName);
  }

  public static String sendMailHelper() {
    return "输入格式为：收件人笔名，留言内容。\n"
            + "例如：文杰，你书单中的《三体》我也很喜欢。\n"
            + "注意：\n"
            + "1. 收件人笔名确保与公布的笔名完全一致；\n"
            + "2. 请使用【中文逗号】分隔笔名和留言；\n"
            + "3. 内容请使用纯文本，不要包含表情；\n"
            + "4. 邮件发送人为协会统一邮箱；\n"
            + "5. 若非特意在留言中说明，收件人不知道发件人是谁。";
  }

  public static String saveIegUserHelper() {
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
}
