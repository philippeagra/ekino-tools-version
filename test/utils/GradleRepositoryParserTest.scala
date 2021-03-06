package utils

import org.junit.Test
import org.scalatest.Matchers
import org.scalatest.junit.AssertionsForJUnit

class GradleRepositoryParserTest extends AssertionsForJUnit with Matchers  {

  @Test
  def should_parse_ekino_style_dependencies() {
    val prop = "compile group: 'com.ekino.base', name: 'ekino-base-service', version: property('ekino-base-service.version')"
    val matchers = GradleRepositoryParser.artifactRegex.findAllIn(prop)

    matchers should not be empty
    matchers.group(1) shouldBe "com.ekino.base"
    matchers.group(2) shouldBe "ekino-base-service"
    matchers.group(3) shouldBe "ekino-base-service.version"

  }

  @Test
  def should_parse_ekino_style_dependencies_with_special_characters() {
    val prop = """playTest group: 'org.scalatestplus.play', name: 'scalatestplus-play_2.11', version: property('scalatestplus-play_2.11.version')"""
    val matchers = GradleRepositoryParser.artifactRegex.findAllIn(prop)

    matchers should not be empty
    matchers.group(1) shouldBe "org.scalatestplus.play"
    matchers.group(2) shouldBe "scalatestplus-play_2.11"
    matchers.group(3) shouldBe "scalatestplus-play_2.11.version"

  }

  @Test
  def should_parse_other_style_dependencies() {
    val prop = """  compile("com.ekino.library:ekino-library-logs:${ekinoLogsVersion}")"""
    val matchers = GradleRepositoryParser.artifactRegex.findAllIn(prop)

    matchers should not be empty
    matchers.group(1) shouldBe "com.ekino.library"
    matchers.group(2) shouldBe "ekino-library-logs"
    matchers.group(3) shouldBe "ekinoLogsVersion"

  }

  @Test
  def should_parse_maven_bom() {
    val prop = """mavenBom 'org.springframework.cloud:spring-cloud-dependencies:Dalston.SR4'"""
    val matchers = GradleRepositoryParser.artifactRegex.findAllIn(prop)

    matchers should not be empty
    matchers.group(1) shouldBe "org.springframework.cloud"
    matchers.group(2) shouldBe "spring-cloud-dependencies"
    matchers.group(3) shouldBe "Dalston.SR4"
  }

  @Test
  def should_parse_kotlin_style_dependencies() {
    val prop = """  compile("com.ekino.base:ekino-base-service:" + property("ekino-base-service.version"))"""
    val matchers = GradleRepositoryParser.artifactRegex.findAllIn(prop)

    matchers should not be empty
    matchers.group(1) shouldBe "com.ekino.base"
    matchers.group(2) shouldBe "ekino-base-service"
    matchers.group(3) shouldBe "ekino-base-service.version"

  }

  @Test
  def should_parse_kotlin_style_dependencies_with_named_parameters() {
    val prop = """ testCompile (group= "org.assertj", name = "assertj-core", version = property("assertj.version") as String)"""
    val matchers = GradleRepositoryParser.artifactRegex.findAllIn(prop)

    matchers should not be empty
    matchers.group(1) shouldBe "org.assertj"
    matchers.group(2) shouldBe "assertj-core"
    matchers.group(3) shouldBe "assertj.version"

  }

  @Test
  def should_parse_plugins() {
    val prop = """ id 'com.ekino.base'   version '1.2.0' """
    val matchers = GradleRepositoryParser.pluginRegex.findAllIn(prop)

    matchers should not be empty
    matchers.group(1) shouldBe "com.ekino.base"
    matchers.group(2) shouldBe "1.2.0"
  }

  @Test
  def should_parse_plugins_with_kotlin_script() {
    val prop = """ id ("com.ekino.base")     version "1.2.0" """
    val matchers = GradleRepositoryParser.pluginRegex.findAllIn(prop)

    matchers should not be empty
    matchers.group(1) shouldBe "com.ekino.base"
    matchers.group(2) shouldBe "1.2.0"
  }

  @Test
  def should_parse_plugins_with_kotlin_script_without_space_after_id() {
    val prop = """ id("com.ekino.base")     version "1.2.0" """
    val matchers = GradleRepositoryParser.pluginRegex.findAllIn(prop)

    matchers should not be empty
    matchers.group(1) shouldBe "com.ekino.base"
    matchers.group(2) shouldBe "1.2.0"
  }

}
