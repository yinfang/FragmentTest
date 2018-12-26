package com.clubank.message;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.clubank.club11test.R;
import com.clubank.device.BaseFragment;
import com.clubank.fragbacktest.FragBack2;
import com.clubank.util.MyData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ExcRecordFragment extends BaseFragment implements MainRecyclerViewAdapter.OnItemClickListener {
    private MyData datas;
    private int columnCount = 0;
    private View view;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExcRecordFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ExcRecordFragment newInstance(int columnCount) {
        ExcRecordFragment fragment = new ExcRecordFragment();
        Bundle args = new Bundle();
        args.putInt("count", columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isAdded() && getArguments() != null) {
            columnCount = getArguments().getInt("count");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.record_fragment;
    }

    @Override
    public void onCreateView(View view, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(view, container, savedInstanceState);
        this.view=view;
        initView();
    }

    private void initView() {
        context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount > 0 ? columnCount : 2));
        MainRecyclerViewAdapter adapter = new MainRecyclerViewAdapter(datas);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClickListener(int postion) {
        Toast.makeText(context, "点击了第" + postion + "项", Toast.LENGTH_SHORT).show();
        openIntent(FragBack2.class,"Fragment回退栈");
    }

    @Subscribe(sticky = true)//EventBus接受粘性事件
    public void getEventDatas(MyData datas) {
        this.datas = datas;
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
