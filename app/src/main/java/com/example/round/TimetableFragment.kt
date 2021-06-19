package com.example.round

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.round.databinding.FragmentTimetableBinding


class TimetableFragment : Fragment() {
    var binding: FragmentTimetableBinding?=null
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: TimetableRVA
    lateinit var routineArray:ArrayList<routineData>
    lateinit var scheduleArray:ArrayList<scheduleData>
    lateinit var recyclerView: RecyclerView

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
            init()
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
        this.rDBHelper= rDBHelper(context!!)
        rDBHelper.onCreate(rDBHelper.writableDatabase)

        this.sDBHelper=sDBHelper(context!!)
        //데이터 넣어주고 여기서 있는거 넣어줄거임...
        this.routineArray=rDBHelper.selectAll()
        this.scheduleArray=sDBHelper.selectAll()

        var tempString=""
        timetableDataList.clear()
        Toast.makeText(context,timetableDataList.size.toString(),Toast.LENGTH_SHORT).show()
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
            this.timetableDataList.add(timetableData(routineArray[i].routineID, tempString))
            tempString=""
        }

        //걍 가져온 결과를 String으로 넣어주는 거임.. 근데 이걸 좀 바꿔야한다고 생각

    }





    fun initRecyclerView() {
        recyclerView=binding!!.recyclerView
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = TimetableRVA(timetableDataList)    //파라미터로 시간표 리스트의 배열 전달


        //여기서 스와이프 되며삭제되도록 해줘야함
        val simpleCallback=object:ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val builder= AlertDialog.Builder(context)
                builder.setTitle("일정 삭제")
                builder.setMessage("일정을 삭제하시겠습니까?")

                builder.setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                    var removeItem=adapter.giveItem(viewHolder.adapterPosition) //지워야할 아이템 객체 리턴받음
                    var flag=rDBHelper.deleteRoutine(removeItem)
                    if (flag == false) {
                        Toast.makeText(activity, "삭제 실패", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "일정이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        adapter.removeItem(viewHolder.adapterPosition)      //리스트에서 없애주기
                    }
                })
                builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                    recyclerView.adapter=adapter
                })
                builder.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)





        binding!!.apply {
            recyclerView.layoutManager = layoutManager
            adapter.itemClickListener=object:TimetableRVA.OnItemClickListener{
                override fun OnItemClick(holder: TimetableRVA.ViewHolder, view: View, data: timetableData, position: Int) {
                    //클릭됐을때 생성 및 수정이 가능하도록 이동하기. 이미 존재하는 페이지로 이동하는 이유는 같은routineIdr를 입력하면 수정되도록
                    var intent= Intent(context, Schedule::class.java)
                    intent.putExtra("RID", data.routineID.toString())
                    startActivity(intent)


                    //Toast.makeText(context, "routineID:"+data.routineID.toString()+" 스케줄 추가화면으로 이동함",Toast.LENGTH_SHORT).show()
                    if(rDBHelper.isDisposable(data.routineID.toString())=="1")
                        Toast.makeText(context,"일회용 시간표입니다.",Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context,"다회용 시간표입니다.",Toast.LENGTH_SHORT).show()
                }
            }



            recyclerView.adapter = adapter

            add.setOnClickListener {
                var intent= Intent(context, Routine::class.java)
                intent.putExtra("disposable","false")
                startActivity(intent)
            }
            tempAdd.setOnClickListener {
                Toast.makeText(context,"일회성 시간표 만들기 파트",Toast.LENGTH_SHORT).show()
                //루틴으로 intent실어서 보내고, 만들고 계속 수정가능할 것인지? 얘기해봐야할듯

                var intent= Intent(context, Routine::class.java)
                intent.putExtra("disposable","true")
                startActivity(intent)
            }



            reset.setOnClickListener {
                rDBHelper.reset()
                rDBHelper.onCreate(rDBHelper.writableDatabase)
                rDBHelper=rDBHelper(requireContext())
                initHelper()
            }

        }



    }

    fun init(){
        binding!!.apply {
            refreshLayout.setOnRefreshListener {
                initHelper()
                initRecyclerView()
                adapter.notifyDataSetChanged()
                recyclerView.adapter=adapter
                refreshLayout.isRefreshing = false
                Toast.makeText(context, "목록을 새로고침 했습니다.", Toast.LENGTH_SHORT).show()
                //리사이클러 어댑터 뷰의 데이터 구성이 변해서 바로 체크할 수 있도록
            }
        }



    }



}