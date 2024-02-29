package com.example.drops;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.drops.utils.Group;

import java.util.List;

// The group spinner adapter holds a list of groups which are displayed in a spinner
// in the new drop fragment.
public class GroupSpinnerAdapter extends ArrayAdapter<Group> {
    private Context context; // Context of the fragment
    private final List<Group> groups; // List of groups that the adapter holds

    public GroupSpinnerAdapter(Context context, int textViewResourceId, List<Group> groups) {
        super(context, textViewResourceId, groups);
        this.context = context;
        this.groups = groups;
    }

    @Override
    public int getCount(){
        return groups.size();
    }

    @Override
    public Group getItem(int position){
        return groups.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK); // Set the text color to black
        label.setText(getItem(position).getName()); // Set the text to the name of the group
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setText(getItem(position).getName()); // Set the text to the name of the group
        return label;
    }
}

