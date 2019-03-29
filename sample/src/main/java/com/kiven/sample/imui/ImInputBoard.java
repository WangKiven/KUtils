package com.kiven.sample.imui;

import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.kiven.sample.R;

public class ImInputBoard extends LinearLayout implements View.OnClickListener {
    private FrameLayout expendBoard;

    private int curShow = 0;//当前显示的面板， 0：没显示面板， 1：显示语言面板， 2： 显示表情面板， 3： 显示更多功能面板

    public ImInputBoard(Context context) {
        super(context);
        init(context);
    }

    public ImInputBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImInputBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ImInputBoard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.im_input_board, this);

        expendBoard = findViewById(R.id.expendBoard);

        findViewById(R.id.btn_voice).setOnClickListener(this);
        findViewById(R.id.btn_emoji).setOnClickListener(this);
        findViewById(R.id.btn_more).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_voice:
                break;
            case R.id.btn_emoji:
                break;
            case R.id.btn_more:
                expendBoard.removeAllViews();
//                LayoutInflater.from(getContext()).inflate(R.layout.view_input_more, expendBoard);

                break;
        }
    }


}
