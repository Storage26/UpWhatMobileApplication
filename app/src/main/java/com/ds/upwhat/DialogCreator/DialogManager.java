package com.ds.upwhat.DialogCreator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ds.upwhat.LocalDatabase.SharedPref;
import com.ds.upwhat.R;
import com.ds.upwhat.StaticData.ErrorInfo;
import com.ds.upwhat.StaticData.ServerInfo;

import org.json.JSONException;

import Tools.Tool;

public class DialogManager
{
    private boolean ProcessOngoing1;
    private boolean ProcessOngoing2;
    private boolean ProcessOngoingNC;

    public void MakeProfileDialog(Context context, Activity activity)
    {
        // Configuration
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_profile);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = dialog.getWindow().getAttributes().height;

        // Interface Variables
        TextView displayNameText = dialog.findViewById(R.id.ProfileDialog_DisplayName);
        LinearLayout displayNameContainer = dialog.findViewById(R.id.ProfileDialog_DisplayNameContainer);
        TextView biographyText = dialog.findViewById(R.id.ProfileDialog_Biography);
        TextView moreOptionsButton = dialog.findViewById(R.id.ProfileDialog_MoreOptionsButton);

        // Other Variables
        String displayName = SharedPref.GetName(context);
        String biography = SharedPref.GetBio(context);

        // Bind Listeners
        displayNameContainer.setOnClickListener(view -> {
            Dialog displayNameDialog = new Dialog(context);
            displayNameDialog.setContentView(R.layout.dialog_edit_display_name);
            displayNameDialog.setCancelable(true);
            displayNameDialog.setCanceledOnTouchOutside(true);
            displayNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Interface Variables
            EditText input = displayNameDialog.findViewById(R.id.EditDisplayNameDialog_Input);
            TextView doneButton = displayNameDialog.findViewById(R.id.EditDisplayNameDialog_DoneButton);

            // Bind Listeners
            doneButton.setOnClickListener(view1 -> {
                String name = input.getText().toString().trim();

                if (!name.equals(displayNameText.getText().toString().trim()))
                {
                    if (!name.isEmpty())
                    {
                        ProcessOngoing1 = false;

                        /* Send Request */
                        // Setup Loading Dialog
                        Dialog LoadingDialog = new Dialog(context);
                        LoadingDialog.setContentView(R.layout.dialog_loading);
                        LoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        LoadingDialog.setCanceledOnTouchOutside(false);
                        LoadingDialog.setCancelable(false);

                        // Setup Queue
                        RequestQueue queue = Volley.newRequestQueue(context);

                        if (!ProcessOngoing1)
                        {
                            // Setup
                            ProcessOngoing1 = true;
                            LoadingDialog.show();
                            queue.cancelAll("SetDisplayName");
                            String email = SharedPref.GetLoginCredentials(context).Email;
                            String token = SharedPref.GetLoginCredentials(context).Token;

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
                                                    // Common
                                                    queue.cancelAll("SetDisplayName");
                                                    ProcessOngoing1 = false;
                                                    LoadingDialog.hide();

                                                    // Post Steps
                                                    SharedPref.SetName(context, name);

                                                    // Finish
                                                    displayNameText.setText(name);
                                                    displayNameDialog.hide();
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

                                                                Tool.CreateDefaultDialog(context, activity, title, body, actionType);
                                                            }
                                                            else
                                                            {
                                                                Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                            queue.cancelAll("SetDisplayName");
                                                            ProcessOngoing1 = false;
                                                            LoadingDialog.hide();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                        queue.cancelAll("SetDisplayName");
                                                        ProcessOngoing1 = false;
                                                        LoadingDialog.hide();
                                                    }

                                                    // Common
                                                    queue.cancelAll("SetDisplayName");
                                                    ProcessOngoing1 = false;
                                                    LoadingDialog.hide();
                                                }
                                            }
                                            else
                                            {
                                                Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                queue.cancelAll("SetDisplayName");
                                                ProcessOngoing1 = false;
                                                LoadingDialog.hide();
                                            }
                                        }
                                        catch (JSONException e)
                                        {
                                            Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                            queue.cancelAll("SetDisplayName");
                                            ProcessOngoing1 = false;
                                            LoadingDialog.hide();
                                        }
                                    },
                                    error -> {
                                        Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_NetworkError, ErrorInfo.Body_NetworkError, "ok");
                                        queue.cancelAll("SetDisplayName");
                                        ProcessOngoing1 = false;
                                        LoadingDialog.hide();
                                    });

