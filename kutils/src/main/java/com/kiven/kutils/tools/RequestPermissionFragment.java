package com.kiven.kutils.tools;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kiven.kutils.callBack.Consumer;

public class RequestPermissionFragment extends Fragment {
    @RequiresApi(api = Build.VERSION_CODES.M)
    private RequestPermissionFragment() {
    }

    public static void requestPermissions(@NonNull FragmentManager manager, @NonNull String[] pers, @NonNull Consumer<Boolean> call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RequestPermissionFragment fragment = new RequestPermissionFragment();
            fragment.pers = pers;
            fragment.call = call;

            fragment.show(manager);
        } else {
            call.callBack(true);
        }
    }

    private void show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, "RequestPermissionFragment");
        ft.commit();
    }

    private String[] pers;
    private Consumer<Boolean> call;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new View(getContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestPermissions(pers, 777);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                call.callBack(false);
                dismiss();
                return;
            }
        }

        call.callBack(true);
        dismiss();
    }

    private void dismiss() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(this);
            ft.commit();
        }
    }
}
