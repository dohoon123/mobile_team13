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
        var isDisposable:String = intent.getStringExtra("disposable").toString()  //1회용 시간표인지의 여부
        binding.test.setOnClickListener {
            Toast.makeText(this@Routine,isDisposable,Toast.LENGTH_LONG).show()
        }

        binding.apply {
            routineInsertBtn.setOnClickListener {
                val routineName = routineNameEdit.text.toString()
                val routineTerm = termEdit.text.toString().toInt()
                var routine:routineData

                if(isDisposable=="true") {routine = routineData(0, routineName, routineTerm, 0,1)   } //일회용인경우
                else{  routine = routineData(0, routineName, routineTerm, 0,0) }      //다회용인 경우




                val result = DBHelper.insertRoutine_disposable(routine)
                if (result) {
                    //Toast.makeText(this@routineActivity, "Data INSERT SUCCESS", Toast.LENGTH_SHORT).show()
                    //새로운 루틴 추가 -> 스케줄 추가 화면.
                    val routineID = DBHelper.getLatestrID()//방금 추가한 rID
                    val nextIntent = Intent(this@Routine, MainActivity::class.java)
                    startActivity(nextIntent)
                }else{
                    Toast.makeText(this@Routine, "Data INSERT FAILED", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}