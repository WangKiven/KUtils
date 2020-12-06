package com.kiven.sample.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.RemoteViews;

import com.kiven.kutils.activityHelper.activity.KActivity;
import com.kiven.sample.LaunchActivity;
import com.kiven.sample.R;

/**
 * Created by kiven on 2016/11/17.
 */

public class ConfigureWidget extends KActivity {
    private int mAppWidgetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            Intent intent2 = new Intent(this, LaunchActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);

            RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.widget_layout);
            views.setTextColor(R.id.tv_title, Color.CYAN);
            views.setTextViewText(R.id.tv_title, "YeahY");
            views.setOnClickPendingIntent(R.id.ll_content, pendingIntent);


            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            appWidgetManager.updateAppWidget(mAppWidgetId, views);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

            setResult(RESULT_OK, resultValue);
        }
        finish();
    }
}
