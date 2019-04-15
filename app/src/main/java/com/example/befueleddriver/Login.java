package com.example.befueleddriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {


    private static final String TAG = "login";
    private Context mContext;
    private ProgressDialog mProgressdialog;
    private EditText mEmail, mPassword;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = Login.this;
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
//         Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
//         [END initialize_auth]
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void signIn(String email, String password) {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        Log.d(TAG, "signIn: "+email+" " +password);
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(mContext, "login successful", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
//                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            dialog.dismiss();
//                            Toast.makeText(mContext, "login Failed", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void signUp(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//    public void onClickListener(View view) {
//        if (view.getId() == R.id.btn_login) {
//
//        }
//    }

    private boolean isStringNull(String str) {
        return str.equals("");
    }

    public void signins(View view) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        Log.d(TAG, "signins: "+ email+"pass"+password);
        if (!isStringNull(email) && !isStringNull(password)){
            signIn(email,password);
        }
        else
            Toast.makeText(mContext, "Fill in the fields", Toast.LENGTH_SHORT).show();

    }
}

