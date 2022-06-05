package com.client.mymusic.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.activities.MainActivity;
import com.client.mymusic.adapters.AudioArtistAdapter;
import com.client.mymusic.adapters.PlaylistSongAdapter;
import com.client.mymusic.entities.Audio;
import com.client.mymusic.entities.AudioResponse;
import com.client.mymusic.utils.MediaPlayerService;
import com.client.mymusic.utils.StorageUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class AudioArtistFragment extends Fragment implements PlaylistSongAdapter.OnListenerRow {

    @BindView(R.id.name_artist_fragment_aritst)
    TextView nameArtist;

    @BindView(R.id.image_artist_fragment_aritst)
    RoundedImageView imageArtist;

    @BindView(R.id.rv_songs_artist)
    RecyclerView rvSongsArtist;

    private List<Audio> listArtistSong = new ArrayList<>();

    private List<Audio> audioListStorageUltil = new ArrayList<>();

    private int audioIndex;

    Call<AudioResponse> callArtistSong;

    private MediaPlayerService player;

    boolean serviceBound;

    public AudioArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_artist, container, false);

        ButterKnife.bind(this, view);
        nameArtist.setText(MediaPlayerService.activeAudio.getArtist());
//        Picasso.get().load(MediaPlayerService.activeAudio.getAvatarArtist())
//                .fit().placeholder(R.drawable.playholder_music).into(imageArtist);
        Picasso.get().load("http://192.168.1.117/music_offical_2/storage/app/public/" + MediaPlayerService.activeAudio.getAvatarArtist())
                .fit().placeholder(R.drawable.playholder_music).into(imageArtist);
        loadDataNetWord(MediaPlayerService.activeAudio.getIdArtist());
        SetAdapterTask setAdapterTask = new SetAdapterTask();
        setAdapterTask.execute();
        return view;
    }

    private void setUpAdapter() {
        AudioArtistAdapter themeSongAdapter = new AudioArtistAdapter(listArtistSong, this::onClickRow);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvSongsArtist.setLayoutManager(layoutManager);
        rvSongsArtist.setAdapter(themeSongAdapter);
    }

    private class SetAdapterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setUpAdapter();
        }
    }

    public void loadDataNetWord(int id) {
        callArtistSong = MainActivity.service.artistSong(id);
        callArtistSong.enqueue(new Callback<AudioResponse>() {
            @Override
            public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    listArtistSong = response.body().getData();
                }
            }

            @Override
            public void onFailure(Call<AudioResponse> call, Throwable t) {
            }
        });
    }

    private void loadDataAuidoListStotage() {
        StorageUtil storage = new StorageUtil(getActivity());
        audioListStorageUltil = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
    }

    @Override
    public void onClickRow(int position) {
        playAudio(position);
    }

    public void playAudio(int audioIndex) {
        StorageUtil storage = new StorageUtil(getActivity());
        storage.storeAudio((ArrayList<Audio>) listArtistSong);
        storage.storeAudioIndex(audioIndex);


        Intent broadcastIntent = new Intent(MainActivity.Broadcast_PLAY_NEW_AUDIO_OUT_STORAGE);
        getActivity().sendBroadcast(broadcastIntent);

        Intent broadcastIntents = new Intent(MainActivity.Broadcast_SERVICE_NOTIFY_CHANGE_AUDIO);
        getActivity().sendBroadcast(broadcastIntents);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }


}
