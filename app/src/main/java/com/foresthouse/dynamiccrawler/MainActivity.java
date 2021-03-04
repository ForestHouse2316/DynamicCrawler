package com.foresthouse.dynamiccrawler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import com.foresthouse.dynamiccrawler.ui.dialog.CreateCodeDialog;
import com.foresthouse.dynamiccrawler.utils.DataManager;
import com.foresthouse.dynamiccrawler.utils.Waitable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

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

    public static final Handler MainHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //초기 로딩화면 딜레이
        try {
            Thread.sleep(1000);
            DataManager.initialize(this);
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

    public static void postAndWait(Waitable waitable, boolean useViewListener, Runnable r) { // JSEngine 스레드에서 호출되고 여기서 실제로 메인스레드로 넘어감
        //
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                //                MainHandler.post(r);
                r.run(); // Already main thread
                if (!useViewListener) { // 따로 뷰의 리스너를 통해 작업 완료시 스레드 재게를 처리하지 않는다면 여기서 JS 스레드를 다시 시작시켜줌
                    waitable.stopWaiting();
                    Log.d(TAG, "run: post 처리 완료. 대기 해제");
                }
            }
        });
        waitable.startWaiting(Integer.parseInt(DataManager.RootPreference.getString("interpreting_interval", "10")));
        Log.d(TAG, "postAndWait: post 후 대기중. . .");
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


}




