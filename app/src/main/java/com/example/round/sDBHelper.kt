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

    fun insertSchedule(schedule:scheduleData): Boolean{
        var checkFlag:Boolean = scheduleCheck(schedule)
        var flag = false
        if (checkFlag==false) {
            val values = ContentValues()
            values.put(RID, schedule.routineID)
            values.put(SNAME, schedule.scheduleName)
            values.put(SSTART, schedule.startTime)
            values.put(SEND, schedule.endTime)
            val db = writableDatabase
            flag = db.insert(TABLE_NAME, null, values) > 0
            db.close()
            return flag
        }
        return flag
    }

    fun deleteSchedule(sid: Int): Boolean {
        val strsql = "select * from ${TABLE_NAME} where ${SID}='${sid}';"

        val db = writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        if (flag) {
            db.execSQL("delete from ${TABLE_NAME} where ${SID} = '$sid';")
        }
        cursor.close()
        db.close()
        return flag
    }

    fun deleteScheduleByR(rid: Int): Boolean {
        val strsql = "select * from ${TABLE_NAME} where ${RID}='${rid}';"

        val db = writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count != 0
        if (flag) {
            db.execSQL("delete from ${TABLE_NAME} where ${RID} = '$rid';")
        }
        cursor.close()
        db.close()
        return flag
    }

    //selectAll처럼 스케줄데이터 리스트 가져오는 건데, sql을 직접 입력해서 검색 결과에 맞는 리스트만 줌
    fun selectAll(sql: String):ArrayList<scheduleData> {
        val db = readableDatabase
        var scheduleArray = ArrayList<scheduleData>()
        var cursorDriver=db.rawQuery(sql, null)
        if(cursorDriver.moveToFirst()) {
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
        } else {

        }

        cursorDriver.close()
        db.close()
        return scheduleArray
    }

    fun selectAll(sql: String, value : Int):ArrayList<scheduleData> {
        val db = readableDatabase
        var scheduleArray = ArrayList<scheduleData>()
        var cursorDriver=db.rawQuery(sql, null)
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

    fun selectAll():ArrayList<scheduleData>{
        //routine데이터 전부 다 가져올거임 Array로 리턴
        val strsql="select * from ${sDBHelper.TABLE_NAME};"
        val db=readableDatabase
        var scheduleArray = ArrayList<scheduleData>()//전체 데이터
        var cursorDriver=db.rawQuery(strsql,null)

        cursorDriver.moveToFirst()
        val attrcount=cursorDriver.columnCount

        /*
        //맨 위에 그 타이틀 읽어와서 붙여준 거임
        var routineid=cursorDriver.getString(0)
        var scheduleid=cursorDriver.getString(1)
        var scheduleName=cursorDriver.getString(2)
        var startTime=cursorDriver.getString(3)
        var endTime=cursorDriver.getString(4)
        scheduleArray.add(scheduleData(routineid.toInt(),scheduleid.toInt(),scheduleName,startTime.toInt(),endTime.toInt()))
        //데이터를 읽어와서 넣어줄 거임*/

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

    fun selectAllSbyR(rid: Int):ArrayList<scheduleData>{
        //routine데이터 전부 다 가져올거임 Array로 리턴
        val strsql="select * from ${sDBHelper.TABLE_NAME} where $RID = $rid;"
        val db=readableDatabase
        var scheduleArray = ArrayList<scheduleData>()//전체 데이터
        var cursorDriver=db.rawQuery(strsql,null)

        cursorDriver.moveToFirst()

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

    fun scheduleCheck(newSchedule: scheduleData):Boolean{
        var flag = false
        val oldSchedules = selectAllSbyR(newSchedule.routineID)

        if (newSchedule.startTime >= newSchedule.endTime){//시작 시간이 종료시간 보다 앞서야함
            return true
        }
        //기존 스케줄과 겹치는지
        for(i in 0..(oldSchedules.size-1)){
            var row = oldSchedules[i]
            flag = row.startTime < newSchedule.endTime && newSchedule.startTime < row.endTime
            if (flag == true)
                return flag
        }

        //여기서부터 24시간 넘기는 지
        var totalTime = 0
        val newScheduleTime = newSchedule.endTime - newSchedule.startTime
        for(i in 0..(oldSchedules.size-1)){
            totalTime = totalTime + (oldSchedules[i].endTime - oldSchedules[i].startTime)
        }

        //기존 시간 + 추가하려는 시간
        flag = (totalTime + newScheduleTime > 1440)
        //flag == true 가 스케줄 추가하면 안 되는 거임

        //24시간 넘는지 계산
        return flag

    }


    fun selectAll_ByRid(routineId: Int):ArrayList<scheduleData> {
        //routine데이터 전부 다 가져올거임 Array로 리턴
        val strsql="select * from ${TABLE_NAME} where $RID=${routineId};"
        val db=readableDatabase
        var scheduleArray = ArrayList<scheduleData>()//전체 데이터
        var cursorDriver=db.rawQuery(strsql,null)

        cursorDriver.moveToFirst()
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


    fun updateSchedule(data:scheduleData):Boolean{
        val sid=data.scheduleID
        val strsql="select * from $TABLE_NAME where $SID=$sid;"
        val db=writableDatabase
        val cursor=db.rawQuery(strsql,null)
        val flag=cursor.count!=0

        if(flag){
            cursor.moveToFirst()
            val values= ContentValues()
            values.put(SNAME,data.scheduleName)
            values.put(SSTART,data.startTime)
            values.put(SEND,data.endTime)
            db.update(TABLE_NAME,values,SID+"="+sid,null)
        }
        cursor.close()
        db.close()
        return flag
    }

}