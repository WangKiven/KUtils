package com.kiven.kutils.activityHelper;

import androidx.lifecycle.ViewModel;

/**
 * Created by wangk on 2017/12/26.
 */

public class KHelperModel extends ViewModel {
    KActivityHelper helper;
//    public int a = 0

    @Override
    protected void onCleared() {
        helper = null;
        super.onCleared();
    }
}
