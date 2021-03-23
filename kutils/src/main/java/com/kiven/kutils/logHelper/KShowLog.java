package com.kiven.kutils.logHelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.MenuItemCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kiven.kutils.R;
import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;
import com.kiven.kutils.tools.KString;
import com.kiven.kutils.tools.KUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class KShowLog extends KActivityHelper implements AdapterView.OnItemClickListener {

    private ListView listView;

    private MyAdapter myAdapter;
    private List<KLogInfo> mData;
    /**
     * 高亮显示匹配结果
     */
    private boolean showSearch = true;

    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        activity.setTheme(R.style.KTheme);
        setContentView(R.layout.k_show_log);

        showSearch = KUtil.getSharedPreferencesBooleanValue("kutil_log_show_search", true);

        initBackToolbar(R.id.toolbar);

        listView = findViewById(R.id.listView);

        mData = new ArrayList<KLogInfo>(KLog.getLogs());

        listView.setDividerHeight(5);
        listView.setAdapter(myAdapter = new MyAdapter(mActivity, mData, showSearch));

        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mActivity.getMenuInflater().inflate(R.menu.show_log, menu);
        menu.add(0, Menu.FIRST + 1000, 0, "高亮结果");
        menu.add(0, Menu.FIRST + 1001, 1, "文件目录");
        menu.add(0, Menu.FIRST + 1002, 2, "查看应用相关");
        menu.add(0, Menu.FIRST + 1003, 3, "内存CPU情况");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem searchItem = menu.findItem(R.id.search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (TextUtils.equals(searchText, newText)) {
                        return true;
                    }
                    if (KString.isBlank(newText)) {
                        myAdapter.changeData(mData, showSearch);
                    } else {
                        searchText = newText;
                        search();
                    }
                    return true;
                }
            });
        }
        return true;
    }

    String searchText;
    private void search() {
        if (searchText == null || searchText.length() == 0) {
            myAdapter.changeData(mData, showSearch);
            return;
        }

        LinkedList<KLogInfo> searchs = new LinkedList<KLogInfo>();
        for (KLogInfo logInfo : mData) {
            if (logInfo.log != null && logInfo.log.contains(searchText)) {
                if (showSearch) {
                    searchs.add(new KLogInfo(logInfo.codePosition, logInfo.log.replace(searchText, "<font color='red'>" + searchText + "</font>")));
                } else {
                    searchs.add(logInfo);
                }
            }
        }
        myAdapter.changeData(searchs, showSearch);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int i = item.getItemId();
        if (i == R.id.res) {
            new ACheckRes().startActivity(mActivity);
            finish();
        } else if (i == Menu.FIRST + 1000) {
            showSearch = !showSearch;
            KUtil.putSharedPreferencesBooleanValue("kutil_log_show_search", showSearch);
            search();
        } else if (i == Menu.FIRST + 1001) {
            new AHFileManager().startActivity(mActivity);
        } else if (i == Menu.FIRST + 1002) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
            mActivity.startActivity(intent);
        } else if (i == Menu.FIRST + 1003) {
            new KCPUMem().startActivity(mActivity);
        }
        return true;
    }

    @Override
    protected Class getActivityClas() {
        return KHelperActivity.class;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        KLogInfo logInfo = (KLogInfo) parent.getItemAtPosition(position);
        final String log = logInfo.log;

        final String[] poss = Pattern.compile(",").split(logInfo.codePosition);

        String slog = log + "\n";
        for (String pp : poss) {
            slog += ("\n" + pp);
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mActivity);
        builder.setMessage(KString.fromHtml(slog));
        builder.setPositiveButton("复制", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KString.setClipText(mActivity, log);
                Toast.makeText(mActivity, "copied: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("打印", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("ULog_default", "logLength = " + (log == null? 0:log.length()));
                Log.i("ULog_default", log);
                for (String pp : poss) {
                    Log.i("ULog_default", pp);
                }
            }
        });
        builder.setNeutralButton("解析", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (log.startsWith("http://") || log.startsWith("https://")) {
                    try {
                        new KShowLogDetail(log).startActivity(mActivity);
                    } catch (Exception e) {
                        KLog.e(e);
                        Toast.makeText(mActivity, "解析网址失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        JsonElement jsonElement = JsonParser.parseString(log.trim());

                        if (jsonElement.isJsonArray()) {
                            new KShowLogDetail(new Gson().fromJson(jsonElement, List.class)).startActivity(mActivity);
                        } else if (jsonElement.isJsonObject()) {
                            new KShowLogDetail(new Gson().fromJson(jsonElement, Map.class)).startActivity(mActivity);
                        }
                    } catch (Exception e) {
                        KLog.e(e);
                        Toast.makeText(mActivity, "解析json失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.create().show();
    }

    static class MyAdapter extends BaseAdapter {
        Activity mActivity;
        List<KLogInfo> searchData;
        boolean showSearch;

        public MyAdapter(Activity activity, List<KLogInfo> searchData, boolean showSearch) {
            this.searchData = searchData;
            mActivity = activity;
            this.showSearch = showSearch;
        }

        @Override
        public int getCount() {
            return searchData == null ? 0 : searchData.size();
        }

        @Override
        public Object getItem(int position) {
            return searchData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(mActivity);
                textView.setPadding(30, 30, 30, 30);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundColor(Color.WHITE);
                textView.setMaxLines(5);
                convertView = textView;
            } else {
                textView = (TextView) convertView;
            }

            textView.setText(KString.fromHtml(position + "：" + searchData.get(position).log));

            return convertView;
        }

        void changeData(List<KLogInfo> mData, boolean showSearch) {
            if (this.searchData == mData) {
                return;
            }
            this.searchData = mData;
            this.showSearch = showSearch;
            notifyDataSetChanged();
        }
    }
}
