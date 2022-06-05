package com.client.mymusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.activities.MainActivity;
import com.client.mymusic.entities.Charts;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChartsAdapter extends RecyclerView.Adapter<ChartsAdapter.RecyclerViewHolder>  {

  private List<Charts> mData;

  private Context context;

  private ChartsAdapter.OnListenerRow mOnListenerRowSong;


  public ChartsAdapter(List<Charts> mData, ChartsAdapter.OnListenerRow onListenerRow, Context context) {
    this.mData = mData;
    this.mOnListenerRowSong = onListenerRow;
    this.context = context;
  }

  @NonNull
  @Override
  public ChartsAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_charts, parent, false);
    return new ChartsAdapter.RecyclerViewHolder(view, mOnListenerRowSong);
  }

  @Override
  public void onBindViewHolder(@NonNull ChartsAdapter.RecyclerViewHolder holder, int position) {
    holder.nameSongTop1.setText(mData.get(position).getSongs().get(0).getTitle());
    holder.nameArtistTop1.setText(mData.get(position).getSongs().get(0).getArtist());
    holder.nameSongTop2.setText(mData.get(position).getSongs().get(1).getTitle());
    holder.nameArtistTop2.setText(mData.get(position).getSongs().get(1).getArtist());
    holder.nameSongTop3.setText(mData.get(position).getSongs().get(2).getTitle());
    holder.nameArtistTop3.setText(mData.get(position).getSongs().get(2).getArtist());
    Picasso.get().load("http://192.168.1.117/music_offical/storage/app/public/" + mData.get(position).getImage())
            .fit().placeholder(R.drawable.playholder_music).into(holder.imageCountry);
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    RoundedImageView imageCountry;
    TextView nameSongTop1;
    TextView nameArtistTop1;
    TextView nameSongTop2;
    TextView nameArtistTop2;
    TextView nameSongTop3;
    TextView nameArtistTop3;

    ChartsAdapter.OnListenerRow onListenerRowSong;

    public RecyclerViewHolder(@NonNull View itemView, ChartsAdapter.OnListenerRow onListenerRowSong) {
      super(itemView);

      imageCountry = itemView.findViewById(R.id.image_country);
      nameSongTop1 = itemView.findViewById(R.id.name_song_top1);
      nameArtistTop1 = itemView.findViewById(R.id.name_artist_top1);
      nameSongTop2 = itemView.findViewById(R.id.name_song_top2);
      nameArtistTop2 = itemView.findViewById(R.id.name_artist_top2);
      nameSongTop3 = itemView.findViewById(R.id.name_song_top3);
      nameArtistTop3 = itemView.findViewById(R.id.name_artist_top3);

      this.onListenerRowSong = onListenerRowSong;

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      Toast.makeText(context, mData.get(0).getName(), Toast.LENGTH_LONG).show();
      onListenerRowSong.onClickRow(getAdapterPosition());
    }
  }

  public interface OnListenerRow {
    void onClickRow(int position);
  }

}