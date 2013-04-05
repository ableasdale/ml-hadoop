package com.marklogic.ps.steps

import java.net.URI
import com.marklogic.xcc.ContentSource
import com.marklogic.xcc.ContentSourceFactory
import com.marklogic.xcc.RequestOptions
import com.marklogic.xcc.Session

import org.apache.commons.io.FileUtils
import java.io.File
import org.slf4j.LoggerFactory

case class MarkLogicXdbcClient(uri: URI) {

  private val contentSource: ContentSource = ContentSourceFactory.newContentSource(uri)

  //  def executeXqueryModule(xqueryModulePath: String) {
  //    val session: Session = contentSource.newSession()
  //    session.submitRequest(session.newAdhocQuery(fromClassPath(xqueryModulePath)))
  //    session.close
  //  }

  def newSession() = contentSource.newSession()

}

trait MarkLogicSteps {

  def LOG = LoggerFactory.getLogger("MarkLogicSteps")

  // TODO - configure these from the XML files on each test
  val XDBC_ADM_URI = "xcc://admin:admin@localhost:8010"
  val XDBC_URI = "xcc://admin:admin@localhost:9001"

  val markLogicXdbcAdminClient = new MarkLogicXdbcClient(new URI(XDBC_ADM_URI))
  val markLogicXdbcClient = new MarkLogicXdbcClient(new URI(XDBC_URI))

  lazy val mlSession = markLogicXdbcClient.newSession()

  private def executeAdmQuery(session: Session, query: String) {
    val ro = new RequestOptions()
    ro.setMaxAutoRetry(10)
    ro.setAutoRetryDelayMillis(1000)
    session.setDefaultRequestOptions(ro)
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

  def markLogicHas(xmlDocs: String*) {
    xmlDocs.foreach(xml => insertDocument(xml))
  }

  def markLogicDocByUri(uri: String): String = {
    executeQueryAndGetResultSequenceAsString("doc('" + uri + "')")
  }

  def insertDocument(xml: String, documentUri: String = math.random.toString) {
    LOG.debug("Trying to insert content: " + xml)
    executeQuery("xdmp:document-insert('/" + documentUri + ".xml', " + xml + ")")
  }

  def quickEstimate(): String = {
    executeQueryAndGetResultSequenceAsString("xdmp:estimate(doc())")
  }

  def setup(foldername: String) {
    LOG.debug("Setup :: in setup for: " + foldername)
    val mlAdmSession = markLogicXdbcAdminClient.newSession()
    executeAdmQuery(mlAdmSession, FileUtils.readFileToString(new File("src/test/xquery/configuration-scripts/" + foldername + "/setup.xqy")))
    mlAdmSession.close()
  }

  def teardown(foldername: String) {
    val mlAdmSession = markLogicXdbcAdminClient.newSession()
    LOG.debug("First part of Test Teardown")
    executeAdmQuery(mlAdmSession, FileUtils.readFileToString(new File("src/test/xquery/configuration-scripts/" + foldername + "/teardown-app-server.xqy")))
    LOG.debug("Second part of Test Teardown")
    executeAdmQuery(mlAdmSession, FileUtils.readFileToString(new File("src/test/xquery/configuration-scripts/" + foldername + "/teardown-db.xqy")))
    LOG.debug("Teardown Complete")
  }

  def loadSampleData() {
    FileUtils.copyDirectory(new File("src/main/resources/sample-data"), new File("/tmp/sample-data"))
    print("Loading sample XML documents into MarkLogic ")
    executeQuery(FileUtils.readFileToString(new File("src/main/resources/load-sample-data.xqy")))
    while (quickEstimate != "93") {
      Thread.sleep(1000)
      print(". ")
    }
  }

  def cleanupSampleData() {
    FileUtils.deleteDirectory(new File("/tmp/sample-data"))
  }
}
