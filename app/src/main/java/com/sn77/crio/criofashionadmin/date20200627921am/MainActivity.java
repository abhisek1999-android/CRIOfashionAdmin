package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText product_name;
    EditText product_description;
    EditText product_price;
    EditText product_company;
    EditText product_overview;
    Spinner  prouduct_type;
    Spinner  prouduct_for;
    Button  submit_data;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    StorageReference pStorageRef;
    private StorageTask uploadTask;
    private DatabaseReference databaseReference;
    private ImageView font_imageView,back_imageView,orginal_imageView;

     Uri font_imageUri,back_imageUri,orginal_imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("products");                //connection to database
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        pStorageRef = FirebaseStorage.getInstance().getReference();                                         //storage referance
        product_name=findViewById(R.id.productName);
        product_price=findViewById(R.id.productPrice);
        product_description=findViewById(R.id.productDescription);
        product_company=findViewById(R.id.companyName);
        product_overview=findViewById(R.id.productOverview);
        prouduct_type=(Spinner)findViewById(R.id.productType);
        prouduct_for=(Spinner)findViewById(R.id.productFor);
         submit_data=(Button)findViewById(R.id.submitButton);
                                                                                                              //sellect font image from gallery
       font_imageView=(ImageView) findViewById(R.id.fontImage);
        font_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery, "Sellect Font Image"), 1);
            }
        });
                                                                                                                   //Sellect the back Image
        back_imageView=(ImageView) findViewById(R.id.backImage);
        back_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery, "Sellect Back Image"), 1);
            }
        });
                                                                                                                    //Sellect Orginal Image
        orginal_imageView=(ImageView) findViewById(R.id.orginalImage);
        orginal_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery, "Sellect Orginal Image"), 1);
            }
        });
                                                                                                               //For submit Image to fairbase
        submit_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask!=null && uploadTask.isInProgress()){
                    Toast.makeText(MainActivity.this, "Upload In Progrress", Toast.LENGTH_SHORT).show();
                }else {
                Imagesubmit();
                }
            }
        });
                                                                                                                  //For Product Type Dropdown
        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(MainActivity.this ,
                R.layout.color_spiner_layout,
                getResources().getStringArray(R.array.Types)
                 );
        myAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        prouduct_type.setAdapter(myAdapter);
                                                                                                             //Product For Whom Dropdown
        ArrayAdapter<String> forAdapter=new ArrayAdapter<String>(MainActivity.this ,
                R.layout.color_spiner_layout,
                getResources().getStringArray(R.array.For)
        );
        forAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        prouduct_for.setAdapter(forAdapter);
    }
                                                                                                                        // For Shwoing Image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            assert data != null;
            font_imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), font_imageUri);
                font_imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (resultCode == RESULT_OK && requestCode == 1) {
            back_imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), back_imageUri);
                back_imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resultCode == RESULT_OK && requestCode == 1) {
            orginal_imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), orginal_imageUri);
                orginal_imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
                                                                                                                                        //image uploding
     private void Imagesubmit(){

       final String randomkey= UUID.randomUUID().toString();
         StorageReference riversRef = pStorageRef.child("images/*" + randomkey);

         riversRef.putFile(font_imageUri)
                 .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                     }
                 })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception exception) {
                         Toast.makeText(MainActivity.this, "Not Yet Done", Toast.LENGTH_SHORT).show();
                     }
                 });

     }


     @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser==null){
            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
         startActivity(intent);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        moveTaskToBack(true);
    }
    public void prodectAdd(View view) {
        String nameProduct=product_name.getText().toString();
        final String priceProduct=product_price.getText().toString();
        String descriptionProduct=product_description.getText().toString();
        String companyProduct=product_company.getText().toString();
        String overviewProduct=product_overview.getText().toString();


        DatabaseReference productPushKey=databaseReference.push();
        String pushKey=productPushKey.getKey();
                                                                               //onk gulo item aksathe database a push korar jonne Hashmap use kora hoi
        HashMap<String,String> productHashMap=new HashMap<>();
        productHashMap.put("name",nameProduct);
        productHashMap.put("price",priceProduct);
        productHashMap.put("description",descriptionProduct);
        productHashMap.put("company",companyProduct);
        productHashMap.put("overview",overviewProduct);

        databaseReference.child(pushKey).setValue(productHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {//set value to database
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "product added Successfully!", Toast.LENGTH_SHORT).show();
                    product_name.setText("");
                    product_price.setText("");
                    product_description.setText("");
                    product_company.setText("");
                    product_overview.setText("");
                }

                else{

                    Toast.makeText(MainActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    }

