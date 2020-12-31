package com.foresthouse.dynamiccrawler.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.Nullable;

public class Generator {
    public final static String TAG = "[ Generator ]";

    public static void makeYNDialog(Context context, String title, String content, @Nullable String btnY, @Nullable String btnN , @Nullable String btnNeutral,
                                       final @Nullable DialogInterface.OnClickListener YesCommand,
                                       final @Nullable DialogInterface.OnClickListener NoCommand,
                                    final @Nullable DialogInterface.OnClickListener NeutralCommand,
                                    final @Nullable DialogInterface.OnCancelListener CancelCommand){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(content);

        if (btnY != null) {
            builder.setPositiveButton(btnY, YesCommand);
        }
        if (btnN != null) {
            builder.setNegativeButton(btnN, NoCommand);
        }
        if (btnNeutral != null) {
            builder.setNeutralButton(btnNeutral, NeutralCommand);
        }
        if (CancelCommand == null) { //리스너에 null 등록하면 취소불가
            builder.setCancelable(false);
        } else{
            builder.setOnCancelListener(CancelCommand);
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Log.d(TAG, "makeYNDialog: <"+title+"> 다이얼로그 생성");
    }

    public static void makeToastMessage(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

}