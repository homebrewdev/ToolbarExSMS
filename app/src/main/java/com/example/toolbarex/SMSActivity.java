package com.example.toolbarex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class SMSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
    }

    private EditText textPhoneNumber;
    private EditText textSmsBody;

    // обработчик нажатия кнопки - Send SMS (послать SMS)
    public void onclick(View view) throws IOException {
        switch (view.getId()) {
            case R.id.buttonSendSms:
                textSmsBody = (EditText) findViewById(R.id.editSMSText);

                textPhoneNumber = (EditText) findViewById(R.id.editPhoneText);
                //SMS Manager посылает сообщение SMS
                sendSmsByManager(textPhoneNumber.getText().toString(), textSmsBody.getText().toString());
        }
    }

    // функция посылки SMS сообщения с помощью SMS Manager
    public void sendSmsByManager(String phoneNumber, String smsBody) {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    smsBody,
                    null,
                    null);
            Toast.makeText(getApplicationContext(), R.string.SMS_send_Success,
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), R.string.SMS_not_Send,
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}

