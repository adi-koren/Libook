package com.libookproject.libookapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.SavedBook;

import java.util.ArrayList;

/**
 * custom RecyclerView adapter for displaying the user's saved books in library.
 * each item displays only the book's cover image.
 * supports click events on book items from the OnBookClickListener interface.
 * uses ViewHolder for efficient view recycling.
 */
public class CustomAdapterLibrary extends RecyclerView.Adapter<CustomAdapterLibrary.BookViewHolder>
{
    private ArrayList<SavedBook> books;
    private Context context;
    private OnBookClickListener listener;

    /**
     * interface for handling click events on book items in the library.
     */
    public interface OnBookClickListener
    {
        /**
         * called when a book item is clicked.
         * implemented by the activity or fragment that uses this adapter.
         * @param book the SavedBook object that was clicked.
         */
        void onBookClick(SavedBook book);
    }

    /**
     * constructs a new CustomAdapterLibrary.
     * @param context  the context of the Activity using this adapter.
     * @param books    the list of SavedBook objects to display.
     * @param listener the click listener to notify when a book is clicked.
     */
    public CustomAdapterLibrary(Context context, ArrayList<SavedBook> books, OnBookClickListener listener)
    {
        this.books = books;
        this.context = context;
        this.listener = listener;
    }

    /**
     * inflates the custom book item layout and returns a new ViewHolder wrapping it.
     * @param parent   the parent ViewGroup the new view will be attached to.
     * @param viewType the view type of the new view (not used here).
     * @return a new BookViewHolder holding the inflated item view.
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_book_library_layout, parent, false);
        return new BookViewHolder(view);
    }

    /**
     * binds the saved book data at the given position to the provided ViewHolder.
     * the book's ID is stored as the ImageView's tag.
     * @param holder   the ViewHolder to bind data to.
     * @param position the position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.imageBook.setTag(books.get(position).getId());
        String imageUrl = books.get(position).getImage();

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.image_not_found)
                .error(R.drawable.image_not_found)
                .into(holder.imageBook);
    }

    /**
     * returns the total number of saved books in the list.
     * @return the size of the books list.
     */
    @Override
    public int getItemCount()
    {
        return books.size();
    }

    /**
     * ViewHolder class that holds a reference to the book cover ImageView.
     * created once for each visible item and reused as the user scrolls, avoiding repeated
     * findViewById calls for better performance.
     */
    class BookViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageBook;

        /**
         * constructs the ViewHolder, binds the ImageView, and sets the click listener.
         * @param itemView the inflated view for this book item.
         */
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBook = itemView.findViewById(R.id.imageBook);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBookClick(books.get(pos));
                }
            });
        }
    }
}