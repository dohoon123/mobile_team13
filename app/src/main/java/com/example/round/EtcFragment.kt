package com.example.round

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.round.databinding.FragmentEtcBinding

class EtcFragment : Fragment() {
    var binding: FragmentEtcBinding?=null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEtcBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {
            alarm.setOnClickListener {
                var intent= Intent(context, alarm::class.java)
                startActivity(intent)
            }
            myPage.setOnClickListener {

            }
            etcmenu3.setOnClickListener {

            }
            etcmenu4.setOnClickListener {

            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}