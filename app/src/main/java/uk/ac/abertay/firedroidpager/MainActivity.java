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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static Boolean aStatus = false;
    private static Context context;
    public final String ETAG = "Incoming SMS: ";
    // Set/Define Audio Variable String
    public String Audio = "nz_callout";
    // Define Arrays/Adapters/String/Buttons ect
    String sms = "";
    ArrayAdapter<String> adapter;
    ArrayList<String> SMSArray;
    Button bclearalerts;
    // Define DatabaseHelper
    SQLDatabaseHelper DB;
    private ListView list;
    private Button button_dismiss;
    // Define Alert Dialog/Audio
    private MediaPlayer alert;
    private AlertDialog dialog;

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Arrays/Adapter/Button ect
        SMSArray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.da_items, SMSArray);
        bclearalerts = (Button)findViewById(R.id.button_clearalerts);
        // Sets on click listener.
        bclearalerts.setOnClickListener(this);
        MainActivity.context = getApplicationContext();
        // Check SDK version, if less than SDK 23 (Error). Else, request permissions.
        if(Build.VERSION.SDK_INT < 23){
            Log.i(ETAG," Permissions: " + "SDK Less Than 23 - No Perms Needed.");
        }else {
            // Request Android Permissions (SMS Read/Write).
            requestSmsPermission();
        }
        // Populate List View
        populateListView();
        if (aStatus) {
            Alert911();
            aStatus = false;
        }
    }

    // Request runtime permissions
    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
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

    // Android Life Cycle - Close/Cleanup Properly
    @Override
    protected void onResume() {
        super.onResume();
        populateListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close SQLite DB
        DB.close();

        // Stop & Release Audio
        if (alert != null) {
            alert.release();
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
            DB = new SQLDatabaseHelper(this);
            DB.removeDataDB();
            // Close DB
            DB.close();
            populateListView();
            break;
        }
    }

    public void populateListView() {
        // Initialize ListView
        ListView list = (ListView) findViewById(R.id.smslistview);
        // Initialize Arraylist+Adapter
        SMSArray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.da_items, SMSArray);
        // Initialize DB Helper
        DB = new SQLDatabaseHelper(this);
        // Get SMS Data
        sms = DB.getDataDB();
        // Add SMS to adapter
        adapter.add(sms);
        // Set Adapter
        list.setAdapter(adapter);
        // Notify Adapter Changes
        adapter.notifyDataSetChanged();
        // Debug Message
        Log.i(ETAG,"SQL: Read " + sms);
        // Close DB
        DB.close();
    }

    public void Alert911() {
        // Set Handset Volume - 100%. (Incase of volume turned off).
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
        // Get sound_id - File Name (String Audio)
        int sound_id = getApplicationContext().getResources().getIdentifier(Audio, "raw", this.getPackageName());
        // Create Media Player
        alert = MediaPlayer.create(this, sound_id);
        // Initialize Vibrator
        Vibrator v = (Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Set Vibrate Pattern
        long[] pattern = {0, 100, 1000};
        // Setup Dialog View
        View mView = getLayoutInflater().inflate(R.layout.dialog_alert, null);
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
        // Pager Volume - Loop, Maximum Volume. Start
        alert.setVolume(100,100);
        alert.setLooping(true);
        alert.start();
        // Set Vibrate Pattern
        v.vibrate(pattern, 0);
    }

    public void Alert911Stop() {
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