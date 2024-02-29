package com.example.drops;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drops.utils.Group;
import com.example.drops.utils.SessionManager;

import java.util.List;

// The group button adapter holds the list of groups that the user belongs to
// the dashboard fragment displays these at the button of the screen
// clicking on the buttons changes the group of which the drops are displayed
public class GroupButtonAdapter extends RecyclerView.Adapter<GroupButtonAdapter.ButtonViewHolder> {

    private final Context groupContext; // Context of the fragment
    private final List<Group> groupsList; // List of groups that the adapter holds
    private final Runnable reload; // Functionality to reload the dashboard

    public GroupButtonAdapter(Context context, List<Group> groupsList, Runnable reload) {
        groupContext = context;
        this.groupsList = groupsList;
        this.reload = reload;
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(groupContext).inflate(R.layout.group_button, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        // Bind the groups to their respective buttons
        // It sets the text of the button to the group name
        holder.bind(groupContext, groupsList.get(position).getName(), groupsList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    // The button view holder holds the view for a single group button
    class ButtonViewHolder extends RecyclerView.ViewHolder {

        private final Button groupButton; // Button of the view

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            groupButton = itemView.findViewById(R.id.group_button);
        }

        public void bind(Context context, String text, int groupId) {
            SessionManager sessionManager = new SessionManager(context);

            groupButton.setText(text); // Sets the text of the button
            groupButton.setOnClickListener((v) -> {
                // When pressing the button the group changes to the pressed group
                sessionManager.setGroup(groupId);

                // Reload dashboard to display the new drops
                reload.run();
            });
        }
    }
}
