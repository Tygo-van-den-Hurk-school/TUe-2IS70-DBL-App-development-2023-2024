package com.example.drops;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drops.utils.Account;
import com.example.drops.utils.Comment;
import com.example.drops.utils.Drop;
import com.example.drops.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// The comment adapter holds the comments of a particular drops. It is displayed by the
// recycler view in the dashboard fragment.
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final int dropID; // ID for the drop that the comment adapter shows the comments of
    private List<Comment> comments; // List of comments to hold
    private Context context; // Context of fragment

    public CommentAdapter(Context context, int dropID) {
        this.context = context;
        this.dropID = dropID;
        this.comments = new ArrayList<>();
        update();
    }

    // Updates the comment list
    public void update() {
        // Gets comments from database
        Comment.getComments(context, dropID, comments -> {
            // Sets the new comment list when the call was successful
            this.comments = comments;
            notifyItemInserted(this.comments.size() - 1);
        });
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comment, parent, false);
        SessionManager sessionManager = new SessionManager(view.getContext());

        //If not in moderator mode, delete comment button becomes unavailable
        if(!sessionManager.getModeratorMode()) {
            LinearLayout ll = view.findViewById(R.id.comment_button_linear_layout);
            AppCompatButton deleteButton = view.findViewById(R.id.delete_comment_button);
            ll.removeView(deleteButton);
        }
        return new CommentViewHolder(view);
    }

    // Populates the comment view with data.
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int position) {
        // Set the data of the Comment view
        Comment comment = comments.get(position);
        commentViewHolder.commentText.setText(comment.getCommentMessage());
        commentViewHolder.userText.setText(Integer.toString(comment.getUser()));
        commentViewHolder.comment = comment;
        commentViewHolder.context = context;
        commentViewHolder.userText.setText(comment.getUsername());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    // Custom ViewHolder class for the comment RecyclerView.
    // The class displays a single comments, the adapter then gives a list of these to the recycler
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText; // Text view showing the content of the comment
        TextView userText; // Text view showing the username of the user that posted the comment
        Comment comment; // The comment the view holder is displaying
        private Context context; // Context of the fragment
        private final Button buttonReport; // Button for reporting the comment
        private final Button buttonDelete; // Button for deleting the comment

        //Initialize all buttons, textfields variables available in the comment view
        CommentViewHolder(View itemView) {
            super(itemView);

            // Set instance variables
            commentText = itemView.findViewById(R.id.comment_content);
            userText = itemView.findViewById(R.id.comment_username);
            buttonReport = (Button) itemView.findViewById(R.id.report_button);

            // Set report button listener
            buttonReport.setOnClickListener(this::reportListener);

            // We delete the delete button for regular comments, since it is
            // not in moderator mode
            buttonDelete = itemView.findViewById(R.id.delete_comment_button);
            LinearLayout ll = itemView.findViewById(R.id.comment_button_linear_layout);
            ll.removeView(buttonDelete);
        }

        // Listener to increase the report count for comments
        private void reportListener(View view) {
            Comment.updateReportCountComment(context, comment, 1);
        }

    }
}
