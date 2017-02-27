package uk.ac.abertay.firedroidpager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button savepref;
    Button setaudio;
    CheckBox enablevibrationcb;
    CheckBox disableappcb;
    EditText editalert1;
    EditText editalert2;
    public String Alert1;
    public String Alert2;
    public String Audio = "cadpage";
    public Boolean Vibrate;
    public Integer VibrateM = 1;
    public Boolean DisableApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        savepref = (Button)findViewById(R.id.button_savesettings);
        setaudio = (Button)findViewById(R.id.button_setaudio);
        enablevibrationcb = (CheckBox)findViewById(R.id.checkBox_vibration);
        disableappcb = (CheckBox)findViewById(R.id.checkBox_disableapp);
        editalert1 = (EditText)findViewById(R.id.editText_keyword);
        editalert2 = (EditText)findViewById(R.id.editText_keyword2);
        savepref.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_savesettings:
                savePrefs(v);
                break;
        }
    }

    // Save Shared Preferences
    public void savePrefs(View view) {

        Alert1 = editalert1.getText().toString();
        Alert2 = editalert2.getText().toString();
        Vibrate = enablevibrationcb.isChecked();
        DisableApp = disableappcb.isChecked();

        // Initilize Shared Preferences
        SharedPreferences sharedpref = SettingsActivity.this.getSharedPreferences("Config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpref.edit();

        // Alert Keyword 1 (&& OR)
        editor.putString("AlertKey1",Alert1);
        // Alert Keyword 2 (&& OR)
        editor.putString("AlertKey2",Alert2);
        editor.putBoolean("VibrateSet",Vibrate);
        // Disable SMS Listener Alerting - T/F
        editor.putBoolean("DisableSMS",DisableApp);
        // Audio File - To Be Played
        editor.putString("AudioName",Audio);
        // 1 - sos, 2 - ect ect, 3- ect ect
        editor.putInt("VibrateMode",VibrateM);
        editor.commit();
    }

    // Load Shared Preferences
    public void loadPrefs(View view) {
        SharedPreferences sharedpref = getSharedPreferences("Config", Context.MODE_PRIVATE);
        Alert1 = sharedpref.getString("AlertKey1","FIRE");
        Alert2 = sharedpref.getString("AlertKey2","EMS");
        Vibrate = sharedpref.getBoolean("VibrateSet",true);
        DisableApp = sharedpref.getBoolean("DisableSMS",false);
    }
}
