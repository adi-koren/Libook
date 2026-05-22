package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.FBRef.Uid;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.libookproject.libookapp.Post;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.ReviewsViewModel;
import com.libookproject.libookapp.serverApi.ApiCallback;
import com.libookproject.libookapp.serverApi.CommunityApiService;

/**
 * fragment displaying the full details of a community post.
 * shows the post's headline, author username, creation date, and content.
 * embeds a ReviewsFragment as a child fragment for displaying and submitting comments.
 * fetches post data from the server on first load, or restores it from saved state on recreation.
 */
public class PostInfoFragment extends Fragment
{
    private static final String KEY_POST_INFO = "postInfo";

    private View view;
    private TextView tVHeadline;
    private TextView tVDate;
    private TextView tVUsername;
    private TextView tVContent;

    private String postId;
    private Post postInfo;
    private ReviewsViewModel reviewsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_post_info, container, false);

        init();

        reviewsViewModel = new ViewModelProvider(requireActivity())
                .get(ReviewsViewModel.class);

        setupReviewFragment();

        if (savedInstanceState != null)
        {
            restoreState(savedInstanceState);
        }
        else
        {
            showPostInfo();
        }

        return view;
    }

    //saves the current post data before the fragment is destroyed.
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //check if is hidden
        if (view == null)
        {
            return;
        }

        outState.putParcelable(KEY_POST_INFO, postInfo);
    }

    /**
     * called when the fragment is destroyed.
     * clears the ReviewsViewModel to release its data before another post is opened later.
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        reviewsViewModel.clear();
    }

    private void init()
    {
        tVHeadline = view.findViewById(R.id.tVHeadline);
        tVDate = view.findViewById(R.id.tVDate);
        tVUsername = view.findViewById(R.id.tVUsername);
        tVContent = view.findViewById(R.id.tVContent);
    }

    /**
     * sets up the ReviewsFragment as a child fragment inside the reviews container.
     * only adds it if it doesn't already exist, to avoid duplicate fragments on recreation.
     */
    private void setupReviewFragment() {
        //check if the reviews fragment already exists
        Fragment existing = getChildFragmentManager()
                .findFragmentById(R.id.reviewsFrameLayout);

        if (existing == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.reviewsFrameLayout, new ReviewsFragment())
                    .commit();
        }
    }

    //restores the fragment's UI state after recreation
    private void restoreState(Bundle savedInstanceState) {
        //restore post info
        postInfo = savedInstanceState.getParcelable(KEY_POST_INFO);
        if (postInfo != null)
        {
            bindData();

            //notifying reviews fragment the reviews changed
            reviewsViewModel.setFields(postInfo.getId(), postInfo.getReviews(),
                    postInfo.getUser_review(), postInfo.getRating_stats());

            ReviewsFragment fragment = (ReviewsFragment)
                    getChildFragmentManager().findFragmentById(R.id.reviewsFrameLayout);
            if (fragment != null)
            {
                fragment.bindData();
            }
        }
    }

    /**
     * fetches the full post data from the server using the post ID.
     * on success, stores the post in postInfo, binds its data to the UI,
     * and notifies the ReviewsViewModel and ReviewsFragment with the loaded review data.
     */
    private void showPostInfo()
    {
        CommunityApiService.getPostInfo(postId, Uid, new ApiCallback<Post>()
        {
            @Override
            public void onPostInfoLoaded(Post post) {
                if (isAdded())
                {
                    reviewsViewModel.setFields(post.getId(), post.getReviews(),
                            post.getUser_review(), post.getRating_stats());

                    postInfo = post;
                    bindData();

                    ReviewsFragment fragment = (ReviewsFragment)
                            getChildFragmentManager().findFragmentById(R.id.reviewsFrameLayout);
                    if (fragment != null) {
                        fragment.bindData();
                    }
                }
            }

            @Override
            public void onPostInfoError(String err) {
                if (isAdded()) {
                    Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //binds the loaded post data to the UI views.
    private void bindData()
    {
        tVHeadline.setText(postInfo.getHeadline());
        tVDate.setText(postInfo.getCreated_at());
        tVUsername.setText(postInfo.getUsername());
        tVContent.setText(postInfo.getContent());
    }
}