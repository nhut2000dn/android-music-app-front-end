package com.client.mymusic.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.client.mymusic.R;
import com.client.mymusic.adapters.ViewPagerMainAdapter;
import com.client.mymusic.entities.Artist;
import com.client.mymusic.entities.ArtistResponse;
import com.client.mymusic.entities.Audio;
import com.client.mymusic.entities.AudioResponse;
import com.client.mymusic.entities.Charts;
import com.client.mymusic.entities.ChartsResponse;
import com.client.mymusic.entities.PersonalPlaylistHostResponse;
import com.client.mymusic.entities.PersonalPlaylistHot;
import com.client.mymusic.entities.Playlist;
import com.client.mymusic.entities.PlaylistResponse;
import com.client.mymusic.entities.Slidershow;
import com.client.mymusic.entities.SlideshowResponse;
import com.client.mymusic.entities.Theme;
import com.client.mymusic.entities.ThemeResponse;
import com.client.mymusic.entities.User;
import com.client.mymusic.fragments.ArtistFragment;
import com.client.mymusic.fragments.ChartsFragment;
import com.client.mymusic.fragments.HomeFragment;
import com.client.mymusic.fragments.PersonalFragment;
import com.client.mymusic.fragments.ThemeMusicFragment;
import com.client.mymusic.networks.ApiService;
import com.client.mymusic.networks.RetrofitBuilder;
import com.client.mymusic.utils.MediaPlayerService;
import com.client.mymusic.utils.StorageUtil;
import com.client.mymusic.utils.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnPlayTopSongListener, HomeFragment.OnPlaySlideshowListener, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.image_account_action)
    CircularImageView imageAccountAction;

    @BindView(R.id.intent_to_is_playing)
    LinearLayout toggleSlideUpAudio;

    public static ImageView togglePlayAudio;

    public static TextView nameSongBar;

    public static  TextView nameArtistBar;

    public static  RoundedImageView imageSongBar;

    @BindView(R.id.bottom_navigation_main)
    BottomNavigationView bottomNavigationMain;

    @BindView(R.id.view_pager_main)
    ViewPager viewPagerMain;

    @BindView(R.id.nav_view)
    NavigationView navView;

    RoundedImageView imageAccountNav;

    TextView nameAccountNav;

    TextView emailAccountNav;

    public static ApiService service;

    public static ApiService serviceWithAuth;

    public static StorageUtil storage;
    public static ArrayList<Audio> audioListStorageUltil;
    public static int audioIndex = -1;

    Call<ThemeResponse> callThemes;

    Call<ArtistResponse> callArtist;

    Call<ChartsResponse> callCharts;

    Call<SlideshowResponse> callSlideshows;

    Call<PlaylistResponse> callPlaylistHot;

    Call<PersonalPlaylistHostResponse> callPersonalPlaylistHot;

    Call<AudioResponse> callTopSongsByViews;

    Call<User> callUser;

    Call<Boolean> callLogout;

    public static List<Theme> listTheme = new ArrayList<>();

    public static List<Artist> listArtist = new ArrayList<>();

    public static List<Charts> listCharts = new ArrayList<>();

    public static List<Slidershow> listSlideshows = new ArrayList<>();

    public static List<Playlist> listPlaylistsHot = new ArrayList<>();

    public static List<PersonalPlaylistHot> listPersonalPlaylistsHot = new ArrayList<>();

    public static List<Audio> listTopSong = new ArrayList<>();

    public static User user;

    public static Context mContext;

    private MenuItem prevMenuItem;

    public static TokenManager tokenManager;

    PersonalFragment personalFragment;
    HomeFragment homeFragment;
    ThemeMusicFragment themeMusicFragment;
    ArtistFragment artistFragment;
    ChartsFragment chartsFragment;

    private MediaPlayerService player;
    boolean serviceBound = false;

    ViewPagerMainAdapter viewPagerMainAdapter;

    private Boolean checkPlayNewInStorage = false;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;


    public static final String Broadcast_PLAY_NEW_AUDIO_IN_STORAGE = "com.example.musicappofficial.playNewAudioInStorage";
    public static final String Broadcast_PLAY_NEW_AUDIO_OUT_STORAGE = "com.example.musicappofficial.playNewAudioOutStorage";
    public static final String Broadcast_PLAY_SKIP_TO_NEXT = "com.example.musicappofficial.skipToNext";
    public static final String Broadcast_PLAY_SKIP_TO_PREVIOUS = "com.example.musicappofficial.skipToPrevious";
    public static final String Broadcast_SERVICE_NOTIFY_CHANGE_AUDIO = "com.example.musicappofficial.serviceNotifyChangeAudio";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplication();

        ButterKnife.bind(this);

        togglePlayAudio = findViewById(R.id.toggle_play_audio);
        nameSongBar  = findViewById(R.id.name_song_bar);
        nameArtistBar = findViewById(R.id.name_artist_bar);
        imageSongBar = findViewById(R.id.image_song_bar);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        service = RetrofitBuilder.createService(ApiService.class);
        serviceWithAuth = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        loadDataTheme();
        loadDataArtist();
        loadDataPlaylistHot();
        loadDataPersonalPlaylistHot();
        loadDataSlideshow();
        loadDataTopSong();
        loadDataUser();
        loadDataChart();

        storage = new StorageUtil(this);

        setUpNavigationViewPager(viewPagerMain);

        player = MediaPlayerService.getInstance();
        Intent playerIntent = new Intent(this, MediaPlayerService.class);
        bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        register_serviceNotifyChangeAudio();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView.setNavigationItemSelectedListener(this);
        View hView =  navView.getHeaderView(0);
        imageAccountNav = (RoundedImageView) hView.findViewById(R.id.image_account_nav);
        nameAccountNav = (TextView) hView.findViewById(R.id.name_account_nav);
        emailAccountNav = (TextView) hView.findViewById(R.id.email_account_nav);
    }

    @OnClick(R.id.toggle_play_audio)
    void togglePlayAudio() {
        if (MediaPlayerService.mediaPlayer != null) {
            if (MediaPlayerService.mediaPlayer.isPlaying()) {
                MediaPlayerService.pauseMedia();
                togglePlayAudio.setImageResource(R.drawable.ic_play_arrow);
            } else {
                MediaPlayerService.resumeMedia();
                togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
            }
        }
    }

    @OnClick(R.id.intent_to_is_playing)
    void intentToIsPlaying() {
        if (MediaPlayerService.mediaPlayer != null) {
            startActivity(new Intent(MainActivity.this, IsPlayingActivity.class));
            overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
        }
    }

    @OnClick(R.id.btn_next_song)
    void nextSong() {
        if (MediaPlayerService.mediaPlayer != null) {
            Intent broadcastIntent = new Intent(Broadcast_PLAY_SKIP_TO_NEXT);
            sendBroadcast(broadcastIntent);
            refreshDataTask refreshDataTask = new refreshDataTask();
            refreshDataTask.execute();
        }
    }

    @SuppressLint("WrongConstant")
    @OnClick(R.id.image_account_action)
    void intentToLoginPage() {
        String accessToken = TokenManager.getToken().getAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
        } else {
            drawerLayout.openDrawer(Gravity.START);
        }
    }

    @OnClick(R.id.intent_to_search_page)
    void intentToSearchPage() {
        startActivityForResult(new Intent(MainActivity.this, SearchAdvencedActivity.class), 1);
        overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
    }

    @Override
    public void onClickPlayTopSong(int position) {
        playAudio(position);
        togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
        nameSongBar.setText(listTopSong.get(position).getTitle());
        nameArtistBar.setText(listTopSong.get(position).getArtist());
//        Picasso.get().load(listTopSong.get(position).getImage())
//                .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
        Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + listTopSong.get(position).getImage())
                .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
        startActivity(new Intent(this, IsPlayingActivity.class));
        overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_edit_profile: {
                startActivityForResult(new Intent(this, EditProfileActivity.class), 1);
                overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
                break;
            }
            case R.id.nav_change_password: {
                startActivity(new Intent(this, ChangePasswordActivity.class));
                overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
                break;
            }
            case R.id.nav_about_me: {
                startActivity(new Intent(this, AboutMeActivity.class));
                overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
                break;
            }
            case R.id.nav_filter: {
                Intent intent = new Intent(this, FilterActivity.class);
                startActivityForResult(intent, 1);
                overridePendingTransition( R.anim.slide_in_left, R.anim.no_change );
                break;
            }
            case R.id.nav_logout: {
                callLogout = serviceWithAuth.logout();
                callLogout.enqueue(new Callback<Boolean>() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (response.body()) {
                                tokenManager.deleteToken();
                                setUpAccountView();
                                drawerLayout.closeDrawer(Gravity.START);
                                Toast.makeText(getApplicationContext(), "Logout succesful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                    }
                });
                break;
            }
        }
        return true;
    }

    public void setUpAccountView() {
        String accessToken = TokenManager.getToken().getAccessToken();
        if (accessToken != null) {
            if (user.getAvatar() != null) {
//                Picasso.get().load(user.getAvatar())
//                        .fit().placeholder(R.drawable.playholder_music).into(imageAccountAction);
//                Picasso.get().load(user.getAvatar())
//                        .fit().placeholder(R.drawable.playholder_music).into(imageAccountNav);
                Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + user.getAvatar())
                        .fit().placeholder(R.drawable.playholder_music).into(imageAccountAction);
                Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + user.getAvatar())
                        .fit().placeholder(R.drawable.playholder_music).into(imageAccountNav);
                nameAccountNav.setText(user.getName());
                emailAccountNav.setText(user.getEmail());
            }
        } else {
            imageAccountAction.setImageResource(R.drawable.ic_person);
            imageAccountNav.setImageResource(R.drawable.ic_person);
            nameAccountNav.setText("");
            emailAccountNav.setText("");
            user = null;
        }
    }

    public void loadDataTheme() {
        // load data Theme
        callThemes = MainActivity.service.themes();
        callThemes.enqueue(new Callback<ThemeResponse>() {
            @Override
            public void onResponse(Call<ThemeResponse> call, Response<ThemeResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    listTheme = response.body().getData();
                    viewPagerMain.setAdapter(viewPagerMainAdapter);
                    viewPagerMain.setCurrentItem(1);
                }
            }

            @Override
            public void onFailure(Call<ThemeResponse> call, Throwable t) {
            }
        });

    }

    public void loadDataArtist() {
        // load data Artist
        callArtist = MainActivity.service.artists();
        callArtist.enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    listArtist = response.body().getData();
                    viewPagerMain.setAdapter(viewPagerMainAdapter);
                    viewPagerMain.setCurrentItem(1);
                }
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
            }
        });
    }

    public void loadDataChart() {
        // load data Charts
        callCharts = service.charts();
        callCharts.enqueue(new Callback<ChartsResponse>() {
            @Override
            public void onResponse(Call<ChartsResponse> call, Response<ChartsResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    listCharts = response.body().getData();
                    viewPagerMain.setAdapter(viewPagerMainAdapter);
                    viewPagerMain.setCurrentItem(1);
                }
            }

            @Override
            public void onFailure(Call<ChartsResponse> call, Throwable t) {
            }
        });
    }

    public void loadDataSlideshow() {
        // load data slideshow
        callSlideshows = service.slideshows();
        callSlideshows.enqueue(new Callback<SlideshowResponse>() {
            @Override
            public void onResponse(Call<SlideshowResponse> call, Response<SlideshowResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    listSlideshows = response.body().getData();
                    viewPagerMain.setAdapter(viewPagerMainAdapter);
                    viewPagerMain.setCurrentItem(1);
                }
            }

            @Override
            public void onFailure(Call<SlideshowResponse> call, Throwable t) {
            }
        });
    }

    public void loadDataPlaylistHot() {
        // load data playlist hot
        callPlaylistHot = service.playlistHot();
        callPlaylistHot.enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    listPlaylistsHot = response.body().getData();
                    viewPagerMain.setAdapter(viewPagerMainAdapter);
                    viewPagerMain.setCurrentItem(1);
                }
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {
            }
        });
    }

    public void loadDataPersonalPlaylistHot() {
        // load data playlist hot
        callPersonalPlaylistHot = service.personalPlaylistHost();
        callPersonalPlaylistHot.enqueue(new Callback<PersonalPlaylistHostResponse>() {
            @Override
            public void onResponse(Call<PersonalPlaylistHostResponse> call, Response<PersonalPlaylistHostResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    listPersonalPlaylistsHot = response.body().getData();
                    viewPagerMain.setAdapter(viewPagerMainAdapter);
                    viewPagerMain.setCurrentItem(1);
                }
            }

            @Override
            public void onFailure(Call<PersonalPlaylistHostResponse> call, Throwable t) {
            }
        });
    }


    public void loadDataTopSong() {
        // load data top song
        callTopSongsByViews = service.topSongs();
        callTopSongsByViews.enqueue(new Callback<AudioResponse>() {
            @Override
            public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    listTopSong = response.body().getData();
                    viewPagerMain.setAdapter(viewPagerMainAdapter);
                    viewPagerMain.setCurrentItem(1);
                }
            }

            @Override
            public void onFailure(Call<AudioResponse> call, Throwable t) {
            }
        });
    }

    public void loadDataUser() {
        callUser = serviceWithAuth.user();
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    user = response.body();
                    setUpAccountView();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }


    public void setUpNavigationViewPager(ViewPager viewPager) {

        //View pager main add on pager change listener
        viewPagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    bottomNavigationMain.getMenu().getItem(0).setChecked(false);

                bottomNavigationMain.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationMain.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Bottom Navigation set event selected item
        bottomNavigationMain.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_personal:
                                viewPagerMain.setCurrentItem(0);
                                return true; //break
                            case R.id.action_home:
                                viewPagerMain.setCurrentItem(1);
                                return true; //break;
                            case R.id.action_charts:
                                viewPagerMain.setCurrentItem(2);
                                return true; //break;
                            case R.id.action_artist:
                                viewPagerMain.setCurrentItem(3);
                                return true; //break;
                            case R.id.action_theme_music:
                                viewPagerMain.setCurrentItem(4);
                                return true; //break;
                        }
                        return false;
                    }
                });

        //Viewpager main add fragment and set adapter
        viewPagerMainAdapter = new ViewPagerMainAdapter(getSupportFragmentManager());
        personalFragment = new PersonalFragment();
        homeFragment = new HomeFragment();
        themeMusicFragment = new ThemeMusicFragment();
        artistFragment = new ArtistFragment();
        chartsFragment = new ChartsFragment();
        viewPagerMainAdapter.addFragment(personalFragment);
        viewPagerMainAdapter.addFragment(homeFragment);
        viewPagerMainAdapter.addFragment(chartsFragment);
        viewPagerMainAdapter.addFragment(artistFragment);
        viewPagerMainAdapter.addFragment(themeMusicFragment);
        viewPager.setAdapter(viewPagerMainAdapter);
        viewPager.setCurrentItem(1);
    }

    public void playAudio(int audioIndex) {
        if (!checkPlayNewInStorage) {
            Toast.makeText(getApplication(), "If", Toast.LENGTH_SHORT).show();
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio((ArrayList<Audio>) listTopSong);
            storage.storeAudioIndex(audioIndex);
            checkPlayNewInStorage = true;

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO_OUT_STORAGE);
            sendBroadcast(broadcastIntent);
        } else {
            Toast.makeText(getApplication(), "Else", Toast.LENGTH_SHORT).show();
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);


            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO_IN_STORAGE);
            sendBroadcast(broadcastIntent);
        }

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
        Toast.makeText(MainActivity.this, "Yeah doi bai", Toast.LENGTH_SHORT).show();
        }
    };

    private void register_serviceNotifyChangeAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(MainActivity.Broadcast_SERVICE_NOTIFY_CHANGE_AUDIO);
        registerReceiver(serviceNotifyChangeAudio, filter);
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    private void refreshData() {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (MediaPlayerService.mediaPlayer != null) {
                if (data.getBooleanExtra("mediaChange", false)) {
                    Toast.makeText(this, "mediaChange", Toast.LENGTH_SHORT).show();
                    refreshData();
                    checkPlayNewInStorage = false;
                }
            }
            if (data.getBooleanExtra("profileAccountChange", false)) {
                Toast.makeText(this, "profileAccountChange", Toast.LENGTH_SHORT).show();
                loadDataUser();
            }
        }
    }

    @Override
    public void onClickSlideshowListener(int position) {
        Toast.makeText(this, position + "ssssssssssssssssssssssssssssssss", Toast.LENGTH_LONG).show();
    }

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

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        drawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        drawerToggle.onConfigurationChanged(newConfig);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            unregisterReceiver(serviceNotifyChangeAudio);
        }
    }

}
