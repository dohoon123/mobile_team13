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

    interface OnItemDoubleClickListener
    {
        fun OnItemDoubleClick(holder:ViewHolder, view: View, data: timetableData, position:Int)
    }

    interface OnItemLongClickListener
    {
        fun OnItemLongClick(holder:ViewHolder, view: View, data: timetableData, position:Int)
    }



    var itemClickListener:OnItemClickListener?=null
    var itemDoubleClickListener:OnItemDoubleClickListener?=null
    var itemLongClickListener:OnItemLongClickListener?=null


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



    fun moveItem(oldPos:Int, newPos:Int){
        //메인에서 온무브가 호출됐을때 뷰홀더가 두갠데 그게 어디서 어디로 옮겨갈지 정보를 우리가 받을 거임!!
        val item= items[oldPos]
        items.removeAt(oldPos)
        items.add(newPos,item)
        notifyItemMoved(oldPos,newPos)
        //이렇게 알려줘야 화면에 반영이 된다.
    }

    fun removeItem(pos:Int){
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }


    fun giveItem(pos:Int):timetableData{
        return items.get(pos)
    }


}