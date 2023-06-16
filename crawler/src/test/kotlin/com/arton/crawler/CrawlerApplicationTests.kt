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
		crawlerService.travelInterPark()
	}

	@Test
	fun crawlerTest() {
//		crawlerService.travelInterPark()
		val dto =
			crawlerService.getInfo("뮤지컬", "https://tickets.interpark.com/goods/23005207")
		println("dto = ${dto}")
	}
}
