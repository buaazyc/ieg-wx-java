package com.tencent.wxcloudrun.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyichuan
 * @date 2025/7/15
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PickOneManager {

    /** 缓存1month */
    private final Cache<String, String> cache =
            Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();

    public String getOne(WxRequest req, Map<String, IegUserDO> userMap) {
        if (Integer.parseInt(TimeUtil.getNowDate())  >= 20250813) {
            return "活动已结束";
        }
        // 频控逻辑
        String cacheKey = req.getFromUserName() + "-" + java.time.LocalDate.now() + req.getContent();
        log.info("cacheKey = {}", cacheKey);
        if (cache.getIfPresent(cacheKey) != null) {
            return "您今天已经参与过了，请明天再来！先好好看一下TA的书和问题吧";
        }
        cache.put(cacheKey, "true");

        IegUserDO target = null;
        List<IegUserDO> userList = new ArrayList<>(userMap.values());
        Collections.shuffle(userList);
        for(IegUserDO iegUserDO : userList) {
            if (iegUserDO.getEmail().isEmpty()) {
                continue;
            }
            if (iegUserDO.getBookList().isEmpty() && iegUserDO.getQueryList().isEmpty()) {
                continue;
            }
            if (isEqual(iegUserDO, req.getContent())) {
                target = iegUserDO;
                log.info("target = {}", target);
                break;
            }
        }
        if (target == null) {
            return "未找到匹配的用户";
        }
        return target.printBox();
    }

    private boolean isEqual(IegUserDO iegUserDO, String content) {
        return "独立男生".equals(iegUserDO.getGender()) && "七夕男".equals(content)
                || "独立女生".equals(iegUserDO.getGender()) && "七夕女".equals(content);
    }
}
