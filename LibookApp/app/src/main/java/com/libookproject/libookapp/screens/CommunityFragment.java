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

import com.libookproject.libookapp.LitePost;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.adapters.CustomAdapterCommunity;
import com.libookproject.libookapp.serverApi.ApiCallback;
import com.libookproject.libookapp.serverApi.CommunityApiService;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private View view;
    private EditText eTSearch;
    private ListView lVPosts;
    private ImageButton btnSearch;
    private Button btnAddPost;

    private ArrayList<LitePost> postsList;
    private CustomAdapterCommunity adp;

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
        view = inflater.inflate(R.layout.fragment_community, container, false);

        init();
        btnSearch.setOnClickListener(v -> {searchClicked(SEARCH_CLICKED_MODE);});
        btnAddPost.setOnClickListener(v -> {addPostClicked();});

        searchClicked(SEARCH_CLICKED_MODE);
        return view;
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
        tvShowMore.setOnClickListener(v -> {searchClicked(!SEARCH_CLICKED_MODE);});

        footerView.setVisibility(View.GONE);
        lVPosts.addFooterView(footerView);
        lVPosts.setOnItemClickListener(this);

        postsList = new ArrayList<>();
        adp = new CustomAdapterCommunity(getContext(), postsList);
        lVPosts.setAdapter(adp);
    }

    public void searchClicked(boolean isSearchClickedMode) {

        String q = eTSearch.getText().toString();

//        if (q.length() == 0)
//        {
//            eTSearch.setError("Field can't be empty");
//            return;
//        }

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
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                System.out.println(err);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        PostInfoFragment postInfoFragment = new PostInfoFragment();

        Bundle bundle = new Bundle();
        bundle.putString("id", view.getTag().toString());
        postInfoFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, postInfoFragment)
                .addToBackStack(null)
                .commit();
    }

    public void addPostClicked()
    {
        AddPostFragment addPostFragment = new AddPostFragment();

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, addPostFragment)
                .addToBackStack(null)
                .commit();
    }
}