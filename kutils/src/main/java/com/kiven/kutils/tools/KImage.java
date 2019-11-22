package com.kiven.kutils.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

public class KImage {
	/**
	 * 图片圆角
	 *
	 * 其实主要靠：paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));这行代码，
	 * 为什么呢，我给大家解释下，SRC_IN这种模式，两个绘制的效果叠加后取交集展现后图，怎么说呢，咱们第一个绘制的是个圆形，
	 * 第二个绘制的是个Bitmap，于是交集为圆形，展现的是BItmap，就实现了圆形图片效果。圆角，其实就是先绘制圆角矩形，
	 * 是不是很简单，以后别人再说实现圆角，你就把这一行代码给他就行了。
	 *
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap bitmap) {
		return getCircleBitmap(bitmap, 200);
	}
	public static Bitmap getCircleBitmap(Bitmap bitmap, int min) {
		if (bitmap != null) {
			Bitmap outBitmap = Bitmap.createBitmap(min,
					min, Config.ARGB_8888);

			Paint paint = new Paint();
			paint.setAntiAlias(true);

			Rect rect = new Rect(0, 0, min, min);

			Canvas canvas = new Canvas(outBitmap);
			canvas.drawCircle(min / 2, min / 2, min / 2, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), rect, paint);
			return outBitmap;
		}

		return null;
	}

	/**
	 * 将图片圆角
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		return getRoundedCornerBitmap(bitmap, 200, KUtil.dip2px(KContext.getInstance(), 4));
	}
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int min, int radius) {
		if (bitmap != null) {
			Bitmap outBitmap = Bitmap.createBitmap(min,
					min, Config.ARGB_8888);

			Paint paint = new Paint();
			paint.setAntiAlias(true);

			Rect rect = new Rect(0, 0, min, min);
			RectF rectF = new RectF(rect);

			Canvas canvas = new Canvas(outBitmap);
			canvas.drawRoundRect(rectF, radius, radius, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), rect, paint);
			return outBitmap;
		}
		return null;
	}
	/**
	 * 圆角纯色
	 * 如：TImage.getCornerDrawable(Color.parseColor("#747ae8"), 10)
	 * 
	 * RoundRectShape(float[] outerRadii, RectF inset, float[] innerRadii) :指定一个外部（圆角）矩形 和 一个 可选的 内部（圆角）矩形。
	 */
	public static ShapeDrawable getCornerDrawable(int color, int corner){
		float[] corners = new float[8];
		for (int i = 0; i < corners.length; i++) {
			corners[i] = corner;
		}
		return getCornerDrawable(color, corners);
	}
	public static ShapeDrawable getCornerDrawable(int color, float[] corners){
		/*ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(corners, null, null));
		drawable.getPaint().setColor(color);
		drawable.getPaint().setStyle(Paint.Style.FILL);*/
		return getCornerDrawable(0, corners, color, 0, 0);
	}

