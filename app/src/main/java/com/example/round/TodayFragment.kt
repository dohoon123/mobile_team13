package com.example.round


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.round.databinding.FragmentTodayBinding

class TodayFragment : Fragment() {
    var binding: FragmentTodayBinding?=null
    lateinit var MemoDBHelper: MemoDBHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //메모 추가
        MemoDBHelper = MemoDBHelper(getActivity())
        memoInsert()
        binding!!.apply {

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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