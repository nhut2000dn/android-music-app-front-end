package com.client.mymusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.entities.Audio;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class SongsPersonalAdapter extends RecyclerView.Adapter<SongsPersonalAdapter.RecyclerViewHolder> implements Filterable {

    private List<Audio> mData;
    private List<Audio> mDataFull;

    private OnListenerRowSong mOnListenerRowSong;


    public SongsPersonalAdapter(List<Audio> mData, OnListenerRowSong onListenerRowSong) {
        this.mData = mData;
        this.mOnListenerRowSong = onListenerRowSong;
        mDataFull = new ArrayList<>(mData);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_songs_personal, parent, false);
        return new RecyclerViewHolder(view, mOnListenerRowSong);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.imageSong.setImageResource(R.drawable.ic_launcher_background);
        holder.nameSong.setText(mData.get(position).getTitle());
        holder.nameArtist.setText(mData.get(position).getArtist());
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

        RoundedImageView imageSong;
        TextView nameSong;
        TextView nameArtist;

        OnListenerRowSong onListenerRowSong;

        public RecyclerViewHolder(@NonNull View itemView, OnListenerRowSong onListenerRowSong) {
            super(itemView);

            imageSong = itemView.findViewById(R.id.image_song);
            nameSong = itemView.findViewById(R.id.name_song);
            nameArtist = itemView.findViewById(R.id.name_artist);

            this.onListenerRowSong = onListenerRowSong;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListenerRowSong.onClickRowSong(getAdapterPosition());
        }
    }

    public interface OnListenerRowSong {
        void onClickRowSong(int position);
    }

}
