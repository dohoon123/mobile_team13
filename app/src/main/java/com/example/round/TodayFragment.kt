package com.example.round


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.round.databinding.FragmentTodayBinding

class TodayFragment : Fragment() {
    var binding: FragmentTodayBinding?=null
    lateinit var scheduleDBHelper: sDBHelper
    lateinit var routineDBHelper:rDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}