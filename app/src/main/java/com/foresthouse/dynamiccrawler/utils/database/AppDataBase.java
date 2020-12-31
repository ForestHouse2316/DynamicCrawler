package com.foresthouse.dynamiccrawler.utils.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//DB 객체 싱글톤 생성
@Database(entities = {CodeCellEntity.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public static AppDataBase INSTANCE;
    public abstract CodeCellEntityDao DAO();

    /*getAppDataBase 메서드는 DAO 를 포함하는 DB 객체 싱글톤 생성을 담당하는 메서드이다.
    * 이 메서드는 INSTANCE 가 오로지 하나만 생성되기 때문에 객체 상관 없이 DataManager 의 DB 변수를 참조하면 된다. */
    public static AppDataBase getAppDataBase(Context ctx) {
        if (INSTANCE == null) { //중복생성 방지
            INSTANCE = Room.databaseBuilder(ctx, AppDataBase.class, "CodeData.DB").build();
            //만약 DB를 하나 더 만들면 DAO 이름 CodeDAO 등과 같이 바꿔줘야함. 물론 INSTANCE 도 분리하던가 아니면 메소드를 분리하던가 해야함.
        }
        return INSTANCE;
    }

    //DB 객체 제거
    public static void destroyInstance(){
        INSTANCE = null;
    }
}
