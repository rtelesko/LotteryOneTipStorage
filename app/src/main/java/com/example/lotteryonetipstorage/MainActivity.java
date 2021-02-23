package com.example.lotteryonetipstorage;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Constants
    static final int LOTTO_NUMBERS = 6;
    public static final String USER_PREF = "myPrefs";

    // GUI Controls
    private TextView tvResult;
    private TextView tvValidation;
    private SharedPreferences lottery;

    // Data to be stored
    String time;
    private int numbers[] = new int[LOTTO_NUMBERS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get references to the TextViews which show the numbers and the validation result
        tvResult = findViewById(R.id.tvResult);
        tvValidation = findViewById(R.id.tvValidation);
    }

    // Generating valid numbers
    public void generateNumbers(View view) {
        tvResult.setText("");      // Initialize TextView for numbers
        tvValidation.setText("");      // Initialize TextView for validation

        Random random = new Random();

        for (int i = 0; i < LOTTO_NUMBERS; i++) {
            numbers[i] = random.nextInt(42) + 1;
            tvResult.append(numbers[i] + "  ");
        }
        if (validationCheck()) {
            tvValidation.setTextColor(new Color().parseColor("#008000"));
            tvValidation.setText("Validation OK");
        } else {
            tvValidation.setTextColor(new Color().parseColor("#FF0000"));
            tvValidation.setText("Validation NOT OK - Please make a new try!");
        }
    }


    // Validation: Check for duplicates in array
    private boolean validationCheck() {
        for (int i = 0; i < numbers.length; i++) {
            for (int j = i + 1; j < numbers.length; j++) {
                if (numbers[i] == numbers[j])
                    return false;  // Got a duplicate element
            }
        }
        return true;
    }

    // Store data to SharedPreferences
    public void storeData(View view) {

        if (validationCheck()) {
            // MODE_PRIVATE: By setting this mode, the file can only be accessed using calling application
            lottery = getApplicationContext().getSharedPreferences(USER_PREF, MODE_PRIVATE);
            SharedPreferences.Editor edit = lottery.edit();
            for (int i = 0; i < LOTTO_NUMBERS; i++) {
                edit.putInt("number" + (i + 1), numbers[i]);
            }
            // Adding Time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            time = dateFormat.format(new Date());
            edit.putString("time", time);
            /* commit() writes the data synchronously (blocking the thread its called from). It then informs you about the success of the operation.
               apply() schedules the data to be written asynchronously. It does not inform you about the success of the operation.
            */
            edit.commit();
            Toast.makeText(getApplicationContext(), "Storage successful!", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(getApplicationContext(), "Nothing to store or numbers invalid!", Toast.LENGTH_LONG).show();
    }

    // Store data from SharedPreferences
    public void retrieveData(View view) {

        lottery = getApplicationContext().getSharedPreferences(USER_PREF, MODE_PRIVATE);

        time = lottery.getString("time", null);
        /* default value for time = null
        If the variable was never accessed by the user or was never created,
        the system will set the default value as value and if you or changed
        this value, the default value is ignored.
        */
        if (time != null) {      // SharedPreferences not empty
            // Show result in TextView "tvResult"
            tvResult.setText("");
            tvValidation.setText("");
            /* A Handler allows you communicate back with the UI thread from
            other background thread. This is useful in Android as Android doesn't
            allow other threads to communicate directly with UI thread.
            Application here: Delay of 2sec for showing the retrieved data
            */
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Retrieving numbers
                    tvResult.append("Numbers:" + "  ");
                    for (int i = 0; i < LOTTO_NUMBERS; i++) {
                        numbers[i] = lottery.getInt("number" + (i + 1), 0);
                        tvResult.append(numbers[i] + "  ");
                    }
                    // Appending time
                    tvResult.append("\n" + "Time:" + "  " + time);

                }
            }, 2000);


        } else  // SharedPreferences empty
            Toast.makeText(getApplicationContext(), "Nothing to retrieve!", Toast.LENGTH_LONG).show();
    }

}