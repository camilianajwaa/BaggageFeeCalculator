package com.cami.baggagefeecalculator;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Toolbar with a back arrow
        Toolbar toolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.about_title);
        }

        // Clickable button that opens the website URL
        Button btnVisit = findViewById(R.id.btn_visit);
        btnVisit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.app_url)));
            startActivity(intent);
        });
    }

    // Back arrow closes this screen
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}