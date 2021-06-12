package com.example.round

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.round.databinding.FragmentMemoBinding

class MemoFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MyAdapter       //단순한 단어 넣어주기
    lateinit var MemoDBHelper: MemoDBHelper


    var binding: FragmentMemoBinding?=null
    var tempList=ArrayList<memoData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMemoBinding.inflate(layoutInflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MemoDBHelper = MemoDBHelper(getActivity())

        //메모 추가 영역. tempList.add를 insertMemo로 변경.
//        MemoDBHelper.insertMemo(memoData(1,"제목1","내용 메모 예시"))
//        MemoDBHelper.insertMemo(memoData(2,"제목2","내용 메모 예시"))
//        MemoDBHelper.insertMemo(memoData(3,"제목3","내용 메모 예시"))
//        MemoDBHelper.insertMemo(memoData(4,"제목4","내용 메모 예시"))


        //메모 추가 후 tempList 가져옴.
        tempList = MemoDBHelper?.getMemo()



        binding.apply {
            recyclerView=binding!!.memoRecyclerView
            recyclerView.layoutManager= LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL,false)

            adapter= MyAdapter(tempList)
            recyclerView.adapter=adapter }

        initRecyclerView()
    }

    private fun initRecyclerView(){
        binding.apply {
            recyclerView=binding!!.memoRecyclerView
            adapter= MyAdapter(tempList)
            recyclerView.adapter=adapter }

        //클릭 시 해당 메모 삭제.
        adapter.itemClickListener = object : MyAdapter.OnItemClickListener{
            override fun OnItemClick(holder: MyAdapter.ViewHolder, view: View, data: memoData, position: Int) {

                var deleteID = adapter.removeItem(position)
                var flag = MemoDBHelper.deleteMemo(deleteID)
                if (flag){
                    Toast.makeText(context, "Delete Success", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }//메모리 누수 방지

}