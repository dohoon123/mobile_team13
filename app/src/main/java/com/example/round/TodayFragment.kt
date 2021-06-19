package com.example.round


import android.R
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.round.databinding.FragmentTodayBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.Utils.init

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

        init()
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

    override fun onResume() {
        super.onResume()
        initSpinner()   //프레그먼트 다시 시작 됐을 때 이닛스피너 갱신
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    private fun init() {

    }

    private fun initSpinner() {     //DB의 루틴의 이름들을 모두 가져와서 스피너의 각 항목에 넣어줌
        var rNameList = ArrayList<String>() //데베에 존재하는 모든 루틴의 이름들

        this.rDBHelper= rDBHelper(requireContext())
        this.routineDataArray=rDBHelper.selectAll()     //DB로 부터 존재하는 루틴 다 가져오기

        if(this.routineDataArray.isNullOrEmpty()) {     //데베에 루틴 없는 경우
            binding!!.apply {
                digitalClock.visibility = View.GONE
                chart.visibility = View.GONE
                clickedChart.visibility = View.GONE
                spinner.visibility = View.GONE
                noRoutine.visibility = View.VISIBLE
            }
//            binding!!.noRoutine.setOnClickListener {
//                //클릭하면 루틴 추가 페이지로 이동하고 시픈데,,
//            }
        } else {
            binding!!.apply {
                digitalClock.visibility = View.VISIBLE
                chart.visibility = View.VISIBLE
                clickedChart.visibility = View.GONE
                spinner.visibility = View.VISIBLE
                noRoutine.visibility = View.GONE
            }
            for (routine in routineDataArray) {     //루틴 아이템마다 이름 가져와서 이름 리스트에 추가
                rNameList.add(routine.routineName)
            }
        }

        //layout에 어레이의 데이터를 매핑 시켜서 보여주는 어댑터
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            rNameList
        )

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
                                //원형 시간표를 생성하고자 하는 루틴의 ID를 인자로 넣어서 원형 차트 함수 시작
                                initCircular(rid)
                            }
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun defaultEntries() : ArrayList<PieEntry> {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(500f, ""))
//        entries.add(PieEntry(500f, "술래잡기"))//엔트리에 파이엔트리를 추가하는 부분
//        entries.add(PieEntry(500f, "고무줄놀이"))//MPAndroidChart라이브러리는 파이 말고도 다른 형태가 있다
//        entries.add(PieEntry(500f, "말뚝박기"))//pieentry의 value값을 통해 시간표의 크기를 지정한다.
//        entries.add(PieEntry(500f, "망까기"))//이전에 입력시간 endtime과 새로 입력한 starttime의 값이 일치하지 않는 경우
//        entries.add(PieEntry(500f, "말타기"))//사이의 빈 시간에 쓰레기값을 add해주는 부분이 필요할 것 같다.
//        entries.add(PieEntry(500f, "놀기"))
//        entries.add(PieEntry(500f, "먹기"))
        return entries
    }
    private fun defaultColors() : ArrayList<Int> {
        val colors = ArrayList<Int>()
        colors.add(rgb("#cfd8dc"))
//        colors.add(rgb("#455a64"))
//        colors.add(rgb("#bf360c"))
//        colors.add(rgb("#004d40"))
//        colors.add(rgb("#000063"))
//        colors.add(rgb("#560027"))
//        colors.add(rgb("#7f0000"))
//        colors.add(rgb("#424242"))
//        colors.add(rgb("#cfd8dc"))

        return colors
    }

    fun rgb(hex: String): Int {
        val color = hex.replace("#", "").toLong(16).toInt()
        val r = color shr 16 and 0xFF
        val g = color shr 8 and 0xFF
        val b = color shr 0 and 0xFF
        return Color.rgb(r, g, b)
    }



    fun initCircular(rid: Int?){     //루틴 ID를 인자로 받아서 그 루틴의 원형 시간표 생성
        var entries = ArrayList<PieEntry>()
        var colorsItems = ArrayList<Int>()//색깔 정하는 부분 사실 이 부분은 저도 따라 친거라 잘 모르겠네요;;

        if (rid == null) {  //따로 받아온 루틴 없으면
            entries = defaultEntries()
            colorsItems = defaultColors()
            binding!!.chart.centerText = """원형 시간표로 보고싶은
                | 루틴을 선택하세요!""".trimMargin()
        } else {    //루틴 아이디 존재할 경우
            val sql = "select * from scheduleData where rountineID = ${rid} order by startTime" //꼭 스케줄 리스트 starttime 순으로 정렬 해서 받아오기
            val scheduleDataArray = sDBHelper.selectAll(sql)    //루틴 ID가 가진 스케줄 데이터 다 가져오기

            //스피너서 선택한 루틴 이름으로부터 루틴 ID를 얻어와 그것과 일치하는 스케줄 데이터 어레이 가져옴
            //스피너에서 선택 안했거나 시작할 땐 가져온 어레이리스트 없으므로 null일 것임
            if(scheduleDataArray.isNullOrEmpty()) {    //가져온 스케줄 없을 경우
                entries = defaultEntries()
                colorsItems = defaultColors()
                binding!!.chart.centerText = "루틴에 저장된 스케줄 정보가 없습니다."
            }else {             //있을 경우
                val rname = rDBHelper.getrName(rid)
                binding!!.chart.centerText = rname
                val scheduleNum = scheduleDataArray.size    //스케줄 개수

                //첫 스케줄이 0시00분에 바로 시작하는 게 아니라면 맨 앞에 빈칸 넣어주기
                if (scheduleDataArray[0].startTime != 0) {
                    entries.add(PieEntry(scheduleDataArray[0].startTime.toFloat(), "")) //빈 공간 넣어줌!
                }

                for (i in 0 until scheduleNum) {
                    val s = scheduleDataArray[i]
                    entries.add(PieEntry((s.endTime - s.startTime).toFloat(), s.scheduleName))  //현재 스케줄 차트에 넣기

                    //작업중인 스케줄이 마지막 스케줄이면 endTime이 23시59분 아니면 끝에 빈공간 채우고
                    //마지막 아니면 다음 스케줄이랑 현재 스케줄 사이의 빈공간 채워주기
                    if (i == scheduleNum-1) {   //만약 마지막 스케줄일 경우
                        if(s.endTime != 1439) { //마지막 스케줄이 끝나는 시간이 시계 종료 시간 아니면 빈공간
                            entries.add(PieEntry((1439 - s.endTime).toFloat(), ""))
                        }
                    } else {    //현재 작업 중인 스케줄 항목이 마지막꺼 아니면, 다음 스케줄과 비교해서 빈공간 넣기
                        val nextS = scheduleDataArray[i + 1]
                        if (nextS.startTime != s.endTime) {
                            entries.add(
                                PieEntry(
                                    (s.endTime - s.startTime).toFloat(),
                                    s.scheduleName
                                )
                            )
                        }
                    }
                }
            }//아마 아래 add부분에 value의 총합을 기준으로 비율을 정해주고 있을 거에요.. 고생 많으셨습니다..
        }
        //원형 차트에 엔트리 추가 끝

        val startTime = System.currentTimeMillis()
        var fromAngle = 0f
        val standardTime = 60000F//임시로 1분! 1분에 한바퀴
        val handler = Handler()

        //연석씨 제 주석을 받아주세요!
        //저는 fragment_today 레이아웃에서 파이 차트를 2개 만들어서 클릭 할 때마다 visibility를 gone <->
        //한 번에 하나씩만 보이게 했습니다. 동적으로 부드럽게 zoom in/out 하는 방법은 아무리 뒤져도 안 보이더군여..
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)
        colorsItems.add(ColorTemplate.getHoloBlue())

        //엔트리 중 라벨이 "" 이면 빈공간, 회색으로
        for(i in 0 until entries.size) {
            if (entries[i].label == "") colorsItems.add(i, rgb("#cfd8dc"))
        }

        val pieDataSet = PieDataSet(entries, "")//위에선 만든 엔트리와 색을 파이데이터셋에 연결하는 곳
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
        override fun onChartGestureEnd(
            me: MotionEvent?,
            lastPerformedGesture: ChartTouchListener.ChartGesture?
        ) {//안 쓰는 메소드도 오버라이드해야해서 좀 지저분하져..
        }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
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

            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
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