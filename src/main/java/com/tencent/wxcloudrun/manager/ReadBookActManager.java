package com.tencent.wxcloudrun.manager;

import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.dao.dataobject.ReadBookActDO;
import com.tencent.wxcloudrun.dao.dataobject.ReadBookUserDO;
import com.tencent.wxcloudrun.dao.mapper.ReadBookActMapper;
import com.tencent.wxcloudrun.dao.mapper.ReadBookUserMapper;
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

        // 校验
        for (ReadBookUserDO readBookUserDO : USER_LIST) {
            // 同一个微信号，笔名需要相同
            if (readBookUserDO.getWxId().equals(req.getFromUserName()) && !readBookUserDO.getUserName().equals(clockEntity.getUserName())) {
                return "你的微信号已经绑定了笔名【"+ readBookUserDO.getUserName() + "】\n请使用之前的笔名打卡";
            }
            // 同一个笔名，不能被不同的微信号使用
            if (!readBookUserDO.getWxId().equals(req.getFromUserName()) && readBookUserDO.getUserName().equals(clockEntity.getUserName())) {
                return "笔名【"+ readBookUserDO.getUserName() + "】已经被其他书友使用，请更换笔名";
            }
            // 如果笔名相同，书名相同，且日期相同，则返回错误
            if (readBookUserDO.getUserName().equals(clockEntity.getUserName()) &&
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
                clockEntity.getUserName(),
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
            if (readBookUserDO.getUserName().equals(clockEntity.getUserName())) {
                continue;
            }
            thinking = readBookUserDO;
        }
        if (thinking == null) {
            return "当前没有用户对" + CUR_READ_BOOK_ACT_DO.getBookName() + "有想法";
        }
        return "笔名："+thinking.getUserName() + "\n\n" + thinking.getThinking();
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
        USER_LIST.addAll(readBookUserList);
        log.info("USER_LIST = {}", USER_LIST);
    }
}
