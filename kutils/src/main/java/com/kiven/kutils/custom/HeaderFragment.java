package com.kiven.kutils.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiven.kutils.R;

/**
 * 不建议直接使用
 * Created by kiven on 16/5/6.
 */
@Deprecated
public class HeaderFragment extends Fragment {

    private Button mLeftBtn;
    private TextView mTitle;
    private TextView mSubtitle;
    private Button mRightBtn;
    private LinearLayout mRootLayout;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_header, container);

        mLeftBtn = (Button) findViewById(R.id.header_left_button);
        mTitle = (TextView) findViewById(R.id.header_title);
        mSubtitle = (TextView) findViewById(R.id.sub_title);
        mRightBtn = (Button) findViewById(R.id.header_right_button);
        mRootLayout = (LinearLayout) findViewById(R.id.simple_header_layout);

        return view;
    }

    private View findViewById(@IdRes int idRes) {
        return view.findViewById(idRes);
    }
    /**
     * 设置状态栏是否透明
     * @param t
     */
    @SuppressLint("NewApi")
    public void setStatusBar(boolean t){
        if (t && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//	        //透明导航栏
//	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            mRootLayout.setFitsSystemWindows(true);
            mRootLayout.setClipToPadding(true);
        }
    }

    public void setRightBtn( int title, int drawable, View.OnClickListener listener ) {
        setRightBtn ( title, drawable == 0 ? null : getActivity ().getResources ().getDrawable ( drawable ), listener );
    }

    public void setRightBtn( String title, int drawable, View.OnClickListener listener ) {
        setRightBtn ( title, drawable == 0 ? null : getActivity ().getResources ().getDrawable ( drawable ), listener );
    }

    public void setRightBtn(String title, Drawable drawable, View.OnClickListener listener ) {
        mRightBtn.setVisibility ( View.VISIBLE );
        if ( title == null )
            title = "";
        mRightBtn.setText ( title );
        mRightBtn.setCompoundDrawablesWithIntrinsicBounds ( null, null, drawable, null );
        if ( listener != null )
            mRightBtn.setOnClickListener ( listener );
    }

    public void setRightBtn( int title, Drawable drawable, View.OnClickListener listener ) {
        mRightBtn.setVisibility ( View.VISIBLE );
        if ( title <= 0 )
            mRightBtn.setText ( "" );
        else
            mRightBtn.setText ( title );
        mRightBtn.setCompoundDrawablesWithIntrinsicBounds ( null, null, drawable, null );
        if ( listener != null )
            mRightBtn.setOnClickListener ( listener );
    }

    public void setLeftBtn( String title, int drawable, View.OnClickListener listener ) {
        setLeftBtn ( title, drawable == 0 ? null : getActivity ().getResources ().getDrawable ( drawable ), listener );
    }

    public void setLeftBtn( int title, int drawable, View.OnClickListener listener ) {
        setLeftBtn ( title, drawable == 0 ? null : getActivity ().getResources ().getDrawable ( drawable ), listener );
    }

    public void setLeftBtn( String title, Drawable drawable, View.OnClickListener listener ) {
        mLeftBtn.setVisibility ( View.VISIBLE );
        if ( title == null )
            title = "";
        mLeftBtn.setText ( title );
        mLeftBtn.setCompoundDrawablesWithIntrinsicBounds ( drawable, null, null, null );
        if ( listener != null )
            mLeftBtn.setOnClickListener ( listener );
    }

    public void setLeftBtn( int title, Drawable drawable, View.OnClickListener listener ) {
        mLeftBtn.setVisibility ( View.VISIBLE );
        if ( title <= 0 )
            mLeftBtn.setText ( "" );
        else
            mLeftBtn.setText ( title );
        mLeftBtn.setCompoundDrawablesWithIntrinsicBounds ( drawable, null, null, null );
        if ( listener != null )
            mLeftBtn.setOnClickListener ( listener );
    }

    public void defaultSetting(final Activity activity, String leftTitle, String title ) {
        setLeftBtn ( leftTitle, getActivity ().getResources ().getDrawable ( R.mipmap.back_icon ), new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                activity.finish ();
            }
        } );
        setTitle ( leftTitle );
    }

    public void defaultSetting( final Activity activity, int leftTitle, int title ) {
        setLeftBtn ( leftTitle, getActivity ().getResources ().getDrawable ( R.mipmap.back_icon ), new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                activity.finish ();
            }
        } );
        setTitle ( title );
    }

    public View getRootLayout() {
        return mRootLayout;
    }

    public void hideRightBtn( boolean hide ) {
        if ( hide )
            mRightBtn.setVisibility ( View.GONE );
        else
            mRightBtn.setVisibility ( View.VISIBLE );
    }

    public void hideLeftBtn( boolean hide ) {
        if ( hide )
            mLeftBtn.setVisibility ( View.GONE );
        else
            mLeftBtn.setVisibility ( View.VISIBLE );
    }

    public TextView getTitle() {
        return mTitle;
    }

    public void setTitle( String title ) {
        if ( title != null )
            mTitle.setText ( title );
    }

    public void setTitle( int resId ) {
        if ( resId != 0 )
            mTitle.setText ( resId );
    }

    public Button getRightBtn() {
        return mRightBtn;
    }

    public Button getLeftBtn() {
        return mLeftBtn;
    }
}
