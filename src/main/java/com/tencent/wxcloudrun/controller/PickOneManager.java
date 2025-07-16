package com.tencent.wxcloudrun.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.provider.WxRequest;
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
        // 频控逻辑
        String cacheKey = req.getFromUserName() + "-" + java.time.LocalDate.now();
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
        return target != null ? target.printBox() : "";
    }

    private boolean isEqual(IegUserDO iegUserDO, String content) {
        return "独立男生".equals(iegUserDO.getGender()) && "七夕男".equals(content)
                || "独立女生".equals(iegUserDO.getGender()) && "七夕女".equals(content);
    }
}
