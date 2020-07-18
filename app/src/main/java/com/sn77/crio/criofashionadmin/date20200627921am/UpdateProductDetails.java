
/*
* Coding done by Abhisek Seal, 17/07/2020,Kharagpur
* Basically in our case the sizes are depends on the color,
* So, we have created two methods one for display color displayColor() and one for size displaySize()
* so displayColor() is called in Oncreate method and dispalySize() inside display color
* and inside our displaySize() we call the method to dispaly our desire output

* */

package com.sn77.crio.criofashionadmin.date20200627921am;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UpdateProductDetails extends AppCompatActivity {

    private DatabaseReference productRef;
    private TextView itemName,itemId;
    private RecyclerView colorRecyclerView;
    private RecyclerView sizeRecyclerView;
    String itemIdIntent,colorNameSelector,sizeNameSelector,firstColor; //colorSelector is for selecting the color form the radio buttons and sizeNameSelector for size
    int lastSelectedPositionColor = 0;
    int lastSelectedPositionSize = -1;
   TextView no_of_items_Text,itemPrice;
    ArrayList<String> colorArrayList;
    EditText incrementField,decrementField,incrementFieldPrice,decrementFieldPrice;
    Long no_of_pieces,priceItem;
    Animation animation,animationSlide;
    Button incrementButton,decrementButton,incrementButtonPrice,decrementButtonPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product_details);
        itemName=findViewById(R.id.itemName);
        itemId=findViewById(R.id.itemId);
        no_of_items_Text=findViewById(R.id.noOfItems);

        incrementField=findViewById(R.id.incrementField);
        incrementButton=findViewById(R.id.incrementButton);


        decrementButton=findViewById(R.id.decrementButton);
        decrementField=findViewById(R.id.decrementField);


        incrementFieldPrice=findViewById(R.id.incrementFieldPrice);
        incrementButtonPrice=findViewById(R.id.incrementButtonPrice);


        decrementButtonPrice=findViewById(R.id.decrementButtonPrice);
        decrementFieldPrice=findViewById(R.id.decrementFieldPrice);


        animation= AnimationUtils.loadAnimation(UpdateProductDetails.this,R.anim.slide_in_right);
        animation= AnimationUtils.loadAnimation(UpdateProductDetails.this,R.anim.slide_in_left);
        itemPrice=findViewById(R.id.itemPrice);
        itemName.setText(getIntent().getStringExtra("itemName"));
        itemIdIntent=getIntent().getStringExtra("itemId");
        itemId.setText(itemIdIntent);
        colorArrayList=new ArrayList<>();

        productRef= FirebaseDatabase.getInstance().getReference().child("products");

        colorRecyclerView=findViewById(R.id.recyclerViewForColors);
        colorRecyclerView.hasFixedSize();
        colorRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL,false));//setting layout for colors


        sizeRecyclerView=findViewById(R.id.recyclerViewForSizes);
        sizeRecyclerView.hasFixedSize();
        sizeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));//setting layout for sizes

        displayColor(); //for displaying the colors

    }


    private void displayColor() {

        // adapter creation for colors
        DatabaseReference colorRef=productRef.child(itemIdIntent).child("color_details");

        FirebaseRecyclerOptions<Colors> options=new FirebaseRecyclerOptions.Builder<Colors>()
                .setQuery(colorRef,Colors.class)
                .build();



        FirebaseRecyclerAdapter<Colors,ColorsAndSizeViewHolder> colorsRecyclerAdapter=new FirebaseRecyclerAdapter<Colors, ColorsAndSizeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ColorsAndSizeViewHolder colorsAndSizeViewHolder, final int i, @NonNull final Colors colors) {

                colorsAndSizeViewHolder.valueTextView.setText(colors.getColor_name());
                colorsAndSizeViewHolder.valueTextView.setTextColor(Color.parseColor(colors.getColor_code()));



                colorArrayList.add(colors.getColor_name());//this array actually stores the colors
                firstColor=colorArrayList.get(0);//firstColor stores the very first color of the list, this is done because at the starting time the colorSelector will be null;

                colorsAndSizeViewHolder.selectorButton.setChecked(lastSelectedPositionColor == i);//selecting the desire positon of recycler view
                colorsAndSizeViewHolder.selectorButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        lastSelectedPositionColor =i;
                        notifyDataSetChanged();
                        colorNameSelector=colors.getColor_name();// getting the selected color , note: at the very first time it will be null because no click is occoured!! ;)
                        lastSelectedPositionSize=-1;
                        no_of_items_Text.setText("");
                        itemPrice.setText("");
                        displaySize();//calling to display size when some item clicked

                    }
                });
                displaySize();// calling to display size very first time if you remove this you will not show any size when the activity was started

            }

            @NonNull
            @Override
            public ColorsAndSizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.color_size_item_list,parent,false);
                ColorsAndSizeViewHolder colorsAndSizeViewHolder=new ColorsAndSizeViewHolder(view);
                return colorsAndSizeViewHolder;
            }
        };
        colorRecyclerView.setAdapter(colorsRecyclerAdapter);
        colorsRecyclerAdapter.startListening();
    }


    private void displaySize() {

        String colorName;

        if (colorNameSelector == null) {
                colorName = firstColor;
        } else {

            colorName=colorNameSelector;

        }
            DatabaseReference sizeRef = productRef.child(itemIdIntent).child("color_details").child(colorName).child("size");

            FirebaseRecyclerOptions<SizesItems> sizeoption = new FirebaseRecyclerOptions.Builder<SizesItems>()
                    .setQuery(sizeRef, SizesItems.class)
                    .build();

            FirebaseRecyclerAdapter<SizesItems, SizeViewHolder> sizeFirebaseAdapter = new FirebaseRecyclerAdapter<SizesItems, SizeViewHolder>(sizeoption) {
                @Override
                protected void onBindViewHolder(@NonNull SizeViewHolder sizeViewHolder, final int i, @NonNull final SizesItems sizesItems) {
                    sizeViewHolder.valueTextViewSize.setText(sizesItems.getSize());


                    sizeViewHolder.selectorButtonSize.setChecked(lastSelectedPositionSize == i);
                    sizeViewHolder.selectorButtonSize.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            lastSelectedPositionSize = i;
                            notifyDataSetChanged();
                            sizeNameSelector = sizesItems.getSize();//getting the size from the redioButtons


                            if (colorNameSelector==null) {
                                changeFild(firstColor, sizeNameSelector);//this is because colorNameSelector will be null at the very first time but firstColor contains the first color
                            }
                            else {
                                changeFild(colorNameSelector, sizeNameSelector);//calling to display the output
                            }
                        }
                    });

                }

                @NonNull
                @Override
                public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_size_item_list, parent, false);
                    SizeViewHolder sizeViewHolder = new SizeViewHolder(view);

                    return sizeViewHolder;
                }
            };

            sizeRecyclerView.setAdapter(sizeFirebaseAdapter);
            sizeFirebaseAdapter.startListening();

    }

    //In this method we can get any thing inside the size///
    private void changeFild(final String colorNameSelector, final String sizeNameSelector) {


        if (!colorNameSelector.equals(null) | !sizeNameSelector.equals(null)){


        productRef.child(itemIdIntent).child("color_details").child(colorNameSelector).child("size").child(sizeNameSelector).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SizesItems sizesItems=dataSnapshot.getValue(SizesItems.class);

                no_of_pieces=sizesItems.getNo_of_pices();
                priceItem=sizesItems.getPrice();
                no_of_items_Text.setText("No of item available: "+no_of_pieces.toString());
                itemPrice.setText("Price: "+priceItem.toString());
// this is for increment items

                incrementButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long numberOfitem= Long.parseLong(incrementField.getText().toString());



                        productRef.child(itemIdIntent).child("color_details").child(colorNameSelector).child("size").child(sizeNameSelector).child("no_of_pices")
                                .setValue(numberOfitem+no_of_pieces).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(UpdateProductDetails.this, "Updated", Toast.LENGTH_SHORT).show();
                                incrementField.setText("");

                            }
                        });
                    }
                });


