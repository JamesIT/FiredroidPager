package uk.ac.abertay.firedroidpager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;

import java.sql.Date;

public class SMSListenerHelper extends BroadcastReceiver {
    // Define/Initialize strings/booleans
    private String smsMesg;
    private String Alert1 = "FIRE";
    private String Alert2 = "EMS";
    private Boolean DisableApp = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Define/initialize debugging ETAG.
        String ETAG = "Incoming SMS: ";

        // Set SQLDatabaseHelper as instance with context (From onreceive).
        SQLDatabaseHelper SDH = new SQLDatabaseHelper(context);

        // Get Shared Preferences into string values.
        Alert1 = SharedPreferencesHelper.getSharedPreferenceString(MainActivity.getAppContext(), "AlertKey1", Alert1);
        Alert2 = SharedPreferencesHelper.getSharedPreferenceString(MainActivity.getAppContext(), "AlertKey2", Alert2);
        DisableApp = SharedPreferencesHelper.getSharedPreferenceBoolean(MainActivity.getAppContext(), "DisableSMS", DisableApp);

        // Run if intent is SMS_RECEIVED and has not been disabled via user settings. (DisableSMS).
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
                smsMesg = "Error";
            }

            // Remove null word from SMS data into sms string
            String sms = smsMesg.substring(4);

            // Check SMS for keyword - From Shared Preferences. Ensure strings are not empty OR null, to prevent error or wrong SMS being passed.
            if (Alert1 != null && !Alert1.isEmpty() && Alert2 != null && !Alert2.isEmpty() && sms.contains(Alert1) || sms.contains(Alert2)) {
                // Insert SMS data
                SDH.insertDataDB(sms);
                // Set Alert Activation Variable (aStatus).
                MainActivity.aStatus = true;
                // Get date/time
                Date currentDate = new Date(System.currentTimeMillis());
                CharSequence s = DateFormat.format("dd-MM-yyyy hh:mm:ss", currentDate.getTime());
                // Store date - String
                final String date = s.toString();
                // Send alert message - MainActivity
                MainActivity.alarmMsg = "SMS: " + sms + "\n" + "Alarm Time: " + date + "\n";
                // Start Main Activity - Triggers Alert.
                Intent i = new Intent(context,MainActivity.class);
                // Flag needed (Current context != Activity due to being background service).
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                    } else {
                // Ensure alert is not triggered.
                    MainActivity.aStatus = false;
                }
            }
        }
    }

}
