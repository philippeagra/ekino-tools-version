package utils

import java.io.File

import model.{Repository, SpringBootData}
import play.Logger

import scala.util.matching.Regex

/**
  * Parse a sbt project and extract version data.
  */
object SBTRepositoryParser extends AbstractParser {

  val buildFileName = "build.sbt"
  val artifactRegex: Regex = """[^"]*(?<! %% )\"([^\"]+)\" % \"([^\"]+)\" % \"?([^ ",\n]+).*""".r
  val scalaArtifactRegex: Regex = """[^\"]*\"([^\"]+)\" %% \"([^\"]+)\" % \"?([^ ",\n]+).*""".r
  val propertyRegex: Regex = """.*val (\w+) = \"([^ \n]+)\"(?! %)""".r
  val scalaVersionRegex: Regex = """.*scalaVersion (?:in ThisBuild )?:= \"?([^ ",\n]+).*""".r
  val scala2Regex: Regex = """(2.\d+).*""".r

  override def buildRepository(file: File, groupName: String, springBootDefaultData: SpringBootData, springBootMasterData: SpringBootData): Option[Repository] = {
    // project files
    val buildFile = getBuildFile(file)

    val name = file.getName
    val extractedArtifacts = extractFromFile(buildFile, artifactRegex, extractArtifacts)
    val scalaArtifacts = extractFromFile(buildFile, scalaArtifactRegex, extractArtifacts)
    val properties = extractFromFile(buildFile, propertyRegex, extractProperties)
    val scalaVersion = replaceVersionsHolder(extractFromFile(buildFile, scalaVersionRegex, extractValue), properties).getOrElse("value", "2.12.0")
    val shortScalaVersion = shortenScalaVersion(scalaVersion)

    Logger.debug(s"scala version for $name is $scalaVersion")

    val artifacts = replaceVersionsHolder(extractedArtifacts ++ appendScalaVersion(scalaArtifacts, shortScalaVersion), properties)

    Some(Repository(name, groupName, artifacts, s"SBT with scala $scalaVersion", Map.empty[String, String]))
  }

  override def getBuildFile(repositoryPath: File): File = {
    new File(repositoryPath, buildFileName)
  }

  private def appendScalaVersion(artifacts: Map[String, String], version: String): Map[String, String] = {
    artifacts
      .map(entry => (entry._1 + s"_$version", entry._2))
  }

  private def shortenScalaVersion(scalaVersion: String): String = {
    scala2Regex.findFirstMatchIn(scalaVersion).map(_.group(1)).getOrElse("2.12")
  }

}
