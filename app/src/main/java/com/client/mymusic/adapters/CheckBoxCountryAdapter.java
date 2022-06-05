package com.client.mymusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.client.mymusic.R;
import com.client.mymusic.entities.Country;

import java.util.List;

public class CheckBoxCountryAdapter extends RecyclerView.Adapter<CheckBoxCountryAdapter.RecyclerViewHolder> {

  private List<Country> mData;

  public interface OnItemCheckListener {
    void onItemCheck(Country item);
    void onItemUncheck(Country item);
  }

  @NonNull
  private OnItemCheckListener onItemCheckListener;

  public CheckBoxCountryAdapter(List<Country> mData, @NonNull OnItemCheckListener onItemCheckListener) {
    this.mData = mData;
    this.onItemCheckListener = onItemCheckListener;
  }

  @NonNull
  @Override
  public CheckBoxCountryAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.row_checkbox_country, parent, false);
    return new CheckBoxCountryAdapter.RecyclerViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull CheckBoxCountryAdapter.RecyclerViewHolder holder, int position) {
    final Country currentItem = mData.get(position);
    holder.checkBox.setText(mData.get(position).getName());
    holder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        holder.checkBox.setChecked(!(holder.checkBox.isChecked()));
        if (holder.checkBox.isChecked()) {
          onItemCheckListener.onItemCheck(currentItem);
        } else {
          onItemCheckListener.onItemUncheck(currentItem);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

    CheckBox checkBox;

    public RecyclerViewHolder(@NonNull View itemView) {
      super(itemView);

      checkBox = itemView.findViewById(R.id.checkbox_country);
      checkBox.setClickable(false);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
      itemView.setOnClickListener(onClickListener);
    }
  }

}
