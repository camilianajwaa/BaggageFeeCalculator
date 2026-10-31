package com.cami.baggagefeecalculator;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Free baggage allowance (kg) and service tax
    private static final double ECONOMY_LIMIT = 20.0;
    private static final double BUSINESS_LIMIT = 40.0;
    private static final double TAX_RATE = 0.08;

    private TextInputLayout tilWeight, tilRate;
    private TextInputEditText etWeight, etRate;
    private MaterialButtonToggleGroup toggleClass;
    private View layoutResult;
    private TextView tvExcessWeight, tvExcessCharge, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always use the light (blue) look, even if the phone is in dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar (title bar with Share button and About menu)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Connect the screen items to Java
        tilWeight = findViewById(R.id.til_weight);
        tilRate = findViewById(R.id.til_rate);
        etWeight = findViewById(R.id.et_weight);
        etRate = findViewById(R.id.et_rate);
        toggleClass = findViewById(R.id.toggle_class);
        layoutResult = findViewById(R.id.layout_result);
        tvExcessWeight = findViewById(R.id.tv_excess_weight);
        tvExcessCharge = findViewById(R.id.tv_excess_charge);
        tvTotal = findViewById(R.id.tv_total);

        MaterialButton btnCalculate = findViewById(R.id.btn_calculate);
        MaterialButton btnReset = findViewById(R.id.btn_reset);

        btnCalculate.setOnClickListener(v -> calculateFee());
        btnReset.setOnClickListener(v -> resetForm());
    }

    // ---------- CALCULATE ----------
    private void calculateFee() {
        hideKeyboard();
        tilWeight.setError(null);
        tilRate.setError(null);

        boolean valid = true;
        double weight = 0;
        double rate = 0;

        // Check input 1: baggage weight
        String weightText = etWeight.getText().toString().trim();
        if (weightText.isEmpty()) {
            tilWeight.setError(getString(R.string.error_weight_empty));
            valid = false;
        } else {
            try {
                weight = Double.parseDouble(weightText);
                if (weight <= 0) {
                    tilWeight.setError(getString(R.string.error_weight_invalid));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                tilWeight.setError(getString(R.string.error_weight_invalid));
                valid = false;
            }
        }

        // Check input 3: rate per kg
        String rateText = etRate.getText().toString().trim();
        if (rateText.isEmpty()) {
            tilRate.setError(getString(R.string.error_rate_empty));
            valid = false;
        } else {
            try {
                rate = Double.parseDouble(rateText);
                if (rate <= 0) {
                    tilRate.setError(getString(R.string.error_rate_invalid));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                tilRate.setError(getString(R.string.error_rate_invalid));
                valid = false;
            }
        }

        // Stop if there is any mistake
        if (!valid) {
            layoutResult.setVisibility(View.GONE);
            Toast.makeText(this, R.string.error_fix_inputs, Toast.LENGTH_SHORT).show();
            return;
        }

        // Input 2: ticket class (Economy = 20 kg, Business = 40 kg)
        boolean isBusiness = toggleClass.getCheckedButtonId() == R.id.btn_business;
        double limit = isBusiness ? BUSINESS_LIMIT : ECONOMY_LIMIT;

        // Calculations
        double excessWeight = Math.max(0, weight - limit);   // never negative
        double excessCharge = excessWeight * rate;
        double totalPayable = excessCharge + (TAX_RATE * excessCharge);

        // Show the 3 outputs
        tvExcessWeight.setText(String.format(Locale.US, "%.2f kg", excessWeight));
        tvExcessCharge.setText(String.format(Locale.US, "RM %.2f", excessCharge));
        tvTotal.setText(String.format(Locale.US, "RM %.2f", totalPayable));
        layoutResult.setVisibility(View.VISIBLE);
    }

    // ---------- RESET ----------
    private void resetForm() {
        etWeight.setText("");
        etRate.setText("");
        tilWeight.setError(null);
        tilRate.setError(null);
        toggleClass.check(R.id.btn_economy);
        layoutResult.setVisibility(View.GONE);
        etWeight.requestFocus();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    // ---------- TOOLBAR MENU (Share + About) ----------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            // Share the application URL to other users
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.share_text) + " " + getString(R.string.app_url));
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser)));
            return true;

        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}