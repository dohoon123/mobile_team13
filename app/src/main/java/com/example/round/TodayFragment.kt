package com.example.round


import android.R
import android.annotation.SuppressLint
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
import android.os.Message
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class TodayFragment : Fragment() {
    var binding: FragmentTodayBinding?=null
    lateinit var MemoDBHelper: MemoDBHelper
    lateinit var routineDataArray:ArrayList<routineData>
    lateinit var scheduleArray:ArrayList<scheduleData>
    lateinit var rDBHelper: rDBHelper
    lateinit var sDBHelper: sDBHelper
//    val clock = binding!!.clockView //시계로 쓸 텍스트뷰
//    var mHandler: Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initClock()
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
//    //시계
//    private fun initClock() {
//        @SuppressLint("HandlerLeak")
//        mHandler = object : Handler() {
//            override fun handleMessage(msg: Message) {
//                val cal = Calendar.getInstance()
//
//                val sdf = SimpleDateFormat("HH:mm:ss")
//                val strTime = sdf.format(cal.time)
//
//                clock?.setText(strTime)
//            }
//        }
//        thread(start = true) {
//            while (true) {
//                Thread.sleep(1000)
//                mHandler?.sendEmptyMessage(0)
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initSpinner() {     //DB의 루틴의 이름들을 모두 가져와서 스피너의 각 항목에 넣어줌
        //spinner 초기화
        var rNameList = ArrayList<String>() //스피너의 각 아이템이 될 루틴의 이름들

        var context= this.context
        this.rDBHelper= rDBHelper(requireContext())
        this.routineDataArray=rDBHelper.selectAll()     //DB로 부터 존재하는 루틴 다 가져오기
        if(this.routineDataArray.isNullOrEmpty()) {
            //데베에 루틴 없는 경우
        } else {
            for (routine in routineDataArray) {     //루틴 아이템마다 이름 가져와서 이름 리스트에 추가
                rNameList.add(routine.routineName)
            }
        }

        //layout에 어레이의 데이터를 매핑 시켜서 보여주는 어댑터
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_dropdown_item, rNameList)
/*
        adapter.insert("선택 안함", 0)    //디폴트 (선택 안됨을 의미)
        for(rName in rNameList) {
            adapter.add(rName)
        }
*/
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
        }//아마 아래 add부분에 value의 총합을 기준으로 비율을 정해주고 있을 거에요.. 고생 많으셨습니다..

        val handler = Handler()
        val entries = ArrayList<PieEntry>()
        //연석씨 제 주석을 받아주세요!
        //저는 fragment_today 레이아웃에서 파이 차트를 2개 만들어서 클릭 할 때마다 visibility를 gone <->
        //한 번에 하나씩만 보이게 했습니다. 동적으로 부드럽게 zoom in/out 하는 방법은 아무리 뒤져도 안 보이더군여..
        entries.add(PieEntry(500f, "술래잡기"))//엔트리에 파이엔트리를 추가하는 부분
        entries.add(PieEntry(500f, "고무줄놀이"))//MPAndroidChart라이브러리는 파이 말고도 다른 형태가 있다
        entries.add(PieEntry(500f, "말뚝박기"))//pieentry의 value값을 통해 시간표의 크기를 지정한다.
        entries.add(PieEntry(500f, "망까기"))//이전에 입력시간 endtime과 새로 입력한 starttime의 값이 일치하지 않는 경우
        entries.add(PieEntry(500f, "말타기"))//사이의 빈 시간에 쓰레기값을 add해주는 부분이 필요할 것 같다.
        entries.add(PieEntry(500f, "잠자기"))
        entries.add(PieEntry(500f, "먹기"))

        val colorsItems = ArrayList<Int>()//색깔 정하는 부분 사실 이 부분은 저도 따라 친거라 잘 모르겠네요;;
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)
        colorsItems.add(ColorTemplate.getHoloBlue())


        val pieDataSet = PieDataSet(entries,"")//위에선 만든 엔트리와 색을 파이데이터셋에 연결하는 곳
        pieDataSet.apply {
            colors = colorsItems//여기 문단은 사실 안 건드려도 될 듯 합니다.
            valueTextColor = Color.BLACK
            valueTextSize = 20f
        }

        val pieData = PieData(pieDataSet)
        //확대를 안 할 때 보여지는 기본 차트입니다.
        binding!!.chart.apply {
            data = pieData
            isHighlightPerTapEnabled = false//클릭된 일정이 튀어나오지 않게
            pieDataSet.setDrawValues(false)
            description.isEnabled = false
            isRotationEnabled = false//손가락으로 돌리면 시간표가 돌아가는 것을 방지
            centerText = "둥글지"
            legend.isEnabled = false//레전드도 지웠습니다.
            setUsePercentValues(false)//여기가 루틴 내 스케줄들의 비율을 나타내는 부분입니다. 시간표를 깔끔하게 하려고 false
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }

        //확대한 clicked_chart용입니다.
        binding!!.clickedChart.apply {
            data = pieData
            pieDataSet.setDrawValues(false)
            isHighlightPerTapEnabled = false
            description.isEnabled = false
            isRotationEnabled = true//확대했을 땐 시간표를 손으로 돌려가며 짧아서 잘 안보이는 일정도 볼 수 있게
            legend.isEnabled = false
            setUsePercentValues(false)
            setEntryLabelColor(Color.BLACK)
            rotationAngle = 0f
            extraBottomOffset = -420f//사실 시간표가 절반만 구현되는 기능이 있는게 아니라 오프셋을 주어 윗부분만 보이게 한 겁니다.
        }

        val gesture = object : OnChartGestureListener{//제스쳐 리스너입니다. 클릭, 롱클릭, 더블클릭, 스와이프 등.. 저는 클릭을 통해 chart, clicked_chart의 visibility를 바꾸는 것만 구현했습니다.
        override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {//안 쓰는 메소드도 오버라이드해야해서 좀 지저분하져..
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

        binding!!.chart.onChartGestureListener = gesture//리스너를 연결해 주었습니다.
        binding!!.clickedChart.onChartGestureListener = gesture

        val handlerTask = object : Runnable {//주기적으로 시간표를 돌리는 핸들러
            override fun run() {
                val currentTime = System.currentTimeMillis()//임시로 설정한 현재 시간입니다.
                val elapsedTime = (startTime - currentTime) * (-1)//총 지난 시간, starttime 은 위에서 임시로 프로젝트 생성 시간을 기준으로 잡았습니다.
            // 생성 시간이 아닌 루틴 시작시간을 고려해서 수정해야할 부분입니다.

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