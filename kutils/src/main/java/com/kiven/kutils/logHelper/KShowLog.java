package com.kiven.kutils.logHelper;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;
import com.kiven.kutils.tools.KString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KShowLog extends KActivityHelper implements AdapterView.OnItemClickListener {

	ListView listView;

	@Override
	public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
		super.onCreate(activity, savedInstanceState);

		listView = new ListView(activity);
		listView.setDividerHeight(5);
		listView.setAdapter(new MyAdapter(new ArrayList<String>(KLog.getLogs())));

		setContentView(listView);

		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		String log = parent.getItemAtPosition(position).toString();
		KString.setClipText(mActivity, log);
		Log.i("ULog_default", log);
		Toast.makeText(mActivity, "copied: " + position, Toast.LENGTH_SHORT).show();

		if(log.startsWith("http://") || log.startsWith("https://")){
			try {
				new KShowLogDetail(log).startActivity(mActivity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			try {
				JsonElement jsonElement = new JsonParser().parse(log.trim());
				if (jsonElement.isJsonArray()) {
					new KShowLogDetail(new Gson().fromJson(jsonElement, List.class)).startActivity(mActivity);
				}else if (jsonElement.isJsonObject()) {
					new KShowLogDetail(new Gson().fromJson(jsonElement, Map.class)).startActivity(mActivity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class MyAdapter extends BaseAdapter{

		List<String> mData;

		public MyAdapter(List<String> data) {
			super();
			mData = data;
		}

		@Override
		public int getCount() {
			return mData == null? 0: mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
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
			}else {
				textView = (TextView) convertView;
			}

			textView.setText(position + "：" + getItem(position));

			return convertView;
		}

	}
}
