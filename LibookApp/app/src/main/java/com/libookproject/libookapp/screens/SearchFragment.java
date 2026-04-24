package com.libookproject.libookapp.screens;

import com.libookproject.libookapp.adapters.CustomAdapterSearch;
import com.libookproject.libookapp.LiteBook;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.SearchRequest;
import com.libookproject.libookapp.serverApi.ApiCallback;
import com.libookproject.libookapp.serverApi.BooksApiService;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener{
    private View view;
    private EditText eTSearch;
    private ListView lVBooks;
    private ImageButton btnSearch;
    private ArrayList<LiteBook> booksList;
    private CustomAdapterSearch adp;

    private LinearLayout layoutFilterHeader;
    private ScrollView scrollFilters;
    private ImageView iVArrow;
    private boolean isExpanded = false;
    private EditText eTTitle;
    private EditText eTAuthor;
    private EditText eTISBN;
    private LinearLayout layoutIncludeSubjects;
    private LinearLayout layoutExcludeSubjects;
    private LinearLayout layoutLanguages;
    private RadioButton radioSubjAll;
    private RadioButton radioLangAll;
    private Button btnAdvancedSearch;
    private Button btnSearchISBN;

    private View footerView;
    private TextView tvShowMore;
    private int startIndex = 0;

    private SearchRequest prevRequest = null;
    private final int NORMAL_SEARCH_MODE = 0;
    private final int VIEW_MORE_SEARCH_MODE = 1;
    private final int ADVANCED_SEARCH_MODE = 2;
    private final int ISBN_SEARCH_MODE = 3;

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
        btnSearch.setOnClickListener(v -> {
            SearchRequest request = buildSearchRequest(NORMAL_SEARCH_MODE);
            if (request != null)
            {
                searchClicked(request, false);
            }
        });

        layoutFilterHeader.setOnClickListener(v -> toggleFilters());
        btnAdvancedSearch.setOnClickListener(v -> {
            SearchRequest request = buildSearchRequest(ADVANCED_SEARCH_MODE);
            if (request != null)
            {
                searchClicked(request, false);
            }
        });
        btnSearchISBN.setOnClickListener(v -> {
            SearchRequest request = buildSearchRequest(ISBN_SEARCH_MODE);
            if (request != null)
            {
                searchClicked(request, false);
            }
        });

        return view;
    }

    private void init()
    {
        eTSearch = view.findViewById(R.id.eTSearch);
        lVBooks = view.findViewById(R.id.lVBooks);
        btnSearch = view.findViewById(R.id.btnSearch);

        layoutFilterHeader = view.findViewById(R.id.layoutFilterHeader);
        scrollFilters = view.findViewById(R.id.scrollFilters);
        iVArrow = view.findViewById(R.id.iVArrow);
        eTTitle = view.findViewById(R.id.eTTitle);
        eTAuthor = view.findViewById(R.id.eTAuthor);
        eTISBN = view.findViewById(R.id.eTISBN);
        layoutIncludeSubjects = view.findViewById(R.id.layoutIncludeSubjects);
        layoutExcludeSubjects = view.findViewById(R.id.layoutExcludeSubjects);
        layoutLanguages = view.findViewById(R.id.layoutLanguages);
        radioSubjAll = view.findViewById(R.id.radioSubjAll);
        radioLangAll = view.findViewById(R.id.radioLangAll);
        btnAdvancedSearch = view.findViewById(R.id.btnAdvancedSearch);
        btnSearchISBN = view.findViewById(R.id.btnSearchISBN);

        footerView = LayoutInflater.from(getContext())
                .inflate(R.layout.show_more_footer, lVBooks, false);

        tvShowMore = footerView.findViewById(R.id.tvShowMore);
        tvShowMore.setOnClickListener(v -> {
            SearchRequest request = buildSearchRequest(VIEW_MORE_SEARCH_MODE);
            if (request != null)
            {
                searchClicked(request, true);
            }
        });

        footerView.setVisibility(View.GONE);
        lVBooks.addFooterView(footerView);
        lVBooks.setOnItemClickListener(this);

        booksList = new ArrayList<>();
        adp = new CustomAdapterSearch(getContext(), booksList);
        lVBooks.setAdapter(adp);
    }

    public void searchClicked(SearchRequest request, boolean isViewMoreMode)
    {
        if (!isViewMoreMode)
        {
            footerView.setVisibility(View.GONE);
        }
        else
        {
            tvShowMore.setText("Loading...");
            tvShowMore.setEnabled(false);
        }

        BooksApiService.searchBooks(request, new ApiCallback<LiteBook>() {
            @Override
            public void onSearchResultsLoaded(List<LiteBook> books)
            {
                if (isAdded())
                {
                    if (!isViewMoreMode)
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

        prevRequest = request;
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


    private void toggleFilters()
    {
        if (isExpanded)
        {
            scrollFilters.setVisibility(View.GONE);
            iVArrow.setRotation(0);
        }
        else
        {
            scrollFilters.setVisibility(View.VISIBLE);
            iVArrow.setRotation(180);
        }
        isExpanded = !isExpanded;
    }

    private SearchRequest buildSearchRequest(int searchMode)
    {
        Map<String, String> q_inter = new HashMap<>();
        if (searchMode != VIEW_MORE_SEARCH_MODE)
        {
            startIndex = 0;
        }

        switch (searchMode)
        {
            case NORMAL_SEARCH_MODE:
                String q = eTSearch.getText().toString();
                if (q.length() == 0)
                {
                    eTSearch.setError("Field can't be empty");
                    return null;
                }
                return new SearchRequest(q, null, startIndex);

            case VIEW_MORE_SEARCH_MODE:
                SearchRequest searchRequest = prevRequest;
                searchRequest.setStartIndex(startIndex);
                return searchRequest;

            case ADVANCED_SEARCH_MODE:
                String title = eTTitle.getText().toString();
                String author = eTAuthor.getText().toString();
                List<String> selectedIncludeSubjects = getCheckedBoxes(layoutIncludeSubjects);
                List<String> selectedExcludeSubjects = getCheckedBoxes(layoutExcludeSubjects);
                List<String> selectedLanguages = getCheckedBoxes(layoutLanguages);

                if (title.length() == 0 && author.length() == 0 &&
                        selectedIncludeSubjects.isEmpty() &&
                        selectedExcludeSubjects.isEmpty() &&
                        selectedLanguages.isEmpty())
                {
                    Toast.makeText(getContext(),
                            "Enter at least one field (title, author, subject or language)",
                            Toast.LENGTH_SHORT).show();
                    return null;
                }

                if (title.length() != 0)
                {
                    q_inter.put("title", title);
                }
                if (author.length() != 0)
                {
                    q_inter.put("author_name", author);
                }
                if (!selectedIncludeSubjects.isEmpty())
                {
                    boolean isAll = radioSubjAll.isChecked();
                    String operator = isAll ? " AND " : " OR ";
                    String subjectsQuery = "(" + TextUtils.join(operator, selectedIncludeSubjects) + ")";

                    q_inter.put("subject", subjectsQuery);
                }
                if (!selectedExcludeSubjects.isEmpty())
                {
                    String operator = " OR ";
                    String subjectsQuery = "(" + TextUtils.join(operator, selectedExcludeSubjects) + ")";

                    q_inter.put("-subject", subjectsQuery);
                }
                if (!selectedLanguages.isEmpty())
                {
                    boolean isAll = radioLangAll.isChecked();
                    String operator = isAll ? " AND " : " OR ";
                    String languagesQuery = "(" + TextUtils.join(operator, selectedLanguages) + ")";

                    q_inter.put("language", languagesQuery);
                }

                return new SearchRequest(null, q_inter, startIndex);

            case ISBN_SEARCH_MODE:
                String isbn = eTISBN.getText().toString();
                if (isbn.length() == 0)
                {
                    eTISBN.setError("Field can't be empty");
                    return null;
                }

                q_inter.put("isbn", isbn);
                return new SearchRequest(null, q_inter, startIndex);

            default:
                return null;
        }
    }

    private List<String> getCheckedBoxes(ViewGroup parent)
    {
        List<String> selected = new ArrayList<>();

        for (int i = 0; i < parent.getChildCount(); i++)
        {
            View child = parent.getChildAt(i);

            if (child instanceof CheckBox)
            {
                CheckBox cb = (CheckBox)child;
                if (cb.isChecked())
                {
                    selected.add(cb.getTag().toString());
                }
            }
            else if (child instanceof ViewGroup)
            {
                selected.addAll(getCheckedBoxes((ViewGroup)child));
            }
        }

        return selected;
    }
}