package com.t3h.filternews.firebase;



import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


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

//    public boolean checkExists(String nameCol, String nameAttr){
//        Query query = databaseReference.child("user").orderByChild(nameCol).equalTo(nameAttr);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//
//                }else {
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        return false;
//    }

}
