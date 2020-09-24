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
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class ReturnedItemActivity extends AppCompatActivity {

    private RecyclerView returnItemListView;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseUser mCurrentUser;

    RelativeLayout ownProgressDialog;
    private RelativeLayout notFoundLayout;

    int countItem = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Returned Items");
        setContentView(R.layout.activity_returned_item);
        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();

        mCurrentUser=mAuth.getCurrentUser();

        ownProgressDialog = findViewById(R.id.ownProgressDialog);
        notFoundLayout = findViewById(R.id.notFoundLayout);

        returnItemListView=findViewById(R.id.returnedItemListView);
        returnItemListView.hasFixedSize();
        returnItemListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));


        if (mCurrentUser != null) {

            Query firebaseSearchQuery = rootRef.child("company_details").child(mCurrentUser.getUid()).child("returned_products");

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
       final DatabaseReference firebaseSearchQuery = rootRef.child("company_details").child(mCurrentUser.getUid()).child("returned_products");

        FirebaseRecyclerOptions<ReturnedItem> options=new FirebaseRecyclerOptions.Builder<ReturnedItem>()
                .setQuery(firebaseSearchQuery,ReturnedItem.class)
                .build();

        FirebaseRecyclerAdapter<ReturnedItem,ReturnedItemViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ReturnedItem, ReturnedItemViewHolder>(options) {

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                ownProgressDialog.setVisibility(View.GONE);
                // dismissProgressDialog();
            }


            @Override
            protected void onBindViewHolder(@NonNull ReturnedItemViewHolder returnedItemViewHolder, int i, @NonNull ReturnedItem returnedItem) {




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


                returnedItemViewHolder.productName.setText(returnedItem.getOrder_id().toString());

            }

            @NonNull
            @Override
            public ReturnedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_product_single_item,parent,false);
                ReturnedItemViewHolder returnedItemViewHolder=new ReturnedItemViewHolder(view);

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