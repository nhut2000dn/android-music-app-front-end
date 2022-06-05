package com.client.mymusic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.client.mymusic.R;
import com.client.mymusic.activities.LoginActivity;
import com.client.mymusic.activities.PersonalPlaylistActivity;
import com.client.mymusic.activities.SongPersonalActivity;
import com.client.mymusic.utils.TokenManager;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalFragment extends Fragment {

    View view;

    public static CardView intentToPesonalSongs;

    public PersonalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_personal, container, false);

        ButterKnife.bind(this, view);

        intentToPesonalSongs = (CardView) view.findViewById(R.id.intent_to_personal_songs);
        intentToPesonalSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), SongPersonalActivity.class), 1);

                Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
            }
        });

        return view;
    }

    @OnClick(R.id.intent_to_playlists_personal)
    void intentToPlaylistsPersonal() {
        String accessToken = TokenManager.getToken().getAccessToken();
        if (accessToken != null) {
            Intent intent = new Intent(getActivity(), PersonalPlaylistActivity.class);
            intent.putExtra("action", "view_playlist");
            intent.putExtra("id", 0);
            intent.putExtra("name", "Playlists Personal");
            startActivityForResult(intent, 1);
            Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
        } else {
            startActivity(new Intent(getContext(), LoginActivity.class));
            Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
        }

    }

}
