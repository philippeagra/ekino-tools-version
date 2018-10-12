package services

import java.io.FileNotFoundException
import java.net.URL
import java.util.Base64

import model.CustomExecutionContext.executionContextExecutor
import play.api.Logger

import scala.concurrent.Future
import scala.io.Source
import scala.util.control.NonFatal
import scala.util.matching.Regex
import scala.xml.XML

/**
  * Download metadata from a maven url and return a Future of the last release.
  */
object MavenVersionFetcher {

  val pattern: Regex = "([^:]+):(.+)".r

  // download maven-metadata to get the latest repository
  def getLatestVersion(name: String, mavenUrl: String, mavenUser: String, mavenPassword: String): Future[(String, String)] = Future {

    try {
      val uri = name match {
        case pattern(group, artefact) => s"${group.replace('.', '/')}/$artefact"
      }

      // maven meta data
      val url = mavenUrl + uri + "/maven-metadata.xml"

      val connection = new URL(url).openConnection
      if (!mavenUser.isEmpty && !mavenPassword.isEmpty) {
        connection.setRequestProperty(HttpBasicAuth.AUTHORIZATION,
          HttpBasicAuth.getHeader(mavenUser, mavenPassword)
        )
      }

      val html = Source.fromInputStream(connection.getInputStream)
      val xmlFromString = XML.loadString(html.mkString)
      val version = xmlFromString \\ "release" // XPATH to select release node
      Logger.info("Resolved " + url + ":" + version.text)

      (name, version.text)

    } catch {
      case _: FileNotFoundException => (name, "")
      case NonFatal(e) =>
        Logger.error(s"Unexpected exception for $name", e)
        (name, "")

    }

  }

  def isMavenVersion(name: String): Boolean =
    name match {
      case pattern(_, _) => true
      case _             => false
    }

  object HttpBasicAuth {
    val BASIC = "Basic"
    val AUTHORIZATION = "Authorization"

    def getHeader(username: String, password: String): String =
      BASIC + " " + encodeCredentials(username, password)

    def encodeCredentials(username: String, password: String): String =
      new String(Base64.getEncoder.encode((username + ":" + password).getBytes))
  }

}
