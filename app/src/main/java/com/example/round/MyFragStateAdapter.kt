package com.example.round

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class MyFragStateAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> TimetableFragment()
            1 -> TodayFragment()
            2 -> MemoFragment()
            3 -> EtcFragment()
            else -> TodayFragment()
        }
    }
}