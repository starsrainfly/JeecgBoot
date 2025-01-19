package org.jeecg.modules.mis.utils;

import cn.hutool.core.date.DateTime;

public class DateTimeUtil {

    // 将时间字符串（HH:mm:ss格式）转换为秒数，并以5位数字输出
    public static String convertTimeToSeconds(String timeString) {
        String[] parts = timeString.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid time format. Expected HH:mm:ss");
        }

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        int totalSeconds = hours * 3600 + minutes * 60 + seconds;

        // 使用String.format确保秒数总是5位数字，不足部分用0填充
        String formattedSeconds = String.format("%05d", totalSeconds);
        return formattedSeconds;
    }

    public static String convertTimeToSeconds(DateTime time) {
        int hours = time.hour(true);
        int minutes = time.minute();
        int seconds = time.second();

        int totalSeconds = hours * 3600 + minutes * 60 + seconds;

        String formattedSeconds = String.format("%05d", totalSeconds);

        return  formattedSeconds;

    }

    // 将秒数转换为时间字符串（HH:mm:ss格式），并确保每部分都是两位数
    public static String convertSecondsToTimeString(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        // 使用String.format来确保每部分都是两位数，不足部分用0填充
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
        return timeString;
    }
}
