package dev.lyg.cp.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;

import androidx.annotation.StyleRes;

import dev.lyg.cp.R;

public class BaseCustomDialog<T> extends Dialog {

    protected Context mContext;
    /**
     * 动画资源id
     */
    protected int mAnimId = -1;
    /**
     * 显示位置
     */
    protected int mGravity = -1;
    protected boolean isFullScreen = false;

    public BaseCustomDialog(Context context, @StyleRes int style) {
        super(context, style);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        if (window != null) {
            if (mGravity != -1) {
                window.setGravity(mGravity);
            } else {
                window.setGravity(Gravity.CENTER);
            }
            if (mAnimId != -1) {
                window.setWindowAnimations(mAnimId);
            }
        }
    }


    /**
     * 设置显示位置
     *
     * @param gravity
     * @return
     */
    public T setLocation(int gravity) {
        mGravity = gravity;
        return (T) this;
    }

    /**
     * 展示弹窗
     */
    @Override
    public void show() {
        if (mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) {
                return;
            }
        }
        if (isShowing()) {
            return;
        }
        super.show();
    }

    /**
     * 销毁弹窗
     */
    @Override
    public void dismiss() {
        if (mContext == null) {
            return;
        }
        if (mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing()) {
                return;
            }
        }
        if (!isShowing()) {
            return;
        }
        super.dismiss();
    }
}
