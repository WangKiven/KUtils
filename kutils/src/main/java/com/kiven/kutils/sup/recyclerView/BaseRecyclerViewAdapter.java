package com.kiven.kutils.sup.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 *
 * Created by kiven on 16/7/3.
 */
public abstract class BaseRecyclerViewAdapter<T extends BaseRecyclerViewHolder, H> extends RecyclerView.Adapter<T> {
    /**
     * 行点击事件
     */
    private OnItemClickListener<T> itemClickListener;
    protected List<H> mList;

    public BaseRecyclerViewAdapter(List<H> list, OnItemClickListener<T> itemClickListener) {
        mList = list;
        this.itemClickListener = itemClickListener;
    }
    public BaseRecyclerViewAdapter(List<H> list) {
        mList = list;
    }

    public BaseRecyclerViewAdapter(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public BaseRecyclerViewAdapter() {
    }

    @Override
    public final T onCreateViewHolder(ViewGroup parent, int viewType) {

        T holder = onCreateViewHolder2(parent, viewType);
        holder.setClickListener(itemClickListener);

        return holder;
    }

    public abstract T onCreateViewHolder2(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(T holder, int position) {
        onBindViewHolder2(holder, position);
        holder.setPosition(position);
    }

    public abstract void onBindViewHolder2(T holder, int position);

    @Override
    public int getItemCount() {
        return mList == null? 0: mList.size();
    }

    public H getItem(int position) {
        return mList.get(position);
    }
}