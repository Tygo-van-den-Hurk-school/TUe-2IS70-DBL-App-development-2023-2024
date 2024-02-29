package com.example.drops;

// TODO: Properly comment this

import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drops.utils.Comment;
import com.example.drops.utils.Drop;
import com.example.drops.utils.GPSHandler;
import com.example.drops.utils.Group;
import com.example.drops.utils.GroupApi;
import com.example.drops.utils.SessionManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import retrofit2.http.HEAD;

// This fragment is for the Dashboard view. Here the user can ses
public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private static final int DATASET_COUNT = 10;

    protected RecyclerView dropRecyclerView; // The view for displaying the drops
    protected RecyclerView commentRecyclerView; // The view for displaying reported comments
    protected RecyclerView groupRecyclerView; // The view for displaying the group buttons
    protected DropAdapter dropAdapter; // Adapter for showing the drops in a list
    protected ReportedCommentAdapter commentAdapter; // Adapter for reported comments in moderator view
    private GroupButtonAdapter groupButtonAdapter; // Adapter for the group buttons at the bottom of the screen
    private SessionManager sessionManager; // session manager

    private Location loc; // Location of the user
    private float range = 50; // Range of drop view is 50m from the user position

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO: Function is way too long, decompose

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        rootView.setTag(TAG);

        Button createGroupButton = rootView.findViewById(R.id.create_group_button);
        createGroupButton.setOnClickListener(view -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            NewGroupFragment createGroupFragment = new NewGroupFragment();
            fragmentTransaction.replace(getId(), createGroupFragment);
            fragmentTransaction.commit();
        });

        Button leaveGroupButton = rootView.findViewById(R.id.leave_group_button);
        leaveGroupButton.setOnClickListener(view -> {
            if(sessionManager.getGroup() != 1) {
                // Make an alert to ask for confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Leave group");
                builder.setMessage("Are you sure you want to leave the group?");

                // Set log out functionality to 'Confirm' button
                builder.setPositiveButton("Leave group", (dialog, which) -> {
                    Group.removeMember(getActivity().getApplicationContext(), sessionManager.getGroup(),
                            sessionManager.getAccount().getId(), (n) -> {
                                sessionManager.setGroup(1);
                                loadGroups();
                                loadDrops();
                            });
                });

                // Do nothing when canceling
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});

                // Create and show dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                Toast.makeText(getContext(), "Cannot leave public group", Toast.LENGTH_LONG).show();
            }
        });

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        TextView title = rootView.findViewById(R.id.nearby_notes);
        if(sessionManager.getModeratorMode()) {
            title.setText("Reported notes");
        } else {
            title.setText("Nearby notes");
        }

        if(!sessionManager.getModeratorMode()) {
            LinearLayout ll = rootView.findViewById(R.id.linear_layout_dashboard);
            RecyclerView rv = rootView.findViewById(R.id.comment_recyclerView);
            ll.removeView(rv);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        dropRecyclerView = (RecyclerView) getView().findViewById(R.id.drop_recyclerView);
        commentRecyclerView = (RecyclerView) getView().findViewById(R.id.comment_recyclerView);
        loadDrops();
        loadGroups();
        loadComments();
    }

    private Void createDropAdapter(List<Drop> drops) {
        SessionManager sessionManager = new SessionManager(getContext());
        dropAdapter = new DropAdapter(getActivity().getApplicationContext(), sessionManager.getDrops(), this::loadDrops);
        // BEGIN_INCLUDE(initializeRecyclerView)
        dropRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Set CustomAdapter as the adapter for RecyclerView.
        dropRecyclerView.setAdapter(dropAdapter);

        dropAdapter.setOnClickListener(new DropAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FullDropFragment fullDropFragment = new FullDropFragment(sessionManager.getDrops().get(position), () -> { loadDrops(); loadComments(); });
                fragmentTransaction.add(getId(), fullDropFragment);
                fragmentTransaction.commit();
            }
        });
        return null;
        // END_INCLUDE(initializeRecyclerView)
    }

    private void loadDrops() {
        boolean isModerator = sessionManager.getModeratorMode();

        GPSHandler gpsHandler = new GPSHandler();

        LocationManager locationManager = (LocationManager)
                getActivity().getSystemService(getContext().LOCATION_SERVICE);

        if (gpsHandler.checkPermission(getActivity().getApplicationContext(), getActivity())){
            loc = locationManager.getLastKnownLocation(GPSHandler.locationProvider);
        } else {
            System.exit(0);
        }

        LatLng pos;
        if (loc != null) {
            pos = new LatLng(loc.getLatitude(), loc.getLongitude());
        } else {
            pos = new LatLng(51.44868332980898, 5.490);
        }

        LatLngBounds area = gpsHandler.queryByRange(pos, range);

        sessionManager.setDrops(area.southwest.latitude, area.southwest.longitude,
                area.northeast.latitude, area.northeast.longitude, sessionManager.getGroup(),
                isModerator, this::createDropAdapter);
    }

    private void loadComments() {
        boolean isModerator = sessionManager.getModeratorMode();
        if(isModerator) {
            Comment.getReportedComments(getContext(), this::createCommentAdapter);
        }
    }

    private Void createCommentAdapter(List<Comment> comments) {
        SessionManager sessionManager = new SessionManager(getContext());
        commentAdapter = new ReportedCommentAdapter(getActivity().getApplicationContext(), comments, this::loadComments);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentRecyclerView.setAdapter(commentAdapter);
        return null;
    }

    private void loadGroups() {
        groupRecyclerView = getView().findViewById(R.id.group_recycler_view);
        sessionManager.setGroups(this::createGroupAdapter);
    }

    private void createGroupAdapter() {
        groupButtonAdapter = new GroupButtonAdapter(getContext(), sessionManager.getGroups(), this::loadDrops);
        groupRecyclerView.setAdapter(groupButtonAdapter);
    }
}
