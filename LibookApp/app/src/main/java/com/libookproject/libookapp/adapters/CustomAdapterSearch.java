package com.libookproject.libookapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.libookproject.libookapp.dataObjects.LiteBook;
import com.libookproject.libookapp.R;

import java.util.ArrayList;

/**
 * custom adapter for displaying a list of LiteBook objects in a search results ListView.
 * each list item shows the book's cover image, title, and first author.
 */
public class CustomAdapterSearch extends BaseAdapter
{
    private Context context;
    private ArrayList<LiteBook> booksList;
    private LayoutInflater inflater;

    /**
     * constructs a new CustomAdapterSearch.
     * @param context   the context of the activity using this adapter.
     * @param booksList the list of LiteBook objects to display.
     */
    public CustomAdapterSearch(Context context, ArrayList<LiteBook> booksList)
    {
        this.context = context;
        this.booksList = booksList;
        inflater = LayoutInflater.from(context);
    }

    /**
     * returns the total number of items in the list.
     * @return the size of the books list.
     */
    @Override
    public int getCount()
    {
        return booksList.size();
    }

    /**
     * returns the LiteBook object at the given position.
     * @param position the index of the item.
     * @return the LiteBook at the specified position.
     */
    @Override
    public Object getItem(int position)
    {
        return booksList.get(position);
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
     * creates or recycles a list item view and binds the book's data to it.
     * the book's ID is stored as the view's tag.
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
            view = inflater.inflate(R.layout.custom_lv_books_search_layout, parent, false);
        }
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
