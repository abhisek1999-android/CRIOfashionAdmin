package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ReturnedItemActivity extends AppCompatActivity {

    private RecyclerView returnItemListView;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseUser mCurrentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returned_item);
        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();

        mCurrentUser=mAuth.getCurrentUser();

        returnItemListView=findViewById(R.id.returnedItemListView);
        returnItemListView.hasFixedSize();
        returnItemListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

    }

    @Override
    protected void onStart() {
        super.onStart();
       DatabaseReference firebaseSearchQuery = rootRef.child("company_details").child(mCurrentUser.getUid()).child("returned_products");

        FirebaseRecyclerOptions<ReturnedItem> options=new FirebaseRecyclerOptions.Builder<ReturnedItem>()
                .setQuery(firebaseSearchQuery,ReturnedItem.class)
                .build();

        FirebaseRecyclerAdapter<ReturnedItem,ReturnedItemViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ReturnedItem, ReturnedItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReturnedItemViewHolder returnedItemViewHolder, int i, @NonNull ReturnedItem returnedItem) {

                returnedItemViewHolder.productName.setText(returnedItem.getOrder_id().toString());

            }

            @NonNull
            @Override
            public ReturnedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_product_single_item,parent,false);
                ReturnedItemActivity.ReturnedItemViewHolder returnedItemViewHolder=new ReturnedItemViewHolder(view);

                return returnedItemViewHolder;
            }
        };returnItemListView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }


    public static class ReturnedItemViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        ImageView itemImageView;
        View mview;

        public ReturnedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
            productName=itemView.findViewById(R.id.itemName);

        }
    }
}