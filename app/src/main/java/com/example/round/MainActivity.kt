package com.example.round

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.round.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.time.LocalDateTime


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var DBHelper:rDBHelper
    val textarr = arrayListOf<String>("일정관리", "원형시간표", "메모", "부가기능")
    val iconarr = arrayListOf<Int>(R.drawable.timetable, R.drawable.today, R.drawable.memo, R.drawable.etc)
    override fun onCreate(savedInstanceState: Bundle?) {
        DBHelper = rDBHelper(this)
        var scoreDBHelper = scoreDBHelper(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewPager()
        if(intent.hasExtra("time")){
            val currentMin = LocalDateTime.now().minute
            val currentHour = LocalDateTime.now().hour
            val currentTime = currentHour*60 + currentMin
            val time = intent.getIntExtra("time", 100)
            val rid = intent.getIntExtra("rid", 100)
            val isEnd = intent.getBooleanExtra("isEnd", false)
            if(currentTime - time < 10){//10분안에 누르면
                var flag = DBHelper.alarmCheckPlus(rid)
                //Toast.makeText(this, "알람!", Toast.LENGTH_SHORT).show()
                if (flag){
                    Toast.makeText(this, "점수가 상승했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            if(isEnd ==true){//마지막 스케줄인가
                //routine name 가져옴.
                var rDBHelper = rDBHelper(this)
                var routineName = rDBHelper.getrName(rid)
                //평가 등록
                scoreDBHelper.insertScore(rid, routineName)
            }

        }
    }

    private fun initViewPager() {
        binding.viewPager.adapter = MyFragStateAdapter(this)
        TabLayoutMediator(binding.tablayout, binding.viewPager) {
            tab, position ->
            tab.text = textarr[position]
            tab.setIcon(iconarr[position])
        }.attach()
        binding.viewPager.setCurrentItem(1) //오늘의 시간표 메인으로 설정
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent!=null){
            if(intent.hasExtra("time")){
                val currentMin = LocalDateTime.now().minute
                val currentHour = LocalDateTime.now().hour
                val currentTime = currentHour*60 + currentMin
                val time = intent.getIntExtra("time", 100)
                val rid = intent.getIntExtra("rid", 100)
                if(currentTime - time < 10){//10분안에 누르면
                    DBHelper.alarmCheckPlus(rid)
                    Toast.makeText(this, "알람!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}