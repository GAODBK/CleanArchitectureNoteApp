package dev.lyg.cp;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.WindowManager;

import dev.lyg.cp.unit_test.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Bottom";
    private BottomNavigationView bottomNavigationView;

    private final Fragment[] fragments = new Fragment[4];
    private int currentFragmentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initializeFragments();

        // Set default fragment
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
            return; // Avoid redundant operations
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
        }

        transaction.commit();
        currentFragmentIndex = newIndex;
    }
}
