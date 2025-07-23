package com.tencent.wxcloudrun.manager;

import com.tencent.wxcloudrun.dao.dataobject.UserDO;
import com.tencent.wxcloudrun.dao.mapper.UserMapper;
import com.tencent.wxcloudrun.domain.constant.CmdEnum;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.provider.WxRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyichuan
 * @date 2025/7/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManager {
    private final UserMapper userMapper;

    private static final Map<String, UserDO> USER_PEN_NAME_MAP = new HashMap<>();

    private static final Map<String, UserDO> USER_WX_OPEN_ID_MAP = new HashMap<>();

    public String register(WxRequest req) {
        if (req.getContent() == null || req.getContent().isEmpty()) {
            return "输入为空";
        }
        String[] parts = req.getContent().split("，", 2);
        if (parts.length != 2 || !parts[0].equals(CmdEnum.REGISTER.getCmd())) {
            return "格式错误\n" + Constants.registerHelper();
        }
        if (getUserByWxOpenId(req.getFromUserName()) != null) {
            return "你的微信已经注册笔名【"+ getUserByWxOpenId(req.getFromUserName()).getPenName()+"】，无法重复注册";
        }
        if (parts[1].length() < 2 || parts[1].length() > 10) {
            return "笔名长度不合法，请输入2-10个字符";
        }
        if (getUserByPenName(parts[1]) != null) {
            return "该笔名已存在，请换一个笔名";
        }
        UserDO userDO = new UserDO(req.getFromUserName(), parts[1]);
        insertUser(userDO);
        return "注册成功："+ userDO.getPenName();
    }

    public void insertUser(UserDO userDO) {
        userMapper.insertUser(userDO);
        USER_PEN_NAME_MAP.put(userDO.getPenName(), userDO);
        USER_WX_OPEN_ID_MAP.put(userDO.getWxOpenId(), userDO);
    }

    public UserDO getUserByPenName(String penName) {
        return USER_PEN_NAME_MAP.get(penName);
    }

    public UserDO getUserByWxOpenId(String wxOpenId) {
        return USER_WX_OPEN_ID_MAP.get(wxOpenId);
    }

    public void update(){
        List<UserDO> userList = userMapper.getUserList();
        if (userList == null) {
            return;
        }
        userList.forEach(
                userDO -> {
                    USER_PEN_NAME_MAP.put(userDO.getPenName(), userDO);
                    USER_WX_OPEN_ID_MAP.put(userDO.getWxOpenId(), userDO);
                });
    }

}
