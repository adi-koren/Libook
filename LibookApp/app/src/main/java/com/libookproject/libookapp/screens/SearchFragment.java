package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.serverApi.BooksApiService.searchBooks;

import com.libookproject.libookapp.CustomAdapterSearch;
import com.libookproject.libookapp.LiteBook;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.SearchRequest;
import com.libookproject.libookapp.serverApi.ApiCallback;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener{
    private View view;
    private EditText eTSearch;
    private ListView lVBooks;
    private ImageButton btnSearch;
    private ArrayList<LiteBook> booksList;
    private CustomAdapterSearch adp;

    private View footerView;
    private TextView tvShowMore;
    private int startIndex = 0;
    private boolean SEARCH_CLICKED_MODE = true;

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
        btnSearch.setOnClickListener(v -> searchClicked(SEARCH_CLICKED_MODE));

        return view;
    }

    private void init()
    {
        eTSearch = view.findViewById(R.id.eTSearch);
        lVBooks = view.findViewById(R.id.lVBooks);
        btnSearch = view.findViewById(R.id.btnSearch);

        footerView = LayoutInflater.from(getContext())
                .inflate(R.layout.show_more_footer, lVBooks, false);

        tvShowMore = footerView.findViewById(R.id.tvShowMore);
        tvShowMore.setOnClickListener(v -> {
            searchClicked(!SEARCH_CLICKED_MODE);
        });

        footerView.setVisibility(View.GONE);
        lVBooks.addFooterView(footerView);
        lVBooks.setOnItemClickListener(this);

        booksList = new ArrayList<>();
        adp = new CustomAdapterSearch(getContext(), booksList);
        lVBooks.setAdapter(adp);
    }

    public void searchClicked(boolean isSearchClickedMode) {

        String q = eTSearch.getText().toString();

        if (q.length() == 0)
        {
            eTSearch.setError("Field can't be empty");
            return;
        }

        if (isSearchClickedMode)
        {
            startIndex = 0;
            footerView.setVisibility(View.GONE);
        }
        else
        {
            tvShowMore.setText("Loading...");
            tvShowMore.setEnabled(false);
        }

        SearchRequest request = new SearchRequest(q, null, startIndex);

        searchBooks(request, new ApiCallback<LiteBook>() {
            @Override
            public void onSearchResultsLoaded(List<LiteBook> books) {
                if (isAdded())
                {
                    if (isSearchClickedMode)
                    {
                        booksList.clear();
                    }
                    booksList.addAll(books);
                    adp.notifyDataSetChanged();

                    if (books.size() == 10)
                    {
                        footerView.setVisibility(View.VISIBLE);
                        tvShowMore.setEnabled(true);
                    }
                    else
                    {
                        footerView.setVisibility(View.GONE);
                    }
                    tvShowMore.setText("Show more");
                    startIndex += books.size();
                }
            }

            @Override
            public void onSearchResultsError(String err) {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                System.out.println(err);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        BookInfoFragment bookInfoFragment = new BookInfoFragment();

        Bundle bundle = new Bundle();
        //bundle.putParcelable("liteBook", booksList.get(position));
        bundle.putString("id", view.getTag().toString());
        bookInfoFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, bookInfoFragment)
                .addToBackStack(null)
                .commit();
    }
}