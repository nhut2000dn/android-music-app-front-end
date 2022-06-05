package com.client.mymusic.adapters;

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

public class ArtistDetailsAdapter extends RecyclerView.Adapter<ArtistDetailsAdapter.RecyclerViewHolder> {

  private List<Audio> mData;

  private ArtistDetailsAdapter.OnListenerRow mOnListenerRowSong;

  private ArtistDetailsAdapter.OnListenerMenuOption mOnListenerMenuOption;


  public ArtistDetailsAdapter(List<Audio> mData, ArtistDetailsAdapter.OnListenerRow onListenerRow, ArtistDetailsAdapter.OnListenerMenuOption onListenerMenuOption) {
    this.mData = mData;
    this.mOnListenerRowSong = onListenerRow;
    this.mOnListenerMenuOption = onListenerMenuOption;
  }

  @NonNull
  @Override
  public ArtistDetailsAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_song, parent, false);
    return new ArtistDetailsAdapter.RecyclerViewHolder(view, mOnListenerRowSong, mOnListenerMenuOption);
  }

  @Override
  public void onBindViewHolder(@NonNull ArtistDetailsAdapter.RecyclerViewHolder holder, int position) {
    holder.name.setText(mData.get(position).getTitle());
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
    TextView name;
    TextView artist;
    TextView views;
    ImageView menuOption;

    ArtistDetailsAdapter.OnListenerRow onListenerRowSong;

    ArtistDetailsAdapter.OnListenerMenuOption onListenerMenuOption;

    public RecyclerViewHolder(@NonNull View itemView, ArtistDetailsAdapter.OnListenerRow onListenerRowSong, ArtistDetailsAdapter.OnListenerMenuOption onListenerMenuOption) {
      super(itemView);

      image = itemView.findViewById(R.id.image_song);
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
        onListenerRowSong.onClickRow(getAdapterPosition());
      } else if (v == menuOption) {
        onListenerMenuOption.onClickMenuOption(getAdapterPosition(), itemView);
      }
    }
  }

  public interface OnListenerRow {
    void onClickRow(int position);
  }

  public interface OnListenerMenuOption {
    void onClickMenuOption(int position, View view);
  }
}