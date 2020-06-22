package com.example.midtrans;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TransactionFinishedCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMidtransSDK();
    }

    private void initMidtransSDK() {
        SdkUIFlowBuilder.init()
                .setContext(this)
                .setMerchantBaseUrl(BuildConfig.BASE_URL)
                .setClientKey(BuildConfig.CLIENT_KEY)
                .setTransactionFinishedCallback(this)
                .enableLog(true)
                .setColorTheme(new CustomColorTheme("#777777","#f77474" , "#3f0d0d"))
                .buildSDK();
    }

    public void payPressed(View view) {
        EditText priceText = (EditText) findViewById(R.id.priceText);
        int total = 1;
        if (priceText.length() != 0) {
            total = Integer.valueOf(priceText.getText().toString());
        }
        MidtransSDK.getInstance().setTransactionRequest(transactionRequest(total));
        MidtransSDK.getInstance().startPaymentUiFlow(this);
    }

    TransactionRequest transactionRequest(int price) {
        CustomerDetails cd = new CustomerDetails();
        cd.setFirstName("Fred");
        cd.setPhone("085945152252");
        cd.setEmail("fred@gmail.com");

        TransactionRequest request = new TransactionRequest(System.currentTimeMillis()+"", price);
        request.setCustomerDetails(cd);

//        ArrayList<ItemDetails> itemDetails = new ArrayList<>();
//        ItemDetails details = new ItemDetails(id, price, qty, name);
//        itemDetails.add(details);
//        request.setItemDetails(itemDetails);

        CreditCard creditCard =  new CreditCard();
        creditCard.setSaveCard(false);
        creditCard.setAuthentication(CreditCard.AUTHENTICATION_TYPE_RBA);
        request.setCreditCard(creditCard);

        return request;
    }

    @Override
    public void onTransactionFinished(TransactionResult result) {
        if(result.getResponse() != null){
            switch (result.getStatus()){
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(this, "Transaction finished : "+result.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaction pending : "+result.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaction failed : "+result.getResponse().getTransactionId(), Toast.LENGTH_SHORT).show();
                    break;
            }

            result.getResponse().getValidationMessages();
        }
        else if(result.isTransactionCanceled()) {
            Toast.makeText(this, "Transaction canceled", Toast.LENGTH_SHORT).show();
        }
        else {
            if (result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this, "Transaction invalid", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Transaction finished with failure", Toast.LENGTH_SHORT).show();
            }
        }
    }
}