package com.kiven.kutils.tools;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.kiven.kutils.callBack.Consumer;

import java.util.HashMap;
import java.util.Map;

public class RequestPermissionFragment2  extends Fragment {
    @RequiresApi(api = Build.VERSION_CODES.M)
    public RequestPermissionFragment2() {
    }

    public static void requestPermissions(@NonNull FragmentManager manager, @NonNull String[] pers, @NonNull Consumer<Map<String, Boolean>> call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RequestPermissionFragment2 fragment = new RequestPermissionFragment2();
            fragment.pers = pers;
            fragment.call = call;

            fragment.show(manager);
        } else {
            Map<String, Boolean> map = new HashMap<>();
            for (String per : pers) {
                map.put(per, true);
            }
            call.callBack(map);
        }
    }

    private void show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, "RequestPermissionFragment");
        ft.commit();
    }

    private String[] pers;
    private Consumer<Map<String, Boolean>> call;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new View(getActivity());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestPermissions(pers, 777);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Map<String, Boolean> map = new HashMap<>();
        for (int i = 0; i < permissions.length; i++) {
            map.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }
        call.callBack(map);

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
