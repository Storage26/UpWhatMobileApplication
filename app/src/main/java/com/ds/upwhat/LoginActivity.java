package com.ds.upwhat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ds.upwhat.LocalDatabase.SharedPref;
import com.ds.upwhat.StaticData.ErrorInfo;
import com.ds.upwhat.StaticData.ServerInfo;

import org.json.JSONException;

import Tools.Tool;

public class LoginActivity extends AppCompatActivity {

    EditText AgeInput;
    EditText EmailInput;

    TextView LoginButton;

    RequestQueue queue;

    boolean PrimaryLoginRequestQueued;
    boolean SecondaryLoginRequestQueued;

    Dialog LoadingDialog1;
    Dialog LoadingDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Tool.Hide(getSupportActionBar());
        SetupVariables();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SetupLoadingDialogs();
        BindListeners();
    }

    private void SetupLoadingDialogs()
    {
        LoadingDialog1 = new Dialog(this);
        LoadingDialog1.setContentView(R.layout.dialog_loading);
        LoadingDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LoadingDialog1.setCanceledOnTouchOutside(false);
        LoadingDialog1.setCancelable(false);

        LoadingDialog2 = new Dialog(this);
        LoadingDialog2.setContentView(R.layout.dialog_loading);
        LoadingDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LoadingDialog2.setCanceledOnTouchOutside(false);
        LoadingDialog2.setCancelable(false);
    }

    void BindListeners()
    {
        LoginButton.setOnClickListener(view -> {
            if (!PrimaryLoginRequestQueued)
            {
                if (!AgeInput.getText().toString().trim().isEmpty())
                {
                    int age = Integer.parseInt(AgeInput.getText().toString().trim());
                    String email = EmailInput.getText().toString().trim().toLowerCase();

                    if (age != 0)
                    {
                        if (!email.isEmpty())
                        {
                            StartLoginProcess(age, email);
                        }
                        else
                        {
                            EmailInput.setError("Please enter your email address");
                            EmailInput.requestFocus();
                        }
                    }
                    else
                    {
                        AgeInput.setError("Please confirm your age");
                        AgeInput.requestFocus();
                    }
                }
                else
                {
                    AgeInput.setError("Please confirm your age");
                    AgeInput.requestFocus();
                }
            }
        });
    }

    private void StartLoginProcess(int age, String email)
    {
        // Cancel Previous Requests
        LoadingDialog1.show();
        PrimaryLoginRequestQueued = true;
        queue.cancelAll("PrimaryLogin");

        // Request Configuration
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                ServerInfo.ServerLocation + "/login-email?age=" + age + "&email=" + email,
                null,
                response -> {
                    try
                    {
                        if (response.has("shouldProceed"))
                        {
                            if (response.getBoolean("shouldProceed"))
                            {
                                // Success Step
                                queue.cancelAll("PrimaryLogin");
                                PrimaryLoginRequestQueued = false;
                                LoadingDialog1.hide();
                                ApprovalProcess(email);
                            }
                            else
                            {
                                // Error Step
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
                                    }
                                }
                                else
                                {
                                    Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                    queue.cancelAll("PrimaryLogin");
                                    PrimaryLoginRequestQueued = false;
                                    LoadingDialog1.hide();
                                }

                                // Common
                                queue.cancelAll("PrimaryLogin");
                                PrimaryLoginRequestQueued = false;
                                LoadingDialog1.hide();
                            }
                        }
                        else
                        {
                            Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                            queue.cancelAll("PrimaryLogin");
                            PrimaryLoginRequestQueued = false;
                            LoadingDialog1.hide();
                        }
                    }
                    catch (JSONException e)
                    {
                        Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                        queue.cancelAll("PrimaryLogin");
                        PrimaryLoginRequestQueued = false;
                        LoadingDialog1.hide();
                    }
                },
                error -> {
                    Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_NetworkError, ErrorInfo.Body_NetworkError, "ok");
                    queue.cancelAll("PrimaryLogin");
                    PrimaryLoginRequestQueued = false;
                    LoadingDialog1.hide();
                });

        // Final Steps
        request.setTag("PrimaryLogin");
        queue.add(request);
    }

    private void SetupVariables()
    {
        // Activity Variables
        AgeInput = findViewById(R.id.Login_AgeInput);
        EmailInput = findViewById(R.id.Login_EmailInput);
        LoginButton = findViewById(R.id.Login_LoginButton);

        // Other Variables
        queue = Volley.newRequestQueue(this);
    }

    private void ApprovalProcess(String email)
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_login_approval);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        // Interface Variables
        EditText CodeInput = dialog.findViewById(R.id.LoginApprovalDialog_CodeInput);
        TextView DoneButton = dialog.findViewById(R.id.LoginApprovalDialog_DoneButton);

        // Attach Listeners
        DoneButton.setOnClickListener(view -> {
            if (!SecondaryLoginRequestQueued)
            {
                if (!CodeInput.getText().toString().trim().isEmpty())
                {
                    int code = Integer.parseInt(CodeInput.getText().toString().trim());

                    /* Send Request to Server */
                    // Cancel Previous Requests
                    LoadingDialog2.show();
                    SecondaryLoginRequestQueued = true;
                    queue.cancelAll("SecondaryLogin");

                    // Request Configuration
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                            ServerInfo.ServerLocation + "/login-code?code=" + code + "&email=" + email,
                            null,
                            response -> {
                                try
                                {
                                    if (response.has("success"))
                                    {
                                        if (response.getBoolean("success"))
                                        {
                                            if (response.has("token") && response.has("displayName") && response.has("biography"))
                                            {
                                                String token = response.getString("token");
                                                String name = response.getString("displayName");
                                                String bio = response.getString("biography");
                                                SharedPref.SaveLoginCredentials(this, email, token, name, bio);

                                                queue.cancelAll("SecondaryLogin");
                                                SecondaryLoginRequestQueued = false;
                                                LoadingDialog2.hide();

                                                PostLoginSteps(name, bio);
                                            }
                                            else
                                            {
                                                Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                queue.cancelAll("SecondaryLogin");
                                                SecondaryLoginRequestQueued = false;
                                                LoadingDialog2.hide();
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
                                                    queue.cancelAll("SecondaryLogin");
                                                    SecondaryLoginRequestQueued = false;
                                                    LoadingDialog2.hide();
                                                }
                                            }
                                            else
                                            {
                                                Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                queue.cancelAll("SecondaryLogin");
                                                SecondaryLoginRequestQueued = false;
                                                LoadingDialog2.hide();
                                            }

                                            // Common
                                            queue.cancelAll("SecondaryLogin");
                                            SecondaryLoginRequestQueued = false;
                                            LoadingDialog2.hide();
                                        }
                                    }
                                    else
                                    {
                                        Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                        queue.cancelAll("SecondaryLogin");
                                        SecondaryLoginRequestQueued = false;
                                        LoadingDialog2.hide();
                                    }
                                }
                                catch (JSONException e)
                                {
                                    Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                    queue.cancelAll("SecondaryLogin");
                                    SecondaryLoginRequestQueued = false;
                                    LoadingDialog2.hide();
                                }
                            },
                            error -> {
                                Tool.CreateDefaultDialog(this, this, ErrorInfo.Title_NetworkError, ErrorInfo.Body_NetworkError, "ok");
                                queue.cancelAll("SecondaryLogin");
                                SecondaryLoginRequestQueued = false;
                                LoadingDialog2.hide();
                            });

                    // Final Steps
                    request.setTag("SecondaryLogin");
                    queue.add(request);
                }
                else
                {
                    CodeInput.setError("Please enter the login code");
                    CodeInput.requestFocus();
                }
            }
        });

        dialog.show();
    }

    private void PostLoginSteps(String name, String bio)
    {
        Intent intent = new Intent(this, PostLoginUpdateProfileActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("bio", bio);
        startActivity(intent);
        finish();
    }
}