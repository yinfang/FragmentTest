package com.clubank.fragbacktest;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;

import com.clubank.club11test.R;
import com.clubank.device.BaseActivity;

public class FragBackActivity extends BaseActivity {
    private FragmentManager manager; //Fragmnet的管理器
    private FragmentTransaction transaction; //Fragment事物

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_back);
        initView();
    }

    private void initView() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction(); //对RadioGroup设置监听
        FragBack1 frag1 = FragBack1.newInstance("FragBack1");
        transaction.addToBackStack(null);
        transaction.add(R.id.fl_content, frag1);
        transaction.commit();
    }

    //监听返回键 退出应用
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getFragmentManager().getBackStackEntryCount() > 0)
                getFragmentManager().popBackStack();
        }
        return true;
    }

    @Override
    public void back() {
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else
            super.back();
    }

}
