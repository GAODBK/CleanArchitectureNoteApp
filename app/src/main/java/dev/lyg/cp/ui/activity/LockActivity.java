package dev.lyg.cp.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import dev.lyg.cp.R;
import dev.lyg.cp.lock.*;

public class LockActivity extends Activity {

    private FlipLayout bit_hour;
    private FlipLayout bit_minute;
    private Calendar oldNumber = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_lock);

        this.bit_minute = (FlipLayout) findViewById(R.id.bit_flip_2);
        this.bit_hour = (FlipLayout) findViewById(R.id.bit_flip_1);

        bit_hour.flip(oldNumber.get(Calendar.HOUR_OF_DAY), 24, TimeTAG.hour);
        bit_minute.flip(oldNumber.get(Calendar.MINUTE), 60, TimeTAG.min);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                start();
            }
        }, 60000, 60000);
    }

 public void start() {
    Calendar now = Calendar.getInstance();
    int nhour = now.get(Calendar.HOUR_OF_DAY);
    int nminute = now.get(Calendar.MINUTE);

    int ohour = oldNumber.get(Calendar.HOUR_OF_DAY);
    int ominute = oldNumber.get(Calendar.MINUTE);

    oldNumber = now;

    // 计算小时差，考虑跨天的情况
    int hour = nhour - ohour;
    if (hour < 0) {
        hour += 24;  // 处理跨天的情况
    }

    // 计算分钟差，考虑跨小时的情况
    int minute = nminute - ominute;
    if (minute < 0) {
        minute += 60;  // 处理跨小时的情况
        hour--;  // 需要调整小时
    }

    // 根据时间差更新数字翻转
    if (hour >= 1 || hour == -23) {
        bit_hour.smoothFlip(1, 24, TimeTAG.hour, false);
    }

    if (minute >= 1 || minute == -59) {
        bit_minute.smoothFlip(1, 60, TimeTAG.min, false);
    }
}
}
