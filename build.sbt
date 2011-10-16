name := "Name"

version := "1.0"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "0.9.30",
  "commons-logging" % "commons-logging" % "1.1.1" % "provided",
  "org.slf4j" % "jcl-over-slf4j" % "1.6.2", 
  "commons-modeler" % "commons-modeler" % "2.0.1",  
  "commons-codec" % "commons-codec" % "1.5", 
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "commons-io" % "commons-io" % "2.1",
  "org.scalatest" % "scalatest_2.9.0" % "1.6.1",
  "com.codecommit" %% "anti-xml" % "0.2",
  "org.specs2" %% "specs2" % "1.6.1",
  "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
  "junit" % "junit" % "4.8.2",
  "org.seleniumhq.selenium" % "selenium-java" % "2.6.0",
  "org.apache.hadoop" % "hadoop-core" % "0.20.2"
)

resolvers ++= Seq("JBoss Repository" at "http://repository.jboss.org/maven2/",
                  "releases"  at "http://scala-tools.org/repo-releases")

unmanagedBase <<= baseDirectory { base => base / "unmanaged_libs" }

unmanagedJars in Compile <<= baseDirectory map { base => (base ** "*.jar").classpath }