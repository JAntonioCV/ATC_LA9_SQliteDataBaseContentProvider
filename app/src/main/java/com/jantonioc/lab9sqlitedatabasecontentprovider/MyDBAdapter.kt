package com.jantonioc.lab9sqlitedatabasecontentprovider

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.ContactsContract

class MyDBAdapter(_context: Context) {
    private val DATABASE_NAME = "first_database"
    private val DATABASE_VERSION = 1

    private var mcontext : Context? = null
    private var mDBHelper : MyDBHelper? = null
    private var mSQLiteDatabase : SQLiteDatabase? = null

    init {
        this.mcontext = _context
        mDBHelper = MyDBHelper(_context,DATABASE_NAME,null,DATABASE_VERSION)
    }

    public fun openDataBase()
    {
        mSQLiteDatabase = mDBHelper?.writableDatabase
    }

    inner class MyDBHelper(context: Context?,name: String?, factory: SQLiteDatabase.CursorFactory?,version: Int): SQLiteOpenHelper(context, name, factory, version)
    {
        override fun onCreate(db: SQLiteDatabase?) {
            val query = "CREATE TABLE students(id integer primary key autoincrement, name text, faculty integer);"
            db?.execSQL(query)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            val query = "DROP TABLE IF EXISTS students;"
            db?.execSQL(query)
            onCreate(db)
        }
    }

    public fun insertStudent(name: String, faculty: Int)
    {
        val cv: ContentValues = ContentValues()
        cv.put("name",name)
        cv.put("faculty",faculty)
        mSQLiteDatabase?.insert("students",null,cv)
    }

    public fun selectAllStudents(): List<String> {
        val allStudents: MutableList<String> = ArrayList()
        val cursor: Cursor = mSQLiteDatabase?.query("students",null,null,null,null,null,null)!!
        if(cursor.moveToFirst())
        {
            do {
                allStudents.add(cursor.getString(1))
            }while (cursor.moveToNext())
        }
        return allStudents
    }

    public fun deleteAllStudents()
    {
        mSQLiteDatabase?.delete("students",null,null)

    }

}

