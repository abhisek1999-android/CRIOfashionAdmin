package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class others_details extends AppCompatActivity {
    private EditText color_name;
    private EditText size_description;
    private EditText base_material;
    private EditText product_size;
    private EditText package_includes;
    private EditText product_price_max;
    private EditText product_price_min;
    private EditText numberOf_Pieces;
    private Button submit1;
    private TextView idTextView;
    private DatabaseReference rootReference;
    private EditText product_warranty;
   //ArrayList<Uri> images;
    private StorageReference pStorageRef;
    String validColor="available";
    ArrayList<Uri> imageViews ;
    ArrayList<Uri> images;
    ArrayList<String> downloadUrls;
    private TextView imageTex1, imageTex2, imageTex3, imageTex4, imageTex5, imageTex6;


    int i;
    int j = 0;
    private String parentItem, childItem, intentItemId;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    protected ProgressDialog progressDialog;
    Uri imageUri1, imageUri2, imageUri3, imageUri4, imageUri5, imageUri6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_details);
        color_name = findViewById(R.id.colorName);
        size_description = findViewById(R.id.sizeDescription);
        base_material = findViewById(R.id.productFilling);
        product_size = findViewById(R.id.productSize);
        package_includes = findViewById(R.id.packageIncludes);
        submit1 = findViewById(R.id.submitButton1);
        product_price_max = findViewById(R.id.productPriceMax);
        product_price_min = findViewById(R.id.productPriceMin);
        numberOf_Pieces = findViewById(R.id.numberOfPieces);
        idTextView = findViewById(R.id.copyId);
        product_warranty = findViewById(R.id.productWarranty);
        rootReference = FirebaseDatabase.getInstance().getReference();
        pStorageRef = FirebaseStorage.getInstance().getReference();
        intentItemId = getIntent().getStringExtra("itemId");

        imageTex1 = findViewById(R.id.text1);
        imageTex2 = findViewById(R.id.text2);
        imageTex3 = findViewById(R.id.textView5);
        imageTex4 = findViewById(R.id.textView6);
        imageTex5 = findViewById(R.id.textView7);
        imageTex6 = findViewById(R.id.textView8);

        images = new ArrayList<>();
        imageViews = new ArrayList<>();
        downloadUrls=new ArrayList<>();

        idTextView.setText("Product Id: " + intentItemId);

        parentItem = getIntent().getStringExtra("parentCategory");
        childItem = getIntent().getStringExtra("childCategory");


        // check the first upload is success or not

        rootReference.child("products").child(intentItemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("base_material")) {

                    Products products = dataSnapshot.getValue(Products.class);
                    base_material.setText(products.getBase_material());

                    product_warranty.setText(products.getWarranty_description());
                    package_includes.setText(products.getPackage_includes());

                    base_material.setEnabled(false);
                    package_includes.setEnabled(false);
                    product_warranty.setEnabled(false);

                } else {


                    Toast.makeText(others_details.this, "no match", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


// select image.....................................

        //select image1 from gallery
        imageView1 = findViewById(R.id.Image1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery, "Select Image1"), 1);
            }
        });
        //Select the Image2
        imageView2 = findViewById(R.id.Image2);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null & ((CheckImageSize(imageUri1) / 1024) < 160)) {
                    Intent gallery1 = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery1.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery1, "Select Image2"), 2);
                } else {
                    Toast.makeText(others_details.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Select Image3
        imageView3 = findViewById(R.id.Image3);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null & imageUri2 != null & ((CheckImageSize(imageUri1) / 1024) < 160) & ((CheckImageSize(imageUri2) / 1024) < 160)) {
                    Intent gallery2 = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery2.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery2, "Select Image3"), 3);

                } else {
                    Toast.makeText(others_details.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Select Image4
        imageView4 = findViewById(R.id.Image4);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null & imageUri2 != null & imageUri3 != null & ((CheckImageSize(imageUri1) / 1024) < 160)
                        & ((CheckImageSize(imageUri2) / 1024) < 160) & ((CheckImageSize(imageUri3) / 1024) < 160)) {
                    Intent gallery3 = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery3.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery3, "Select Image3"), 4);
                } else {
                    Toast.makeText(others_details.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }

            }

        });

        //Select Image5
        imageView5 = findViewById(R.id.Image5);
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null & imageUri2 != null & imageUri3 != null & imageUri4 != null
                        & ((CheckImageSize(imageUri1) / 1024) < 160) & ((CheckImageSize(imageUri2) / 1024) < 160)
                        & ((CheckImageSize(imageUri3) / 1024) < 160) & ((CheckImageSize(imageUri4) / 1024) < 160)) {
                    Intent gallery4 = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery4.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery4, "Select Image3"), 5);
                } else {
                    Toast.makeText(others_details.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Select Image6
        imageView6 = findViewById(R.id.Image6);
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null & imageUri2 != null & imageUri3 != null & imageUri4 != null & imageUri5 != null

                        & ((CheckImageSize(imageUri1) / 1024) < 160) & ((CheckImageSize(imageUri2) / 1024) < 160)
                        & ((CheckImageSize(imageUri3) / 1024) < 160) & ((CheckImageSize(imageUri4) / 1024) < 160) & ((CheckImageSize(imageUri5) / 1024) < 160)) {
                    Intent gallery5 = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery5.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery5, "Select Image3"), 6);
                } else {
                    Toast.makeText(others_details.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }

            }

        });





              color_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    rootReference.child("products").child(intentItemId).child("color_details").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(color_name.getText().toString())){
                                color_name.setError("This color Already available");
                                validColor="not_available";
                            }
                            else {
                                // Toast.makeText(MainActivity.this, productId.getEditText().getText().toString()+seller_id, Toast.LENGTH_SHORT).show();
                                validColor="available";
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });






        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (isConnected(others_details.this)){ Datasubmit();}



            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            assert data != null;
            imageUri1 = data.getData()
            ;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri1);
                imageView1.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (resultCode == RESULT_OK && requestCode == 2) {
            imageUri2 = data.getData();
            try {
                Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri2);
                imageView2.setImageBitmap(bitmap1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resultCode == RESULT_OK && requestCode == 3) {
            imageUri3 = data.getData();
            try {
                Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri3);
                imageView3.setImageBitmap(bitmap2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (resultCode == RESULT_OK && requestCode == 4) {
            imageUri4 = data.getData();
            try {
                Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri4);
                imageView4.setImageBitmap(bitmap2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (resultCode == RESULT_OK && requestCode == 5) {
            imageUri5 = data.getData();
            try {
                Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri5);
                imageView5.setImageBitmap(bitmap2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resultCode == RESULT_OK && requestCode == 6) {
            imageUri6 = data.getData();
            try {
                Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri6);
                imageView6.setImageBitmap(bitmap2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void Datasubmit() {



        Long sizeImage1 = CheckImageSize(imageUri1) / 1024;
        images.removeAll(images);
        imageViews.removeAll(imageViews);
        downloadUrls.removeAll(downloadUrls);

        if (imageUri1 != null & sizeImage1 > 160) {
                imageViews.add(imageUri1);
        }
        if (imageUri2 != null & (CheckImageSize(imageUri2) / 1024) > 160) {
            imageViews.add(imageUri2);
        }
        if (imageUri3 != null  & (CheckImageSize(imageUri3) / 1024) > 160) {
            imageViews.add(imageUri3);
        }
        if (imageUri4 != null  & (CheckImageSize(imageUri4) / 1024) > 160) {
            imageViews.add(imageUri4);
        }
        if (imageUri5 != null  & (CheckImageSize(imageUri5) / 1024) > 160) {
            imageViews.add(imageUri5);
        }
        if (imageUri6 != null  & (CheckImageSize(imageUri6) / 1024) > 160) {
            imageViews.add(imageUri6);
        }





        if (imageUri1 != null) {
            if (sizeImage1 < 160) {
                images.add(imageUri1);
                imageTex1.setText("image 1");
            } else {
                imageTex1.setText("Size <150Kb");
                imageTex1.setTextColor(Color.RED);

            }
        }

        if (imageUri2 != null) {
            if ((CheckImageSize(imageUri2) / 1024) < 160) {
                images.add(imageUri2);
                imageTex2.setText("image 2");

            } else {
                imageTex2.setText("Size <150Kb");
                imageTex2.setTextColor(Color.RED);

            }
        }

        if (imageUri3 != null) {

            if ((CheckImageSize(imageUri3) / 1024) < 160) {
                images.add(imageUri3);
                imageTex3.setText("image 3");
            } else {
                imageTex3.setText("Size <150Kb");
                imageTex3.setTextColor(Color.RED);

            }
        }

        if (imageUri4 != null) {
            if ((CheckImageSize(imageUri4) / 1024) < 160) {
                images.add(imageUri4);
                imageTex4.setText("image 4");
            } else {
                imageTex4.setText("Size <150Kb");
                imageTex4.setTextColor(Color.RED);

            }
        }

        if (imageUri5 != null) {
            if ((CheckImageSize(imageUri5) / 1024) < 160) {
                images.add(imageUri5);
                imageTex5.setText("image 5");
            } else {
                imageTex5.setText("Size <150Kb");
                imageTex5.setTextColor(Color.RED);

            }
        }

        if (imageUri6 != null) {
            if ((CheckImageSize(imageUri6) / 1024) < 160) {
                images.add(imageUri6);
                imageTex6.setText("image 6");
            } else {
                imageTex6.setText("Size <150Kb");
                imageTex6.setTextColor(Color.RED);

            }
        }

        if (color_name.getText().toString().equals("") | size_description.getText().toString().equals("")
                | product_size.getText().toString().equals("") | base_material.getText().toString().equals("") |
                product_price_max.getText().toString().equals("") | numberOf_Pieces.getText().toString().equals("")
                | product_price_min.getText().toString().equals("") | images.size() == 0) {

            Toast.makeText(getApplicationContext(), "Some Fields Are Empty", Toast.LENGTH_SHORT).show();
            dismissProgressDialog();

        } else {
            if (validColor.equals("available")){

                if (imageViews.size()>0) {


                    imageViews.removeAll(imageViews);
                    AlertDialog alertbox = new AlertDialog.Builder(this)
                            .setTitle("Are you Sure?")
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setMessage("Some images are not less then 150 kb, this images are not going to be uploaded.\n Are you sure?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {

                                    //Toast.makeText(others_details.this, "images"+String.valueOf(images.size())+" imageViews"+String.valueOf(imageViews.size()), Toast.LENGTH_SHORT).show();
                                    startUpload();
                                    showProgressDialog();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismissProgressDialog();
                                }
                            })
                            .setCancelable(false)
                            .show();


                }
                else {


                    imageViews.removeAll(imageViews);

                    Toast.makeText(others_details.this, "else = images"+String.valueOf(images.size())+" imageViews"+String.valueOf(imageViews.size()), Toast.LENGTH_SHORT).show();
                    startUpload();
                    showProgressDialog();

                }


            }else {
                Toast.makeText(this, "Color name Already exists!", Toast.LENGTH_SHORT).show();
            }



        }


    }

    public void startUpload() {


        final String sizePathString = "products/" + intentItemId + "/color_details/" + color_name.getText().toString() + "/size/";
        final String colorPathString = "products/" + intentItemId + "/color_details/" + color_name.getText().toString();

        final Map colorMap = new HashMap();
        colorMap.put("color_name", color_name.getText().toString());
        colorMap.put("color_code", "#020729");

        final String colorname = color_name.getText().toString();
        String sizedescription = size_description.getText().toString();
        final String baseMaterial = base_material.getText().toString();
        final String procuctSize = product_size.getText().toString();
        final String packageincludes = package_includes.getText().toString();
        String productPriceMax = product_price_max.getText().toString();
        String productPriceMin = product_price_min.getText().toString();
        String numberOfPieces = numberOf_Pieces.getText().toString();
        String productWarranty = product_warranty.getText().toString();


        Map otherDetailsProductHashMap = new HashMap();
        otherDetailsProductHashMap.put("size_description", sizedescription);
        otherDetailsProductHashMap.put("base_material", baseMaterial);
        otherDetailsProductHashMap.put("package_includes", packageincludes);
        otherDetailsProductHashMap.put("warranty_description", productWarranty);

        final Map sizedetailsMap = new HashMap();
        sizedetailsMap.put("size", procuctSize);
        sizedetailsMap.put("max_price", Long.parseLong(productPriceMax.trim()));
        sizedetailsMap.put("min_price", Long.parseLong(productPriceMin.trim()));
        sizedetailsMap.put("pieces", Long.parseLong(numberOfPieces.trim()));

        rootReference.child("products").child(intentItemId).updateChildren(otherDetailsProductHashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {

                    rootReference.child(colorPathString).setValue(colorMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                rootReference.child(sizePathString + procuctSize).updateChildren(sizedetailsMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        Toast.makeText(others_details.this, "Done", Toast.LENGTH_SHORT).show();

                                        base_material.setEnabled(false);
                                        package_includes.setEnabled(false);
                                        product_warranty.setEnabled(false);
                                        uploadImage(colorname);
                                    }
                                });


                            }
                        }
                    });

                }
            }
        });


    }


    private void uploadImage(final String colorname) {


        j = 0;


        for (i = 0; i < images.size(); i++) {

            if (images.get(i) != null) {

                //  CheckImageSize(images.get(i));
                final String randomkey = UUID.randomUUID().toString();
                final StorageReference riversRef = pStorageRef.child("product_images").child(intentItemId).child(colorname).child(randomkey + ".jpg");


                riversRef.putFile(images.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        final String downloadUrl = uri.toString();

                                        downloadUrls.add(downloadUrl);
                                        saveImageUrl(downloadUrl, colorname);


                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                dismissProgressDialog();
                                Toast.makeText(getApplicationContext(), "Not Yet Done", Toast.LENGTH_SHORT).show();
                            }
                        });


            } else {
                i++;
            }

        }


    }

    private void saveImageUrl(String downloadUrl, String colorname) {


        Map hashMap = new HashMap<>();
        String imageName = "image" + j;
        j++;
        hashMap.put(imageName, downloadUrl);

        rootReference.child("products/" + intentItemId + "/color_details/" + colorname + "/images").updateChildren(hashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (databaseError == null) {



                    imageView1.setImageResource(R.drawable.ic_baseline_account_box_24);
                    imageView2.setImageResource(R.drawable.ic_baseline_account_box_24);
                    imageView3.setImageResource(R.drawable.ic_baseline_account_box_24);
                    imageView4.setImageResource(R.drawable.ic_baseline_account_box_24);
                    imageView5.setImageResource(R.drawable.ic_baseline_account_box_24);
                    imageView6.setImageResource(R.drawable.ic_baseline_account_box_24);



                    imageTex1.setText("image1");
                    imageTex2.setText("image2");
                    imageTex3.setText("image3");
                    imageTex4.setText("image4");
                    imageTex5.setText("image5");
                    imageTex6.setText("image6");




                    rootReference.child("products").child(intentItemId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (!dataSnapshot.hasChild("thumb_image")){
                                if (downloadUrls.get(0)!=null){

                                    Map thumbImageMap=new HashMap();
                                    thumbImageMap.put("thumb_image",downloadUrls.get(0));

                                    rootReference.child("products").child(intentItemId).updateChildren(thumbImageMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            statusCheck();
                                            dismissProgressDialog();
                                        }
                                    });
                                }

                            }else {
                                statusCheck();
                               // dismissProgressDialog();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(others_details.this, "some error occurred", Toast.LENGTH_SHORT).show();
                           dismissProgressDialog();
                        }
                    });

                    dismissProgressDialog();
                    Toast.makeText(others_details.this, "done", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    public void statusCheck() {
        Map statusMap = new HashMap();
        statusMap.put("upload_status", "ok");


        rootReference.child("products").child(intentItemId).updateChildren(statusMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (databaseError == null) {
                    Log.i("done", "done");
                } else {
                    Toast.makeText(others_details.this, "Error occurred for updateing status.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            rootReference.child("products").child(intentItemId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("warranty_description")) {
                        Log.i("msg", "msg");
                        finish();
                    } else {
                        exitByBackKey();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {


        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("If you go back then your uploaded item will be removed.Are you sure?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        rootReference.child("products").child(intentItemId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(others_details.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //close();


                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }


    //check the size of an image......................
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

    public void showProgressDialog() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(others_details.this);
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

}