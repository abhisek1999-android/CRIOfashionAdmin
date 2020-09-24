package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CompanyDetailsActivity extends AppCompatActivity {

    private EditText companyName;
    private EditText companyEmail;
    private EditText companyPhoneNumber;
    private EditText companyGstId;
    private EditText userName;
    private EditText sizeChartName;
    private EditText sellerId;

    private String installationId;
    Uri sizeChartUri;
    protected ProgressDialog progressDialog;
    Button upload;
    private ImageView sizeChart;

    private CircleImageView companyImage;

  //  CropImageView companyImage;

    private DatabaseReference rootRef;
    private EditText houseNo,city,district,state,pin;

    private FirebaseAuth mAuth;

    private FirebaseUser mCurrentUser;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    Uri userImageUri;
    StorageReference  pStorageRef;
    private RecyclerView sizeChartRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Seller Information");
        setContentView(R.layout.activity_company_details);

        companyName=findViewById(R.id.companyName);
        companyEmail=findViewById(R.id.companyEmail);
        companyPhoneNumber=findViewById(R.id.companyNumber);
        companyImage=findViewById(R.id.companyImage);
        companyGstId=findViewById(R.id.companyGstId);
        sellerId=findViewById(R.id.sellerId);
        userName=findViewById(R.id.userName);
        pStorageRef = FirebaseStorage.getInstance().getReference();
        sizeChartName=findViewById(R.id.sizeChartName);
        upload=findViewById(R.id.size_chart_upload);
        rootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();

        houseNo=findViewById(R.id.HouseNo);
        city=findViewById(R.id.City);
        district=findViewById(R.id.District);
        state=findViewById(R.id.State);
        pin=findViewById(R.id.Pin);


        sizeChartRecyclerView=findViewById(R.id.sizeChartChoosingRecyclerView);
        sizeChartRecyclerView.hasFixedSize();
        sizeChartRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));



        FirebaseInstallations.getInstance().getId().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    installationId=task.getResult();
                } else {

                    Log.i("Failed to get id","Failed");
                }
            }
        });

        sizeChart = findViewById(R.id.sizeChart);
        sizeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery, "Select Image1"), 1);
            }
        });


        companyPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){

                    if (companyPhoneNumber.getText().length()!=10){
                        companyPhoneNumber.setError("Phone number should be 10 digit");

                    }
                    else {
                        companyPhoneNumber.setError(null);
                    }

                }
            }
        });

        companyEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                if (!hasFocus){

                    if (!companyEmail.getText().toString().matches(emailPattern)){
                        companyEmail.setError("Email not valid");
                    }else {
                        companyEmail.setError(null);
                    }

                }




            }
        });

    }
    public void imageSelect(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

       // Intent gallery1 = new Intent(Intent.ACTION_GET_CONTENT);
        //gallery1.setType("image/*");
       // startActivityForResult(Intent.createChooser(gallery1, "Sellect Back Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
               // Uri resultUri = result.getUri();
               userImageUri = result.getUri();
               companyImage.setImageURI(userImageUri);
               //for bitmap we have to user setImageBitmap(), for uri we have to use setImageUri(();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

      /*  if (resultCode == RESULT_OK && requestCode == 1) {
            assert data != null;
            userImageUri = data.getData()
            ;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), userImageUri);
                companyImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        if (resultCode == RESULT_OK && requestCode == 1) {
            assert data != null;
            sizeChartUri = data.getData()
            ;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sizeChartUri);
                sizeChart.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void addImage(View view) {


      if(isConnected(getApplicationContext()))
      {

          showProgressDialog();

          //testing null or empty edit Text
          if (companyName.getText().toString().equals("")){
              companyName.setError("Give company name");
          }else {
              companyName.setError(null);
          }
          if (userName.getText().toString().equals("")){
              userName.setError("Give user name");
          }else {
              userName.setError(null);
          }
          if (!companyEmail.getText().toString().matches(emailPattern)){
              companyEmail.setError("Email not valid");
          }else {
              companyEmail.setError(null);
          }
          if (companyPhoneNumber.getText().toString().equals("")){
              companyPhoneNumber.setError("Give the phone number");
          }
          else {
              companyPhoneNumber.setError(null);
          }
          if (sellerId.getText().toString().equals("")){
              sellerId.setError("Give seller id");
          }else {
              sellerId.setError(null);
          }
          if (houseNo.getText().toString().equals("")){
              houseNo.setError("Give House No");
          }else {
              houseNo.setError(null);
          }
          if (city.getText().toString().equals("")){
              city.setError("Give city");
          }else {
              city.setError(null);
          }
          if (district.getText().toString().equals("")){
              district.setError("Give district");
          }else {
              district.setError(null);
          }
          if (state.getText().toString().equals("")){
              state.setError("Give state");
          }else {
              state.setError(null);
          }
          if (pin.getText().toString().equals("")){
              pin.setError("Give pin");
          }else {
              pin.setError(null);
          }






          String deviceToken= FirebaseInstanceId.getInstance().getToken();

          final Map companyDetails=new HashMap();
          companyDetails.put("company_name",companyName.getText().toString());
          companyDetails.put("user_name",userName.getText().toString());
          companyDetails.put("company_email",companyEmail.getText().toString());
          companyDetails.put("company_phone_number",companyPhoneNumber.getText().toString());
          companyDetails.put("GST_id",companyGstId.getText().toString());
          companyDetails.put("company_id",mCurrentUser.getUid());
          companyDetails.put("seller_id",sellerId.getText().toString());
          companyDetails.put("device_token",deviceToken);
          companyDetails.put("installation_id",installationId);


          final Map address=new HashMap();
          address.put("house_no",houseNo.getText().toString());
          address.put("city",city.getText().toString());
          address.put("district",district.getText().toString());
          address.put("state",state.getText().toString());
          address.put("pin",pin.getText().toString());

          if (!companyName.getText().toString().equals("") & !userName.getText().toString().equals("") &
                  !companyEmail.getText().toString().equals("")& !companyPhoneNumber.getText().toString().equals("")&!sellerId.getText().toString().equals("")
                  & !houseNo.getText().toString().equals("") & !city.getText().toString().equals("") & !district.getText().toString().equals("") & !state.getText().toString().equals("")
                  & !pin.getText().toString().equals("") &companyPhoneNumber.getText().length()==10)
          {

              //  Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
              if (userImageUri!=null && (CheckImageSize(sizeChartUri) / 1024)<160){

                  final StorageReference riversRef = pStorageRef.child("Seller_image").child(mCurrentUser.getUid()).child("user_image.jpg");

                  riversRef.putFile(userImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                      @Override
                      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                          riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                              @Override
                              public void onSuccess(Uri uri) {
                                  final String downloadUrl = uri.toString();

                                  companyDetails.put("user_image",downloadUrl);

                                  rootRef.child("company_details").child(mCurrentUser.getUid()).updateChildren(companyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {

                                          if (task.isSuccessful()){
                                              rootRef.child("company_details").child(mCurrentUser.getUid()+"/address/").updateChildren(address, new DatabaseReference.CompletionListener() {
                                                  @Override
                                                  public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                                      if (databaseError==null){

                                                          Toast.makeText(CompanyDetailsActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                                          dismissProgressDialog();
                                                          Intent intent=new Intent(getApplicationContext(),Inventory_Activity.class);
                                                          startActivity(intent);

                                                      }else {
                                                          Toast.makeText(CompanyDetailsActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                                      }

                                                  }
                                              });
                                          }


                                          else {
                                              dismissProgressDialog();
                                              Toast.makeText(CompanyDetailsActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                          }

                                      }
                                  }).addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          dismissProgressDialog();
                                          Toast.makeText(CompanyDetailsActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                      }
                                  });




                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  Toast.makeText(CompanyDetailsActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();

                              }
                          });

                          // Toast.makeText(CompanyDetailsActivity.this, "done", Toast.LENGTH_SHORT).show();

                      }
                  });
              }else {

                  companyDetails.put("user_image","default");

                  rootRef.child("company_details").child(mCurrentUser.getUid()).updateChildren(companyDetails, new DatabaseReference.CompletionListener() {
                      @Override
                      public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                          if (databaseError==null){


                              rootRef.child("company_details").child(mCurrentUser.getUid()+"/address/").updateChildren(address, new DatabaseReference.CompletionListener() {
                                  @Override
                                  public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                      if (databaseError==null){

                                          Toast.makeText(CompanyDetailsActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                          dismissProgressDialog();
                                          Intent intent=new Intent(getApplicationContext(),Inventory_Activity.class);
                                          startActivity(intent);

                                      }else {
                                          dismissProgressDialog();
                                          Toast.makeText(CompanyDetailsActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                      }

                                  }
                              });

                          }

                      }
                  });


              }



          }
          else {
              Toast.makeText(this, "Some Fields Are Empty", Toast.LENGTH_SHORT).show();
              dismissProgressDialog();
          }
      }

      else {
          dismissProgressDialog();
          Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
      }

    }


    public void showProgressDialog() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(CompanyDetailsActivity.this);
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


   //size chart
    public void uploadImage(View view) {


        if (isConnected(CompanyDetailsActivity.this)){



            if (sizeChartUri!=null & !sizeChartName.getText().toString().equals("")) {
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

                                rootRef.child("company_details").child(mCurrentUser.getUid()).child("size_charts").child(sizeChartName.getText().toString()).updateChildren(sizeChartDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dismissProgressDialog();
                                            Toast.makeText(CompanyDetailsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                            sizeChartName.setText("");
                                            sizeChart.setImageResource(R.drawable.ic_baseline_account_box_24);
                                            dismissProgressDialog();

                                        } else {
                                            dismissProgressDialog();
                                            Toast.makeText(CompanyDetailsActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });

                            }
                        });
                    }
                });
            }else {
                Toast.makeText(this, "You have to select name and image both", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }


        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference sizeChart = rootRef.child("company_details").child(mCurrentUser.getUid()).child("size_charts");

        FirebaseRecyclerOptions<SizeChart> options=new FirebaseRecyclerOptions.Builder<SizeChart>()
                .setQuery(sizeChart,SizeChart.class)
                .build();

        FirebaseRecyclerAdapter<SizeChart, CompanyDetailsActivity.SizeViewHolder> sizeChartHolder =new FirebaseRecyclerAdapter<SizeChart, CompanyDetailsActivity.SizeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CompanyDetailsActivity.SizeViewHolder sizeViewHolder, int i, @NonNull SizeChart sizeChart) {

                sizeViewHolder.sizeChartName.setText(sizeChart.getSize_chart_name());

            }

            @NonNull
            @Override
            public CompanyDetailsActivity.SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.size_choose_item_design,parent,false);
                CompanyDetailsActivity.SizeViewHolder sizeViewHolder=new CompanyDetailsActivity.SizeViewHolder(view);

                return sizeViewHolder;
            }
        };sizeChartRecyclerView.setAdapter(sizeChartHolder);
        sizeChartHolder.startListening();
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
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
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













