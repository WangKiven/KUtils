package com.kiven.kutils.sup.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kiven on 16/7/3.
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    OnItemClickListener clickListener;
    int position;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public int getCurrentPosition() {
        return position;
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onItemClick(this, position);
        }
    }
}