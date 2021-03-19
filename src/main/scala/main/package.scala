package main
import scala.language.postfixOps
import scala.jdk.CollectionConverters._
import sys.process._
import java.net.URL
import java.io.File
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import java.nio.file.{Files, Path, Paths}
//added comment
object main extends App {
  System.setProperty("http.proxyHost", "proxy-se-uan.ddc.teliasonera.net")
  System.setProperty("http.proxyPort", "8080")

  def getLinks(url: String, selector: String,rootUrl: String) = {
    val document: Document = Jsoup.connect(url).get()
    val aTags: Elements = document.select(selector)
    val links = for(aTag <- aTags.asScala) yield aTag.attr("href")
    links.toIndexedSeq
    val loadableLinks = for(link <- links) yield if(!(link.startsWith("www.")||link.startsWith("http")||link.startsWith("https") || link.equals("") || link.isEmpty))rootUrl + link
    loadableLinks.toList
  }


  def downloadFiles(downloadableFileLinks: List[Any], path: String, selector: String, rootUrl:String): Unit = {
    downloadableFileLinks.map( downloadableLink =>
    {
      // TODO: Only write if fileNotExists?
      if(downloadableLink.toString.startsWith("https://tretton37.com/")) {
        println("link is " + downloadableLink)
        val urlObject = new URL(downloadableLink.toString)
        val getElement = urlObject.getPath().replaceAll("/", "")
        val directory = createDirIfNotExist(path + "\\" + getElement)
        val filePath = (directory + "\\" + getElement + ".html")
        urlObject #> new File(filePath) !!;
        println("writing data into file" + filePath)
        if (!path.contains(getElement))
          downloadFiles(getLinks(urlObject.toString, selector, rootUrl), directory.toString, selector, rootUrl)
      }
    })
  }

  def createDirIfNotExist(dir: String): java.nio.file.Path = {
    val path = Paths.get(dir)
    if (Files.exists(path)) {
      if (Files.isDirectory(path)) {
        path
      } else {
        throw new IllegalArgumentException(s"Directory is an existing file: $dir")
      }
    } else {
      Files.createDirectories(Paths.get(dir))
    }
  }

  val rootUrl = "https://tretton37.com"
  val scrapedUrl = "https://tretton37.com/"
  val aLinksContentSelectpr = "li a[target !=_blank]"
  val linksOnPage = getLinks(scrapedUrl, aLinksContentSelectpr, rootUrl)
  downloadFiles(linksOnPage, "C:\\Users\\ose414\\IdeaProjects\\files\\Homepage", aLinksContentSelectpr, rootUrl)

}