                            request.setTag("SetDisplayName");
                            queue.add(request);
                        }
                    }
                    else
                    {
                        input.setError("Please enter your name");
                        input.requestFocus();
                    }
                }
                else
                {
                    displayNameDialog.hide();
                }
            });

            // Show
            displayNameDialog.show();
            displayNameDialog.getWindow().setLayout(width, height);
            input.setText(displayNameText.getText().toString().trim());
            input.requestFocus();
        });

        biographyText.setOnClickListener(view -> {
            Dialog biographyDialog = new Dialog(context);
            biographyDialog.setContentView(R.layout.dialog_edit_biography);
            biographyDialog.setCancelable(true);
            biographyDialog.setCanceledOnTouchOutside(true);
            biographyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Interface Variables
            EditText input = biographyDialog.findViewById(R.id.EditBiographyDialog_Input);
            TextView doneButton = biographyDialog.findViewById(R.id.EditBiographyDialog_DoneButton);

            // Bind Listeners
            doneButton.setOnClickListener(view1 -> {
                String bio = input.getText().toString().trim();

                if (!bio.equals(biographyText.getText().toString().trim()))
                {
                    if (!bio.isEmpty())
                    {
                        ProcessOngoing2 = false;

                        /* Send Request */
                        // Setup Loading Dialog
                        Dialog LoadingDialog = new Dialog(context);
                        LoadingDialog.setContentView(R.layout.dialog_loading);
                        LoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        LoadingDialog.setCanceledOnTouchOutside(false);
                        LoadingDialog.setCancelable(false);

                        // Setup Queue
                        RequestQueue queue = Volley.newRequestQueue(context);

                        if (!ProcessOngoing2)
                        {
                            // Setup
                            ProcessOngoing2 = true;
                            LoadingDialog.show();
                            queue.cancelAll("SetBiography");
                            String email = SharedPref.GetLoginCredentials(context).Email;
                            String token = SharedPref.GetLoginCredentials(context).Token;

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
                                                    // Common
                                                    queue.cancelAll("SetBiography");
                                                    ProcessOngoing2 = false;
                                                    LoadingDialog.hide();

                                                    // Post Steps
                                                    SharedPref.SetBio(context, bio);

                                                    // Finish
                                                    biographyText.setText(bio);
                                                    biographyDialog.hide();
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

                                                                Tool.CreateDefaultDialog(context, activity, title, body, actionType);
                                                            }
                                                            else
                                                            {
                                                                Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                            queue.cancelAll("SetBiography");
                                                            ProcessOngoing2 = false;
                                                            LoadingDialog.hide();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                        queue.cancelAll("SetBiography");
                                                        ProcessOngoing2 = false;
                                                        LoadingDialog.hide();
                                                    }

                                                    // Common
                                                    queue.cancelAll("SetBiography");
                                                    ProcessOngoing2 = false;
                                                    LoadingDialog.hide();
                                                }
                                            }
                                            else
                                            {
                                                Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                queue.cancelAll("SetBiography");
                                                ProcessOngoing2 = false;
                                                LoadingDialog.hide();
                                            }
                                        }
                                        catch (JSONException e)
                                        {
                                            Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                            queue.cancelAll("SetBiography");
                                            ProcessOngoing2 = false;
                                            LoadingDialog.hide();
                                        }
                                    },
                                    error -> {
                                        Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_NetworkError, ErrorInfo.Body_NetworkError, "ok");
                                        queue.cancelAll("SetBiography");
                                        ProcessOngoing2 = false;
                                        LoadingDialog.hide();
                                    });

                            request.setTag("SetBiography");
                            queue.add(request);
                        }
                    }
                    else
                    {
                        input.setError("Please enter biography");
                        input.requestFocus();
                    }
                }
                else
                {
                    biographyDialog.hide();
                }
            });

            // Show
            biographyDialog.show();
            biographyDialog.getWindow().setLayout(width, height);
            input.setText(biographyText.getText().toString().trim());
            input.requestFocus();
        });

        // Set Values
        displayNameText.setText(displayName);
        biographyText.setText(Html.fromHtml(biography, Html.FROM_HTML_MODE_COMPACT));

        // Show Dialog
        dialog.show();
        dialog.getWindow().setLayout(width, height);
    }

    public void MakeNewConversationDialog(Context context, Activity activity)
    {
        // Configuration
        Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_new_conversation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85);
        int height = dialog.getWindow().getAttributes().height;

        // Interface Variables
        EditText EmailInput = dialog.findViewById(R.id.NewConversationDialog_EmailInput);
        TextView AddButton = dialog.findViewById(R.id.NewConversationDialog_AddButton);

        // Listeners
        AddButton.setOnClickListener(view -> {
            String otherEmail = EmailInput.getText().toString().trim().toLowerCase();

            if (!otherEmail.isEmpty())
            {
                ProcessOngoingNC = false;

                /* Send Request */
                // Setup Loading Dialog
                Dialog LoadingDialog = new Dialog(context);
                LoadingDialog.setContentView(R.layout.dialog_loading);
                LoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                LoadingDialog.setCanceledOnTouchOutside(false);
                LoadingDialog.setCancelable(false);

                // Setup Queue
                RequestQueue queue = Volley.newRequestQueue(context);

                if (!ProcessOngoingNC)
                {
                    // Setup
                    ProcessOngoingNC = true;
                    LoadingDialog.show();
                    queue.cancelAll("NewConversation");
                    String email = SharedPref.GetLoginCredentials(context).Email;
                    String token = SharedPref.GetLoginCredentials(context).Token;

                    // Request
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                            ServerInfo.ServerLocation + "/get-basic-user-info?email=" + email + "&token=" + token + "&other_email=" + otherEmail,
                            null,
                            response -> {
                                try
                                {
                                    if (response.has("success"))
                                    {
                                        if (response.getBoolean("success"))
                                        {
                                            if (response.has("display_name"))
                                            {
                                                // Retrieve Data
                                                String displayName = response.getString("display_name");

                                                // Send Broadcast
                                                Intent intent = new Intent();
                                                intent.setAction("CHATS_FRAGMENT_FUNCTION__NEW_CONVERSATION");
                                                intent.putExtra("email", otherEmail);
                                                intent.putExtra("name", displayName);
                                                context.sendBroadcast(intent);

                                                // Dismiss Dialog
                                                dialog.dismiss();
                                            }
                                            else
                                            {
                                                Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                            }

                                            // Common
                                            queue.cancelAll("NewConversation");
                                            ProcessOngoingNC = false;
                                            LoadingDialog.hide();
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

                                                        Tool.CreateDefaultDialog(context, activity, title, body, actionType);
                                                    }
                                                    else
                                                    {
                                                        Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                    }
                                                }
                                                else
                                                {
                                                    Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                    queue.cancelAll("NewConversation");
                                                    ProcessOngoingNC = false;
                                                    LoadingDialog.hide();
                                                }
                                            }
                                            else
                                            {
                                                Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                                queue.cancelAll("NewConversation");
                                                ProcessOngoingNC = false;
                                                LoadingDialog.hide();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                    }

                                    // Common
                                    queue.cancelAll("NewConversation");
                                    ProcessOngoingNC = false;
                                    LoadingDialog.hide();
                                }
                                catch (JSONException e)
                                {
                                    Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_DataMismatch, ErrorInfo.Body_DataMismatch, "ok");
                                    queue.cancelAll("NewConversation");
                                    ProcessOngoingNC = false;
                                    LoadingDialog.hide();
                                }
                            },
                            error -> {
                                Tool.CreateDefaultDialog(context, activity, ErrorInfo.Title_NetworkError, ErrorInfo.Body_NetworkError, "ok");
                                queue.cancelAll("NewConversation");
                                ProcessOngoingNC = false;
                                LoadingDialog.hide();
                            });

                    request.setTag("NewConversation");
                    queue.add(request);
                }
            }
            else
            {
                EmailInput.setError("Please enter a valid email address");
                EmailInput.requestFocus();
            }
        });

        // Show Dialog
        dialog.show();
        dialog.getWindow().setLayout(width, height);
    }
}
