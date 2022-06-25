package com.ds.upwhat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ds.upwhat.LocalDatabase.SharedPref;

import Tools.Tool;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tool.Hide(getSupportActionBar());
    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckAgreement();
    }

    private void CheckAgreement()
    {
        if (!SharedPref.AgreedToAgreement(this))
        {
            AgreementProcess();
        }
        else
        {
            CheckLogin();
        }
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

    private void AgreementProcess()
    {
        startActivity(new Intent(this, AgreementActivity.class));
        finish();
    }
}