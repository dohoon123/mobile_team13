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
        val ALARM = "alarmChecked"
        val checkDisposable="disposable"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME ("+
                "$RID integer primary key autoincrement,"+
                "$RNAME string," +
                "$RTERM integer," +
                "$ALARM integer,"+
                "$checkDisposable integer);"
        db!!.execSQL(create_table)
    }

    fun reset(){
        var db=writableDatabase
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME+";");
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun insertRoutine(routine:routineData):Boolean{
        val values = ContentValues()
        //RID값은 자동 증가
        values.put(RNAME, routine.routineName)
        values.put(RTERM, routine.routineTerm)//기간 24시간 고정.
        values.put(ALARM, routine.alarmChecked)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }


    //데이터 구조가 바껴서 일회용 시간표와 다회용 시간표를 구분해서 인서트해주는 함수
    fun insertRoutine_disposable(routine:routineData):Boolean{
        val values = ContentValues()
        //RID값은 자동 증가
        values.put(RNAME, routine.routineName)
        values.put(RTERM, routine.routineTerm)//기간 24시간 고정.
        values.put(ALARM, routine.alarmChecked)
        values.put(checkDisposable,routine.disposable)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }


    fun getLatestrID() : Int{
        val strsql="select * from $TABLE_NAME;"//전부 가져와서 가장 최근 루틴ID 반납
        val db = readableDatabase
        var cursorDriver = db.rawQuery(strsql, null)
        cursorDriver.moveToLast()//마지막으로 이동

        if(cursorDriver.columnCount != 0) {
            return cursorDriver.getInt(0)
        } else {
            return cursorDriver.getInt(0)
        }
    }

    //루틴의 이름을 받아서 루틴 아이디를 반환해주는 함수. 일치하는 루틴 없으면 -1 반환
    fun getrId(rname: String) : Int {
        val strsql="select * from $TABLE_NAME where $RNAME = ${rname};"
        val db = readableDatabase
        var rId:Int
        var cursorDriver = db.rawQuery(strsql, null)
        cursorDriver.moveToFirst()
        //Name도 후보키로 중복된 데이터가 없어야할것같아요
        if(cursorDriver.count==1) {
            rId = cursorDriver.getInt(0)
        } else {
            rId = -1
        }
        cursorDriver.close()
        db.close()
        return rId
    }

    //루틴의 ID받아서 Rid와 일치하는 튜플의 rname 반환
    fun getrName(rid: Int) : String {
        val strsql="select * from $TABLE_NAME where $RID = ${rid};"
        val db = readableDatabase
        var rname: String
        var cursorDriver = db.rawQuery(strsql, null)
        cursorDriver.moveToFirst()
        if(cursorDriver.count==1) {
            rname = cursorDriver.getString(1)
        } else {
            rname = ""
        }
        cursorDriver.close()
        db.close()
        return rname
    }

    fun selectAll():ArrayList<routineData>{
        //routine데이터 전부 다 가져올거임 Array로 리턴
        val strsql="select * from $TABLE_NAME;"
        val db= readableDatabase
        var dataArray = ArrayList<String>()//속성
        var routineArray = ArrayList<routineData>()//전체 데이터
        var cursorDriver=db.rawQuery(strsql,null)

        cursorDriver.moveToFirst()
        val attrcount=cursorDriver.columnCount

        var tempString=""

        do{
            if(cursorDriver.count==0){return routineArray}
            else{
                tempString=""
                var routineid= cursorDriver.getString(0)
                var routineName= cursorDriver.getString(1)
                var routineTerm= cursorDriver.getString(2)
                var alarmChecked = cursorDriver.getString(3).toInt()
                var disposal=cursorDriver.getString(4).toString().toInt()
                routineArray.add(routineData(routineid.toInt(), routineName, routineTerm.toInt(),alarmChecked,disposal))
            }
        }while(cursorDriver.moveToNext())

        cursorDriver.close()
        db.close()
        return routineArray
    }


    fun deleteRoutine(routine:timetableData):Boolean{
        val strsql="select * from $TABLE_NAME where $RID='${routine.routineID}';"
        val db=writableDatabase
        val cursor=db.rawQuery(strsql,null)
        val flag=cursor.count!=0
        if(flag){
            cursor.moveToFirst()
            db.delete(TABLE_NAME,"$RID=?", arrayOf(routine.routineID.toString()))
        }
        cursor.close()
        db.close()
        return flag
    }

    //1회용 시간표인지의 여부 알려주는 함수
    fun isDisposable(routineId:String):String{
        var temp=""
        val strsql="select * from $TABLE_NAME where $RID='${routineId}';"
        val db=readableDatabase
        val cursor=db.rawQuery(strsql,null)
        val flag=cursor.count!=0

        if(flag){
            cursor.moveToFirst()
            temp=cursor.getString(4).toString()
        }
        cursor.close()
        db.close()

        return temp
    }

    fun returnDataInfo(routineId: String):String{
        var temp=""
        val strsql="select * from $TABLE_NAME where $RID='${routineId}';"
        val db=readableDatabase
        val cursor=db.rawQuery(strsql,null)
        val flag=cursor.count!=0

        if(flag){
            cursor.moveToFirst()
            temp+=cursor.getString(0)
            temp+=cursor.getString(1)
            temp+=cursor.getString(2)
            temp+=cursor.getString(3)
            temp+=cursor.getString(4)
        }
        cursor.close()
        db.close()

        return temp
    }


}