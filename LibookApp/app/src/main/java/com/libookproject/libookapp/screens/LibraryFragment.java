package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.FBRef.refAuth;
import static com.libookproject.libookapp.FBRef.refUsers;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.libookproject.libookapp.CustomAdapterLibrary;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.SavedBook;

import java.util.ArrayList;
import java.util.Collections;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link LibraryFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class LibraryFragment extends Fragment
{
    private View view;
    private DatabaseReference refCurrUserShelves;
    private DatabaseReference refCurrShelf;
    private RecyclerView recyclerViewBooks;
    private ValueEventListener shelfListener = null;
    private CustomAdapterLibrary adpBooks;
    private ArrayList<SavedBook> booksList;

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageView menuIcon;
    private TextView tvShelfName;
    private TextView tvBookCount;

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
        loadShelvesMenu();

        menuIcon.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.END)
        );

        navView.setNavigationItemSelectedListener(item -> {
            String shelfName = item.getTitle().toString();
            tvShelfName.setText(shelfName);
            loadBooks(shelfName);
            drawerLayout.closeDrawer(navView);
            return true;
        });

        View headerView = navView.getHeaderView(0);
        ImageButton btnAddShelf = headerView.findViewById(R.id.btnAddShelf);

        btnAddShelf.setOnClickListener(v -> showAddShelfDialog());

        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if (shelfListener != null && refCurrShelf != null)
        {
            refCurrShelf.removeEventListener(shelfListener);
        }
    }

    private void init()
    {
        drawerLayout = view.findViewById(R.id.drawerLayout);
        navView = view.findViewById(R.id.navView);
        menuIcon = view.findViewById(R.id.menuIcon);

        tvShelfName = view.findViewById(R.id.tvShelfName);
        tvBookCount = view.findViewById(R.id.tvBookCount);

        recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setReverseLayout(false);      // newest on top

        recyclerViewBooks.setLayoutManager(layoutManager);

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
        recyclerViewBooks.setAdapter(adpBooks);
    }

    private void loadBooks(String shelfName)
    {
        if (shelfListener != null && refCurrShelf != null) {
            refCurrShelf.removeEventListener(shelfListener);
        }
        refCurrShelf = refCurrUserShelves.child(shelfName);

        shelfListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                booksList.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    if (data.getValue() instanceof String)
                    {
                        booksList.add(new SavedBook((String)data.getValue(), data.getKey()));
                    }
                }

                // Reverse list for proper display (first book bottom-left -> top-left)
                Collections.reverse(booksList);

                adpBooks.notifyDataSetChanged();
                tvBookCount.setText(booksList.size() + " ספרים");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                booksList.clear();
                adpBooks.notifyDataSetChanged();
                tvBookCount.setText(0 + " ספרים");
                showFirebaseError(error);
            }
        };
        refCurrShelf.addValueEventListener(shelfListener);
    }

    private void loadShelvesMenu() {
        navView.getMenu().clear();

        refCurrUserShelves.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren())
                {
                    navView.getMenu().add(data.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showFirebaseError(error);
            }
        });
    }

    private void showFirebaseError(DatabaseError error)
    {
        if (getContext() != null) {
            Toast.makeText(getContext(),
                    "Failed to load books. Check your connection.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddShelfDialog()
    {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_shelf);
        dialog.setCancelable(true);

        EditText etShelfName = dialog.findViewById(R.id.etShelfName);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String shelfName = etShelfName.getText().toString().trim();
            if (!shelfName.isEmpty()) {
                refCurrUserShelves.child(shelfName).child("_meta").setValue(true).addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Failed to create shelf",
                            Toast.LENGTH_SHORT).show();
                });
                loadShelvesMenu();
                dialog.dismiss();
            }
            else
            {
                etShelfName.setError("Name can not be empty");
            }
        });

        dialog.show();
    }
}


