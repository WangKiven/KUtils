package com.kiven.sample;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;

/**
 *
 * Created by kiven on 2017/2/25.
 */
public class ActivityCustomRecyclerView extends KActivityHelper {
    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(mActivity);
        linearLayout.setId(R.id.ll_root);
        setContentView(linearLayout);
        mActivity.getSupportFragmentManager().beginTransaction().add(R.id.ll_root, new FragmentApple()).commit();
    }
}
