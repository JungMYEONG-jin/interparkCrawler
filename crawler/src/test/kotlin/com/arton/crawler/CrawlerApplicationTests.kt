package com.arton.crawler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
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
//		val webDriverPath = "/Users/a60156077/interpark/interparkCrawler/crawler/src/main/resources/static/chromedriver"
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

		val baseUrl: String = "https://tickets.interpark.com/goods/23007049"

		try{
			driver.get(baseUrl)
			// find title
			val titleElement = driver.findElement(By.xpath("//h2[@class='prdTitle']"))
			if (titleElement != null) {
				println("titleElement = ${titleElement.text}")
			} else {
				println("title not found")
			}
			// find summary info
			val summaryElement = driver.findElement(By.xpath("//div[@class='summaryBody']"))
			if (summaryElement != null) {
				val imgElement =
					summaryElement.findElement(By.xpath("//div[@class='posterBox']/div[@class='posterBoxTop']/img"))
				if (imgElement != null) {
					val src = imgElement.getAttribute("src").toString()
					val performanceImageUrl = src
					println("imageUrl = ${performanceImageUrl}")
				}
				val summaryList = summaryElement.findElement(By.xpath("//ul[@class='info']"))
				if (summaryList != null) {
					val summaryLis = summaryList.findElements(By.xpath("li"))
					for (summaryLi in summaryLis) {
						val infoLabel = summaryLi.findElement(By.xpath("strong[@class='infoLabel']"))
						if (infoLabel != null) {
							when(infoLabel.text){
								"장소" -> {
									val infoDesc =
										summaryLi.findElement(By.xpath("//div[@class='infoDesc']/a[@class='infoBtn']"))
									println("label = ${infoLabel.text}, infoDesc = ${infoDesc.text}")
								}
								"가격" -> {
									val infoDesc =
										summaryLi.findElement(By.xpath("//div[@class='infoDesc']/ul[@class='infoPriceList']"))
									val itemInfos = infoDesc.findElements(By.xpath("li[@class='infoPriceItem']"))
									for (itemInfo in itemInfos) {
										val gradeName = itemInfo.findElement(By.xpath("span[@class='name']"))
										val gradePrice = itemInfo.findElement(By.xpath("span[@class='price']"))
										println("label = ${infoLabel.text}, gradeName = ${gradeName.text}, price = ${gradePrice.text}")
									}
								}
								"공연기간", "관람연령" -> {
									val infoDesc =
										summaryLi.findElement(By.xpath("div[@class='infoDesc']/p[@class='infoText']"))
									println("label = ${infoLabel.text}, infoDesc = ${infoDesc.text}")
								}
							}
						}
					}
				}
			}


			// find casting content
			val castingElement: WebElement? = driver.findElement(By.xpath("//div[@class='content casting']"))
			if (castingElement != null) {
				val castingList = castingElement.findElement(By.xpath("//div/ul[@class='castingList']"))
				if (castingList != null) {
					val listItems: List<WebElement> = castingList.findElements(By.tagName("li"))
					for (listItem in listItems) {
						// image
						val imgElement = listItem.findElement(By.xpath("//div[@class='castingProfile']/img"))
						val imageUrl = imgElement.getAttribute("src").toString()
						println("imageUrl = ${imageUrl}")
						// name
						println("listItem = ${listItem.text}")
					}
				}
			}


			// rest service
			// call artist add

			// call performance add

		}catch (e: Exception){
			e.printStackTrace()
		}finally {
		    driver.close()
		}
	}
}
