package dev.lyg.cp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import dev.lyg.cp.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of App Widget functionality.
 */
public class aWordWidget extends AppWidgetProvider {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // 构造 RemoteViews 对象
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.a_word_widget);

        // 异步获取每日一言
        executor.execute(() -> {
            String hitokoto = getHitokoto();
            views.setTextViewText(R.id.widget_text, hitokoto); // 更新文字
            ComponentName widget = new ComponentName(context, aWordWidget.class);
            appWidgetManager.updateAppWidget(widget, views); // 刷新小组件
        });

        // 指示小部件管理器更新小部件
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static String getHitokoto() {
        String apiUrl = "https://v1.hitokoto.cn/?c=f&encode=text";
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "思念悬枝，月影迟迟";
        }
        return result.toString();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // 可能有多个活动小部件，因此请更新所有小部件
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // 创建第一个小部件时输入相关功能
    }

    @Override
    public void onDisabled(Context context) {
        // 输入禁用最后一个小部件时的相关功能
    }
}