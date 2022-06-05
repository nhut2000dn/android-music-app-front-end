package com.client.mymusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.entities.PersonalPlaylist;

import java.util.List;

public class PersonalPlaylistAdapter extends RecyclerView.Adapter<PersonalPlaylistAdapter.RecyclerViewHolder>  {

  private List<PersonalPlaylist> mData;

  private PersonalPlaylistAdapter.OnListenerRow mOnListenerRowSong;

  private PersonalPlaylistAdapter.OnListenerMenuOption mOnListenerMenuOption;


  public PersonalPlaylistAdapter(List<PersonalPlaylist> mData, PersonalPlaylistAdapter.OnListenerRow onListenerRow, PersonalPlaylistAdapter.OnListenerMenuOption onListenerMenuOption) {
    this.mData = mData;
    this.mOnListenerRowSong = onListenerRow;
    this.mOnListenerMenuOption = onListenerMenuOption;
  }

  @NonNull
  @Override
  public PersonalPlaylistAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_personal_playlist, parent, false);
    return new PersonalPlaylistAdapter.RecyclerViewHolder(view, mOnListenerRowSong, mOnListenerMenuOption);
  }

  @Override
  public void onBindViewHolder(@NonNull PersonalPlaylistAdapter.RecyclerViewHolder holder, int position) {
    holder.name.setText(mData.get(position).getName());
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView name;
    ImageView menuOption;

    PersonalPlaylistAdapter.OnListenerRow onListenerRowSong;

    PersonalPlaylistAdapter.OnListenerMenuOption onListenerMenuOption;

    public RecyclerViewHolder(@NonNull View itemView, PersonalPlaylistAdapter.OnListenerRow onListenerRowSong, PersonalPlaylistAdapter.OnListenerMenuOption onListenerMenuOption) {
      super(itemView);

      name = itemView.findViewById(R.id.name);

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