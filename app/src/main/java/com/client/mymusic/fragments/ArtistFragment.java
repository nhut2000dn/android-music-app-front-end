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
import com.client.mymusic.activities.ArtistDetailsActivity;
import com.client.mymusic.activities.MainActivity;
import com.client.mymusic.adapters.ArtistAdapter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistFragment extends Fragment implements ArtistAdapter.OnListenerRow {

  @BindView(R.id.rv_artists)
  RecyclerView rvArtists;

  public ArtistFragment() {
  }

  public static ArtistFragment newInstance(String param1, String param2) {
    ArtistFragment fragment = new ArtistFragment();
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
    View view = inflater.inflate(R.layout.fragment_artist, container, false);

    ButterKnife.bind(this, view);

    setUpAdapter();

    return view;
  }

  private void setUpAdapter() {
    ArtistAdapter themeSongAdapter = new ArtistAdapter(MainActivity.listArtist, this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

    rvArtists.setLayoutManager(new GridLayoutManager(getContext(), 3));
    rvArtists.setAdapter(themeSongAdapter);
  }

  @Override
  public void onClickRow(int position) {
    Intent intent = new Intent(getContext(), ArtistDetailsActivity.class);
    intent.putExtra("id", MainActivity.listArtist.get(position).getId());
    intent.putExtra("name", MainActivity.listArtist.get(position).getName());
    intent.putExtra("sex", MainActivity.listArtist.get(position).getSex());
    intent.putExtra("avatar", MainActivity.listArtist.get(position).getAvatar());
    intent.putExtra("country", MainActivity.listArtist.get(position).getCountry());
    intent.putExtra("description", MainActivity.listArtist.get(position).getDescription());
    startActivityForResult(intent, 1);
    Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
  }
}
