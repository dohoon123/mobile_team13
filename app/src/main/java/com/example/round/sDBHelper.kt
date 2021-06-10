package com.example.round

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class sDBHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_NAME = "myScheduleDB.db"
        val DB_VERSION = 1
        val TABLE_NAME = "scheduleData"
        val RID = "routineID"
        val SID = "scheduleID"
        val SNAME = "scheduleName"
        val SSTART= "startTime"
        val SEND = "endTime"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME ("+
                "$RID integer,"+
                "$SID integer primary key autoincrement,"+
                "$SNAME string," +
                "$SSTART integer,"+
                "$SEND integer);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun insertSchedule(schedule:scheduleData):Boolean{
        val values = ContentValues()
        values.put(RID, schedule.routineID)
        values.put(SNAME, schedule.scheduleName)
        values.put(SSTART, schedule.startTime)
        values.put(SEND, schedule.endTime)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }



    fun selectAll():ArrayList<scheduleData>{
        //routine데이터 전부 다 가져올거임 Array로 리턴
        val strsql="select * from ${sDBHelper.TABLE_NAME};"
        val db=readableDatabase
        var scheduleArray = ArrayList<scheduleData>()//전체 데이터
        var cursorDriver=db.rawQuery(strsql,null)

        cursorDriver.moveToFirst()
        val attrcount=cursorDriver.columnCount

        //맨 위에 그 타이틀 읽어와서 붙여준 거임
        var routineid=cursorDriver.getString(0)
        var scheduleid=cursorDriver.getString(1)
        var scheduleName=cursorDriver.getString(2)
        var startTime=cursorDriver.getString(3)
        var endTime=cursorDriver.getString(4)
        scheduleArray.add(scheduleData(routineid.toInt(),scheduleid.toInt(),scheduleName,startTime.toInt(),endTime.toInt()))
        //데이터를 읽어와서 넣어줄 거임
        //var routineID: Int, var scheduleID: Int, var scheduleName: String, var startTime: Int, var endTime: Int
        do{
            if(cursorDriver.count==0){return scheduleArray}
            else{
                var routineid=cursorDriver.getString(0)
                var scheduleid=cursorDriver.getString(1)
                var scheduleName=cursorDriver.getString(2)
                var startTime=cursorDriver.getString(3)
                var endTime=cursorDriver.getString(4)
                scheduleArray.add(scheduleData(routineid.toInt(),scheduleid.toInt(),scheduleName,startTime.toInt(),endTime.toInt()))
            }
        }while(cursorDriver.moveToNext())

        cursorDriver.close()
        db.close()
        return scheduleArray
    }

}