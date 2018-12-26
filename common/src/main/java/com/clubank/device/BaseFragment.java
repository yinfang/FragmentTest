package com.clubank.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.clubank.common.R;
import com.clubank.domain.BRT;
import com.clubank.domain.C;
import com.clubank.domain.ContactInfo;
import com.clubank.domain.Criteria;
import com.clubank.domain.ListObject;
import com.clubank.domain.RT;
import com.clubank.domain.Result;
import com.clubank.util.GlideUtil;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.UI;

import java.util.Objects;
import java.util.Set;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    /**
     * Standard activity result: operation canceled.
     */
    public static final int RESULT_CANCELED = 0;
    /**
     * Standard activity result: operation succeeded.
     */
    public static final int RESULT_OK = -1;
    /**
     * Start of user-defined activity results.
     */
    public static final int RESULT_FIRST_USER = 1;

    protected boolean hasGet304Status = false;

    /**
     * 隐藏对象
     *
     * @param resId
     */
    /**
     * 次此方法在onCreateView之后调用才有用
     *
     * @param resId
     */
    public void hide(int resId) {
        if (getView() != null) {
            setV(getView().findViewById(resId), View.GONE);
        }
    }

    public void hide(View view, int resId) {
        setV(view.findViewById(resId), View.GONE);
    }

    /**
     * 次此方法在onCreateView之后调用才有用
     *
     * @param resId
     */
    public void show(int resId) {
        if (getView() != null) {
            setV(getView().findViewById(resId), View.VISIBLE);
        }
    }

    public void show(View view, int resId) {
        setV(view.findViewById(resId), View.VISIBLE);
    }

    private void setV(View v, int value) {
        if (v != null) {
            v.setVisibility(value);
        }
    }


    /**
     * 初始化列表。调用此方法后，系统会按需自动显示“更多”按钮。 子类需要覆盖 refreshData()
     *
     * @param listView
     * @param adapter
     * @param criteria
     */

    private MyRow lists = new MyRow();


    /**
     * 初始化 ListView, 以便统一处理更多翻页和无数据文字显示情况
     *
     * @param listView 指定的ListView
     * @param adapter  数据适配器
     * @param criteria 查询条件，要翻页时必须有此参数
     * @param op       对应ListView查询数据的远程方法
     */
    protected void initList(ListView listView, ArrayAdapter<?> adapter,
                            Criteria criteria, Class<?> op) {
        initList(listView, adapter, criteria, op, 0);
    }

    /**
     * 初始化 ListView, 以便统一处理更多翻页和无数据文字显示情况
     *
     * @param listView 指定的ListView
     * @param adapter  数据适配器
     * @param op       对应ListView查询数据的远程方法
     */

    protected void initList(ListView listView, ArrayAdapter<?> adapter,
                            Class<?> op) {
        initList(listView, adapter, null, op, 0);
    }

    /**
     * 初始化 ListView, 以便统一处理更多翻页和无数据文字显示情况
     *
     * @param listView  指定的ListView
     * @param adapter   数据适配器
     * @param op        对应ListView查询数据的远程方法
     * @param noDataTip 无数据时显示的自定义文字，资源ID
     */
    protected void initList(ListView listView, ArrayAdapter<?> adapter,
                            Class<?> op, int noDataTip) {
        initList(listView, adapter, null, op, noDataTip);
    }

    /**
     * 初始化 ListView, 以便统一处理更多翻页和无数据文字显示情况
     *
     * @param listView  指定的ListView
     * @param adapter   数据适配器
     * @param criteria  查询条件，要翻页时必须有此参数
     * @param op        对应ListView查询数据的远程方法
     * @param noDataTip 无数据时显示的自定义文字，资源ID
     */
    protected void initList(ListView listView, ArrayAdapter<?> adapter,
                            Criteria criteria, Class<?> op, int noDataTip) {
        View footer = getLayoutInflater().inflate(R.layout.list_footer, null);
//        footer.setOnClickListener(clickListener);
        ListObject lo = new ListObject();
        lo.adapter = adapter;
        lo.listView = listView;
        lo.footer = footer;
        lo.criteria = criteria;
        lo.noDataTip = noDataTip;
        lists.put(op.getName(), lo);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(footer);// 防止重复添加footer
        } else {
            hide(R.id.no_data);
            hide(R.id.more_data);
        }
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new MyOnScrollListener(this, lo));
    }

    protected void addEmptyView(ListView listView) {
        LayoutInflater inflater = getLayoutInflater();
        View emptyView = inflater.inflate(R.layout.list_empty_view, null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setEmptyView(emptyView);
        hide(R.id.emptyview);
    }

    private void displayFooter(final int currTotal, final ListObject lo) {
        hide(R.id.no_data);
        hide(R.id.more_data);
        lo.hasMore = false;


        if (currTotal == 0 && lo.adapter.getCount() == 0) {
            if (lo.noDataTip > 0) {
                UI.setEText(lo.footer, R.id.no_data, lo.noDataTip);
            }
            show(R.id.emptyview);
            show(R.id.no_data);
        } else if (lo.criteria != null && currTotal == lo.criteria.PageSize) {
            /* 2014-07-02 暂时用拖动刷新 */
            // show(footer,R.id.more_data);
            lo.hasMore = true;
        }
    }

    public void moreData(ListObject lo) {
        if (lo.hasMore) {
            show(lo.footer, R.id.more_data);
            lo.criteria.PageIndex += 1;
            refreshData();
        }
    }


    /**
     * 数据列表的滚动事件处理方法，项目子类覆盖此方法 本 Activity 使用了 initList()方法进行初始化
     *
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    public void specificOnScroll(AbsListView listView,
                                 final int firstVisibleItem, final int visibleItemCount,
                                 final int totalItemCount) {
    }

    /**
     * 刷新数据
     *
     * @param clearData ，清除现有数据
     */
    public void refreshData(boolean clearData, Class<?> op) {
        if (clearData) {
            ListObject lo = (ListObject) lists.get(op.getName());
            if (lo != null) {
                lo.adapter.clear();// clear data since criteria changed
                if (lo.criteria != null) {
                    lo.criteria.PageIndex = C.PageIndex;
                    hide(lo.footer, R.id.no_data);
                    hide(lo.footer, R.id.more_data);
                }
            }
        }
        refreshData();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1652) {
                try {
                    Class cla = Class.forName("com.clubank.knightclub.own.LoginActivity");
                    openIntent(cla, "登录");
                    MyApplication app = (MyApplication) getActivity().getApplication();
                    app.finishAllActivites();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 远程调用虚方法，子类需要覆盖
     *
     * @param op
     * @param result
     */
    public void onPostExecute(Class<?> op, Result result) {

        if (result.code == BRT.AUTH_ERROR.getCode()) {
            if (!hasGet304Status) {
                hasGet304Status = true;
                UI.showToast(getContext(), getContext().getString(R.string.please_relogin));
                handler.sendEmptyMessageDelayed(1652, 2000);
//                try {
//                    Class cla = Class.forName("com.clubank.knightclub.own.LoginActivity");
//                    openIntent(cla, "登录");
//                    MyApplication app = (MyApplication) getActivity().getApplication();
//                    app.finishAllActivites();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
            }
            return;
        }

        if (result.code == RT.SUCCESS) {
            ListObject lo = (ListObject) lists.get(op.getName());
            if (lo != null) {
                MyData d = (MyData) result.obj;
                displayFooter(d.size(), lo);// display more data/no data
            }
        }
    }


    public void setHeaderTitle(int resId) {
        setHeaderTitle(getString(resId));
    }

    public void setHeaderTitle(CharSequence title) {
        if (title != null) {
            if (getView() != null) {
                TextView tvTitle = getView().findViewById(R.id.header_title);
                if (tvTitle != null) {
                    tvTitle.setText(title);
                }
            }
        }
    }


    public void openIntent(Class<?> clazz, CharSequence title) {
        openIntent(clazz, title, null);
    }

    public void openIntent(Class<?> clazz, CharSequence title, Bundle extras) {
        openIntent(clazz, title, extras, 0);
    }

    public void openIntent(Class<?> clazz, CharSequence title, Bundle extras,
                           int requestCode) {
        Intent intent = new Intent(getContext(), clazz);
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.putExtra("title", title.toString().replaceAll("\n", " "));
        if (requestCode > 0) {
            startActivityForResult(intent, requestCode);
        } else {
            startActivity(intent);
        }
        getActivity().overridePendingTransition(R.anim.forward_enter, R.anim.forward_exit);

    }

    public void openIntent(Class<?> clazz, int resId) {
        openIntent(clazz, getText(resId), null);
    }

    public void openIntent(Class<?> clazz, int resId, int requestCode) {
        openIntent(clazz, getText(resId), null, requestCode);
    }

    public void openIntent(Class<?> clazz, int resId, Bundle extras,
                           int requestCode) {
        openIntent(clazz, getText(resId), extras, requestCode);
    }

    /**
     * 打开新Activity
     *
     * @param clazz Activity
     */
    public void openIntent(Class<?> clazz, int resId, Bundle b) {
        openIntent(clazz, getText(resId), b);
    }

    /**
     * 打开新的Fragment 并将当前Fragment添加至回退栈
     */
    public void startToFragment(Context context, int container, Fragment newFragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = null;
        if (manager != null) {
            transaction = manager.beginTransaction();
            transaction.replace(container, newFragment);
            transaction.addToBackStack(context.getClass().getName());//将Fragment添加到回退栈中
            transaction.commit();
        }
    }

    /**
     * 一层层回退Fragment
     * <p>
     * popBackStack(); 清除回退栈中栈顶的Fragment
     * popBackStack(String tag, int i );
     * <p>
     * 如果i=0,回退到该tag所对应的Fragment层
     * 如果i=FragmentManager.POP_BACK_STACK_INCLUSIVE,回退到该tag所对应的Fragment的上一层
     * <p>
     * <p>
     * popBackStackImmediate() 立即清除回退栈中栈顶Fragment
     * popBackStackImmediate(String tag, int i );
     * <p>
     * 如果tag=null,i=0,会弹出回退栈中最上层的那一个fragment
     * 如果i=FragmentManager.POP_BACK_STACK_INCLUSIVE,会弹出所有回退栈中的fragment
     * <p>
     * <p>
     * getBackStackEntryCount(); 获取回退栈中Fragment的个数
     * getBackStackEntryAt(int index) 获取回退栈中该索引值下的Fragment
     */
    public void backFragment() {
        Objects.requireNonNull(getActivity()).getFragmentManager().popBackStack();
    }

    public void dateSet(View view, int year, int month, int day) {

    }

    public void timeSet(View view, int hour, int minute) {
    }

    public void listSelected(View view, int index) {
    }

    public void mlistSelected(View view, boolean[] b) {
    }

    public void refreshData() {
    }

    public void processDialogOK(int type, Object tag) {
    }

    public void menuSelected(View view, int index) {
    }

    public void selectedContact(ContactInfo contact) {
    }

    public void onClick(View view) {
    }

    public String checkNull(String s) {
        if (null == s || s.length() < 1 || s.equals("null") || TextUtils.isEmpty(s)) {
            return "";
        } else {
            return s;
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onHide();
        } else {
            onShow();
        }
    }

    protected void onHide() {
    }

    protected void onShow() {
    }


    protected SharedPreferences getSp() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(C.APP_ID,
                Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    /**
     * 保存sp
     *
     * @param key
     * @param value 值为null时,相当于清除这个key对应的值
     */
    public void saveSetting(String key, @Nullable String value) {
        getSp().edit().putString(key, value).apply();
    }

    public void saveSetting(String key, @Nullable Set<String> value) {
        getSp().edit().putStringSet(key, value).apply();
    }


    public void saveSetting(String key, boolean value) {
        getSp().edit().putBoolean(key, value).apply();
    }

    public void saveSetting(String key, float value) {
        getSp().edit().putFloat(key, value).apply();
    }

    public void saveSetting(String key, int value) {
        getSp().edit().putInt(key, value).apply();
    }

    public void saveSetting(String key, long value) {
        getSp().edit().putLong(key, value).apply();
    }

    public void removeSetting(String key) {
        getSp().edit().remove(key).apply();
    }

    public void clearSetting() {
        getSp().edit().clear().apply();
    }


    /**
     * 获取Sp的值
     *
     * @param key
     * @param defValue
     * @return
     */
    public String getSPString(String key, String defValue) {
        return getSp().getString(key, defValue);
    }

    public Set<String> getSPStringSet(String key, Set<String> defValue) {
        return getSp().getStringSet(key, defValue);
    }

    public boolean getSPBoolean(String key, boolean defValue) {
        return getSp().getBoolean(key, defValue);
    }

    public float getSPFloat(String key, float defValue) {
        return getSp().getFloat(key, defValue);
    }

    public int getSPInt(String key, int defValue) {
        return getSp().getInt(key, defValue);
    }

    public long getSPLong(String key, long defValue) {
        return getSp().getLong(key, defValue);
    }


    public void setImage(ImageView imageView, Object res) {
        GlideUtil.getInstance().setImage(getContext(), imageView, res, 0);
    }

    public void setImage(ImageView imageView, Object res, int loadingImg) {
        GlideUtil.getInstance().setImage(getContext(), imageView, res, loadingImg);
    }


    public void setEText(TextView tv, CharSequence charSequence) {
        if (tv != null) {
            tv.setText(charSequence);
        }
    }

    public void setEText(int id, CharSequence charSequence) {
        TextView view = getView().findViewById(id);
        if (view != null) {
            view.setText(charSequence);
        }
    }

    public void setEText(TextView tv, int stringId) {
        if (tv != null) {
            tv.setText(getContext().getString(stringId));
        }
    }

    public void setEText(int id, int stringId) {
        TextView view = getView().findViewById(id);
        setEText(view, stringId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        onCreateView(view, container, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onShow();
    }

    public void onCreateView(View view, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    }

    protected abstract int getLayoutId();


}
