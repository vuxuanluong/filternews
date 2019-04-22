package com.t3h.filternews.firebase;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.t3h.filternews.login.LoginActivity;
import com.t3h.filternews.main.ViewPagerActivity;
import com.t3h.filternews.model.User;

import java.util.Objects;

public class FireBaseActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public void firebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void insertUser(Object objects, String name){
        databaseReference = databaseReference.child("user").child(name);
        databaseReference.setValue(objects);
    }

}
