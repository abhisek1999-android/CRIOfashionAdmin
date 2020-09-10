package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
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
import com.github.naz013.colorslider.ColorSlider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ColorSizeAddingActivity extends AppCompatActivity {

    TextView colorHexCode, sizeChartNameTextView;
    RelativeLayout colorLayout;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    protected ProgressDialog progressDialog;
    Uri imageUri1, imageUri2, imageUri3, imageUri4, imageUri5, imageUri6;
    Button submit_data;
    private FirebaseAuth mAuth;
    int i;
    int j = 0,flag=0;
    String intentItemId;
    ArrayList<Uri> imageViews;
    ArrayList<Uri> images;
    ArrayList<String> downloadUrls;



    String hex;

    String parentItem, childItem;

    private Button submitButton;
    private FirebaseUser firebaseUser;
    private StorageReference pStorageRef;
    private DatabaseReference rootReference;
    private EditText product_warranty;
    String validColor = "available";

    private EditText b2cSprice, b2cMprice, b2cLprice, b2cXLprice, b2cXXLprice;

    private EditText b2bSprice, b2bMprice, b2bLprice, b2bXLprice, b2bXXLprice;

    private EditText sInventory, mInventory, lInventory, xlInventory, xxlInventory;

    private TextView sTextView, mTextView, lTextView, xlTextView, xxlTextView;

    private EditText colorName;

    private RelativeLayout sizeRelativeLayout;

    Boolean check=false;
    Boolean stateCheck=true;

    private EditText package_Includes;

    private RecyclerView sizeChartRecyclerView;
    TextView idTextView;
    private TextView imageTex1, imageTex2, imageTex3, imageTex4, imageTex5, imageTex6;

    Double volumerticWeight,sellerPrice,customerPrice,finalWeight,finalSellerPrice;
    Double packageWidth,packageDepth,packageHeight,packageWeight;
    Double fildPrice;


    private ColorSlider.OnColorSelectedListener mListener = new ColorSlider.OnColorSelectedListener() {
        @Override
        public void onColorChanged(int position, int color) {
            updateView(color);
        }
    };

    private void updateView(int color) {

        colorLayout.setBackgroundColor(color);
        hex = "#" + Integer.toHexString(color).substring(2);
        colorHexCode.setText(hex);

    }


    //Uri font_imageUri, back_imageUri, orginal_imageUri;
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
        setContentView(R.layout.activity_color_size_adding);

        getApplicationContext().registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));//CHECKING CONNECTIVITY

        images = new ArrayList<>();
        imageViews = new ArrayList<>();
        downloadUrls=new ArrayList<>();

        rootReference = FirebaseDatabase.getInstance().getReference();               //connection to root of the database
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        pStorageRef = FirebaseStorage.getInstance().getReference();

        submitButton = findViewById(R.id.submitButton);
        sizeChartRecyclerView = findViewById(R.id.sizeChartChoosingRecyclerView);
        sizeChartRecyclerView.hasFixedSize();
        sizeChartRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        idTextView = findViewById(R.id.copyId);
        intentItemId = getIntent().getStringExtra("itemId");

        idTextView.setText("Product Id: " + intentItemId);


        imageTex1 = findViewById(R.id.textView3);
        imageTex2 = findViewById(R.id.textView4);
        imageTex3 = findViewById(R.id.textView5);
        imageTex4 = findViewById(R.id.textView6);
        imageTex5 = findViewById(R.id.textView7);
        imageTex6 = findViewById(R.id.textView8);


        parentItem = getIntent().getStringExtra("parentCategory");
        childItem = getIntent().getStringExtra("childCategory");
        sizeRelativeLayout = findViewById(R.id.sizeRelativeLayout);
        package_Includes = findViewById(R.id.packageIncludes);
        product_warranty = findViewById(R.id.productWarranty);


        sizeChartNameTextView = findViewById(R.id.sizechartnameTextView);

        colorHexCode = findViewById(R.id.colorTextCode);
        colorLayout = findViewById(R.id.colorRelativeLayout);
        submit_data = findViewById(R.id.submitButton);

        colorName = findViewById(R.id.colorName);

        b2cSprice = findViewById(R.id.b2cSPrice);
        b2cMprice = findViewById(R.id.b2cMPrice);
        b2cLprice = findViewById(R.id.b2cLPrice);
        b2cXLprice = findViewById(R.id.b2cXLPrice);
        b2cXXLprice = findViewById(R.id.b2cXXLPrice);


        b2bSprice = findViewById(R.id.b2bSPrice);
       b2bSprice.setEnabled(false);
       
        b2bMprice = findViewById(R.id.b2bMPrice);
        b2bMprice.setEnabled(false);
        b2bLprice = findViewById(R.id.b2bLPrice);
        b2bLprice.setEnabled(false);
        b2bXLprice = findViewById(R.id.b2bXLPrice);
        b2bXLprice.setEnabled(false);
        b2bXXLprice = findViewById(R.id.b2bXXLPrice);
        b2bXXLprice.setEnabled(false);

        sInventory = findViewById(R.id.sInventory);
        mInventory = findViewById(R.id.mInventory);
        lInventory = findViewById(R.id.lInventory);
        xlInventory = findViewById(R.id.xlInventory);
        xxlInventory = findViewById(R.id.xxlInventory);

        sTextView = findViewById(R.id.textview7);
        mTextView = findViewById(R.id.textview8);
        lTextView = findViewById(R.id.textview10);
        xlTextView = findViewById(R.id.textview11);
        xxlTextView = findViewById(R.id.textview12);


        if (childItem.equals("Saree")|childItem.equals("Scerf")){

            sTextView.setText("450cm");
            mTextView.setText("550cm");
            lTextView.setText("650cm");
            xlTextView.setText("750cm");
            xxlTextView.setText("850cm");

        }


        if (childItem.equals("Jeans") | childItem.equals("Leggings") | childItem.equals("Jeggings")|
                childItem.equals("Palazzo")|childItem.equals("Hot Pants")|childItem.equals("Trousers")|childItem.equals("Skirt")
                |childItem.equals("Under Garments")|childItem.equals("Shorts")) {

            sTextView.setText("28");
            mTextView.setText("30");
            lTextView.setText("32");
            xlTextView.setText("34");
            xxlTextView.setText("36");
        }


        ColorSlider slider = findViewById(R.id.color_slider);
        slider.setSelectorColor(Color.GREEN);
        slider.setListener(mListener);

        ColorSlider sliderGradientArray = findViewById(R.id.color_slider_gradient_array);
        sliderGradientArray.setGradient(new int[]{Color.MAGENTA, Color.parseColor("#561571"), Color.parseColor("#1c2566"), Color.BLUE, Color.CYAN, Color.parseColor("#00352c"), Color.GREEN, Color.YELLOW, Color.RED, Color.BLACK, Color.WHITE}, 300);
        sliderGradientArray.setListener(mListener);





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
                    Toast.makeText(ColorSizeAddingActivity.this, "some error occurred1", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ColorSizeAddingActivity.this, "some error occurred", Toast.LENGTH_SHORT).show();
            }
        });



