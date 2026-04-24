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

public class CustomAdapterCommunity extends BaseAdapter
{
    private Context context;
    private ArrayList<LitePost> postsList;
    private LayoutInflater inflater;

    public CustomAdapterCommunity(Context context, ArrayList<LitePost> postsList)
    {
        this.context = context;
        this.postsList = postsList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return postsList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return postsList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

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
