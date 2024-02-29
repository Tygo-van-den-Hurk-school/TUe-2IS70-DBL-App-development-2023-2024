package com.example.drops;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drops.utils.SessionManager;

public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //Personalize username on settings fragment
        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        TextView userName = view.findViewById(R.id.username_settings);
        userName.setText(sessionManager.getAccount().getName());

        //Change moderator button based on moderator status
        AppCompatButton moderatorButton = view.findViewById(R.id.button3);
        handleModeratorButton(sessionManager, moderatorButton, view);

        return view;
    }

    //Handle the UI around the moderator button
    private void handleModeratorButton(SessionManager sessionManager, AppCompatButton moderatorButton, View view) {
        //If user has moderator access the moderator button toggles based on the selected mode
        if (sessionManager.getModeratorMode()) {
            moderatorButton.setBackground(getActivity().getDrawable(R.drawable.toggled_button));
        } else {
            moderatorButton.setBackground(getActivity().getDrawable(R.drawable.standard_button));
        }

        //If user does not have moderator access, button is removed from UI
        if (sessionManager.getAccount().getIsModerator() == 0) {
            LinearLayout ll = view.findViewById(R.id.linearLayoutSettings);
            ll.removeView(moderatorButton);
        }
    }
}