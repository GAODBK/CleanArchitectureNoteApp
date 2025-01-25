package dev.lyg.cp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.WindowManager;
import android.widget.Toast;

import dev.lyg.cp.lock.NotificationUtil;
import dev.lyg.cp.lock.PlayService;
import dev.lyg.cp.unit_test.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Bottom";
    private BottomNavigationView bottomNavigationView;

    private final Fragment[] fragments = new Fragment[4];
    private int currentFragmentIndex = -1;

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 使状态栏图标为白色，旗帜布局无限制
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, PlayService.class);
        startService(intent);

        // 检查并请求通知权限（针对 Android 13 及以上版本）
        startNotification();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initializeFragments();

        // 设置默认片段
        switchFragment(0);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                switchFragment(0);
            } else if (itemId == R.id.search) {
                switchFragment(1);
            } else if (itemId == R.id.sticky) {
                switchFragment(2);
            } else if (itemId == R.id.me) {
                switchFragment(3);
            }
            return true;
        });
    }

    private void initializeFragments() {
        fragments[0] = new HomeFragment();
        fragments[1] = new SearchFragment();
        fragments[2] = new LeaveFragment();
        fragments[3] = new MeFragment();
    }

    // 1 15 10
    private void switchFragment(int newIndex) {
        if (newIndex == currentFragmentIndex) {
            return; // 避免冗余操作
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Hide the current fragment if exists
        if (currentFragmentIndex >= 0) {
            transaction.hide(fragments[currentFragmentIndex]);
        }

        // Show or add the new fragment
        Fragment newFragment = fragments[newIndex];
        if (!newFragment.isAdded()) {
            transaction.add(R.id.content, newFragment);
        } else {
            transaction.show(newFragment);
            // 如果是 HomeFragment，触发数据刷新
            if (newFragment instanceof HomeFragment) {
                ((HomeFragment) newFragment).loadTicketsFromDatabase();
            }
        }

        transaction.commit();
        currentFragmentIndex = newIndex;
    }

    private void startNotification() {
        NotificationUtil notificationUtil = new NotificationUtil(this);
        notificationUtil.showNotification();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startNotification();
            } else {
                // 权限被拒绝时可以提示用户
                Toast.makeText(
                        this,
                        "请允许通知权限",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}
