package com.example.drops;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drops.utils.Comment;
import com.example.drops.utils.Drop;
import com.example.drops.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.http.HEAD;

// The drop adapter holds a list of drops which are displayed in a recycler view in the dashboard fragment
public class DropAdapter extends RecyclerView.Adapter<DropAdapter.ViewHolder> {

    public final List<Drop> localDataSet; // List of drops that the adapter holds
    private final Context dropContext; // Context of the fragment
    private DropAdapter.OnClickListener cardClickListener; // Listener for when the card is clicked
    private static Runnable refresh; // Refresh functionality of the dashboard

    // The view holder holds the view of a single drop in the list
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dropTitleTextView; // View that displays the drop title
        private final TextView dropContentTextView; // View that displays the drop content
        private final TextView dropDateTextView; // View that displays the time that the drop was posted
        private final TextView dropUpvoteTextView; // View that display the upvote count of the drop
        private Drop drop; // The drop that the view holds
        private Context context; // Context of the fragment


        public ViewHolder(@NonNull View view) {
            super(view);

            // Define click listener for the ViewHolder's View
            dropTitleTextView = view.findViewById(R.id.drop_title);
            dropContentTextView = view.findViewById(R.id.drop_content);
            dropDateTextView = view.findViewById(R.id.drop_time);
            dropUpvoteTextView = view.findViewById(R.id.upvote_count);
            Button buttonReport = view.findViewById(R.id.report_button);
            buttonReport.setOnClickListener(this::reportListener);
            Button buttonUpvote = view.findViewById(R.id.like_button);
            buttonUpvote.setOnClickListener(this::upvoteListener);
            Button buttonDownvote = view.findViewById(R.id.dislike_button);
            buttonDownvote.setOnClickListener(this::downvoteListener);
            Button buttonDelete = view.findViewById(R.id.delete_button);
            if(buttonDelete != null) {
                buttonDelete.setOnClickListener(this::deleteListener);
            }
        }

        // Listener for the report button
        private void reportListener(View view) {
            // Increment the report count of the drop
            Drop.updateReportCount(context, drop, 1);
        }

        // Listener for the upvote button
        private void upvoteListener(View view) {
            // Increment the upvote count of the drop
            Drop.updateUpvoteCount(context, drop, 1);
            dropUpvoteTextView.setText(Integer.toString(Integer.parseInt(dropUpvoteTextView.getText().toString()) + 1));
        }

        // Listener for the downvote button
        private void downvoteListener(View view) {
            // Decrement the upvote count of the drop
            Drop.updateUpvoteCount(context, drop, -1);
            dropUpvoteTextView.setText(Integer.toString(Integer.parseInt(dropUpvoteTextView.getText().toString()) - 1));
        }

        // Listener for the delete button
        private void deleteListener(View view) {
            // Delete the drop from the database
            Drop.deleteDrop(context, drop.getId(), (n) -> {
                // Refresh the database when drop is deleted
                refresh.run();
            });
        }

        public TextView getDropTitleTextView() {
            return dropTitleTextView;
        }
        public TextView getDropContentTextView() {
            return dropContentTextView;
        }
        public TextView getDropDateTextView() {
            return dropDateTextView;
        }
        public TextView getDropUpvoteTextView() {
            return dropUpvoteTextView;
        }
        public void setDrop(Drop drop) { this.drop = drop; }

        public void setContext(Context context) {
            this.context = context;
        }
    }

    public DropAdapter(Context context, List<Drop> dataSet, Runnable refresh) {
        dropContext = context;
        localDataSet = dataSet;
        this.refresh = refresh;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_drop, viewGroup, false);
        SessionManager sessionManager = new SessionManager(view.getContext());

        // If the user is not in moderator mode we remove the delete button
        if(!sessionManager.getModeratorMode()) {
            LinearLayout ll = view.findViewById(R.id.drop_buttons_linear_layout);
            AppCompatButton deleteButton = view.findViewById(R.id.delete_button);
            ll.removeView(deleteButton);
        }
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from the dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getDropTitleTextView().setText(localDataSet.get(position).getTitle());
        viewHolder.getDropContentTextView().setText(localDataSet.get(position).getMessage());
        viewHolder.getDropDateTextView().setText(convertTime(localDataSet.get(position).getTime()));
        viewHolder.getDropUpvoteTextView().setText(Integer.toString(localDataSet.get(position).getUpvotes()));
        viewHolder.setDrop(localDataSet.get(position));
        viewHolder.setContext(dropContext);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardClickListener != null) {
                    cardClickListener.onClick(viewHolder.getAdapterPosition());
                }
            }
        });

    }

    // Method to convert the time from UNIX time to a HH:mm format
    private String convertTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(calendar.getTime());
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void setOnClickListener(DropAdapter.OnClickListener onClickListener) {
        this.cardClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }
}

