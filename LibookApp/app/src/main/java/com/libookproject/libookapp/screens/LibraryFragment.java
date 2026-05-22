package com.libookproject.libookapp.screens;

import static com.libookproject.libookapp.FBRef.Uid;
import static com.libookproject.libookapp.FBRef.refUsers;

import android.app.AlertDialog;
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
import com.libookproject.libookapp.adapters.CustomAdapterLibrary;
import com.libookproject.libookapp.R;
import com.libookproject.libookapp.dataObjects.SavedBook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * fragment responsible for displaying and managing the user's personal book library.
 * shows the books saved on the currently selected shelf.
 * allows the user to create new shelves, delete existing ones, and navigate between them.
 * book and shelf data are loaded in real time from Firebase Realtime Database.
 */
public class LibraryFragment extends Fragment
{
    private static final String KEY_SHELF_NAME = "shelfName";
    private static final String KEY_DRAWER_OPEN = "drawerOpen";
    private static final String KEY_RECYCLER_STATE = "recyclerState";

    private View view;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageView menuIcon;
    private TextView tvShelfName;
    private TextView tvBookCount;
    private ImageButton btnDeleteShelf;
    private RecyclerView recyclerViewBooks;

    private DatabaseReference refCurrUserShelves;
    private DatabaseReference refCurrShelf;
    private ValueEventListener shelfListener = null;
    private ValueEventListener shelvesListener = null;
    private CustomAdapterLibrary adpBooks;
    private ArrayList<SavedBook> booksList;

