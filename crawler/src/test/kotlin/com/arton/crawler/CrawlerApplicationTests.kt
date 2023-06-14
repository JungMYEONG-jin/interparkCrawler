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
//		val baseUrl: String = "https://tickets.interpark.com/goods/23007654"
//		val baseUrl: String = "https://tickets.interpark.com/goods/23005708"
		val objectMapper: ObjectMapper = ObjectMapper()
		try{
			driver.get(baseUrl)
			val performanceCreateDTO: PerformanceCreateDTO = PerformanceCreateDTO()
			// set link
			performanceCreateDTO.link = baseUrl
			// find title
			try{
				val titleElement = driver.findElement(By.xpath("//h2[@class='prdTitle']"))
				if (titleElement != null) {
					performanceCreateDTO.title = titleElement.text
				}
			}catch (e: Exception){

			}

			// find summary info
			try {
				val summaryElement = driver.findElement(By.xpath("//div[@class='summaryBody']"))
				if (summaryElement != null) {
					val imgElement =
						summaryElement.findElement(By.xpath("//div[@class='posterBox']/div[@class='posterBoxTop']/img"))
					if (imgElement != null) {
						val src = imgElement.getAttribute("src").toString()
						val performanceImageUrl = src
						performanceCreateDTO.imageUrl = performanceImageUrl
					}
					val summaryList = summaryElement.findElement(By.xpath("//ul[@class='info']"))
					if (summaryList != null) {
						val summaryLis = summaryList.findElements(By.xpath("li"))
						for (summaryLi in summaryLis) {
							val infoLabel = summaryLi.findElement(By.xpath("strong[@class='infoLabel']"))
							if (infoLabel != null) {
								when (infoLabel.text) {
									"장소" -> {
										val infoDesc =
											summaryLi.findElement(By.xpath("//div[@class='infoDesc']/a[@class='infoBtn']"))
										if (infoDesc != null) {
											performanceCreateDTO.place = infoDesc.text.replace(
												"(자세히)",
												""
											)
										}
									}

									"가격" -> {
										val infoDesc =
											summaryLi.findElement(By.xpath("//div[@class='infoDesc']/ul[@class='infoPriceList']"))
										val itemInfos = infoDesc.findElements(By.xpath("li[@class='infoPriceItem']"))
										for (itemInfo in itemInfos) {
											val split = itemInfo.text.split("\n")
											for (s in split) {
												val indexOfFirst = s.indexOfFirst { c -> c.isDigit() }
												val grade = s.substring(0, indexOfFirst)
												val price =
													s.substring(indexOfFirst).replace(Regex("[^0-9]"), "")
												performanceCreateDTO.grades.add(GradeCreateDTO(gradeName =  grade, price = price))
											}
										}
									}

									"관람연령" -> {
										val infoDesc =
											summaryLi.findElement(By.xpath("div[@class='infoDesc']/p[@class='infoText']"))
										if (infoDesc != null) {
											if (!infoDesc.text.contains("전체")) {
												var age = infoDesc.text.replace(Regex("[^0-9]"), "")
												if (infoDesc.text.contains("개월")) {
													age = (age.toInt() / 12).toString()
												}
												performanceCreateDTO.limitAge = age
											}
										}
									}

									"공연시간" ->{
										val infoDesc =
											summaryLi.findElement(By.xpath("div[@class='infoDesc']/p[@class='infoText']"))
										if (infoDesc != null) {
											val runningTime = infoDesc.text.replace(Regex("[^0-9]"), "")
											performanceCreateDTO.runningTime = runningTime
										}
									}

									"공연기간" -> {
										val infoDesc =
											summaryLi.findElement(By.xpath("div[@class='infoDesc']/p[@class='infoText']"))
										if (infoDesc != null) {
											val dates = infoDesc.text.replace(" ", "")
											// set date first
											performanceCreateDTO.musicalDateTime = dates
											// split date
											val split = dates.split("~")
											performanceCreateDTO.startDate = split[0]
											if (split.size == 2)
												performanceCreateDTO.endDate = split[1]
										}
									}
								}
							}
						}
					}
				}
			} catch (e: Exception) {

			}

			// details url
			try{
				val detailsContent = driver.findElement(By.xpath("//div[@class='contentDetail']/p/strong/img"))
				if (detailsContent != null) {
					performanceCreateDTO.description = detailsContent.getAttribute("src").toString()
				}
			}catch(e: Exception){

			}


			// find casting content
			try {
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
							performanceCreateDTO.artists.add(ArtistCreateDTO(profileImageUrl = imageUrl, name = listItem.text))
						}
					}
				}
			} catch (e: Exception) {

			}
			val dtoToJson = objectMapper.writeValueAsString(performanceCreateDTO)
			println("dtoToJson = ${dtoToJson}")
		}catch (e: Exception){
			e.printStackTrace()
		}finally {
			// rest
		    driver.close()
		}
	}
}
