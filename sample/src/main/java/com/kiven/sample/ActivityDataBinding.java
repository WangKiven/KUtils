package com.kiven.sample;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.kiven.sample.entity.EntityUser;

/**
 * Created by kiven on 2017/2/13.
 */

public class ActivityDataBinding extends AppCompatActivity implements View.OnClickListener {
    DataBindingMode dataBindingMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_data_binding);

        dataBindingMode = DataBindingUtil.setContentView(this, R.layout.activity_data_binding);
        dataBindingMode.setBoo(false);
        dataBindingMode.setUser(new EntityUser(2, "kiven", 14));

//        setTitle("Data Binding");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public void onClick(View v) {
        dataBindingMode.setBoo(!dataBindingMode.getBoo());
    }
}
