package com.foresthouse.dynamiccrawler.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.ui.view.LineNumberEditText;
import com.foresthouse.dynamiccrawler.utils.DataManager;
import com.foresthouse.dynamiccrawler.utils.Generator;
import com.foresthouse.dynamiccrawler.utils.Reflectable;
import com.foresthouse.dynamiccrawler.utils.database.CodeCellEntity;
//import com.foresthouse.dynamiccrawler.utils.js.JavaScriptManager;
import com.foresthouse.dynamiccrawler.utils.js.JavaScriptManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.icpa.dynamiccrawler.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class EditorActivity extends AppCompatActivity implements Reflectable {
    private static final String TAG = "EditorActivity";

    private CodeCellEntity curCell;
    private LineNumberEditText editorBox;
    private MutableLiveData<String> QueriedCode = new MutableLiveData<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        editorBox = findViewById(R.id.et_code);
        FloatingActionButton saveButton = findViewById(R.id.fab_save_code);
        curCell = RecyclerAdapter.getSelectedCell();
        Log.d(TAG, "onCreate: "+curCell.getCodeName()+" 코드의 에디터를 열었습니다.");


        editorBox.setText("Loading Code. . .");
        QueriedCode.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "onChanged: DB로부터 불러온 코드를 반영했습니다.");
                editorBox.setText(s);
            }
        });

        DataManager.getCodeContent(curCell, this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // Short click
                save();
            }
        });
        saveButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) { // Long click
                save();
                JavaScriptManager.runJS(curCell.code);
                return false;
            }
        });

    }

    private void save() {
        curCell.code = Objects.requireNonNull(editorBox.getText()).toString();
        DataManager.saveCode(curCell);
        Log.d(TAG, "onClick: 코드 저장됨. ID = " + curCell.getCodeId());
        Generator.makeToastMessage(MainActivity.ApplicationContext, getText(R.string.str_saved).toString());
    }

    public void reflectData(String code){
        try {
            QueriedCode.postValue(code);
        } catch (NullPointerException e) {
            QueriedCode.postValue("//Start a new code!");
        }
    }


}