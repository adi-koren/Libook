package com.libookproject.libookapp.screens;

import static android.text.TextUtils.replace;
import static com.libookproject.libookapp.FBRef.refAuth;
import static com.libookproject.libookapp.FBRef.refUsers;
import static com.libookproject.libookapp.serverApi.BooksApiService.getBookInfo;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.libookproject.libookapp.Book;
import com.libookproject.libookapp.GeminiCallback;
import com.libookproject.libookapp.GeminiManager;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.serverApi.ApiCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link //Fragment} subclass.
 * Use the {@link //BookInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookInfoFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    private View view;
    private String bookId;
    private TextView tVTitle;
    private TextView tVAuthor;
    private TextView tVSubj;
    private TextView tVDesc;
    private ImageView iVImage;
    private TextView tVGeminiReco;
    private Button btnGenerate;
    private Spinner sSave;
    private DatabaseReference refCurrUserShelves;
    private Book bookInfo;
    private String prevSelectedShelf;
    private String newSelectedShelf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            bookId = getArguments().getString("id");
            //liteBook = getArguments().getParcelable("liteBook");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_book_info, container, false);

        init();
        showBookInfo();

        refCurrUserShelves = refUsers.child(refAuth.getUid()).child("Shelves");
        enableSaveOption();
        return view;
    }

    private void init()
    {
        tVTitle = view.findViewById(R.id.tVTitle);
        tVAuthor = view.findViewById(R.id.tVAuthor);
        tVSubj = view.findViewById(R.id.tVSubj);
        tVDesc = view.findViewById(R.id.tVDesc);
        iVImage = view.findViewById(R.id.iVImage);
        tVGeminiReco = view.findViewById(R.id.tVGeminiReco);
        btnGenerate = view.findViewById(R.id.btnGenerate);
        sSave = view.findViewById(R.id.sSave);

        sSave.setOnItemSelectedListener(this);
        btnGenerate.setOnClickListener(v -> generateGeminiReco());
    }

    private void showBookInfo()
    {
        getBookInfo(bookId, new ApiCallback<Book>() {
            @Override
            public void onBookInfoLoaded(Book book)
            {
                bookInfo = book;
                tVTitle.setText(book.getTitle());
                tVAuthor.setText(book.getAuthors().get(0));
                tVSubj.setText(book.getSubjects());
                tVDesc.setText(Html.fromHtml(book.getDescription(), Html.FROM_HTML_MODE_LEGACY));

                String imageUrl = book.getImage();
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

            @Override
            public void onBookInfoError(String err)
            {
                Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                System.out.println(err);
            }
        });
    }

    private void enableSaveOption()
    {
        ArrayList<String> shelvesNames = new ArrayList<>();
        shelvesNames.add("save in");

        refCurrUserShelves.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren())
                {
                    shelvesNames.add(data.getKey());
                }

                ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(),
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, shelvesNames);

                sSave.setAdapter(adp);
                showSavedMode();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String shelfName = parent.getItemAtPosition(position).toString();

        if (!shelfName.equals("save in"))
        {
            newSelectedShelf = shelfName;
        }
        else
        {
            newSelectedShelf = null;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


    private void showSavedMode()
    {
        DatabaseReference refSavedIndex = refUsers.child(refAuth.getUid()).child("SavedBooksIndex").child(bookId);

        refSavedIndex.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    for (DataSnapshot shelfSnap : snapshot.getChildren())
                    {
                        String shelfName = shelfSnap.getKey();
                        prevSelectedShelf = shelfName;

                        // Set spinner selection
                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) sSave.getAdapter();
                        int position = adapter.getPosition(shelfName);
                        if (position >= 0)
                        {
                            sSave.setSelection(position);
                        }
                    }
                }
                else
                {
                    prevSelectedShelf = null;
                }

                newSelectedShelf = prevSelectedShelf;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addBookToShelf(String shelfName)
    {
        Map<String, Object> updates = new HashMap<>();
        String userId = refAuth.getUid();

        // Shelves branch
        updates.put(
                userId + "/Shelves/" + shelfName + "/" + bookId,
                bookInfo.getImage()
        );

        // SavedBooksIndex branch
        updates.put(
                userId + "/SavedBooksIndex/" + bookId + "/" + shelfName,
                true
        );

        refUsers.updateChildren(updates).addOnFailureListener(e -> {
            if (e instanceof com.google.firebase.FirebaseNetworkException)
            {
                Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeBookFromShelf(String shelfName)
    {

        Map<String, Object> updates = new HashMap<>();
        String userId = refAuth.getUid();

        // Remove from Shelves
        updates.put(
                userId + "/Shelves/" + shelfName + "/" + bookId,
                null
        );

        // Remove only this shelf from SavedBooksIndex
        updates.put(
                userId + "/SavedBooksIndex/" + bookId + "/" + shelfName,
                null
        );

        refUsers.updateChildren(updates).addOnFailureListener(e -> {
            if (e instanceof com.google.firebase.FirebaseNetworkException)
            {
                Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();

        String userId = refAuth.getUid();

        if (prevSelectedShelf != null && !prevSelectedShelf.equals(newSelectedShelf)) {
            // remove from previous shelf
            removeBookFromShelf(prevSelectedShelf);
        }

        if (newSelectedShelf != null && !newSelectedShelf.equals(prevSelectedShelf)) {
            // add to new shelf
            addBookToShelf(newSelectedShelf);
        }
    }

    public void generateGeminiReco()
    {
        String promptTemplate = getString(R.string.gemini_prompt);

        String prompt = promptTemplate
                .replace("{/title/}", bookInfo.getTitle())
                .replace("{/author/}", bookInfo.getAuthors().get(0))
                .replace("{/subject/}", bookInfo.getSubjects())
                .replace("{/description/}", bookInfo.getDescription())
                .replace("{/cover_url/}", bookInfo.getImage());

        GeminiManager geminiManager = GeminiManager.getInstance();

        ProgressDialog pD = new ProgressDialog(getContext());
        pD.setTitle("Sent Prompt");
        pD.setMessage("Waiting for response...");
        pD.setCancelable(false);
        pD.show();
        geminiManager.sendTextPrompt(prompt,
                new GeminiCallback()
                {
                    @Override
                    public void onSuccess(String result) {
                        pD.dismiss();
                        tVGeminiReco.setText(result);
                        btnGenerate.setEnabled(false);
                    }

                    @Override
                    public void onFailure(Throwable error)
                    {
                        pD.dismiss();
                        tVGeminiReco.setText("Failed prompting Gemini:\n" + error.getMessage());
                    }
                });
    }
}