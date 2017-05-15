package com.kiven.kutils.logHelper;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class KShowLogDetail extends KActivityHelper implements AdapterView.OnItemClickListener {

	private int mType = 0;//0：数组 1：字典
	
	@SuppressWarnings("rawtypes")
	private List mList;
	private List<MyObject> oList;

	ListView listView;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (mType == 1) {
			startNewActivity(oList.get(position).value);
		}else {
			startNewActivity(mList.get(position));
		}
	}

	class MyObject{
		String key;
		Object value;
		public MyObject(String key, Object value) {
			this.key = key;
			this.value = value;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public KShowLogDetail(List list) {
		mType = 0;
		mList = list;
	}
	@SuppressLint("NewApi")
	public KShowLogDetail(String httpStr) {
		mType = 1;
		oList = new ArrayList<MyObject>();
		if (Build.VERSION.SDK_INT >= 11) {
			Uri uri = Uri.parse(httpStr);
			if (uri != null) {
				try {
					for (String key : uri.getQueryParameterNames()) {
						oList.add(new MyObject(key, uri.getQueryParameter(key)));
					}
					sortOList();
				} catch (Exception e) {
					KLog.e(e);
				}
			}
		}else {
			oList.add(new MyObject("error", "sdk过低，不予解析"));
		}
	}

	@SuppressWarnings("rawtypes")
	public KShowLogDetail(Map map) {
		mType = 1;
		if (map != null) {
			oList = new ArrayList<MyObject>();
			for (Object object : map.keySet().toArray()) {
				oList.add(new MyObject(object.toString(), map.get(object)));
			}
			sortOList();
		}
	}

	@Override
	public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
		super.onCreate(activity, savedInstanceState);

		listView = new ListView(activity);
		listView.setDividerHeight(5);
		listView.setAdapter(new MyAdapter());

		setContentView(listView);

		listView.setOnItemClickListener(this);
	}

	@Override
	protected Class getActivityClas() {
		return KHelperActivity.class;
	}
	
	private void sortOList(){
		if (oList != null) {
			Collections.sort(oList, new Comparator<MyObject>() {

				@Override
				public int compare(MyObject lhs, MyObject rhs) {
					return lhs.key.compareToIgnoreCase(rhs.key);
				}
			});
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void startNewActivity(Object tObject){
		if (tObject instanceof List) {
			new KShowLogDetail((List) tObject).startActivity(mActivity);
		}else if (tObject instanceof Map) {
			new KShowLogDetail((Map) tObject).startActivity(mActivity);
		} else if (tObject instanceof String) {
			try {
				JsonElement jsonElement = new JsonParser().parse((String) tObject);
				if (jsonElement.isJsonArray()) {
					new KShowLogDetail(new Gson().fromJson(jsonElement, List.class)).startActivity(mActivity);
				}else if (jsonElement.isJsonObject()) {
					new KShowLogDetail(new Gson().fromJson(jsonElement, Map.class)).startActivity(mActivity);
				}
			} catch (Exception e) {
				KLog.e(e);
			}
		}
	}
	
	class MyAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mType == 0) {
				return KShowLogDetail.this.mList.size();
			} else {
				return oList.size();
			}
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = null;
			if (convertView == null) {
				textView = new TextView(mActivity);
				textView.setPadding(30, 30, 30, 30);
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.WHITE);
				convertView = textView;
			}else {
				textView = (TextView) convertView;
			}
//			textView.setText(position + "：" + getItem(position).toString());
			if (mType == 0) {
				textView.setText(position + "：" + KShowLogDetail.this.mList.get(position));
			} else {
				MyObject object = oList.get(position);
				textView.setText(position + "_" + object.key + "：" + object.value);
			}
			return convertView;
		}
		
	}
}
