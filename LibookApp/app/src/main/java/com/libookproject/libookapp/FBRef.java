package com.libookproject.libookapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * class holding all static Firebase references and current user data.
 */
public class FBRef
{
    public static String Uid = null;
    public static String username = null;

    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();

    public static DatabaseReference refUsers = FBDB.getReference("Users");
}
