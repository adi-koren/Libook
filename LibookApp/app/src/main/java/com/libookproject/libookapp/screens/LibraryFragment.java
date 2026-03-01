package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.FBRef.refAuth;
import static com.libookproject.libookapp.FBRef.refUsers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.libookproject.libookapp.CustomAdapterLibrary;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.SavedBook;

import java.util.ArrayList;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link LibraryFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class LibraryFragment extends Fragment
{
    private View view;
    private DatabaseReference refCurrUserShelves;
    private RecyclerView recyclerView;
    private CustomAdapterLibrary adpBooks;
    private ArrayList<SavedBook> booksList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_library, container, false);
        refCurrUserShelves = refUsers.child(refAuth.getUid()).child("Shelves");

        init();
        loadBooks("favorites");
        return view;
    }

    private void init()
    {
        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        // 3 columns grid
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setReverseLayout(true);      // newest on top

        recyclerView.setLayoutManager(layoutManager);

        booksList = new ArrayList<>();
        adpBooks = new CustomAdapterLibrary(getContext(), booksList, book -> {
            BookInfoFragment bookInfoFragment = new BookInfoFragment();

            Bundle bundle = new Bundle();
            bundle.putString("id",book.getId());
            bookInfoFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, bookInfoFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adpBooks);
    }

    private void loadBooks(String shelfName)
    {
        DatabaseReference refShelf = refCurrUserShelves.child(shelfName);
        refShelf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                booksList.clear();
                for (DataSnapshot data: snapshot.getChildren())
                {
                    if (data.getValue() instanceof String)
                    {
                        booksList.add(0, new SavedBook((String) data.getValue(), data.getKey()));
                    }
                }
                adpBooks.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
    }
}