//set pre seted fields

        rootReference.child("products").child(intentItemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("warranty_description")) {

                    Products products = dataSnapshot.getValue(Products.class);
                    product_warranty.setText(products.getWarranty_description());
                    package_Includes.setText(products.getPackage_includes());


                    product_warranty.setEnabled(false);
                    package_Includes.setEnabled(false);

                } else {


                    Toast.makeText(ColorSizeAddingActivity.this, "no match", Toast.LENGTH_SHORT).show();


                }

                if (dataSnapshot.hasChild("size_chart")){
                    Products products = dataSnapshot.getValue(Products.class);
                    sizeChartRecyclerView.setVisibility(View.GONE);
                    if (products.getSize_chart()!=null) {
                        sizeChartNameTextView.setText(products.getSize_chart());
                    }
                }else {

                    sizeChartRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //CALCULATING GST

      b2cSprice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if (!b2cSprice.getText().toString().equals("")){
                    b2bSprice.setText(String.valueOf(generateCustomerPrice(Double.parseDouble(b2cSprice.getText().toString()))));
                } }

            }
      });


        b2cMprice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!b2cMprice.getText().toString().equals("")){
                    b2bMprice.setText(String.valueOf(generateCustomerPrice(Double.parseDouble(b2cMprice.getText().toString()))));

                }
            }
        });

        b2cLprice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!b2cLprice.getText().toString().equals("")){
                    b2bLprice.setText(String.valueOf(generateCustomerPrice(Double.parseDouble(b2cLprice.getText().toString()))));

                }
            }
        });

        b2cXLprice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!b2cXLprice.getText().toString().equals("")){
                    b2bXLprice.setText(String.valueOf(generateCustomerPrice(Double.parseDouble(b2cXLprice.getText().toString()))));

                }
            }
        });

        b2cXXLprice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!b2cXXLprice.getText().toString().equals("")){
                    b2bXXLprice.setText(String.valueOf(generateCustomerPrice(Double.parseDouble(b2cXXLprice.getText().toString()))));

                }
            }
        });





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
                    Toast.makeText(ColorSizeAddingActivity.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ColorSizeAddingActivity.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ColorSizeAddingActivity.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ColorSizeAddingActivity.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ColorSizeAddingActivity.this, "Select Previous Image First", Toast.LENGTH_SHORT).show();
                }

            }

        });


        colorName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    rootReference.child("products").child(intentItemId).child("color_details").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(colorName.getText().toString())) {
                                colorName.setError("This color Already available");
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
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (stateCheck) {
                    colorItemAdded();
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


    public void colorItemAdded() {


        //b2c =max,b2b=min;
        final String sizePathString = "products/" + intentItemId + "/color_details/" + colorName.getText().toString() + "/size/";
        final String colorPathString = "products/" + intentItemId + "/color_details/" + colorName.getText().toString();

        images.removeAll(images);
        imageViews.removeAll(imageViews);
        downloadUrls.removeAll(downloadUrls);


        final Map colorMap = new HashMap();
        colorMap.put("color_name", colorName.getText().toString());
        colorMap.put("color_code", hex);
        colorMap.put("color_image_url", "default");



        //Adding package includes in products child


        final Map pathRefMap = new HashMap();
        Map sSizeMap = new HashMap();
        if (!b2cSprice.getText().toString().equals("") & !b2bSprice.getText().toString().equals("") & !sInventory.getText().toString().equals("")) {
            sSizeMap.put("size", sTextView.getText().toString());
            sSizeMap.put("max_price", Double.parseDouble(b2cSprice.getText().toString().trim()));
            sSizeMap.put("min_price", Double.parseDouble(b2bSprice.getText().toString().trim()));
            sSizeMap.put("pieces", Double.parseDouble(sInventory.getText().toString().trim()));
            pathRefMap.put(sizePathString + sTextView.getText().toString(), sSizeMap);

        } else {

            // Toast.makeText(this, "Filds are rquired", Toast.LENGTH_SHORT).show();
        }


        Map mSizeMap = new HashMap();
        if (!b2cMprice.getText().toString().equals("") & !b2bMprice.getText().toString().equals("") & !mInventory.getText().toString().equals("")) {
            mSizeMap.put("size", mTextView.getText().toString());
            mSizeMap.put("max_price", Double.parseDouble(b2cMprice.getText().toString().trim()));
            mSizeMap.put("min_price", Double.parseDouble(b2bMprice.getText().toString().trim()));
            mSizeMap.put("pieces", Double.parseDouble(mInventory.getText().toString().trim()));
            pathRefMap.put(sizePathString + mTextView.getText().toString(), mSizeMap);
        }

        Map lSizeMap = new HashMap();
        if (!b2cLprice.getText().toString().equals("") & !b2bLprice.getText().toString().equals("") & !lInventory.getText().toString().equals("")) {
            lSizeMap.put("size", lTextView.getText().toString());
            lSizeMap.put("max_price", Double.parseDouble(b2cLprice.getText().toString().trim()));
            lSizeMap.put("min_price", Double.parseDouble(b2bLprice.getText().toString().trim()));
            lSizeMap.put("pieces", Double.parseDouble(lInventory.getText().toString().trim()));
            pathRefMap.put(sizePathString + lTextView.getText().toString(), lSizeMap);
        }


        Map xlSizeMap = new HashMap();
        if (!b2cXLprice.getText().toString().equals("") & !b2bXLprice.getText().toString().equals("") & !xlInventory.getText().toString().equals("")) {
            xlSizeMap.put("size", xlTextView.getText().toString());
            xlSizeMap.put("max_price", Double.parseDouble(b2cXLprice.getText().toString().trim()));
            xlSizeMap.put("min_price", Double.parseDouble(b2bXLprice.getText().toString().trim()));
            xlSizeMap.put("pieces", Double.parseDouble(xlInventory.getText().toString().trim()));
            pathRefMap.put(sizePathString + xlTextView.getText().toString(), xlSizeMap);
        }

        Map xxlSizeMap = new HashMap();
        if (!b2cXXLprice.getText().toString().equals("") & !b2bXXLprice.getText().toString().equals("") & !xxlInventory.getText().toString().equals("")) {
            xxlSizeMap.put("size", xxlTextView.getText().toString());
            xxlSizeMap.put("max_price", Double.parseDouble(b2cXXLprice.getText().toString().trim()));
            xxlSizeMap.put("min_price", Double.parseDouble(b2bXXLprice.getText().toString().trim()));
            xxlSizeMap.put("pieces", Double.parseDouble(xxlInventory.getText().toString().trim()));
            pathRefMap.put(sizePathString + xxlTextView.getText().toString(), xxlSizeMap);
        }


        Long sizeImage1 = CheckImageSize(imageUri1) / 1024;

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
                //Toast.makeText(this, "Size should be less then 150Kb", Toast.LENGTH_SHORT).show();
            }
        }


        if (imageUri2 != null) {
            if ((CheckImageSize(imageUri2) / 1024) < 500) {
                images.add(imageUri2);
                imageTex2.setText("image 2");

            } else {
                imageTex2.setText("Size <150Kb");
                imageTex2.setTextColor(Color.RED);
                // Toast.makeText(this, "Size should be less then 150Kb", Toast.LENGTH_SHORT).show();
            }
        }

        if (imageUri3 != null) {

            if ((CheckImageSize(imageUri3) / 1024) < 500) {
                images.add(imageUri3);
                imageTex3.setText("image 3");
            } else {
                imageTex3.setText("Size <150Kb");
                imageTex3.setTextColor(Color.RED);
                // Toast.makeText(this, "Size should be less then 150Kb", Toast.LENGTH_SHORT).show();
            }
        }

        if (imageUri4 != null) {
            if ((CheckImageSize(imageUri4) / 1024) < 500) {
                images.add(imageUri4);
                imageTex4.setText("image 4");
            } else {
                imageTex4.setText("Size <150Kb");
                imageTex4.setTextColor(Color.RED);
                //Toast.makeText(this, "Size should be less then 150Kb", Toast.LENGTH_SHORT).show();
            }
        }

        if (imageUri5 != null) {
            if ((CheckImageSize(imageUri5) / 1024) < 500) {
                images.add(imageUri5);
                imageTex5.setText("image 5");
            } else {
                imageTex5.setText("Size <150Kb");
                imageTex5.setTextColor(Color.RED);
                // Toast.makeText(this, "Size should be less then 150Kb", Toast.LENGTH_SHORT).show();
            }
        }

        if (imageUri6 != null) {
            if ((CheckImageSize(imageUri6) / 1024) < 500) {
                images.add(imageUri6);
                imageTex5.setText("image 6");
            } else {
                imageTex6.setText("Size <150Kb");
                imageTex6.setTextColor(Color.RED);
                //   Toast.makeText(this, "Size should be less then 150Kb", Toast.LENGTH_SHORT).show();
            }
        }


        if (images.size() > 0) {
            if (!colorHexCode.getText().toString().equals("")) {
                if (!sizeChartNameTextView.getText().toString().equals("Choose Size Chart")) {
                    if (pathRefMap.size() != 0 & !colorName.getText().toString().equals("")) {

                        if (validColor.equals("available")) {


                            if (imageViews.size() > 0) {
                                imageViews.removeAll(imageViews);
                                Toast.makeText(ColorSizeAddingActivity.this, "images" + String.valueOf(images.size()) + " imageViews" + String.valueOf(imageViews.size()), Toast.LENGTH_SHORT).show();

                                //     Toast.makeText(this, String.valueOf(images.size()), Toast.LENGTH_SHORT).show();
                                AlertDialog alertbox = new AlertDialog.Builder(this)
                                        .setTitle("Are you Sure?")
                                        .setIcon(R.drawable.ic_baseline_warning_24)
                                        .setMessage("Some images are not less then 150 kb, this images are not going to be uploaded.\n Are you sure?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface arg0, int arg1) {

                                                startUpload(pathRefMap, colorMap, colorPathString);

                                                showProgressDialog();
                                                //Toast.makeText(getApplicationContext(), "if runs", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("NO", null)
                                        .setCancelable(false)
                                        .show();

                            } else {
                                imageViews.removeAll(imageViews);
                                Toast.makeText(ColorSizeAddingActivity.this, "else = images" + String.valueOf(images.size()) + " imageViews" + String.valueOf(imageViews.size()), Toast.LENGTH_SHORT).show();

                                startUpload(pathRefMap, colorMap, colorPathString);
                                showProgressDialog();
                            }


                        } else {
                            Toast.makeText(this, "Color name Already exists!", Toast.LENGTH_SHORT).show();
                        }


                    } else {

                        Toast.makeText(this, "You Must add at least one color or size properly", Toast.LENGTH_SHORT).show();
                        dismissProgressDialog();
                    }
                } else {

                    Toast.makeText(this, "Choose size chart first", Toast.LENGTH_LONG).show();
                    dismissProgressDialog();
                }
            } else {
                Toast.makeText(this, "Select the Color!", Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }

        } else {

            Toast.makeText(this, "Select Atleast one image less then 150Kb", Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
        }


    }

    private void startUpload(final Map pathRefMap, Map colorMap, String colorPathString) {


        Map packageHashMap = new HashMap();
        packageHashMap.put("package_includes", package_Includes.getText().toString());
        packageHashMap.put("warranty_description", product_warranty.getText().toString());
        packageHashMap.put("size_chart", sizeChartNameTextView.getText().toString());
        rootReference.child("products/" + intentItemId).updateChildren(packageHashMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // colorName.setText("");
                    Log.i("done", "done");
                    flag=1;
                    // dismissProgressDialog();
                }
            }
        });


        rootReference.child(colorPathString).setValue(colorMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()) {

                    rootReference.updateChildren(pathRefMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(ColorSizeAddingActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            uploadImage(colorName.getText().toString());
                            product_warranty.setEnabled(false);
                            package_Includes.setEnabled(false);
                            colorName.setText("");
                        }
                    });
                }
            }
        });


    }

    private void uploadImage(final String colorString) {


        j = 0;

        for (i = 0; i < images.size(); i++) {

            if (images.get(i) != null) {

                final String randomkey = UUID.randomUUID().toString();
                final StorageReference riversRef = pStorageRef.child("product_images").child(intentItemId).child(colorString).child(randomkey + ".jpg");


                riversRef.putFile(images.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        final String downloadUrl = uri.toString();
                                        downloadUrls.add(downloadUrl);
                                        saveImageUrl(downloadUrl, colorString);


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

    private void saveImageUrl(String downloadUrl, String colorString) {

        Map hashMap = new HashMap<>();

        String imageName = "image" + j;
        j++;
        hashMap.put(imageName, downloadUrl);

        rootReference.child("products/" + intentItemId + "/color_details/" + colorString + "/images").updateChildren(hashMap, new DatabaseReference.CompletionListener() {
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
                                //dismissProgressDialog();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(ColorSizeAddingActivity.this, "some error occurred", Toast.LENGTH_SHORT).show();
                            dismissProgressDialog();
                        }
                    });



                    dismissProgressDialog();
                    Toast.makeText(ColorSizeAddingActivity.this, "done", Toast.LENGTH_SHORT).show();

                    if (flag==1){
                        sizeChartRecyclerView.setVisibility(View.GONE);
                    }
                }


            }
        });
    }

    public void showProgressDialog() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ColorSizeAddingActivity.this);
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

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference sizeChart = rootReference.child("company_details").child(firebaseUser.getUid()).child("size_charts");

        FirebaseRecyclerOptions<SizeChart> options = new FirebaseRecyclerOptions.Builder<SizeChart>()
                .setQuery(sizeChart, SizeChart.class)
                .build();

        FirebaseRecyclerAdapter<SizeChart, SizeViewHolder> sizeChartHolder = new FirebaseRecyclerAdapter<SizeChart, SizeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SizeViewHolder sizeViewHolder, int i, @NonNull SizeChart sizeChart) {

                sizeViewHolder.sizeChartName.setText(sizeChart.getSize_chart_name());





                sizeViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //flag=1;
                        // sizeViewHolder.sizeNameRelativeLayout.setBackgroundResource(R.drawable.button_background_new);
                        // sizeViewHolder.sizeCharttName.setTextColor(Color.WHITE);
                        Toast.makeText(ColorSizeAddingActivity.this, sizeViewHolder.sizeChartName.getText().toString(), Toast.LENGTH_SHORT).show();

                        sizeChartNameTextView.setText(sizeViewHolder.sizeChartName.getText().toString());
                    }
                });

            }

            @NonNull
            @Override
            public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.size_choose_item_design, parent, false);
                ColorSizeAddingActivity.SizeViewHolder sizeViewHolder = new ColorSizeAddingActivity.SizeViewHolder(view);

                return sizeViewHolder;
            }
        };
        sizeChartRecyclerView.setAdapter(sizeChartHolder);
        sizeChartHolder.startListening();
    }


    public static class SizeViewHolder extends RecyclerView.ViewHolder {

        TextView sizeChartName;
        RelativeLayout sizeNameRelativeLayout;
        CardView sizeCardView;

        View mview;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
            sizeChartName = itemView.findViewById(R.id.size_chart_name);
            sizeNameRelativeLayout = itemView.findViewById(R.id.sizeNameRelativeLayout);
            sizeCardView = itemView.findViewById(R.id.sizeCardView);

        }
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
                                Toast.makeText(ColorSizeAddingActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
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


    public void statusCheck() {
        Map statusMap = new HashMap();
        statusMap.put("upload_status", "ok");


        rootReference.child("products").child(intentItemId).updateChildren(statusMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (databaseError == null) {
                    Log.i("done", "done");
                } else {
                    Toast.makeText(ColorSizeAddingActivity.this, "Error occurred for updateing status.", Toast.LENGTH_SHORT).show();
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

// for checking internetConnection

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

    public Double generateCustomerPrice(double v){

        fildPrice= v;


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

        if (finalSellerPrice>1000){
            customerPrice=finalSellerPrice+(finalSellerPrice*12/100);
        }
        if (finalSellerPrice<1000){
            customerPrice=finalSellerPrice+(finalSellerPrice*5/100);
        }

        return Math.ceil(customerPrice);

    }


}