package com.kiven.kutils.sup.recyclerView;

import android.support.v7.widget.RecyclerView;

/**
 * Created by kiven on 16/7/3.
 */
public interface OnItemClickListener<T extends RecyclerView.ViewHolder> {
    void onItemClick(T holder, int position);
}
