package com.tencent.wxcloudrun.manager;

import com.tencent.wxcloudrun.dao.dataobject.AccessDO;
import com.tencent.wxcloudrun.dao.dataobject.CommunicationDO;
import com.tencent.wxcloudrun.dao.mapper.CommunicationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangyichuan
 * @date 2025/7/22
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CommunicationManager {

    private final CommunicationMapper communicationMapper;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * 异步插入沟通记录
     */
    public void insertCommunication(String scene, String fromName, String toName) {
        // 异步执行
        executor.submit(
                () -> {
                    CommunicationDO communicationDO = new CommunicationDO(scene, fromName, toName);
                    communicationMapper.insertCommunication(communicationDO);
                    log.info("insertCommunication: {}", communicationDO);
                });
    }
}
