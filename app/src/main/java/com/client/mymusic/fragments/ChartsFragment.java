package com.client.mymusic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.activities.ChartsSongActivity;
import com.client.mymusic.activities.MainActivity;
import com.client.mymusic.adapters.ChartsAdapter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartsFragment extends Fragment implements ChartsAdapter.OnListenerRow  {

  @BindView(R.id.rv_charts)
  RecyclerView rvCharts;

  public ChartsFragment() {
    // Required empty public constructor
  }

  public static ChartsFragment newInstance(String param1, String param2) {
    ChartsFragment fragment = new ChartsFragment();
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

    View view = inflater.inflate(R.layout.fragment_charts, container, false);

    ButterKnife.bind(this, view);
    setUpAdapter();
    return view;
  }

  private void setUpAdapter() {
    ChartsAdapter themeSongAdapter = new ChartsAdapter(MainActivity.listCharts, this, getContext());
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

    rvCharts.setLayoutManager(layoutManager);
    rvCharts.setAdapter(themeSongAdapter);
  }

  @Override
  public void onClickRow(int position) {
    Intent intent = new Intent(getActivity(), ChartsSongActivity.class);
    intent.putExtra("id", MainActivity.listCharts.get(position).getId());
    intent.putExtra("name", MainActivity.listCharts.get(position).getName());
    startActivityForResult(intent, 1);
    Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
  }
}
