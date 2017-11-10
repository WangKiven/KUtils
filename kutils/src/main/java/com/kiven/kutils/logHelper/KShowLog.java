package com.kiven.kutils.logHelper;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import java.util.regex.Pattern;

public class KShowLog extends KActivityHelper implements AdapterView.OnItemClickListener {

	ListView listView;
	private ArrayList<String> positions;

	@Override
	public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
		super.onCreate(activity, savedInstanceState);


		positions = new ArrayList<String>(KLog.getPositions());

		listView = new ListView(activity);
		listView.setDividerHeight(5);
		listView.setAdapter(new MyAdapter(new ArrayList<String>(KLog.getLogs())));

		setContentView(listView);

		listView.setOnItemClickListener(this);

		new ACheckRes().startActivity(activity);
	}

	@Override
	protected Class getActivityClas() {
		return KHelperActivity.class;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

		final String log = String.valueOf(parent.getItemAtPosition(position));
		String pos = positions.get(position);

		final String[] poss = Pattern.compile(",").split(pos);

		String slog = log + "\n";
		for (String pp : poss) {
			slog += ("\n" + pp);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setMessage(slog);
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
				if(log.startsWith("http://") || log.startsWith("https://")){
					try {
						new KShowLogDetail(log).startActivity(mActivity);
					} catch (Exception e) {
						KLog.e(e);
						Toast.makeText(mActivity, "解析网址失败", Toast.LENGTH_SHORT).show();
					}
				}else {
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
