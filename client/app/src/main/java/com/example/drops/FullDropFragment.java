package com.example.drops;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.drops.utils.Drop;
import com.example.drops.utils.NewComment;
import com.example.drops.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

// The full drop fragment displays a drop in a bigger view. It opens when you click a drop
// on the map or in the dashboard
public class FullDropFragment extends Fragment {

    private final Drop drop; // Drop that the fragment displays
    private CommentAdapter commentAdapter; // Adapter to hold list of comments of the drop
    private final Runnable refresh; // Functionality for refreshing the dashboard
    private TextView dropVoteTextView; // View that displays the text content of the drop

    public FullDropFragment(Drop drop, Runnable refresh) {
        this.drop = drop;
        this.refresh = refresh;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fullView = inflater.inflate(R.layout.fragment_full_drop, container, false);
        ((TextView) fullView.findViewById(R.id.full_drop_title)).setText(drop.getTitle());
        ((TextView) fullView.findViewById(R.id.full_drop_content)).setText(drop.getMessage());
        ((TextView) fullView.findViewById(R.id.upvote_count2)).setText(Integer.toString(drop.getUpvotes()));
        ((TextView) fullView.findViewById(R.id.full_drop_time)).setText(convertTime(drop.getTime()));
        ((TextView) fullView.findViewById(R.id.username_poster)).setText(drop.getUsername());

        dropVoteTextView = fullView.findViewById(R.id.upvote_count2);
        SessionManager sessionManager = new SessionManager(getContext());

        // Initialize the close button
        AppCompatButton closeButton = fullView.findViewById(R.id.close_button);
        onClickCloseButton(closeButton);

        //Upvote functionality
        AppCompatButton buttonUpvoteFull = fullView.findViewById(R.id.like_button);
        onClickUpvoteButton(buttonUpvoteFull);

        //Downvote functionality
        AppCompatButton buttonDownvoteFull = fullView.findViewById(R.id.dislike_button);
        onClickDownvoteButton(buttonDownvoteFull);

        //Report drop functionality
        AppCompatButton reportButton = fullView.findViewById(R.id.report_button_full);
        onClickReportButton(reportButton);

        //Delete drop functionality based on moderator status
        AppCompatButton deleteButton = fullView.findViewById(R.id.delete_button_full);
        onClickDeleteButton(sessionManager, fullView, deleteButton);

        //Set up the comment adapter for the full drop view.
        commentAdapter = new CommentAdapter(getActivity().getApplicationContext(), drop.getId());
        RecyclerView commentRecyclerView = fullView.findViewById(R.id.comment_recyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set commentAdapter as the adapter for RecyclerView.
        commentRecyclerView.setAdapter(commentAdapter);

        return fullView;
    }

    //Sets up functionality when the close drop button is clicked
    private void onClickCloseButton(AppCompatButton closeButton) {
        closeButton.setOnClickListener(view -> {
            // Closing the fragment loads the parent fragment and refreshes it
            refresh.run();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();
        });
    }

    // Sets up functionality when upvote button is clicked
    private void onClickUpvoteButton(AppCompatButton buttonUpvoteFull) {
        buttonUpvoteFull.setOnClickListener(view -> {
            // Increment the upvote count of the drop
            Drop.updateUpvoteCount(getContext(), drop, 1);
            dropVoteTextView.setText(Integer.toString(Integer.parseInt(dropVoteTextView.getText().toString()) + 1));
        });
    }

    // Sets up functionality when downvote button is clicked
    private void onClickDownvoteButton(AppCompatButton buttonDownvoteFull) {
        buttonDownvoteFull.setOnClickListener(view -> {
            // Decrement the upvote count of the drop
            Drop.updateUpvoteCount(getContext(), drop, -1);
            dropVoteTextView.setText(Integer.toString(Integer.parseInt(dropVoteTextView.getText().toString()) - 1));
        });
    }

    //Sets up functionality when report drop button is clicked
    private void onClickReportButton(AppCompatButton reportButton) {
        reportButton.setOnClickListener(view -> {
            // Increment the report count of the drop
            Drop.updateReportCount(getContext(), drop, 1);
        });
    }

    //Sets up functionality when delete drop button is clicked in the moderator view
    private void onClickDeleteButton(SessionManager sessionManager, View fullView, AppCompatButton deleteButton) {
        // Check whether the user is a moderator, or whether it is their own drop
        if (sessionManager.getModeratorMode() || sessionManager.getAccount().getId() == drop.getUserId()) {

            // Set the delete button functionality
            deleteButton.setOnClickListener(view -> {
                // Delete the drop from the database
                Drop.deleteDrop(getContext(), drop.getId(), (n) -> {
                    // When the drop is deleted we return to the parent fragment and refresh it
                    refresh.run();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(this);
                    fragmentTransaction.commit();
                });
            });
        } else { //if user is not moderator, delete button is removed from the UI
            LinearLayout ll = fullView.findViewById(R.id.linear_layout_fulldrop);
            ll.removeView(deleteButton);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Prevent clicking through the fragment into the behind activity
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        // Add comment under a drop
        EditText commentText = view.findViewById(R.id.comment_text_box);
        AppCompatImageButton commentButton = view.findViewById(R.id.comment_button);

        // Set the listener of the comment button
        commentButton.setOnClickListener((v) -> {

            SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());

            // Make the new comment
            NewComment newComment = new NewComment(commentText.getText().toString(),
                    drop.getId(), sessionManager.getAccount().getId(),
                    sessionManager.getAccount().getName(), 0);

            newComment.createComment(getContext(), (n) -> {
                // Update the adapter when a new comment is made
                commentAdapter.update();
            });

            // Clear the text area
            commentText.getText().clear();
        });
    }

    // Converts the UNIX time to a time in the format HH:mm
    private String convertTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(calendar.getTime());
    }
}