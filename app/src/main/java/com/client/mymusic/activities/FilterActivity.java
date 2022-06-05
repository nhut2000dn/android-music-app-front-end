package com.client.mymusic.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.client.mymusic.R;
import com.client.mymusic.adapters.ChartsSongAdapter;
import com.client.mymusic.adapters.CheckBoxCountryAdapter;
import com.client.mymusic.adapters.FilterSongAdapter;
import com.client.mymusic.adapters.PlaylistSongAdapter;
import com.client.mymusic.entities.Audio;
import com.client.mymusic.entities.AudioResponse;
import com.client.mymusic.entities.Country;
import com.client.mymusic.entities.CountryResponse;
import com.client.mymusic.entities.Theme;
import com.client.mymusic.entities.ThemeResponse;
import com.client.mymusic.utils.MediaPlayerService;
import com.client.mymusic.utils.StorageUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FilterActivity extends AppCompatActivity implements FilterSongAdapter.OnListenerRow, FilterSongAdapter.OnListenerMenuOption {

  @BindView(R.id.rv_checkbox_country)
  RecyclerView rvCheckBoxCountry;

  @BindView(R.id.radioGroup_character)
  RadioGroup radioGroupCharacter;

  @BindView(R.id.radioButton_male)
  RadioButton radioButtonMale;

  @BindView(R.id.radioButton_female)
  RadioButton radioButtonFemale;

  @BindView(R.id.rv_filter_song)
  RecyclerView rvFilterSong;

  @BindView(R.id.name_song_bar)
  TextView nameSongBar;

  @BindView(R.id.name_artist_bar)
  TextView nameArtistBar;

  @BindView(R.id.image_song_bar)
  RoundedImageView imageSongBar;

  @BindView(R.id.toggle_play_audio)
  ImageView togglePlayAudio;

  Call<ThemeResponse> callTheme;
  public static List<Theme> listTheme = new ArrayList<>();

  Call<CountryResponse> callCountry;
  public static List<Country> listCountry = new ArrayList<>();

  Call<AudioResponse> callFilter;

  public static JSONObject jsonArray;

  private ArrayList<Country> currentSelectedItems = new ArrayList<>();

  private List<Audio> listSongFilter = new ArrayList<>();

  private Boolean checkPlayNewInStorage = false;

  private static Boolean checkClickPlay = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filter);
    ButterKnife.bind(this);
    loadDataCountry();
    register_serviceNotifyChangeAudio();
    setUpData();

    // When radio button "Female" checked change.
    this.radioButtonMale.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      }
    });

    // When radio button "Male" checked change.
    this.radioButtonFemale.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      }
    });
  }

  private void setUpAdapter() {

    CheckBoxCountryAdapter myAdapter = new CheckBoxCountryAdapter(listCountry, new CheckBoxCountryAdapter.OnItemCheckListener() {
      @Override
      public void onItemCheck(Country item) {
        currentSelectedItems.add(item);
      }

      @Override
      public void onItemUncheck(Country item) {
        currentSelectedItems.remove(item);
      }
    });
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

    rvCheckBoxCountry.setLayoutManager(layoutManager);
    rvCheckBoxCountry.setAdapter(myAdapter);
  }

  public void loadDataTheme() {
    callTheme = MainActivity.service.themes();
    callTheme.enqueue(new Callback<ThemeResponse>() {
      @Override
      public void onResponse(Call<ThemeResponse> call, Response<ThemeResponse> response) {
        if (response.isSuccessful()) {
          assert response.body() != null;
          listTheme = response.body().getData();
          Toast.makeText(getApplicationContext(), listTheme.get(0).getName(), Toast.LENGTH_LONG).show();
        }
      }

      @Override
      public void onFailure(Call<ThemeResponse> call, Throwable t) {
      }
    });
  }

  public void loadDataCountry() {
    callCountry = MainActivity.service.countrys();
    callCountry.enqueue(new Callback<CountryResponse>() {
      @Override
      public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
        if (response.isSuccessful()) {
          assert response.body() != null;
          listCountry = response.body().getData();
          setUpAdapter();
        }
      }

      @Override
      public void onFailure(Call<CountryResponse> call, Throwable t) {
      }
    });
  }

  public void loadDataTopSong(JSONObject jsonArray) {
    // load data top song
    callFilter = MainActivity.service.Filter(jsonArray.toString());
    callFilter.enqueue(new Callback<AudioResponse>() {
      @Override
      public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
        if (response.isSuccessful()) {
          assert response.body() != null;
          listSongFilter = response.body().getData();
          setUpAdapterFilterSong();
        }
      }

      @Override
      public void onFailure(Call<AudioResponse> call, Throwable t) {
      }
    });
  }

  public void setUpAdapterFilterSong() {
    FilterSongAdapter themeSongAdapter = new FilterSongAdapter(listSongFilter, this, this);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

    rvFilterSong.setLayoutManager(layoutManager);
    rvFilterSong.setAdapter(themeSongAdapter);
  }

  @OnClick(R.id.filter)
  void filter() {
    try {
      jsonArray = createGroupInServer(this, currentSelectedItems);
      loadDataTopSong(jsonArray);
      Toast.makeText(getApplicationContext(), jsonArray.toString(), Toast.LENGTH_LONG).show();
    } catch (JSONException e) {
      e.printStackTrace();
    }
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

  public JSONObject createGroupInServer(
          Activity activity,
          ArrayList<Country> groups)
          throws JSONException {

    JSONObject jResult = new JSONObject();

    int gameCharacter = this.radioGroupCharacter.getCheckedRadioButtonId();

    RadioButton radioButtonGameCharacter = (RadioButton) this.findViewById(gameCharacter);

    if (radioButtonGameCharacter.getText().equals("Male")) {
      jResult.putOpt("artist", true);
    } else {
      jResult.putOpt("artist", false);
    }

    JSONArray jArray = new JSONArray();

    for (int i = 0; i < groups.size(); i++) {
      JSONObject jGroup = new JSONObject();
      jGroup.put("id", groups.get(i).getId());
      jGroup.put("name", groups.get(i).getName());
      // etcetera

      JSONObject jOuter = new JSONObject();
      jOuter.put("country", jGroup);

      jArray.put(jOuter);
    }

    jResult.put("countrys", jArray);
    return jResult;
  }

  public void playAudio(int audioIndex) {
    if (!checkPlayNewInStorage) {
      Toast.makeText(getApplication(), "If", Toast.LENGTH_SHORT).show();
      StorageUtil storage = new StorageUtil(getApplicationContext());
      storage.storeAudio((ArrayList<Audio>) listSongFilter);
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


  @Override
  public void onClickRow(int position) {
    playAudio(position);
    togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
    nameSongBar.setText(listSongFilter.get(position).getTitle());
    nameArtistBar.setText(listSongFilter.get(position).getArtist());
    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + listSongFilter.get(position).getImage())
            .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
    startActivity(new Intent(this, IsPlayingActivity.class));
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
          intent.putExtra("id", listSongFilter.get(position).getId());
          intent.putExtra("name", listSongFilter.get(position).getTitle());
          startActivityForResult(intent, 1);
          overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
        }
        return true;
      }
    });

    popup.show();
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
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(serviceNotifyChangeAudio);
  }
}
