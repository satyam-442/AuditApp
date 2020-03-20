package com.example.auditapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity
{
    Dialog dialogframe;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase,reflectref, mSavingAccountRef;
    private CircleImageView SetImage;
    private ImageView add,closeimg,paypal,paytm,visa,phonepay,gpay,sub;
    private TextView google,paytmre,phonepe,savings,bank,totalBankBalance;
    private ProgressDialog loadingBar;
    private FirebaseUser mCurrentUser;
    private String refUid, googleup, bankup, savingsup, phonepeup, paytmup;
    int totalBankBalanceStr = 0;
    Button checkBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dialogframe=new Dialog(this);
        mAuth = FirebaseAuth.getInstance();

        refUid = mAuth.getCurrentUser().getUid();
        reflectref=FirebaseDatabase.getInstance().getReference().child("Bank Amount").child(refUid);

        mSavingAccountRef = FirebaseDatabase.getInstance().getReference().child("MySavings").child(refUid);

        checkBalance = (Button) findViewById(R.id.checkBalance);
        checkBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, CheckBalanceActivity.class));
            }
        });

        bank = (TextView) findViewById(R.id.bankref);
        savings = (TextView) findViewById(R.id.savingsref);
        phonepe = (TextView) findViewById(R.id.phoneperef);
        paytmre = (TextView) findViewById(R.id.paytmref);
        google = (TextView) findViewById(R.id.googleref);

        totalBankBalance = (TextView) findViewById(R.id.totalBankBalance);

        //SowBalanceInBank();

        String currentUserID = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        SetImage=(CircleImageView)findViewById(R.id.main_profile);

        sub=(ImageView)findViewById(R.id.sub);
        sub.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(Dashboard.this,ExpenseActivity.class);
                startActivity(intent);
            }
        });

        mUserDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                /*final String image = dataSnapshot.child("image").getValue().toString();
                if(!image.equals("default"))
                {
                    Picasso.with(Dashboard.this).load(image).placeholder(R.drawable.profiles).into(SetImage);
                    Picasso.with(Dashboard.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profiles).into(SetImage, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(Dashboard.this).load(image).placeholder(R.drawable.profiles).into(SetImage);
                        }
                    });
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });

        add=(ImageView)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               showpay();
            }
        });
    }

    /*private void SowBalanceInBank() {
        //bank=(TextView)findViewById(R.id.bankref);
        mSavingAccountRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    bankup = dataSnapshot.child("Bank Amount").getValue().toString();
                    googleup = dataSnapshot.child("Google Amount").getValue().toString();
                    paytmup = dataSnapshot.child("Paytm Amount").getValue().toString();
                    phonepeup = dataSnapshot.child("PhonePe Amount").getValue().toString();
                    savingsup = dataSnapshot.child("Savings Amount").getValue().toString();
                    bank.setText(bankup);
                    google.setText(googleup);
                    paytmre.setText(paytmup);
                    phonepe.setText(phonepeup);
                    savings.setText(savingsup);

                    //totalBankBalanceStr = bankup + googleup + paytmup + phonepeup + savingsup;
                    int totalBankBalanceInt = ((Integer.valueOf(bankup)) + (Integer.valueOf(googleup)) + (Integer.valueOf(paytmup)) + (Integer.valueOf(phonepeup)) + (Integer.valueOf(savingsup)));
                    totalBankBalanceStr = totalBankBalanceStr + totalBankBalanceInt;

                    //THIS THE LOGIC TO SUM-UP THE TOTAL SAVINGS AMOUNT
                    totalBankBalance.setText(String.valueOf(totalBankBalanceStr));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });

        //totalBankBalanceStr = googleup + paytmup + phonepeup + savingsup + bankup;

        //totalBankBalance.setText(googleup,paytmup,phonepeup,savingsup,bankup);

    }*/

    private void showpay()
    {
        dialogframe.setContentView(R.layout.custom_add_popup);
        closeimg=(ImageView)dialogframe.findViewById(R.id.close);
        closeimg.setOnClickListener(new View.OnClickListener()
        {
            @Override
          public void onClick(View v)
          {
              dialogframe.dismiss();
          }
      });

        paypal=(ImageView) dialogframe.findViewById(R.id.paypal);
        paypal.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Dashboard.this,SavingsActivity.class));
            }
        });

        paytm=(ImageView) dialogframe.findViewById(R.id.paytm);
        paytm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Dashboard.this,PaytmActivity.class));
            }
        });

        visa=(ImageView) dialogframe.findViewById(R.id.visa);
        visa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Dashboard.this,BankActivity.class));
            }
        });

        phonepay=(ImageView) dialogframe.findViewById(R.id.phonepay);
        phonepay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Dashboard.this,PhonePayActivity.class));
            }
        });

        gpay=(ImageView) dialogframe.findViewById(R.id.googlepay);
        gpay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(Dashboard.this,GoogleActivity.class));
            }
        });

        dialogframe.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogframe.show();
    }
}
