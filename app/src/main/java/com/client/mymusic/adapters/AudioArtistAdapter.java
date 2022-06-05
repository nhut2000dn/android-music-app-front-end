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

public class AudioArtistAdapter extends RecyclerView.Adapter<AudioArtistAdapter.RecyclerViewHolder>  {

  private List<Audio> mData;

  private AudioArtistAdapter.OnListenerRow mOnListenerRowSong;


  public AudioArtistAdapter(List<Audio> mData, AudioArtistAdapter.OnListenerRow onListenerRow) {
    this.mData = mData;
    this.mOnListenerRowSong = onListenerRow;
  }

  @NonNull
  @Override
  public AudioArtistAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_audio_artist_song, parent, false);
    return new AudioArtistAdapter.RecyclerViewHolder(view, mOnListenerRowSong);
  }

  @Override
  public void onBindViewHolder(@NonNull AudioArtistAdapter.RecyclerViewHolder holder, int position) {
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

    AudioArtistAdapter.OnListenerRow onListenerRowSong;

    public RecyclerViewHolder(@NonNull View itemView, AudioArtistAdapter.OnListenerRow onListenerRowSong) {
      super(itemView);

      image = itemView.findViewById(R.id.image_song);
      name = itemView.findViewById(R.id.name_song);
      artist = itemView.findViewById(R.id.name_artist);
      views = itemView.findViewById(R.id.views_song);

      this.onListenerRowSong = onListenerRowSong;

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      onListenerRowSong.onClickRow(getAdapterPosition());
    }
  }

  public interface OnListenerRow {
    void onClickRow(int position);
  }

}
