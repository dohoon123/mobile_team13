package com.example.round

import android.app.TaskStackBuilder
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


//메모 DB 저장, 데이터 가져오기
class MemoDBHelper(val context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_NAME = "myMemo.db"
        val DB_VERSION = 1
        val TABLE_NAME = "memoData"
        val MID = "id"
        val TITLE = "title"
        val CONTENT = "content"
    }

    //Create table
    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME ("+
                "$MID integer primary key autoincrement,"+
                "$TITLE string," +
                "$CONTENT string);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun insertMemo(memo:memoData):Boolean{
        val values = ContentValues()
        //MID값은 자동 증가
        values.put(TITLE, memo.title)
        values.put(CONTENT, memo.content)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }

    fun deleteMemo(mid: String): Boolean{
        val strsql = "select * from $TABLE_NAME where id ='$mid';"
        val db = writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        if(flag){
            cursor.moveToFirst()
            db.delete(TABLE_NAME, "$MID=?", arrayOf(mid))
        }
        cursor.close()
        db.close()
        return flag
    }


    fun getMemo(): ArrayList<memoData>{
        val strsql = "select * from $TABLE_NAME;"
        val db = readableDatabase
        var memo=ArrayList<memoData>()
        val cursor = db.rawQuery(strsql, null)

        if (cursor.moveToFirst()){
            do{
                val mid:Int = cursor.getInt(0)
                val title: String = cursor.getString(1)
                val content: String = cursor.getString(2)
                val data = memoData(mid, title, content)
                memo.add(data)
            }while (cursor.moveToNext())
        }else{
            //오류 출력
        }
        cursor.close()
        db.close()
        return memo
    }

}