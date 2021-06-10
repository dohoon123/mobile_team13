package com.example.round

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.round.databinding.RowBinding

class TimetableRVA(val items:ArrayList<timetableData>) :RecyclerView.Adapter<TimetableRVA.ViewHolder>() {  //데이터 타입 시간표 리스트 객체 전체!

    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, view: View, data: timetableData, position:Int)
    }

    var itemClickListener:OnItemClickListener?=null


    inner class ViewHolder(val binding: RowBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.root.setOnClickListener{
                itemClickListener?.OnItemClick(this,it,items[adapterPosition],adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= RowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            memoTitle.text="routineID: "+items[position].routineID.toString()
            memoContent.text=items[position].data.toString()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


}