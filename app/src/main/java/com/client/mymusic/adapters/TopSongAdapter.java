package com.client.mymusic.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.entities.Audio;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TopSongAdapter extends RecyclerView.Adapter<TopSongAdapter.RecyclerViewHolder>  {

  private List<Audio> mData;

  private TopSongAdapter.OnListenerRow mOnListenerRowSong;

  private TopSongAdapter.OnListenerMenuOption mOnListenerMenuOption;


  public TopSongAdapter(List<Audio> mData, TopSongAdapter.OnListenerRow onListenerRow, TopSongAdapter.OnListenerMenuOption onListenerMenuOption) {
    this.mData = mData;
    this.mOnListenerRowSong = onListenerRow;
    this.mOnListenerMenuOption = onListenerMenuOption;
  }

  @NonNull
  @Override
  public TopSongAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_charts_song, parent, false);
    return new TopSongAdapter.RecyclerViewHolder(view, mOnListenerRowSong, mOnListenerMenuOption);
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void onBindViewHolder(@NonNull TopSongAdapter.RecyclerViewHolder holder, int position) {
    holder.name.setText(mData.get(position).getTitle());
    if (position < 9) {
      holder.numericalOrder.setText( "0" + (position + 1));
    } else {
      holder.numericalOrder.setText( "" + (position + 1));
    }
    if (position == 0) {
      holder.numericalOrder.setTextColor(Color.parseColor("#31a7e0"));
    } else if (position == 1) {
      holder.numericalOrder.setTextColor(Color.parseColor("#31e087"));
    } else if (position == 2) {
      holder.numericalOrder.setTextColor(Color.parseColor("#e08b31"));
    }
    holder.artist.setText(mData.get(position).getArtist());
    holder.views.setText(mData.get(position).getViews() + "");
//    Picasso.get().load(mData.get(position).getImage())
//            .fit().placeholder(R.drawable.playholder_music).into(holder.image);
    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + mData.get(position).getImage())
            .fit().placeholder(R.drawable.playholder_music).into(holder.image);
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView image;
    TextView numericalOrder;
    TextView name;
    TextView artist;
    TextView views;
    ImageView menuOption;

    TopSongAdapter.OnListenerRow onListenerRowSong;

    TopSongAdapter.OnListenerMenuOption onListenerMenuOption;

    public RecyclerViewHolder(@NonNull View itemView, TopSongAdapter.OnListenerRow onListenerRowSong, TopSongAdapter.OnListenerMenuOption onListenerMenuOption) {
      super(itemView);

      image = itemView.findViewById(R.id.image_song);
      numericalOrder = itemView.findViewById(R.id.numerical_order);
      name = itemView.findViewById(R.id.name_song);
      artist = itemView.findViewById(R.id.name_artist);
      views = itemView.findViewById(R.id.views_song);
      menuOption = itemView.findViewById(R.id.menu_option);

      this.onListenerRowSong = onListenerRowSong;

      this.onListenerMenuOption = onListenerMenuOption;

      itemView.setOnClickListener(this);

      menuOption.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      if (v == itemView) {
        onListenerRowSong.onClickRow(getAdapterPosition(), "TopSong");
      } else if (v == menuOption) {
        onListenerMenuOption.onClickMenuOption(getAdapterPosition(), itemView);
      }
    }
  }

  public interface OnListenerRow {
    void onClickRow(int position, String action);
  }

  public interface OnListenerMenuOption {
    void onClickMenuOption(int position, View view);
  }

}