package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Jewellery extends AppCompatActivity {

    private EditText colorName;
    private EditText sizeDescription;
    private EditText baseMaterial;
    private EditText jewelleryPlating;
    private EditText warrantyDescription;
    private EditText packageIncludes;
    private EditText jewelleryWeight;
    private EditText productDescription1, productDescription2, productDescription3;
    private EditText product_price_max;
    private EditText product_price_min;
    private EditText numberOf_Pieces;
    private Button submit2;
    private TextView idTextView;
    private DatabaseReference rootReference;
    private StorageReference pStorageRef;
    ArrayList<String> downloadUrls;
    int i;
    int j = 1;
    ArrayList<Uri> images;
    ArrayList<Uri> imageViews ;
    ArrayList<ImageView> threeImages;
    private String parentItem, childItem, intentItemId;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    protected ProgressDialog progressDialog;
    private TextView imageTex1, imageTex2, imageTex3;
    Uri imageUri1, imageUri2, imageUri3, imageUri4, imageUri5, imageUri6;

    String validColor="available";
    Boolean check=false;
    Boolean stateCheck=true;

    Double volumerticWeight,sellerPrice,customerPrice,finalWeight,finalSellerPrice;
    Double packageWidth,packageDepth,packageHeight,packageWeight;
    Double fildPrice;

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (currentNetworkInfo.isConnected()) {
                if (check) {
                    stateCheck=true;
                    Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content), "Connected  ", Snackbar.LENGTH_LONG);
                    View snakBarView=snackbar.getView();
                    snakBarView.setBackgroundColor(Color.parseColor("#4ebaaa"));
                    snackbar.show();
                }
            } else {
                check = true;
                stateCheck=false;
                Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content), "Not Connected  ", Snackbar.LENGTH_INDEFINITE);
                View snakBarView=snackbar.getView();
                snakBarView.setBackgroundColor(Color.parseColor("#ef5350"));
                snackbar.show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jewellery);

        getApplicationContext().registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));//CHECKING CONNECTIVITY

        images = new ArrayList<>();
        colorName = findViewById(R.id.colorName);
        sizeDescription = findViewById(R.id.sizeDescription);
        baseMaterial = findViewById(R.id.baseMaterial);
        jewelleryWeight = findViewById(R.id.productWeight);
        packageIncludes = findViewById(R.id.packageIncludes);
        submit2 = findViewById(R.id.submitButton2);
        product_price_max = findViewById(R.id.productPriceMax);
        product_price_min = findViewById(R.id.productPriceMin);
        numberOf_Pieces = findViewById(R.id.numberOfPieces);
        idTextView = findViewById(R.id.copyId);
        jewelleryPlating = findViewById(R.id.productPlating);
        warrantyDescription = findViewById(R.id.productWarranty);
        productDescription1 = findViewById(R.id.includesJewelleryDescription1);
        productDescription2 = findViewById(R.id.includesJewelleryDescription2);
        productDescription3 = findViewById(R.id.includesJewelleryDescription3);


        imageTex1 = findViewById(R.id.text1);
        imageTex2 = findViewById(R.id.text2);
        imageTex3 = findViewById(R.id.textView5);

        threeImages = new ArrayList<>();
        imageViews = new ArrayList<>();
        downloadUrls=new ArrayList<>();


        rootReference = FirebaseDatabase.getInstance().getReference();
        pStorageRef = FirebaseStorage.getInstance().getReference();

        intentItemId = getIntent().getStringExtra("itemId");
        idTextView.setText("Product Id: " + intentItemId);

        parentItem = getIntent().getStringExtra("parentCategory");
        childItem = getIntent().getStringExtra("childCategory");



        product_price_min.setEnabled(false);

        rootReference.child("products/"+intentItemId+"/package_details/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    PackageDetails packageDetails=dataSnapshot.getValue(PackageDetails.class);
                    assert packageDetails != null;
                    packageDepth= Double.parseDouble(packageDetails.getPackage_depth());
                    packageHeight= Double.parseDouble(packageDetails.getPackage_height());
                    packageWidth= Double.parseDouble(packageDetails.getPackage_width());
                    packageWeight= Double.parseDouble(packageDetails.getWeight());
                    volumerticWeight=(packageDepth*packageHeight*packageWidth)/4000;

                }else {
                    Toast.makeText(Jewellery.this, "some error occurred1", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Jewellery.this, "some error occurred", Toast.LENGTH_SHORT).show();
            }
        });


