package com.kiven.kutils.tools;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kiven.kutils.callBack.Consumer;

import java.util.HashMap;
import java.util.Map;

public class RequestPermissionFragment extends Fragment {
    @RequiresApi(api = Build.VERSION_CODES.M)
    private RequestPermissionFragment() {
    }

    public static void requestPermissions(@NonNull FragmentManager manager, @NonNull String[] pers, @NonNull Consumer<Map<String, Boolean>> call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RequestPermissionFragment fragment = new RequestPermissionFragment();
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
        return new View(getContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityResultLauncher<String[]> requestPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> o) {
                dismiss();
                call.callBack(o);
            }
        });
        requestPermissions.launch(pers);
    }

    private void dismiss() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.remove(this);
        ft.commit();
    }
}
