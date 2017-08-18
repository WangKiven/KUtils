package com.kiven.kutils.activityHelper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.kiven.kutils.R;
import com.kiven.kutils.logHelper.KLog;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * fragment代理Activity。
 * Created by kiven on 2017/7/21.
 */

public class KFragmentActivity extends AppCompatActivity {
    Fragment helperFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setId(R.id.ll_content);
        setContentView(linearLayout);

        initFragment();
    }

    private void initFragment() {
        if (helperFragment == null) {
            if (getIntent().hasExtra("fragment_name")) {
                String fragmentName = getIntent().getStringExtra("fragment_name");
                @SuppressLint("RestrictedApi") List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments == null || fragments.size() == 0) {
                    try {
                        Constructor[] constructors = Class.forName(fragmentName).getConstructors();
                        if (constructors != null && constructors.length > 0) {
                            Constructor constructor = constructors[0];
                            helperFragment = (Fragment) constructor.newInstance();

                            getSupportFragmentManager().beginTransaction().add(R.id.ll_content, helperFragment).commit();
                        }

                    } catch (Exception e) {
                        KLog.e(e);
                    }
                } else {
                    for (Fragment fragment : fragments) {
                        if (TextUtils.equals(fragmentName, fragment.getClass().getName())) {
                            helperFragment = fragment;
                        }
                    }
                }
            }
        }

    }

    public void onClick(View view) {
        if (helperFragment != null && helperFragment instanceof View.OnClickListener) {
            ((View.OnClickListener) helperFragment).onClick(view);
        }
    }
}
