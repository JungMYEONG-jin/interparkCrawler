package com.arton.crawler

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Service
class CrawlerService (
    private val objectMapper: ObjectMapper,
    @Value("\${arton.access.token}")
    var accessToken: String,
    @Value("\${driver.google.path}")
    var driverPath: String,
)
{
    val threadPool: ThreadPoolExecutor = Executors.newFixedThreadPool(3) as ThreadPoolExecutor
    fun travelInterPark(): Int {
        var cnt = 0;
        val webDriverID = "webdriver.chrome.driver"
        val webDriverPath = System.getProperty("user.dir") + driverPath
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
        val wait = WebDriverWait(driver, Duration.ofSeconds(5))
        
        val baseUrl = "https://ticket.interpark.com/TiKi/Special/TPRegionReserve.asp?ImgYn=Y&Ca=&Region=42001&RegionName=%BC%AD%BF%EF"
        try{
            driver.get(baseUrl)

            val gp = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='Gp']")))
            val objs = gp.findElements(By.xpath("//div[@class='obj']"))
            println("objs.size = ${objs.size}")
            for (obj in objs) {
                val a = obj.findElement(By.xpath("div[@class='obj_tit']/a"))
                val name = a.getAttribute("name")
                println("name = ${name}")
                if(name == "btn_genre_musical")
                    continue
                when (name) {
                    "btn_genre_musical", "btn_genre_concert" -> {
                        var genre = "뮤지컬"
                        if (name == "btn_genre_concert")
                            genre = "콘서트"
                        val Gp = obj.findElement(By.xpath("div[@class='Gp']"))
                        val contents = Gp.findElements(By.xpath("div[@class='content']"))
                        println("contents = ${contents.size}")
                        for (content in contents) {
                            val a = content.findElement(By.xpath("dl/dd[@class='name']/a"))
                            val href = a.getAttribute("href")
                            // do service
                            val info = getInfo(genre, href)
                            try {
                                addPerformance("http://aws.hancy.kr:8333/performance/crawler", info)
                                cnt++;
                            } catch (e: Exception) {
                                println("e = ${e}")
                            }
                        }
                    }
                }
            }
        }catch (e: Exception){

        }finally {
            driver.close()
            return cnt
        }
        return cnt
    }



    fun addPerformance(url: String, body: String) {
        println("url = ${url} body = ${body}")
        val factory = HttpComponentsClientHttpRequestFactory()
        factory.setConnectTimeout(5000)
        val restTemplate = RestTemplate(factory)
        // set header option
        val header = HttpHeaders()
        header.set("Authorization", "Bearer $accessToken")
        header.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity<String>(body, header)
        // uri build
        val uri = UriComponentsBuilder.fromHttpUrl(url).build()
        // post
        val resultMap = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, Map::class.java)
        if (resultMap.statusCode.isError) {

        }
        val body = resultMap.body?.let { objectMapper.writeValueAsString(it) }
        println("body = ${body}")
    }


    fun getInfo(genre: String, baseUrl: String): String {
        val webDriverID = "webdriver.chrome.driver"
        val webDriverPath = System.getProperty("user.dir") + driverPath
        System.setProperty(webDriverID, webDriverPath)
//        System.setProperty("webdriver.chrome.verboseLogging", "true");

        // chrome option
        val options: ChromeOptions = ChromeOptions()
        options.setBinary("")
        options.addArguments("--start-maximized")
        options.addArguments("--disable-popup-blocking")
        options.addArguments("--disable-default-apps")
        options.addArguments("--headless")
        // load
        val driver = ChromeDriver(options)
        val wait = WebDriverWait(driver, Duration.ofSeconds(10))
        val performanceCreateDTO = PerformanceCreateDTO()
        try{
            driver.get(baseUrl)
            // set link
            performanceCreateDTO.link = baseUrl
            // set genre
            performanceCreateDTO.performanceType = genre
            // find title
            try{
                val titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[@class='prdTitle']")))
                if (titleElement != null) {
                    performanceCreateDTO.title = titleElement.text
                }
            }catch (e: Exception){
                println("e = ${e}")
            }
            // if pop up
            try {
                val popup = driver.findElement(By.xpath("//div[@class='popupWrap']"))
                if (popup != null){
                    val closeButton = popup.findElement(By.xpath("//div[@class='popupFooter']/button"))
                    if (closeButton != null)
                        closeButton.click()
                }
            } catch (e: Exception) {

            }


            // find summary info
            try {
                val summaryElement = driver.findElement(By.xpath("//div[@class='summaryBody']"))
//                val summaryElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='summaryBody']")))
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
                                                if (age == "")
                                                    age = "0"
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
                                            var startDate = split[0]
                                            var endDate = split[0]
                                            if (split.size == 2)
                                                endDate = split[1]
                                            // 정규식 파싱
                                            startDate = startDate.replace(Regex("[^0-9.]"), "")
                                            endDate = endDate.replace(Regex("[^0-9.]"), "")
                                            if (endDate == "")
                                                endDate = startDate
                                            performanceCreateDTO.startDate = startDate
                                            performanceCreateDTO.endDate = endDate
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
                        println("listItems = ${listItems.size}")
                        for (listItem in listItems) {
                            // image
                            val imgElement = listItem.findElement(By.xpath("div[@class='castingTop']/a/div[@class='castingProfile']/img"))
                            val nameElement = listItem.findElement(By.xpath("div[@class='castingInfo']/div[@class='castingName']"))
                            var name = nameElement.getAttribute("textContent")
                            val imageUrl = imgElement.getAttribute("src").toString()
                            // name
                            performanceCreateDTO.artists.add(ArtistCreateDTO(profileImageUrl = imageUrl, name = name))
                        }
                    }
                }
            } catch (e: Exception) {
                println("e.toString() = ${e}")
            }
            return objectMapper.writeValueAsString(performanceCreateDTO)
        }catch (e: Exception){

        }finally {
            // rest
            driver.close()
            return objectMapper.writeValueAsString(performanceCreateDTO)
        }
    }
}