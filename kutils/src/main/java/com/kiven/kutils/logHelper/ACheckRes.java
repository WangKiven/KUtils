package com.kiven.kutils.logHelper;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiven.kutils.R;
import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;
import com.kiven.kutils.tools.KUtil;

import java.lang.reflect.Field;

/**
 * Created by wangk on 2017/11/10.
 */

public class ACheckRes extends KActivityHelper {
    private Class dclass = android.R.drawable.class;
    private Field[] types = dclass.getFields();

    private int resWhere = 0;
    private int resType = 0;

    private RecyclerView recyclerView;
    private ResAdapter resAdapter = new ResAdapter();

    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        setContentView(R.layout.k_a_check_res);

        Toolbar toolBar = findViewById(R.id.toolbar);
        mActivity.setSupportActionBar(toolBar);
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            toolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, KUtil.getScreenWith(mActivity) / KUtil.dip2px(50f)));
        recyclerView.setAdapter(resAdapter);

        AppCompatSpinner spinner_where = findViewById(R.id.spinner_where);
        spinner_where.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resWhere = position;
                onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        AppCompatSpinner spinner_type = findViewById(R.id.spinner_type);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resType = position;
                onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onChange() {
        if (resWhere == 0) {
            switch (resType) {
                case 0:
                    dclass = android.R.drawable.class;
                    break;
                default:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        dclass = android.R.mipmap.class;
                    }
                    break;
            }
        } else {
            switch (resType) {
                case 0:
                    dclass = R.drawable.class;
                    break;
                default:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        dclass = R.mipmap.class;
                    }
                    break;
            }
        }
        if (dclass == null) {
            types = null;
        } else {
            types = dclass.getFields();
        }
        resAdapter.notifyDataSetChanged();
    }

    private class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }

        void bindData(int position) {
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            TextView tv_num = (TextView) itemView.findViewById(R.id.tv_num);
            try {
                imageView.setImageResource(types[position].getInt(dclass));
            } catch (Exception e) {
            }
            tv_num.setText("" + position);
        }
    }

    private class ResAdapter extends RecyclerView.Adapter<Holder> {
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(mActivity).inflate(R.layout.k_item_res, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bindData(position);
        }

        @Override
        public int getItemCount() {
            return types == null ? 0 : types.length;
        }

    }

    @Override
    protected Class getActivityClas() {
        return KHelperActivity.class;
    }
}
