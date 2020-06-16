package com.example.vewviewtests;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private MainActivityViewModel viewModel;
    private TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerText = findViewById(R.id.textview);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainActivityViewModel.class);

    }


    public void simulateClick(View view) {
        initScraping();
        startTimer();
    }

    private void startTimer() {
//        while(true){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        timerText.setText(dtf.format(now));
//            try {
//                sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private void initScraping() {
        viewModel.initScraping();
    }

}
