package com.tencent.wxcloudrun.dao.dataobject;

import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/7/22
 */
@Data
public class CommunicationDO {
    private String scene;

    private String fromName;

    private String toName;

    public CommunicationDO(String scene, String fromName, String toName) {
        this.scene = scene;
        this.fromName = fromName;
        this.toName = toName;
    }
}
