package com.firebase.ginggingi.myfbs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * 정보 액티비티
 */

public class infoActivity extends AppCompatActivity {

    TextView Version;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);
        init();
    }

    private void init() {
        Version = (TextView) findViewById(R.id.Version);

        Version.setText(R.string.Version);

    }
}
