package com.example.round

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.round.databinding.ActivityUpdateScheduleBinding

class update_schedule : AppCompatActivity() {
    lateinit var binding: ActivityUpdateScheduleBinding
    lateinit var DBHelper: sDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        DBHelper = sDBHelper(this)
        val fix_routineID: Int = intent.getStringExtra("fix_RID").toString().toInt()
        val fix_scheduleID: Int = intent.getStringExtra("fix_SID").toString().toInt()

        binding.apply {

            insertBtn.setOnClickListener {
                val name = sNameEdit.text.toString()
                val start = startTime.hour * 60 + startTime.minute
                val end = endTime.hour * 60 + endTime.minute
                val schedule = scheduleData(fix_routineID, fix_scheduleID, name, start, end)



                val result = DBHelper.updateSchedule(schedule)
                if (result){

                    Toast.makeText(this@update_schedule, "Data INSERT SUCCESS", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@update_schedule, "Failed", Toast.LENGTH_SHORT).show()
                }


            }
        }
    }
}