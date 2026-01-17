package com.libookproject.libookapp;

import static com.libookproject.libookapp.BooksApiService.searchBooks;
import com.libookproject.libookapp.ApiCallback;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private View view;
    private EditText eTSearch;
    private ListView lVBooks;
    private ImageButton btnSearch;
    private ArrayList<LiteBook> booksList;
    private CustomAdapterSearch adp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        init();
        btnSearch.setOnClickListener(v -> searchClicked());

        return view;
    }

    private void init()
    {
        eTSearch = view.findViewById(R.id.eTSearch);
        lVBooks = view.findViewById(R.id.lVBooks);
        btnSearch = view.findViewById(R.id.btnSearch);

        booksList = new ArrayList<>();
        adp = new CustomAdapterSearch(getContext(), booksList);
        lVBooks.setAdapter(adp);
    }

    public void searchClicked() {
        String q = eTSearch.getText().toString();
        Toast.makeText(getContext(), "inside func", Toast.LENGTH_LONG).show();

        if (q.length() == 0) {
            eTSearch.setError("Field can't be empty");
            return;
        }

        SearchRequest request = new SearchRequest(q, null, 0);

        searchBooks(request, new ApiCallback<LiteBook>() {
            @Override
            public void onSearchResultsLoaded(List<LiteBook> books) {
                if (isAdded())
                {
                    booksList.clear();
                    booksList.addAll(books);
                    adp.notifyDataSetChanged();
                }
            }

            @Override
            public void onSearchResultsError(String err) {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                System.out.println(err);
            }
        });
    }
}