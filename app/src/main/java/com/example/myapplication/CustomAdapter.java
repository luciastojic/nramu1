package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import models.Parts;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Parts> partsList;
    private List<Parts> filteredList;

    public CustomAdapter(Context context, List<Parts> partsList) {
        this.context = context;
        this.partsList = partsList;
        this.filteredList = new ArrayList<>(partsList);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.part_item, parent, false);
        }

        Parts part = filteredList.get(position);

        TextView name = convertView.findViewById(R.id.part_name);
        TextView model = convertView.findViewById(R.id.part_model);
        TextView quantity = convertView.findViewById(R.id.part_quantity);
        ImageView imageView = convertView.findViewById(R.id.part_image);

        name.setText("Name: " + part.getName());
        model.setText("Model: " + part.getModel());
        quantity.setText("Quantity: " + part.getQuantity());

        // Učitavanje slike pomoću Picassa
        if (part.getImageUrl() != null && !part.getImageUrl().isEmpty()) {
            Picasso.get().load(part.getImageUrl()).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_plus); // Placeholder ako nema slike
        }

        return convertView;
    }

    // Filtriranje liste
    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(partsList);
        } else {
            for (Parts part : partsList) {
                if (part.getName().toLowerCase().contains(query) || part.getModel().toLowerCase().contains(query)) {
                    filteredList.add(part);
                }
            }
        }
        notifyDataSetChanged();
    }
}
