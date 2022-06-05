package com.client.mymusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.entities.Theme;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.RecyclerViewHolder>  {

  private List<Theme> mData;

  private OnListenerRow mOnListenerRow;


  public ThemeAdapter(List<Theme> mData, OnListenerRow onListenerRow) {
    this.mData = mData;
    this.mOnListenerRow = onListenerRow;
  }

  @NonNull
  @Override
  public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_theme, parent, false);
    return new RecyclerViewHolder(view, mOnListenerRow);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
    holder.name.setText(mData.get(position).getName());
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

    OnListenerRow onListenerRow;

    public RecyclerViewHolder(@NonNull View itemView, OnListenerRow onListenerRow) {
      super(itemView);

      image = itemView.findViewById(R.id.image);
      name = itemView.findViewById(R.id.name);

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