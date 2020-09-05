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

public class OrderedItemActivity extends AppCompatActivity {


    private RecyclerView orderedItemListView;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseUser mCurrentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_item);
        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();

        mCurrentUser=mAuth.getCurrentUser();

        orderedItemListView=findViewById(R.id.orderedItemListView);
        orderedItemListView.hasFixedSize();
        orderedItemListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference firebaseSearchQuery = rootRef.child("company_details").child(mCurrentUser.getUid()).child("ordered_products");

        FirebaseRecyclerOptions<OrderedItem> options=new FirebaseRecyclerOptions.Builder<OrderedItem>()
                .setQuery(firebaseSearchQuery,OrderedItem.class)
                .build();

        FirebaseRecyclerAdapter<OrderedItem, OrderedItemViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<OrderedItem, OrderedItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderedItemViewHolder orderedItemViewHolder, int i, @NonNull OrderedItem orderdItem) {
                orderedItemViewHolder.productName.setText(orderdItem.getOrder_id());
            }

            @NonNull
            @Override
            public OrderedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_product_single_item,parent,false);
                OrderedItemActivity.OrderedItemViewHolder orderedItemViewHolder=new OrderedItemViewHolder(view);

                return orderedItemViewHolder;

            }
        };orderedItemListView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class OrderedItemViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        ImageView itemImageView;
        View mview;

        public OrderedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
            productName=itemView.findViewById(R.id.itemName);

        }
    }
}