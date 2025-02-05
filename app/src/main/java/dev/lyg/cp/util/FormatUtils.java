package dev.lyg.cp.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {

    // 1. 日期格式: "yyyy-MM-dd" 转换为 "yyyyMMdd"
    public static String convertDateToSimpleFormat(String date) {
        if (date == null || date.isEmpty()) {
            Log.e("DateFormatUtils", "输入日期为空");
            return "";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            Log.e("DateFormatUtils", "日期格式转换失败: " + e.getMessage());
            return "";
        }
    }

    // 2. 日期格式: "yyyyMMdd" 转换为 "yyyy-MM-dd"
    public static String convertSimpleDateToDateFormat(String date) {
        if (date == null || date.isEmpty()) {
            Log.e("DateFormatUtils", "输入日期为空");
            return "";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            Log.e("DateFormatUtils", "日期格式转换失败: " + e.getMessage());
            return "";
        }
    }

    public static String extractTicketGateInfo(String result) {
        // 正则表达式匹配检票口信息，提取15A、15B
        String regex = "<span style='color:red; font-weight:600;'>检票口(.*?)</span>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result);

        StringBuilder ticketGateInfo = new StringBuilder();
        // 第一个匹配的值（忽略）
        while (matcher.find()) {
            ticketGateInfo.append(matcher.group(1)).append(" ");
        }
        return ticketGateInfo.length() > 0 ? ticketGateInfo.toString() : "--";    }
}
