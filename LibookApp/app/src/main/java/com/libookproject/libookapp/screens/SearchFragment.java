package com.libookproject.libookapp.screens;

import com.libookproject.libookapp.adapters.CustomAdapterSearch;
import com.libookproject.libookapp.dataObjects.LiteBook;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.requestObjects.SearchRequest;
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

/**
 * fragment responsible for searching books using the server API.
 * supports four search modes: normal search, advanced filtered search, ISBN search,
 * and "show more".
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private static final String KEY_BOOKS_LIST = "booksList";
    private static final String KEY_START_INDEX = "startIndex";
    private static final String KEY_PREV_REQUEST = "prevRequest";
    private static final String KEY_IS_EXPANDED = "isExpanded";
    private static final String KEY_FOOTER_VISIBLE = "footerVisible";
    private static final String KEY_LIST_POSITION = "listPosition";
    private static final String KEY_LIST_OFFSET = "listOffset";

    private View view;
    private EditText eTSearch;
    private ListView lVBooks;
    private ImageButton btnSearch;

    private LinearLayout layoutFilterHeader;
    private ScrollView scrollFilters;
    private ImageView iVArrow;
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

    private ArrayList<LiteBook> booksList;
    private CustomAdapterSearch adp;
    private boolean isExpanded = false;
    private int startIndex = 0;
    private SearchRequest prevRequest = null;

    private final int NORMAL_SEARCH_MODE = 0;
    private final int VIEW_MORE_SEARCH_MODE = 1;
    private final int ADVANCED_SEARCH_MODE = 2;
    private final int ISBN_SEARCH_MODE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        init();
        restoreState(savedInstanceState);
        attachListeners();

        return view;
    }

    //saves the current state of the fragment before it is destroyed.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (footerView == null)
        {
            return;
        }

        //saves the results list
        outState.putParcelableArrayList(KEY_BOOKS_LIST, booksList);

        // saves start index state
        outState.putInt(KEY_START_INDEX, startIndex);

        //saves the last request so "show more" still works
        outState.putParcelable(KEY_PREV_REQUEST, prevRequest);

        //saves if filter is opened/closed
        outState.putBoolean(KEY_IS_EXPANDED, isExpanded);

        //saves if the "show more" footer was visible
        outState.putBoolean(KEY_FOOTER_VISIBLE, footerView.getVisibility() == View.VISIBLE);

        //saves ListView scroll position so the user returns to the same spot
        outState.putInt(KEY_LIST_POSITION, lVBooks.getFirstVisiblePosition());
        View firstChild = lVBooks.getChildAt(0);
        outState.putInt(KEY_LIST_OFFSET, firstChild == null ? 0 : firstChild.getTop());
    }

    //initialize all the UI references
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
        footerView.setVisibility(View.GONE);
        lVBooks.addFooterView(footerView);
        lVBooks.setOnItemClickListener(this);

        booksList = new ArrayList<>();
        adp = new CustomAdapterSearch(getContext(), booksList);
        lVBooks.setAdapter(adp);
    }

    //restores the fragment's UI state after recreation.
    private void restoreState(Bundle savedInstanceState)
    {
        if (savedInstanceState == null)
        {
            return;
        }

        //restore results list
        ArrayList<LiteBook> saved =
                savedInstanceState.getParcelableArrayList(KEY_BOOKS_LIST);
        if (saved != null)
        {
            booksList.addAll(saved);
            adp.notifyDataSetChanged();
        }

        startIndex = savedInstanceState.getInt(KEY_START_INDEX, 0);
        prevRequest = savedInstanceState.getParcelable(KEY_PREV_REQUEST);

        //restore footer visibility
        boolean footerVisible = savedInstanceState.getBoolean(KEY_FOOTER_VISIBLE, false);
        footerView.setVisibility(footerVisible ? View.VISIBLE : View.GONE);

        //restore filter state
        isExpanded = savedInstanceState.getBoolean(KEY_IS_EXPANDED, false);
        if (isExpanded)
        {
            lVBooks.setVisibility(View.GONE);
            scrollFilters.setVisibility(View.VISIBLE);
            iVArrow.setRotation(180);
        }
        else
        {
            lVBooks.setVisibility(View.VISIBLE);
            scrollFilters.setVisibility(View.GONE);
            iVArrow.setRotation(0);
        }

        int pos = savedInstanceState.getInt(KEY_LIST_POSITION, 0);
        int offset = savedInstanceState.getInt(KEY_LIST_OFFSET, 0);
        lVBooks.post(() -> lVBooks.setSelectionFromTop(pos, offset));
    }

    //attaches click listeners to all interactive UI elements.
    private void attachListeners()
    {
        //normal search listener
        btnSearch.setOnClickListener(v ->
        {
            SearchRequest request = buildSearchRequest(NORMAL_SEARCH_MODE);
            if (request != null)
            {
                searchClicked(request, false);
            }
        });

        //toggle filter section listener
        layoutFilterHeader.setOnClickListener(v -> toggleFilters());

        btnAdvancedSearch.setOnClickListener(v -> {
            toggleFilters();
            SearchRequest request = buildSearchRequest(ADVANCED_SEARCH_MODE);
            if (request != null)
            {
                searchClicked(request, false);
            }
        });

        //isbn search listener
        btnSearchISBN.setOnClickListener(v -> {
            toggleFilters();
            SearchRequest request = buildSearchRequest(ISBN_SEARCH_MODE);
            if (request != null)
            {
                searchClicked(request, false);
            }
        });

        //show more search listener
        tvShowMore.setOnClickListener(v -> {
            SearchRequest request = buildSearchRequest(VIEW_MORE_SEARCH_MODE);
            if (request != null)
            {
                searchClicked(request, true);
            }
        });
    }


    //sends a search request to the server API and handles the response.
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
            public void onSearchResultsLoaded(List<LiteBook> books) {
                if (isAdded()) {
                    if (!isViewMoreMode) {
                        booksList.clear();
                    }
                    booksList.addAll(books);
                    adp.notifyDataSetChanged();

                    //check if there might be more books
                    if (books.size() == 10) {
                        footerView.setVisibility(View.VISIBLE);
                        tvShowMore.setEnabled(true);
                    }
                    else {
                        footerView.setVisibility(View.GONE);
                    }
                    tvShowMore.setText("Show more");
                    startIndex += books.size();
                }
            }

            @Override
            public void onSearchResultsError(String err) {
                if (isAdded()) {
                    Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                }
            }
        });

        prevRequest = request;
    }

    /**
     * called when a book item in the ListView is clicked.
     * creates a BookInfoFragment, passes the selected book's ID as an argument,
     * and pushes it onto the back stack to display the book's detail page.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BookInfoFragment bookInfoFragment = new BookInfoFragment();

        Bundle bundle = new Bundle();
        bundle.putString("id", view.getTag().toString());
        bookInfoFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout, bookInfoFragment)
                .addToBackStack(null)
                .commit();
    }

    //toggles the advanced filters panel open or closed.
    private void toggleFilters() {
        if (isExpanded) {
            scrollFilters.setVisibility(View.GONE);
            iVArrow.setRotation(0);
            lVBooks.setVisibility(View.VISIBLE);
        } else {
            lVBooks.setVisibility(View.GONE);
            scrollFilters.setVisibility(View.VISIBLE);
            iVArrow.setRotation(180);
        }
        isExpanded = !isExpanded;
    }

    /**
     * builds and returns a SearchRequest based on the given search mode.
     * in NORMAL mode: uses the main search field as a plain query string.
     * in VIEW_MORE mode: reuses the previous request with an updated pagination index.
     * in ADVANCED mode: collects title, author, included/excluded subjects and languages
     *   from the filter fields and builds a structured query map. requires at least one field.
     * in ISBN mode: uses the ISBN field to build a query map with a single "isbn" key.
     * returns null and shows an error if required fields are empty.
     */
    private SearchRequest buildSearchRequest(int searchMode) {
        Map<String, String> q_inter = new HashMap<>();
        if (searchMode != VIEW_MORE_SEARCH_MODE) {
            startIndex = 0;
        }

        switch (searchMode) {
            case NORMAL_SEARCH_MODE:
                String q = eTSearch.getText().toString();
                if (q.length() == 0) {
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
                        selectedLanguages.isEmpty()) {
                    Toast.makeText(getContext(),
                            "Enter at least one field (title, author, subject or language)",
                            Toast.LENGTH_SHORT).show();
                    return null;
                }

                if (title.length()  != 0)
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
                    q_inter.put("subject",
                            "(" + TextUtils.join(operator, selectedIncludeSubjects) + ")");
                }
                if (!selectedExcludeSubjects.isEmpty())
                {
                    q_inter.put("-subject",
                            "(" + TextUtils.join(" OR ", selectedExcludeSubjects) + ")");
                }
                if (!selectedLanguages.isEmpty())
                {
                    boolean isAll = radioLangAll.isChecked();
                    String operator = isAll ? " AND " : " OR ";
                    q_inter.put("language",
                            "(" + TextUtils.join(operator, selectedLanguages) + ")");
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

    /**
     * recursively collects the tags of all checked CheckBoxes within a ViewGroup.
     * used to retrieve the selected subjects and languages from the filter panel.
     */
    private List<String> getCheckedBoxes(ViewGroup parent) {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox cb = (CheckBox) child;
                if (cb.isChecked()) selected.add(cb.getTag().toString());
            } else if (child instanceof ViewGroup) {
                selected.addAll(getCheckedBoxes((ViewGroup) child));
            }
        }
        return selected;
    }
}