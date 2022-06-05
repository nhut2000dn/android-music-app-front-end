package com.client.mymusic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.activities.MainActivity;
import com.client.mymusic.activities.PlaylistsActivity;
import com.client.mymusic.adapters.ThemeAdapter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThemeMusicFragment extends Fragment implements ThemeAdapter.OnListenerRow  {

    @BindView(R.id.rv_theme)
    RecyclerView rvTheme;

    public ThemeMusicFragment() {
        // Required empty public constructor
    }

    public static ThemeMusicFragment newInstance(String param1, String param2) {
        ThemeMusicFragment fragment = new ThemeMusicFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_theme_music, container, false);

        ButterKnife.bind(this, view);

        setUpAdapter();

        return view;
    }


    @Override
    public void onClickRow(int position) {
        Intent intent = new Intent(getActivity(), PlaylistsActivity.class);
        intent.putExtra("id", MainActivity.listTheme.get(position).getId());
        intent.putExtra("name", MainActivity.listTheme.get(position).getName());
        startActivityForResult(intent, 1);
        Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
    }

    private void setUpAdapter() {
        ThemeAdapter themeSongAdapter = new ThemeAdapter(MainActivity.listTheme, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvTheme.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvTheme.setAdapter(themeSongAdapter);
    }
}
