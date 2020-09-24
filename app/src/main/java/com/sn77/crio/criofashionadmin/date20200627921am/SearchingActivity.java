package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SearchingActivity extends AppCompatActivity {


    EditText searchItem;
    ImageButton searchButton;
    RecyclerView searchItemsRecyclerView;
    DatabaseReference rootRef;
    FirebaseUser mCurrentUser;
    String companyId,productId;
    FirebaseAuth mAuth;
    RelativeLayout ownProgressDialog;
    private RelativeLayout notFoundLayout;


    Boolean check = false;
    Boolean stateCheck = true;

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
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(R.layout.activity_searching);

        getApplicationContext().registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));//CHECKING CONNECTIVITY

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);


        getSupportActionBar().hide();
        searchItem=findViewById(R.id.searchItemName);
        searchButton=findViewById(R.id.searchButtonClicked);
        searchItemsRecyclerView=findViewById(R.id.searchItemRecyclerView);
        rootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        ownProgressDialog = findViewById(R.id.ProgressDialog);
        notFoundLayout = findViewById(R.id.notFoundLayout);


        searchItemsRecyclerView.hasFixedSize();
        searchItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        rootRef.child("company_details").child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("seller_id")){

                    CompanyDetails companyDetails=dataSnapshot.getValue(CompanyDetails.class);
                    companyId=companyDetails.getSeller_id();
                }

                else {
                    Toast.makeText(SearchingActivity.this, "error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        productId=companyId+searchItem.getText().toString();

      searchItem.requestFocus();
        searchItem.setCursorVisible(true);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                generateSeach();
               // Toast.makeText(SearchingActivity.this,companyId+searchItem.getText().toString() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateSeach() {

        if (!searchItem.getText().toString().equals("")){

            ownProgressDialog.setVisibility(View.VISIBLE);
            notFoundLayout.setVisibility(View.VISIBLE);
             String id = companyId + searchItem.getText().toString();
            final Query firebaseSearchQuery = rootRef.child("products").orderByChild("productId").startAt(id).endAt(id+"\uf8ff");

            FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                    .setQuery(firebaseSearchQuery, Products.class)
                    .build();

            FirebaseRecyclerAdapter<Products, ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {

                @Override
                public void onDataChanged() {
                    super.onDataChanged();

                    ownProgressDialog.setVisibility(View.GONE);
                    // dismissProgressDialog();
                }

                @Override
                protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, final int i, @NonNull final Products products) {

                    notFoundLayout.setVisibility(View.GONE);


                    if (!products.getUpload_status().equals("ok")) {
                        productViewHolder.errorImage.setVisibility(View.VISIBLE);
                    } else {
                        productViewHolder.errorImage.setVisibility(View.GONE);
                    }

                    productViewHolder.productName.setText(products.getName());
                    Picasso.get().load(products.getThumb_image()).placeholder(R.drawable.ic_baseline_image_search_24).into(productViewHolder.itemImageView);
                    productViewHolder.info1.setVisibility(View.VISIBLE);
                    productViewHolder.info1.setText("Parent Category:" + products.getParent_category());

                    productViewHolder.info2.setVisibility(View.VISIBLE);
                    productViewHolder.info2.setText("Child Category:" + products.getProduct_type());

                    productViewHolder.info3.setVisibility(View.VISIBLE);
                    productViewHolder.info3.setText("Id:" + products.getProductId());


                    productViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (stateCheck) {
                                if (products.getProductId() != null) {

                                    // Toast.makeText(Inventory_Activity.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                                    if (products.getUpload_status().equals("ok")) {

                                        Intent intent = new Intent(getApplicationContext(), UpdateProductDetails.class);
                                        intent.putExtra("itemName", products.getName());
                                        intent.putExtra("itemId", products.getProductId());
                                        intent.putExtra("childCategory", products.getProduct_type());
                                        intent.putExtra("parentCategory", products.getParent_category());
                                        startActivity(intent);
                                    }


                                } else {
                                    Toast.makeText(SearchingActivity.this, "Id Not Found", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(SearchingActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
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
            searchItemsRecyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();
        }

      else {
            Toast.makeText(this, "Give The Product id first", Toast.LENGTH_SHORT).show();
        }


        //.makeText(this, String.valueOf(countItem), Toast.LENGTH_SHORT).show();


    }
    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productName, info1, info2, info3;
        ImageView itemImageView, errorImage;
        View mview;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
            info1 = itemView.findViewById(R.id.info1);
            info2 = itemView.findViewById(R.id.info2);
            info3 = itemView.findViewById(R.id.info3);
            itemImageView = itemView.findViewById(R.id.productsImage);
            productName = itemView.findViewById(R.id.itemName);
            errorImage = itemView.findViewById(R.id.errorSign);

        }
    }

}