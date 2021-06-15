package com.example.round

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.round.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val textarr = arrayListOf<String>("시간표 리스트", "오늘의 시간표", "메모", "부가기능")
    val iconarr = arrayListOf<Int>(R.drawable.timetable, R.drawable.today, R.drawable.memo, R.drawable.etc)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewPager()
    }

    private fun initViewPager() {
        binding.viewPager.adapter = MyFragStateAdapter(this)
        TabLayoutMediator(binding.tablayout, binding.viewPager) {
            tab, position ->
            tab.text = textarr[position]
            tab.setIcon(iconarr[position])
        }.attach()
        binding.viewPager.setCurrentItem(1) //오늘의 시간표 메인으로 설정
    }

}