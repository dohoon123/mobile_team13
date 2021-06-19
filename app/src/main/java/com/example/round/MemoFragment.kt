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

        initHelper()
        initRecyclerView()
        init()
    }

    fun initHelper(){
        MemoDBHelper = MemoDBHelper(getActivity())
        tempList = MemoDBHelper?.getMemo()

        binding.apply {
            recyclerView=binding!!.memoRecyclerView
            recyclerView.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)

            adapter= MyAdapter(tempList)
            recyclerView.adapter=adapter }
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }//메모리 누수 방지

}