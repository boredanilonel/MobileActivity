package com.example.zhuk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentAuthors extends Fragment {

    private ListView listViewAuthors;
    private AuthorAdapter authorAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authors, container, false);

        listViewAuthors = view.findViewById(R.id.listViewAuthors);
        setupAuthorsList();

        return view;
    }

    private void setupAuthorsList() {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author("Власова Марина", R.drawable.kitty1));
        authors.add(new Author("Власов Данил", R.drawable.kitty2jpg));
        authorAdapter = new AuthorAdapter(getContext(), authors);
        listViewAuthors.setAdapter(authorAdapter);
    }
}