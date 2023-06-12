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
//		val webDriverPath = "/home/godcoder/interpark/crawler/src/main/resources/static/chromedriver"
		val webDriverPath = "/Users/a60156077/interpark/interparkCrawler/crawler/src/main/resources/static/chromedriver"
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

		val baseUrl: String = "https://tickets.interpark.com/goods/23004388"

		val contents = mutableListOf<String>()
		try{
			driver.get(baseUrl)
			// find casting content
			val castingElement: WebElement? = driver.findElement(By.xpath("//div[contains(text(), 'content casting')]"))
			if (castingElement != null) {
				val castingList = castingElement.findElement(By.xpath("//div/ul[contains(text(), castingList]"))
				if (castingList != null) {
					val listItems: List<WebElement> = castingList.findElements(By.tagName("li"))
					for (listItem in listItems) {
						println("listItem = ${listItem.text}")
					}
				}
			}
		}catch (e: Exception){
			e.printStackTrace()
		}finally {
		    driver.close()
		}
	}
}
