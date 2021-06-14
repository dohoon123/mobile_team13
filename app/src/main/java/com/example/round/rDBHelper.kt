package com.example.round

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class rDBHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_NAME = "myRoutineDB.db"
        val DB_VERSION = 1
        val TABLE_NAME = "routineData"
        val RID = "routineID"
        val RNAME = "routineName"
        val RTERM = "routineTerm"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME ("+
                "$RID integer primary key,"+
                "$RNAME string," +
                "$RTERM integer);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun insertRoutine(routine:routineData):Boolean{
        val values = ContentValues()
        //RID값은 자동 증가
        values.put(RID, routine.routineID)
        values.put(RNAME, routine.routineName)
        values.put(RTERM, routine.routineTerm)//기간 24시간 고정.
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }

    //루틴의 이름을 받아서 루틴 아이디를 반환해주는 함수. 일치하는 루틴 없으면 -1 반환
    fun getrId(rname: String) : Int {
        val strsql="select * from $TABLE_NAME where $RNAME = $rname;"
        val db = readableDatabase
        var rId:Int
        var cursorDriver=db.rawQuery(strsql, null)
        cursorDriver.moveToFirst()
        //Name도 후보키로 중복된 데이터가 없어야할것같아요
        if(cursorDriver.columnCount==1) {
            rId = cursorDriver.getInt(0)
        } else {
            rId = -1
        }
        cursorDriver.close()
        db.close()
        return rId

    }

    fun selectAll():ArrayList<routineData>{
        //routine데이터 전부 다 가져올거임 Array로 리턴
        val strsql="select * from $TABLE_NAME;"
        val db=readableDatabase
        var dataArray = ArrayList<String>()//속성
        var routineArray = ArrayList<routineData>()//전체 데이터
        var cursorDriver=db.rawQuery(strsql,null)

        cursorDriver.moveToFirst()
        val attrcount=cursorDriver.columnCount

        var tempString=""
        //맨 위에 그 타이틀 읽어와서 붙여준 거임
        var routineid=cursorDriver.getString(0)
        var routineName=cursorDriver.getString(1)
        var routineTerm=cursorDriver.getString(2)
        routineArray.add(routineData(routineid.toInt(),routineName,routineTerm.toInt()))
        //데이터를 읽어와서 넣어줄 거임
        //(var routineID: Int, var routineName: String, var routineTerm: Int
        do{
            if(cursorDriver.count==0){return routineArray}
            else{
                tempString=""
                var routineid=cursorDriver.getString(0)
                var routineName=cursorDriver.getString(1)
                var routineTerm=cursorDriver.getString(2)
                routineArray.add(routineData(routineid.toInt(),routineName,routineTerm.toInt()))
              }
        }while(cursorDriver.moveToNext())

        cursorDriver.close()
        db.close()
        return routineArray
    }

}