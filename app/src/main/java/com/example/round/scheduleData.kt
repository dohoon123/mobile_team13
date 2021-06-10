package com.example.round


//routineID: 어느 루틴에 소속되어 있는지
//start_time, end time 단위: minute (시간, 분) 시간*60 + 분
data class scheduleData(var routineID: Int, var scheduleID: Int, var scheduleName: String, var startTime: Int, var endTime: Int)