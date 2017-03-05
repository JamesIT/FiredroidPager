package uk.ac.abertay.firedroidpager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static Boolean aStatus = false;
    private static Context context;
    private final String ETAG = "MainActivity: ";
    // Set/Define Audio Variable String
    private String Audio = "";
    private Boolean Vibrate = true;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> SMSArray;
    // Define DatabaseHelper
    private SQLDatabaseHelper SDH;
    private ListView list;
    private Button button_dismiss;
    // Define Alert Dialog/Audio
    private MediaPlayer alert;
    private AlertDialog dialog;
    private String sms;
    private String alarmMsg;

    // Set static context
    public static Context getAppContext() {
        return MainActivity.context;
    }

    // Android Life Cycle - Create/Close/Cleanup Properly
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupApp();
        checkPerms();
        // Populate List View
        populateListView();
        // If alert status == true, run pager function.
        if (aStatus) {
            Alert911();
            aStatus = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get writable database.
        SDH.getWritableDatabase();
        populateListView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop & Release Audio
        if (alert != null) {
            alert.release();
        }
        // TODO: Add Thread Names + Cleanup in onPause/onStop.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close SQLite DB
        SDH.close();
        // Stop & Release Audio
        if (alert != null) {
            alert.release();
        }

    }

    // Action Bar Menu - Override
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Action Bar Menu - Override
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                // Settings Page - Set/Launch Activity
                Intent settingsact = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsact);
                return true;
            case R.id.item2:
                // Hazmat Page - Set/Launch Activity
                Intent biohazact = new Intent(MainActivity.this, HazmatActivity.class);
                startActivity(biohazact);
                return true;
            case R.id.item3:
                // About Us Page - Set/Launch Activity
                Intent aboutact = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutact);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View v) {
    switch (v.getId()) {
        default:
            break;
        case R.id.button_dismiss:
            // Dismiss Dialog
            dialog.dismiss();
            // Stop Pager Alerting
            Alert911Stop();
            populateListView();
            break;
        case R.id.button_clearalerts:
            // Clear SMS Alerting SQLite.
            SDH.removeDataDB();
            populateListView();
            break;
    }
    }

    // Request runtime permissions - RECEIVE_SMS.
    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void setupApp() {
        // Initialize Arrays/Adapter/Button ect
        SMSArray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.da_items, SMSArray);
        MainActivity.context = getApplicationContext();
        Button bclearalerts = (Button) findViewById(R.id.button_clearalerts);
        // Sets on click listener.
        bclearalerts.setOnClickListener(this);
    }

    private void checkPerms() {
        // Check SDK version, if less than SDK 23 (Error). Else, request permissions.
        if (Build.VERSION.SDK_INT < 23) {
            Log.i(ETAG, " Permissions: " + "SDK Less Than 23 - No Perms Needed.");
        } else {
            // Request Android Permissions (SMS Read/Write).
            requestSmsPermission();
        }
    }

    private void populateListView() {
        // Initialize ListView
        ListView list = (ListView) findViewById(R.id.smslistview);
        // Initialize Arraylist+Adapter
        SMSArray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.da_items, SMSArray);
        // Initialize DB Helper
        SDH = new SQLDatabaseHelper(this);
        // Clear SMS Data
        sms = "";
        // Get SMS Data
        sms = SDH.getDataDB();
        // Add SMS to adapter
        adapter.add(sms);
        // Set Adapter
        list.setAdapter(adapter);
        // Notify Adapter Changes
        adapter.notifyDataSetChanged();
        // Debug Message
        Log.i(ETAG,"SQL: Read " + sms);
    }

    private void Alert911() {
        // Add latest alert to AlertMsg - Strip by new lines. (Check first if not null - Prevent possible crash).
        if (sms != null && !sms.trim().isEmpty()) {
            // Get substring, all data before ]~ (Unique, less chance of an SMS containing the data).
            alarmMsg = sms.substring(0, sms.indexOf(']')) + "]~";
        }
        // Get Custom Alert (From Saved Preferences)
        Audio = SharedPreferencesHelper.getSharedPreferenceString(this, "AudioName", Audio);
        // Prevents "avoid passing null as view root" error
        // Define Viewgroup, set to null (Prevents an error/warning).
        final ViewGroup nullParent = null;
        // Get sharedpreferences (Vibration)
        Vibrate = SharedPreferencesHelper.getSharedPreferenceBoolean(this, "VibrateSet", Vibrate);
        // Set Handset Volume - 100%. (Incase of volume turned off).
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
        // Get sound_id - File Name (String Audio)
        int sound_id = getApplicationContext().getResources().getIdentifier(Audio, "raw", this.getPackageName());
        // Create Media Player
        alert = MediaPlayer.create(this, sound_id);
        // Setup Dialog View
        View mView = getLayoutInflater().inflate(R.layout.dialog_alert, nullParent);
        // Define Button
        Button dismiss = (Button) mView.findViewById(R.id.button_dismiss);
        // Setup Listener
        dismiss.setOnClickListener(this);
        // Define & Initialize Dialog
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        // Set dialog view & create dialog
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        // Show Dialog
        dialog.show();
        TextView tvalert = (TextView) mView.findViewById(R.id.tv_alert);
        tvalert.setText(alarmMsg);
        // Clear alertMsg string.
        alarmMsg = "";
        // Check if vibration disabled.
        if (Vibrate) {
        // Set Vibrate Pattern
        long[] pattern = {0, 100, 1000};
            // Initialize Vibrator
            Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Set Vibrate Pattern
            v.vibrate(pattern, 0);
        }
        // Pager Volume - Loop, Maximum Volume. Start
        alert.setVolume(100,100);
        alert.setLooping(true);
        alert.start();

    }

    private void Alert911Stop() {
        // Initialize Vibrator
    Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
    // Stop volume & Release/Reset if !=null && isPlaying
    if (alert!=null) {
        if (alert.isPlaying())
            alert.setLooping(false);
            alert.stop();
    alert.reset();
    alert.release();
    alert=null;
    }
    // Disable Vibration
    v.vibrate(0);
    // Closes Current Activity - Save Memory Usage ect...
    this.finish();
    }

}