package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Saller_Information extends AppCompatActivity {

    private EditText companyName;
    private EditText companyPhoneNumber;
    private EditText companyGstNumber;
    private TextView companyEmail;
    private TextView companyId;

    private DatabaseReference companyInfoRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private ImageButton companyNameId;
    private ImageButton companyNameIdToDb;

    private ImageButton companyPhoneId;
    private ImageButton companyPhoneIdToDb;

    private ImageButton companyGstId;
    private ImageButton companyGstToDb;
    StorageReference  pStorageRef;

    private EditText sizeChartName;
    Uri sizeChartUri;
    private ImageView sizeChart;
    private CircleImageView user_image;
    protected ProgressDialog progressDialog;
    private RecyclerView sizeChartRecyclerView;
    Uri userImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saller__information);

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        companyInfoRef= FirebaseDatabase.getInstance().getReference().child("company_details");
        pStorageRef = FirebaseStorage.getInstance().getReference();
        companyName=findViewById(R.id.companyName);
        companyPhoneNumber=findViewById(R.id.companyPhoneNumber);
        companyGstNumber=findViewById(R.id.companyGSTNumber);
        companyEmail=findViewById(R.id.companyEmail);
        companyId=findViewById(R.id.companyId);
        companyName.setEnabled(false);
        companyPhoneNumber.setEnabled(false);
        companyGstNumber.setEnabled(false);

        user_image=findViewById(R.id.user_image);

        companyNameId=findViewById(R.id.changeCompanyNameId);
        companyNameIdToDb=findViewById(R.id.changeCompanyNameIdToDb);

        companyPhoneId=findViewById(R.id.changeCompanyPhoneId);
        companyPhoneIdToDb=findViewById(R.id.changeCompanyPhoneIdToDb);

        companyGstId=findViewById(R.id.changeCompanyGstId);
        companyGstToDb=findViewById(R.id.changeCompanyGstIdToDb);


        sizeChartRecyclerView=findViewById(R.id.sizeChartChoosingRecyclerView);
        sizeChartRecyclerView.hasFixedSize();
        sizeChartRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));



        companyPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){

                    if ( companyPhoneNumber.getText().length()!=10){
                        companyPhoneNumber.setError("Phone number should be 10 digit");
                    }
                    else {
                        companyPhoneNumber.setError(null);
                    }
                }
            }
        });

        companyInfoRef.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CompanyDetails companyDetails=dataSnapshot.getValue(CompanyDetails.class);
                companyName.setText(companyDetails.getUser_name());
                companyEmail.setText(companyDetails.getCompany_email());
                companyPhoneNumber.setText(companyDetails.getCompany_phone_number());
                companyGstNumber.setText(companyDetails.getGST_id());
                companyId.setText(companyDetails.getSeller_id());

                if (companyDetails.getUser_image()!=null) {
                    Picasso.with(getApplicationContext()).load(companyDetails.getUser_image()).placeholder(R.drawable.ic_user).into(user_image);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadProfilePhoto();
            }
        });
        sizeChartName=findViewById(R.id.sizeChartName);
        sizeChart = findViewById(R.id.sizeChart);
        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery, "Select Image1"), 1);
            }
        });

    }

    private void uploadProfilePhoto() {


        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setTitle(" Change profile photo")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setMessage("Do you want to change profile photo?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                        pStorageRef.child("Seller_image").child(mCurrentUser.getUid()+"/").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .start(Saller_Information.this);

                            }
                        });


                    }
                })
                .setNegativeButton("NO", null)
                .setCancelable(false)
                .show();




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                userImageUri= result.getUri();
                user_image.setImageURI(userImageUri);

                // final String randomkey = UUID.randomUUID().toString();



                if (  userImageUri!=null){
                    final StorageReference riversRef = pStorageRef.child("Seller_image").child(mCurrentUser.getUid()).child("user_image.jpg");
                    showProgressDialog();
                    riversRef.putFile(userImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    Map   companyDetails=new HashMap();
                                    companyDetails.put("user_image",downloadUrl);

                                    companyInfoRef.child(mCurrentUser.getUid()).updateChildren(companyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                dismissProgressDialog();
                                                Toast.makeText(Saller_Information.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                                            }


                                            else {
                                                dismissProgressDialog();
                                                Toast.makeText(Saller_Information.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dismissProgressDialog();
                                            Toast.makeText(Saller_Information.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                        }
                                    });




                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Saller_Information.this, "Error Occurred!", Toast.LENGTH_SHORT).show();

                                }
                            });

                            // Toast.makeText(CompanyDetailsActivity.this, "done", Toast.LENGTH_SHORT).show();

                        }
                    });
                }else {
                    Toast.makeText(this, "Image size must be less then  150KB", Toast.LENGTH_SHORT).show();
                    dismissProgressDialog();
                }





            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }


        }




        if (resultCode == RESULT_OK && requestCode == 1) {
            assert data != null;
            sizeChartUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sizeChartUri);
                sizeChart.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }







    }
    //size chart
    public void uploadImage(View view) {



        if ((CheckImageSize(sizeChartUri) / 1024)<160){



            if (sizeChartUri!=null&!sizeChartName.getText().toString().equals("")) {
                showProgressDialog();
                final String randomkey1 = UUID.randomUUID().toString();
                final StorageReference chartRef = pStorageRef.child("Size_charts").child(mCurrentUser.getUid()).child(randomkey1 + ".jpg");
                chartRef.putFile(sizeChartUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        chartRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String downloadUrl1 = uri.toString();
                                Map sizeChartDetails = new HashMap();
                                sizeChartDetails.put("size_chart_name", sizeChartName.getText().toString());
                                sizeChartDetails.put("chart_url", downloadUrl1);

                                companyInfoRef.child(mCurrentUser.getUid()).child("size_charts").child(sizeChartName.getText().toString()).updateChildren(sizeChartDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dismissProgressDialog();
                                            Toast.makeText(Saller_Information.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                            sizeChartName.setText("");
                                            sizeChart.setImageResource(R.drawable.ic_baseline_account_box_24);
                                            dismissProgressDialog();

                                        } else {
                                            dismissProgressDialog();
                                            Toast.makeText(Saller_Information.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });

                            }
                        });
                    }
                });
            }else {
                Toast.makeText(this, "You Have To Select Name And Image Both", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }

        }else {
            Toast.makeText(this, "Image size must be less then  150KB", Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
        }

    }


    public void showProgressDialog() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(Saller_Information.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }

        progressDialog.setMessage("Uploading Please wait!!");
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void changePassword(View view) {


        AlertDialog alertDialog = new AlertDialog.Builder(this)

                .setIcon(android.R.drawable.ic_dialog_alert)

                .setTitle("Are you sure ?")

                .setMessage("You will be redirected to G-mail to send us Password changing request. ")

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String[] email={"sellers@criof.com"};

                        Intent intent=new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL,email);
                        intent.putExtra(Intent.EXTRA_SUBJECT,"Request for Password Change");
                        intent.putExtra(Intent.EXTRA_TEXT,"I request you to change the password and set it to-");
                        startActivity(Intent.createChooser(intent,"Choose one.."));
                    }
                })

                .setNegativeButton("CANCEL", null)
                .show();

    }

    public void changeCompnayName(View view) {
        companyName.setEnabled(true);
        companyName.requestFocus();
        companyName.setCursorVisible(true);
            companyNameId.setVisibility(View.GONE);
            companyNameIdToDb.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }

    public void changeCompnayPhoneNumber(View view) {
        companyPhoneNumber.setEnabled(true);
        companyPhoneNumber.requestFocus();
        companyPhoneNumber.setCursorVisible(true);
        companyPhoneId.setVisibility(View.GONE);
        companyPhoneIdToDb.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public void changeCompnayGstNumber(View view) {
        companyGstNumber.setEnabled(true);
        companyGstNumber.requestFocus();
        companyGstNumber.setCursorVisible(true);
        companyGstId.setVisibility(View.GONE);
        companyGstToDb.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public void changeCompnayNameToDb(View view) {

        companyInfoRef.child(mCurrentUser.getUid()).child("user_name").setValue(companyName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    companyName.setEnabled(false);
                    companyNameId.setVisibility(View.VISIBLE);
                    companyNameIdToDb.setVisibility(View.GONE);
                    Toast.makeText(Saller_Information.this, "Success full!", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(Saller_Information.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void changeCompnayPhoneNumberToDb(View view) {

        if ( companyPhoneNumber.getText().length()!=10){
            Toast.makeText(this, "Length should be exactly 10", Toast.LENGTH_SHORT).show();
            companyPhoneNumber.setError("Length should be exactly 10");
        }

else {
        companyInfoRef.child(mCurrentUser.getUid()).child("company_phone_number").setValue(companyPhoneNumber.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    companyPhoneNumber.setEnabled(false);
                    companyPhoneId.setVisibility(View.VISIBLE);
                    companyPhoneIdToDb.setVisibility(View.GONE);
                    Toast.makeText(Saller_Information.this, "Successfull!", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(Saller_Information.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });
}

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//inflating the menu
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.help_support, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.help_support) {

            String[] email={"sellers@criof.com"};

            Intent intent=new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL,email);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Request for Help");

            startActivity(Intent.createChooser(intent,"Choose one.."));





        }
        return true;
    }

    public void changeCompnayGstNumberToDb(View view) {

        companyInfoRef.child(mCurrentUser.getUid()).child("GST_id").setValue(companyGstNumber.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    companyGstNumber.setEnabled(false);
                    companyGstId.setVisibility(View.VISIBLE);
                    companyGstToDb.setVisibility(View.GONE);
                    Toast.makeText(Saller_Information.this, "Success full!", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(Saller_Information.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public long CheckImageSize(Uri uri) {
        Cursor returnCursor = null;
        int sizeIndex = 0;

        if (uri != null) {
            returnCursor = getContentResolver().query(uri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            Log.e("TAG", "Name:" + returnCursor.getString(nameIndex));
            Log.e("TAG", "Size: " + Long.toString(returnCursor.getLong(sizeIndex)));
            //Toast.makeText(this,Long.toString(returnCursor.getLong(sizeIndex)) , Toast.LENGTH_SHORT).show();
            return returnCursor.getLong(sizeIndex);
        } else {

            //Toast.makeText(this,"empty" , Toast.LENGTH_SHORT).show();
            return 200;
        }


    }



    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference sizeChart = companyInfoRef.child(mCurrentUser.getUid()).child("size_charts");

        FirebaseRecyclerOptions<SizeChart> options=new FirebaseRecyclerOptions.Builder<SizeChart>()
                .setQuery(sizeChart,SizeChart.class)
                .build();

        FirebaseRecyclerAdapter<SizeChart, Saller_Information.SizeViewHolder> sizeChartHolder =new FirebaseRecyclerAdapter<SizeChart, Saller_Information.SizeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Saller_Information.SizeViewHolder sizeViewHolder, int i, @NonNull SizeChart sizeChart) {

                sizeViewHolder.sizeChartName.setText(sizeChart.getSize_chart_name());

            }

            @NonNull
            @Override
            public Saller_Information.SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.size_choose_item_design,parent,false);
                Saller_Information.SizeViewHolder sizeViewHolder=new Saller_Information.SizeViewHolder(view);

                return sizeViewHolder;
            }
        };sizeChartRecyclerView.setAdapter(sizeChartHolder);
        sizeChartHolder.startListening();
    }






    public void ChangeProfilePicture(View view) {

        uploadProfilePhoto();

    }


    public static class SizeViewHolder extends RecyclerView.ViewHolder {

        TextView sizeChartName;
        RelativeLayout sizeNameRelativeLayout;
        CardView sizeCardView;

        View mview;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
            sizeChartName=itemView.findViewById(R.id.size_chart_name);
            sizeNameRelativeLayout=itemView.findViewById(R.id.sizeNameRelativeLayout);
            sizeCardView=itemView.findViewById(R.id.sizeCardView);

        }
    }
}