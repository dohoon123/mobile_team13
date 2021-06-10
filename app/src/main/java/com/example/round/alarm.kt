package com.example.round

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.round.databinding.ActivityAlarmBinding

class alarm : AppCompatActivity() {
    lateinit var binding: ActivityAlarmBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MyAdapter       //단순한 단어 넣어주기


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        //recyclerView달아주기

        binding.makeAlarm.setOnClickListener {
            var intent= Intent(this, makeAlarm::class.java)
            //나중에 데이터 실어보내도록 수정하기 지금은 그냥 화면 전환만
            startActivity(intent)
        }

        binding.home.setOnClickListener {
            var intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }



}