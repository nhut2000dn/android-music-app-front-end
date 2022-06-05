package com.client.mymusic.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.adapters.PersonalPlaylistAdapter;
import com.client.mymusic.entities.PersonalPlaylist;
import com.client.mymusic.entities.PersonalPlaylistResponse;
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


public class PersonalPlaylistActivity extends AppCompatActivity implements PersonalPlaylistAdapter.OnListenerRow, PersonalPlaylistAdapter.OnListenerMenuOption {

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

  private int idSongCurrent = -1;

  private String action;

  private int idSongAdd;

  private static Boolean checkClickPlay = false;

  private List<PersonalPlaylist> listPersonalPlaylist = new ArrayList<>();
  Call<PersonalPlaylistResponse> callPersonalPlaylist;

  Call<Boolean> callCreatePersonalPlaylist;

  Call<Boolean> callEditPersonalPlaylist;

  Call<Boolean> callAddToPersonalPlaylist;

  Call<Boolean> callDeletePersonalPlaylist;

  Call<Boolean> callSharePersonalPlaylist;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_personal_playlist);
    ButterKnife.bind(this);
    setUpData();
    register_serviceNotifyChangeAudio();
  }

  @OnClick(R.id.intent_to_is_playing)
  void intentToIsPlaying() {
    if (MediaPlayerService.mediaPlayer != null) {
      startActivity(new Intent(this, IsPlayingActivity.class));
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

  @OnClick(R.id.new_playlist)
  void newPlaylist() {
    displayAddPlaylist();
  }

  @Override
  public void onClickRow(int position) {
    if (action.equals("view_playlist")) {
      Intent intent = new Intent(this, PersonalPlaylistSongsActivity.class);
      intent.putExtra("id", listPersonalPlaylist.get(position).getId());
      intent.putExtra("name", listPersonalPlaylist.get(position).getName());
      startActivityForResult(intent, 1);
      overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
    } else if (action.equals("add_to_playlist")) {
      callAddToPersonalPlaylist = MainActivity.serviceWithAuth.addToPersonalPlaylists(listPersonalPlaylist.get(position).getId(), idSongAdd);
      callAddToPersonalPlaylist.enqueue(new Callback<Boolean>() {
        @Override
        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
          if (response.isSuccessful()) {
            assert response.body() != null;
            if (response.body()) {
              Toast.makeText(getApplicationContext(), "Add Succesful", Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(getApplicationContext(), "The song has been added to this playlist", Toast.LENGTH_SHORT).show();
            }
          }
        }

        @Override
        public void onFailure(Call<Boolean> call, Throwable t) {
          Toast.makeText(getApplicationContext(), "False", Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

  @Override
  public void onClickMenuOption(int position, View view) {
    ImageView btnMore = (ImageView) view.findViewById(R.id.menu_option);
    PopupMenu popup = new PopupMenu(this, btnMore);

    popup.getMenuInflater().inflate(R.menu.menu_option_personal_playlist, popup.getMenu());

    //registering popup with OnMenuItemClickListener
    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.edit) {
          displayEditPlaylist(position);
        } else if (item.getItemId() == R.id.delete) {
          callDeletePersonalPlaylist = MainActivity.serviceWithAuth.deletePersonalPlaylist(listPersonalPlaylist.get(position).getId());
          callDeletePersonalPlaylist.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
              if (response.isSuccessful()) {
                assert response.body() != null;
                if (response.body()) {
                  Toast.makeText(getApplicationContext(), "Delete Succesful", Toast.LENGTH_SHORT).show();
                  loadDataNetWord();
                } else {
                  Toast.makeText(getApplicationContext(), "Delete fail", Toast.LENGTH_SHORT).show();
                }
              }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
              Toast.makeText(getApplicationContext(), "False", Toast.LENGTH_SHORT).show();
            }
          });
        } else if (item.getItemId() == R.id.share) {
          callSharePersonalPlaylist = MainActivity.serviceWithAuth.sharePersonalPlaylist(listPersonalPlaylist.get(position).getId());
          callSharePersonalPlaylist.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
              if (response.isSuccessful()) {
                assert response.body() != null;
                if (response.body()) {
                  Toast.makeText(getApplicationContext(), "Share Successfull", Toast.LENGTH_SHORT).show();
                  loadDataNetWord();
                } else {
                  Toast.makeText(getApplicationContext(), "Remove Share fail", Toast.LENGTH_SHORT).show();
                }
              }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
              Toast.makeText(getApplicationContext(), "False", Toast.LENGTH_SHORT).show();
            }
          });
        }

        return true;
      }
    });

    popup.show();
  }

  public void displayEditPlaylist(int position) {
    LayoutInflater inflater = getLayoutInflater();
    View fromLayout = inflater.inflate(R.layout.dialog_edit_personal_playlist, null);
    final EditText etNamePlaylist = (EditText) fromLayout.findViewById(R.id.name_playlist);

    etNamePlaylist.setText(listPersonalPlaylist.get(position).getName());

    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setView(fromLayout);
    alert.setCancelable(false);
    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
      }
    });

    alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        callEditPersonalPlaylist = MainActivity.serviceWithAuth.EditPersonalPlaylist(listPersonalPlaylist.get(position).getId(), etNamePlaylist.getText().toString());
        callEditPersonalPlaylist.enqueue(new Callback<Boolean>() {

          @Override
          public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            if (response.isSuccessful()) {
              assert response.body() != null;
              if (response.body()) {
                loadDataNetWord();
                Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
              }
            } else {
              if (response.code() == 422) {
              }

              if (response.code() == 401) {
              }
            }
          }
          @Override
          public void onFailure(Call<Boolean> call, Throwable t) {
          }
        });
      }
    });
    AlertDialog dialog = alert.create();
    dialog.show();
  }


  public void displayAddPlaylist() {
    LayoutInflater inflater = getLayoutInflater();
    View fromLayout = inflater.inflate(R.layout.dialog_new_personal_playlist, null);
    final EditText etNamePlaylist = (EditText) fromLayout.findViewById(R.id.name_playlist);

    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setView(fromLayout);
    alert.setCancelable(false);
    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
      }
    });

    alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        callCreatePersonalPlaylist = MainActivity.serviceWithAuth.CreatePersonalPlaylist(etNamePlaylist.getText().toString());
        callCreatePersonalPlaylist.enqueue(new Callback<Boolean>() {

          @Override
          public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            if (response.isSuccessful()) {
              assert response.body() != null;

              if (response.body()) {
                loadDataNetWord();
                Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
              }

            } else {

              if (response.code() == 422) {
              }

              if (response.code() == 401) {
              }

            }
          }

          @Override
          public void onFailure(Call<Boolean> call, Throwable t) {
          }
        });
      }
    });
    AlertDialog dialog = alert.create();
    dialog.show();
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
    action = intent.getStringExtra("action");
    idSongAdd = intent.getIntExtra("id", 0);
    String name = intent.getStringExtra("name");

    titleToolBar.setText(name);
    loadDataNetWord();
  }

  public void loadDataNetWord() {
    callPersonalPlaylist = MainActivity.serviceWithAuth.personalPlaylist();
    callPersonalPlaylist.enqueue(new Callback<PersonalPlaylistResponse>() {
      @Override
      public void onResponse(Call<PersonalPlaylistResponse> call, Response<PersonalPlaylistResponse> response) {
        if (response.isSuccessful()) {
          assert response.body() != null;
          listPersonalPlaylist = response.body().getData();
          setUpAdapter();
        }
      }

      @Override
      public void onFailure(Call<PersonalPlaylistResponse> call, Throwable t) {
        Toast.makeText(getApplicationContext(), "False", Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void setUpAdapter() {
    PersonalPlaylistAdapter  personalPlaylistAdapter = new PersonalPlaylistAdapter(listPersonalPlaylist, this, this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

    rvPlaylist.setLayoutManager(layoutManager);
    rvPlaylist.setAdapter(personalPlaylistAdapter);
  }

  private void refreshData() {
    nameSongBar.setText(MediaPlayerService.activeAudio.getTitle());
    nameArtistBar.setText(MediaPlayerService.activeAudio.getArtist());
//    Picasso.get().load(MediaPlayerService.activeAudio.getImage())
//            .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + MediaPlayerService.activeAudio.getImage())
            .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
    if (MediaPlayerService.mediaPlayer.isPlaying()) {
      togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
    } else {
      togglePlayAudio.setImageResource(R.drawable.ic_play_arrow);
    }
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
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == Activity.RESULT_OK) {
      if (MediaPlayerService.mediaPlayer != null) {
        refreshData();
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(serviceNotifyChangeAudio);
  }
}
