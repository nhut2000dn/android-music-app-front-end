package com.client.mymusic.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.adapters.SearchViewMainAdapter;
import com.client.mymusic.entities.Audio;
import com.client.mymusic.entities.AudioResponse;
import com.client.mymusic.utils.MediaPlayerService;
import com.client.mymusic.utils.StorageUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class SearchAdvencedActivity extends AppCompatActivity implements SearchViewMainAdapter.OnListenerRow, SearchViewMainAdapter.OnListenerMenuOption {

  @BindView(R.id.intent_to_is_playing)
  LinearLayout toggleSlideUpAudio;

  @BindView(R.id.toggle_play_audio)
  ImageView togglePlayAudio;

  @BindView(R.id.name_song_bar)
  TextView nameSongBar;

  @BindView(R.id.name_artist_bar)
  TextView nameArtistBar;

  @BindView(R.id.image_song_bar)
  RoundedImageView imageSongBar;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.rv_search_advenced)
  RecyclerView rvSearchAdvenced;

  @BindView(R.id.search_input)
  EditText searchInput;

  SearchView mSearchView;

  Call<AudioResponse> callAllSongs;

  private List<Audio> listAllSongs;

  SearchViewMainAdapter searchViewMainAdapter;

  private Boolean checkPlayNewInStorage = false;

  private MediaPlayerService player;

  private static Boolean checkClickPlay = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_advenced);

    ButterKnife.bind(this);

    setUpAudioBar();
    getDataNetword();
    setUpSearchInputEvenChange();
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    getSupportActionBar();
  }

  @OnClick(R.id.intent_to_is_playing)
  void intentToIsPlaying() {
    if (MediaPlayerService.mediaPlayer != null) {
      startActivity(new Intent(this, IsPlayingActivity.class));
      overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
    }
  }

  @OnClick(R.id.btn_next_song)
  void nextSong() {
    if (MediaPlayerService.mediaPlayer != null) {
      Intent broadcastIntent = new Intent(MainActivity.Broadcast_PLAY_SKIP_TO_NEXT);
      sendBroadcast(broadcastIntent);
      refreshDataTask refreshDataTask = new refreshDataTask();
      refreshDataTask.execute();
    }
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
    nameSongBar.setText(listAllSongs.get(position).getTitle());
    nameArtistBar.setText(listAllSongs.get(position).getArtist());
    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + listAllSongs.get(position).getImage())
            .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
    startActivity(new Intent(this, IsPlayingActivity.class));
    overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
  }

  @Override
  public void onClickMenuOption(int position, View view) {
    ImageView btnMore = (ImageView) view.findViewById(R.id.menu_option);
    PopupMenu popup = new PopupMenu(this, btnMore);
    popup.getMenuInflater().inflate(R.menu.menu_option_song, popup.getMenu());

    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.play_next) {
          Toast.makeText(getApplicationContext(),"You Clicked Play next : " + position, Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.add_to_playlist) {
          Intent intent = new Intent(getApplicationContext(), PersonalPlaylistActivity.class);
          intent.putExtra("action", "add_to_playlist");
          intent.putExtra("id", listAllSongs.get(position).getId());
          intent.putExtra("name", listAllSongs.get(position).getTitle());
          startActivityForResult(intent, 1);
          overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
        }
        return true;
      }
    });

    popup.show();
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

  public void playAudio(int audioIndex) {
    if (!checkPlayNewInStorage) {
      Toast.makeText(getApplication(), "If", Toast.LENGTH_SHORT).show();
      StorageUtil storage = new StorageUtil(getApplicationContext());
      storage.storeAudio((ArrayList<Audio>) listAllSongs);
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

  private void setUpAdapter() {
    searchViewMainAdapter = new SearchViewMainAdapter(listAllSongs, this, this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

    rvSearchAdvenced.setLayoutManager(layoutManager);
    rvSearchAdvenced.setAdapter(searchViewMainAdapter);
  }

  private void setUpAudioBar() {
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
  }

  private void setUpSearchInputEvenChange() {
    searchInput.requestFocus();
    searchInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        searchViewMainAdapter.getFilter().filter(s);
      }
    });
  }

  public void getDataNetword() {
    callAllSongs = MainActivity.service.allSongs();
    callAllSongs.enqueue(new Callback<AudioResponse>() {
      @Override
      public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
        if (response.isSuccessful()) {
          assert response.body() != null;
          listAllSongs = response.body().getData();
          setUpAdapter();
        }
      }

      @Override
      public void onFailure(Call<AudioResponse> call, Throwable t) {
      }
    });
  }

  @SuppressLint("StaticFieldLeak")
  private class refreshDataTask extends AsyncTask<Void, Void, Void> {

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
      refreshData();
    }
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

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    getMenuInflater().inflate(R.menu.menu_search, menu);
//
//    MenuItem mSearch = menu.findItem(R.id.action_search);
//    mSearchView = (SearchView) mSearch.getActionView();
//    mSearchView.setIconifiedByDefault(false);
//    mSearchView.setQueryHint("Search");
//
//    LinearLayout ll = (LinearLayout)mSearchView.getChildAt(0);
//    LinearLayout ll2 = (LinearLayout)ll.getChildAt(2);
//    LinearLayout ll3 = (LinearLayout)ll2.getChildAt(1);
//    SearchView.SearchAutoComplete autoComplete = ((SearchView.SearchAutoComplete)ll3.getChildAt(0));
//    autoComplete.setHintTextColor(Color.parseColor("#2f2e2e"));
//    autoComplete.setHintTextColor(Color.parseColor("#949494"));
//    autoComplete.setOutlineAmbientShadowColor(Color.parseColor("#2f2e2e"));
//    autoComplete.setTextColor(Color.parseColor("#2f2e2e"));
//    mSearchView.setMaxWidth(Integer.MAX_VALUE);
//    autoComplete.setTextSize(17);
//
//    View searchplate = (View)mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
//    searchplate.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
//
//    View searchFrame = (View)mSearchView.findViewById(androidx.appcompat.R.id.search_edit_frame);
//    searchFrame.setBackgroundResource(R.drawable.shapte_search_view);
//
//    ImageView searchViewIcon = (ImageView)mSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
//    searchViewIcon.setVisibility(View.GONE);
//    searchViewIcon.setImageDrawable(null);
//
//    LinearLayout searchEditFrame = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
//    ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).leftMargin = 100;
//    ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).rightMargin = 30;
//
//    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//      @Override
//      public boolean onQueryTextSubmit(String query) {
//        return false;
//      }
//      @Override
//      public boolean onQueryTextChange(String newText) {
//        searchViewMainAdapter.getFilter().filter(newText);
//        return true;
//      }
//    });
//
//    return super.onCreateOptionsMenu(menu);
//  }
}
