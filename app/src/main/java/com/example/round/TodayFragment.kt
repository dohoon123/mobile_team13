package com.example.round


import android.R
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.round.databinding.FragmentTodayBinding
import android.graphics.Color
import android.os.Handler
import android.view.MotionEvent
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.ColorTemplate

class TodayFragment : Fragment() {
    var binding: FragmentTodayBinding?=null
    lateinit var MemoDBHelper: MemoDBHelper
    lateinit var routineDataArray:ArrayList<routineData>
    lateinit var scheduleArray:ArrayList<scheduleData>
    lateinit var rDBHelper: rDBHelper
    lateinit var sDBHelper: sDBHelper



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSpinner()   //스피너 만들기
        //원형 스케줄러
        initCircular(null)


        //메모 추가
        MemoDBHelper = MemoDBHelper(getActivity())
        memoInsert()
        binding!!.apply {
            addBtn.setOnClickListener {
                //데이터 베이스에서 루틴 가져와서 ID 받아서 스케줄 오픈, 차트에 넣기
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initSpinner() {     //DB의 루틴의 이름들을 모두 가져와서 스피너의 각 항목에 넣어줌
        //spinner 초기화
        lateinit var rNameList : ArrayList<String> //스피너의 각 아이템이 될 루틴의 이름들

        var context=this.context
        this.rDBHelper= rDBHelper(context!!)
        this.routineDataArray=rDBHelper.selectAll()     //DB로 부터 존재하는 루틴 다 가져오기
        for (routine in routineDataArray) {     //루틴 아이템마다 이름 가져와서 이름 리스트에 추가
            rNameList.add(routine.routineName)
        }
        //layout에 어레이의 데이터를 매핑 시켜서 보여주는 어댑터
        val adapter = ArrayAdapter<String>(context!!, R.layout.simple_spinner_dropdown_item, rNameList)
        adapter.insert("선택 안함", 0)    //디폴트 (선택 안됨을 의미)
        for(rName in rNameList) {
            adapter.add(rName)
        }
        binding!!.apply {
            spinner.adapter = adapter
            spinner.setSelection(0) //디폴트(아직 루틴 선택 안한 거)로 처음에 설정
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    //아이템 선택되면 선택된 루틴
                    when(position) {
                        0 -> {
                            //아직 루틴을 설정하지 않았으므로, 원형 시간표 보여줄게 없음. 루틴 이름을 선택하게 해야함
                            initCircular(null)
                        }
                        else -> {
                            val rname = view.toString()
                            val rid = rDBHelper.getrId(rname)
                            if (rid == -1) {
                                //루틴 이름과 일치하는 아이디가 없다 -> 오류
                            } else {
                                val sql = "select * from scheduleData where rountineID = ${rname} "
                                val scheduleDataArray = sDBHelper.selectAll(sql)
                                //루틴 이름과 일치하는 루틴 아이디를 가진 스케줄데이터 어레이를 가져옴
                                //이제 스케줄데이터 어레이리스트를 원형 스케줄에 전달해서 반영해야함.
                                initCircular(scheduleDataArray)
                            }
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //
                }

            }
        }
    }

    fun initCircular(sDataArrayList: ArrayList<scheduleData>?){     //스케줄 데이터 리스트를 인자로 받아서 원형 시간표 생성
        val startTime = System.currentTimeMillis()
        var fromAngle = 0f
        val standardTime = 60000F//임시로 1분!

        //스피너서 선택한 루틴 이름으로부터 루틴 ID를 얻어와 그것과 일치하는 스케줄 데이터 어레이 가져옴
        //스피너에서 선택 안했거나 시작할 땐 가져온 어레이리스트 없으므로 null일 것임
        if(sDataArrayList.isNullOrEmpty()) {    //가져온 스케줄 없을 경우

        }else {             //있을 경우
            lateinit var piePercent :ArrayList<Int>  //전체 원 중에 스케줄 하나가 차지할 파이의 비율
            val scheduleNum = sDataArrayList.size.toInt()
            var totalMinute:Int = 0 //스케줄의 총 시간을 다 계산해서 각 아이템의 비율 계산해야댐.
            //스케줄 아이템의 시작과 끝 시간을 계산, 총 24시간 중에 몇 퍼센트의 비율인지
            for(i in sDataArrayList) {  //모든 스케줄 각각의 소요 시간 합하기
                totalMinute += (i.endTime - i.startTime)
            }
            for(i in 0..scheduleNum) {
                //해당 스케줄이 전체 스케줄 중에 차지하는 비율로, 파이의 비율
                piePercent[i] = (sDataArrayList[i].endTime - sDataArrayList[i].startTime) / totalMinute
            }
            //원 전체 크기 중 각 아이템의 비율 만큼 원 차지하면 됩니당,,,
            //그리고 원 그리면 됩니당,,
        }

        val handler = Handler()
        val entries = ArrayList<PieEntry>()

        entries.add(PieEntry(500f, "술래잡기"))
        entries.add(PieEntry(500f, "고무줄놀이"))
        entries.add(PieEntry(500f, "말뚝박기"))
        entries.add(PieEntry(500f, "망까기"))
        entries.add(PieEntry(500f, "말타기"))
        entries.add(PieEntry(500f, "잠자기"))

        val colorsItems = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)
        colorsItems.add(ColorTemplate.getHoloBlue())

        val pieDataSet = PieDataSet(entries,"")
        pieDataSet.apply {
            colors = colorsItems
            valueTextColor = Color.BLACK
            valueTextSize = 20f
        }

        val pieData = PieData(pieDataSet)
        binding!!.chart.apply {
            data = pieData
            isHighlightPerTapEnabled = false
            pieDataSet.setDrawValues(false)
            description.isEnabled = false
            isRotationEnabled = false
            centerText = "둥글지"
            legend.isEnabled = false
            setUsePercentValues(false)
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }

        binding!!.clickedChart.apply {
            data = pieData
            pieDataSet.setDrawValues(false)
            isHighlightPerTapEnabled = false
            description.isEnabled = false
            isRotationEnabled = true
            legend.isEnabled = false
            setUsePercentValues(false)
            setEntryLabelColor(Color.BLACK)
            rotationAngle = 0f
            extraBottomOffset = -420f
        }

        val gesture = object : OnChartGestureListener{//클릭을 보자!!
        override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        }

            override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {
            }

            override fun onChartSingleTapped(me: MotionEvent?) {
                binding!!.apply {
                    if (chart.visibility == View.VISIBLE && clickedChart.visibility == View.GONE){
                        chart.visibility = View.GONE
                        clickedChart.visibility = View.VISIBLE
                    }
                    else{
                        chart.visibility = View.VISIBLE
                        clickedChart.visibility = View.GONE
                    }
                }
            }

            override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {

            }

            override fun onChartLongPressed(me: MotionEvent?) {
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
            }

            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
            }
        }

