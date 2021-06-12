package com.example.round

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.round.databinding.RowBinding

//db
class MyAdapter(val items:ArrayList<memoData>): RecyclerView.Adapter<MyAdapter.ViewHolder>(){

    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, view: View, data:memoData, position:Int)
    }

    var itemClickListener:OnItemClickListener?=null


    inner class ViewHolder(val binding: RowBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.root.setOnClickListener{
                itemClickListener?.OnItemClick(this,it,items[adapterPosition],adapterPosition)
            }
        }
    }
    fun removeItem(pos:Int): String{
        var deletedID= items[pos].id.toString()
        items.removeAt(pos)
        notifyItemRemoved(pos)
        return deletedID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= RowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            memoTitle.text=items[position].title
            memoContent.text=items[position].content
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


}