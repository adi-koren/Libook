package com.libookproject.libookapp;

import static com.libookproject.libookapp.BooksApiService.getBookInfo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

/**
 * A simple {@link //Fragment} subclass.
 * Use the {@link //BookInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookInfoFragment extends Fragment
{
    private View view;
    private LiteBook liteBook;
    private TextView tVTitle;
    private TextView tVAuthor;
    private TextView tVPub;
    private TextView tVDesc;
    private ImageView iVImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            //book_id = getArguments().getString("id");
            liteBook = getArguments().getParcelable("liteBook");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_book_info, container, false);

        init();
        showBookInfo();
        return view;
    }

    private void init()
    {
        tVTitle = view.findViewById(R.id.tVTitle);
        tVAuthor = view.findViewById(R.id.tVAuthor);
        tVPub = view.findViewById(R.id.tVPub);
        tVDesc = view.findViewById(R.id.tVDesc);
        iVImage = view.findViewById(R.id.iVImage);

        tVTitle.setText(liteBook.getTitle());
        tVAuthor.setText(liteBook.getAuthors().get(0));

        String imageUrl = liteBook.getImage();
        if (!imageUrl.equals("ERROR"))
        {
            imageUrl = imageUrl.replace("http://", "https://");

            Glide.with(view)
                    .load(imageUrl)
                    .placeholder(R.drawable.image_not_found)
                    .error(R.drawable.image_not_found)
                    .into(iVImage);
        }
        else
        {
            iVImage.setImageResource(R.drawable.image_not_found);
        }
    }

    private void showBookInfo()
    {
        getBookInfo(liteBook.getId(), new ApiCallback<Book>() {
            @Override
            public void onBookInfoLoaded(Book book)
            {
                tVPub.setText(book.getPublishedDate());
                tVDesc.setText(Html.fromHtml(book.getDescription(), Html.FROM_HTML_MODE_LEGACY));
            }

            @Override
            public void onBookInfoError(String err)
            {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                System.out.println(err);
            }
        });
    }
}