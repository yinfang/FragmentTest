package com.clubank.fragbacktest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.clubank.club11test.R;
import com.clubank.device.BaseFragment;

public class FragBack2 extends BaseFragment {
    private String title;

    public static FragBack2 newInstance(String title) {
        FragBack2 fragment = new FragBack2();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.back_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView header_title = view.findViewById(R.id.title);
        header_title.setText(title);
        Button btn_back = view.findViewById(R.id.back);
        btn_back.setText("To FragBack3");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startToFragment(getContext(), R.id.fl_content, FragBack3.newInstance("FragBack3"));
            }
        });
    }
}
