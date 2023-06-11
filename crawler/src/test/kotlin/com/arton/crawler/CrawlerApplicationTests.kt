package com.arton.crawler

import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CrawlerApplicationTests {

	@Test
	fun contextLoads() {

	}


	@Test
	fun crawlerTest() {
		// set property
		val webDriverID = "webdriver.chrome.driver"
		val webDriverPath = "/home/godcoder/interpark/crawler/src/main/resources/static/chromedriver"
		System.setProperty(webDriverID, webDriverPath)

		// chrome option
		val options: ChromeOptions = ChromeOptions()
		options.setBinary("")
		options.addArguments("--start-maximized")
		options.addArguments("--disable-popup-blocking")
		options.addArguments("--disable-default-apps")
		options.addArguments("--headless")

		// load
		val driver = ChromeDriver(options)

		val baseUrl: String = "https://www.google.com/search?q=interpark"

		val contents = mutableListOf<String>()

		try{
			driver.get(baseUrl)
			for (i in 1 until 4) {
				val doc: WebElement = driver.findElement(By.xpath("//*[@id=\"rso\"]/div[$i]"))
				println(doc.text)
			}
		}catch (e: Exception){
			e.printStackTrace()
		}finally {
		    driver.close()
		}
	}
}
