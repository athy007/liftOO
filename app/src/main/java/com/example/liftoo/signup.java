package com.example.liftoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.share.internal.DeviceShareDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends Activity {

    private Button btn;
    EditText txt_FullName,txt_Email,txt_Pass;
    Button Register;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        btn =  findViewById(R.id.sign_up);
        txt_FullName = findViewById(R.id.names);
        txt_Email =findViewById(R.id.email);
        txt_Pass = findViewById(R.id.pass);
        Register = findViewById(R.id.validate);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth = FirebaseAuth.getInstance();

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fullName = txt_FullName.getText().toString();
                final String email =txt_Email.getText().toString();
                final String password =txt_Pass.getText().toString();

                if (email.isEmpty()){
                    Toast.makeText(signup.this, "Please enter Email", Toast.LENGTH_SHORT).show();
                }
                if (password.isEmpty()){
                    Toast.makeText(signup.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                }
                if (fullName.isEmpty()){
                    Toast.makeText(signup.this, "Please enter names", Toast.LENGTH_SHORT).show();
                }
                if (password.length()<6){
                    Toast.makeText(signup.this, "the password is too short", Toast.LENGTH_SHORT).show();
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Register information = new Register(
                                            fullName,
                                            email

                                    );

                                    FirebaseDatabase.getInstance().getReference("User")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(signup.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(),mapActivity.class));
                                        }
                                    });

                                } else {

                                }

                                // ...
                            }
                        });
            }
        });


    }


    public void openLogin(){

        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);

    }
}
