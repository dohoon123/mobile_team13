package com.example.round

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.round.databinding.ActivityFixScheduleBinding

class fix_schedule : AppCompatActivity() {
    lateinit var binding: ActivityFixScheduleBinding
    lateinit var sDBHelper: sDBHelper
    lateinit var rDBHelper: rDBHelper

    lateinit var scheduleArray:ArrayList<scheduleData>
    lateinit var routineArray:ArrayList<routineData>
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: TimetableRVA

    var routineID=0

    var timetableDataList=ArrayList<timetableData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFixScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initHelper_recyclerView()
        init()
    }




    fun initHelper_recyclerView(){
        //여기서 데이터 읽어올 거임
        routineID = intent.getStringExtra("RID").toString().toInt()

        this.sDBHelper=sDBHelper(this)
        this.scheduleArray=sDBHelper.selectAll_ByRid(routineID) //전체 스케줄을 리턴해줌

        var tempString=""

        timetableDataList.clear()

        for( i in 0 until scheduleArray.size){
            //스케줄 사이즈만큼..
            var id=scheduleArray[i].scheduleID
            var name=scheduleArray[i].scheduleName.toString()
            var start=scheduleArray[i].startTime.toString()
            var end=scheduleArray[i].endTime.toString()
            tempString+="schedlueName:"+name+"\n"+start+"~"+end+"\n"

            this.timetableDataList.add(timetableData(id, tempString))
            tempString=""
        }


        recyclerView=binding!!.scheduleRecyclerView
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = TimetableRVA(timetableDataList)    //파라미터로 시간표 리스트의 배열 전달
        adapter.itemClickListener=object:TimetableRVA.OnItemClickListener{
            override fun OnItemClick(
                holder: TimetableRVA.ViewHolder,
                view: View,
                data: timetableData,
                position: Int
            ) {
                //클릭되면..수정할 수 있도록 이동시켜줄겅임
                var intent= Intent(this@fix_schedule,update_schedule::class.java)
                intent.putExtra("fix_RID", routineID.toString())
                intent.putExtra("fix_SID",data.routineID.toString() )  //routineID라고 되어있는데 그냥 데이터 구조때문이고 SID넣어뒀음
                startActivity(intent)
            }

        }


        recyclerView.layoutManager = layoutManager
        recyclerView.adapter=adapter

    }


    private fun init(){

        binding!!.apply {
            refreshLayout.setOnRefreshListener {
                initHelper_recyclerView()
                adapter.notifyDataSetChanged()
                recyclerView.adapter=adapter
                refreshLayout.isRefreshing = false
                Toast.makeText(this@fix_schedule, "목록을 새로고침 했습니다.", Toast.LENGTH_SHORT).show()
                //리사이클러 어댑터 뷰의 데이터 구성이 변해서 바로 체크할 수 있도록
            }
        }


    }



}