package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import models.Parts;

public class PartsAdapter extends ArrayAdapter<Parts> {
    private Context context;
    private List<Parts> partList;

    public PartsAdapter(Context context, List<Parts> partList) {
        super(context, 0, partList);
        this.context = context;
        this.partList = partList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.part_item, parent, false);
        }

        // Get the current part
        Parts part = getItem(position);

        // Populate the views with data
        TextView nameTextView = convertView.findViewById(R.id.part_name);
        TextView modelTextView = convertView.findViewById(R.id.part_model);
        TextView quantityTextView = convertView.findViewById(R.id.part_quantity);
        ImageView imageView = convertView.findViewById(R.id.part_image);

        nameTextView.setText(part.getName());
        modelTextView.setText(part.getModel());
        quantityTextView.setText("Quantity: " + part.getQuantity());

        // Učitavanje slike sa URL-a koristeći Picasso
        String imageUrl = part.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_plus); // Default image if no URL
        }

        return convertView;
    }
}