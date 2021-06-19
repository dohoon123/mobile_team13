package com.example.round

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.round.databinding.FragmentMyPageBinding
import java.text.SimpleDateFormat
import java.time.LocalDate

class myPageFragment : Fragment(){
    lateinit var binding: FragmentMyPageBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: scoreAdapter
    lateinit var scoreDBHelper: scoreDBHelper

    lateinit var scoreList: ArrayList<scoreData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMyPageBinding.inflate(layoutInflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scoreDBHelper = scoreDBHelper(requireContext())
        var data = scoreData(0, 1, 0f, 0)

        var flag = scoreDBHelper.insertScore(data)
        if (flag){
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
        }


        binding.apply {
            val calendar = Calendar.getInstance()
            val simpleDateFormat = SimpleDateFormat("YYYYMMDD")
            var Date = simpleDateFormat.format(calendarView.dateTextAppearance)
            scoreList = scoreDBHelper?.selectScByDate(calendarView.date)//캘린더에서 날짜 가져옴.
            recyclerView=binding!!.scoreRecyclerView
            recyclerView.layoutManager= LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL,false)

            adapter= scoreAdapter(scoreList)
            recyclerView.adapter=adapter
            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                var str: String = "$year"
                if (month<10)
                    str = str + "0"
                //month는 1을 더해줘야 정상적인 값이 나옴..ㅠ
                str += (month +1)
                if(dayOfMonth<10)
                    str = str + "0"
                str = str+dayOfMonth

                var date = str.toLong()
                scoreList = scoreDBHelper?.selectScByDate(date)//캘린더에서 날짜 가져옴.
//                if (scoreList != null){
//                    Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show()
//                }
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

    }}