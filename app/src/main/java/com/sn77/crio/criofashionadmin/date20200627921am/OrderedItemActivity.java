package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OrderedItemActivity extends AppCompatActivity {


    private RecyclerView orderedItemListView;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseUser mCurrentUser;

    RelativeLayout ownProgressDialog;
    private RelativeLayout notFoundLayout;

    int countItem = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Ordered Items");
        setContentView(R.layout.activity_ordered_item);
        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();


        ownProgressDialog = findViewById(R.id.ownProgressDialog);
        notFoundLayout = findViewById(R.id.notFoundLayout);

        mCurrentUser=mAuth.getCurrentUser();

        orderedItemListView=findViewById(R.id.orderedItemListView);
        orderedItemListView.hasFixedSize();
        orderedItemListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));



        if (mCurrentUser != null) {

            Query firebaseSearchQuery =  rootRef.child("company_details").child(mCurrentUser.getUid()).child("ordered_products");

            firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        countItem = (int) snap.getChildrenCount();

                    }

                    if (countItem == 0) {
                        notFoundLayout.setVisibility(View.VISIBLE);
                    } else {
                        notFoundLayout.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference firebaseSearchQuery = rootRef.child("company_details").child(mCurrentUser.getUid()).child("ordered_products");

        FirebaseRecyclerOptions<OrderedItem> options=new FirebaseRecyclerOptions.Builder<OrderedItem>()
                .setQuery(firebaseSearchQuery,OrderedItem.class)
                .build();

        FirebaseRecyclerAdapter<OrderedItem, OrderedItemViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<OrderedItem, OrderedItemViewHolder>(options) {


            @Override
            public void onDataChanged() {
                super.onDataChanged();

                ownProgressDialog.setVisibility(View.GONE);
                // dismissProgressDialog();
            }



            @Override
            protected void onBindViewHolder(@NonNull final OrderedItemViewHolder orderedItemViewHolder, int i, @NonNull final OrderedItem orderdItem) {


                firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {

                            countItem = (int) snap.getChildrenCount();


                        }

                        if (countItem == 0) {
                            notFoundLayout.setVisibility(View.VISIBLE);
                        } else {
                            notFoundLayout.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                orderedItemViewHolder.orderId.setText(orderdItem.getOrder_id());
                orderedItemViewHolder.customerName.setText("Customer Name: "+orderdItem.getCustomer_name());
                orderedItemViewHolder.productId.setText("Product Id: "+orderdItem.getProduct_id());
                orderedItemViewHolder.phoneNumber.setText("Customer Phone No: "+orderdItem.getCustomer_phone_number());

                orderedItemViewHolder.downloadInvoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(OrderedItemActivity.this,orderdItem.getCustomer_name() , Toast.LENGTH_SHORT).show();
                    }
                });
                
                
                
            }

            @NonNull
            @Override
            public OrderedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ordred_single_item,parent,false);
                OrderedItemViewHolder orderedItemViewHolder=new OrderedItemViewHolder(view);

                return orderedItemViewHolder;

            }
        };orderedItemListView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class OrderedItemViewHolder extends RecyclerView.ViewHolder {

        TextView orderId,customerName,productId,phoneNumber;
        CardView downloadInvoice;
        ImageView itemImageView;
        View mview;

        public OrderedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
           orderId=itemView.findViewById(R.id.itemName);
           customerName=itemView.findViewById(R.id.info1);
           productId=itemView.findViewById(R.id.info2);
           phoneNumber=itemView.findViewById(R.id.info3);
           downloadInvoice=itemView.findViewById(R.id.DownloadInvoice);

        }
    }
}