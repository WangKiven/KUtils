package com.kiven.kutils.tools;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class KView {
	/**
	 * 设置单击事件，主要防止多次连续点击
	 * @param view
	 * @param listener
	 */
	public static void setOnClickListener(View view, final OnClickListener listener) {
		if (view == null || listener == null) {
			return ;
		}
		
		view.setOnClickListener(new OnClickListener() {
			private long lastClickTime;
			private synchronized boolean isFastClick(){
				long time = System.currentTimeMillis();
		        if ( time - lastClickTime < 1000) {
		            return true;
		        }
		        lastClickTime = time;
		        return false;
			}
			
			@Override
			public void onClick(View v) {
				if (isFastClick()) {
					return ;
				}
				listener.onClick(v);
			}
		});
	}
	
	/**
	 * 设置背景
	 * @param v
	 * @param background
	 */
	@SuppressLint("NewApi")
	public static void setBackground( View v, Drawable background ) {
		if ( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
			v.setBackgroundDrawable ( background );
		} else {
			v.setBackground ( background );
		}
	}
	
	/**
	 * 显示，隐藏控件
	 * @param view
	 * @param isShow
	 */
	public static void setVisibility(View view, boolean isShow){
		view.setVisibility(isShow ? View.VISIBLE: View.GONE);
	}
	
	/**
	 * 设置View在LinearLayout中的weight
	 * @param view
	 * @param weight
	 */
	public static void setWeight(View view, float weight){
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
		params.weight = weight;
	}
}
