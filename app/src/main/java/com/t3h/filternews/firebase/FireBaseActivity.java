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
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public void initFirebase(){
         firebaseDatabase = FirebaseDatabase.getInstance();
         databaseReference = firebaseDatabase.getReference();
     }

    public void insertUser(Object objects, String email){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String emailEncode = encodeUserEmail(email);
        databaseReference = databaseReference.child("user").child(emailEncode);
        databaseReference.setValue(objects);
    }

    public void insertKeyword(String email, String keyword){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String emailEncode = encodeUserEmail(email);
        databaseReference  = databaseReference.child("user").child(emailEncode).child("keyword").push();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String date = sdf.format(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put(date, keyword);
        databaseReference.updateChildren(map);
    }

    public void insertTotalTime(String email, long totalTime){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String emailEncode = encodeUserEmail(email);
        databaseReference  = databaseReference.child("user").child(emailEncode).child("totalTime").push();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String date = sdf.format(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put(date, totalTime);
        databaseReference.updateChildren(map);
    }

    public void insertTitle(String email, String title){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String emailEncode = encodeUserEmail(email);
        databaseReference  = databaseReference.child("user").child(emailEncode).child("title").push();
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
                    String email = user.getEmail();
                    if(object instanceof FragmentItemNews) {
                        FragmentItemNews a = (FragmentItemNews) object;
                        a.setEmailFrag(email);
                    }
                    if(object instanceof String) {
                        insertKeyword(email, object.toString());
                    }
                    if (object instanceof Long ){
                        insertTotalTime(email, ((Long) object).longValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

}


