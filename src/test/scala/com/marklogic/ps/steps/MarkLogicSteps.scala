package com.marklogic.ps.steps

import java.net.URI
import com.marklogic.xcc.ContentSource
import com.marklogic.xcc.ContentSourceFactory
import com.marklogic.xcc.Request
import com.marklogic.xcc.Session
import com.marklogic.xcc.ResultSequence

case class MarkLogicXdbcClient(val uri: URI) {

  private val contentSource: ContentSource = ContentSourceFactory.newContentSource(uri)

  def executeXqueryModule(xqueryModulePath: String) {
    val session: Session = contentSource.newSession()
    session.submitRequest(session.newAdhocQuery(fromClassPath(xqueryModulePath)))
    session.close
  }

  def newSession() = contentSource.newSession()

  private def fromClassPath(fileName: String): String = {
    io.Source.fromInputStream(classOf[MarkLogicXdbcClient].getClassLoader().getResourceAsStream(fileName)).mkString
  }
}

trait MarkLogicSteps {

  val markLogicXdbcClient: MarkLogicXdbcClient = new MarkLogicXdbcClient(new URI("xcc://admin:admin@localhost:9001"))
  lazy val mlSession: Session = markLogicXdbcClient.newSession()

  def executeQuery(query: String){
    mlSession.submitRequest(mlSession.newAdhocQuery(query))
  }
  
  def executeQueryAndGetResultSequenceAsString(query: String): String = {
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

  def markLogicDocByUri(uri: String) : String = {
	 executeQueryAndGetResultSequenceAsString("doc('"+uri+"')")
  }

  def insertDocument(xml: String, documentUri: String = math.random.toString) {
    executeQuery("xdmp:document-insert('/" + documentUri + ".xml', " + xml + ")")
  }

}