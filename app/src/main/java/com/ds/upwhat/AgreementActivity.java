package com.ds.upwhat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.ds.upwhat.LocalDatabase.SharedPref;

import Tools.Tool;

public class AgreementActivity extends AppCompatActivity {

    TextView ContinueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        Tool.Hide(getSupportActionBar());
        SetupVariables();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConfigureAgreementText();
        ApplyListeners();
    }

    private void SetupVariables()
    {
        // Activity Variables
        ContinueButton = findViewById(R.id.Agreement_ContinueButton);
    }

    private void ApplyListeners()
    {
        // Continue Button
        ContinueButton.setOnClickListener(view -> {
            SharedPref.ModifyAgreementValue(this, true);
            CheckLogin();
        });
    }

    private void OpenHome()
    {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void LoginScreen()
    {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void CheckLogin()
    {
        if (SharedPref.IsLoggedIn(this))
        {
            OpenHome();
        }
        else
        {
            LoginScreen();
        }
    }

    private void ConfigureAgreementText()
    {
        TextView textView = findViewById(R.id.Agreement_AgreementText);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}