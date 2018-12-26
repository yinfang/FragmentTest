package com.clubank.message;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.clubank.club11test.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private List<Fragment> fragments;
    private ViewPager vp;
    private RadioGroup rg;
    private MsgChatFragment msg_chat_fragment;//    聊天的Fragment
    private MsgExchangeFragment msg_exchange_fragment;    //互动的Fragment


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment, container, false);
        LinearLayout back = view.findViewById(R.id.header_back);
        back.setVisibility(View.INVISIBLE);

        vp = view.findViewById(R.id.vp);
        rg = view.findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.main) {
                    vp.setCurrentItem(0, true);
                } else {
                    vp.setCurrentItem(1, true);
                    EventBus.getDefault().post("测试EventBus传值");
                }
            }
        });
        fragments = new ArrayList<>(2);
        msg_chat_fragment =  MsgChatFragment.newInstance("聊天");
        msg_exchange_fragment = new MsgExchangeFragment();

        fragments.add(msg_chat_fragment);
        fragments.add(msg_exchange_fragment);
        vp.setOnPageChangeListener(this);
        FragPagerAdapter adapter = new FragPagerAdapter(getFragmentManager(), fragments);
        vp.setAdapter(adapter);
        return view;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        ((RadioButton) rg.getChildAt(i)).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
