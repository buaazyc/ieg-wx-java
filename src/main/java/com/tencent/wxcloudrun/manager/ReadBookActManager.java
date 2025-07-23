package com.tencent.wxcloudrun.manager;

import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.dao.dataobject.ReadBookActDO;
import com.tencent.wxcloudrun.dao.dataobject.ReadBookUserDO;
import com.tencent.wxcloudrun.dao.dataobject.UserDO;
import com.tencent.wxcloudrun.dao.mapper.ReadBookActMapper;
import com.tencent.wxcloudrun.dao.mapper.ReadBookUserMapper;
import com.tencent.wxcloudrun.dao.mapper.UserMapper;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.entity.ClockEntity;
import com.tencent.wxcloudrun.domain.entity.ReadBookActEntity;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author zhangyichuan
 * @date 2025/7/21
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ReadBookActManager {
    private final ReadBookActMapper readBookActMapper;

    private final ReadBookUserMapper readBookUserMapper;

    private final UserManager userManager;

    private static ReadBookActDO CUR_READ_BOOK_ACT_DO = null;

    private static final List<ReadBookUserDO> USER_LIST = new ArrayList<>();

    public String saveReadBookAct(WxRequest req) {
        // 必须是管理员
        if (!Constants.isAdmin(req.getFromUserName())) {
            return "非管理员无法使用该功能";
        }

        ReadBookActEntity readBookActEntity = new ReadBookActEntity(req.getContent());
        if (!readBookActEntity.isOk()) {
            return "格式错误" + Constants.saveIegActHelper();
        }
        log.info("readBookActEntity = {}", readBookActEntity);

        ReadBookActDO readBookActDO = new ReadBookActDO(
                readBookActEntity.getBookName(),
                readBookActEntity.getBeginDate(),
                readBookActEntity.getEndDate()
        );
        readBookActMapper.saveReadBookAct(readBookActDO);
        update();
        return "保存活动信息成功";
    }

    public String clockIn(WxRequest req) {
        // 解析出请求体
        ClockEntity clockEntity = new ClockEntity(req.getContent());
        if (!clockEntity.isOk()) {
            return "格式错误" + Constants.clockInHelper();
        }

        // 找到当前活动
        if (CUR_READ_BOOK_ACT_DO == null) {
            return "当前没有活动";
        }

        UserDO userDO = userManager.getUserByWxOpenId(req.getFromUserName());
        if (userDO == null) {
            return "你的微信未注册笔名，请先注册\n"+ Constants.registerHelper();
        }

        // 校验
        for (ReadBookUserDO readBookUserDO : USER_LIST) {
            // 如果微信id相同，书名相同，且日期相同，则返回错误
            if (readBookUserDO.getWxId().equals(userDO.getWxOpenId()) &&
                    readBookUserDO.getBookName().equals(CUR_READ_BOOK_ACT_DO.getBookName())
                    && readBookUserDO.getDate().equals(TimeUtil.getNowDate())) {
                return "你今天已经打过卡了，请明天再来吧";
            }
        }
        if (clockEntity.getThinking().length() < 15) {
            return "你的想法太短了，请写点长一点";
        }

        // 更新db，并更新缓存
        readBookUserMapper.insertReadBookUser(new ReadBookUserDO(
                userDO.getPenName(),
                req.getFromUserName(),
                CUR_READ_BOOK_ACT_DO.getBookName(),
                clockEntity.getThinking(),
                TimeUtil.getNowDate()
        ));
        update();

        // 打乱USER_LIST的顺序
        Collections.shuffle(USER_LIST);
        // 从缓存中获取一个和当前笔名不一样的感想
        ReadBookUserDO thinking = null;
        for (ReadBookUserDO readBookUserDO : USER_LIST) {
            if (readBookUserDO.getWxId().equals(req.getFromUserName())) {
                continue;
            }
            thinking = readBookUserDO;
        }
        if (thinking == null) {
            return "当前没有用户对" + CUR_READ_BOOK_ACT_DO.getBookName() + "有想法";
        }
        return userDO.getPenName() + "，你已成功打卡活动：" + CUR_READ_BOOK_ACT_DO.getBookName() + "\n" +
                "累计打卡" + stat(req.getFromUserName()) + "天\n" +
                "\n" +
                "其他书友：" + "\n" +
                "笔名：" + thinking.getUserName() + "\n" +
                "累计打卡：" + stat(thinking.getWxId()) + "天\n" +
                "TA的想法：" + thinking.getThinking() + "\n";
    }

    public void update() {
        // 更新当前生效的活动
        CUR_READ_BOOK_ACT_DO =  readBookActMapper.getCurReadBookAct(TimeUtil.getNowDate());
        if (CUR_READ_BOOK_ACT_DO == null) {
            log.info("当前没有活动");
            return;
        }
        log.info("CUR_READ_BOOK_ACT_DO = {}", CUR_READ_BOOK_ACT_DO);
        // 查询当前活动的用户想法
        List<ReadBookUserDO> readBookUserList = readBookUserMapper.getReadBookUser(CUR_READ_BOOK_ACT_DO.getBookName());
        // 更新当前活动的用户想法
        USER_LIST.clear();
        USER_LIST.addAll(readBookUserList);
        log.info("USER_LIST = {}", USER_LIST);
    }

    public Integer stat(String userName) {
        Integer count = 0;
        for (ReadBookUserDO readBookUserDO : USER_LIST) {
            if (readBookUserDO.getWxId().equals(userName)) {
                count++;
            }
        }
        return count;
    }
}
