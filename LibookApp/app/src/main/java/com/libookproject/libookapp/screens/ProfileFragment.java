package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.FBRef.Uid;
import static com.libookproject.libookapp.FBRef.refAuth;
import static com.libookproject.libookapp.FBRef.username;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

public class ProfileFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    private View view;
    private boolean needsRefresh = true;

    private TextView tVUsername;
    private TextView tVPostsCount;
    private ListView lVPosts;
    private Button btnLogout;

    private ArrayList<LitePost> postsList;
    private CustomAdapterCommunity adp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        init();
        //getPosts();
        //add listener to logout button
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (needsRefresh)
        {
            getPosts();
        }
    }

    private void init()
    {
        tVUsername = view.findViewById(R.id.tVUsername);
        tVUsername.setText(username);

        tVPostsCount = view.findViewById(R.id.tVPostsCount);
        lVPosts = view.findViewById(R.id.lVPosts);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());

        lVPosts.setOnItemClickListener(this);
        lVPosts.setOnItemLongClickListener(this);

        postsList = new ArrayList<>();
        adp = new CustomAdapterCommunity(getContext(), postsList);
        lVPosts.setAdapter(adp);
    }

    public void setNeedsRefresh(boolean state)
    {
        needsRefresh = state;
    }

    private void getPosts() {
        CommunityApiService.getUserPosts(Uid, new ApiCallback<List<LitePost>>() {
            @Override
            public void onGetUserPostsSucceeded(List<LitePost> posts) {
                if (isAdded())
                {
                    postsList.clear();

                    if (!posts.isEmpty())
                    {
                        postsList.addAll(posts);
                        adp.notifyDataSetChanged();
                    }
                    else
                    {
                        adp.notifyDataSetChanged();
                        //put here the "post your first..."
                    }
                    tVPostsCount.setText("Posts: " + Integer.toString(posts.size()));
                    setNeedsRefresh(false);
                }
            }

            @Override
            public void onGetUserPostsFailed(String err) {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                System.out.println(err);
            }
        });
    }

    private void deletePostClicked(String postId)
    {
        CommunityApiService.deletePost(postId, new ApiCallback() {
            @Override
            public void onDeletePostSucceeded() {
                getPosts();
                Toast.makeText(getContext(), "Your post has been deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeletePostFailed(String err) {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                System.out.println(err);
            }
        });
    }

    private void logout()
    {
        refAuth.signOut();

        Uid = null;
        username = null;

        Intent intent = new Intent(getActivity(), AuthActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        requireActivity().finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Post")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deletePostClicked(view.getTag().toString()))
                .setNegativeButton("Cancel", null)
                .show();
        return true;
    }
}