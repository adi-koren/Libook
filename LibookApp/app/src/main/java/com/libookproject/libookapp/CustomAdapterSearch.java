package com.libookproject.libookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomAdapterSearch extends BaseAdapter
{
    private Context context;
    private ArrayList<LiteBook> booksList;
    private LayoutInflater inflater;

    public CustomAdapterSearch(Context context, ArrayList<LiteBook> booksList)
    {
        this.context = context;
        this.booksList = booksList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return booksList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return booksList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent)
    {
        view = inflater.inflate(R.layout.custom_lv_books_search_layout, parent, false);
        TextView clvTitle = view.findViewById(R.id.clvTitle);
        TextView clvAuthor = view.findViewById(R.id.clvAuthor);
        ImageView clvBookCover = view.findViewById(R.id.clvBookCover);

        clvTitle.setText(booksList.get(i).getTitle());
        clvAuthor.setText(booksList.get(i).getAuthors().get(0));
        view.setTag(booksList.get(i).getId());

        String imageUrl = booksList.get(i).getImage();
        if (!imageUrl.equals("ERROR"))
        {
            imageUrl = imageUrl.replace("http://", "https://");

            Glide.with(view)
                    .load(imageUrl)
                    .placeholder(R.drawable.image_not_found)
                    .error(R.drawable.image_not_found)
                    .into(clvBookCover);
        }
        else
        {
            clvBookCover.setImageResource(R.drawable.image_not_found);
        }

        return view;
    }
}
