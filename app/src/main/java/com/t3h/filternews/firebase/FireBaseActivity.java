package com.t3h.filternews.firebase;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.t3h.filternews.model.User;
import com.t3h.filternews.viewpager.FragmentItemNews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class FireBaseActivity {

    public void insertUser(Object objects, String name){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("user").child(name);
        databaseReference.setValue(objects);
    }

    public void insertKeyword(String name, String keyword){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference  = databaseReference.child("user").child(name).child("keyword").push();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String date = sdf.format(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put(date, keyword);
        databaseReference.updateChildren(map);
    }

    public void insertTotalTime(String name, long totalTime){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference  = databaseReference.child("user").child(name).child("totalTime").push();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String date = sdf.format(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put(date, totalTime);
        databaseReference.updateChildren(map);
    }

    public void insertTitle(String name, String title){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference  = databaseReference.child("user").child(name).child("title").push();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String date = sdf.format(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put(date, title);
        databaseReference.updateChildren(map);
    }

    public void insertKeywordAndTitleAndTotalTimeFirebase(String username, final Object object){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query queryName = databaseReference.child("user").orderByChild("username").equalTo(username);
        queryName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    User user = data.getValue(User.class);
                    String nameLogin = user.getName();
                    if(object instanceof FragmentItemNews) {
                        FragmentItemNews a = (FragmentItemNews) object;
                        a.setNameFrag(nameLogin);
                    }
                    if(object instanceof String) {
                        insertKeyword(nameLogin, object.toString());
                    }
                    if (object instanceof Long ){
                        insertTotalTime(nameLogin, ((Long) object).longValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}


