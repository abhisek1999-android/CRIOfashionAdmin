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
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.naz013.colorslider.ColorSlider;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//colors must be added
public class Stationery extends AppCompatActivity {
    private EditText color_name;
    private EditText size_description;
    private EditText base_material;
    private EditText product_Weight;
    private EditText package_includes;
    private EditText product_price;
    private EditText customer_price;
    private EditText numberOf_Pieces;
    private EditText brand_name;
    private EditText product_highlights;
    private Button submit1;
    private TextView idTextView;
    private DatabaseReference rootReference;
    private EditText product_warranty;
    //ArrayList<Uri> images;
    private StorageReference pStorageRef;

    private RelativeLayout colorPalat;
    private RelativeLayout colorRelativeLayout;

    String validColor = "available";
    ArrayList<Uri> imageViews;
    ArrayList<Uri> images;
    ArrayList<String> downloadUrls;
    private TextView imageTex1, imageTex2, imageTex3, imageTex4, imageTex5, imageTex6;

    Double volumerticWeight, sellerPrice, customerPrice, finalWeight, finalSellerPrice;
    Double packageWidth, packageDepth, packageHeight, packageWeight;
    Double fildPrice;

    Boolean check = false;
    Boolean stateCheck = true;


    TextView colorHexCode;
    int i;
    String hex;
    int j = 0;
    private String parentItem, childItem, intentItemId;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    protected ProgressDialog progressDialog;
    Uri imageUri1, imageUri2, imageUri3, imageUri4, imageUri5, imageUri6;


    private ColorSlider.OnColorSelectedListener mListener = new ColorSlider.OnColorSelectedListener() {
        @Override
        public void onColorChanged(int position, int color) {
            updateView(color);
        }
    };

    private void updateView(int color) {

        colorRelativeLayout.setBackgroundColor(color);
        hex = "#" + Integer.toHexString(color).substring(2);
        colorHexCode.setText(hex);

    }

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
        setContentView(R.layout.stationery_activity);

