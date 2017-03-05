package uk.ac.abertay.firedroidpager;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button setaudio;
    private CheckBox enablevibrationcb;
    private CheckBox disableappcb;
    private EditText editalert1;
    private EditText editalert2;
    private String Alert1 = "";
    private String Alert2 = "";
    private String Audio = "";
    private Boolean Vibrate = true;
    private Integer VibrateM = 1;
    private Boolean DisableApp = false;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Initialize/Set Buttons/Text Boxes ect.
        Button savepref = (Button) findViewById(R.id.button_savesettings);
        setaudio = (Button) findViewById(R.id.button_setaudio);
        enablevibrationcb = (CheckBox)findViewById(R.id.checkBox_vibration);
        disableappcb = (CheckBox)findViewById(R.id.checkBox_disableapp);
        editalert1 = (EditText)findViewById(R.id.editText_keyword);
        editalert2 = (EditText)findViewById(R.id.editText_keyword2);
        RadioGroup audio_rg = (RadioGroup) findViewById(R.id.audio_rg);
        RadioButton radioButton_selectaudio1 = (RadioButton) findViewById(R.id.radioButton_selectaudio1);
        RadioButton radioButton_selectaudio2 = (RadioButton) findViewById(R.id.radioButton_selectaudio2);
        RadioButton radioButton_selectaudio3 = (RadioButton) findViewById(R.id.radioButton_selectaudio3);
        RadioButton radioButton_selectaudio4 = (RadioButton) findViewById(R.id.radioButton_selectaudio4);
        // Set on click listener.
        savepref.setOnClickListener(this);
        setaudio.setOnClickListener(this);
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
        setaudio.setText(Audio);
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
                Audio = setaudio.getText().toString();
                VibrateM = 1;
                // Check strings for null data, log/error if null data.
                if (Alert1 != null && !Alert1.isEmpty() || Alert2 != null && !Alert2.isEmpty()) {
                    SharedPreferencesHelper.setSharedPreferenceString(this, "AlertKey1", Alert1);
                    SharedPreferencesHelper.setSharedPreferenceString(this, "AlertKey2", Alert2);
                    SharedPreferencesHelper.setSharedPreferenceBoolean(this, "VibrateSet", Vibrate);
                    SharedPreferencesHelper.setSharedPreferenceBoolean(this, "DisableSMS", DisableApp);
                    SharedPreferencesHelper.setSharedPreferenceString(this, "AudioName", Audio);
                    SharedPreferencesHelper.setSharedPreferenceInt(this, "VibrateMode", VibrateM);
                } else {
                    // Error (No Data Entered) - Toast + Log.d
                    Toast.makeText(getApplicationContext(), "ERROR: No Data Entered. Enter Keywords", Toast.LENGTH_LONG).show();
                    Log.i("Settings", "SettingsActivity - " + "Error - No Data!!");
                }
                break;
            case R.id.button_setaudio:
                // Prevent null error message. Set viewgroup.
                final ViewGroup nullParent = null;
                // Create view, set/inflate layout.
                View mView = getLayoutInflater().inflate(R.layout.dialog_audiosettings, nullParent);
                // Build dialog and set view.
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingsActivity.this);
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                // Show Dialog
                dialog.show();
                break;
            default:
                break;
        }

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton_selectaudio1:
                String audio1 = "cadpage";
                // If checked set audio
                if (checked)
                    Audio = audio1;
                // Dismiss dialog and set audio button to selected option
                dialog.dismiss();
                setaudio.setText(Audio);
                break;
            case R.id.radioButton_selectaudio2:
                String audio2 = "monty_als";
                // If checked set audio
                if (checked)
                    Audio = audio2;
                // Dismiss dialog and set audio button to selected option
                dialog.dismiss();
                setaudio.setText(Audio);
                break;
            case R.id.radioButton_selectaudio3:
                String audio3 = "nz_callout";
                // If checked set audio
                if (checked)
                    Audio = audio3;
                // Dismiss dialog and set audio button to selected option
                dialog.dismiss();
                setaudio.setText(Audio);
                break;
            case R.id.radioButton_selectaudio4:
                String audio4 = "stationbuzz";
                // If checked set audio
                if (checked)
                    Audio = audio4;
                // Dismiss dialog and set audio button to selected option
                dialog.dismiss();
                setaudio.setText(Audio);
                break;
            default:
                break;
        }
    }
}
