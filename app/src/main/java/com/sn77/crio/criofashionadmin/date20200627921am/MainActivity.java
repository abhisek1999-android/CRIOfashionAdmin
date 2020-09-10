package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText product_name;
    EditText product_description;
    EditText product_company;
    EditText product_overview;
    //*****************for TextInputLayout chenge all the EditText to TextInputLayout
    // ******************and at the time of retriving insted of .getText() we have to write getEditText().getText();
    TextInputLayout productId;
    EditText product_Feature;
    EditText product_shortdescription;
    EditText product_material,product_careinstruction,material_weight;
    EditText package_width,package_height,package_depth,package_weight;
    TextView textView;
    EditText product_type;

    Spinner product_for;
    String parentCategory,childCategory,productGeneratedId;

    int i;
    Button submit_data;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    StorageReference pStorageRef;
    private StorageTask uploadTask;
    private DatabaseReference databaseReference;
   // private ImageView font_imageView, back_imageView, orginal_imageView;
    protected ProgressDialog progressDialog;
    private String product_for_item, product_type_item;
    private String seller_id;
    private String productId_unique;

    private Boolean validProductID;
    Boolean check=false;
    Boolean stateCheck=true;


    View parentLayout;

    //Uri font_imageUri, back_imageUri, orginal_imageUri;
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (currentNetworkInfo.isConnected()) {
                if (check) {
                    stateCheck=true;
                    Snackbar snackbar=Snackbar.make(parentLayout, "Connected  ", Snackbar.LENGTH_LONG);
                    View snakBarView=snackbar.getView();
                    snakBarView.setBackgroundColor(Color.parseColor("#4ebaaa"));
                    snackbar.show();
                }
            } else {
                check = true;
                stateCheck=false;
                Snackbar snackbar=Snackbar.make(parentLayout, "Not Connected  ", Snackbar.LENGTH_INDEFINITE);
                View snakBarView=snackbar.getView();
                snakBarView.setBackgroundColor(Color.parseColor("#ef5350"));
                snackbar.show();

            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getApplicationContext().registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));//CHECKING CONNECTIVITY

        databaseReference = FirebaseDatabase.getInstance().getReference();               //connection to root of the database
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        pStorageRef = FirebaseStorage.getInstance().getReference();                                         //storage referance
        product_name = findViewById(R.id.productName);
        product_description = findViewById(R.id.productDescription);
        product_company = findViewById(R.id.companyName);
        product_overview = findViewById(R.id.productOverview);
        product_Feature=findViewById(R.id.productFeature);
        product_type = findViewById(R.id.productType);//this is editText now
        product_for=findViewById(R.id.productFor);

        parentLayout= findViewById(android.R.id.content);




        validProductID=true;

        submit_data = findViewById(R.id.submitButton);
        productId = findViewById(R.id.productId);
        textView = findViewById(R.id.textView2);
        product_shortdescription=findViewById(R.id.shortDescription);
        product_careinstruction=findViewById(R.id.careInstruction);
        product_material=findViewById(R.id.productMaterial);
        material_weight=findViewById(R.id.materialWight);

        package_width=findViewById(R.id.packageWidth);
        package_height=findViewById(R.id.packageHeight);
        package_depth=findViewById(R.id.packageDepth);
        package_weight=findViewById(R.id.packageWeight);

        parentCategory=getIntent().getStringExtra("parentCategory");// to take intent info
        childCategory=getIntent().getStringExtra("childCategory");

         product_type.setText(childCategory);
         product_type_item=product_type.getText().toString();
        product_type.setEnabled(false);



        if (parentCategory.equals("Home Accessories") | parentCategory.equals("Furniture") | parentCategory.equals("Stationary"))//for furniture boy girl choose must be gone
        {
            product_for.setVisibility(View.GONE);//for invisible the boys girls field
            product_for_item=parentCategory;//boys girls parent category
        }



       databaseReference.child("company_details").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               CompanyDetails companyDetails=dataSnapshot.getValue(CompanyDetails.class);

               if (companyDetails != null) {
                   seller_id=companyDetails.getSeller_id();

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });



        submit_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stateCheck){
                Datasubmit();}

            }
        });




        //Product For Whom Dropdown

        ArrayAdapter<String> forAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.color_spiner_layout,
                getResources().getStringArray(R.array.For)
        );
        forAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        product_for.setAdapter(forAdapter);

        if( parentCategory.equals("Woman's Fashion") | childCategory.equals("Ladies Purse")){  //for selection of girls when girls item in clicked
            product_for.setSelection(1);
            product_for_item=product_for.getSelectedItem().toString();

        }

        
       product_for.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                product_for_item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, product_type_item, Toast.LENGTH_SHORT).show();
            }
        });

        Objects.requireNonNull(productId.getEditText()).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                databaseReference.child("products").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(productId.getEditText().getText().toString()+seller_id)){
                            productId.getEditText().setError("This Product Id is Already available");
                            validProductID=false;
                        }

                        else  if (productId.getEditText().getText().length()<4){

                            productId.getEditText().setError("Id length should be more then 4 character");
                            validProductID=false;
                        }
                        else {

                            Toast.makeText(MainActivity.this, productId.getEditText().getText().toString()+seller_id, Toast.LENGTH_SHORT).show();
                            validProductID=true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            }
        });



    }



  //Deta uploding
    private void Datasubmit() {

        if(validProductID){
                if (productId.getEditText().getText().toString().equals("") |product_name.getText().toString().equals("")| product_description.getText().toString().equals("")
                        |package_width.getText().toString().equals("")|package_height.getText().toString().equals("")|package_depth.getText().toString().equals("")
                        |package_weight.getText().toString().equals(""))

                {
                    Toast.makeText(MainActivity.this, "Some Fields Are Empty!", Toast.LENGTH_SHORT).show();

                }

                else {

                    if (Double.parseDouble(package_weight.getText().toString())<1.5){

                        showProgressDialog();
                        String nameProduct = product_name.getText().toString();
                        String descriptionProduct = product_description.getText().toString();
                        String companyProduct = product_company.getText().toString();
                        String overviewProduct = product_overview.getText().toString();
                        productId_unique = productId.getEditText().getText().toString()+seller_id;
                        final String shortDescription = product_shortdescription.getText().toString();
                        final String productMaterial  = product_material.getText().toString();
                        String materialWeight=material_weight.getText().toString();
                        final String careInstruction = product_careinstruction.getText().toString();
                        String packageWidth = package_width.getText().toString();
                        final String packageHeight = package_height.getText().toString();
                        String packageDepth = package_depth.getText().toString();
                        String packageWeight = package_weight.getText().toString();
                        final String productFeature=product_Feature.getText().toString();

                        //  String path_ref = "products/" + productId_unique;


                        Map productHashMap = new HashMap();
                        productHashMap.put("name", nameProduct);
                        productHashMap.put("description", descriptionProduct);
                        productHashMap.put("company", companyProduct);
                        productHashMap.put("overview", overviewProduct);
                        productHashMap.put("productId", productId_unique);
                        productHashMap.put("short_description", shortDescription);
                        productHashMap.put("product_material", productMaterial);
                        productHashMap.put("material_weight",materialWeight);
                        productHashMap.put("care_information", careInstruction);
                        productHashMap.put("company_id",firebaseUser.getUid());
                        productHashMap.put("product_for",product_for_item);
                        productHashMap.put("product_type",product_type_item);
                        productHashMap.put("product_Feature",productFeature);
                        productHashMap.put("parent_category",parentCategory);
                        productHashMap.put("upload_status","false");




                        final Map packageHashMap=new HashMap();
                        packageHashMap.put("package_width",packageWidth);
                        packageHashMap.put("package_height",packageHeight);
                        packageHashMap.put("package_depth",packageDepth);
                        packageHashMap.put("weight",packageWeight);

                        final Map pathRefMap = new HashMap();
                        pathRefMap.put("products/"+productId_unique+"/package_details" , packageHashMap);

                        databaseReference.child("products/" + productId_unique).setValue(productHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){


                                    databaseReference.updateChildren(pathRefMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                Toast.makeText(MainActivity.this, "Added SuccessFully!", Toast.LENGTH_SHORT).show();

                                                product_name.setText("");
                                                product_company.setText("");
                                                product_description.setText("");
                                                product_overview.setText("");
                                                product_shortdescription.setText("");
                                                product_material.setText("");
                                                product_careinstruction.setText("");
                                                package_height.setText("");
                                                package_width.setText("");
                                                package_depth.setText("");
                                                package_weight.setText("");
                                                dismissProgressDialog();
                                                product_Feature.setText("");
                                                material_weight.setText("");

                                                //intending depends on the category

                                                if (parentCategory.equals("Furniture") | parentCategory.equals("Home Accessories"))

                                                {
                                                    Intent intent=new Intent(getApplicationContext(),others_details.class);
                                                    intent.putExtra("itemId",productId_unique);
                                                    intent.putExtra("parentCategory",parentCategory);
                                                    intent.putExtra("childCategory",childCategory);
                                                    startActivity(intent);
                                                }
                                                else if(childCategory.equals("Jewellery"))
                                                {
                                                    Intent intent=new Intent(getApplicationContext(),Jewellery.class);
                                                    intent.putExtra("itemId",productId_unique);
                                                    intent.putExtra("parentCategory",parentCategory);
                                                    intent.putExtra("childCategory",childCategory);
                                                    startActivity(intent);
                                                }

                                                else if(parentCategory.equals("Stationary") | parentCategory.equals("Fashion Accessories"))
                                                {
                                                    Intent intent=new Intent(getApplicationContext(),Stationery.class);
                                                    intent.putExtra("itemId",productId_unique);
                                                    intent.putExtra("parentCategory",parentCategory);
                                                    intent.putExtra("childCategory",childCategory);
                                                    startActivity(intent);
                                                }
                                                else{
                                                    Intent intent = new Intent(getApplicationContext(), ColorSizeAddingActivity.class);
                                                    intent.putExtra("itemId", productId_unique);
                                                    intent.putExtra("parentCategory", parentCategory);
                                                    intent.putExtra("childCategory", childCategory);
                                                    startActivity(intent);
                                                }


                                            }else {
                                                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                dismissProgressDialog();
                                            }
                                        }
                                    });


                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                dismissProgressDialog();

                            }
                        });






                    }else {
                        Toast.makeText(this, "weight should be less then 1.5 kg!", Toast.LENGTH_SHORT).show();
                    }


                }




            //////////////



        }else {
            Toast.makeText(this, "Please provide a valid Product ID!", Toast.LENGTH_SHORT).show();
        }
    }


    public void showProgressDialog() {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this);
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



  /*  private void showDialog() {
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
    }*/


}

