package com.client.mymusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.entities.Audio;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchViewMainAdapter extends RecyclerView.Adapter<SearchViewMainAdapter.RecyclerViewHolder> implements Filterable {

  private List<Audio> mData;
  private List<Audio> mDataFull;

  private SearchViewMainAdapter.OnListenerRow mOnListenerRowSong;

  private SearchViewMainAdapter.OnListenerMenuOption mOnListenerMenuOption;


  public SearchViewMainAdapter(List<Audio> mData, SearchViewMainAdapter.OnListenerRow onListenerRow, SearchViewMainAdapter.OnListenerMenuOption onListenerMenuOption) {
    this.mData = mData;
    this.mOnListenerRowSong = onListenerRow;
    this.mOnListenerMenuOption = onListenerMenuOption;
    mDataFull = new ArrayList<>(mData);
  }

  @NonNull
  @Override
  public SearchViewMainAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_song, parent, false);
    return new SearchViewMainAdapter.RecyclerViewHolder(view, mOnListenerRowSong, mOnListenerMenuOption);
  }

  @Override
  public void onBindViewHolder(@NonNull SearchViewMainAdapter.RecyclerViewHolder holder, int position) {
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

  @Override
  public Filter getFilter() {
    return SearchViewManFilter;
  }

  private Filter SearchViewManFilter = new Filter() {
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

      List<Audio> filterList = new ArrayList<>();

      if (constraint == null || constraint.length() == 0) {
        filterList.addAll(mDataFull);
      } else {
        String filterPattern = constraint.toString().toLowerCase().trim();
        for (Audio item : mDataFull) {
          if (item.getTitle().toLowerCase().contains(filterPattern)) {
            filterList.add(item);
          }
        }
      }

      FilterResults results = new FilterResults();
      results.values = filterList;

      return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
      mData.clear();
      mData.addAll((List) results.values);
      notifyDataSetChanged();
    }
  };

  public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView image;
    TextView name;
    TextView artist;
    TextView views;
    ImageView menuOption;

    SearchViewMainAdapter.OnListenerRow onListenerRowSong;

    SearchViewMainAdapter.OnListenerMenuOption onListenerMenuOption;

    public RecyclerViewHolder(@NonNull View itemView, SearchViewMainAdapter.OnListenerRow onListenerRowSong, SearchViewMainAdapter.OnListenerMenuOption onListenerMenuOption) {
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
