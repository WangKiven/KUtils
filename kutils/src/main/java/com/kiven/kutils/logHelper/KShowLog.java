package com.kiven.kutils.logHelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
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
    List<KLogInfo> mData;

    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        setContentView(R.layout.k_show_log);

        if (KUtil.getSharedPreferencesIntValue("kutil_log_res_preferences", 0) == 1) {
            new ACheckRes().startActivity(mActivity);
            finish();
            return;
        }
        initBackToolbar(R.id.toolbar);
        listView = findViewById(R.id.listView);

        mData = new ArrayList<KLogInfo>(KLog.getLogs());

        listView.setDividerHeight(5);
        listView.setAdapter(myAdapter = new MyAdapter(mActivity, mData));

        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mActivity.getMenuInflater().inflate(R.menu.show_log, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem searchItem = menu.findItem(R.id.search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                String searchText = "";
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
                        myAdapter.changeData(mData);
                    } else {
                        LinkedList<KLogInfo> searchs = new LinkedList<KLogInfo>();
                        for (KLogInfo logInfo : mData) {
                            if (logInfo.log != null && logInfo.log.contains(newText)) {
                                searchs.add(new KLogInfo(logInfo.codePosition, logInfo.log.replace(newText, "<font color='red'>" + newText + "</font>")));
                            }
                        }
                        myAdapter.changeData(searchs);
                    }
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int i = item.getItemId();
        if (i == R.id.res) {
            KUtil.putSharedPreferencesIntValue("kutil_log_res_preferences", 1);
            new ACheckRes().startActivity(mActivity);
            finish();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
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
                        /*JsonReader jsonReader = new JsonReader(new StringReader(log));
						jsonReader.setLenient(true);*/
                        JsonElement jsonElement = new JsonParser().parse(log.trim());
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

        public MyAdapter(Activity activity, List<KLogInfo> searchData) {
            this.searchData = searchData;
            mActivity = activity;
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

        void changeData(List<KLogInfo> mData) {
            this.searchData = mData;
            notifyDataSetChanged();
        }
    }
}
