package com.arton.crawler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CrawlerApplicationTests {

	@Autowired
	private lateinit var crawlerService: CrawlerService;
	@Test
	fun contextLoads() {

	}


	@Test
	fun crawlerTest() {
		val baseUrl: String = "https://tickets.interpark.com/goods/23007049"
//		val baseUrl: String = "https://tickets.interpark.com/goods/23007654"
//		val baseUrl: String = "https://tickets.interpark.com/goods/23005708"
		val info = crawlerService.getInfo(baseUrl)
		println("info = ${info}")
	}
}
