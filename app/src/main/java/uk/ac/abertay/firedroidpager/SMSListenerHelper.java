package uk.ac.abertay.firedroidpager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSListenerHelper extends BroadcastReceiver {
    // Create instance of Main Activity
    MainActivity main = new MainActivity();
    // Define SmsMessage.
    private String smsMesg;
    private String Alert1;
    private String Alert2;
    private Boolean DisableApp = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Set SQLDatabaseHelper as instance with context (From onreceive).
        SQLDatabaseHelper SDH = new SQLDatabaseHelper(context);
        // Get Shared Preferences into string values.
        Alert1 = SharedPreferencesHelper.getSharedPreferenceString(MainActivity.getAppContext(), "AlertKey1", Alert1);
        Alert2 = SharedPreferencesHelper.getSharedPreferenceString(MainActivity.getAppContext(), "AlertKey2", Alert2);
        DisableApp = SharedPreferencesHelper.getSharedPreferenceBoolean(MainActivity.getAppContext(), "DisableSMS", DisableApp);

        // Execute code if SMS_RECEIVED intent.
        String ETAG = "Incoming SMS: ";
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED") && !DisableApp) {
            // Define bundle + Initialize - Get intent extras.
            Bundle smsbundle = intent.getExtras(); // Get SMS message
            SmsMessage[] currSMS;
        // Execute code if not null.
        if (smsbundle != null) {
            // Exception Handling (Try function/Catch)
            try {   // Define + Initialize object and get pdus.
                Object[] smspdu = (Object[]) smsbundle.get("pdus");
                // Prevent possible null pointer exception
                assert smspdu != null;
                currSMS = new SmsMessage[smspdu.length];

                for (int i = 0; i < currSMS.length; i++) {
                    currSMS[i] = SmsMessage.createFromPdu((byte[]) smspdu[i]);
                    //String smsFrom = currSMS[i].getOriginatingAddress();
                    smsMesg += currSMS[i].getMessageBody();
                }

            } catch (Exception e) {
                // Debugging - Exception Handling
                Log.e(ETAG, "Exception caught " + e.getMessage());
                // Set SMS message, incase of error. Prevent further errors. (Due to null message).
                smsMesg = "SMS: Error! Exception ";
            }
            // Remove null word from SMS data into sms string
            String sms = smsMesg.substring(4);
                 // Debug
            Log.i(ETAG, "SQL: SMS Data - " + sms);

            // Check SMS for keyword - From Shared Preferences
            if (sms.contains(Alert1) || sms.contains(Alert2)) {
                // Insert SMS data
                SDH.insertDataDB(sms);
                // Set Alert Activation Variable (aStatus).
                MainActivity.aStatus = true;
                // Start Main Activity - Triggers Alert.
                Intent i = new Intent(context,MainActivity.class);
                // Flag needed (Current context != Activity due to being background service).
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                    } else {
                // If not 911 call, log message and set Alert Status boolean to false. (Prevents alarm).
                Log.i(ETAG, " Not 911 Call");
                    MainActivity.aStatus = false;
                }
            }

        } else {
            // Debug Message - SMS Alerting Disabled
            Log.i(ETAG, " SMS: " + "SMS Alerts Disabled.");
        }
    }


}
