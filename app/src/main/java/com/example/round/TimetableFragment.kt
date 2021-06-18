package com.example.round

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.round.databinding.FragmentTimetableBinding


class TimetableFragment : Fragment() {
    var binding: FragmentTimetableBinding?=null
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: TimetableRVA
    lateinit var routineArray:ArrayList<routineData>
    lateinit var scheduleArray:ArrayList<scheduleData>

    lateinit var rDBHelper: rDBHelper
    lateinit var sDBHelper: sDBHelper

    var timetableDataList=ArrayList<timetableData>()
    //되는지 안되는지 보려고 일단은 임시로 스트링이랑 마이어댑터로 구현..나중에 time이랑 timeData형으로 수정하기

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimetableBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {
            initHelper()
            initRecyclerView()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun initHelper(){
        //여기서 데이터 읽어올 거임
        var context=this.context
        this.rDBHelper= rDBHelper(requireContext())
        this.sDBHelper=sDBHelper(requireContext())
        //데이터 넣어주고 여기서 있는거 넣어줄거임...
        this.routineArray=rDBHelper.selectAll()
        this.scheduleArray=sDBHelper.selectAll()

        var tempString=""
        for (i in 0 until routineArray.size-1){
            for (j in 0 until scheduleArray.size-1){
                //같은 routine에 속하는 schedule을 넣어줘서 string으로 보여줄거임..
                if(scheduleArray[j].routineID==routineArray[i].routineID){
                    var name=scheduleArray[j].scheduleName.toString()
                    var start=scheduleArray[j].startTime.toString()
                    var end=scheduleArray[j].endTime.toString()
                    tempString+=name+"\n"+start+"~"+end+"\n"
                }
            }
            this.timetableDataList.add(timetableData(routineArray[i].routineID,tempString))
            tempString=""
        }

        //걍 가져온 결과를 String으로 넣어주는 거임.. 근데 이걸 좀 바꿔야한다고 생각

    }


    fun initRecyclerView() {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = TimetableRVA(timetableDataList)    //파라미터로 시간표 리스트의 배열 전달

        binding!!.apply {
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter

            add.setOnClickListener {
                var intent= Intent( context, Routine::class.java)
                startActivity(intent)
            }
        }


    }

}