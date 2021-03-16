package com.foresthouse.dynamiccrawler.utils;
//TODO DATAMANAGER의 Async를 모두 RxJava 또는 스레드로 바꿔서 사용하기...

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.util.Log;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.foresthouse.dynamiccrawler.ui.RecyclerAdapter;
import com.foresthouse.dynamiccrawler.utils.database.AppDataBase;
import com.foresthouse.dynamiccrawler.utils.database.CodeCellEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public class DataManager {
    private final static String TAG = "[ DataManager ]";

    public static AppDataBase DB;
    public static SharedPreferences RootPreference;
    private static ArrayList<CodeCellEntity> mArrayList;

    public static void initialize(Context ctx) {
        Initializer initializer = new Initializer(ctx);
        Thread thread = new Thread(initializer);
        thread.setDaemon(true);
        thread.start();
    }

    private static class Initializer implements Runnable {
        Context ctx;

        Initializer(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            DB = AppDataBase.getAppDataBase(ctx);
            RootPreference = PreferenceManager.getDefaultSharedPreferences(MainActivity.ApplicationContext);
            RootPreference.registerOnSharedPreferenceChangeListener(((sharedPreferences, key) -> { //언어 변경 리스너
                Locale locale = new Locale(RootPreference.getString("set_language", "sys"));
                Locale.setDefault(locale);
                Resources resources = activity.getResources(); //TODO Complete this part
                Configuration config = resources.getConfiguration();
                config.setLocale(locale);
                resources.updateConfiguration(config, resources.getDisplayMetrics());
            }));
        }
    }

    public static String getStringResource(int id) {
        return MainActivity.ApplicationContext.getString(id).replace("\\n", "\n");
    }

    //파일 입출력
    public static void writeFile(String path, String fileName, String content) {
        File file = new File(path, fileName);
        FileWriter fw = null;
        BufferedWriter bufw = null;
        try {
            fw = new FileWriter(file);
            bufw = new BufferedWriter(fw);
            bufw.write(content);
            bufw.flush();
        } catch (IOException e) {
            MainActivity.MainHandler
                    .post(() -> Generator.makeToastMessage(MainActivity.ApplicationContext, "IOException occurred\nPlease try again"));
        } finally {
            try {
                if (fw != null) fw.close();
                if (bufw != null) bufw.close();
            } catch (Exception ignore) {
            }
        }
    }

    public static void writeFileInBackground(String path, String fileName, String content) {
        ThreadManager.runInAnotherThread(() -> {
            writeFile(path, fileName, content);
        }, false);
    }

    public static String readFile(String path, String fileName) { // Run in MainThread because of synchronization.
        File file = new File(path, fileName);
        FileReader fr = null;
        BufferedReader bufr = null;
        String content = null;
        try {
            fr = new FileReader(file);
            bufr = new BufferedReader(fr);
            content = bufr.lines().collect(Collectors.joining());
        } catch (FileNotFoundException e) {
            Generator.makeToastMessage(MainActivity.ApplicationContext, "IOException occurred\nPlease try again");
        } finally {
            try {
                if (fr != null) fr.close();
                if (bufr != null) bufr.close();
            } catch (Exception ignore) {
            }
        }
        return content;
    }

    //코드 추가
    public static void insertData(String name, boolean thirdparty, boolean trigger, @Nullable String code) {
        new InsertAsyncTask().execute(new CodeCellEntity(name, thirdparty, trigger, code));
        Log.d(TAG, "insertData: DB에 < " + name + " > 코드 추가");
    }

    private static class InsertAsyncTask extends AsyncTask<CodeCellEntity, Void, Void> {
        @Override
        protected Void doInBackground(CodeCellEntity... codeCellEntities) { //TODO * DB에 등록하는 동안 원 뺑글뺑글 돌게 만들자
            DB.DAO().insertAll(codeCellEntities);
            return null;
        }
    }

    //전체 코드 불러오기
    public static ArrayList<CodeCellEntity> getAllCodeData(){
        new SelectAllAsyncTask();
        return mArrayList;
    }

    //DB 반영
    public static void reflectAllCodeData(){
        new SelectAllAsyncTask().execute();
    }
    //DB 코드데이터 최신상태를 리사이클러뷰에 반영
    public static void reflectAllCodeData(RecyclerAdapter adapter){
        new SelectAllAsyncTask(adapter).execute();
    }

    private static class SelectAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private RecyclerAdapter adapter = null;

        private SelectAllAsyncTask() {
            //Don't do anything.
        }

        private SelectAllAsyncTask(RecyclerAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: 백그라운드에서 코드데이터 불러오는중...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mArrayList = (ArrayList<CodeCellEntity>) DB.DAO().getAllCodeData();
            Log.d(TAG, "doInBackground: 백그라운드에서 코드데이터 불러오기 완료");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (adapter != null) {
                adapter.CodeCellEntityBundle = mArrayList;
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onPostExecute: 코드리스트 새로고침 완료");
            }
        }
    }

    //코드 삭제
    public static void removeCode(CodeCellEntity entity) {
        new Thread(new RemoveCode(entity)).start();
    }
    private static class RemoveCode implements Runnable{
        private final CodeCellEntity entity;
        public RemoveCode(CodeCellEntity entity){
            this.entity = entity;
        }

        @Override
        public void run() {
            DataManager.DB.DAO().deleteCode(entity);
        }
    }

    //코드 저장
    public static void saveCode(CodeCellEntity entity) {
        new Thread(new SaveCode(entity)).start();
    }
    private static class SaveCode implements Runnable{
        private final CodeCellEntity entity;
        public SaveCode(CodeCellEntity entity) {this.entity = entity;}

        @Override
        public void run() {
            DataManager.DB.DAO().saveCode(entity.id, entity.code);
        }
    }
    //코드 불러오기
    public static void getCodeContent(CodeCellEntity entity, Reflectable reflectable){
        new GetCodeContent(entity, reflectable).execute();
    }

    private static class GetCodeContent extends AsyncTask<Void, Void, Void> {
        private final CodeCellEntity entity;
        private final Reflectable reflectable;
        private String code;

        public GetCodeContent(CodeCellEntity entity, Reflectable reflectable) {
            this.entity = entity;
            this.reflectable = reflectable;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            code = DataManager.DB.DAO().getCodeContent(String.valueOf(entity.getCodeId())).get(0).getCode();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            reflectable.reflectData(code);
        }
    }
}

