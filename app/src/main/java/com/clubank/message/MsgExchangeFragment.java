package com.clubank.message;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.clubank.club11test.R;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MsgExchangeFragment extends Fragment implements ViewPager.OnPageChangeListener {
    private List<Fragment> fragments;
    private View view;
    private int[] radios;
    private RadioGroup rg;
    //对我有意，谁看过我，新职位，记录的Fragment
    private ExcLikemeFragment likeMeFragment;
    private ExcSeemeFragment seeMeFragment;
    private ExcnewJobFragment newJobFragment;
    private ExcRecordFragment recordFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MsgExchangeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.msg_exchange_fragment, container, false);
        initView();
        return view;
    }

    private void initView() {
        radios = new int[]{R.id.likeme, R.id.seeme, R.id.newjob, R.id.record};
        rg = view.findViewById(R.id.radioGroup);
        final ViewPager vp = view.findViewById(R.id.exc_chenge_vp);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < radios.length; i++) {
                    if (checkedId == radios[i]) {
                        vp.setCurrentItem(i);
                    }
                }
            }
        });
        fragments = new ArrayList<>(2);
        likeMeFragment = ExcLikemeFragment.newInstance("对我有意");
        seeMeFragment = ExcSeemeFragment.newInstance("看过我");
        newJobFragment = ExcnewJobFragment.newInstance("新职位");
        recordFragment = ExcRecordFragment.newInstance(0);
        EventBus.getDefault().postSticky(TestData());//EventBus发送粘性事件

        fragments.add(likeMeFragment);
        fragments.add(seeMeFragment);
        fragments.add(newJobFragment);
        fragments.add(recordFragment);
        vp.setOnPageChangeListener(this);
        FragPagerAdapter adapter = new FragPagerAdapter(getFragmentManager(), fragments);
        vp.setAdapter(adapter);
        vp.setCurrentItem(2);//setCurrentItem()需放在setAdapter()后面才有效
    }

    @Subscribe
    public void getEventMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private MyData TestData() {
        MyData datas = new MyData();
        for (int i = 0; i < 10; i++) {
            MyRow ro = new MyRow();
            ro.put("img", R.mipmap.ic_launcher);
            ro.put("desc", "title" + i);
            datas.add(ro);
        }
        return datas;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
