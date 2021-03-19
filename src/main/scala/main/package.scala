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
import scala.collection.immutable.HashMap
//added comment
object main extends App {


  def getLinks(url: String, selector: String,rootUrl: String) = {
    val document: Document = Jsoup.connect(url).get()
    val aTags: Elements = document.select(selector)
    val links = for(aTag <- aTags.asScala) yield aTag.attr("href")
    links.toIndexedSeq
    val loadableLinks = for(link <- links) yield if(!(link.startsWith("www.")||link.startsWith("http")||link.startsWith("https") || link.equals("") || link.isEmpty))rootUrl + link
    loadableLinks.toList.map(x => x.toString)
  }







  def downloadFiles2(downloadableFileLinks: scala.collection.mutable.Map[String, Boolean], path: String, selector: String, rootUrl:String): Unit = {
    if(downloadableFileLinks.filter(x => x._2==false ).size==0){
      return
    }
    downloadableFileLinks.filter(x => x._2==false ).keys
      .filter(x => x.startsWith("https://tretton37.com/")).foreach(downloadableLink =>
    {

      val urlObject = new URL(downloadableLink.toString)
      val getElement = urlObject.getPath().replaceAll("/", "")


      println("urlObject is " + urlObject)
      downloadableFileLinks(urlObject.toString)=true
      val tempMap = getHashMap(urlObject.toString, selector, rootUrl)
      val newVal = tempMap.keySet.diff(downloadableFileLinks.keySet)
      newVal.map(x => downloadableFileLinks += (x -> false))


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
  val hashMapName = getHashMap(scrapedUrl,aLinksContentSelectpr, rootUrl)

  downloadFiles2(hashMapName, "resource/Homepage", aLinksContentSelectpr, rootUrl)




  def getHashMap(scrapedUrl:String,aLinksContentSelectpr:String, rootUrl:String) ={
    val linksOnPage = getLinks(scrapedUrl, aLinksContentSelectpr, rootUrl)
    var hashMapName = scala.collection.mutable.Map[String, Boolean]()
    linksOnPage.map(x => hashMapName += (x -> false))
    hashMapName
  }
}