package com.example.myapplication;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FavResults.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract FavResultsDao favResultsDao();

}
