package com.kiven.kutils.fragment;

import android.os.Bundle;
import android.view.View;

import roboguice.RoboGuice;

/**
 *
 * Created by kiven on 16/7/22.
 */
public class KRoboFragment extends KFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
    }
}
