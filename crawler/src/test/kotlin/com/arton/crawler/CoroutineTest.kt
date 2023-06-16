package com.arton.crawler

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CoroutineTest {

    @Test
    internal fun runBlockingTest() {
        var a = "오픈런"
        a = a.replace(Regex("[^0-9.]"), "")
        println("a = ${a}")
    }

}