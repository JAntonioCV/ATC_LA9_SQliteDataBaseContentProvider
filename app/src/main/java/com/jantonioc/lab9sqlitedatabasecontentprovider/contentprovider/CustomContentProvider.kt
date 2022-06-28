package com.jantonioc.lab9sqlitedatabasecontentprovider.contentprovider

import android.content.*
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils

class CustomContentProvider: ContentProvider() {

    companion object{
        val ID: String = "id"
        val PROVIDER_NAME: String = "com.jantonioc.lab9sqlitedatabasecontentprovider.contentprovider"
        val NAME : String = "name"
        val NICK_NAME: String = "nickname"
        val CONTENT_URI: Uri  = Uri.parse("content://$PROVIDER_NAME/nicknames")
    }

    val NICKNAME_COLUMN : Int = 1
    val NICKNAME_ID_COLUMN : Int = 2

    private val mNickNameMap = HashMap<String,String>()

    var mUriMatcher : UriMatcher? = null

    private var mDatabase: SQLiteDatabase? = null
    val DATABASE_NAME = "NicknamesDirectory"
    val TABLE_NAME = "Nicknames"
    val DATABASE_VERSION = 1
    val CREATE_TABLE = "CREATE TABLE $TABLE_NAME(id integer PRIMARY KEY AUTOINCREMENT, name text NOT NULL, nickname TEXT NOT NULL);"

    init {
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        mUriMatcher?.addURI(PROVIDER_NAME, "nicknames", NICKNAME_COLUMN)
        mUriMatcher?.addURI(PROVIDER_NAME, "nicknames/#", NICKNAME_ID_COLUMN)
    }

    override fun onCreate(): Boolean {
        val context = context
        val mDbHelper = DBHelper(context)

        mDatabase = mDbHelper.writableDatabase
        return mDatabase != null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = TABLE_NAME

        when(mUriMatcher?.match(uri))
        {
            NICKNAME_COLUMN -> queryBuilder.setProjectionMap(mNickNameMap)
            NICKNAME_ID_COLUMN -> queryBuilder.appendWhere(ID + "=" + uri.lastPathSegment)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        var sortorder = NAME
        if(!TextUtils.isEmpty(sortorder))
        {
            sortorder = sortOrder.toString()
        }

        val cursor = queryBuilder.query(mDatabase,projection,selection,selectionArgs,null,null,sortorder)
        cursor.setNotificationUri(context!!.contentResolver,uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        when(mUriMatcher?.match(uri)){
            NICKNAME_COLUMN -> return "vnd.android.cursor.dir/vnd.customcontentprovider.nicknames"
            NICKNAME_ID_COLUMN -> return "vnd.android.cursor.item/vnd.customcontentprovider.nicknames"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val row = mDatabase?.insert(TABLE_NAME,"",values)
        if(row != null)
        {
            if(row > 0)
            {
                val newUri = ContentUris.withAppendedId(CONTENT_URI,row)
                context?.contentResolver?.notifyChange(newUri,null)
                return newUri
            }
        }
        throw  SQLException("Fail to add new record into $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        var count = 0
        when(mUriMatcher?.match(uri)){
            NICKNAME_COLUMN -> count = mDatabase?.delete(TABLE_NAME, selection, selectionArgs)!!
            NICKNAME_ID_COLUMN -> {
                var whereClause = ""
                if (!TextUtils.isEmpty(selection)){
                    whereClause = " AND($selection)"
                }
                count = mDatabase?.delete("nicknames", "$ID = ${uri?.lastPathSegment} $whereClause", selectionArgs)!!
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        var count = 0
        when(mUriMatcher?.match(uri)){
            NICKNAME_COLUMN -> count = mDatabase?.update(TABLE_NAME, values, selection, selectionArgs)!!
            NICKNAME_ID_COLUMN -> {
                var whereClause = ""
                if (!TextUtils.isEmpty(selection)){
                    whereClause = " AND($selection)"
                }
                count = mDatabase?.update(TABLE_NAME, values, "$ID = ${uri.lastPathSegment} $whereClause", selectionArgs)!!
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    inner class DBHelper(context: Context?): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
    {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(CREATE_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            val query = "DROP TABLE IF EXISTS $TABLE_NAME;"
            db?.execSQL(query)
            onCreate(db)
        }
    }

}