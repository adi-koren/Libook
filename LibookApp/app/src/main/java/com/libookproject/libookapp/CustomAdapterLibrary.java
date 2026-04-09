package com.libookproject.libookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomAdapterLibrary extends RecyclerView.Adapter<CustomAdapterLibrary.BookViewHolder> {

    private ArrayList<SavedBook> books;
    private Context context;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(SavedBook book);
    }

    public CustomAdapterLibrary(Context context, ArrayList<SavedBook> books, OnBookClickListener listener)
    {
        this.books = books;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_book_library_layout, parent, false);
        return new BookViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return books.size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageBook;

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