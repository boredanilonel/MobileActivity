package com.example.zhuk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RecordsAdapter extends ArrayAdapter<ScoreRecord> {

    public RecordsAdapter(Context context, List<ScoreRecord> scores) {
        super(context, 0, scores);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_record, parent, false);
        }

        ScoreRecord record = getItem(position);

        if (record != null) {
            TextView tvPosition = convertView.findViewById(R.id.tvPosition);
            TextView tvPlayerName = convertView.findViewById(R.id.tvPlayerName);
            TextView tvScore = convertView.findViewById(R.id.tvScore);
            TextView tvDifficulty = convertView.findViewById(R.id.tvDifficulty);
            TextView tvDate = convertView.findViewById(R.id.tvDate);

            tvPosition.setText(String.valueOf(position + 1));
            tvPlayerName.setText(record.getPlayerName());
            tvScore.setText(String.valueOf(record.getScore()) + " очков");
            tvDifficulty.setText(record.getDifficultyName());
            tvDate.setText(record.getFormattedDate());

            if (position == 0) {
                convertView.setBackgroundColor(0x30FFD700);
            } else if (position == 1) {
                convertView.setBackgroundColor(0x30C0C0C0);
            } else if (position == 2) {
                convertView.setBackgroundColor(0x30CD7F32);
            } else {
                convertView.setBackgroundColor(0x00000000);
            }
        }

        return convertView;
    }
}