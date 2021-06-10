package com.example.round


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.round.databinding.FragmentMemoBinding

class MemoFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MyAdapter       //단순한 단어 넣어주기

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
        tempList.add(memoData(1,"제목","내용 메모 예시"))
        tempList.add(memoData(2,"제목","내용 메모 예시"))
        tempList.add(memoData(3,"제목","내용 메모 예시"))
        tempList.add(memoData(4,"제목","내용 메모 예시"))

        binding.apply {
            recyclerView=binding!!.memoRecyclerView
            recyclerView.layoutManager= LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL,false)

            adapter= MyAdapter(tempList)
            recyclerView.adapter=adapter        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }//메모리 누수 방지

}