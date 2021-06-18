package com.example.round

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.round.databinding.ActivityRoutineBinding


class Routine : AppCompatActivity() {
    lateinit var binding: ActivityRoutineBinding
    lateinit var DBHelper: rDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init(){
        DBHelper = rDBHelper(this)
        binding.apply {
            routineInsertBtn.setOnClickListener {
                val routineName = routineNameEdit.text.toString()
                val routineTerm = termEdit.text.toString().toInt()
                val routine = routineData(0, routineName, routineTerm, 0)//기간 24시간으로 들어감.
                val result = DBHelper.insertRoutine(routine)
                if (result) {
                    //Toast.makeText(this@routineActivity, "Data INSERT SUCCESS", Toast.LENGTH_SHORT).show()
                    //새로운 루틴 추가 -> 스케줄 추가 화면.
                    val routineID = DBHelper.getLatestrID()//방금 추가한 rID
                    val nextIntent = Intent(this@Routine, Schedule::class.java)
                    nextIntent.putExtra("RID", routineID.toString())
                    startActivity(nextIntent)
                }else{
                    Toast.makeText(this@Routine, "Data INSERT FAILED", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}