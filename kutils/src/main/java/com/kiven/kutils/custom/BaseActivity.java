package com.kiven.kutils.custom;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import com.kiven.kutils.R;
import com.kiven.kutils.activityHelper.activity.KActivity;
import com.kiven.kutils.logHelper.KShowLog;
import com.kiven.kutils.logHelper.KLog;
import com.kiven.kutils.tools.KImage;
import com.kiven.kutils.tools.KView;

/**
 * 仅为事例, 建议在工程中重新创建
 * Created by kiven on 16/5/6.
 */
@Deprecated
public class BaseActivity extends KActivity {

    private View frame;
    protected HeaderFragment mHeader;
    private LinearLayout content;
    protected View v_conver;
    protected View v_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 状态栏是否透明
     *
     * @param isTran
     */
    protected void setStatusBar(boolean isTran) {
        mHeader.setStatusBar(isTran);
    }

    /**
     * 隐藏头部，如非必要，最好不掉
     */
    public void hideHeader() {
        getSupportFragmentManager().beginTransaction().hide(mHeader).commit();
    }


    @Override
    public void setContentView(View view) {
        frame = getLayoutInflater().inflate(R.layout.base_activity, null);
        content = (LinearLayout) frame.findViewById(R.id.ll_content);

        content.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        super.setContentView(frame);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment.getId() == R.id.m_header) {
                mHeader = (HeaderFragment) fragment;
                break;
            }
        }
        v_conver = getDelegate().findViewById(R.id.v_conver);
        v_btn = getDelegate().findViewById(R.id.v_btn);



        /*状态栏*/
        setStatusBar(false);
        /*调试按钮*/
        if (showLog()) {
            v_btn.setVisibility(View.VISIBLE);
            KView.setBackground(v_btn, KImage.getCircleDrawable(Color.parseColor("#22ff0000")));
            v_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    new KShowLog().startActivity(BaseActivity.this);
                    startActivity(new Intent(BaseActivity.this, KShowLog.class));
                }
            });
        } else {
            v_btn.setVisibility(View.GONE);

        }
    }

    protected boolean showLog() {
        return KLog.isDebug();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, null));
    }
}
