package com.example.drops;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drops.utils.NewComment;
import com.example.drops.utils.NewGroup;
import com.example.drops.utils.SessionManager;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// The new group fragment pops up when the user pressed the create group button
// it handles creating the group and adding its members
public class NewGroupFragment extends Fragment {

    private static final String TAG = "NewGroupFragment"; // Tag for the fragment
    protected RecyclerView groupRecyclerView; // Recycler view for the members added to the group
    private GroupMembersAdapter groupMembersAdapter; // Adapter to hold the members added to the group
    private final List<String> userList = new ArrayList<>(); // List of users added to the group

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());

        // Initialize layout
        View rootView = inflater.inflate(R.layout.fragment_new_group, container, false);
        rootView.setTag(TAG);
        groupRecyclerView = rootView.findViewById(R.id.members_recyclerView);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_END);

        // Set up for displaying the list of members added to the group
        groupRecyclerView.setLayoutManager(layoutManager);
        groupMembersAdapter = new GroupMembersAdapter(getContext(), userList);
        groupRecyclerView.setAdapter(groupMembersAdapter);

        //Cancel create group action
        AppCompatButton cancelButton = rootView.findViewById(R.id.cancel_button);
        onClickCancelButton(cancelButton);

        //Add users to group using the edit textview
        EditText searchView = rootView.findViewById(R.id.search_view);
        addUsersToGroup(searchView, layoutManager);
        userList.add(sessionManager.getAccount().getName());

        //Create button functionality
        EditText groupName = rootView.findViewById(R.id.editTextTextPersonName2);
        AppCompatButton createButton = rootView.findViewById(R.id.create_button);
        onClickCreateButton(createButton, groupName);
        return rootView;
    }

    // Set up cancel button functionality
    private void onClickCancelButton(AppCompatButton cancelButton) {
        cancelButton.setOnClickListener(view -> {
            // Go back to dashboard fragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DashboardFragment dashboardFragment = new DashboardFragment();
            fragmentTransaction.replace(getId(), dashboardFragment);
            fragmentTransaction.commit();
        });
    }

    //Set up create button functionality
    private void onClickCreateButton(AppCompatButton createButton, EditText groupName) {
        createButton.setOnClickListener(view -> {
            // Send the new group to the database
            NewGroup newGroup = new NewGroup(groupName.getText().toString());
            newGroup.createGroup(getContext().getApplicationContext(), userList, (n) -> {
                // When the group is created, we go back to the dashboard
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                DashboardFragment dashboardFragment = new DashboardFragment();
                fragmentTransaction.replace(getId(), dashboardFragment);
                fragmentTransaction.commit();
            });
        });
    }

    // Add users to group by typing in the names
    private void addUsersToGroup(EditText searchView, RecyclerView.LayoutManager layoutManager) {
        searchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int key, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (key == KeyEvent.KEYCODE_ENTER)) {
                    // When the enter key is pressed, we add the user that is typed in
                    // to the list of users to add to the group
                    String user = searchView.getText().toString();
                    userList.add(user);
                    searchView.getText().clear();

                    // Update the recycler view
                    groupRecyclerView.setLayoutManager(layoutManager);
                    groupMembersAdapter = new GroupMembersAdapter(getContext(), userList);
                    groupRecyclerView.setAdapter(groupMembersAdapter);
                    return true;
                }
                return false;
            }
        });
    }
}