package uk.ac.abertay.firedroidpager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox enablevibrationcb;
    private CheckBox disableappcb;
    private EditText editalert1;
    private EditText editalert2;
    private String Alert1 = "";
    private String Alert2 = "";
    private String Audio = "cadpage";
    private Boolean Vibrate = true;
    private Integer VibrateM = 1;
    private Boolean DisableApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Initialize/Set Buttons/Text Boxes ect.
        Button savepref = (Button) findViewById(R.id.button_savesettings);
        Button setaudio = (Button) findViewById(R.id.button_setaudio);
        enablevibrationcb = (CheckBox)findViewById(R.id.checkBox_vibration);
        disableappcb = (CheckBox)findViewById(R.id.checkBox_disableapp);
        editalert1 = (EditText)findViewById(R.id.editText_keyword);
        editalert2 = (EditText)findViewById(R.id.editText_keyword2);
        // Set on click listener.
        savepref.setOnClickListener(this);
        // Load Shared Preferences (To UI).
        Alert1 = SharedPreferencesHelper.getSharedPreferenceString(this, "AlertKey1", Alert1);
        editalert1.setText(Alert1);
        Alert2 = SharedPreferencesHelper.getSharedPreferenceString(this, "AlertKey2", Alert2);
        editalert2.setText(Alert2);
        Vibrate = SharedPreferencesHelper.getSharedPreferenceBoolean(this, "VibrateSet", Vibrate);
        enablevibrationcb.setChecked(Vibrate);
        DisableApp = SharedPreferencesHelper.getSharedPreferenceBoolean(this, "DisableSMS", DisableApp);
        disableappcb.setChecked(DisableApp);
        Audio = SharedPreferencesHelper.getSharedPreferenceString(this, "AudioName", Audio);
        VibrateM = SharedPreferencesHelper.getSharedPreferenceInt(this, "VibrateMode", VibrateM);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_savesettings:
                // Get + Set Values - From UI
                Alert1 = editalert1.getText().toString();
                Alert2 = editalert2.getText().toString();
                Vibrate = enablevibrationcb.isChecked();
                DisableApp = disableappcb.isChecked();
                Audio = "cadpage";
                VibrateM = 1;
                // Check strings for null data, log/error if null data.
                if(Alert1 != null && !Alert1.isEmpty() || Alert2 != null && !Alert2.isEmpty()) {
                    SharedPreferencesHelper.setSharedPreferenceString(this, "AlertKey1", Alert1);
                    SharedPreferencesHelper.setSharedPreferenceString(this, "AlertKey2", Alert2);
                    SharedPreferencesHelper.setSharedPreferenceBoolean(this, "VibrateSet", Vibrate);
                    SharedPreferencesHelper.setSharedPreferenceBoolean(this, "DisableSMS", DisableApp);
                    SharedPreferencesHelper.setSharedPreferenceString(this, "AudioName", Audio);
                    SharedPreferencesHelper.setSharedPreferenceInt(this, "VibrateMode", VibrateM);
                } else {
                    // Error (No Data Entered) - Toast + Log.d
                    Toast.makeText(getApplicationContext(),"ERROR: No Data Entered. Enter Keywords",Toast.LENGTH_LONG).show();
                    Log.i("Settings","SettingsActivity - " + "Error - No Data!!");
                }
                break;
        }
    }
}
