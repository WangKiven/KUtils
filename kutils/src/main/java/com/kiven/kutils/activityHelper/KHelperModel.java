package com.kiven.kutils.activityHelper;

import android.arch.lifecycle.ViewModel;

/**
 * Created by wangk on 2017/12/26.
 */

public class KHelperModel extends ViewModel {
    public KActivityHelper helper;

    @Override
    protected void onCleared() {
        helper = null;
        super.onCleared();
    }
}
