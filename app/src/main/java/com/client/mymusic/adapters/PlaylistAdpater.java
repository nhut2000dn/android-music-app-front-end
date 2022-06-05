package com.client.mymusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.entities.Playlist;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaylistAdpater extends RecyclerView.Adapter<PlaylistAdpater.RecyclerViewHolder>  {

  private List<Playlist> mData;

  private PlaylistAdpater.OnListenerRow mOnListenerRowSong;


  public PlaylistAdpater(List<Playlist> mData, PlaylistAdpater.OnListenerRow onListenerRow) {
    this.mData = mData;
    this.mOnListenerRowSong = onListenerRow;
  }

  @NonNull
  @Override
  public PlaylistAdpater.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_playlist, parent, false);
    return new PlaylistAdpater.RecyclerViewHolder(view, mOnListenerRowSong);
  }

  @Override
  public void onBindViewHolder(@NonNull PlaylistAdpater.RecyclerViewHolder holder, int position) {
    holder.name.setText(mData.get(position).getName());
    holder.views.setText(mData.get(position).getViews() + "");
//    Picasso.get().load(mData.get(position).getImage())
//            .fit().placeholder(R.drawable.playholder_music).into(holder.image);
    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + mData.get(position).getImage())
            .fit().placeholder(R.drawable.placeholder_playlist).into(holder.image);
}

  @Override
  public int getItemCount() {
    return mData.size();
  }

  public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView image;
    TextView name;
    TextView views;

    PlaylistAdpater.OnListenerRow onListenerRowSong;

    public RecyclerViewHolder(@NonNull View itemView, PlaylistAdpater.OnListenerRow onListenerRowSong) {
      super(itemView);

      image = itemView.findViewById(R.id.image);
      name = itemView.findViewById(R.id.name);
      views = itemView.findViewById(R.id.views);

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
