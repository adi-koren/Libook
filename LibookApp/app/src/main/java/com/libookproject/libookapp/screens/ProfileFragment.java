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

import com.libookproject.libookapp.dataObjects.LitePost;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.adapters.CustomAdapterCommunity;
import com.libookproject.libookapp.serverApi.ApiCallback;
import com.libookproject.libookapp.serverApi.CommunityApiService;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment displaying the current user's profile page.
 * shows the username, post count, and a list of all posts created by the user.
 * supports opening a post's detail page on tap, and deleting a post on long press.
 */
public class ProfileFragment extends Fragment implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private static final String KEY_POSTS_LIST = "postsList";
    private static final String KEY_POSTS_COUNT = "postsCount";
    private static final String KEY_NEEDS_REFRESH = "needsRefresh";
    private static final String KEY_LIST_POSITION = "listPosition";
    private static final String KEY_LIST_OFFSET = "listOffset";

    private View view;
    private TextView tVUsername;
    private TextView tVPostsCount;
    private ListView lVPosts;
    private Button btnLogout;

    private ArrayList<LitePost> postsList;
    private CustomAdapterCommunity adp;

    //starts true so that the first onResume will loads posts.
    private boolean needsRefresh = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        init();
        restoreState(savedInstanceState);

        return view;
    }

    /**
     * called each time the fragment becomes visible and run.
     * if needsRefresh is true, reloads the user's posts from the server.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (needsRefresh)
        {
            getPosts();
        }
    }

    //saves the current state of the fragment before it is destroyed.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //check if is hidden
        if (lVPosts == null)
        {
            return;
        }

        outState.putParcelableArrayList(KEY_POSTS_LIST, postsList);
        outState.putString(KEY_POSTS_COUNT, tVPostsCount.getText().toString());
        outState.putBoolean(KEY_NEEDS_REFRESH, needsRefresh);
        outState.putInt(KEY_LIST_POSITION, lVPosts.getFirstVisiblePosition());
        View firstChild = lVPosts.getChildAt(0);
        outState.putInt(KEY_LIST_OFFSET, firstChild == null ? 0 : firstChild.getTop());
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

    //restores the fragment's UI state after recreation.
    private void restoreState(Bundle savedInstanceState)
    {
        if (savedInstanceState == null)
        {
            return;
        }

        //restore posts list
        ArrayList<LitePost> saved =
                savedInstanceState.getParcelableArrayList(KEY_POSTS_LIST);
        if (saved != null)
        {
            postsList.addAll(saved);
            adp.notifyDataSetChanged();
        }

        //restore posts count
        String savedCount = savedInstanceState.getString(KEY_POSTS_COUNT);
        if (savedCount != null) {
            tVPostsCount.setText(savedCount);
        }

        //restore needsRefresh
        needsRefresh = savedInstanceState.getBoolean(KEY_NEEDS_REFRESH, true);

        //restore scroll position
        int pos = savedInstanceState.getInt(KEY_LIST_POSITION, 0);
        int offset = savedInstanceState.getInt(KEY_LIST_OFFSET, 0);
        lVPosts.post(() -> lVPosts.setSelectionFromTop(pos, offset));
    }

    /**
     * sets the needsRefresh flag. called externally by MainActivity when posts
     * have been created or deleted elsewhere in the app, signaling that the
     * posts list should be reloaded on the next onResume.
     */
    public void setNeedsRefresh(boolean state)
    {
        needsRefresh = state;
    }


    // fetches the current user's posts from the server and updates the UI.
    private void getPosts()
    {
        CommunityApiService.getUserPosts(Uid, new ApiCallback<List<LitePost>>() {
            @Override
            public void onGetUserPostsSucceeded(List<LitePost> posts) {
                if (isAdded())
                {
                    postsList.clear();
                    postsList.addAll(posts);
                    adp.notifyDataSetChanged();

                    tVPostsCount.setText("Posts: " + posts.size());

                    setNeedsRefresh(false);
                }
            }

            @Override
            public void onGetUserPostsFailed(String err)
            {
                if (isAdded())
                {
                    Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * sends a delete request to the server for the given post ID.
     * on success, reloads the posts list and shows a confirmation toast.
     */
    private void deletePostClicked(String postId)
    {
        CommunityApiService.deletePost(postId, new ApiCallback()
        {
            @Override
            public void onDeletePostSucceeded() {
                if (isAdded())
                {
                    getPosts();
                    Toast.makeText(getContext(),
                            "Your post has been deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeletePostFailed(String err) {
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
     * called when a post item in the ListView is long pressed.
     * shows a confirmation dialog before deleting the post.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Post")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) ->
                        deletePostClicked(view.getTag().toString()))
                .setNegativeButton("Cancel", null)
                .show();
        return true;
    }

    /**
     * signs the user out of Firebase Authentication, clears the data from FBRef,
     * and navigates back to AuthActivity, clearing the entire back stack so the user
     * cannot navigate back to the app without logging in again.
     */
    private void logout()
    {
        refAuth.signOut();

        Uid = null;
        username = null;

        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        requireActivity().finish();
    }
}