package com.example.fahim.ibuyandsell;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.fahim.ibuyandsell.Model.Products;
import com.example.fahim.ibuyandsell.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


import javax.xml.transform.Templates;

public class ProductDetailsActivity extends AppCompatActivity
{
    private Button addToCartButton,removeProduct,updateProduct;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice, productDescription, productName;
    private String productID = "",productUID="", state = "Normal",userid="";
    SessionManagment session;
    DatabaseReference reference;
    Products products;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        productID = getIntent().getStringExtra("pid");
        productUID = getIntent().getStringExtra("p_userid");
        reference = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        session = new SessionManagment(getApplicationContext());
        addToCartButton = (Button) findViewById(R.id.pd_add_to_cart_button);
        removeProduct = findViewById(R.id.remove_product_btn);
        updateProduct = findViewById(R.id.update_product_btn);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);







        if (productID==null|| productID.isEmpty()){
            Toast.makeText(this, "Product id is empty", Toast.LENGTH_SHORT).show();
        }
        else {
			
            getProductDetails(productID);
        }
        

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               if (productUID.equals(Prevalent.currentOnlineUser.getEmail())){
                   Toast.makeText(ProductDetailsActivity.this, "This product is added by you cannot add to cart", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                   startActivity(intent);
                   finish();
               }
               else {
                   if (state.equals("Order Placed") || state.equals("Order Shipped"))
                   {
                       Toast.makeText(ProductDetailsActivity.this, "you can add purchase more products, once your order is shipped or confirmed.", Toast.LENGTH_LONG).show();
                   }
                   else
                   {
                       addingToCartList();
                   }
               }
                

               
                
            }
        });
        
        removeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				if (productUID.equals(Prevalent.currentOnlineUser.getEmail())){
					removeProduct();
					
				}
				else {
					Toast.makeText(ProductDetailsActivity.this, "This product is not added by you so cannot remove from sale", Toast.LENGTH_LONG).show();
					
				}
            
            }
        });
        
        updateProduct.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (productUID.equals(Prevalent.currentOnlineUser.getEmail())){
					
					updateProduct();
					
				}
				else {
					Toast.makeText(ProductDetailsActivity.this, "This product is not added by you so cannot update Product Detial", Toast.LENGTH_LONG).show();
					
				}
				
			}
		});
    }
	
	private void updateProduct() {
		Intent intent = new Intent(ProductDetailsActivity.this,AdminUpdateProductsActivity.class);
		intent.putExtra("pid",productID);
		startActivity(intent);
		finish();
	}
	
	@Override
    protected void onStart()
    {
        super.onStart();

//        CheckOrderState();
    }
    
    private void removeProduct(){
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProductDetailsActivity.this, "Product is Successfully deleted", Toast.LENGTH_SHORT).show();
                }
        
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
        
            }
        });
    
    
    }

    private void addingToCartList()
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");


        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getEmail())
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getEmail())
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(ProductDetailsActivity.this, "Product Added to Cart", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                intent.putExtra("pUID",productID);
                                                Log.e("ouneeb", "Product Id"+productID );
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }



    private void getProductDetails(final String productID)
    {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    session.storeProductUserEmail(products.getUserid());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }


    private void CheckOrderState()
    {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getEmail());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String shippingState = dataSnapshot.child("state").getValue().toString();

                    if (shippingState.equals("shipped"))
                    {
                        state = "order Shipped";
                    }
                    else if (shippingState.equals("not shipped"))
                    {
                        state = "order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
