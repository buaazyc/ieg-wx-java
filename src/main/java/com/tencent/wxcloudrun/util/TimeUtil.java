package com.tencent.wxcloudrun.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author zhangyichuan
 * @date 2025/7/21
 */
public class TimeUtil {

    public static String getNowDate() {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 格式化日期
        return  currentDate.format(formatter);
    }

    public static String getDate(int days) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 格式化日期
        return  currentDate.plusDays(days).format(formatter);
    }

    public static int compareDate(String date1, String date2) {
        // 直接比较字符串形式的日期
        return date1.compareTo(date2);
    }
}