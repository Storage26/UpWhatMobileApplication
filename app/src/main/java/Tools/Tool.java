package Tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.ds.upwhat.LocalDatabase.SharedPref;
import com.ds.upwhat.R;

import java.util.Map;

public class Tool
{
    public static void Hide(ActionBar bar)
    {
        if (bar != null)
        {
            bar.hide();
        }
    }

    public static void CreateDefaultDialog(Context context, Activity activity, String title, String body, String action_type)
    {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_default);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        // Interface Variables
        TextView TitleText = dialog.findViewById(R.id.DefaultDialog_Title);
        TextView BodyText = dialog.findViewById(R.id.DefaultDialog_Body);
        TextView ActionButton = dialog.findViewById(R.id.DefaultDialog_ActionButton);

        // Setup Interface
        TitleText.setText(title);
        BodyText.setText(body);

        // Conditions
        if (action_type.equals("ok"))
        {
            ActionButton.setText(context.getString(R.string.ok).toUpperCase());
            ActionButton.setOnClickListener(view -> dialog.dismiss());
        }
        else if (action_type.equals("logout_and_done"))
        {
            SharedPref.Logout(context);
            ActionButton.setText(context.getString(R.string.ok).toUpperCase());
            ActionButton.setOnClickListener(view -> {
                Toast.makeText(context, "Please restart the application", Toast.LENGTH_SHORT).show();
                activity.finishAffinity();
            });
        }

        dialog.show();
    }
}
