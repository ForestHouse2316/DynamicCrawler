package com.foresthouse.dynamiccrawler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import com.foresthouse.dynamiccrawler.ui.dialog.CreateCodeDialog;
import com.foresthouse.dynamiccrawler.utils.DataManager;
import com.foresthouse.dynamiccrawler.utils.Generator;
import com.foresthouse.dynamiccrawler.utils.js.Api;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.icpa.dynamiccrawler.R;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "[ MainActivity ]";
    public static Boolean Initialized = false;

    public static Context ApplicationContext;
    @SuppressLint("StaticFieldLeak")
    public static MainActivity MainContext;

    private AppBarConfiguration mAppBarConfiguration;
    public static FloatingActionButton fab;
    public View Include;

    public static int CurrentFragment;
    public static int PrevFragment;
    public static final int FRAGMENT_CODE_LIST = 1;
    public static final int FRAGMENT_SHARE = 2;
    public static final int FRAGMENT_SETTING = 3;
    public static final int FRAGMENT_HELP = 4;





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        //초기 로딩화면 딜레이
        try {
            Thread.sleep(1000);
            DataManager.Initialize(this);
            setTheme(R.style.AppTheme);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ApplicationContext = getApplicationContext();
        MainContext = this;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Include = findViewById(R.id.include_main_home);

        //프리셋 정의 구역
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //fab 정의 및 리스너 등록, fab 을 통한 코드셀 추가
        fab = findViewById(R.id.btn_add_code);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (CurrentFragment) {
                    case FRAGMENT_CODE_LIST:
                        new CreateCodeDialog(MainActivity.this).showDialog();
                    case FRAGMENT_SHARE:
                    case FRAGMENT_SETTING:
                        //Do not do anything
                    case FRAGMENT_HELP:
                        //Do not do anything
                    default:
                }
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration =
                new AppBarConfiguration.Builder(R.id.nav_codes, R.id.nav_share, R.id.nav_setting, R.id.nav_help).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        Initialized = true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        MainContext = null;
    }



//    public void runJS(String code) {
//        org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();
//        rhino.setOptimizationLevel(-1);
//        try{
//            // 스코프 지정
//            Scriptable scope = rhino.initStandardObjects();
//            // Context 스코프 설정 (변수명 Context 로 앱 콘텍스트 사용 가능)
//            ScriptableObject.putProperty(scope, "ctx", this);
//
//            // 커스텀 API 클래스를 라이노 엔진에 선언
//            ScriptableObject.defineClass(scope, Api.class);
//
//
//            rhino.evaluateString(scope, code, "JavaScript", 1, null);
//        } catch (Exception e) {
//            Generator.makeToastMessage(MainActivity.ApplicationContext, e.toString());
//            Log.d(TAG, "runJS: "+e.toString());
//        } finally {
//            org.mozilla.javascript.Context.exit();
//        }
//    }




}


