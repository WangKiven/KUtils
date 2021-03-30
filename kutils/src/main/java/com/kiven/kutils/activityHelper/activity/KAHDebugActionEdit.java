package com.kiven.kutils.activityHelper.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.kiven.kutils.R;
import com.kiven.kutils.activityHelper.KActivityHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;
import com.kiven.kutils.callBack.CallBack;
import com.kiven.kutils.callBack.Consumer;
import com.kiven.kutils.logHelper.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class KAHDebugActionEdit extends KActivityHelper {

    private List<DebugEntity> quickActions;
    private List<DebugEntity> otherActions;
    @Override
    public void onCreate(@NonNull KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        setContentView(R.layout.k_ah_debug_action_edit);
        initBackToolbar(R.id.toolbar, true);



        final ImageView iv_edit = findViewById(R.id.iv_edit);
        final RecyclerView rv_quick = findViewById(R.id.rv_quick);
        final RecyclerView rv_other = findViewById(R.id.rv_other);




        otherActions = new ArrayList<>(DebugView.customAction);
        quickActions = DebugConst.getQuickActions();
        otherActions.removeAll(quickActions);




        iv_edit.setOnClickListener(new View.OnClickListener() {
            boolean isEditing = false;
            @Override
            public void onClick(View v) {
                MyAdapter quickAdapter = (MyAdapter) rv_quick.getAdapter();
                MyAdapter otherAdapter = (MyAdapter) rv_other.getAdapter();

                if (isEditing) {
                    ImageViewCompat.setImageTintList(iv_edit, ContextCompat.getColorStateList(mActivity, R.color.design_default_color_secondary_variant));
                    quickAdapter.editType = 0;
                    otherAdapter.editType = 0;
                } else {
                    ImageViewCompat.setImageTintList(iv_edit, ContextCompat.getColorStateList(mActivity, R.color.design_default_color_error));
                    quickAdapter.editType = 2;
                    otherAdapter.editType = 1;
                }

                quickAdapter.notifyDataSetChanged();
                otherAdapter.notifyDataSetChanged();
                isEditing = !isEditing;
            }
        });


        rv_quick.setLayoutManager(new FlexboxLayoutManager(activity));
        rv_quick.setAdapter(new MyAdapter(quickActions, new Consumer<DebugEntity>() {
            @Override
            public void callBack(DebugEntity param) {
                if (quickActions.contains(param)) {
                    int position = quickActions.indexOf(param);
                    quickActions.remove(param);
                    DebugConst.saveQuickActions(quickActions);
                    rv_quick.getAdapter().notifyItemRemoved(position);

                    otherActions.add(param);
                    rv_other.getAdapter().notifyItemRemoved(otherActions.size() - 1);
                } else {
                    Toast.makeText(mActivity, "编辑异常", Toast.LENGTH_SHORT).show();
                }
            }
        }));
        new ItemTouchHelper(new ItemTouchHelperCallback(quickActions, new CallBack() {
            @Override
            public void callBack() {
                DebugConst.saveQuickActions(quickActions);
            }
        })).attachToRecyclerView(rv_quick);








        rv_other.setLayoutManager(new FlexboxLayoutManager(activity));
        rv_other.setAdapter(new MyAdapter(otherActions, new Consumer<DebugEntity>() {
            @Override
            public void callBack(DebugEntity param) {
                if (quickActions.size() >= DebugConst.maxQuickShow) {
                    Toast.makeText(mActivity, "已到上限", Toast.LENGTH_SHORT).show();
                } else {
                    quickActions.add(param);
                    DebugConst.saveQuickActions(quickActions);
                    rv_quick.getAdapter().notifyItemInserted(quickActions.size() - 1);

                    int position = otherActions.indexOf(param);
                    otherActions.remove(param);
                    rv_other.getAdapter().notifyItemRemoved(position);
                }
            }
        }));
        new ItemTouchHelper(new ItemTouchHelperCallback(otherActions, null)).attachToRecyclerView(rv_other);
    }

    private static class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        final List<DebugEntity> actions;
        final CallBack onChange;
        public ItemTouchHelperCallback(List<DebugEntity> actions, CallBack onChange) {
            this.actions = actions;
            this.onChange = onChange;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            KLog.i("getMovementFlags()");
            if (recyclerView.getLayoutManager() instanceof FlexboxLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            KLog.i("onMove()");
            //得到当拖拽的viewHolder的Position
            int fromPosition = viewHolder.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(actions, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(actions, i, i - 1);
                }
            }

            if (onChange != null) onChange.callBack();
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            KLog.i("onSwiped()" + direction);

        }
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        final List<DebugEntity> actions;
        final Consumer<DebugEntity> onEdit;
        int editType = 0; // 编辑类型：0：不可编辑，1：可添加，2：可删除

        public MyAdapter(@NonNull List<DebugEntity> actions, Consumer<DebugEntity> onEdit) {
            this.actions = actions;
            this.onEdit = onEdit;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(mActivity)
                    .inflate(R.layout.k_ah_debug_action_edit_item, parent, false)) {};
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            TextView text = holder.itemView.findViewById(R.id.text);
            ImageView iv_edit = holder.itemView.findViewById(R.id.iv_edit);

            final DebugEntity entity = actions.get(position);
            if (entity.isIcon()) {
                text.setBackgroundResource(entity.getResId());
                text.setText("");
            } else {
                text.setBackgroundResource(R.mipmap.k_bg_blank_circle);
                text.setText(entity.getText());
            }

            switch (editType) {
                case 0:
                    iv_edit.setVisibility(View.GONE);
                    break;
                case 1:
                    iv_edit.setVisibility(View.VISIBLE);
                    iv_edit.setImageResource(android.R.drawable.ic_input_add);
                    break;
                case 2:
                    iv_edit.setVisibility(View.VISIBLE);
                    iv_edit.setImageResource(android.R.drawable.presence_busy);
                    break;
            }

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editType == 0) {
                        entity.onClick(mActivity, v);
                    } else {
                        onEdit.callBack(entity);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return actions.size();
        }
    }
}
