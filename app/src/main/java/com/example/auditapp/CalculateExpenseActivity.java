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

public class CalculateExpenseActivity extends AppCompatActivity
{

    DatabaseReference mSavingAccountRef,mExpenseRef;
    String currentUserId;
    FirebaseAuth mAuth;
    int totalBankBalanceStr = 0;
    int totalExpenseStr = 0;
    TextView expenseValue,bankValue;
    private String refUid, googleup, bankup, savingsup, phonepeup, paytmup;
    private String billup, clothup, entertainup, foodup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_expense);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        mSavingAccountRef = FirebaseDatabase.getInstance().getReference().child("MySavings").child(currentUserId);
        mExpenseRef = FirebaseDatabase.getInstance().getReference().child("MySavings").child(currentUserId).child("Expenses");

        expenseValue = (TextView) findViewById(R.id.expenseBalance);
        bankValue = (TextView) findViewById(R.id.bankBalance);

        SowBalanceBankTotal();
        SowBalanceExpenseTotal();
    }

    private void SowBalanceBankTotal()
    {
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

                    int totalBankBalanceInt = ((Integer.valueOf(bankup)) + (Integer.valueOf(googleup)) + (Integer.valueOf(paytmup)) + (Integer.valueOf(phonepeup)) + (Integer.valueOf(savingsup)));
                    totalBankBalanceStr = totalBankBalanceStr + totalBankBalanceInt;

                    //THIS THE LOGIC TO SUM-UP THE TOTAL SAVINGS AMOUNT
                    bankValue.setText(String.valueOf(totalBankBalanceStr));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }

    private void SowBalanceExpenseTotal()
    {
        mExpenseRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    billup = dataSnapshot.child("Bill Expense").getValue().toString();
                    clothup = dataSnapshot.child("Cloth Expense").getValue().toString();
                    entertainup = dataSnapshot.child("Entertainment Expense").getValue().toString();
                    foodup = dataSnapshot.child("Food Expense").getValue().toString();
                    /*savingsup = dataSnapshot.child("Savings Amount").getValue().toString();
                    bank.setText(bankup);
                    google.setText(googleup);
                    paytmre.setText(paytmup);
                    phonepe.setText(phonepeup);
                    savings.setText(savingsup);*/

                    int totalExpenseInt = ((Integer.valueOf(billup)) + (Integer.valueOf(clothup)) + (Integer.valueOf(entertainup)) + (Integer.valueOf(foodup)));
                    totalExpenseStr = totalExpenseStr + totalExpenseInt;

                    //THIS THE LOGIC TO SUM-UP THE TOTAL SAVINGS AMOUNT
                    expenseValue.setText(String.valueOf(totalExpenseStr));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            { }
        });
    }
}
