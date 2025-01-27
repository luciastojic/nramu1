package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import models.Parts;

public class PartsAdapter extends RecyclerView.Adapter<PartsAdapter.PartsViewHolder> {

    private Context context;
    private List<Parts> partsList;

    public void setFilteredList(List<Parts> filteredList){
        this.partsList = filteredList;
        notifyDataSetChanged();
    }
    public PartsAdapter(Context context, List<Parts> partsList) {
        this.context = context;
        this.partsList = partsList;
    }

    @Override
    public PartsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.part_item, parent, false);
        return new PartsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PartsViewHolder holder, int position) {
        Parts part = partsList.get(position);
        holder.nameTextView.setText(part.getName());
        holder.modelTextView.setText(part.getModel());
        holder.quantityTextView.setText("Quantity: " + part.getQuantity());
    }

    @Override
    public int getItemCount() {
        return partsList.size();
    }

    public static class PartsViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, modelTextView, quantityTextView;
        ImageView imageView;

        public PartsViewHolder(View itemView) {
            super(itemView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
        }
    }
}
