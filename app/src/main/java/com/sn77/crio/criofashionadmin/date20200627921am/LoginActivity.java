package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText userName;
    EditText userPassword;
    Button button;
    DatabaseReference rootRef;
    FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName=findViewById(R.id.editTextUserId);
        userPassword=findViewById(R.id.editTextPassword);
        button=findViewById(R.id.button);
        mAuth=FirebaseAuth.getInstance();

        rootRef= FirebaseDatabase.getInstance().getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signInWithEmailAndPassword(userName.getText().toString(),userPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mCurrentUser=mAuth.getCurrentUser();//setting current user after signIn
                            rootRef.child("company_details").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(mCurrentUser.getUid())){

                                        Intent intent=new Intent(getApplicationContext(),Inventory_Activity.class);
                                        startActivity(intent);

                                    }
                                    else {
                                        Toast.makeText(LoginActivity.this, "Add Your Details", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(getApplicationContext(),CompanyDetailsActivity.class);
                                        startActivity(intent);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else{
                            Toast.makeText(getApplicationContext(),"wrong",Toast.LENGTH_LONG);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }


    public void buttonClicked(View view) {//this button is for testing purpose only
        Intent intent=new Intent(getApplicationContext(),CompanyDetailsActivity.class);
        startActivity(intent);

    }
}