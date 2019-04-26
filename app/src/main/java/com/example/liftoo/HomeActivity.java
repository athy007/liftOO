package com.example.liftoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.lang.reflect.Array;
import java.util.Arrays;

public class HomeActivity extends Activity {
    private Button btn;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView txt;
    GoogleSignInClient mGoogleSignInClient;

    LoginButton loginButton;
    SignInButton signInButton;

    CallbackManager callbackManager;

    EditText txt_Email,txt_Pass;
    Button btn_login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user == null){

            setContentView(R.layout.login);
            FacebookSdk.sdkInitialize(getApplicationContext());

            loginButton = findViewById(R.id.fblog);

            callbackManager = CallbackManager.Factory.create();
            loginButton.setReadPermissions(Arrays.asList("email"));

            txt = findViewById(R.id.txt2);

        }else{
            Intent myIntent = new Intent(HomeActivity.this, mapActivity.class);
            startActivity(myIntent);
        }




        btn =  findViewById(R.id.sign_up);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUp();
            }
        });

        txt_Email = findViewById(R.id.log_email);
        txt_Pass = findViewById(R.id.log_pass);
        btn_login= findViewById(R.id.login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email =txt_Email.getText().toString().trim();
                final String password =txt_Pass.getText().toString().trim();

                if (email.isEmpty()){
                    Toast.makeText(HomeActivity.this, "Please enter Email", Toast.LENGTH_SHORT).show();
                }
                if (password.isEmpty()){
                    Toast.makeText(HomeActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                }
                if (password.length()<6){
                    Toast.makeText(HomeActivity.this, "the password is too short", Toast.LENGTH_SHORT).show();
                }


                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(HomeActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(),mapActivity.class));

                                } else {

                                    Toast.makeText(HomeActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });

            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        signInButton = findViewById(R.id.btn_google);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

    }


    public void btnClickLogFb(View v){
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(HomeActivity.this, "user cancel it", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser myuserobj = auth.getCurrentUser();
                            updateUI(myuserobj);

                        }else{
                            Toast.makeText(getApplicationContext(),"couldn't register to firebase",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void onActivityResult(int requestCode,int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode,data);
        super.onActivityResult(requestCode,resultCode,data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
    }

    private void updateUI(FirebaseUser myuserobj) {

        txt.setText(myuserobj.getEmail());
    }




    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = auth.getCurrentUser();
                            Intent i = new Intent(getApplicationContext(),mapActivity.class);
                            startActivity(i);
                            finish();

                            Toast.makeText(HomeActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(HomeActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    public void openSignUp(){

        Intent intent = new Intent(this,signup.class);
        startActivity(intent);

    }
}