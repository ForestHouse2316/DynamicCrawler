package com.foresthouse.dynamiccrawler.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.foresthouse.dynamiccrawler.R;
import com.foresthouse.dynamiccrawler.ui.nav_fragment.codelist.CodeListFragment;
import com.foresthouse.dynamiccrawler.utils.DataManager;

public class CreateCodeDialog {
    private static final  String TAG = "[ CreateCodeDialog ]";
    private final Context context;
    public CreateCodeDialog(Context context) {
        this.context = context;
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_create_code);
        final EditText editText = dialog.findViewById(R.id.et_code_name);
        final Button createBtn = dialog.findViewById(R.id.btn_create_code);
        final Button cancelBtn = dialog.findViewById(R.id.btn_cancel_create_code);
        final TextView hint = dialog.findViewById(R.id.tv_create_code_hint);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editText.getText().toString();
                if (name.equals("")) {
                    hint.setText(DataManager.getStringResource(R.string.str_create_code_hint));
                    hint.setVisibility(View.VISIBLE);
                } else {
                    CodeListFragment.ignoreChangeOnce = false;
                    DataManager.insertData(name, false, false, null);
                    try {
                        CodeListFragment.CodeList.scrollToPosition(DataManager.getAllCodeData().size() - 1);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        editText.setSelected(true);
        dialog.setCancelable(true);
        dialog.show();

    }

}
