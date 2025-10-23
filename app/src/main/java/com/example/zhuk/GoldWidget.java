package com.example.zhuk;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class GoldWidget extends AppWidgetProvider {
    private static final String ACTION_REFRESH = "ACTION_REFRESH";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_REFRESH.equals(intent.getAction())) {
            Toast.makeText(context, "Обновляем курс золота...", Toast.LENGTH_SHORT).show();
            updateAllWidgets(context);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent refreshIntent = new Intent(context, GoldWidget.class);
        refreshIntent.setAction(ACTION_REFRESH);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
                context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_layout, refreshPendingIntent);

        views.setTextViewText(R.id.widget_gold_rate, "...");
        views.setTextViewText(R.id.widget_label, "загрузка");

        appWidgetManager.updateAppWidget(appWidgetId, views);
        loadAndUpdateGoldRate(context, appWidgetManager, appWidgetId);
    }

    private void loadAndUpdateGoldRate(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        GoldRateService goldRateService = new GoldRateService(context);

        goldRateService.loadGoldRate(new GoldRateService.GoldRateCallback() {
            @Override
            public void onGoldRateLoaded(double goldRate) {
                updateWidgetView(context, appWidgetManager, appWidgetId, goldRate);
            }

            @Override
            public void onError(String error) {
                double cachedRate = goldRateService.getCachedGoldRate();
                if (cachedRate > 0) {
                    updateWidgetView(context, appWidgetManager, appWidgetId, cachedRate);
                } else {
                    updateWidgetView(context, appWidgetManager, appWidgetId, 0);
                }
            }
        });
    }

    private void updateWidgetView(Context context, AppWidgetManager appWidgetManager,
                                  int appWidgetId, double goldRate) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        if (goldRate > 0) {
            views.setTextViewText(R.id.widget_gold_rate, String.valueOf((int)goldRate));
            views.setTextViewText(R.id.widget_label, "руб/г");
        } else {
            views.setTextViewText(R.id.widget_gold_rate, "—");
            views.setTextViewText(R.id.widget_label, "ошибка");
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, GoldWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int appWidgetId : appWidgetIds) {
            GoldWidget widget = new GoldWidget();
            widget.updateWidget(context, appWidgetManager, appWidgetId);
        }
    }
}