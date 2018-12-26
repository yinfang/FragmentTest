package com.clubank.home;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.clubank.club11test.R;
import com.clubank.company.CompanyFragment;
import com.clubank.device.BaseActivity;
import com.clubank.job.JobFragment;
import com.clubank.my.MyFragment;
import com.clubank.message.MessageFragment;
import com.clubank.util.UI;
import com.clubank.widget.BottomNavigationViewEx;

public class MainActivity extends BaseActivity {
    private long exittime;
    private FragmentManager manager; //Fragmnet的管理器
    private FragmentTransaction transaction; //Fragment事物
    private MyFragment  my_fragment;//我的页面
    private CompanyFragment company_fragment;//公司页面
    private JobFragment   job_fragment;//职位页面
    private MessageFragment  message_fragment; //消息页面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    private void initView() {
        hide(R.id.header_title);
        BottomNavigationViewEx navigation = findViewById(R.id.navigation);
        navigation.enableAnimation(true);
        navigation.enableShiftingMode(false);
        navigation.setOnNavigationItemSelectedListener(navListener);

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction(); //对RadioGroup设置监听
        // 职位
         job_fragment = JobFragment.newInstance("职位");
        //公司
         company_fragment = CompanyFragment.newInstance("公司");
        //消息
         message_fragment = new MessageFragment();
        //我的
         my_fragment = MyFragment.newInstance("我的");
        //为事物添加布局页面
        transaction.add(R.id.id_ll_content, job_fragment);
        transaction.add(R.id.id_ll_content, company_fragment);
        transaction.add(R.id.id_ll_content, message_fragment);
        transaction.add(R.id.id_ll_content, my_fragment);
        //隐藏收藏和我的界面
        transaction.hide(company_fragment);
        transaction.hide(my_fragment);
        transaction.hide(message_fragment);
        //提交事物
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            transaction = manager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.show(job_fragment);
                    transaction.hide(company_fragment);
                    transaction.hide(message_fragment);
                    transaction.hide(my_fragment);
                    break;
                case R.id.navigation_dashboard:
                    transaction.show(company_fragment);
                    transaction.hide(job_fragment);
                    transaction.hide(message_fragment);
                    transaction.hide(my_fragment);
                    break;
                case R.id.i_empty:
                    return false;
                case R.id.navigation_notifications:
                    transaction.show(message_fragment);
                    transaction.hide(company_fragment);
                    transaction.hide(job_fragment);
                    transaction.hide(my_fragment);
                    break;
                case R.id.navigation_visibility:
                    transaction.show(my_fragment);
                    transaction.hide(company_fragment);
                    transaction.hide(message_fragment);
                    transaction.hide(job_fragment);
                    break;
            }
            //提交事务
            transaction.commit();
            return true;
        }
    };

    /**
     * 中间按钮event
     */
    public void navCenter(View view) {
        UI.showToast(this, "我在中间");
    }

    //监听返回键 退出应用
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.exitapp();
        }
        return true;
    }

    private void exitapp() {
        if ((System.currentTimeMillis() - exittime) > 2000) {
            UI.showToast(MainActivity.this, "亲，再按一次就退出程序了哦\n *^__^* ");
            exittime = System.currentTimeMillis();
        } else {
            System.exit(0);
        }
    }
}
