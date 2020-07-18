package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText product_name;
    EditText product_description;
    EditText product_price;
    EditText product_company;
    EditText product_overview;
    EditText productId;
    TextView textView;
    Spinner prouduct_type;
    Spinner prouduct_for;
    int i;
    Boolean isImageUploaded;
    Button submit_data;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    StorageReference pStorageRef;
    private StorageTask uploadTask;
    private DatabaseReference databaseReference;
    private ImageView font_imageView, back_imageView, orginal_imageView;
    protected ProgressDialog progressDialog;
    private String product_for_item, product_type_item;

    Uri font_imageUri, back_imageUri, orginal_imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference = FirebaseDatabase.getInstance().getReference();               //connection to root of the database
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        pStorageRef = FirebaseStorage.getInstance().getReference();                                         //storage referance
        product_name = findViewById(R.id.productName);
        product_price = findViewById(R.id.productPrice);
        product_description = findViewById(R.id.productDescription);
        product_company = findViewById(R.id.companyName);
        product_overview = findViewById(R.id.productOverview);
        prouduct_type = findViewById(R.id.productType);
        prouduct_for = findViewById(R.id.productFor);
        submit_data = findViewById(R.id.submitButton);
        productId = findViewById(R.id.productId);
        textView = findViewById(R.id.textView2);
        //sellect font image from gallery
        font_imageView = findViewById(R.id.fontImage);
        font_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery, "Sellect Font Image"), 1);
            }
        });
        //Sellect the back Image
        back_imageView = findViewById(R.id.backImage);
        back_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery1 = new Intent(Intent.ACTION_GET_CONTENT);
                gallery1.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery1, "Sellect Back Image"), 2);
            }
        });
        //Sellect Orginal Image
        orginal_imageView = findViewById(R.id.orginalImage);
        orginal_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery2 = new Intent(Intent.ACTION_GET_CONTENT);
                gallery2.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery2, "Sellect Orginal Image"), 3);
            }
        });
        //For submit Image to fairbase
        submit_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Imagesubmit();

            }
        });
        //For Product Type Dropdown
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.color_spiner_layout,
                getResources().getStringArray(R.array.Types)
        );
        myAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        prouduct_type.setAdapter(myAdapter);
        prouduct_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                product_for_item = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Product For Whom Dropdown
        ArrayAdapter<String> forAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.color_spiner_layout,
                getResources().getStringArray(R.array.For)
        );
        forAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        prouduct_for.setAdapter(forAdapter);
        prouduct_for.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                product_type_item = parent.getItemAtPosition(position).toString();
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

    }


    // For Shwoing Image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            assert data != null;
            font_imageUri = data.getData()
            ;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), font_imageUri);
                font_imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (resultCode == RESULT_OK && requestCode == 2) {
            back_imageUri = data.getData();
            try {
                Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), back_imageUri);
                back_imageView.setImageBitmap(bitmap1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resultCode == RESULT_OK && requestCode == 3) {
            orginal_imageUri = data.getData();
            try {
                Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), orginal_imageUri);
                orginal_imageView.setImageBitmap(bitmap2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //image uploding
    private void Imagesubmit() {

        if (productId.getText().toString().equals("") |product_company.getText().toString().equals("") | product_name.getText().toString().equals("") | product_price.getText().toString().equals("")| product_description.getText().toString().equals("")
                | product_overview.getText().toString().equals(""))

        {
            Toast.makeText(MainActivity.this, "This fields are required!", Toast.LENGTH_SHORT).show();

        }

        else {
        showProgressDialog();
        String nameProduct = product_name.getText().toString();
        final String priceProduct = product_price.getText().toString();
        String descriptionProduct = product_description.getText().toString();
        String companyProduct = product_company.getText().toString();
        String overviewProduct = product_overview.getText().toString();
        final String productId_unique = productId.getText().toString();


        String path_ref = "products/" + product_type_item + "/" + product_for_item;


        Map productHashMap = new HashMap();
        productHashMap.put("name", nameProduct);
        productHashMap.put("price", priceProduct);
        productHashMap.put("description", descriptionProduct);
        productHashMap.put("company", companyProduct);
        productHashMap.put("overview", overviewProduct);
        productHashMap.put("productId", productId_unique);
        productHashMap.put("colour", "default");
        productHashMap.put("front_image_url", "default");
        productHashMap.put("back_image_url", "default");
        productHashMap.put("original_image_url", "default");

        Map pathRefMap = new HashMap();
        pathRefMap.put(path_ref + "/" + productId_unique, productHashMap);
        databaseReference.updateChildren(pathRefMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull final DatabaseReference databaseReference) {
                if (databaseError == null) {

                    // Toast.makeText(MainActivity.this, "Added SuccessFully!", Toast.LENGTH_SHORT).show();

                    if (!(font_imageUri ==null)) {

                        final String randomkey = UUID.randomUUID().toString();
                        final StorageReference riversRef = pStorageRef.child("product_images").child(productId_unique).child(randomkey + ".jpg");

                        riversRef.putFile(font_imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                final String downloadUrl = uri.toString();
                                                databaseReference.child("products").child(product_type_item).child(product_for_item).child(productId_unique)
                                                        .child("front_image_url").setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        dismissProgressDialog();

                                                        Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                        font_imageView.setImageResource(R.drawable.ic_baseline_account_box_24);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity.this, "Not Yet Done", Toast.LENGTH_SHORT).show();
                                                        //  dismissProgressDialog();

                                                    }
                                                });


                                            }
                                        });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        dismissProgressDialog();
                                        Toast.makeText(MainActivity.this, "Not Yet Done", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }


                    if (back_imageUri!=null) {

                        final String randomkey = UUID.randomUUID().toString();
                        final StorageReference riversRef = pStorageRef.child("product_images").child(productId_unique).child(randomkey + ".jpg");

                        riversRef.putFile(back_imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                final String downloadUrl = uri.toString();
                                                databaseReference.child("products").child(product_type_item).child(product_for_item).child(productId_unique)
                                                        .child("back_image_url").setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                         dismissProgressDialog();

                                                        Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                        back_imageView.setImageResource(R.drawable.ic_baseline_account_box_24);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity.this, "Not Yet Done", Toast.LENGTH_SHORT).show();
                                                        //  dismissProgressDialog();

                                                    }
                                                });


                                            }
                                        });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        dismissProgressDialog();
                                        Toast.makeText(MainActivity.this, "Not Yet Done", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }


                    if (!(orginal_imageUri==null)) {

                        final String randomkey = UUID.randomUUID().toString();
                        final StorageReference riversRef = pStorageRef.child("product_images").child(productId_unique).child(randomkey + ".jpg");

                        riversRef.putFile(orginal_imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                final String downloadUrl = uri.toString();
                                                databaseReference.child("products").child(product_type_item).child(product_for_item).child(productId_unique)
                                                        .child("original_image_url").setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                      dismissProgressDialog();

                                                        Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                        orginal_imageView.setImageResource(R.drawable.ic_baseline_account_box_24);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity.this, "Not Yet Done", Toast.LENGTH_SHORT).show();
                                                         dismissProgressDialog();

                                                    }
                                                });


                                            }
                                        });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        dismissProgressDialog();
                                        Toast.makeText(MainActivity.this, "Not Yet Done", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }

                    product_name.setText("");
                    product_company.setText("");
                    product_price.setText("");
                    product_description.setText("");
                    product_overview.setText("");
                    productId.setText("");

                } else {

                    dismissProgressDialog();
                    Toast.makeText(MainActivity.this, "Some error occoured!", Toast.LENGTH_SHORT).show();

                }
            }
        });


        // Toast.makeText(this, product_for_item, Toast.LENGTH_SHORT).show();


      /*  if (!font_imageUri.equals(null)) {

            ArrayList<Uri> images=new ArrayList<Uri>();
            images.add(font_imageUri);
            images.add(back_imageUri);
            images.add(orginal_imageUri);

            for (int i=0;i<images.size();i++){
            final String randomkey = UUID.randomUUID().toString();
            StorageReference riversRef = pStorageRef.child("product_images").child(randomkey + ".jpg");

            riversRef.putFile(images.get(i))
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
        }}*/
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
}

 /*final ArrayList<Uri> images=new ArrayList<Uri>();
                         images.add(orginal_imageUri);
                         images.add(back_imageUri);
                         images.add(font_imageUri);

                         final ArrayList <String> image_field_name=new ArrayList<String>();
                         image_field_name.add("front_image_url");
                         image_field_name.add("back_image_url");
                         image_field_name.add("original_image_url");



                         for (i=0;i<images.size();i++){



                             if (images.get(i)==null || images.get(i).equals("")){
                                 isImageUploaded=false;
                                 i++;
                             }

                             else {
                                 isImageUploaded=true;
                             final String randomkey = UUID.randomUUID().toString();
                             final StorageReference riversRef = pStorageRef.child("product_images").child(productId_unique).child(randomkey + ".jpg");

                             riversRef.putFile(images.get(i))
                                     .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                         @Override
                                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                             riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                 @Override
                                                 public void onSuccess(Uri uri) {

                                                     final String downloadUrl=uri.toString();
                                                     databaseReference.child("products").child(product_type_item).child(product_for_item).child(productId_unique)
                                                             .child(image_field_name.get(i-1)).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                         @Override
                                                         public void onSuccess(Void aVoid) {

                                                             dismissProgressDialog();

                                                             Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                             i--;


                                                         }
                                                     }).addOnFailureListener(new OnFailureListener() {
                                                         @Override
                                                         public void onFailure(@NonNull Exception e) {
                                                             Toast.makeText(MainActivity.this, "Not Yet Done", Toast.LENGTH_SHORT).show();
                                                             dismissProgressDialog();

                                                         }
                                                     });



                                                 }
                                             });

                                         }
                                     })
                                     .addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception exception) {
                                             dismissProgressDialog();
                                             Toast.makeText(MainActivity.this, "Not Yet Done", Toast.LENGTH_SHORT).show();
                                         }
                                     });
                         }

                         }*/