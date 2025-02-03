package dev.lyg.cp.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import dev.lyg.cp.R;

public class SelectView extends ConstraintLayout {
    private ImageView ivIcon;
    private TextView tvTitle;
    private ImageView ivArrow;

    public SelectView(Context context) {
        super(context);
        init(context);
    }

    public SelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.select_layout, this, true);

        // 绑定视图
        ivIcon = findViewById(R.id.ivIcon);
        tvTitle = findViewById(R.id.tvTitle);
        ivArrow = findViewById(R.id.ivArrow);
    }

    public void setTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    // 动态设置下拉选项并展示菜单
    public void setDropdownOptions(List<String> options) {
        this.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), this);

            // 动态添加选项
            for (String option : options) {
                popup.getMenu().add(option);
            }

            // 设置选择监听
            popup.setOnMenuItemClickListener(item -> {
                setTitle(item.getTitle().toString());
                return true;
            });

            // 显示菜单
            popup.show();
        });
    }
}

