package uk.ac.abertay.firedroidpager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSListenerHelper extends BroadcastReceiver {
    // Set debug tag
    public final String ETAG = "Incoming SMS: ";
    // Define instance of SQLDatabaseHelper
    SQLDatabaseHelper DB;
    // Create instance of Main Activity
    MainActivity main = new MainActivity();
    // Define SmsMessage.
    private String smsMesg;
    private String Alert1;
    private String Alert2;
    private Boolean DisableApp = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        DB = new SQLDatabaseHelper(context);
        Alert1 = SharedPreferencesHelper.getSharedPreferenceString(MainActivity.getAppContext(), "AlertKey1", Alert1);
        Alert2 = SharedPreferencesHelper.getSharedPreferenceString(MainActivity.getAppContext(), "AlertKey2", Alert2);
        DisableApp = SharedPreferencesHelper.getSharedPreferenceBoolean(MainActivity.getAppContext(), "DisableSMS", DisableApp);

        // Execute code if SMS_RECEIVED intent.
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED") && !DisableApp) {
            // Define bundle + Initialize - Get intent extras.
            Bundle smsbundle = intent.getExtras(); // Get SMS message
            SmsMessage[] currSMS;
            // Define + Initialize object and get pdus.
        // Execute code if not null.
        if (smsbundle != null) {
            // Exception Handling (Try function/Catch)
            try {
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
            Log.d("Exception caught",e.getMessage());
            }
                // Remove null word from SMS data into sms string
            String sms = smsMesg.substring(4);
                 // Debug
                Log.i(ETAG,"SQL: SMS Data - " + sms);
            // Check SMS for keyword - From Shared Preferences
            if (sms.contains(Alert1) || sms.contains(Alert2)) {
                    // Insert SMS data
                    boolean insertData = DB.insertDataDB(sms);
                    // Error logging.
                    if (insertData) {
                    // Debug Message
                    Log.i(ETAG," SQL: Saved Successfully. " + sms);
                        MainActivity.aStatus = true;

                    } else {
                        Log.i(ETAG," SQL: Not Saved Successfully.");
                        }

                // Start Main Activity - Triggers Alert.
                Intent i = new Intent(context,MainActivity.class);
                // Flag needed (Current context != Activity due to being background service).
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

                    } else {
                        Log.i(ETAG," Not 911 Call");
                    MainActivity.aStatus = false;
                }
            }
        } else {
            // Debug Message - SMS Alerting Disabled
            Log.i(ETAG, " SMS: " + "SMS Alerts Disabled.");
        }
    }


}
