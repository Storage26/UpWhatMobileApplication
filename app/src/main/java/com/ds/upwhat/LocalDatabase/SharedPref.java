package com.ds.upwhat.LocalDatabase;

import android.content.Context;
import android.content.SharedPreferences;

import com.ds.upwhat.Objects.LoginCredentials;
import com.ds.upwhat.StaticData.DefaultValues;

public class SharedPref
{
    public static boolean AgreedToAgreement(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Configuration", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("AgreedToAgreement", false);
    }

    public static void SetName(Context context, String name)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DisplayName", name);
        editor.apply();
    }

    public static void SetBio(Context context, String bio)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Biography", bio);
        editor.apply();
    }

    public static String GetName(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("DisplayName", DefaultValues.displayName);
    }

    public static String GetBio(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Biography", DefaultValues.biography);
    }

    public static void ModifyAgreementValue(Context context, boolean value)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("AgreedToAgreement", value);
        editor.apply();
    }

    public static boolean IsLoggedIn(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        return (!sharedPreferences.getString("Email", "").trim().equals("") && !sharedPreferences.getString("LoginToken", "").trim().equals(""));
    }

    public static LoginCredentials GetLoginCredentials(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        LoginCredentials credentials = new LoginCredentials();
        credentials.Email = sharedPreferences.getString("Email", "");
        credentials.Token = sharedPreferences.getString("LoginToken", "");
        return credentials;
    }

    public static void SaveLoginCredentials(Context context, String email, String token, String name, String bio)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("LoginToken", token);
        editor.putString("DisplayName", name);
        editor.putString("Biography", bio);
        editor.apply();
    }

    public static void Logout(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Email");
        editor.remove("LoginToken");
        editor.remove("DisplayName");
        editor.remove("Biography");
        editor.apply();
    }
}
