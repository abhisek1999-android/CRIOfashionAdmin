package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Inventory_Activity extends AppCompatActivity {
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference productsRef;
    RecyclerView uploadedItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        productsRef= FirebaseDatabase.getInstance().getReference().child("products");
        uploadedItemView=findViewById(R.id.uploaded_item_list);
        uploadedItemView.hasFixedSize();
        uploadedItemView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));



    }

    private void displayItems() {

        Query firebaseSearchQuery = productsRef.orderByChild("company_id").equalTo(mCurrentUser.getUid());

        FirebaseRecyclerOptions<Products> options=new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(firebaseSearchQuery,Products.class)
                .build();




        FirebaseRecyclerAdapter<Products,ProductViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {

                productViewHolder.productName.setText(products.getName());

                productViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(getApplicationContext(),UpdateProductDetails.class);
                        intent.putExtra("itemName",products.getName());
                        intent.putExtra("itemId",products.getProductId());
                        startActivity(intent);


                    }
                });


            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_product_single_item,parent,false);
                ProductViewHolder productViewHolder=new ProductViewHolder(view);

                return productViewHolder;
            }
        };
        uploadedItemView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }
//class for the view holder

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        ImageView itemImageView;
        View mview;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
            productName=itemView.findViewById(R.id.itemName);

        }
    }



    public void addProducts(View view) {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);

        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        else {
            displayItems();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        moveTaskToBack(true);
    }
}