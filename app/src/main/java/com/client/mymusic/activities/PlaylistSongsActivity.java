package com.client.mymusic.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
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
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistSongsActivity extends AppCompatActivity implements PlaylistSongAdapter.OnListenerRow, PlaylistSongAdapter.OnListenerMenuOption  {

  @BindView(R.id.title_tool_bar)
  TextView titleToolBar;

  @BindView(R.id.rv_playlist_song)
  RecyclerView rvPlaylistSong;

  @BindView(R.id.name_song_bar)
  TextView nameSongBar;

  @BindView(R.id.name_artist_bar)
  TextView nameArtistBar;

  @BindView(R.id.image_song_bar)
  RoundedImageView imageSongBar;

  @BindView(R.id.toggle_play_audio)
  ImageView togglePlayAudio;

  private List<Audio> listPlaylistSong = new ArrayList<>();

  Call<AudioResponse> callPlaylistSong;

  private MediaPlayerService player;
  boolean serviceBound;

  private Boolean checkPlayNewInStorage = false;

  private static Boolean checkClickPlay = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_playlist_songs);
    ButterKnife.bind(this);
    setUpData();
    register_serviceNotifyChangeAudio();
  }

  @OnClick(R.id.intent_to_is_playing)
  void intentToIsPlaying() {
    if (MediaPlayerService.mediaPlayer != null) {
      startActivity(new Intent(PlaylistSongsActivity.this, IsPlayingActivity.class));
      overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
    }
  }

  @OnClick(R.id.btn_previous_page)
  void previousPage() {
    Intent returnIntent = new Intent();
    if (checkClickPlay) {
      returnIntent.putExtra("mediaChange",true);
    } else  {
      returnIntent.putExtra("mediaChange",false);
    }
    setResult(Activity.RESULT_OK,returnIntent);
    finish();
    overridePendingTransition( R.anim.no_change, R.anim.slide_out_right );
    checkClickPlay = false;
  }

  @OnClick(R.id.toggle_play_audio)
  void togglePlayAudio() {
    if (MediaPlayerService.mediaPlayer != null) {
      Toast.makeText(this, MediaPlayerService.mediaPlayer.getCurrentPosition() + "", Toast.LENGTH_SHORT).show();
      if (MediaPlayerService.mediaPlayer.isPlaying()) {
        MediaPlayerService.pauseMedia();
        togglePlayAudio.setImageResource(R.drawable.ic_play_arrow);
      } else {
        MediaPlayerService.resumeMedia();
        togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
      }
    }
  }

  @Override
  public void onClickRow(int position) {

    playAudio(position);
    togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
    nameSongBar.setText(listPlaylistSong.get(position).getTitle());
    nameArtistBar.setText(listPlaylistSong.get(position).getArtist());
//    Picasso.get().load(listPlaylistSong.get(position).getImage())
//            .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + listPlaylistSong.get(position).getImage())
            .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
    startActivity(new Intent(PlaylistSongsActivity.this, IsPlayingActivity.class));
    overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );

  }

  @Override
  public void onClickMenuOption(int position, View view) {
    ImageView btnMore = (ImageView) view.findViewById(R.id.menu_option);
    PopupMenu popup = new PopupMenu(this, btnMore);

    popup.getMenuInflater().inflate(R.menu.menu_option_song, popup.getMenu());

    //registering popup with OnMenuItemClickListener
    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.play_next) {
          Toast.makeText(getApplicationContext(),"You Clicked Play next : " + position, Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.add_to_playlist) {
          Intent intent = new Intent(getApplicationContext(), PersonalPlaylistActivity.class);
          intent.putExtra("action", "add_to_playlist");
          intent.putExtra("id", listPlaylistSong.get(position).getId());
          intent.putExtra("name", listPlaylistSong.get(position).getTitle());
          startActivityForResult(intent, 1);
          overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
        }
        return true;
      }
    });

    popup.show();
  }

  public void playAudio(int audioIndex) {
    if (!checkPlayNewInStorage) {
      Toast.makeText(getApplication(), "If", Toast.LENGTH_SHORT).show();
      StorageUtil storage = new StorageUtil(getApplicationContext());
      storage.storeAudio((ArrayList<Audio>) listPlaylistSong);
      storage.storeAudioIndex(audioIndex);
      checkPlayNewInStorage = true;
      checkClickPlay = true;

      Intent broadcastIntent = new Intent(MainActivity.Broadcast_PLAY_NEW_AUDIO_OUT_STORAGE);
      sendBroadcast(broadcastIntent);
    } else {
      Toast.makeText(getApplication(), "Else", Toast.LENGTH_SHORT).show();
      StorageUtil storage = new StorageUtil(getApplicationContext());
      storage.storeAudioIndex(audioIndex);


      Intent broadcastIntent = new Intent(MainActivity.Broadcast_PLAY_NEW_AUDIO_IN_STORAGE);
      sendBroadcast(broadcastIntent);
    }
  }

  public void setUpData() {

    if (MediaPlayerService.mediaPlayer != null) {
      nameSongBar.setText(MediaPlayerService.activeAudio.getTitle());
      nameArtistBar.setText(MediaPlayerService.activeAudio.getArtist());
      if (!MediaPlayerService.activeAudio.getImage().equals("")) {
//        Picasso.get().load(MediaPlayerService.activeAudio.getImage())
//                .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
        Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + MediaPlayerService.activeAudio.getImage())
                .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
      }
    }

    Intent intent = this.getIntent();
    int id = intent.getIntExtra("id", 0);
    String name = intent.getStringExtra("name");
    titleToolBar.setText(name);
    loadDataNetWord(id);
  }

  public void loadDataNetWord(int id) {
    callPlaylistSong = MainActivity.service.playlistSong(id);
    callPlaylistSong.enqueue(new Callback<AudioResponse>() {
      @Override
      public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
        if (response.isSuccessful()) {
          assert response.body() != null;
          listPlaylistSong = response.body().getData();
          setUpAdapter();
        }
      }

      @Override
      public void onFailure(Call<AudioResponse> call, Throwable t) {
      }
    });
  }

  private void setUpAdapter() {
    PlaylistSongAdapter themeSongAdapter = new PlaylistSongAdapter(listPlaylistSong, this, this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

    rvPlaylistSong.setLayoutManager(layoutManager);
    rvPlaylistSong.setAdapter(themeSongAdapter);
  }

  private BroadcastReceiver serviceNotifyChangeAudio = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      nameSongBar.setText(MediaPlayerService.activeAudio.getTitle());
      nameArtistBar.setText(MediaPlayerService.activeAudio.getArtist());
//      Picasso.get().load(MediaPlayerService.activeAudio.getImage())
//              .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
      Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + MediaPlayerService.activeAudio.getImage())
              .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
      if (MediaPlayerService.mediaPlayer.isPlaying()) {
        togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
      } else {
        togglePlayAudio.setImageResource(R.drawable.ic_play_arrow);
      }
      Toast.makeText(getApplicationContext(), "Yeah doi bai", Toast.LENGTH_SHORT).show();
    }
  };

  private void register_serviceNotifyChangeAudio() {
    //Register playNewMedia receiver
    IntentFilter filter = new IntentFilter(MainActivity.Broadcast_SERVICE_NOTIFY_CHANGE_AUDIO);
    registerReceiver(serviceNotifyChangeAudio, filter);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putBoolean("ServiceState", serviceBound);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    serviceBound = savedInstanceState.getBoolean("ServiceState");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(serviceNotifyChangeAudio);
  }

}
