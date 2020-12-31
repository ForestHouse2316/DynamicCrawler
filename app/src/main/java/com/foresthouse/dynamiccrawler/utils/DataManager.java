package com.foresthouse.dynamiccrawler.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.foresthouse.dynamiccrawler.utils.database.AppDataBase;
import com.foresthouse.dynamiccrawler.utils.database.CodeCellEntity;
import com.foresthouse.dynamiccrawler.ui.RecyclerAdapter;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DataManager {
    private final static String TAG = "[ DataManager ]";

    public static AppDataBase DB;
    private static ArrayList<CodeCellEntity> mArrayList;

    public static String getStringResource(Context context, int id) {
        return context.getString(id).replace("\\n", "\n");
    }













    public static void Initialize(Context ctx) {
        DB = AppDataBase.getAppDataBase(ctx);
    }

    //코드 추가
    public static void insertData(String name, boolean thirdparty, boolean trigger, @Nullable String code){
        new InsertAsyncTask().execute(new CodeCellEntity(name, thirdparty, trigger, code));
        Log.d(TAG, "insertData: DB에 < "+name+" > 코드 추가");
    }
    public static class InsertAsyncTask extends AsyncTask<CodeCellEntity, Void, Void> {
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
    public static class SelectAllAsyncTask extends AsyncTask<Void, Void, Void>{

        private RecyclerAdapter adapter = null;

        private SelectAllAsyncTask(){
            //Don't do anything.
        }
        private SelectAllAsyncTask(RecyclerAdapter adapter){
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
    public static class GetCodeContent extends AsyncTask<Void, Void, Void> {
        private final CodeCellEntity entity;
        private final Reflectable reflectable;
        private String code;
        public GetCodeContent(CodeCellEntity entity, Reflectable reflectable){
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
