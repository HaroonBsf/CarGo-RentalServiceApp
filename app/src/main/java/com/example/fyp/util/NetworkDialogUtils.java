package com.example.fyp.util;

import static com.example.fyp.network.ConnectionReceiver.isConnected;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fyp.R;

public class NetworkDialogUtils {

    public static void showNetworkDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.NetworkAlertDialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.network_error_dialog, null);
        Button btnRetry = view.findViewById(R.id.btnRetry);
        builder.setCancelable(false);
        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.AnimationForDialog);
        dialog.show();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(context)) {
                    dialog.dismiss();

                }else {
                    Toast.makeText(context, "Internet is not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