	public static ShapeDrawable getCornerDrawable(int type, float[] corners, int fillColor, int rimColor, float rimWidth){
		ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(corners, null, null));
		setPaint(drawable.getPaint(), type, fillColor, rimColor, rimWidth);
		return drawable;
	}

	/**
	 * 圆角矩形线框
	 */
	public static ShapeDrawable getRimCornerDrawable(int color, int corner, float rimWidth){
		float[] corners = new float[8];
		for (int i = 0; i < corners.length; i++) {
			corners[i] = corner;
		}
		return getRimCornerDrawable(color, corners, rimWidth);
	}
	/**
	 * 圆角矩形线框
	 */
	public static ShapeDrawable getRimCornerDrawable(int color, float[] corners, float rimWidth){
		return getCornerDrawable(2, corners, 0, color, rimWidth);
	}

	/**
	 * 设置Paint
	 * @param type			0：仅填充 1：填充+边框 2：仅边框
	 * @param fillColor		填充颜色
	 * @param rimColor		边框颜色
	 * @param rimWidth		边框宽度
	 */
	private static void setPaint(Paint paint, int type, int fillColor, int rimColor, float rimWidth) {
		switch (type) {
			case 0:
				paint.setColor(fillColor);
				paint.setStyle(Paint.Style.FILL);
				break;
			case 1:
				//
				paint.setColor(fillColor);
				paint.setStrokeWidth(rimWidth);
				paint.setStyle(Paint.Style.FILL_AND_STROKE);
				break;
			case 2:
				paint.setColor(rimColor);
				paint.setStrokeWidth(rimWidth);
				paint.setStyle(Paint.Style.STROKE);
				break;
		}
	}
	/**
	 * 有各种效果的按钮Drawable
	 * @param normalDrawable
	 * @param disenableDrawable
	 * @param selectedDrawable
	 * @param pressDrawable
	 * @return
	 */
	public static StateListDrawable getSelectorDrawable(Drawable normalDrawable, Drawable disenableDrawable, Drawable selectedDrawable, Drawable pressDrawable){
		StateListDrawable listDrawable = new StateListDrawable();
		if (selectedDrawable != null) {
			listDrawable.addState(new int[]{android.R.attr.state_selected}, selectedDrawable);
		}
		if (pressDrawable != null) {
			listDrawable.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
		}
		if (normalDrawable != null) {
			listDrawable.addState(new int[]{android.R.attr.state_enabled}, normalDrawable);
		}
		if (disenableDrawable != null) {
			listDrawable.addState(new int[]{}, disenableDrawable);
		}
		return listDrawable;
	}
	/**
	 * 有各种效果的圆角按钮Drawable
	 * @param normalColor
	 * @param disenableColor
	 * @param selectedColor
	 * @param corner
	 */
	public static StateListDrawable getSelectorDrawable(int normalColor, int disenableColor, int selectedColor, int pressColor, int[] padding, int corner){
		StateListDrawable listDrawable = new StateListDrawable();
		if (selectedColor != 0) {
			listDrawable.addState(new int[]{android.R.attr.state_selected}, setShapeDrawablePadding(getCornerDrawable(selectedColor, corner), padding));
		}
		if (pressColor != 0) {
			listDrawable.addState(new int[]{android.R.attr.state_pressed}, setShapeDrawablePadding(getCornerDrawable(pressColor, corner), padding));
		}
		if (normalColor != 0) {
			listDrawable.addState(new int[]{android.R.attr.state_enabled}, setShapeDrawablePadding(getCornerDrawable(normalColor, corner), padding));
		}
		if (disenableColor != 0) {
			listDrawable.addState(new int[]{}, setShapeDrawablePadding(getCornerDrawable(disenableColor, corner), padding));
		}
		return listDrawable;
	}
	private static ShapeDrawable setShapeDrawablePadding(ShapeDrawable drawable, int[] padding){
		if (drawable != null) {
			if (padding != null && padding.length > 3) {
				drawable.setPadding(padding[0], padding[1], padding[2], padding[3]);
			}
		}
		return drawable;
	}
	public static StateListDrawable getSelectorPressDrawable(int normalColor, int pressColor, int[] padding, int corner){
		return getSelectorDrawable(normalColor, 0, 0, pressColor, padding, corner);
	}
	public static StateListDrawable getSelectorSelectedDrawable(int normalColor, int selectedColor, int[] padding, int corner){
		return getSelectorDrawable(normalColor, 0, selectedColor, 0, padding, corner);
	}
	/**
	 * 纯色圆，如果View是长方形则显示为椭圆
	 */
	public static ShapeDrawable getCircleDrawable(int color){
		/*ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
		drawable.getPaint().setColor(color);
		drawable.getPaint().setStyle(Paint.Style.FILL);*/
		
		return getCircleDrawable(0, color, 0, 0);
	}

	/**
	 * 获取圆，如果View是长方形则显示为椭圆
	 * @param type			0：仅填充 1：填充+边框 2：仅边框
	 * @param fillColor		填充颜色
	 * @param rimColor		边框颜色
	 * @param rimWidth		边框宽度
     */
	public static ShapeDrawable getCircleDrawable(int type, int fillColor, int rimColor, float rimWidth){
		ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
		setPaint(drawable.getPaint(), type, fillColor, rimColor, rimWidth);

		return drawable;
	}
	
	/**
	 * 压缩图片后显示
	 */
	public static void showImage(final View imageView, final Bitmap bitmap){
		if (bitmap == null || imageView == null) {
			return ;
		}
		final float iWid = imageView.getWidth();
		final float iHeight = imageView.getHeight();
		AsyncTask<Integer, Integer, Bitmap> task = new AsyncTask<Integer, Integer, Bitmap>(){

			@Override
			protected Bitmap doInBackground(Integer... params) {
//				float iWid = imageView.getWidth();
//				float iHeight = imageView.getHeight();
				
				float bWid = bitmap.getWidth();
				float bHeight = bitmap.getHeight();
				
				if (iWid * 1.5 >= bWid && iHeight * 1.5 >= bHeight) {
					return bitmap;
				}
				
				if (iWid == 0 || iHeight == 0 || bWid == 0 || bHeight == 0) {
					return bitmap;
				}
				
				Matrix matrix = new Matrix();
				matrix.postScale(iWid * 1.0f / bWid, iHeight * 1f / bHeight);
				
				Bitmap tBitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) bWid, (int) bHeight, matrix, true);
				return tBitmap;
			}
			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (result != null) {
					setImageView(imageView, result);
				}
			}
		};
		task.execute();
	}

	/**
	 * 设置显示图片
	 * @param imageView
	 * @param image
	 */
	public static void setImageView( View imageView, Bitmap image ){
		if(imageView instanceof ImageView){
			((ImageView)imageView).setImageBitmap(image);
		}else{

			Drawable drawable = new BitmapDrawable(imageView.getContext().getResources(), image);
			setBackgroundDrawable(imageView, drawable);
		}
	}
	/**
	 * 设置显示背景图片
	 * @param view
	 * @param drawable
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void setBackgroundDrawable(View view, Drawable drawable){
		if ( android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN ) {
			view.setBackgroundDrawable(drawable);
		} else {
			view.setBackground(drawable);
		}
	}

	/**
	 * 获取颜色
	 * @param rId
	 * @return
     */
	public static int getColor(Context context, @ColorRes int rId) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			return context.getColor(rId);
		} else {
			return context.getResources().getColor(rId);
		}
	}

	/**
	 * 获取颜色2
	 * @param context
	 * @param rId
	 * @return
	 */
	public static ColorStateList getColorStateList(Context context, @ColorRes int rId) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			return context.getColorStateList(rId);
		} else {
			return context.getResources().getColorStateList(rId);
		}
	}

	/**
	 * 获取drawable
	 * @param context	context
	 * @param rId		rId
	 * @return			Drawable
	 */
	public static Drawable getDrawable(Context context, @DrawableRes int rId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return context.getDrawable(rId);
		} else {
			return context.getResources().getDrawable(rId);
		}
	}
}
