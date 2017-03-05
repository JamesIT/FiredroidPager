package uk.ac.abertay.firedroidpager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class HazmatActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazmat);
        // Initialize button
        Button hazsearch = (Button) findViewById(R.id.button_hazsearch);
        // Set Button listener.
        hazsearch.setOnClickListener(this);
        hazmatSearch("");
    }

    @Override
    public void onClick(View v) {
    switch (v.getId()) {
        case R.id.button_hazsearch:
            // Initialize EditText
            EditText editsearchterm = (EditText) findViewById(R.id.edit_hazmatsearch);
            // Get EditText data, convert to string and then submit search term to hazmat search function.
            String searchterm = editsearchterm.getText().toString();
            // Execute Search
            hazmatSearch(searchterm);
            break;
        }
    }

    private void hazmatSearch(String searchterm) {
        // Initialize webview
     WebView hazmatview = (WebView) findViewById(R.id.hazmatWebview);
     // Set search URL + param string
     String webaddress = "http://www.hazmattool.com/info.php?submit2=search&info_name=" + searchterm + "&info_hazclass=+&submit=search";
     // Define webview client and load url
     hazmatview.setWebViewClient(new WebViewClient());
     hazmatview.loadUrl(webaddress);
    }


}
