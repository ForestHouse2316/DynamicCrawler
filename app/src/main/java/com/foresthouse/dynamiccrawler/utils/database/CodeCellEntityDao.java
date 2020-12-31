package com.foresthouse.dynamiccrawler.utils.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CodeCellEntityDao {
    //전체 코드 목록 조회
    @Query("select id, name, thirdparty, `trigger`, isCompiled from codecellentity")
    List<CodeCellEntity> getAllCodeData();
    //전체 코드 목록 조회 (Observer)
    @Query("select id, name, thirdparty, `trigger`, isCompiled from codecellentity")
    LiveData<CodeCellEntity> getDBObserver();
    //특정 코드 정보 조회
    @Query("select id, name, thirdparty, `trigger`, isCompiled from codecellentity where name = :name")
    LiveData<CodeCellEntity> getCodeData(String name);
    //특정 코드 내용 가져오기. select * 하지 않으면 컴파일 에러남.
    @Query("select * from codecellentity where id = :id")
    List<CodeCellEntity> getCodeContent(String id);

    //코드 저장
    @Query("update codecellentity set code=:code where id = :id")
    void saveCode(int id, String code);

//@Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert
    public void insertAll(CodeCellEntity... entities);

    @Delete
    public void deleteCode(CodeCellEntity entity);

    @Update
    public void updateAll(CodeCellEntity entities);
}
