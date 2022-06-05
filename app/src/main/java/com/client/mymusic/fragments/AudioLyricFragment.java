package com.client.mymusic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.client.mymusic.R;
import com.client.mymusic.entities.Audio;
import com.client.mymusic.utils.MediaPlayerService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioLyricFragment extends Fragment {

    @BindView(R.id.lyrics)
    TextView lyrics;


    private static ArrayList<Audio> audioList;
    private int audioIndex;

    public AudioLyricFragment() {
        // Required empty public constructor
    }

    public static AudioLyricFragment newInstance(String param1, String param2) {
        AudioLyricFragment fragment = new AudioLyricFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_lyric, container, false);

        ButterKnife.bind(this, view);

        lyrics.setText(MediaPlayerService.activeAudio.getLyrics());

        return view;
    }

}
