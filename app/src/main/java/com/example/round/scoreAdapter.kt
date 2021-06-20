package com.example.round

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.round.databinding.RowBinding
import com.example.round.databinding.ScorerowBinding
import java.security.AccessController.getContext

class scoreAdapter(val context: Context, val items:ArrayList<scoreData>): RecyclerView.Adapter<scoreAdapter.ViewHolder>(){
    lateinit var rDBHelper: rDBHelper
    inner class ViewHolder(val binding: ScorerowBinding): RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= ScorerowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        rDBHelper = rDBHelper(context)
        holder.binding.apply{
            routineName.text = rDBHelper.getrName(items[position].routineID)
            //scheduleList.text=items[position].scheduleName.toString()
            //schedule list 대신 스케줄에 대한 설명이나 후기 또는 시작시간~끝난 시간 정도만 나타나는 것은 어떨지
            ratingBar.rating = items[position].score
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


}