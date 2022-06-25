package com.ds.upwhat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ds.upwhat.LocalDatabase.SharedPref;
import com.ds.upwhat.StaticData.ErrorInfo;
import com.ds.upwhat.StaticData.ServerInfo;

import org.json.JSONException;
import org.json.JSONObject;

import Tools.Tool;

public class PostLoginUpdateProfileActivity extends AppCompatActivity {

    EditText DisplayNameInput;
    EditText BiographyInput;
    TextView FinishButton;

    Dialog LoadingDialog;

    boolean ProcessOngoing;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login_update_profile);
        Tool.Hide(getSupportActionBar());
        SetupVariables();
    }

    @Override
    protected void onStart() {
        super.onStart();

        BindListeners();
        SetupLoadingDialog();
        UpdateInput();
    }

    private void SetupLoadingDialog()
    {
        LoadingDialog = new Dialog(this);
        LoadingDialog.setContentView(R.layout.dialog_loading);
        LoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LoadingDialog.setCanceledOnTouchOutside(false);
        LoadingDialog.setCancelable(false);
    }

    private void SetupVariables()
    {
        // Interface Variables
        DisplayNameInput = findViewById(R.id.PLUP_DisplayName);
        BiographyInput = findViewById(R.id.PLUP_Biography);
        FinishButton = findViewById(R.id.PLUP_FinishButton);

        // Other Variables
        queue = Volley.newRequestQueue(this);
        ProcessOngoing = false;
    }

    private void BindListeners()
    {
        // FinishButton
        FinishButton.setOnClickListener(view -> {
            String name = DisplayNameInput.getText().toString().trim();
            String bio = BiographyInput.getText().toString();

            if (!name.isEmpty())
            {
                UpdateDisplayName(name, bio);
            }
            else
            {
                DisplayNameInput.setError("A display name is required");
            }
        });
    }

    private void UpdateDisplayName(String name, String bio)
    {
        if (!ProcessOngoing)
        {
            // Setup
            ProcessOngoing = true;
            LoadingDialog.show();
            queue.cancelAll("Main");
            String email = SharedPref.GetLoginCredentials(this).Email;
            String token = SharedPref.GetLoginCredentials(this).Token;

            // Request
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    ServerInfo.ServerLocation + "/update-display-name?email=" + email + "&token=" + token + "&display_name=" + name,
                    null,
                    response -> {
                        try
                        {
                            if (response.has("success"))
                            {
                                if (response.getBoolean("success"))
                                {
                                    // Update Biography
                                    if (!bio.isEmpty())
                                    {
                                        UpdateBiography(name, bio);
                                    }
                                    else
                                    {
                                        queue.cancelAll("Main");
                                        ProcessOngoing = false;
                                        LoadingDialog.hide();

                                        Completed();
                                    }
                                }
                                else
                                {
                                    if (response.has("dataType"))
                                    {
                                        if (response.getString("dataType").equals("text"))
                                        {
                                            if (response.has("headerText") && response.has("bodyText") && response.has("actionType"))
                                            {
                                                String title = response.getString("headerText");
                                                String body = response.getString("bodyText");
                                                String actionType = response.getString("actionType");

                                                Tool.CreateDefaultDialog(this, this, title, body, actionType);
                                            }
                                            else
                                            {
                                                Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                            }
                                        }
                                        else
                                        {
                                            Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                            queue.cancelAll("Main");
                                            ProcessOngoing = false;
                                            LoadingDialog.hide();
                                        }
                                    }
                                    else
                                    {
                                        Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                        queue.cancelAll("Main");
                                        ProcessOngoing = false;
                                        LoadingDialog.hide();
                                    }

                                    // Common
                                    queue.cancelAll("Main");
                                    ProcessOngoing = false;
                                    LoadingDialog.hide();
                                }
                            }
                            else
                            {
                                Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                queue.cancelAll("Main");
                                ProcessOngoing = false;
                                LoadingDialog.hide();
                            }
                        }
                        catch (JSONException e)
                        {
                            Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                            queue.cancelAll("Main");
                            ProcessOngoing = false;
                            LoadingDialog.hide();
                        }
                    },
                    error -> {
                        Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_NetworkError, ErrorInfo.Body_NetworkError, "ok");
                        queue.cancelAll("Main");
                        ProcessOngoing = false;
                        LoadingDialog.hide();
                    });

            request.setTag("Main");
            queue.add(request);
        }
    }

    private void UpdateBiography(String name, String bio)
    {
        // Setup
        LoadingDialog.show();
        queue.cancelAll("Main");
        String email = SharedPref.GetLoginCredentials(this).Email;
        String token = SharedPref.GetLoginCredentials(this).Token;

        // Request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                ServerInfo.ServerLocation + "/update-biography?email=" + email + "&token=" + token + "&biography=" + bio,
                null,
                response -> {
                    try
                    {
                        if (response.has("success"))
                        {
                            if (response.getBoolean("success"))
                            {
                                queue.cancelAll("Main");
                                ProcessOngoing = false;
                                LoadingDialog.hide();

                                EndSteps(name, bio);
                                Completed();
                            }
                            else
                            {
                                if (response.has("dataType"))
                                {
                                    if (response.getString("dataType").equals("text"))
                                    {
                                        if (response.has("headerText") && response.has("bodyText") && response.has("actionType"))
                                        {
                                            String title = response.getString("headerText");
                                            String body = response.getString("bodyText");
                                            String actionType = response.getString("actionType");

                                            Tool.CreateDefaultDialog(this, this, title, body, actionType);
                                        }
                                        else
                                        {
                                            Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                        }
                                    }
                                    else
                                    {
                                        Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                        queue.cancelAll("Main");
                                        ProcessOngoing = false;
                                        LoadingDialog.hide();
                                    }
                                }
                                else
                                {
                                    Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                    queue.cancelAll("Main");
                                    ProcessOngoing = false;
                                    LoadingDialog.hide();
                                }

                                // Common
                                queue.cancelAll("Main");
                                ProcessOngoing = false;
                                LoadingDialog.hide();
                            }
                        }
                        else
                        {
                            Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                            queue.cancelAll("Main");
                            ProcessOngoing = false;
                            LoadingDialog.hide();
                        }
                    }
                    catch (JSONException e)
                    {
                        Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                        queue.cancelAll("Main");
                        ProcessOngoing = false;
                        LoadingDialog.hide();
                    }
                },
                error -> {
                    Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_NetworkError, ErrorInfo.Body_NetworkError, "ok");
                    queue.cancelAll("Main");
                    ProcessOngoing = false;
                    LoadingDialog.hide();
                });

        request.setTag("Main");
        queue.add(request);
    }

    private void UpdateInput()
    {
        String name = getIntent().getExtras().getString("name");
        String bio = getIntent().getExtras().getString("bio");

        DisplayNameInput.setText(name);
        BiographyInput.setText(bio);
    }

    private void EndSteps(String name, String bio)
    {
        SharedPref.SetName(this, name);
        SharedPref.SetBio(this, bio);
    }

    private void Completed()
    {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}