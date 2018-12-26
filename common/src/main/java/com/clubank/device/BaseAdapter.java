package com.clubank.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.clubank.common.R;
import com.clubank.domain.C;
import com.clubank.util.GlideUtil;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;
import com.clubank.util.ViewHolder;

import java.text.ParseException;
import java.util.Date;

public abstract class BaseAdapter extends ArrayAdapter<MyRow> {

    protected BaseActivity a;

    private int item_layout;//布局ID
    private boolean interlaced; //item 交替变色
    private int listitem_odd_bg;
    private int listitem_even_bg;
    private BaseFragment fragment;
    private MyData data;
    protected  LayoutInflater inflater;
    private ViewHolder holder;
    private GlideUtil glideUtil = GlideUtil.getInstance();

    public BaseAdapter(BaseActivity a, int item_layout) {
        this(a, item_layout, null);
    }


    public void setInterlaced(boolean interlaced) {
        this.interlaced = interlaced;
    }

    public void setOddBackGround(int drawableId) {
        this.listitem_odd_bg = drawableId;
    }

    public void setEvenBackGround(int drawableId) {
        this.listitem_even_bg = drawableId;
    }

    public BaseAdapter(BaseActivity a, int item_layout, MyData data) {
        super(a, 0, data);
        this.item_layout = item_layout;
        this.a = a;
        this.data = data;
        inflater = (LayoutInflater) a
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public BaseFragment getFragment() {
        return fragment;
    }

    public BaseAdapter(BaseFragment fragment, int layout, MyData data) {
        this((BaseActivity) fragment.getActivity(), layout, data);
        this.fragment = fragment;
    }


    @Override
    public MyRow getItem(int position) {
        if (data != null) {
            return data.get(position);
        }
        return null;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = ViewHolder.bind(parent.getContext(), convertView, parent, item_layout, position);
        if (interlaced) {
            setInterlaced(position, convertView);
        }
        display(position, holder.getItemView(), getItem(position));
     /*   View  view=null;
        if(convertView!=null){
            view=convertView;
        }else{
            view= inflater.inflate(item_layout, null);
        }

        MyRow o = getItem(position);
        display(position, view, o);
        return view;*/
        return holder.getItemView();
    }

    //public abstract void display(int position, View v, MyRow row);

    protected void display(int position, View v, MyRow row) {

    }

    protected void display(int position, ViewHolder holder, MyRow row) {

    }

    /**
     * 设置交换色
     */
    private void setInterlaced(int position, View convertView) {

        if (position % 2 == 0) {
            if (listitem_odd_bg > 0) {
                convertView.setBackgroundResource(listitem_odd_bg);
            } else {
                convertView.setBackgroundResource(R.color.list_odd);
            }
        } else {
            if (listitem_even_bg > 0) {
                convertView.setBackgroundResource(listitem_even_bg);
            } else {
                convertView.setBackgroundResource(R.color.list_even);
            }
        }
    }


    //圆图
    protected void setImage(int resId, Object model, boolean isCircle) {
        View view = holder.getView(resId);
        if (view instanceof ImageView) {
            glideUtil.setImage(a, (ImageView) view, model, isCircle);
        }
    }


    //常规图
    protected void setImage(int resId, Object model) {
        View view = holder.getView(resId);
        if (view instanceof ImageView) {
            glideUtil.setImage(a, (ImageView) view, model);
        }
    }
    public void setImage(int resId, Object model, int defaultId) {

        holder.setImage(resId, model, defaultId);
    }


    /**
     * 格式化
     *
     * @param sDate
     * @return
     */
    protected String getMDate(String sDate) {
        String result = "";
        try {
            if (TextUtils.isEmpty(sDate) || sDate.length() < 11) {

            } else {
                Date date = C.df_yMdHm.parse(sDate);
                result = C.df_yMdHm.format(date);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 设置文本
     *
     * @param resId textView或Eidtext ID
     * @param value 内容
     */
    protected void setEText(int resId, CharSequence value) {
        holder.setText(resId, value);
    }

    /**
     * 设置文本颜色
     *
     * @param resId textView或Eidtext ID
     * @param color 颜色资源
     */
    protected void setEColor(int resId, int color) {
        holder.setEColor(resId, color);
    }

    /**
     * 隐藏控件
     *
     * @param resId 控件ID
     */
    protected void hide(int resId) {
        holder.hide(resId);
    }

    /**
     * 显示控件
     *
     * @param resId 控件ID
     */
    protected void show(int resId) {
        holder.show(resId);
    }

    protected void invisable(int resId) {
        holder.invisable(resId);
    }


    /**
     * 通过资源ID获取对应字符串
     *
     * @param resId 资源ID
     */
    protected String getString(int resId) {
        return a.getString(resId);
    }


    /**
     * * * * * * * * * * * * * * * * * 以下方法务删 兼容老项目 * * * * * * * * * * *
     */


    /**
     * 常规加载图片
     */
    protected void setImage(View v, int resId, Object model) {

        holder.setImage(resId, model, false);

    }

    /**
     * 圆图图片
     */
    protected void setImage(View v, int resId, Object model, boolean isCircle) {

        holder.setImage(resId, model, isCircle);

    }

    public void setImage(View v, int resId, Object model, int defaultId) {
        holder.setImage(resId, model, defaultId);
    }


    protected void setEText(View v, int resId, CharSequence value) {
        holder.setText(resId, value);


    }

    protected void setEColor(View v, int resId, int color) {
        holder.setEColor(resId, color);

    }


    protected void hide(View v, int resId) {
        holder.hide(resId);
    }


    protected void show(View v, int resId) {
        holder.show(resId);
    }

    protected void invisable(View v, int resId) {
        holder.invisable(resId);
    }

    /*** * * * * * * * * * * * * * * * * 以上方法务删 兼容老项目 * * * * * * * * * * * */


}
