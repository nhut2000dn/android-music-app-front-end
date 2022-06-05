package com.client.mymusic.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.activities.MainActivity;
import com.client.mymusic.activities.PersonalPlaylistActivity;
import com.client.mymusic.activities.PersonalPlaylistSongsActivity;
import com.client.mymusic.activities.PlaylistSongsActivity;
import com.client.mymusic.adapters.PersonalPlaylistHotAdapter;
import com.client.mymusic.adapters.PlaylistHotAdapter;
import com.client.mymusic.adapters.SlidershowAdapter;
import com.client.mymusic.adapters.TopSongAdapter;
import com.client.mymusic.entities.Audio;
import com.client.mymusic.utils.MediaPlayerService;
import com.client.mymusic.utils.StorageUtil;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class HomeFragment extends Fragment implements  SlidershowAdapter.OnListenerRow, PlaylistHotAdapter.OnListenerRow, PersonalPlaylistHotAdapter.OnListenerRow, TopSongAdapter.OnListenerRow, TopSongAdapter.OnListenerMenuOption  {

    @BindView(R.id.imageSlider)
    SliderView sliderView;

    @BindView(R.id.rv_playlist_hot)
    RecyclerView rvPlayliistHot;

    @BindView(R.id.rv_personal_playlist_hot)
    RecyclerView rvPersonalPlayliistHot;

    @BindView(R.id.rv_top_song)
    RecyclerView rvTopSong;

    private OnPlayTopSongListener mListener;

    private OnPlaySlideshowListener mListenerSlideshow;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        SetAdapterTask setAdapterTask = new SetAdapterTask();
        setAdapterTask.execute();
        return view;
    }

    @Override
    public void onClickRow(int position, String action) {
        switch (action) {
            case "SlideShow":
                break;
            case "PlaylistHot":
                Intent intent = new Intent(getActivity(), PlaylistSongsActivity.class);
                intent.putExtra("id", MainActivity.listPlaylistsHot.get(position).getId());
                intent.putExtra("name", MainActivity.listPlaylistsHot.get(position).getName());
                startActivityForResult(intent, 1);
                Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
                break;
            case "PersonalPlaylistHot":
                Intent intents = new Intent(getActivity(), PersonalPlaylistSongsActivity.class);
                intents.putExtra("id", MainActivity.listPersonalPlaylistsHot.get(position).getId());
                intents.putExtra("name", MainActivity.listPersonalPlaylistsHot.get(position).getName());
                intents.putExtra("otherUser", true);
                startActivityForResult(intents, 1);
                Objects.requireNonNull(getActivity()).overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
                break;
            case "TopSong":
                mListener.onClickPlayTopSong(position);
                break;
        }
    }

    public interface OnPlayTopSongListener {
        void onClickPlayTopSong(int position);
    }

    public interface OnPlaySlideshowListener {
        void onClickSlideshowListener(int position);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnPlayTopSongListener)context;
            this.mListenerSlideshow = (OnPlaySlideshowListener)context;
        }
        catch (final ClassCastException e) {
        }
    }

    @Override
    public void onClickMenuOption(int position, View view) {
        ImageView btnMenuOption = (ImageView) view.findViewById(R.id.menu_option);
        PopupMenu popup = new PopupMenu(getActivity(), btnMenuOption);

        popup.getMenuInflater().inflate(R.menu.menu_option_song, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.play_next) {
                    Toast.makeText(getContext(),"You Clicked Play next : " + position, Toast.LENGTH_SHORT).show();
                } else if (item.getItemId() == R.id.add_to_playlist) {
                    Intent intent = new Intent(getContext(), PersonalPlaylistActivity.class);
                    intent.putExtra("action", "add_to_playlist");
                    intent.putExtra("id", MainActivity.listTopSong.get(position).getId());
                    intent.putExtra("name", MainActivity.listTopSong.get(position).getTitle());
                    startActivityForResult(intent, 1);
                    getActivity().overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
                }
                return true;
            }
        });

        popup.show();
    }

    private void setUpAdapterSlideshow() {

        SlidershowAdapter adapter = new SlidershowAdapter(getContext(), MainActivity.listSlideshows, this);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.DROP);
        //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);

        sliderView.setAutoCycleDirection(SliderView.KEEP_SCREEN_ON);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

    }

    public void setUpAdapterPlaylistHot() {
        PlaylistHotAdapter themeSongAdapter = new PlaylistHotAdapter(MainActivity.listPlaylistsHot, this);

        RecyclerView.LayoutManager RecyclerViewLayoutManager
                = new LinearLayoutManager(
                getContext());

        // Set LayoutManager on Recycler View
        rvPlayliistHot.setLayoutManager(
                RecyclerViewLayoutManager);

        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        rvPlayliistHot.setLayoutManager(HorizontalLayout);

        rvPlayliistHot.setAdapter(themeSongAdapter);
    }

    public void setUpAdapterPersonalPlaylistHot() {
        PersonalPlaylistHotAdapter personalPlaylistHotAdapter = new PersonalPlaylistHotAdapter(MainActivity.listPersonalPlaylistsHot, this);

        RecyclerView.LayoutManager RecyclerViewLayoutManager
                = new LinearLayoutManager(
                getContext());

        // Set LayoutManager on Recycler View
        rvPersonalPlayliistHot.setLayoutManager(
                RecyclerViewLayoutManager);

        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        rvPersonalPlayliistHot.setLayoutManager(HorizontalLayout);

        rvPersonalPlayliistHot.setAdapter(personalPlaylistHotAdapter);
    }

    public void setUpAdapterTopSong() {
        TopSongAdapter themeSongAdapter = new TopSongAdapter(MainActivity.listTopSong, this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvTopSong.setNestedScrollingEnabled(false);
        rvTopSong.setLayoutManager(layoutManager);
        rvTopSong.setAdapter(themeSongAdapter);
    }

    private class SetAdapterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setUpAdapterSlideshow();
            setUpAdapterPlaylistHot();
            setUpAdapterTopSong();
            setUpAdapterPersonalPlaylistHot();
        }
    }

}
