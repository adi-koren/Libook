package com.libookproject.libookapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.libookproject.libookapp.LitePost;
import com.libookproject.libookapp.R;

import java.util.ArrayList;

/**
 * custom adapter for displaying a list of LitePost objects in a community ListView.
 * each list item shows the post's headline, author username, and creation date.
 */
public class CustomAdapterCommunity extends BaseAdapter
{
    private Context context;
    private ArrayList<LitePost> postsList;
    private LayoutInflater inflater;

    /**
     * constructs a new CustomAdapterCommunity.
     * @param context   the context of the activity using this adapter.
     * @param postsList the list of LitePost objects to display.
     */
    public CustomAdapterCommunity(Context context, ArrayList<LitePost> postsList)
    {
        this.context = context;
        this.postsList = postsList;
        inflater = LayoutInflater.from(context);
    }

    /**
     * returns the total number of posts in the list.
     * @return the size of the posts list.
     */
    @Override
    public int getCount()
    {
        return postsList.size();
    }

    /**
     * returns the LitePost object at the given position.
     * @param position the index of the item.
     * @return the LitePost at the specified position.
     */
    @Override
    public Object getItem(int position)
    {
        return postsList.get(position);
    }

    /**
     * returns the ID of the item at the given position.
     * uses the position itself as the ID.
     * @param position the index of the item.
     * @return the position as the item ID.
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * creates or recycles a list item view and binds the post's data to it.
     * the post's ID is stored as the view's tag.
     * @param i      the position of the item in the list.
     * @param view   a recycled view to reuse, or null if none is available.
     * @param parent the parent ViewGroup the view will be attached to.
     * @return the bound view for this list item.
     */
    @Override
    public View getView(int i, View view, ViewGroup parent)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.custom_lv_posts_search_layout, parent, false);
        }
        TextView clvHeadline = view.findViewById(R.id.clvHeadline);
        TextView clvUsername = view.findViewById(R.id.clvUsername);
        TextView clvDate = view.findViewById(R.id.clvDate);

        clvHeadline.setText(postsList.get(i).getHeadline());
        clvUsername.setText(postsList.get(i).getUsername());
        clvDate.setText(postsList.get(i).getCreated_at());
        view.setTag(postsList.get(i).getId());

        return view;
    }
}
