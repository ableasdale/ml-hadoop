name := "MarkLogic Hadoop Connector Tests"

version := "1.0"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.0.10",
  "commons-logging" % "commons-logging" % "1.1.1" % "provided",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.3", 
  "org.slf4j" % "jul-to-slf4j" % "1.7.3",
  "commons-modeler" % "commons-modeler" % "2.0.1",  
  "commons-codec" % "commons-codec" % "1.7", 
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "commons-io" % "commons-io" % "2.1",
  "org.scalatest" % "scalatest_2.10" % "1.9.1",
  "com.marklogic" % "marklogic-xcc" % "5.0.5",
  "com.marklogic" % "marklogic-mapreduce" % "1.1.2",
  //"com.codecommit" %% "anti-xml" % "0.3",
  "org.specs2" %% "specs2" % "1.14" % "test",
  // "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
  "junit" % "junit" % "4.11",
  "org.seleniumhq.selenium" % "selenium-java" % "2.6.0",
  "org.apache.hadoop" % "hadoop-core" % "1.1.2"
)

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                    "releases"  at "http://oss.sonatype.org/content/repositories/releases",
                    "MarkLogic" at "http://developer.marklogic.com/maven2")