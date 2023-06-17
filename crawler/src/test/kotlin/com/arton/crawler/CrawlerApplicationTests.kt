package com.arton.crawler

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CrawlerApplicationTests {

	@Autowired
	private lateinit var crawlerService: CrawlerService
	@Value("\${driver.google.path}")
	private lateinit var driverPath: String
	@Test
	fun contextLoads() {
		println("drivrPath = ${driverPath}")
	}

	@Test
	fun addTest() {
		val cnt = crawlerService.travelInterPark()
		println("cnt = ${cnt}")
	}

	@Test
	fun crawlerTest() {
//		crawlerService.travelInterPark()
		val dto =
//        options.addArguments("--headless")
//        options.addArguments("--headless")
			crawlerService.getInfo("콘서트", "http://ticket.interpark.com/TIKI/Main/TikiGoodsInfo.asp?GoodsCode=23008019")
		crawlerService.addPerformance("http://aws.hancy.kr:8333/performance/crawler", dto)
		println("dto = ${dto}")
	}
}
