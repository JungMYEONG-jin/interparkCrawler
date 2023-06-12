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

		try{
			driver.get(baseUrl)

			// find title
			val titleElement = driver.findElement(By.xpath("//h2[contains(text(), 'prdTitle')]"))
			if (titleElement != null) {
				println("titleElement = ${titleElement.text}")
			} else {
				println("title not found")
			}
			// find summary info
			val summaryElement = driver.findElement(By.xpath("//div[contains(text(), 'summaryBody')]"))
			if (summaryElement != null) {
				val imgElement =
					summaryElement.findElement(By.xpath("//div[@class='posterBox']/div[@class='posterBoxTop']/img"))
				if (imgElement != null) {
					val src = imgElement.getAttribute("src").toString()
					val imageUrl = "https:" + src
					println("imageUrl = ${imageUrl}")
				}
				val summaryList = summaryElement.findElement(By.xpath("//ul[contains(text(), 'info']"))
				if (summaryList != null) {
					val summaryLis = summaryList.findElements(By.xpath("li"))
					for (summaryLi in summaryLis) {
						println("summaryLi = ${summaryLi.text}")
					}
				}
			}


			// find casting content
			val castingElement: WebElement? = driver.findElement(By.xpath("//div[contains(text(), 'content casting')]"))
			if (castingElement != null) {
				val castingList = castingElement.findElement(By.xpath("//div/ul[contains(text(), 'castingList')]"))
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
