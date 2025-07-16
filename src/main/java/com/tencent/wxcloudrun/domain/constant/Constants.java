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
}
