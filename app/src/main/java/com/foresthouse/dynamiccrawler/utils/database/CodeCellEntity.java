package com.foresthouse.dynamiccrawler.utils.database;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class CodeCellEntity implements Serializable {

    @Ignore
    public CodeCellEntity(int id, @NonNull String name, boolean thirdparty, boolean trigger, @Nullable String code, @Nullable Boolean isAnalyzed){
        this.id = id;
        this.name = name;
        this.thirdparty = thirdparty;
        this.trigger = trigger;
        this.code = code;
        this.isCompiled = true;
    }
    public CodeCellEntity(@NotNull String name, boolean thirdparty, boolean trigger, @Nullable String code){
        this.name = name;
        this.thirdparty = thirdparty;
        this.trigger = trigger;
        this.code = code;
        this.isCompiled = true;
    }

    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String name;

    public boolean thirdparty;

    public boolean trigger;

    public String code;

    public boolean isCompiled;

    @Ignore
    public int getCodeId(){
        return id;
    }
    @Ignore
    public String getCodeName(){
        return name;
    }
    @Ignore
    public boolean isThirdParty(){
        return thirdparty;
    }
    @Ignore
    public boolean isTriggered(){
        return trigger;
    }
    @Ignore
    public String getCode(){
        return code;
    }


    @Ignore
    @Override
    public boolean equals(@Nullable Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        CodeCellEntity entity = (CodeCellEntity) obj;
        return entity.id == this.id && entity.name.equals(this.name);
    }
}

