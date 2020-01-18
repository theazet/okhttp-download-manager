package com.aze.download_manager.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER) {
    companion object {
        const val DATABASE_NAME = "tasks"
        const val DATABASE_VER = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS tasks (id integer PRIMARY KEY AUTOINCREMENT,url varchar ( 64 ),path varchar ( 64 ),name varchar(64),status integer,time timestamp,priority integer,support short,total integer,progress integer )")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}