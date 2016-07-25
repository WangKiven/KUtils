package com.kiven.kutils.activityHelper.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.xutils.x;

/**
 * 可显示日志, 可使用XUtils3注解
 * Created by kiven on 16/7/22.
 */
public class KXUtilActivity extends KActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }
}
