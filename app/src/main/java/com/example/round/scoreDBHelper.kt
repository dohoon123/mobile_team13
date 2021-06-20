package com.example.round

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class scoreDBHelper(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_NAME = "myScoreDB.db"
        val DB_VERSION = 1
        val TABLE_NAME = "scoreData"
        val SCID = "scoreID"
        val RID = "routineID"
        val SCORE = "score"
        val EDATE = "endDate"
        val RNAME = "routineName"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists $TABLE_NAME ("+
                "$SCID integer primary key autoincrement," +
                "$RID integer," +
                "$SCORE float," +
                "$EDATE long," +
                "$RNAME string);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun insertScore(rid: Int, rName: String):Boolean{
        var rDBHelper = rDBHelper(context)
        val values = ContentValues()

        //스코어 계산
        var score =rDBHelper.calculateScore(rid)
        if (score == -1f)
            return false

        //시간 값 가져오기(insert 하는 시점의 시간이 들어감)
        var date:Long = LocalDate.now().toString().replace("-","").toLong()

        //SCID값은 자동 증가
        values.put(RID, rid)
        values.put(SCORE, score)
        values.put(EDATE, date)//바꿔야함
        values.put(RNAME, rName)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }


    fun selectScByDate(date: Long):ArrayList<scoreData>{
        //routine데이터 전부 다 가져올거임 Array로 리턴
        val strsql="select * from $TABLE_NAME where $EDATE = $date;"
        val db= readableDatabase
        var scoreArray = ArrayList<scoreData>()//속성
        var cursorDriver=db.rawQuery(strsql,null)

        cursorDriver.moveToFirst()

        do{
            if(cursorDriver.count==0){//Toast.makeText(context, "nothing", Toast.LENGTH_SHORT).show()
                return scoreArray}
            else{
                var scoreId = cursorDriver.getString(0)
                var routineid= cursorDriver.getString(1)
                var score= cursorDriver.getString(2)
                var date= cursorDriver.getString(3)
                var rName = cursorDriver.getString(4)
                scoreArray.add(scoreData(scoreId.toInt(), routineid.toInt(), score.toFloat(), date.toLong(), rName))
            }
        }while(cursorDriver.moveToNext())

        cursorDriver.close()
        db.close()
        return scoreArray
    }

}