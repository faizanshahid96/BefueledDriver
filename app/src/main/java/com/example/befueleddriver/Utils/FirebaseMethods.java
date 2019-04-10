package com.example.befueleddriver.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;


import com.example.befueleddriver.Models.User;
import com.example.befueleddriver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseMethods {

    private static final String TAG = "";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mContext;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

//    public void checkIfUsernameExists(String username, DataSnapshot datasnapshot){
//        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");
//
//        User user = new User();
//
//        for (DataSnapshot ds: datasnapshot.child(userID).getChildren()){
//            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);
//
//            user.setFirstname(ds.getValue(User.class).getFirstname());
//            Log.d(TAG, "checkIfUsernameExists: username: " + user.getFirstname());
//
//            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
//                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
//                return true;
//            }
//        }
//
//    }

    /**
     *
     * @param email
     * @param firstname
     * @param password
     * @param phone
     * @param lastname
     */
    public void registerNewEmail(final String email, final String firstname, String password, long phone, String lastname){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(task.isSuccessful()){
                            //send verificaton email
//                            sendVerificationEmail();


                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }
                    }
                });
    }

    private void sendVerificationEmail() {
    }

    /**
     *
     * @param email
     * @param firstname
     * @param lastname
     * @param phone
     */
    public void addNewUser(String email,String firstname, String lastname, long phone){
        User user = new User(userID,phone,email,firstname,lastname);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);
    }


}
