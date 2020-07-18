package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
    protected ProgressDialog progressDialog;

    private CircleImageView companyImage;

  //  CropImageView companyImage;

    private DatabaseReference rootRef;

    private FirebaseAuth mAuth;

    private FirebaseUser mCurrentUser;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    Uri userImageUri;
    StorageReference  pStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);

        companyName=findViewById(R.id.companyName);
        companyEmail=findViewById(R.id.companyEmail);
        companyPhoneNumber=findViewById(R.id.companyNumber);
        companyImage=findViewById(R.id.companyImage);
        companyGstId=findViewById(R.id.companyGstId);
        userName=findViewById(R.id.userName);
        pStorageRef = FirebaseStorage.getInstance().getReference();

        rootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();




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
               companyImage.setImageURI(userImageUri); //for bitmap we have to user setImageBitmap(), for uri we have to use setImageUri(();


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
    }


    public void addImage(View view) {

        showProgressDialog();

        //tesing null or empty edit Text
        if (companyName.getText().toString().equals("")){
            companyName.setError("Non empty Fild");
        }else {
            companyName.setError(null);
        }
        if (userName.getText().toString().equals("")){
            userName.setError("Non empty Fild");
        }else {
            userName.setError(null);
        }
        if (!companyEmail.getText().toString().matches(emailPattern)){
            companyEmail.setError("Email not valid");
        }else {
            companyEmail.setError(null);
        }
        if (companyPhoneNumber.getText().toString().equals("")){
            companyPhoneNumber.setError("Non empty Fild");
        }else {
            companyPhoneNumber.setError(null);
        }




if (!companyName.getText().toString().equals("") |!userName.getText().toString().equals("") |
        !companyEmail.getText().toString().equals("")| !companyPhoneNumber.getText().toString().equals(""))
{
        if (userImageUri!=null){

            final String randomkey = UUID.randomUUID().toString();
        final StorageReference riversRef = pStorageRef.child("Seller_image").child(mCurrentUser.getUid()).child(randomkey+".jpg");

        riversRef.putFile(userImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String downloadUrl = uri.toString();
                        Map companyDetails=new HashMap();
                        companyDetails.put("company_name",companyName.getText().toString());
                        companyDetails.put("user_name",userName.getText().toString());
                        companyDetails.put("company_email",companyEmail.getText().toString());
                        companyDetails.put("company_phone_number",companyPhoneNumber.getText().toString());
                        companyDetails.put("GST_id",companyGstId.getText().toString());
                        companyDetails.put("company_id",mCurrentUser.getUid());
                        companyDetails.put("user_image",downloadUrl);

                        rootRef.child("company_details").child(mCurrentUser.getUid()).setValue(companyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    dismissProgressDialog();
                                Toast.makeText(CompanyDetailsActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getApplicationContext(),Inventory_Activity.class);
                                startActivity(intent);
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
                        dismissProgressDialog();
                    }
                });




                Toast.makeText(CompanyDetailsActivity.this, "done", Toast.LENGTH_SHORT).show();

            }
        });
    }else {
            Toast.makeText(CompanyDetailsActivity.this, "not done", Toast.LENGTH_SHORT).show();
        }
    }


    }


    public void showProgressDialog() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(CompanyDetailsActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }

        progressDialog.setMessage("Uploading Plese wait!!");
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}













