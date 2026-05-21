package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.FBRef.refAuth;
import static com.libookproject.libookapp.FBRef.refUsers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import com.libookproject.libookapp.ReviewsViewModel;
import com.libookproject.libookapp.GeminiCallback;
import com.libookproject.libookapp.GeminiManager;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.serverApi.ApiCallback;
import com.libookproject.libookapp.serverApi.BooksApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookInfoFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    private static final String KEY_BOOK_INFO = "bookInfo";
    private static final String KEY_GEMINI_TEXT = "geminiText";
    private static final String KEY_GEMINI_BTN_ENABLED = "geminiBtnEnabled";
    private static final String KEY_PREV_SHELF = "prevShelf";
    private static final String KEY_NEW_SHELF = "newShelf";

    private View view;
    private TextView tVTitle;
    private TextView tVAuthor;
    private TextView tVSubj;
    private TextView tVDesc;
    private ImageView iVImage;
    private TextView tVGeminiReco;
    private Button btnGenerate;
    private Spinner sSave;

    private String bookId;
    private Book bookInfo;
    private ReviewsViewModel reviewsViewModel;
    private String prevSelectedShelf;
    private String newSelectedShelf;
    private DatabaseReference refCurrUserShelves;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            bookId = getArguments().getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_book_info, container, false);

        init();

        reviewsViewModel = new ViewModelProvider(requireActivity())
                .get(ReviewsViewModel.class);

        setupReviewFragment();

        refCurrUserShelves = refUsers.child(refAuth.getUid()).child("Shelves");

        if (savedInstanceState != null)
        {
            restoreState(savedInstanceState);
        }
        else
        {
            showBookInfo();
            enableSaveOption();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //check if is hidden
        if (view == null)
        {
            return;
        }

        //save the book info
        outState.putParcelable(KEY_BOOK_INFO, bookInfo);

        //save Gemini details
        outState.putString(KEY_GEMINI_TEXT, tVGeminiReco.getText().toString());
        outState.putBoolean(KEY_GEMINI_BTN_ENABLED, btnGenerate.isEnabled());

        //save shelf selection
        outState.putString(KEY_PREV_SHELF, prevSelectedShelf);
        outState.putString(KEY_NEW_SHELF, newSelectedShelf);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        reviewsViewModel.clear();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if (prevSelectedShelf != null && !prevSelectedShelf.equals(newSelectedShelf)) {
            removeBookFromShelf(prevSelectedShelf);
        }
        if (newSelectedShelf != null && !newSelectedShelf.equals(prevSelectedShelf)) {
            addBookToShelf(newSelectedShelf);
        }

        prevSelectedShelf = newSelectedShelf;
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

    private void setupReviewFragment()
    {
        //check if the reviews fragment already exists
        Fragment existing = getChildFragmentManager()
                .findFragmentById(R.id.reviewsFrameLayout);

        if (existing == null)
        {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.reviewsFrameLayout, new ReviewsFragment())
                    .commit();
        }
    }

    private void restoreState(Bundle savedInstanceState) {
        //restore book info
        bookInfo = savedInstanceState.getParcelable(KEY_BOOK_INFO);
        if (bookInfo != null)
        {
            bindData();

            //notifying reviews fragment the reviews changed
            reviewsViewModel.setFields(bookInfo.getId(), bookInfo.getReviews(),
                    bookInfo.getUser_review(), bookInfo.getRating_stats());

            ReviewsFragment fragment = (ReviewsFragment)
                    getChildFragmentManager().findFragmentById(R.id.reviewsFrameLayout);
            if (fragment != null)
            {
                fragment.bindData();
            }
        }

        //restore gemini text and button state
        String geminiText = savedInstanceState.getString(KEY_GEMINI_TEXT, "");
        if (!geminiText.isEmpty())
        {
            tVGeminiReco.setText(geminiText);
        }
        btnGenerate.setEnabled(
                savedInstanceState.getBoolean(KEY_GEMINI_BTN_ENABLED, true));

        //restore shelf selections
        prevSelectedShelf = savedInstanceState.getString(KEY_PREV_SHELF);
        newSelectedShelf  = savedInstanceState.getString(KEY_NEW_SHELF);

        //reload the spinner
        enableSaveOption();
    }

    private void showBookInfo()
    {
        String userId = refAuth.getUid();
        BooksApiService.getBookInfo(bookId, userId, new ApiCallback<Book>() {
            @Override
            public void onBookInfoLoaded(Book book) {
                if (isAdded()) {
                    reviewsViewModel.setFields(book.getId(), book.getReviews(),
                            book.getUser_review(), book.getRating_stats());

                    bookInfo = book;
                    bindData();

                    ReviewsFragment fragment = (ReviewsFragment)
                            getChildFragmentManager().findFragmentById(R.id.reviewsFrameLayout);
                    if (fragment != null) {
                        fragment.bindData();
                    }
                }
            }

            @Override
            public void onBookInfoError(String err) {
                if (isAdded()) {
                    Toast.makeText(getContext(), err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void bindData()
    {
        tVTitle.setText(bookInfo.getTitle());
        tVAuthor.setText(bookInfo.getAuthors().get(0));
        tVSubj.setText(bookInfo.getSubjects());
        tVDesc.setText(Html.fromHtml(bookInfo.getDescription(),
                Html.FROM_HTML_MODE_LEGACY));

        String imageUrl = bookInfo.getImage();
        if (!imageUrl.equals("ERROR")) {
            imageUrl = imageUrl.replace("http://", "https://");
            Glide.with(view)
                    .load(imageUrl)
                    .placeholder(R.drawable.image_not_found)
                    .error(R.drawable.image_not_found)
                    .into(iVImage);
        }
        else {
            iVImage.setImageResource(R.drawable.image_not_found);
        }
    }

    private void enableSaveOption()
    {
        ArrayList<String> shelvesNames = new ArrayList<>();
        shelvesNames.add("save in");

        refCurrUserShelves.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded())
                {
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {
                    shelvesNames.add(data.getKey());
                }

                ArrayAdapter<String> adp = new ArrayAdapter<>(getContext(),
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        shelvesNames);
                sSave.setAdapter(adp);

                //check what is the shelf selection
                if (newSelectedShelf != null)
                {
                    int position = adp.getPosition(newSelectedShelf);
                    if (position >= 0)
                    {
                        //remove listener so the onItemSelected won't be called
                        sSave.setOnItemSelectedListener(null);
                        sSave.setSelection(position);
                        sSave.setOnItemSelectedListener(BookInfoFragment.this);
                    }
                }
                else {
                    showSavedMode();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showSavedMode() {
        DatabaseReference refSavedIndex = refUsers.child(refAuth.getUid())
                .child("SavedBooksIndex").child(bookId);

        refSavedIndex.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded())
                {
                    return;
                }

                if (snapshot.exists()) {
                    for (DataSnapshot shelfSnap : snapshot.getChildren()) {
                        String shelfName = shelfSnap.getKey();
                        prevSelectedShelf = shelfName;

                        ArrayAdapter<String> adapter =
                                (ArrayAdapter<String>) sSave.getAdapter();
                        int position = adapter.getPosition(shelfName);
                        if (position >= 0)
                        {
                            sSave.setOnItemSelectedListener(null);
                            sSave.setSelection(position);
                            sSave.setOnItemSelectedListener(BookInfoFragment.this);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String shelfName = parent.getItemAtPosition(position).toString();
        newSelectedShelf = shelfName.equals("save in") ? null : shelfName;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void addBookToShelf(String shelfName) {
        Map<String, Object> updates = new HashMap<>();
        String userId = refAuth.getUid();

        updates.put(userId + "/Shelves/" + shelfName + "/" + bookId,
                bookInfo.getImage());
        updates.put(userId + "/SavedBooksIndex/" + bookId + "/" + shelfName,
                true);

        refUsers.updateChildren(updates).addOnFailureListener(e -> {
            if (!isAdded())
            {
                return;
            }
            if (e instanceof com.google.firebase.FirebaseNetworkException) {
                Toast.makeText(getContext(),
                        "No internet connection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                        "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeBookFromShelf(String shelfName) {
        Map<String, Object> updates = new HashMap<>();
        String userId = refAuth.getUid();

        updates.put(userId + "/Shelves/" + shelfName + "/" + bookId, null);
        updates.put(userId + "/SavedBooksIndex/" + bookId + "/" + shelfName, null);

        refUsers.updateChildren(updates).addOnFailureListener(e -> {
            if (!isAdded())
            {
                return;
            }
            if (e instanceof com.google.firebase.FirebaseNetworkException) {
                Toast.makeText(getContext(),
                        "No internet connection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                        "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        //disable button and show loading text
        btnGenerate.setEnabled(false);
        tVGeminiReco.setText("Waiting for response...");

        GeminiManager.getInstance().sendTextPrompt(prompt, new GeminiCallback() {
            @Override
            public void onSuccess(String result) {
                if (isAdded()) {
                    tVGeminiReco.setText(result);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                if (isAdded()) {
                    tVGeminiReco.setText("Failed prompting Gemini:\n" + error.getMessage());
                    //enable button so user can try again
                    btnGenerate.setEnabled(true);
                }
            }
        });
    }
}