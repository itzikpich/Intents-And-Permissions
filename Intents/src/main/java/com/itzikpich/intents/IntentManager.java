package com.itzikpich.intents;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.URLEncoder;


public class IntentManager {

    private static final int CALL_PHONE_PERMISSION = 123;
    private static final int SEND_SMS_PERMISSION = 456;
    private static final int READ_CONTACTS_PERMISSION = 789;

    AppCompatActivity activity;
    ActivityResultLauncher<Intent> getContactResultLauncher;

    public IntentManager(AppCompatActivity activity) {
        this.activity = activity;

        getContactResultLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        getContactByResult(result);
                    }
                });

    }

    private void getContactByResult(ActivityResult result) {
        Intent data = result.getData();
        try {
            Cursor cursor = null;
            String phoneNo = null;
            String name = null;
            Uri uri = data.getData();
            cursor = activity.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int  phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int  nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            cursor.close();
            Log.e("Name and Contact number is",name+","+phoneNo);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSms() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION);
        } else {
            startSmsActivity();
        }
    }

    public void startSmsActivity() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+9720504555575", null, "sms message", null, null);

//        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//        sendIntent.setDataAndType(Uri.parse("smsto:"), "vnd.android-dir/mms-sms");
//        sendIntent.putExtra("sms_body", "sms message");
//        sendIntent.putExtra("address"  , new String("0504555575"));//;0523078191"));
//        startActivity(sendIntent);
    }

    public void startAppMsg() {
        /**
         * this open app, for instance Whatsapp, fills the message input,  then you must select contact to send the message
         */
        String message = " message you want to share..";
        String number = "+9720523078191";
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.setPackage(getAppPackage());
//        intent.putExtra(Intent.EXTRA_TEXT, message);
//        activity.startActivity(Intent.createChooser(intent, "Send with"));

        /**
         * this open app, for instance Whatsapp, enters user screen, fills the message input, then you must select press send button
         */
        PackageManager packageManager = activity.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage(getAppPackage());
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                activity.startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAppPackage() {
        return "com.whatsapp";
//        "com.linkedin.android";
//        "com.twitter.android";
//        "com.facebook.katana";
//        "com.google.android.apps.plus";
    }

    public void call() {
        if ( ContextCompat.checkSelfPermission( activity, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( activity, new String[] {  android.Manifest.permission.CALL_PHONE  }, CALL_PHONE_PERMISSION );
        } else {
            startCallActivity();
        }
    }

    public void startCallActivity() {
//        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0523078191")); // naviagte to calls activity
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:0523078191")); // direct call
        activity.startActivity(intent);
    }

    public void getContact() {
        if ( ContextCompat.checkSelfPermission( activity, Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( activity, new String[] {  android.Manifest.permission.READ_CONTACTS  }, READ_CONTACTS_PERMISSION );
        } else {
            startContactActivity();
        }
    }

    public void startContactActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        getContactResultLauncher.launch(intent);
    }

}
