package com.libookproject.libookapp.screens;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.libookproject.libookapp.dataObjects.LitePost;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.adapters.CustomAdapterCommunity;
import com.libookproject.libookapp.serverApi.ApiCallback;
import com.libookproject.libookapp.serverApi.CommunityApiService;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment responsible for displaying and searching community posts.
 * supports keyword search, "show more", and navigation to add post screen.
 */
public class CommunityFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private static final String KEY_POSTS_LIST = "postsList";
    private static final String KEY_START_INDEX = "startIndex";
    private static final String KEY_FOOTER_VISIBLE = "footerVisible";
    private static final String KEY_LIST_POSITION = "listPosition";
    private static final String KEY_LIST_OFFSET = "listOffset";

    private View view;
    private EditText eTSearch;
    private ListView lVPosts;
    private ImageButton btnSearch;
    private Button btnAddPost;
    private View footerView;
    private TextView tvShowMore;

    private ArrayList<LitePost> postsList;
    private CustomAdapterCommunity adp;
    private int startIndex = 0;

    private final boolean SEARCH_CLICKED_MODE = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_community, container, false);

        init();
        attachListeners();

        if (savedInstanceState == null)
        {
            //load newest posts from the server
            searchClicked(SEARCH_CLICKED_MODE);
        }
        else
        {
            //restore the data
            restoreState(savedInstanceState);
        }

        return view;
    }

    //saves the current state of the fragment before it is destroyed.
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //check if its hidden
        if (footerView == null)
        {
            return;
        }

        outState.putParcelableArrayList(KEY_POSTS_LIST, postsList);
        outState.putInt(KEY_START_INDEX, startIndex);
        outState.putBoolean(KEY_FOOTER_VISIBLE,
                footerView.getVisibility() == View.VISIBLE);
        outState.putInt(KEY_LIST_POSITION, lVPosts.getFirstVisiblePosition());
        View firstChild = lVPosts.getChildAt(0);
        outState.putInt(KEY_LIST_OFFSET, firstChild == null ? 0 : firstChild.getTop());
    }

    private void init()
    {
        eTSearch = view.findViewById(R.id.eTSearch);
        lVPosts = view.findViewById(R.id.lVPosts);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnAddPost = view.findViewById(R.id.btnAddPost);

        footerView = LayoutInflater.from(getContext())
                .inflate(R.layout.show_more_footer, lVPosts, false);
        tvShowMore = footerView.findViewById(R.id.tvShowMore);
        footerView.setVisibility(View.GONE);
        lVPosts.addFooterView(footerView);
        lVPosts.setOnItemClickListener(this);

        postsList = new ArrayList<>();
        adp = new CustomAdapterCommunity(getContext(), postsList);
        lVPosts.setAdapter(adp);
    }

    private void attachListeners()
    {
        btnSearch.setOnClickListener(v -> searchClicked(SEARCH_CLICKED_MODE));
        tvShowMore.setOnClickListener(v -> searchClicked(!SEARCH_CLICKED_MODE));
        btnAddPost.setOnClickListener(v -> addPostClicked());
    }

    //restores the fragment's UI state after recreation.
    private void restoreState(Bundle savedInstanceState)
    {
        //restore posts list
        ArrayList<LitePost> saved =
                savedInstanceState.getParcelableArrayList(KEY_POSTS_LIST);
        if (saved != null)
        {
            postsList.addAll(saved);
            adp.notifyDataSetChanged();
        }

        //restore start index
        startIndex = savedInstanceState.getInt(KEY_START_INDEX, 0);

        // Restore footer visibility
        boolean footerVisible = savedInstanceState.getBoolean(KEY_FOOTER_VISIBLE, false);
        footerView.setVisibility(footerVisible ? View.VISIBLE : View.GONE);

        //restore scroll position
        int pos = savedInstanceState.getInt(KEY_LIST_POSITION, 0);
        int offset = savedInstanceState.getInt(KEY_LIST_OFFSET, 0);
        lVPosts.post(() -> lVPosts.setSelectionFromTop(pos, offset));
    }


    // sends a search request to the server and handles the response.
    public void searchClicked(boolean isSearchClickedMode) {
        String q = eTSearch.getText().toString();

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

        CommunityApiService.searchPosts(q, startIndex, new ApiCallback<List<LitePost>>() {
            @Override
            public void onSearchPostsLoaded(List<LitePost> posts) {
                if (isAdded())
                {
                    if (isSearchClickedMode)
                    {
                        postsList.clear();
                    }
                    postsList.addAll(posts);
                    adp.notifyDataSetChanged();

                    if (posts.size() == 10)
                    {
                        footerView.setVisibility(View.VISIBLE);
                        tvShowMore.setEnabled(true);
                    }
                    else
                    {
                        footerView.setVisibility(View.GONE);
                    }
                    tvShowMore.setText("Show more");
                    startIndex += posts.size();
                }
            }

            @Override
            public void onSearchPostsError(String err) {
                if (isAdded()) {
                    Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * called when a post item in the ListView is clicked.
     * creates a PostInfoFragment, passes the selected post's ID as an argument,
     * and pushes it onto the back stack to display the post's detail page.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        PostInfoFragment postInfoFragment = new PostInfoFragment();

        Bundle bundle = new Bundle();
        bundle.putString("id", view.getTag().toString());
        postInfoFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout, postInfoFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * navigates to AddPostFragment by pushing it onto the back stack,
     * allowing the user to create a new community post.
     */
    public void addPostClicked()
    {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout, new AddPostFragment())
                .addToBackStack(null)
                .commit();
    }
}