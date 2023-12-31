package com.arton.crawler

data class PerformanceCreateDTO(
    var title: String = "",
    var musicalDateTime: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var ticketOpenDate: String = "",
    var ticketEndDate: String = "",
    var place: String = "",
    var runningTime: String = "0",
    var limitAge: String = "0",
    var link: String = "https://tickets.interpark.com/",
    var description: String = "자세한 공연 정보는 예매처에서 확인 부탁드립니다.",
    var imageUrl: String = "이미지가 존재하지 않습니다.",
    var performanceType: String = "뮤지컬",

    var artists: MutableList<ArtistCreateDTO> = mutableListOf(),
    var grades: MutableList<GradeCreateDTO> = mutableListOf(),
) {

}