package com.example.round


//routineTerm: 기간(시간 단위. 12시간, 24시간 등), repeatDay: 반복 요일
data class routineData(var routineID: Int, var routineName: String, var routineTerm: Int, var alarmChecked : Int, var disposable:Int)//var repeatDay: 추가 예정
                                                                                              //알람 누른 횟수 확인용 alarmChecked