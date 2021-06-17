package com.itzikpich.intentsandpermissions;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.itzikpich.intents.IntentManager;
import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity {

    private static final int CALL_PHONE_PERMISSION = 123;
    private static final int SEND_SMS_PERMISSION = 456;
    private static final int READ_CONTACTS_PERMISSION = 789;

    IntentManager intentManager = new IntentManager(this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendSms(View view) {
        intentManager.sendSms();
    }

    public void call(View view) {
        intentManager.call();
    }

    public void getContact(View view) {
        intentManager.getContact();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_PHONE_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intentManager.startCallActivity();
                }
                break;
            }
            case SEND_SMS_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intentManager.startSmsActivity();
                }
                break;
            }
            case READ_CONTACTS_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intentManager.startContactActivity();
                }
                break;
            }
        }
    }

    public void sendSMessage(View view) {
        intentManager.startAppMsg();
    }
}
