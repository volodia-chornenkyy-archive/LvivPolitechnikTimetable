package com.temnoi.lvivpolitechniktimetable.ui.setup.group;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.temnoi.lvivpolitechniktimetable.R;
import com.temnoi.lvivpolitechniktimetable.model.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chornenkyy@gmail.com
 * @since 14.09.2016
 */

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private List<Group> items = new ArrayList<>();

    private int selectedItemPos = -1;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = items.get(position);

        if (selectedItemPos == position) {
            // TODO: 9/15/16 make background another color
        } else {
            // TODO: 9/15/16 make background default color
        }

        holder.tvName.setText(group.getShortName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(List<Group> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged(); // TODO: 15.09.2016 probably better to use DiffUtil
    }

    public Group getSelectedItem() {
        if (selectedItemPos >= 0) {
            return items.get(selectedItemPos);
        } else {
            return null;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View rootItemView;
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            rootItemView = itemView;

            itemView.setOnClickListener(this);

            tvName = (TextView) itemView.findViewById(R.id.tv_group_name);
        }

        @Override
        public void onClick(View view) {
            selectedItemPos = getAdapterPosition();
        }
    }
}
