package com.marklogic.ps.steps

import java.net.URI
import com.marklogic.xcc.ContentSource
import com.marklogic.xcc.ContentSourceFactory
import com.marklogic.xcc.Request
import com.marklogic.xcc.RequestOptions
import com.marklogic.xcc.Session
import com.marklogic.xcc.ResultSequence

import org.apache.commons.io.FileUtils
import java.io.File

case class MarkLogicXdbcClient(val uri: URI) {
  // Jersey uses java.util.logging - bridge to slf4  
  /*  val rootLogger = java.util.logging.LogManager.getLogManager().getLogger("");
  var handlers = rootLogger.getHandlers()
  for (i <- 0 to handlers.length) {
    rootLogger.removeHandler(handlers(i))
  } */

  private val contentSource: ContentSource = ContentSourceFactory.newContentSource(uri)

  //  def executeXqueryModule(xqueryModulePath: String) {
  //    val session: Session = contentSource.newSession()
  //    session.submitRequest(session.newAdhocQuery(fromClassPath(xqueryModulePath)))
  //    session.close
  //  }

  def newSession() = contentSource.newSession()

  //  private def fromClassPath(fileName: String): String = {
  //    io.Source.fromInputStream(classOf[MarkLogicXdbcClient].getClassLoader().getResourceAsStream(fileName)).mkString
  //  }
}

trait MarkLogicSteps {
  val XDBC_ADM_URI = "xcc://admin:admin@localhost:8010"
  val XDBC_URI = "xcc://admin:admin@localhost:9001"

  val markLogicXdbcAdminClient = new MarkLogicXdbcClient(new URI(XDBC_ADM_URI))
  val markLogicXdbcClient = new MarkLogicXdbcClient(new URI(XDBC_URI))

  lazy val mlSession = markLogicXdbcClient.newSession()

  private def executeAdmQuery(session: Session, query: String) {
    val ro = new RequestOptions()
    ro.setMaxAutoRetry(10)
    ro.setAutoRetryDelayMillis(1000);
    session.setDefaultRequestOptions(ro)
    session.getLogger().setLevel(java.util.logging.Level.FINEST)
    session.submitRequest(session.newAdhocQuery(query))
  }

  def executeQuery(query: String) {
    mlSession.submitRequest(mlSession.newAdhocQuery(query))
  }

  def executeQueryAndGetResultSequenceAsString(query: String): String = {
    mlSession.submitRequest(mlSession.newAdhocQuery(query)).asString()
  }

  def theResultFromTheGivenQuery(query: String): String = {
    mlSession.submitRequest(mlSession.newAdhocQuery(query)).asString()
  }

  def clearDatabase() {
    executeQuery("for $doc in doc() return xdmp:document-delete(xdmp:node-uri($doc))")
  }

  def closeSession() {
    mlSession.close()
  }

  def markLogicHas(xmlDocs: String*) = {
    xmlDocs.foreach(xml => insertDocument(xml))
  }

  def markLogicDocByUri(uri: String): String = {
    executeQueryAndGetResultSequenceAsString("doc('" + uri + "')")
  }

  def insertDocument(xml: String, documentUri: String = math.random.toString) {
    executeQuery("xdmp:document-insert('/" + documentUri + ".xml', " + xml + ")")
  }

  def quickEstimate(): String = {
    executeQueryAndGetResultSequenceAsString("xdmp:estimate(doc())")
  }

  def setup(foldername: String) {
    val mlAdmSession = markLogicXdbcAdminClient.newSession()
    executeAdmQuery(mlAdmSession, FileUtils.readFileToString(new File("src/test/xquery/configuration-scripts/" + foldername + "/setup.xqy")))
    mlAdmSession.close
  }

  def teardown(foldername: String) {
    val mlAdmSession = markLogicXdbcAdminClient.newSession()
    println("first part of teardown")
    executeAdmQuery(mlAdmSession, FileUtils.readFileToString(new File("src/test/xquery/configuration-scripts/" + foldername + "/teardown-app-server.xqy")))
    println("second part of teardown")
    executeAdmQuery(mlAdmSession, FileUtils.readFileToString(new File("src/test/xquery/configuration-scripts/" + foldername + "/teardown-db.xqy")))
    println("teardown done")
    //executeAdmQuery(mlAdmSession, FileUtils.readFileToString(new File("src/test/xquery/configuration-scripts/" + foldername + "/teardown-db.xqy")))
   // This can be removed
   // new com.marklogic.ps.XccHelper().clean

  }

  def loadSampleData() = {
    FileUtils.copyDirectory(new File("src/main/resources/sample-data"), new File("/tmp/sample-data"))
    print("Loading sample XML documents into MarkLogic ")
    executeQuery(FileUtils.readFileToString(new File("src/main/resources/load-sample-data.xqy")))
    while (quickEstimate != "93") {
      Thread.sleep(1000)
      print(". ")
    }
  }

  def cleanupSampleData() = {
    FileUtils.deleteDirectory(new File("/tmp/sample-data"))
  }

}
