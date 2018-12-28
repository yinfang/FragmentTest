package com.clubank.job;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.clubank.club11test.R;
import com.clubank.device.BaseFragment;
import com.clubank.fragbacktest.FragBackActivity;

public class JobFragment extends BaseFragment {
    private String content;
    private RadioGroup rg;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static JobFragment newInstance(String title) {
        JobFragment fragment = new JobFragment();
        Bundle args = new Bundle();
        args.putString("content", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            content = getArguments().getString("content");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.job_fragment;
    }

    @Override
    public void onCreateView(View view, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(view, container, savedInstanceState);
        TextView headerTitle = view.findViewById(R.id.title);
        if (!TextUtils.isEmpty(content)) {
            headerTitle.setText(content);
        }
        Button btn_back=view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.addToBackStack(null);//将Fragment添加到回退栈中
                transaction.commit();
                openIntent(FragBackActivity.class, "测试Fragment回退栈");
            }
        });
    }

}