    // The shelf that is currently displayed - default "favorites".
    private String currentShelfName = "favorites";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library, container, false);
        refCurrUserShelves = refUsers.child(Uid).child("Shelves");

        init();

        // restore which shelf was open
        if (savedInstanceState != null)
        {
            currentShelfName = savedInstanceState.getString(KEY_SHELF_NAME, "favorites");
        }

        tvShelfName.setText(currentShelfName);
        btnDeleteShelf.setVisibility(
                currentShelfName.equals("favorites") ? View.GONE : View.VISIBLE);

        //load the current shelf's books, and the shelves menu
        loadBooks(currentShelfName);
        loadShelvesMenu();

        //restore drawer open state
        if (savedInstanceState != null) {
            boolean drawerWasOpen = savedInstanceState.getBoolean(KEY_DRAWER_OPEN, false);
            if (drawerWasOpen)
            {
                drawerLayout.post(() -> drawerLayout.openDrawer(GravityCompat.END));
            }
        }

        //restore RecyclerView scroll position
        if (savedInstanceState != null)
        {
            Bundle recyclerState = savedInstanceState.getBundle(KEY_RECYCLER_STATE);
            if (recyclerState != null)
            {
                recyclerViewBooks.post(() ->
                {
                    RecyclerView.LayoutManager lm = recyclerViewBooks.getLayoutManager();
                    if (lm != null)
                    {
                        lm.onRestoreInstanceState(recyclerState.getParcelable(KEY_RECYCLER_STATE));
                    }
                });
            }
        }

        attachListeners();

        return view;
    }

    //saves the current state of the fragment before it is destroyed.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //check if is hidden
        if (drawerLayout == null)
        {
            return;
        }

        //save which shelf is open
        outState.putString(KEY_SHELF_NAME, currentShelfName);

        //save drawer open/closed state
        outState.putBoolean(KEY_DRAWER_OPEN,
                drawerLayout.isDrawerOpen(GravityCompat.END));

        //save RecyclerView scroll position
        RecyclerView.LayoutManager lm = recyclerViewBooks.getLayoutManager();
        if (lm != null)
        {
            Bundle recyclerState = new Bundle();
            recyclerState.putParcelable(KEY_RECYCLER_STATE, lm.onSaveInstanceState());
            outState.putBundle(KEY_RECYCLER_STATE, recyclerState);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        //remove shelf listener
        if (shelfListener != null && refCurrShelf != null) {
            refCurrShelf.removeEventListener(shelfListener);
        }

        //remove shelves menu listener
        if (shelvesListener != null && refCurrUserShelves != null) {
            refCurrUserShelves.removeEventListener(shelvesListener);
        }
    }

    private void init()
    {
        drawerLayout = view.findViewById(R.id.drawerLayout);
        navView = view.findViewById(R.id.navView);
        menuIcon = view.findViewById(R.id.menuIcon);
        btnDeleteShelf = view.findViewById(R.id.btnDeleteShelf);
        tvShelfName = view.findViewById(R.id.tvShelfName);
        tvBookCount = view.findViewById(R.id.tvBookCount);

        //set RecyclerView with three column
        recyclerViewBooks = view.findViewById(R.id.recyclerViewBooks);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setReverseLayout(false);
        recyclerViewBooks.setLayoutManager(layoutManager);

        booksList = new ArrayList<>();
        adpBooks = new CustomAdapterLibrary(getContext(), booksList, book -> {
            BookInfoFragment bookInfoFragment = new BookInfoFragment();

            Bundle bundle = new Bundle();
            bundle.putString("id", book.getId());
            bookInfoFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameLayout, bookInfoFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerViewBooks.setAdapter(adpBooks);
    }

    /**
     * attaches click and selection listeners to all interactive UI elements.
     * the menu icon opens the shelf drawer. the NavigationView item listener switches
     * to the selected shelf. the add shelf button in the drawer header shows the
     * add shelf dialog. the delete button shows a confirmation dialog before deleting.
     */
    private void attachListeners()
    {
        menuIcon.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.END));

        navView.setNavigationItemSelectedListener(item -> {
            showShelf(item.getTitle().toString());
            drawerLayout.closeDrawer(navView);
            return true;
        });

        View headerView = navView.getHeaderView(0);
        ImageButton btnAddShelf = headerView.findViewById(R.id.btnAddShelf);
        btnAddShelf.setOnClickListener(v -> showAddShelfDialog());

        btnDeleteShelf.setOnClickListener(v ->
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Shelf")
                        .setMessage("This action cannot be undone.")
                        .setPositiveButton("Delete", (dialog, which) ->
                                deleteShelf(tvShelfName.getText().toString()))
                        .setNegativeButton("Cancel", null)
                        .show());
    }

    /**
     * switches the displayed shelf to the given shelf name.
     * updates the shelf name label, reloads the books list, and shows or hides
     * the delete button (hidden for the "favorites" shelf).
     */
    private void showShelf(String shelfName)
    {
        currentShelfName = shelfName;
        tvShelfName.setText(shelfName);
        loadBooks(shelfName);
        btnDeleteShelf.setVisibility(
                shelfName.equals("favorites") ? View.GONE : View.VISIBLE);
    }

    /**
     * attaches a realtime Firebase listener to the given shelf.
     * removes any previous shelf listener before attaching the new one.
     * on each data change, show the new books list.
     */
    private void loadBooks(String shelfName)
    {
        //remove previous listener before switching shelves
        if (shelfListener != null && refCurrShelf != null) {
            refCurrShelf.removeEventListener(shelfListener);
        }
        refCurrShelf = refCurrUserShelves.child(shelfName);

        shelfListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                booksList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getValue() instanceof String) {
                        booksList.add(new SavedBook(
                                (String) data.getValue(), data.getKey()));
                    }
                }
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

    /**
     * attaches a realtime Firebase listener to the user's Shelves.
     * on each data change, shows the new NavigationView menu
     * with the current list of shelf names.
     */
    private void loadShelvesMenu()
    {
        shelvesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                navView.getMenu().clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    navView.getMenu().add(data.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                navView.getMenu().clear();
                showFirebaseError(error);
            }
        };
        refCurrUserShelves.addValueEventListener(shelvesListener);
    }

    /**
     * shows a dialog allowing the user to enter a name for a new shelf.
     * on confirmation, creates the shelf in Firebase under the user's Shelves node.
     * shows an error if the name field is left empty.
     */
    private void showAddShelfDialog()
    {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_shelf);
        dialog.setCancelable(true);

        EditText etShelfName = dialog.findViewById(R.id.etShelfName);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v ->
        {
            String shelfName = etShelfName.getText().toString().trim();
            if (!shelfName.isEmpty()) {
                refCurrUserShelves.child(shelfName).child("_meta")
                        .setValue(true)
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(),
                                        "Failed to create shelf",
                                        Toast.LENGTH_SHORT).show());
                dialog.dismiss();
            }
            else {
                etShelfName.setError("Name can not be empty");
            }
        });

        dialog.show();
    }

    /**
     * deletes the specified shelf and removes all its books from the user's SavedBooksIndex
     * in a single atomic Firebase update. after deletion, switches the view back to
     * the "favorites" shelf and shows a confirmation toast.
     */
    private void deleteShelf(String shelfName)
    {
        refCurrShelf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> updates = new HashMap<>();

                for (DataSnapshot bookSnap : snapshot.getChildren()) {
                    String bookId = bookSnap.getKey();
                    if (bookId.equals("_meta")) continue;
                    updates.put(Uid + "/SavedBooksIndex/" + bookId + "/" + shelfName, null);
                }
                updates.put(Uid + "/Shelves/" + shelfName, null);

                refUsers.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            showShelf("favorites");
                            Toast.makeText(getContext(),
                                    "Your shelf has been deleted",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(),
                                        "Error occurred: " + e,
                                        Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showFirebaseError(error);
            }
        });
    }

    //displays a generic error toast when a Firebase operation fails.
    private void showFirebaseError(DatabaseError error)
    {
        if (getContext() != null) {
            Toast.makeText(getContext(),
                    "Error: Check your connection.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}