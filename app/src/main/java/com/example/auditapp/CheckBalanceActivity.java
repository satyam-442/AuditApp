package com.example.auditapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckBalanceActivity extends AppCompatActivity {

    DatabaseReference mSavingAccountRef;
    String currentUserId;
    FirebaseAuth mAuth;
    int totalBankBalanceStr = 0;
    private TextView google,paytmre,phonepe,savings,bank,totalBankBalance;
    private String refUid, googleup, bankup, savingsup, phonepeup, paytmup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        mSavingAccountRef = FirebaseDatabase.getInstance().getReference().child("MySavings").child(currentUserId);
        SowBalanceInBank();

        bank = (TextView) findViewById(R.id.bankref);
        savings = (TextView) findViewById(R.id.savingsref);
        phonepe = (TextView) findViewById(R.id.phoneperef);
        paytmre = (TextView) findViewById(R.id.paytmref);
        google = (TextView) findViewById(R.id.googleref);

        totalBankBalance = (TextView) findViewById(R.id.totalBankBalance);

    }

    private void SowBalanceInBank() {
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

    }

}
