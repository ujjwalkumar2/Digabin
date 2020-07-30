package com.codinghelper.digabin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.codinghelper.digabin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {

    private EditText t_email,t_tempassword,t_conpassword;
    private DatabaseReference rootRef;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        t_email=(EditText)findViewById(R.id.setEmail);
        t_tempassword=(EditText)findViewById(R.id.setPassword);
        t_conpassword=(EditText)findViewById(R.id.confirmPassword);
        Button btn_register = findViewById(R.id.confirmSignup);
        progressDialog = new ProgressDialog(this,R.style.AlertDialogTheme);
        progressDialog.setMessage("Registering User...");

        firebaseAuth=FirebaseAuth.getInstance();
        rootRef=FirebaseDatabase.getInstance().getReference().child("User");
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = t_email.getText().toString().trim();
                String password = t_tempassword.getText().toString().trim();
                String conpassword = t_conpassword.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    t_email.setError("Invalid email");
                    t_email.setFocusable(true);
                    return;
                }


                if (TextUtils.isEmpty(email)) {
                    t_email.setError("enter email");
                    t_email.setFocusable(true);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    t_tempassword.setError("enter password");
                    t_email.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(conpassword)) {
                    t_conpassword.setError("enter conpasword");
                    t_conpassword.setFocusable(true);
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "password short", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (password.equals(conpassword)) {

                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        currentUserId= firebaseAuth.getCurrentUser().getUid();
                                        rootRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                HashMap rec=new HashMap();
                                                rec.put("Email id",email);
                                                rootRef.child(currentUserId).updateChildren(rec).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(Signup.this, LoginActivity.class));
                                                        finish();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    } else {

                                        progressDialog.dismiss();

                                        Toast.makeText(getApplicationContext(), "Authontication failed", Toast.LENGTH_SHORT).show();

                                    }

                                    // ...
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "password not matched", Toast.LENGTH_SHORT).show();

                }

            }



        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}