// check the first upload is success or not

        rootReference.child("products").child(intentItemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("base_material")) {

                    Products products = dataSnapshot.getValue(Products.class);
                    baseMaterial.setText(products.getBase_material());
                    jewelleryPlating.setText(products.getJewellery_plating());
                    warrantyDescription.setText(products.getWarranty_description());
                    packageIncludes.setText(products.getPackage_includes());

                    baseMaterial.setEnabled(false);
                    jewelleryPlating.setEnabled(false);
                    warrantyDescription.setEnabled(false);
                    packageIncludes.setEnabled(false);

                } else {


                    Toast.makeText(Jewellery.this, "no match", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // select image.....................................
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
                Intent gallery1 = new Intent(Intent.ACTION_GET_CONTENT);
                gallery1.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery1, "Select Image2"), 2);
            }
        });
        //Select Image3
        imageView3 = findViewById(R.id.Image3);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery2 = new Intent(Intent.ACTION_GET_CONTENT);
                gallery2.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery2, "Select Image3"), 3);
            }
        });

        //Select Image4
        imageView4 = findViewById(R.id.Image4);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery3 = new Intent(Intent.ACTION_GET_CONTENT);
                gallery3.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery3, "Select Image4"), 4);
            }
        });

        //Select Image5
        imageView5 = findViewById(R.id.Image5);
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery4 = new Intent(Intent.ACTION_GET_CONTENT);
                gallery4.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery4, "Select Image5"), 5);
            }
        });

        //Select Image6
        imageView6 = findViewById(R.id.Image6);
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery5 = new Intent(Intent.ACTION_GET_CONTENT);
                gallery5.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery5, "Select Image5"), 6);
            }
        });



        colorName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    rootReference.child("products").child(intentItemId).child("color_details").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(colorName.getText().toString())){
                                colorName.setError("This color Already available");
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


        product_price_max.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!product_price_max.getText().toString().equals("")){
                    product_price_min.setText(String.valueOf(generateCustomerPrice()));

                }
            }
        });


        submit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stateCheck){ Datasubmit();}

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


       // showProgressDialog();
        j = 0;


        Long sizeImage1 = CheckImageSize(imageUri1) / 1024;

        images.removeAll(images);
        imageViews.removeAll(imageViews);
        downloadUrls.removeAll(downloadUrls);

        if (imageUri1 != null & sizeImage1 > 500) {
            imageViews.add(imageUri1);
        }
        if (imageUri2 != null & (CheckImageSize(imageUri2) / 1024) > 500) {
            imageViews.add(imageUri2);
        }
        if (imageUri3 != null  & (CheckImageSize(imageUri3) / 1024) > 500) {
            imageViews.add(imageUri3);
        }


        if (imageUri1 != null) {
            if (sizeImage1 < 500) {
                images.add(imageUri1);
                imageTex1.setText("image 1");
            } else {
                imageTex1.setText("Size <150Kb");
                imageTex1.setTextColor(Color.RED);

            }
        }

        if (imageUri2 != null) {
            if ((CheckImageSize(imageUri2) / 1024) < 500) {
                images.add(imageUri2);
                imageTex2.setText("image 2");

            } else {
                imageTex2.setText("Size <150Kb");
                imageTex2.setTextColor(Color.RED);

            }
        }

        if (imageUri3 != null) {

            if ((CheckImageSize(imageUri3) / 1024) < 500) {
                images.add(imageUri3);
                imageTex3.setText("image 3");
            } else {
                imageTex3.setText("Size <150Kb");
                imageTex3.setTextColor(Color.RED);

            }
        }

        if (colorName.getText().toString().equals("") | sizeDescription.getText().toString().equals("")
                | jewelleryWeight.getText().toString().equals("") | baseMaterial.getText().toString().equals("") |
                product_price_max.getText().toString().equals("") | numberOf_Pieces.getText().toString().equals("") | product_price_min.getText().toString().equals("") | images.size() == 0) {
            Toast.makeText(this, "Some Fields Are Empty", Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
        }


        else {

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
                            .setNegativeButton("NO", null)
                            .setCancelable(false)
                            .show();
                }
                else {

                    showProgressDialog();
                    imageViews.removeAll(imageViews);
                    //  Toast.makeText(Jewellery.this, "else = images"+String.valueOf(images.size())+" imageViews"+String.valueOf(imageViews.size()), Toast.LENGTH_SHORT).show();
                    startUpload();


                }


            }else {
                Toast.makeText(this, "Color name Already exists!", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void startUpload() {


        final String weightPathString = "products/" + intentItemId + "/color_details/" + colorName.getText().toString() + "/size/";
        final String colorPathString = "products/" + intentItemId + "/color_details/" + colorName.getText().toString();

        final Map colorMap = new HashMap();
        colorMap.put("color_name", colorName.getText().toString());
        colorMap.put("color_code", "#020729");


        final String colorname = colorName.getText().toString();
        final String sizedescription = sizeDescription.getText().toString();
        String basematerial = baseMaterial.getText().toString();
        final String jewelleryweight = jewelleryWeight.getText().toString();
        String packageincludes = packageIncludes.getText().toString();
        String productPriceMax = product_price_max.getText().toString();
        String productPriceMin = product_price_min.getText().toString();
        String numberOfPieces = numberOf_Pieces.getText().toString();
        String jewelleryplating = jewelleryPlating.getText().toString();
        String warrantydescription = warrantyDescription.getText().toString();

        Map otherDetailsProductHashMap = new HashMap();
        otherDetailsProductHashMap.put("size_description", sizedescription);
        otherDetailsProductHashMap.put("base_material", basematerial);
        otherDetailsProductHashMap.put("package_includes", packageincludes);
        otherDetailsProductHashMap.put("jewellery_plating", jewelleryplating);
        otherDetailsProductHashMap.put("warranty_description", warrantydescription);
        otherDetailsProductHashMap.put("upload_status", "false");

        final Map weightdetailsMap = new HashMap();
        weightdetailsMap.put("jewellery_weight", jewelleryweight);
        weightdetailsMap.put("max_price", Double.parseDouble(productPriceMax.trim()));
        weightdetailsMap.put("min_price", Double.parseDouble(productPriceMin.trim()));
        weightdetailsMap.put("pieces", Double.parseDouble(numberOfPieces.trim()));
        weightdetailsMap.put("size", sizedescription);


        if ((imageUri4 != null & !productDescription1.getText().toString().equals(""))
                || (imageUri5 != null & !productDescription2.getText().toString().equals("")) ||  (imageUri6 != null & !productDescription3.getText().toString().equals(""))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    colorAnotherImageDetails(colorname);
                }
            }, 4000);
        }else {
            Toast.makeText(this, "Select Image And Description Both", Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
        }


        rootReference.child("products").child(intentItemId).updateChildren(otherDetailsProductHashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {

                    rootReference.child(colorPathString).setValue(colorMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                rootReference.child(weightPathString + sizeDescription.getText().toString()).updateChildren(weightdetailsMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        Toast.makeText(Jewellery.this, "Done", Toast.LENGTH_SHORT).show();

                                        product_price_max.setText("");
                                        product_price_min.setText("");
                                        numberOf_Pieces.setText("");
                                        colorName.setText("");
                                        jewelleryWeight.setText("");
                                        sizeDescription.setText("");
                                        baseMaterial.setEnabled(false);
                                        packageIncludes.setEnabled(false);
                                        warrantyDescription.setEnabled(false);
                                        jewelleryPlating.setEnabled(false);
                                        uploadImage(colorname);

                                    }
                                });


                            }
                        }
                    });

                } else {

                    dismissProgressDialog();
                    Toast.makeText(Jewellery.this, "There is Some Error!", Toast.LENGTH_SHORT).show();


                }
            }
        });






    }

    public void uploadImage(final String colorname) {

        final ArrayList<ImageView> imageViews = new ArrayList<>();
        imageViews.add(imageView1);
        imageViews.add(imageView2);
        imageViews.add(imageView3);

        for (i = 0; i < images.size(); i++) {

            if (images.get(i) != null) {

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

    private void colorAnotherImageDetails(final String colorname) {

        if (imageUri4 != null & !productDescription1.getText().toString().equals("")) {
            final String randomkey1 = UUID.randomUUID().toString();
            final StorageReference jewelleryRef = pStorageRef.child("product_images").child(intentItemId).child(colorname).child(randomkey1 + ".jpg");


            jewelleryRef.putFile(imageUri4).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    jewelleryRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl1 = uri.toString();
                            Map jewelleryDetails1 = new HashMap();
                            jewelleryDetails1.put("description_1", productDescription1.getText().toString());
                            jewelleryDetails1.put("imageUri4", downloadUrl1);


                            rootReference.child("products/" + intentItemId + "/color_details/" + colorname + "/images" + "/image4").updateChildren(jewelleryDetails1, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(Jewellery.this, "D1 Done", Toast.LENGTH_SHORT).show();


                                    if (imageUri5 == null | imageUri6 == null) {
                                        statusCheck();
                                        dismissProgressDialog();


                                    }
                                    productDescription1.setText("");
                                    imageView4.setImageResource(R.drawable.ic_baseline_account_box_24);
                                }
                            });


                        }
                    });
                }
            });


        } else {
            //Toast.makeText(this, "Select Image And Description Both", Toast.LENGTH_SHORT).show();
            //
            dismissProgressDialog();
        }

        if (imageUri5 != null & !productDescription2.getText().toString().equals("")) {
            final String randomkey2 = UUID.randomUUID().toString();
            final StorageReference jewelleryRef1 = pStorageRef.child("product_images").child(intentItemId).child(colorname).child(randomkey2 + ".jpg");


            jewelleryRef1.putFile(imageUri5).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    jewelleryRef1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl2 = uri.toString();
                            Map jewelleryDetails2 = new HashMap();
                            jewelleryDetails2.put("description_2", productDescription2.getText().toString());
                            jewelleryDetails2.put("imageUri5", downloadUrl2);


                            rootReference.child("products/" + intentItemId + "/color_details/" + colorname + "/images" + "/image5").updateChildren(jewelleryDetails2, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(Jewellery.this, "D2 Done", Toast.LENGTH_SHORT).show();


                                    if (imageUri6 == null) {
                                        statusCheck();
                                        dismissProgressDialog();
                                    }
                                    productDescription2.setText("");
                                    imageView5.setImageResource(R.drawable.ic_baseline_account_box_24);
                                }
                            });


                        }
                    });
                }
            });

        } else {
           // Toast.makeText(this, "Select Image And Description Both", Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
        }


        if (imageUri6 != null & !productDescription3.getText().toString().equals("")) {
            final String randomkey3 = UUID.randomUUID().toString();
            final StorageReference jewelleryRef2 = pStorageRef.child("product_images").child(intentItemId).child(colorname).child(randomkey3 + ".jpg");

            jewelleryRef2.putFile(imageUri6).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    jewelleryRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl3 = uri.toString();
                            Map jewelleryDetails3 = new HashMap();
                            jewelleryDetails3.put("description_3", productDescription3.getText().toString());
                            jewelleryDetails3.put("imageUri6", downloadUrl3);


                            rootReference.child("products/" + intentItemId + "/color_details/" + colorname + "/images" + "/image6").updateChildren(jewelleryDetails3, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Toast.makeText(Jewellery.this, "D3 Done", Toast.LENGTH_SHORT).show();
                                    statusCheck();
                                    dismissProgressDialog();
                                    productDescription3.setText("");
                                    imageView6.setImageResource(R.drawable.ic_baseline_account_box_24);
                                }
                            });


                        }
                    });
                }
            });
        } else {
           // Toast.makeText(this, "Select Image And Description Both", Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
        }


    }

    private void saveImageUrl(String downloadUrl, final String colorname) {


        Map hashMap = new HashMap<>();
        String imageName = "image" + j;
        j++;
        hashMap.put(imageName, downloadUrl);

        rootReference.child("products/" + intentItemId + "/color_details/" + colorname + "/images").updateChildren(hashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (databaseError == null) {

                    statusCheck();
                    Toast.makeText(Jewellery.this, "Done", Toast.LENGTH_SHORT).show();
                    imageView1.setImageResource(R.drawable.ic_baseline_account_box_24);
                    imageView2.setImageResource(R.drawable.ic_baseline_account_box_24);
                    imageView3.setImageResource(R.drawable.ic_baseline_account_box_24);


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
                                          //  dismissProgressDialog();
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

                            Toast.makeText(Jewellery.this, "some error occurred", Toast.LENGTH_SHORT).show();
                            //dismissProgressDialog();
                        }
                    });
                    //  dismissProgressDialog();

                }
                // threeImages.get(j-1).setImageResource(R.drawable.ic_baseline_account_box_24);


            }

        });


    }


    public void showProgressDialog() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(Jewellery.this);
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

    public void statusCheck() {
        Map statusMap = new HashMap();
        statusMap.put("upload_status", "ok");


        rootReference.child("products").child(intentItemId).updateChildren(statusMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (databaseError == null) {
                    Log.i("done", "done");
                } else {
                    Toast.makeText(Jewellery.this, "Error occurred for updateing status.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    //for back press evevt
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            rootReference.child("products").child(intentItemId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("base_material")) {
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
                                Toast.makeText(Jewellery.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
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


    public Double generateCustomerPrice(){

        fildPrice= Double.parseDouble(product_price_max.getText().toString());


        sellerPrice=fildPrice+((fildPrice*6)/100);
        finalSellerPrice=sellerPrice;

        if (packageWeight>volumerticWeight)
            finalWeight=packageWeight;
        else
            finalWeight=volumerticWeight;



        if (finalWeight<0.5){

            if (sellerPrice<500){
                finalSellerPrice+=80;
            }
            else if (sellerPrice>=500 && sellerPrice<1000){
                finalSellerPrice+=100;
            }
            else
                finalSellerPrice+=120;

        }

        else if (finalWeight>=0.5 && finalWeight <1){

            if (sellerPrice<500){
                finalSellerPrice+=100;
            }
            else if (sellerPrice>=500 && sellerPrice<1000){
                finalSellerPrice+=130;
            }
            else
                finalSellerPrice+=200;

        }
        else if (finalWeight>=1 && finalWeight <1.5){

            if (sellerPrice<1000){
                finalSellerPrice+=150;
            }
            else if (sellerPrice>=1000 && sellerPrice<2000){
                finalSellerPrice+=200;
            }
            else
                finalSellerPrice+=300;

        }

        if (parentItem.equals("Home Accessories")){
            customerPrice=finalSellerPrice+(finalSellerPrice*18/100);
        }
        if (parentItem.equals("Furniture")){
            customerPrice=finalSellerPrice+(finalSellerPrice*12/100);
        }

        return Math.ceil(customerPrice);

    }



}
