package dev.lyg.cp.lock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import dev.lyg.cp.MainActivity;
import dev.lyg.cp.R;

public class NotificationUtil {
    private final NotificationManager notificationManager;
    private final Context mContext;

    public static final int NOTIFICATION_ID = 10003;

    public NotificationUtil(Context mContext) {
        this.mContext = mContext;
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 展示通知栏
     */
    public void showNotification() {
        String channelId = "channel_demo";
        String channelName = mContext.getString(R.string.app_name);

        // 创建 NotificationChannel（针对 Android 8.0 及以上）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            mChannel.setDescription("通知栏");
            notificationManager.createNotificationChannel(mChannel);
        }

        // 创建通知
        Notification notification = new NotificationCompat.Builder(mContext, channelId)
                .setSmallIcon(R.drawable.music) // 替换为你的实际图标
                .setCustomBigContentView(getContentView(true)) // 自定义大视图
                .setCustomContentView(getContentView(false)) // 自定义小视图
                .setContentIntent(getDefaultIntent()) // 点击跳转
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setTicker("正在播放")
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private PendingIntent getDefaultIntent() {
        Intent intent = new Intent(mContext, MainActivity.class);
        return PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    /**
     * 获取自定义通知栏视图
     *
     * @param showBigView 是否为大视图
     * @return RemoteViews
     */
    private RemoteViews getContentView(boolean showBigView) {
        int layoutId = showBigView ? R.layout.view_notify_big : R.layout.view_notify_small;
        return new RemoteViews(mContext.getPackageName(), layoutId);
    }
}
