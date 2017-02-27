package uk.ac.abertay.firedroidpager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSListenerHelper extends BroadcastReceiver {
    // Define SmsMessage.
    private String smsMesg;
    private String smsFrom;
    private String sms;

    // Set debug tag
    public final String ETAG = "Incoming SMS: ";

    // Define instance of SQLDatabaseHelper
    SQLDatabaseHelper DB;

    // Create instance of Main Activity
    MainActivity main = new MainActivity();

    @Override
    public void onReceive(Context context, Intent intent) {
        DB = new SQLDatabaseHelper(context);
        // Execute code if SMS_RECEIVED intent.
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
        // Define bundle + Initalize - Get intent extras.
        Bundle smsbundle = intent.getExtras(); // Get SMS message
        SmsMessage[] currSMS = null;
        // Define + Initalize object and get pdus.
        // Execute code if not null.
        if (smsbundle != null) {

            try {
                Object[] smspdu = (Object[]) smsbundle.get("pdus");
                currSMS = new SmsMessage[smspdu.length];
                String msgBody = "";
                String msgfrom = "";

                for (int i = 0; i < currSMS.length; i++) {
                    currSMS[i] = SmsMessage.createFromPdu((byte[]) smspdu[i]);
                    smsFrom = currSMS[i].getOriginatingAddress();
                    smsMesg += currSMS[i].getMessageBody();
                }

            } catch (Exception e) {
            Log.d("Exception caught",e.getMessage());
            }
                // Remove null word from SMS data into sms string
                sms = smsMesg.substring(4);

                 // Debug
                Log.i(ETAG,"SQL: SMS Data - " + sms);
                // Check SMS for keyword
                if (sms.contains("FIRE") || sms.contains("EMS")) {
                // Insert SMS data
                boolean insertData = DB.insertDataDB(sms);
                // Error logging.
                if(insertData == true) {
                    Log.i(ETAG," SQL: Saved Successfully. " + sms);
                    main.aStatus = true;
                } else {
                    Log.i(ETAG," SQL: Not Saved Successfully.");
                        }
            } else {
                    Log.i(ETAG," Not 911 Call");
                    main.aStatus = false;
                }
            }
        }

    }


}
