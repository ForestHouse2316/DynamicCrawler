package com.foresthouse.dynamiccrawler.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.R;
import com.foresthouse.dynamiccrawler.ui.view.LineNumberEditText;
import com.foresthouse.dynamiccrawler.utils.DataManager;
import com.foresthouse.dynamiccrawler.utils.Generator;
import com.foresthouse.dynamiccrawler.utils.Reflectable;
import com.foresthouse.dynamiccrawler.utils.database.CodeCellEntity;
import com.foresthouse.dynamiccrawler.utils.js.JavaScriptEngineManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

//import com.foresthouse.dynamiccrawler.utils.js.JavaScriptEngineManager;

public class EditorActivity extends AppCompatActivity implements Reflectable {
    private static final String TAG = "EditorActivity";

    private CodeCellEntity curCell;
    private LineNumberEditText editorBox;
    public static WebView crawler;
    private final MutableLiveData<String> QueriedCode = new MutableLiveData<>();

    private boolean ignoreOnce = false;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        editorBox = findViewById(R.id.et_code);
        crawler = findViewById(R.id.webview_crawler);
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
//                Thread JSEngine = new Thread(new JavaScriptEngineManager(curCell.code));
                JavaScriptEngineManager jsManager = new JavaScriptEngineManager(curCell.code); //이런 식으로 해서 크롤링 대기열 만드는거 어떰? 큐 만들어서 스레드 객체 넣어놓고 순차적으로 스타트 시키는거야!
                jsManager.startEngineInBackground();
                ignoreOnce = true;
                return false;
            }
        });



    }

    private void save() {
        if (!ignoreOnce) {
            curCell.code = Objects.requireNonNull(editorBox.getText()).toString();
            DataManager.saveCode(curCell);
            Log.d(TAG, "onClick: 코드 저장됨. ID = " + curCell.getCodeId());
            Generator.makeToastMessage(MainActivity.ApplicationContext, getText(R.string.str_saved).toString());
        } else {
            ignoreOnce = false; // Toast message should be shown only 1 time
        }
    }

    public void reflectData(String code){
        try {
            QueriedCode.postValue(code);
        } catch (NullPointerException e) {
            QueriedCode.postValue("//Start a new code!");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        crawler.destroy();
        crawler = null;
    }
}