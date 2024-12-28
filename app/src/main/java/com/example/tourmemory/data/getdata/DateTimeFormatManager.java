package com.example.tourmemory.data.getdata;

import android.content.Context;

import com.example.tourmemory.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatManager {
    public static String[] getCurrentDateTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String date = currentDateTime.format(dateFormatter);
        String time = currentDateTime.format(timeFormatter);
        return new String[]{date, time};
    }

    public static String formatDate(Context context, String date, boolean lineWrap){
        /* 设置日期格式
        输入日期格式: yyyy-MM-dd
        返回格式: yyyy年\n-MM月-dd日
        lineWrap: 年份后是否换行
         */
        String[] currentDateTime = getCurrentDateTime();
        int currentYear = Integer.parseInt(currentDateTime[0].substring(0,4));
        int recordYear = Integer.parseInt(date.substring(0,4));
        String monthAndDay = date.substring(5,7) + context.getString(R.string.month) +
                                date.substring(8,10) + context.getString(R.string.day);
        String dateFormat = date.substring(0,4) + context.getString(R.string.year);

        if(currentYear == recordYear){
            return monthAndDay;
        }
        if(lineWrap){
            return dateFormat + "\n" + monthAndDay;
        }
        return dateFormat + monthAndDay;
    }
}
