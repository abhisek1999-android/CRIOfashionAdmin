package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextInputLayout userName;
    TextInputLayout userPassword;
    Button button;
    DatabaseReference rootRef;
    FirebaseUser mCurrentUser;
    TextView forgot_password;
    TextView be_seller;
    protected ProgressDialog progressDialog;

    Boolean check = false;
    Boolean stateCheck = true;


    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (currentNetworkInfo.isConnected()) {
                if (check) {
                    stateCheck = true;
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Connected  ", Snackbar.LENGTH_LONG);
                    View snakBarView = snackbar.getView();
                    snakBarView.setBackgroundColor(Color.parseColor("#4ebaaa"));
                    snackbar.show();
                }
            } else {
                check = true;
                stateCheck = false;
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Not Connected  ", Snackbar.LENGTH_INDEFINITE);
                View snakBarView = snackbar.getView();
                snakBarView.setBackgroundColor(Color.parseColor("#ef5350"));
                snackbar.show();

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isConnected(getApplicationContext());
        setContentView(R.layout.activity_login);
        userName = findViewById(R.id.editTextUserId);
        userPassword = findViewById(R.id.editTextPassword);
        button = findViewById(R.id.button);
        mAuth = FirebaseAuth.getInstance();
        forgot_password = findViewById(R.id.forgotPassword);
        be_seller = findViewById(R.id.beSeller);
        rootRef = FirebaseDatabase.getInstance().getReference();

        getApplicationContext().registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));//CHECKING CONNECTIVITY

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userName.getEditText().getText().toString().equals("") | userPassword.getEditText().getText().toString().equals("")) {

                    Toasty.error(getApplicationContext(), "Id & Password can not be empty.", Toast.LENGTH_SHORT, true).show();
                    return;

                } else {
                    if (stateCheck) {
                        showProgressDialog();

                        mAuth.signInWithEmailAndPassword(Objects.requireNonNull(userName.getEditText()).getText().toString(), Objects.requireNonNull(userPassword.getEditText()).getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mCurrentUser = mAuth.getCurrentUser();//setting current user after signIn
                                    rootRef.child("company_details").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(mCurrentUser.getUid())) {
                                                dismissProgressDialog();
                                                Intent intent = new Intent(getApplicationContext(), Inventory_Activity.class);
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(LoginActivity.this, "Add Your Details", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), CompanyDetailsActivity.class);
                                                startActivity(intent);
                                                dismissProgressDialog();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG);
                                    mCurrentUser = null;
                                    dismissProgressDialog();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dismissProgressDialog();
                                mCurrentUser = null;
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
                        dismissProgressDialog();
                    }
                }


            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (stateCheck) {
                    String[] email = {"sellers@criof.com"};
                    // email=object.getString("email");
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, email);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Request for Password Change");
                    intent.putExtra(Intent.EXTRA_TEXT, "I request you to change the password and set it to-");
                    startActivity(Intent.createChooser(intent, "Choose one.."));
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        be_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(getApplicationContext(),BeASellerPage.class);
             startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent start = new Intent(Intent.ACTION_MAIN);
        start.addCategory(Intent.CATEGORY_HOME);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(start);
    }

    public void buttonClicked(View view) {//this button is for testing purpose only
        Intent intent = new Intent(getApplicationContext(), CompanyDetailsActivity.class);
        startActivity(intent);

    }

    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            return true;

        } else {
            showDialog();
            return false;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are not connected to the Internet!")
                .setCancelable(false)
                .setIcon(R.drawable.ic_baseline_perm_scan_wifi_24)
                .setTitle("No Internet Connection!")
                .setPositiveButton("Go to Setting", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                        }
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent start = new Intent(Intent.ACTION_MAIN);
                        start.addCategory(Intent.CATEGORY_HOME);
                        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(start);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showProgressDialog() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }

        progressDialog.setMessage("Please wait!!");
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}