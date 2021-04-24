package com.pkan.official.payments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.pkan.official.R;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

public class PaymentsActivity extends AppCompatActivity {

    final String COMPANY_UPI_ID = "skdbsp123@okhdfcbank";
    final String PAYEE_NAME = "Satyam Kumar";


    // variables to be used
    int amount;
    String payment_for = "";

    String transaction_id = "", transaction_ref_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

    }

    private void pay () {
        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa(COMPANY_UPI_ID)
                .setPayeeName(PAYEE_NAME)
                .setTransactionId(transaction_id)
                .setTransactionRefId(transaction_ref_id)
                .setDescription(payment_for)
                .setAmount(String.valueOf(amount))
                .build();

        easyUpiPayment.startPayment();

        easyUpiPayment.setPaymentStatusListener(new PaymentStatusListener() {
            @Override
            public void onTransactionCompleted(TransactionDetails transactionDetails) {

            }

            @Override
            public void onTransactionSuccess() {

                // alert the user about the status
                Toast.makeText(getApplicationContext(), "Transaction Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionSubmitted() {

                // alert the user about the status
                Toast.makeText(getApplicationContext(), "Transaction Submitted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionFailed() {

                // alert the user about the status
                Toast.makeText(getApplicationContext(), "Transaction Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancelled() {

                // alert the user about the status
                Toast.makeText(getApplicationContext(), "Transaction Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAppNotFound() {

                // alert the user about the status
                Toast.makeText(getApplicationContext(), "App Not Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

}