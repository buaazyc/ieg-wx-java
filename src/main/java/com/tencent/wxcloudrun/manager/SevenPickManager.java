package com.tencent.wxcloudrun.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.dao.dataobject.SevenUserDO;
import com.tencent.wxcloudrun.provider.WxRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
public class SevenPickManager {

    /** 缓存1month */
    private final Cache<String, String> cache =
            Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();

    public String getOne(WxRequest req, Map<String, SevenUserDO> userMap) {
        // 频控逻辑
        String cacheKey = req.getFromUserName() + "-" + java.time.LocalDate.now() + req.getContent();
        log.info("cacheKey = {}", cacheKey);
        if (cache.getIfPresent(cacheKey) != null) {
            return "您今天已经参与过了，请明天再来！先好好看一下TA的书和问题吧";
        }
        cache.put(cacheKey, "true");

        SevenUserDO target = null;
        List<SevenUserDO> userList = new ArrayList<>(userMap.values());
        Collections.shuffle(userList);
        for(SevenUserDO sevenUserDO : userList) {
            if (sevenUserDO.getEmail().isEmpty()) {
                continue;
            }
            if (sevenUserDO.getBookList().isEmpty() && sevenUserDO.getQueryList().isEmpty()) {
                continue;
            }
            if (isEqual(sevenUserDO, req.getContent())) {
                target = sevenUserDO;
                log.info("target = {}", target);
                break;
            }
        }
        if (target == null) {
            return "未找到匹配的用户";
        }
        return target.printBox();
    }

    private boolean isEqual(SevenUserDO sevenUserDO, String content) {
        return "独立男生".equals(sevenUserDO.getGender()) && "七夕男".equals(content)
                || "独立女生".equals(sevenUserDO.getGender()) && "七夕女".equals(content);
    }
}
