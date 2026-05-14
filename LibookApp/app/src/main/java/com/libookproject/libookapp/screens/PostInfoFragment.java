package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.FBRef.Uid;
import static com.libookproject.libookapp.FBRef.refAuth;

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

public class PostInfoFragment extends Fragment
{
    private View view;
    private String postId;
    private ReviewsViewModel reviewsViewModel;

    private Post postInfo;
    private TextView tVHeadline;
    private TextView tVDate;
    private TextView tVUsername;
    private TextView tVContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            postId = getArguments().getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post_info, container, false);

        init();
        reviewsViewModel = new ViewModelProvider(requireActivity()).get(ReviewsViewModel.class);
        setupReviewFragment();
        showPostInfo();

        return view;
    }

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

    private void setupReviewFragment()
    {
        ReviewsFragment reviewsFragment = new ReviewsFragment();

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.reviewsFrameLayout, reviewsFragment)
                .commit();
    }

    private void showPostInfo()
    {
        CommunityApiService.getPostInfo(postId, Uid, new ApiCallback<Post>()
        {
            @Override
            public void onPostInfoLoaded(Post post)
            {
                reviewsViewModel.setFields(post.getId(), post.getReviews(),
                        post.getUser_review(), post.getRating_stats());

                postInfo = post;
                bindData();

                // notify ReviewsFragment
                ReviewsFragment fragment = (ReviewsFragment)
                        getChildFragmentManager().findFragmentById(R.id.reviewsFrameLayout);

                if (fragment != null)
                {
                    fragment.bindData();
                }
            }

            @Override
            public void onPostInfoError(String err)
            {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                System.out.println(err);
            }
        });
    }

    private void bindData()
    {
        tVHeadline.setText(postInfo.getHeadline());
        tVDate.setText(postInfo.getCreated_at());
        tVUsername.setText(postInfo.getUsername());
        tVContent.setText(postInfo.getContent());
    }
}