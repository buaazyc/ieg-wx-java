package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.dao.mapper.IegUserMapper;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.entity.SaveIegEntity;
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
@Service
@RequiredArgsConstructor
public class UserInfoManager {

    private final IegUserMapper iegUserMapper;

    public String saveIegUser(WxRequest req, Map<String, IegUserDO> userMap) {
        // 必须是管理员
        if (!Constants.isAdmin(req.getFromUserName())) {
            return "";
        }

        // 解析出请求体
        SaveIegEntity saveIegEntity = new SaveIegEntity(req.getContent());
        log.info("saveIegEntity = {}", saveIegEntity);
        if (!saveIegEntity.isOk()) {
            return "格式错误" + saveIegUserHelper();
        }

        IegUserDO iegUserDO = new IegUserDO(
                saveIegEntity.getUserName(),
                saveIegEntity.getGender(),
                saveIegEntity.getEmail(),
                saveIegEntity.getBookList(),
                saveIegEntity.getQueryList()
        );
        iegUserMapper.saveIegUser(iegUserDO);
        userMap.put(iegUserDO.getUserName(), iegUserDO);
        return "保存用户信息成功\n" + iegUserDO.print();
    }

    private String saveIegUserHelper() {
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
