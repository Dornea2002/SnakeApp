package com.example.sankeapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sankeapp.screens.PlayFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, PlayFragment.class, null)
                    .commit();
        }
    }
}