        getApplicationContext().registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));//CHECKING CONNECTIVIT

        color_name = findViewById(R.id.colorName);
        size_description = findViewById(R.id.sizeDescription);
        base_material = findViewById(R.id.productFilling);
        product_Weight = findViewById(R.id.productWeight);
        package_includes = findViewById(R.id.packageIncludes);
        submit1 = findViewById(R.id.submitButton1);
        product_price = findViewById(R.id.productPrice);
        product_highlights = findViewById(R.id.productHighlights);
        customer_price = findViewById(R.id.customerPrice);
        numberOf_Pieces = findViewById(R.id.numberOfPieces);
        idTextView = findViewById(R.id.copyId);
        brand_name = findViewById(R.id.brandName);
        product_warranty = findViewById(R.id.productWarranty);
        rootReference = FirebaseDatabase.getInstance().getReference();
        pStorageRef = FirebaseStorage.getInstance().getReference();
        intentItemId = getIntent().getStringExtra("itemId");
        colorRelativeLayout = findViewById(R.id.colorRelativeLayout);
        colorPalat = findViewById(R.id.colorPalates);
        colorHexCode = findViewById(R.id.colorTextCode);


        imageTex1 = findViewById(R.id.text1);
        imageTex2 = findViewById(R.id.text2);
        imageTex3 = findViewById(R.id.textView5);
        imageTex4 = findViewById(R.id.textView6);
        imageTex5 = findViewById(R.id.textView7);
        imageTex6 = findViewById(R.id.textView8);

        images = new ArrayList<>();
        imageViews = new ArrayList<>();
        downloadUrls = new ArrayList<>();

        idTextView.setText("Product Id: " + intentItemId);

        parentItem = getIntent().getStringExtra("parentCategory");
        childItem = getIntent().getStringExtra("childCategory");


        if (childItem.equals("Ladies Purse")) {
            colorPalat.setVisibility(View.VISIBLE);
        }
        customer_price.setEnabled(false);

        rootReference.child("products/" + intentItemId + "/package_details/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    PackageDetails packageDetails = dataSnapshot.getValue(PackageDetails.class);
                    assert packageDetails != null;
                    packageDepth = Double.parseDouble(packageDetails.getPackage_depth());
                    packageHeight = Double.parseDouble(packageDetails.getPackage_height());
                    packageWidth = Double.parseDouble(packageDetails.getPackage_width());
                    packageWeight = Double.parseDouble(packageDetails.getWeight());
                    volumerticWeight = (packageDepth * packageHeight * packageWidth) / 4000;

                } else {
                    Toast.makeText(Stationery.this, "some error occurred1", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Stationery.this, "some error occurred", Toast.LENGTH_SHORT).show();
            }
        });


        ColorSlider slider = findViewById(R.id.color_slider);
        slider.setSelectorColor(Color.GREEN);
        slider.setListener(mListener);

        ColorSlider sliderGradientArray = findViewById(R.id.color_slider_gradient_array);
        sliderGradientArray.setGradient(new int[]{Color.MAGENTA, Color.parseColor("#561571"), Color.parseColor("#1c2566"), Color.BLUE, Color.CYAN, Color.parseColor("#00352c"), Color.GREEN, Color.YELLOW, Color.RED, Color.BLACK, Color.WHITE}, 300);
        sliderGradientArray.setListener(mListener);


        // check the first upload is success or not

        rootReference.child("products").child(intentItemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("base_material")) {

                    Products products = dataSnapshot.getValue(Products.class);
                    base_material.setText(products.getBase_material());
                    brand_name.setText(products.getBrand_name());
                    product_warranty.setText(products.getWarranty_description());
                    package_includes.setText(products.getPackage_includes());

                    base_material.setEnabled(false);
                    package_includes.setEnabled(false);
                    product_warranty.setEnabled(false);
                    brand_name.setEnabled(false);

                } else {


                    Toast.makeText(Stationery.this, "no match", Toast.LENGTH_SHORT).show();


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
                if (imageUri1 != null & ((CheckImageSize(imageUri1) / 1024) < 500)) {
                    Intent gallery1 = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery1.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery1, "Select Image2"), 2);
                } else {
                    Toast.makeText(Stationery.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Select Image3
        imageView3 = findViewById(R.id.Image3);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null & imageUri2 != null & ((CheckImageSize(imageUri1) / 1024) < 500) & ((CheckImageSize(imageUri2) / 1024) < 500)) {
                    Intent gallery2 = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery2.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery2, "Select Image3"), 3);

                } else {
                    Toast.makeText(Stationery.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Select Image4
        imageView4 = findViewById(R.id.Image4);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null & imageUri2 != null & imageUri3 != null & ((CheckImageSize(imageUri1) / 1024) < 500)
                        & ((CheckImageSize(imageUri2) / 1024) < 500) & ((CheckImageSize(imageUri3) / 1024) < 500)) {
                    Intent gallery3 = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery3.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery3, "Select Image3"), 4);
                } else {
                    Toast.makeText(Stationery.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }

            }

        });

        //Select Image5
        imageView5 = findViewById(R.id.Image5);
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null & imageUri2 != null & imageUri3 != null & imageUri4 != null
                        & ((CheckImageSize(imageUri1) / 1024) < 500) & ((CheckImageSize(imageUri2) / 1024) < 500)
                        & ((CheckImageSize(imageUri3) / 1024) < 500) & ((CheckImageSize(imageUri4) / 1024) < 500)) {
                    Intent gallery4 = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery4.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery4, "Select Image3"), 5);
                } else {
                    Toast.makeText(Stationery.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Select Image6
        imageView6 = findViewById(R.id.Image6);
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null & imageUri2 != null & imageUri3 != null & imageUri4 != null & imageUri5 != null

                        & ((CheckImageSize(imageUri1) / 1024) < 500) & ((CheckImageSize(imageUri2) / 1024) < 500)
                        & ((CheckImageSize(imageUri3) / 1024) < 500) & ((CheckImageSize(imageUri4) / 1024) < 500) & ((CheckImageSize(imageUri5) / 1024) < 500)) {
                    Intent gallery5 = new Intent(Intent.ACTION_GET_CONTENT);
                    gallery5.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery5, "Select Image3"), 6);
                } else {
                    Toast.makeText(Stationery.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }

            }

        });


