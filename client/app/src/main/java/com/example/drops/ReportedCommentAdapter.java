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

// The reported comment adapter holds the reported comments for moderators. It is displayed by the
// recycler view in the dashboard fragment.
public class ReportedCommentAdapter extends RecyclerView.Adapter<ReportedCommentAdapter.CommentViewHolder> {

    private final List<Comment> comments; // List of comments to display
    private Context context; // Context of fragment
    private static Runnable refresh; // Refresh functionality of the dashboard

    public ReportedCommentAdapter(Context context, List<Comment> reportedComments, Runnable refresh) {
        // Set instance variables
        this.refresh = refresh;
        this.context = context;
        this.comments = reportedComments;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create the view holder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        // When binding the view holders, we set their instance variables
        // and set the TextViews to the correct text
        Comment comment = comments.get(position);
        holder.context = context;
        holder.commentText.setText(comment.getCommentMessage());
        holder.userText.setText(Integer.toString(comment.getUser()));
        holder.comment = comment;
        Account.getAccountById(context, comment.getUser(), (account) -> {
            holder.userText.setText(account.getName());
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    // The view holder class holds the view of a single comment, the adapter then has
    // an array of these views which the RecyclerView can display
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText; // view for content of the comment
        TextView userText; // view for username of the user that posted the comment
        private Context context; // context of fragment
        Comment comment; // the comment that the view holds

        CommentViewHolder(View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.comment_content);
            userText = itemView.findViewById(R.id.comment_username);

            // Set up report button
            Button buttonReport = (Button) itemView.findViewById(R.id.report_button);
            buttonReport.setOnClickListener(this::reportListener);

            // Set up delete button
            Button buttonDelete = (Button) itemView.findViewById(R.id.delete_comment_button);
            if(buttonDelete != null) {
                buttonDelete.setOnClickListener(this::deleteListener);
            }
        }

        // Listener for the report button, it increments the report count of the comment by one
        private void reportListener(View view) {
            Comment.updateReportCountComment(context, comment, 1);
        }

        // Listener for the delete button, it deletes the comment
        private void deleteListener(View view) {
            Comment.deleteComment(context, comment.getId(), (n) -> { refresh.run(); });
        }
    }
}
