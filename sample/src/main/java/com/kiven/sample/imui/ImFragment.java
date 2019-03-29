package com.kiven.sample.imui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiven.sample.R;

public class ImFragment extends Fragment {
    private ImInputBoard inputBoard;

    private ImCall imCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_im, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputBoard = view.findViewById(R.id.inputBoard);

        imCall = ImTool.call.callBack("");
        if (imCall == null) {
            return;
        }
    }
}
