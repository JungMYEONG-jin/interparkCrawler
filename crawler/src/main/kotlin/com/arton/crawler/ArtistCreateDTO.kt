package com.arton.crawler

data class ArtistCreateDTO(
    val name: String,
    val profileImageUrl: String,
    val age: String = "0",
    val snsId: String = "",
) {
}