        binding!!.chart.onChartGestureListener = gesture
        binding!!.clickedChart.onChartGestureListener = gesture

        val handlerTask = object : Runnable {//주기적으로 시간표를 돌리는 핸들러
        override fun run() {
            val currentTime = System.currentTimeMillis()//현재 시간
            val elapsedTime = (startTime - currentTime) * (-1)//총 지난 시간
            var toAngle = 360F * (elapsedTime/standardTime)//움직일 각도

            binding!!.chart.apply {
                spin(1000, fromAngle, toAngle, Easing.Linear)
                fromAngle = toAngle
            }
            handler.postDelayed(this, 1000)//1초 = 1000
        }
        }
        handler.post(handlerTask) // 주기적으로 실행
    }

    //메모 추가 함수
    private fun memoInsert(){
        binding?.apply {
            var clickNum = 0
            lateinit var content: String
            lateinit var title: String
            insertBtn.setOnClickListener {

                clickNum++
                //클릭 2번이 메모 추가에서 한 세트.
                if (clickNum%2==1){//클릭 첫번째면 메모 내용 입력.
                    content = memoInsert.text.toString()
                    memoInsert.hint = "제목 입력"
                    memoInsert.text.clear()
                } else {//클릭 두번째는 메모 제목 입력.
                    title = memoInsert.text.toString()
                    var flag = MemoDBHelper.insertMemo(memoData(0, title, content))
                    if (flag) {
                        Toast.makeText(context, "메모 등록 성공", Toast.LENGTH_SHORT).show()
                        memoInsert.hint = "내용 입력"
                        memoInsert.text.clear()
                    } else {
                        Toast.makeText(context, "메모 등록 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}