package com.clubank.my;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clubank.club11test.R;

public class MyFragment extends Fragment {
    private String content;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MyFragment newInstance(String title) {
        MyFragment fragment = new MyFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        TextView headerTitle= view.findViewById(R.id.title);
        if(!TextUtils.isEmpty(content)){
            headerTitle.setText(content);
        }
        return view;
    }

}
