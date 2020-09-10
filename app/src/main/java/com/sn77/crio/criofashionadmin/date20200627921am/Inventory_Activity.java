package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sn77.crio.criofashionadmin.date20200627921am.Inventory_Activity.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Inventory_Activity extends AppCompatActivity {
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference productsRef;
    private StorageReference storageReference;
   private RecyclerView uploadedItemView;
   private RelativeLayout notFoundLayout;

    Boolean check=false;
    Boolean stateCheck=true;

    View viewPager;
    RelativeLayout ownProgressDialog;
    ArrayList<String> allItems;
    int position;
    ItemTouchHelper itemTouchHelper;
    String ItemOfArray;
    private TextView creditPint, deliveredItem, returnedItem, nextPaymentDate;
    int countItem=0;
    FirebaseRecyclerAdapter<Products, ProductViewHolder> firebaseRecyclerAdapter;


    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (currentNetworkInfo.isConnected()) {
                if (check) {
                    stateCheck=true;
                    Snackbar snackbar=Snackbar.make(uploadedItemView, "Connected  ", Snackbar.LENGTH_LONG);
                    View snakBarView=snackbar.getView();
                    snakBarView.setBackgroundColor(Color.parseColor("#4ebaaa"));
                    snackbar.show();
                }
            } else {
                check = true;
                stateCheck=false;
                Snackbar snackbar=Snackbar.make(uploadedItemView, "Not Connected  ", Snackbar.LENGTH_INDEFINITE);
                View snakBarView=snackbar.getView();
                snakBarView.setBackgroundColor(Color.parseColor("#ef5350"));
                snackbar.show();

            }
        }
    };

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);



        getApplicationContext().registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));//CHECKING CONNECTIVITY


        this.setFinishOnTouchOutside(false);

       //isConnected(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        productsRef = FirebaseDatabase.getInstance().getReference().child("products");
        storageReference=FirebaseStorage.getInstance().getReference();
        uploadedItemView = findViewById(R.id.uploaded_item_list);



        uploadedItemView.hasFixedSize();
        uploadedItemView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        ownProgressDialog=findViewById(R.id.ownProgressDialog);
        notFoundLayout=findViewById(R.id.notFoundLayout);
        creditPint = findViewById(R.id.creditPoint);
        deliveredItem = findViewById(R.id.deliveredItem);
        returnedItem = findViewById(R.id.returnItem);
        nextPaymentDate = findViewById(R.id.nextPaymentDate);
        allItems=new ArrayList<>();

        if(mCurrentUser!=null){

            Query firebaseSearchQuery = productsRef.orderByChild("company_id").equalTo(mCurrentUser.getUid());

            firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap: dataSnapshot.getChildren()) {

                        countItem= (int) snap.getChildrenCount();

                    }

                    if (countItem==0){
                        notFoundLayout.setVisibility(View.VISIBLE);
                    }

                    else {notFoundLayout.setVisibility(View.GONE);}

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


     itemTouchHelper  =new ItemTouchHelper(simpleCallback);


    }





