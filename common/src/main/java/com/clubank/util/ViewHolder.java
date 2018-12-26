package com.clubank.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

public class ViewHolder {
    private SparseArray<View> mViews;   //存储ListView 的 item中的View
    private View item;                  //存放convertView
    private int position;               //游标
    private Context context;            //Context上下文

    //构造方法，完成相关初始化
    private ViewHolder(Context context, ViewGroup parent, int layoutRes) {
        mViews = new SparseArray<>();
        this.context = context;
        View convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        convertView.setTag(this);
        item = convertView;
    }

    //绑定ViewHolder与item
    public static ViewHolder bind(Context context, View convertView, ViewGroup parent,
                                  int layoutRes, int position) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder(context, parent, layoutRes);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.item = convertView;
        }
        holder.position = position;
        return holder;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        T t = (T) mViews.get(id);
        if (t == null) {
            t = (T) item.findViewById(id);
            mViews.put(id, t);
        }
        return t;
    }


    /**
     * 获取当前条目
     */
    public View getItemView() {
        return item;
    }

    /**
     * 获取条目位置
     */
    public int getItemPosition() {
        return position;
    }

    /**
     * 设置文字
     */
    public ViewHolder setText(int id, CharSequence text) {
        View view = getView(id);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        }
        return this;
    }

    /**
     * 设置文字颜色
     */
    public ViewHolder setEColor(int id, int color) {
        View view = getView(id);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(context.getResources().getColor(color));
        }
        return this;
    }

    /**
     * 隐藏控件
     */
    public void hide( int resId) {
        View view = getView(resId);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 半隐藏控件
     */
    public void invisable(int resId) {
        View view = getView(resId);
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示控件
     */
    public void show(int resId) {
        View view =getView(resId);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }




    public ViewHolder setImage(int id, Object model , boolean isCircle) {

        View view = getView(id);
        if (view instanceof ImageView) {
            GlideUtil.getInstance().setImage(context, (ImageView) view, model, isCircle);
        }
        return this;
    }

    public ViewHolder setImage(int id, Object model , int defaultResId) {

        View view = getView(id);
        if (view instanceof ImageView) {
            GlideUtil.getInstance().setImage(context, (ImageView) view, model, defaultResId);
        }
        return this;
    }



    /**
     * 设置hint
     */
    public ViewHolder setHint(int id, CharSequence hint) {
        View view = getView(id);
        if (view instanceof TextView) {
            ((TextView) view).setHint(hint);
        }
        return this;
    }

    /**
     * 设置图片
     */
    public ViewHolder setImageResource(int id, int drawableRes) {
        View view = getView(id);
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(drawableRes);
        } else {
            view.setBackgroundResource(drawableRes);
        }
        return this;
    }

    public ViewHolder setImageBitmap(int id, Bitmap bm) {
        View view = getView(id);
        if (view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(bm);
        }
        return this;
    }


    /**
     * 设置点击监听
     */
    public ViewHolder setOnClickListener(int id, View.OnClickListener listener) {
        getView(id).setOnClickListener(listener);
        return this;
    }
//        public ViewHolder setOnClickListener(int id, IView.OnClickListener listener) {
//            getView(id).setOnClickListener(listener);
//            return this;
//        }


    public ViewHolder setSpinerAdapter(int id, SpinnerAdapter adapter) {
        View view = getView(id);
        if (view instanceof Spinner) {
            ((Spinner) view).setAdapter(adapter);
        }
        return this;
    }

    public ViewHolder setOnSpinerItemSelectedListener(int id, AdapterView.OnItemSelectedListener listener) {
        View view = getView(id);
        if (view instanceof Spinner) {
            ((Spinner) view).setOnItemSelectedListener(listener);
        }
        return this;
    }


    public ViewHolder setSpinerSelection(int id, int position) {
        View view = getView(id);
        if (view instanceof Spinner) {
            ((Spinner) view).setSelection(position);
        }
        return this;
    }

    public ViewHolder setSpinerEnable(int id, boolean enable) {
        View view = getView(id);
        if (view instanceof Spinner) {
            ((Spinner) view).setEnabled(enable);
        }
        return this;
    }


    /**
     * 设置可见
     */
    public ViewHolder setVisibility(int id, int visible) {
        getView(id).setVisibility(visible);
        return this;
    }

    /**
     * 设置选中
     */
    public ViewHolder setSelected(int id, boolean isChecked) {
        getView(id).setSelected(isChecked);
        return this;
    }

    public ViewHolder setOnTouchListener(int id, View.OnTouchListener touchListener) {
        View view = getView(id);
        if (view instanceof EditText) {
            ((EditText) view).setOnTouchListener(touchListener);
        }
        return this;
    }

    public ViewHolder addTextChangedListener(int id, TextWatcher textWatcher) {
        View view = getView(id);
        if (view instanceof EditText) {
            ((EditText) view).addTextChangedListener(textWatcher);
        }
        return this;
    }

    //设置焦点监听，当获取到焦点的时候才给它设置内容变化监听解决卡的问题
    public ViewHolder setOnFocusChangeListener(int id, View.OnFocusChangeListener onFocusChangeListener) {
        View view = getView(id);
        if (view instanceof EditText) {
            ((EditText) view).setOnFocusChangeListener(onFocusChangeListener);
        }
        return this;
    }

    /**
     * 设置标签
     */
    public ViewHolder setTag(int id, Object obj) {
        getView(id).setTag(obj);
        return this;
    }

    public ViewHolder setChecked(int id, boolean isChecked) {
        View view = getView(id);
        if (view instanceof CheckBox) {
            ((CheckBox) view).setChecked(isChecked);
        } else if (view instanceof Switch) {
            ((Switch) view).setChecked(isChecked);
        } else if (view instanceof RadioButton) {
            ((RadioButton) view).setChecked(isChecked);
        }
        return this;
    }


    public ViewHolder setOnCheckedChangeListener(int id, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        View view = getView(id);
        if (view instanceof CheckBox) {
            ((CheckBox) view).setOnCheckedChangeListener(onCheckedChangeListener);
        } else if (view instanceof Switch) {
            ((Switch) view).setOnCheckedChangeListener(onCheckedChangeListener);
        } else if (view instanceof RadioButton) {
            ((RadioButton) view).setOnCheckedChangeListener(onCheckedChangeListener);
        }

        return this;
    }


}