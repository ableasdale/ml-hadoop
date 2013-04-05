name := "MarkLogic Hadoop Connector Tests"

version := "1.0"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.0.10",
  "org.scalatest" % "scalatest_2.10" % "1.9.1",
  "com.marklogic" % "marklogic-xcc" % "5.0.5",
  "com.marklogic" % "marklogic-mapreduce" % "1.1.2",
  "commons-modeler" % "commons-modeler" % "2.0.1",
  "org.specs2" %% "specs2" % "1.14" % "test",
  "junit" % "junit" % "4.11",
  "org.apache.hadoop" % "hadoop-core" % "1.1.2"
)

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                    "releases"  at "http://oss.sonatype.org/content/repositories/releases",
                    "MarkLogic" at "http://developer.marklogic.com/maven2")

parallelExecution in Test := false