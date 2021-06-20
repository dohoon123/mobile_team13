package com.example.round

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.round.databinding.ActivityMyPageBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime

class MyPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyPageBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: scoreAdapter
    lateinit var scoreDBHelper: scoreDBHelper

    lateinit var scoreList: ArrayList<scoreData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    fun init(){
        scoreDBHelper = scoreDBHelper(this)

//        var data = scoreData(0, 1, 0f, 0)
//
//        //score DB 임의로 넣어주는 부분. 나중에 삭제 필요.
//        var flag = scoreDBHelper.insertScore(data)
//        if (flag){
//            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
//        }else{
//            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
//        }


        binding.apply {
            scoreList = scoreDBHelper?.selectScByDate(calendarView.date)//캘린더에서 날짜 가져옴.
            recyclerView=binding!!.scoreRecyclerView
            recyclerView.layoutManager= LinearLayoutManager(this@MyPageActivity, LinearLayoutManager.VERTICAL,false)

            adapter= scoreAdapter(scoreList)
            recyclerView.adapter=adapter

            //인텐트 초기화
            var date = getNow()
            scoreList = scoreDBHelper?.selectScByDate(date)//캘린더에서 날짜 가져옴.

            recyclerView=binding!!.scoreRecyclerView
            recyclerView.layoutManager= LinearLayoutManager(this@MyPageActivity, LinearLayoutManager.VERTICAL,false)
            var dateStr = date.toString()
            var str:String = dateStr.substring(0,4) + "년 " + dateStr.substring(4,6)+"월 " + dateStr.substring(6,8) + "일"
            dateLabel.text = str

            adapter= scoreAdapter(scoreList)
            recyclerView.adapter=adapter

            //날짜 누르면 바뀜
            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                var str: String = "$year"
                if (month<10)
                    str = str + "0"
                //month는 1을 더해줘야 정상적인 값이 나옴.
                str += (month +1)
                if(dayOfMonth<10)
                    str = str + "0"
                str = str+dayOfMonth

                var date = str.toLong()
                scoreList = scoreDBHelper?.selectScByDate(date)//캘린더에서 날짜 가져옴.
                recyclerView=binding!!.scoreRecyclerView
                recyclerView.layoutManager= LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL,false)
                dateLabel.text = "${year}년  ${month+1}월 ${dayOfMonth}일"
                adapter= scoreAdapter(scoreList)
                recyclerView.adapter=adapter

            }

        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.apply {
            recyclerView = binding!!.scoreRecyclerView
            adapter = scoreAdapter(scoreList)
            recyclerView.adapter = adapter
        }

    }

    private fun getNow(): Long {
        val currentTime = LocalDate.now()
        val currentDay = currentTime.dayOfMonth
        val currentMonth = currentTime.monthValue
        val currentYear = currentTime.year

        var strDate: String = currentYear.toString()
        if(currentMonth <10){
            strDate += "0"
        }
        strDate +=currentMonth.toString()
        if(currentDay <10){
            strDate += "0"
        }
        strDate +=currentDay.toString()

        return strDate.toLong()
    }
}


