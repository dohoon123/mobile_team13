package com.example.round

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.round.databinding.ActivityMakeAlarmBinding

class makeAlarm : AppCompatActivity() {
    lateinit var binding: ActivityMakeAlarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMakeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init(){
        binding.submit.setOnClickListener {
            var intent= Intent(this, alarm::class.java)
            //나중에 데이터 실어보내도록 수정하기 지금은 그냥 화면 전환만
            startActivity(intent)
        }
    }

}