ItemTouchHelper.SimpleCallback simpleCallback =new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


        position=viewHolder.getAdapterPosition();

        new AlertDialog.Builder(Inventory_Activity.this)
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setTitle("Are you Sure!")
                .setMessage("The data will be completly deleted!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        productsRef.child(allItems.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    firebaseRecyclerAdapter.notifyItemRemoved(position);
                                    allItems.remove(position);
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Inventory_Activity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        });

                     //   Toast.makeText(Inventory_Activity.this,allItems.get(position), Toast.LENGTH_SHORT).show();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseRecyclerAdapter.notifyDataSetChanged();
                    }
                })
                .show()
                .setCancelable(false);






    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {


        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                .addSwipeLeftBackgroundColor(ContextCompat.getColor(Inventory_Activity.this,R.color.colorRed))
                .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                .create()
                .decorate();



        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }
};



    private void displayItems() {

        final Query firebaseSearchQuery = productsRef.orderByChild("company_id").equalTo(mCurrentUser.getUid());

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(firebaseSearchQuery, Products.class)
                .build();

               firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {




            @Override
            public void onDataChanged() {
                super.onDataChanged();

                ownProgressDialog.setVisibility(View.GONE);
               // dismissProgressDialog();
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, final int i, @NonNull final Products products) {



//this is for counting the values
                firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snap: dataSnapshot.getChildren()) {

                            countItem= (int) snap.getChildrenCount();


                        }

                        if (countItem==0){
                            notFoundLayout.setVisibility(View.VISIBLE);
                        }

                        else {notFoundLayout.setVisibility(View.GONE);}

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //item added to an array for delete purpose
                allItems.add(products.getProductId());


                if (!products.getUpload_status().equals("ok")) {
                    productViewHolder.errorImage.setVisibility(View.VISIBLE);
                } else {
                    productViewHolder.errorImage.setVisibility(View.GONE);
                }

                productViewHolder.productName.setText(products.getName());
                Picasso.with(getApplicationContext()).load(products.getThumb_image()).placeholder(R.drawable.ic_baseline_image_search_24).into(productViewHolder.itemImageView);
                productViewHolder.info1.setVisibility(View.VISIBLE);
                productViewHolder.info1.setText("Parent Category:"+products.getParent_category());

                productViewHolder.info2.setVisibility(View.VISIBLE);
                productViewHolder.info2.setText("Child Category:"+products.getProduct_type());

                productViewHolder.info3.setVisibility(View.VISIBLE);
                productViewHolder.info3.setText("Id:"+products.getProductId());


                productViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                      if (stateCheck){
                            if (products.getProductId() != null) {

                                // Toast.makeText(Inventory_Activity.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                                if (!products.getUpload_status().equals("ok")) {


                                    new AlertDialog.Builder(Inventory_Activity.this)
                                            .setIcon(R.drawable.ic_baseline_warning_24)
                                            .setTitle("The Data is not properly added!")
                                            .setMessage("You can not update this item ,You have to add more details to it.")
                                            .setPositiveButton("ADD DETAILS", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {


                                                    if (products.getProduct_type().equals("Jewellery")) {


                                                        Intent intent = new Intent(getApplicationContext(), Jewellery.class);
                                                        intent.putExtra("itemId", products.getProductId());
                                                        intent.putExtra("childCategory", products.getProduct_type());
                                                        intent.putExtra("parentCategory", products.getParent_category());
                                                        startActivity(intent);

                                                    } else if (products.getParent_category().equals("Furniture") | products.getParent_category().equals("Home Accessories")) {
                                                        Intent intent = new Intent(getApplicationContext(), others_details.class);
                                                        intent.putExtra("itemId", products.getProductId());
                                                        intent.putExtra("childCategory", products.getProduct_type());
                                                        intent.putExtra("parentCategory", products.getParent_category());
                                                        startActivity(intent);
                                                    }
                                                    else if (products.getParent_category().equals("Stationary") | products.getParent_category().equals("Fashion Accessories")) {
                                                        Intent intent = new Intent(getApplicationContext(), Stationery.class);
                                                        intent.putExtra("itemId", products.getProductId());
                                                        intent.putExtra("childCategory", products.getProduct_type());
                                                        intent.putExtra("parentCategory", products.getParent_category());
                                                        startActivity(intent);
                                                    }




                                                    else {
                                                        Intent intent = new Intent(getApplicationContext(), ColorSizeAddingActivity.class);
                                                        intent.putExtra("itemId", products.getProductId());
                                                        intent.putExtra("childCategory", products.getProduct_type());
                                                        intent.putExtra("parentCategory", products.getParent_category());
                                                        startActivity(intent);
                                                    }

                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    firebaseRecyclerAdapter.notifyDataSetChanged();
                                                }
                                            })
                                            .show()
                                            .setCanceledOnTouchOutside(false);

                                } else {

                                    Intent intent = new Intent(getApplicationContext(), UpdateProductDetails.class);
                                    intent.putExtra("itemName", products.getName());
                                    intent.putExtra("itemId", products.getProductId());
                                    intent.putExtra("childCategory", products.getProduct_type());
                                    intent.putExtra("parentCategory", products.getParent_category());
                                    startActivity(intent);
                                }


                            } else {
                                Toast.makeText(Inventory_Activity.this, "Id Not Found", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                          Toast.makeText(Inventory_Activity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
                      }

                    }
                });


            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_product_single_item, parent, false);
                ProductViewHolder productViewHolder = new ProductViewHolder(view);

                return productViewHolder;
            }
        };
        uploadedItemView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        itemTouchHelper.attachToRecyclerView(uploadedItemView);

    }
//class for the view holder

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productName,info1,info2,info3;
        ImageView itemImageView, errorImage;
        View mview;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
            info1=itemView.findViewById(R.id.info1);
            info2=itemView.findViewById(R.id.info2);
            info3=itemView.findViewById(R.id.info3);
            itemImageView=itemView.findViewById(R.id.productsImage);
            productName = itemView.findViewById(R.id.itemName);
            errorImage = itemView.findViewById(R.id.errorSign);

        }
    }


    public void addProducts(View view) {
        Intent intent = new Intent(getApplicationContext(), SelectItemActivity.class);

        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            displayItems();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//inflating the menu
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.user_info) {

            Intent intent = new Intent(getApplicationContext(), Saller_Information.class);
            startActivity(intent);

        }

        if (item.getItemId() == R.id.orderd_Products) {

            Intent intent = new Intent(getApplicationContext(), OrderedItemActivity.class);
            startActivity(intent);

        }
        if (item.getItemId() == R.id.returned_products) {

            Intent intent = new Intent(getApplicationContext(), ReturnedItemActivity.class);
            startActivity(intent);
        }

        return true;
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
        private void showDialog()
        {
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
            alert.setCancelable(true);
            alert.show();
        }






}