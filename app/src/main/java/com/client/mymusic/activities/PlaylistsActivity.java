package com.client.mymusic.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.adapters.PlaylistAdpater;
import com.client.mymusic.entities.Playlist;
import com.client.mymusic.entities.PlaylistResponse;
import com.client.mymusic.utils.MediaPlayerService;
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

public class PlaylistsActivity extends AppCompatActivity implements PlaylistAdpater.OnListenerRow {

  @BindView(R.id.title_tool_bar)
  TextView titleToolBar;

  @BindView(R.id.rv_playlist)
  RecyclerView rvPlaylist;

  @BindView(R.id.name_song_bar)
  TextView nameSongBar;

  @BindView(R.id.name_artist_bar)
  TextView nameArtistBar;

  @BindView(R.id.image_song_bar)
  RoundedImageView imageSongBar;

  @BindView(R.id.toggle_play_audio)
  ImageView togglePlayAudio;

  private List<Playlist> listPlaylist = new ArrayList<>();

  Call<PlaylistResponse> callPlaylist;

  private int idSong = -1;

  private static Boolean checkClickPlay = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_playlists);
    ButterKnife.bind(this);
    setUpData();
    register_serviceNotifyChangeAudio();
  }

  @OnClick(R.id.intent_to_is_playing)
  void intentToIsPlaying() {
    if (MediaPlayerService.mediaPlayer != null) {
      startActivity(new Intent(PlaylistsActivity.this, IsPlayingActivity.class));
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
    setUpAdapter();
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
    callPlaylist = MainActivity.service.playlist(id);
    callPlaylist.enqueue(new Callback<PlaylistResponse>() {
      @Override
      public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
        if (response.isSuccessful()) {
          assert response.body() != null;
          listPlaylist = response.body().getData();
          setUpAdapter();
        }
      }

      @Override
      public void onFailure(Call<PlaylistResponse> call, Throwable t) {
      }
    });
  }

  private void setUpAdapter() {
    PlaylistAdpater themeSongAdapter = new PlaylistAdpater(listPlaylist, this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

    rvPlaylist.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
    rvPlaylist.setAdapter(themeSongAdapter);
  }

  @Override
  public void onClickRow(int position) {
    Intent intent = new Intent(this, PlaylistSongsActivity.class);
    intent.putExtra("id", listPlaylist.get(position).getId());
    intent.putExtra("name", listPlaylist.get(position).getName());
    startActivityForResult(intent, 1);
    overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
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
    IntentFilter filter = new IntentFilter(MainActivity.Broadcast_SERVICE_NOTIFY_CHANGE_AUDIO);
    registerReceiver(serviceNotifyChangeAudio, filter);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == Activity.RESULT_OK) {
      if (MediaPlayerService.mediaPlayer != null) {
        if (data.getBooleanExtra("mediaChange", false)) {
          Toast.makeText(this, "mediaChange", Toast.LENGTH_SHORT).show();
          nameSongBar.setText(MediaPlayerService.activeAudio.getTitle());
          nameArtistBar.setText(MediaPlayerService.activeAudio.getArtist());
          Toast.makeText(this, MediaPlayerService.activeAudio.getLyrics(), Toast.LENGTH_LONG).show();
//          Picasso.get().load(MediaPlayerService.activeAudio.getImage())
//                  .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
          Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + MediaPlayerService.activeAudio.getImage())
                  .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
          if (MediaPlayerService.mediaPlayer.isPlaying()) {
            togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
          } else {
            togglePlayAudio.setImageResource(R.drawable.ic_play_arrow);
          }
          checkClickPlay = true;
        }
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(serviceNotifyChangeAudio);
  }
}
