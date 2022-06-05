package com.client.mymusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.entities.Artist;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.RecyclerViewHolder> {

  private List<Artist> mData;

  private ArtistAdapter.OnListenerRow mOnListenerRow;


  public ArtistAdapter(List<Artist> mData, ArtistAdapter.OnListenerRow onListenerRow) {
    this.mData = mData;
    this.mOnListenerRow = onListenerRow;
  }

  @NonNull
  @Override
  public ArtistAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_artist, parent, false);
    return new ArtistAdapter.RecyclerViewHolder(view, mOnListenerRow);
  }

  @Override
  public void onBindViewHolder(@NonNull ArtistAdapter.RecyclerViewHolder holder, int position) {
    holder.name.setText(mData.get(position).getName());
    holder.countSongs.setText(mData.get(position).getCount_songs() + " Songs");
//    Picasso.get().load(mData.get(position).getAvatar())
//            .fit().placeholder(R.drawable.playholder_music).into(holder.avatar);
    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + mData.get(position).getAvatar())
            .fit().placeholder(R.drawable.playholder_music).into(holder.avatar);
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    RoundedImageView avatar;
    TextView name;
    TextView countSongs;

    ArtistAdapter.OnListenerRow onListenerRow;

    public RecyclerViewHolder(@NonNull View itemView, ArtistAdapter.OnListenerRow onListenerRow) {
      super(itemView);

      avatar = itemView.findViewById(R.id.avatar);
      name = itemView.findViewById(R.id.name);
      countSongs = itemView.findViewById(R.id.count_songs);

      this.onListenerRow = onListenerRow;

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      onListenerRow.onClickRow(getAdapterPosition());
    }
  }

  public interface OnListenerRow {
    void onClickRow(int position);
  }
}
