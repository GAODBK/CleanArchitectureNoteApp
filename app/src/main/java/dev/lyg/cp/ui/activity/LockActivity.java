package dev.lyg.cp.ui.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import dev.lyg.cp.R;
import dev.lyg.cp.lock.SlidingFinishLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class LockActivity extends AppCompatActivity implements SlidingFinishLayout.OnSlidingFinishListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_lock);
        initView();
    }

    private void initView() {
        TextView tvLockTime = findViewById(R.id.lock_time);
        TextView tvLockDate = findViewById(R.id.lock_date);
        SlidingFinishLayout vLockRoot = findViewById(R.id.lock_root);
        vLockRoot.setOnSlidingFinishListener(this);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm-M月dd日 E", Locale.CHINESE);
        String[] date = simpleDateFormat.format(new Date()).split("-");
        tvLockTime.setText(date[0]);
        tvLockDate.setText(date[1]);
    }

    @Override
    public void onSlidingFinish() {
        finish();
    }
}