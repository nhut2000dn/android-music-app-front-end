package com.client.mymusic.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.adapters.SongsPersonalAdapter;
import com.client.mymusic.entities.Audio;
import com.client.mymusic.utils.MediaPlayerService;
import com.client.mymusic.utils.StorageUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongPersonalActivity extends AppCompatActivity implements SongsPersonalAdapter.OnListenerRowSong {

    @BindView(R.id.rv_songs_personal)
    RecyclerView rvSongsPersonal;

    @BindView(R.id.name_song_bar)
    TextView nameSongBar;

    @BindView(R.id.name_artist_bar)
    TextView nameArtistBar;

    @BindView(R.id.toggle_play_audio)
    ImageView togglePlayAudio;

    @BindView(R.id.image_song_bar)
    RoundedImageView imageSongBar;

    @BindView(R.id.tool_bar)
    Toolbar toolBar;

    SearchView mSearchView;

    private static ArrayList<Audio> audioListExternal;

    private static ArrayList<Audio> audioListStorageUltil;
    private int audioIndex;

    SongsPersonalAdapter mAdapter;

    private MediaPlayerService player;
    boolean serviceBound;

    private Boolean checkPlayNewInStorage = false;

    private static Boolean checkClickPlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_personal);

        ButterKnife.bind(this);
        setSupportActionBar(toolBar);
        (getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar();

        runtimePermisson();
        if (MediaPlayerService.mediaPlayer != null) {
            nameSongBar.setText(MediaPlayerService.activeAudio.getTitle());
            nameArtistBar.setText(MediaPlayerService.activeAudio.getArtist());
        }
        register_serviceNotifyChangeAudio();
    }

    @Override
    public void onClickRowSong(int position) {
        playAudio(position);
        togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
        nameSongBar.setText(audioListExternal.get(position).getTitle());
        nameArtistBar.setText(audioListExternal.get(position).getArtist());
        startActivity(new Intent(SongPersonalActivity.this, IsPlayingActivity.class));
        overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
    }

    @OnClick(R.id.intent_to_is_playing)
    void intentToIsPlaying() {
        if (MediaPlayerService.mediaPlayer != null) {
            startActivity(new Intent(SongPersonalActivity.this, IsPlayingActivity.class));
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

    public void playAudio(int audioIndex) {
      if (!checkPlayNewInStorage) {
        Toast.makeText(getApplication(), "If", Toast.LENGTH_SHORT).show();
        StorageUtil storage = new StorageUtil(getApplicationContext());
        storage.storeAudio((ArrayList<Audio>) audioListExternal);
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

    public void runtimePermisson() {
        Dexter.withActivity(SongPersonalActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        loadAudio();
                        setUpAdapterSong();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioListExternal = new ArrayList<>();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));

                // Save to audioList
                audioListExternal.add(new Audio(id, title, "", data, 0, artist, "", 0, 0,  0,"", album));
            }
        }
        cursor.close();
    }

    public void setUpAdapterSong() {
        mAdapter = new SongsPersonalAdapter(audioListExternal, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvSongsPersonal.setLayoutManager(layoutManager);
        rvSongsPersonal.setAdapter(mAdapter);
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

    private BroadcastReceiver serviceNotifyChangeAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        nameSongBar.setText(MediaPlayerService.activeAudio.getTitle());
        nameArtistBar.setText(MediaPlayerService.activeAudio.getArtist());
//        Picasso.get().load(MediaPlayerService.activeAudio.getImage())
//                .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
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
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_search, menu);

    MenuItem mSearch = menu.findItem(R.id.action_search);
    mSearchView = (SearchView) mSearch.getActionView();
    mSearchView.setIconifiedByDefault(false);
    mSearchView.setQueryHint("Search");

    LinearLayout ll = (LinearLayout)mSearchView.getChildAt(0);
    LinearLayout ll2 = (LinearLayout)ll.getChildAt(2);
    LinearLayout ll3 = (LinearLayout)ll2.getChildAt(1);
    SearchView.SearchAutoComplete autoComplete = ((SearchView.SearchAutoComplete)ll3.getChildAt(0));
    autoComplete.setHintTextColor(Color.parseColor("#949494"));
    autoComplete.setOutlineAmbientShadowColor(Color.parseColor("#2f2e2e"));
    autoComplete.setTextColor(Color.parseColor("#2f2e2e"));

    ImageView searchClose = mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
    searchClose.setImageResource(R.drawable.ic_clear);

    ImageView searchViewIcon = (ImageView)mSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
    searchViewIcon.setVisibility(View.GONE);
    searchViewIcon.setImageResource(R.drawable.ic_search);

    View searchplate = (View)mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
    searchplate.getBackground().setColorFilter(Color.parseColor("#2f2e2e"), PorterDuff.Mode.MULTIPLY);

    LinearLayout searchEditFrame = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
    ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).leftMargin = -25;

    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }
      @Override
      public boolean onQueryTextChange(String newText) {
          mAdapter.getFilter().filter(newText);
        return true;
      }
    });

    return super.onCreateOptionsMenu(menu);
  }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//
//        MenuItem mSearch = menu.findItem(R.id.action_search);
//        mSearchView = (SearchView) mSearch.getActionView();
//        mSearchView.setQueryHint("Search");
//
//        LinearLayout ll = (LinearLayout)mSearchView.getChildAt(0);
//        LinearLayout ll2 = (LinearLayout)ll.getChildAt(2);
//        LinearLayout ll3 = (LinearLayout)ll2.getChildAt(1);
//        SearchView.SearchAutoComplete autoComplete = ((SearchView.SearchAutoComplete)ll3.getChildAt(0));
//        autoComplete.setHintTextColor(Color.parseColor("#949494"));
//        autoComplete.setOutlineAmbientShadowColor(Color.parseColor("#2f2e2e"));
//        autoComplete.setTextColor(Color.parseColor("#2f2e2e"));
//
//        ImageView searchClose = mSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
//        searchClose.setImageResource(R.drawable.ic_clear);
//
//        ImageView searchViewIcon = (ImageView)mSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
//        searchViewIcon.setVisibility(View.GONE);
//        searchViewIcon.setImageDrawable(null);
//
//
//        View searchplate = (View)mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
//        searchplate.getBackground().setColorFilter(Color.parseColor("#2f2e2e"), PorterDuff.Mode.MULTIPLY);
//
//        LinearLayout searchEditFrame = (LinearLayout) mSearchView.findViewById(R.id.search_edit_frame);
//        ((LinearLayout.LayoutParams) searchEditFrame.getLayoutParams()).leftMargin = 5;
//
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                mAdapter.getFilter().filter(newText);
//                return true;
//            }
//        });
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(serviceNotifyChangeAudio);
//    }
}
