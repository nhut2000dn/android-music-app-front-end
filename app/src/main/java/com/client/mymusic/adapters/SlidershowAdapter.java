package com.client.mymusic.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.client.mymusic.R;
import com.client.mymusic.activities.IsPlayingActivity;
import com.client.mymusic.activities.MainActivity;
import com.client.mymusic.entities.Audio;
import com.client.mymusic.entities.Slidershow;
import com.client.mymusic.utils.StorageUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SlidershowAdapter extends SliderViewAdapter<SlidershowAdapter.SliderAdapterVH> {

  private Context context;
  private List<Slidershow> mData;
  private SlidershowAdapter.OnListenerRow mOnListenerRowSong;


  private Boolean checkPlayNewInStorage = false;

  private static Boolean checkClickPlay = false;

  public SlidershowAdapter(Context context, List<Slidershow> mData, SlidershowAdapter.OnListenerRow onListenerRow) {
    this.context = context;
    this.mData = mData;
    this.mOnListenerRowSong = onListenerRow;
  }

  @Override
  public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.image_slider_layout_item, parent, false);
    return new SliderAdapterVH(view, mOnListenerRowSong);
  }

  @Override
  public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

    viewHolder.title.setText(mData.get(position).getTitle());
//    Picasso.get().load(mData.get(position).getImageUrl())
//            .fit().placeholder(R.drawable.playholder_music).into(viewHolder.image);
    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + mData.get(position).getImageUrl())
            .fit().placeholder(R.drawable.playholder_music).into(viewHolder.image);
    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        playAudio(position);
        MainActivity.togglePlayAudio.setImageResource(R.drawable.ic_pause_circle_outline);
        MainActivity.nameSongBar.setText(MainActivity.listTopSong.get(position).getTitle());
        MainActivity.nameArtistBar.setText(MainActivity.listTopSong.get(position).getArtist());
//        Picasso.get().load(listTopSong.get(position).getImage())
//                .fit().placeholder(R.drawable.playholder_music).into(imageSongBar);
        Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + MainActivity.listTopSong.get(position).getImage())
                .fit().placeholder(R.drawable.playholder_music).into(MainActivity.imageSongBar);
        Activity activity = (Activity) context;
        context.startActivity(new Intent(context, IsPlayingActivity.class));
        activity.overridePendingTransition( R.anim.slide_in_up, R.anim.no_change );
      }
    });
  }

  @Override
  public int getCount() {
    //slider view count could be dynamic size
    return mData.size();
  }

  class SliderAdapterVH extends SliderViewAdapter.ViewHolder implements View.OnClickListener {

    View itemView;

    TextView title;
    RoundedImageView image;
    SlidershowAdapter.OnListenerRow onListenerRowSong;

    public SliderAdapterVH(View itemView, SlidershowAdapter.OnListenerRow onListenerRowSong) {
      super(itemView);
      this.itemView = itemView;

      title = itemView.findViewById(R.id.title);
      image = itemView.findViewById(R.id.image);
      this.onListenerRowSong = onListenerRowSong;
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      onListenerRowSong.onClickRow(getItemPosition(itemView), "SlideShow");
    }
  }

  public interface OnListenerRow {
    void onClickRow(int position, String action);
  }

  public void playAudio(int audioIndex) {
    if (!checkPlayNewInStorage) {
      StorageUtil storage = new StorageUtil(context);
      storage.storeAudio((ArrayList<Audio>) MainActivity.listTopSong);
      storage.storeAudioIndex(audioIndex);
      checkPlayNewInStorage = true;
      checkClickPlay = true;

      Intent broadcastIntent = new Intent(MainActivity.Broadcast_PLAY_NEW_AUDIO_OUT_STORAGE);
      context.sendBroadcast(broadcastIntent);
    } else {
      StorageUtil storage = new StorageUtil(context);
      storage.storeAudioIndex(audioIndex);


      Intent broadcastIntent = new Intent(MainActivity.Broadcast_PLAY_NEW_AUDIO_IN_STORAGE);
      context.sendBroadcast(broadcastIntent);
    }
  }

}