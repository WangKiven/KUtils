package com.kiven.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 *
 * Created by kiven on 2017/2/13.
 */

public class ActivityDataBinding extends AppCompatActivity implements View.OnClickListener {
    com.kiven.sample.DataBindingMode dataBindingMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_data_binding);

        dataBindingMode = DataBindingUtil.setContentView(this, R.layout.activity_data_binding);
        dataBindingMode.setBoo(false);

//        setTitle("Data Binding");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public void onClick(View v) {
        dataBindingMode.setBoo(true);
    }
}
