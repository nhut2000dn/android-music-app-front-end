package com.client.mymusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.entities.PersonalPlaylistHot;
import com.client.mymusic.entities.Playlist;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonalPlaylistHotAdapter extends RecyclerView.Adapter<PersonalPlaylistHotAdapter.RecyclerViewHolder>  {

  private List<PersonalPlaylistHot> mData;

  private PersonalPlaylistHotAdapter.OnListenerRow mOnListenerRowSong;


  public PersonalPlaylistHotAdapter(List<PersonalPlaylistHot> mData, PersonalPlaylistHotAdapter.OnListenerRow onListenerRow) {
    this.mData = mData;
    this.mOnListenerRowSong = onListenerRow;
  }

  @NonNull
  @Override
  public PersonalPlaylistHotAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_personal_playlist_host, parent, false);
    return new PersonalPlaylistHotAdapter.RecyclerViewHolder(view, mOnListenerRowSong);
  }

  @Override
  public void onBindViewHolder(@NonNull PersonalPlaylistHotAdapter.RecyclerViewHolder holder, int position) {
    holder.name.setText(mData.get(position).getName());
    holder.nameUser.setText(mData.get(position).getNameUser());
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
    TextView nameUser;
    TextView name;
    TextView views;

    PersonalPlaylistHotAdapter.OnListenerRow onListenerRowSong;

    public RecyclerViewHolder(@NonNull View itemView, PersonalPlaylistHotAdapter.OnListenerRow onListenerRowSong) {
      super(itemView);

      image = itemView.findViewById(R.id.image);
      name = itemView.findViewById(R.id.name);
      nameUser = itemView.findViewById(R.id.name_user);
      views = itemView.findViewById(R.id.views);

      this.onListenerRowSong = onListenerRowSong;

      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      onListenerRowSong.onClickRow(getAdapterPosition(), "PersonalPlaylistHot");
    }
  }

  public interface OnListenerRow {
    void onClickRow(int position, String action);
  }

}