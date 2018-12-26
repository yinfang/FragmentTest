package com.clubank.message;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clubank.club11test.R;
import com.clubank.util.MyData;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {

    private final MyData datas;
    private OnItemClickListener mItemClickListener;

    public MainRecyclerViewAdapter(MyData items) {
        datas = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.img.setImageResource(datas.get(position).getInt("img"));
        holder.desc.setText(datas.get(position).getString("desc"));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView img;
        public final TextView desc;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            img = view.findViewById(R.id.img);
            desc = view.findViewById(R.id.desc);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + desc.getText() + "'";
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    /**
     * 定义RecycleView item点击事件接口
     */
    public interface OnItemClickListener {
        // TODO: Update argument type and name
        void onItemClickListener(int postion);
    }
}
