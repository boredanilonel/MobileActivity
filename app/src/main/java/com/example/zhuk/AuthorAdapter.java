package com.example.zhuk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AuthorAdapter extends ArrayAdapter<Author> {

    public AuthorAdapter(Context context, List<Author> authors) {
        super(context, 0, authors);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_author, parent, false);
        }

        Author author = getItem(position);

        ImageView imageViewPhoto = convertView.findViewById(R.id.imageViewPhoto);
        TextView textViewName = convertView.findViewById(R.id.textViewName);

        imageViewPhoto.setImageResource(author.getPhotoResId());
        textViewName.setText(author.getName());

        return convertView;
    }
}