//validing color name
        product_Weight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!product_Weight.getText().toString().equals("")) {

                    if (!hasFocus) {
                        rootReference.child("products").child(intentItemId).child("color_details").child(product_Weight.getText().toString()).child("size").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(color_name.getText().toString())) {
                                    color_name.setError("This weight Already available for this color");
                                    validColor = "not_available";
                                } else {
                                    // Toast.makeText(MainActivity.this, productId.getEditText().getText().toString()+seller_id, Toast.LENGTH_SHORT).show();
                                    validColor = "available";
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                } else {
                    Toast.makeText(Stationery.this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }


            }
        });


        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (stateCheck) {
                    Datasubmit();
                }


            }
        });


        product_price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!product_price.getText().toString().equals("")) {
                    customer_price.setText(String.valueOf(generateCustomerPrice()));

                }
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

        if (imageUri1 != null & sizeImage1 > 500) {
            imageViews.add(imageUri1);
        }
        if (imageUri2 != null & (CheckImageSize(imageUri2) / 1024) > 500) {
            imageViews.add(imageUri2);
        }
        if (imageUri3 != null & (CheckImageSize(imageUri3) / 1024) > 500) {
            imageViews.add(imageUri3);
        }
        if (imageUri4 != null & (CheckImageSize(imageUri4) / 1024) > 500) {
            imageViews.add(imageUri4);
        }
        if (imageUri5 != null & (CheckImageSize(imageUri5) / 1024) > 500) {
            imageViews.add(imageUri5);
        }
        if (imageUri6 != null & (CheckImageSize(imageUri6) / 1024) > 500) {
            imageViews.add(imageUri6);
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

        if (imageUri4 != null) {
            if ((CheckImageSize(imageUri4) / 1024) < 500) {
                images.add(imageUri4);
                imageTex4.setText("image 4");
            } else {
                imageTex4.setText("Size <150Kb");
                imageTex4.setTextColor(Color.RED);

            }
        }

        if (imageUri5 != null) {
            if ((CheckImageSize(imageUri5) / 1024) < 500) {
                images.add(imageUri5);
                imageTex5.setText("image 5");
            } else {
                imageTex5.setText("Size <150Kb");
                imageTex5.setTextColor(Color.RED);

            }
        }

        if (imageUri6 != null) {
            if ((CheckImageSize(imageUri6) / 1024) < 500) {
                images.add(imageUri6);
                imageTex6.setText("image 6");
            } else {
                imageTex6.setText("Size <150Kb");
                imageTex6.setTextColor(Color.RED);

            }
        }
        if (childItem.equals("Ladies Purse") & colorHexCode.getText().toString().equals("")) {
            Toast.makeText(this, "Select any color", Toast.LENGTH_SHORT).show();
        } else {

            if (color_name.getText().toString().equals("") | size_description.getText().toString().equals("")
                    | product_Weight.getText().toString().equals("") | base_material.getText().toString().equals("") |
                    product_price.getText().toString().equals("") | numberOf_Pieces.getText().toString().equals("") | images.size() == 0 | product_highlights.getText().toString().equals("")) {

                Toast.makeText(getApplicationContext(), "Some Fields Are Empty", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();

            } else {
                if (validColor.equals("available")) {

                    if (imageViews.size() > 0) {


                        imageViews.removeAll(imageViews);
                        AlertDialog alertbox = new AlertDialog.Builder(this)
                                .setTitle("Are you Sure?")
                                .setIcon(R.drawable.ic_baseline_warning_24)
                                .setMessage("Some images are not less then 150 kb, this images are not going to be uploaded.\n Are you sure?")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0, int arg1) {

                                        //Toast.makeText(Stationery.this, "images"+String.valueOf(images.size())+" imageViews"+String.valueOf(imageViews.size()), Toast.LENGTH_SHORT).show();
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


                    } else {


                        imageViews.removeAll(imageViews);

                        Toast.makeText(Stationery.this, "else = images" + String.valueOf(images.size()) + " imageViews" + String.valueOf(imageViews.size()), Toast.LENGTH_SHORT).show();
                        startUpload();
                        showProgressDialog();

                    }


                } else {
                    Toast.makeText(this, "Color name Already exists!", Toast.LENGTH_SHORT).show();
                }


            }
        }


    }

    public void startUpload() {


        final String sizePathString = "products/" + intentItemId + "/color_details/" + color_name.getText().toString() + "/size/";
        final String colorPathString = "products/" + intentItemId + "/color_details/" + color_name.getText().toString();

        final Map colorMap = new HashMap();
        colorMap.put("color_name", color_name.getText().toString());

        if (childItem.equals("Ladies Purse")) {
            colorMap.put("color_code", colorHexCode.getText().toString());
        } else {
            colorMap.put("color_code", "#020729");

        }
        colorMap.put("product_highlights", product_highlights.getText().toString());

        final String colorname = color_name.getText().toString();
        String sizedescription = size_description.getText().toString();
        String baseMaterial = base_material.getText().toString();
        final String procuctWeight = product_Weight.getText().toString();
        String packageincludes = package_includes.getText().toString();
        String productPrice = product_price.getText().toString();
        String customerPrice = customer_price.getText().toString();
        String numberOfPieces = numberOf_Pieces.getText().toString();
        String productWarranty = product_warranty.getText().toString();
        String brandName = brand_name.getText().toString();
        Map otherDetailsProductHashMap = new HashMap();
        otherDetailsProductHashMap.put("size_description", sizedescription);
        otherDetailsProductHashMap.put("base_material", baseMaterial);
        otherDetailsProductHashMap.put("package_includes", packageincludes);
        otherDetailsProductHashMap.put("warranty_description", productWarranty);
        otherDetailsProductHashMap.put("brand_name", brandName);

        final Map sizedetailsMap = new HashMap();
        sizedetailsMap.put("size", procuctWeight);
        sizedetailsMap.put("min_price", Double.parseDouble(productPrice.trim()));
        sizedetailsMap.put("max_price", Double.parseDouble(customerPrice.trim())); //have to add the calculated price
        sizedetailsMap.put("pieces", Double.parseDouble(numberOfPieces.trim()));

        rootReference.child("products").child(intentItemId).updateChildren(otherDetailsProductHashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {

                    rootReference.child(colorPathString).updateChildren(colorMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError == null) {

                                rootReference.child(sizePathString + procuctWeight).updateChildren(sizedetailsMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        Toast.makeText(Stationery.this, "Done", Toast.LENGTH_SHORT).show();
                                        size_description.setEnabled(false);
                                        base_material.setEnabled(false);
                                        package_includes.setEnabled(false);
                                        product_warranty.setEnabled(false);
                                        brand_name.setEnabled(false);

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

                            if (!dataSnapshot.hasChild("thumb_image")) {
                                if (downloadUrls.get(0) != null) {

                                    Map thumbImageMap = new HashMap();
                                    thumbImageMap.put("thumb_image", downloadUrls.get(0));

                                    rootReference.child("products").child(intentItemId).updateChildren(thumbImageMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            statusCheck();
                                            dismissProgressDialog();
                                        }
                                    });
                                }

                            } else {
                                statusCheck();
                                // dismissProgressDialog();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(Stationery.this, "some error occurred", Toast.LENGTH_SHORT).show();
                            dismissProgressDialog();
                        }
                    });

                    dismissProgressDialog();
                    Toast.makeText(Stationery.this, "done", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(Stationery.this, "Error occurred for updateing status.", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(Stationery.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(Stationery.this);
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

    public Double generateCustomerPrice() {

        fildPrice = Double.parseDouble(product_price.getText().toString());


        sellerPrice = fildPrice + ((fildPrice * 6) / 100);
        finalSellerPrice = sellerPrice;

        if (packageWeight > volumerticWeight)
            finalWeight = packageWeight;
        else
            finalWeight = volumerticWeight;


        if (finalWeight < 0.5) {

            if (sellerPrice < 500) {
                finalSellerPrice += 80;
            } else if (sellerPrice >= 500 && sellerPrice < 1000) {
                finalSellerPrice += 100;
            } else
                finalSellerPrice += 120;

        } else if (finalWeight >= 0.5 && finalWeight < 1) {

            if (sellerPrice < 500) {
                finalSellerPrice += 100;
            } else if (sellerPrice >= 500 && sellerPrice < 1000) {
                finalSellerPrice += 130;
            } else
                finalSellerPrice += 200;

        } else if (finalWeight >= 1 && finalWeight < 1.5) {

            if (sellerPrice < 1000) {
                finalSellerPrice += 150;
            } else if (sellerPrice >= 1000 && sellerPrice < 2000) {
                finalSellerPrice += 200;
            } else
                finalSellerPrice += 300;

        }

        if (childItem.equals("Sunglasses")) {
            customerPrice = finalSellerPrice + (finalSellerPrice * 28 / 100);
        }

        if (parentItem.equals("Stationary")) {
            customerPrice = finalSellerPrice + (finalSellerPrice * 12 / 100);
        }
        if (parentItem.equals("Fashion Accessories") && !childItem.equals("Sunglasses")) {
            customerPrice = finalSellerPrice + (finalSellerPrice * 18 / 100);
        }
        return Math.ceil(customerPrice);

    }


}