package com.example.drops;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

// The group members adapter holds the list of group members of a new groups.
// When creating a new group this displays the members you have already added
public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {

    private final Context groupContext; // Context of the fragment
    private final List<String> groupButtonList; // List of usernames that the adapter holds

    public GroupMembersAdapter(Context context, List<String> buttonList) {
        groupContext = context;
        groupButtonList = buttonList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(groupContext).inflate(R.layout.chip_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(groupButtonList.get(position));
    }

    @Override
    public int getItemCount() {
        return groupButtonList.size();
    }

    // The view holder holds the view of a single group member
    static class ViewHolder extends RecyclerView.ViewHolder {

        private final Chip groupMemberChip; // Chip that displays the username

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupMemberChip = itemView.findViewById(R.id.chip);
        }

        // When binding, set the text to the username
        public void bind(String text) {
            groupMemberChip.setText(text);
        }
    }
}
