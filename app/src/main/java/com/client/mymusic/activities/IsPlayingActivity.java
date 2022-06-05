package com.client.mymusic.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.client.mymusic.R;
import com.client.mymusic.adapters.IsPlayinglTabAdapter;
import com.client.mymusic.entities.Audio;
import com.client.mymusic.utils.MediaPlayerService;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IsPlayingActivity extends AppCompatActivity {

  IsPlayinglTabAdapter isPlayinglTabAdapter;

  @BindView(R.id.btn_down)
  ImageView btnDown;

  @BindView(R.id.tab_layout_audio)
  TabLayout tabLayoutAudio;

  @BindView(R.id.view_pager_slide_up_audio)
  ViewPager viewPagerSlideUpAudio;

  private static MediaPlayerService player;

  public static ArrayList<Audio> audioListStorageUltil;
  public static int audioIndex;

  private int currentPosition;

  @BindView(R.id.background)
  ImageView background;

  private static final String TAG = "IsPlayingActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_is_playing);

    ButterKnife.bind(this);

    setPagerAdapter();
//    Picasso.get().load(MediaPlayerService.activeAudio.getImage())
//            .fit().into(background);
    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + MediaPlayerService.activeAudio.getImage())
            .fit().into(background);
    register_serviceNotifyChangeAudio();
  }

  @OnClick(R.id.btn_down)
  void intentBack() {
    finish();
    overridePendingTransition( R.anim.no_change, R.anim.slide_out_up );
  }

  public void setPagerAdapter() {
    isPlayinglTabAdapter = new IsPlayinglTabAdapter(getSupportFragmentManager(), tabLayoutAudio.getTabCount());
    viewPagerSlideUpAudio.setAdapter(isPlayinglTabAdapter);
    viewPagerSlideUpAudio.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutAudio));
    tabLayoutAudio.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        viewPagerSlideUpAudio.setCurrentItem(tab.getPosition());
        if (tab.getPosition() == 0 || tab.getPosition() == 1 || tab.getPosition() == 2) {
//          isPlayinglTabAdapter.notifyDataSetChanged();
        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });
    viewPagerSlideUpAudio.setCurrentItem(1);
  }

  private BroadcastReceiver serviceNotifyChangeAudio = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Toast.makeText(getApplicationContext(), "Yeah doi bai", Toast.LENGTH_SHORT).show();
      setPagerAdapter();
//      Picasso.get().load(MediaPlayerService.activeAudio.getImage())
//              .fit().into(background);
      Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + MediaPlayerService.activeAudio.getImage())
              .fit().into(background);
    }
  };

  private void register_serviceNotifyChangeAudio() {
    IntentFilter filter = new IntentFilter(MainActivity.Broadcast_SERVICE_NOTIFY_CHANGE_AUDIO);
    registerReceiver(serviceNotifyChangeAudio, filter);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(serviceNotifyChangeAudio);
  }


}
