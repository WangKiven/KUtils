package com.kiven.kutils.logHelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.kiven.kutils.R;
import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;
import com.kiven.kutils.tools.KContext;
import com.kiven.kutils.tools.KImage;
import com.kiven.kutils.tools.KUtil;

import java.lang.reflect.Field;

/**
 * 图片资源查看
 * Created by wangk on 2017/11/10.
 */

public class ACheckRes extends KActivityHelper {
    private Class dclass;
    private Field[] types;

    private String resWhereKey = "K_kiven_resWhereKey";
    private String resTypeKey = "K_kiven_resTypeKey";
    private int resWhere = 0;// 0：由安卓系统库提供，1：由本app提供
    private int resType = 0;// 0：mipmap，1：drawable，2：drawable 和 mipmap，3：string，4：color

    private RecyclerView recyclerView;
    private ResAdapter resAdapter = new ResAdapter();

    private int itemBg = Color.parseColor("#888888");

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, Menu.FIRST + 1, 0, "查看日志");
        menu.add(0, Menu.FIRST + 2, 0, "改变背景");
        menu.add(0, Menu.FIRST + 3, 1, "文件目录");
        menu.add(0, Menu.FIRST + 4, 2, "查看应用相关");
        return true;
    }

    int count = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                new KShowLog().startActivity(mActivity);
                finish();
                break;
            case Menu.FIRST + 2:
                count++;
                if (count % 2 == 1) {
                    itemBg = Color.parseColor("#ffffff");
                } else {
                    itemBg = Color.parseColor("#888888");
                }
                resAdapter.notifyDataSetChanged();
                break;
            case Menu.FIRST + 3:
                new AHFileManager().startActivity(mActivity);
                break;
            case Menu.FIRST + 4:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                mActivity.startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        activity.setTheme(R.style.Theme_AppCompat_NoActionBar);
        setContentView(R.layout.k_a_check_res);

        initBackToolbar(R.id.toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        {
            try {
                Class.forName("com.google.android.flexbox.FlexboxLayoutManager");

                FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(mActivity);
                layoutManager.setFlexDirection(FlexDirection.ROW);
                layoutManager.setJustifyContent(JustifyContent.CENTER);
                recyclerView.setLayoutManager(layoutManager);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                recyclerView.setLayoutManager(new GridLayoutManager(mActivity, KUtil.getScreenWith() / KUtil.dip2px(50f)));
            }
        }

        recyclerView.setAdapter(resAdapter);

        resWhere = KUtil.getSharedPreferencesIntValue(resWhereKey, 0);
        resType = KUtil.getSharedPreferencesIntValue(resTypeKey, 0);

        AppCompatSpinner spinner_where = findViewById(R.id.spinner_where);
        spinner_where.setSelection(resWhere);
        spinner_where.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resWhere = position;
                KUtil.putSharedPreferencesIntValue(resWhereKey, resWhere);
                onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        AppCompatSpinner spinner_type = findViewById(R.id.spinner_type);
        spinner_type.setSelection(resType);
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resType = position;
                KUtil.putSharedPreferencesIntValue(resTypeKey, resType);
                onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private MyTask task;

    private void onChange() {
        if (task != null) {
            task.cancel(true);
        }

        task = new MyTask();
        task.execute(0);
    }

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView tv_num;

        Field field;

        Holder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);


            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);
            tv_num.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void bindData(int position) {
            itemView.setBackgroundColor(itemBg);

            field = types[position];
            try {
                if (resType == 2) {
                    tv_num.setText(position + " " + field.getName());
                    tv_num.setTextColor(KImage.getColor(mActivity, field.getInt(dclass)));
                    setDrawable(null);
                } else {
                    Drawable drawable = getDrawable(field.getInt(dclass));
                    setDrawable(drawable);

                    tv_num.setText(position + " " + drawable.getClass().getSimpleName().replace("Drawable", ""));
                }

            } catch (Exception e) {
                tv_num.setText(position + "");
                setDrawable(null);
            }
        }

        private void setDrawable(Drawable drawable) {
            if (drawable == null) {
                imageView.setImageDrawable(null);
                KImage.setBackgroundDrawable(imageView, null);
            } else {
                if (drawable instanceof ColorDrawable || drawable instanceof StateListDrawable || drawable instanceof NinePatchDrawable
                        || drawable instanceof GradientDrawable || drawable instanceof LayerDrawable) {
                    imageView.setImageDrawable(null);
                    KImage.setBackgroundDrawable(imageView, drawable);
                } else {
                    imageView.setImageDrawable(drawable);
                    KImage.setBackgroundDrawable(imageView, null);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (field != null) {
                KLog.i("fieldName = " + field.getName());

                try {
                    if (resType == 2) {
                        tv_num.setSelected(!tv_num.isSelected());
                    } else {
                        Drawable drawable = getDrawable(field.getInt(dclass));
                        KLog.i("draClass = " + drawable.getClass().getName());

                        View view = LayoutInflater.from(mActivity).inflate(R.layout.k_item_res, null);
                        Holder childHolder = new Holder(view);

                        childHolder.imageView.setMaxHeight(Integer.MAX_VALUE);

                        childHolder.itemView.setBackgroundColor(itemBg);
                        childHolder.setDrawable(drawable);
                        childHolder.tv_num.setText(field.getName() + "\n" + drawable.getClass().getName());

                        new AlertDialog.Builder(mActivity).setView(view).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

    class MyTask extends AsyncTask {
        private Class dclass;
        private Field[] types;

        @Override
        protected Object doInBackground(Object[] objects) {
            if (resWhere == 0) {
                String na;
                switch (resType) {
                    case 0:
                        na = "mipmap";
                        break;
                    case 1:
                        na = "drawable";
                        break;
                    default:
                        na = "color";
                        break;
                }
                try {
                    String cln = KContext.getInstance().getPackageName() + ".R$" + na;
                    dclass = Class.forName(cln);
                } catch (ClassNotFoundException e) {
                    KLog.e(e);
                    dclass = null;
                }
            } else {
                switch (resType) {
                    case 0:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            dclass = android.R.mipmap.class;
                        }
                        break;
                    case 1:
                        dclass = android.R.drawable.class;
                        break;
                    default:
                        dclass = android.R.color.class;
                        break;
                }
            }
            if (dclass == null) {
                types = null;
            } else {
                types = dclass.getFields();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            ACheckRes.this.dclass = dclass;
            ACheckRes.this.types = types;
            resAdapter.notifyDataSetChanged();
        }
    }
}