// this is for decrement items
                decrementButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long givenNumberOfitem= Long.parseLong(decrementField.getText().toString());


                        if (no_of_pieces>givenNumberOfitem){
                        productRef.child(itemIdIntent).child("color_details").child(colorNameSelector).child("size").child(sizeNameSelector).child("no_of_pices")
                                .setValue(no_of_pieces-givenNumberOfitem).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(UpdateProductDetails.this, "Updated", Toast.LENGTH_SHORT).show();
                                decrementField.setText("");

                            }
                        });
                    }else {

                            Toast.makeText(UpdateProductDetails.this, "Only "+no_of_pieces+" left", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


// increment Price

                incrementButtonPrice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long givenPrice= Long.parseLong(incrementFieldPrice.getText().toString());



                        productRef.child(itemIdIntent).child("color_details").child(colorNameSelector).child("size").child(sizeNameSelector).child("price")
                                .setValue(givenPrice+priceItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(UpdateProductDetails.this, "Updated", Toast.LENGTH_SHORT).show();
                                incrementFieldPrice.setText("");

                            }
                        });
                    }
                });


// this is for decrement Price
                decrementButtonPrice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long givenPrice= Long.parseLong(decrementFieldPrice.getText().toString());



                        if (priceItem>givenPrice){
                            productRef.child(itemIdIntent).child("color_details").child(colorNameSelector).child("size").child(sizeNameSelector).child("price")
                                    .setValue(priceItem-givenPrice).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(UpdateProductDetails.this, "Updated", Toast.LENGTH_SHORT).show();
                                    decrementFieldPrice.setText("");

                                }
                            });
                        }else {

                            Toast.makeText(UpdateProductDetails.this, "Value must be lesser than"+priceItem, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            //up to this


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        }else

            {

            Toast.makeText(this, "Some Error !", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    protected void onStart() {
        super.onStart();



    }

    public void addColorClicked(View view) {

        Intent intent=new Intent(getApplicationContext(),ColorSizeAddingActivity.class);
        startActivity(intent);



    }


    public static  class ColorsAndSizeViewHolder extends RecyclerView.ViewHolder{//for colors

        RadioButton selectorButton;
        TextView valueTextView;
        View mView;
        public ColorsAndSizeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            selectorButton=itemView.findViewById(R.id.selectorButton);
            valueTextView=itemView.findViewById(R.id.valueTextView);



        }
    }

    public static  class SizeViewHolder extends RecyclerView.ViewHolder{//for sizes

        RadioButton selectorButtonSize;
        TextView valueTextViewSize;
        View mViewSize;
        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            mViewSize=itemView;
            selectorButtonSize=itemView.findViewById(R.id.selectorButton);
            valueTextViewSize=itemView.findViewById(R.id.valueTextView);



        }
    }
}