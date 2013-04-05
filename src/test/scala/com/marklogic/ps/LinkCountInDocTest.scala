package com.marklogic.ps

import org.scalatest.matchers.ShouldMatchers
import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.marklogic.ps.steps.MarkLogicSteps
import org.slf4j.LoggerFactory

@RunWith(classOf[JUnitRunner])
class LinkCountInDocTest extends FunSpec with MarkLogicSteps with ShouldMatchers with GivenWhenThen with BeforeAndAfterEach with BeforeAndAfterAll {

  def LOG = LoggerFactory.getLogger("LinkCountInDocTest")

  override def beforeAll() {
    LOG.info("About to run 'beforeAll()' setup")
    setup("02_linkcountindoc")
    Thread.sleep(5000)
    loadSampleData()
  }

  override def afterAll() {
    LOG.info("About to run 'afterAll()' teardown")
    teardown("02_linkcountindoc")
    closeSession()
    cleanupSampleData()
  }

  describe("The LinkCountInDoc sample class") {
    it("should exercise the LinkCountInDoc Hadoop Connector Example") {
      val referencesQuery = "for $ref in //ref-count\nreturn fn:concat(xdmp:node-uri($ref),' ',$ref/text())";
      Given("MarkLogic contains the sample XML documents required for this example")
      // set up in the beforeAll method
      When("the Map Reduce job is executed against the database")
      new LinkCountInDoc().executeMapReduce()
      Then("the result should match the reference string held by this test")
      theResultFromTheGivenQuery(referencesQuery) should be("enwiki/Ayn Rand 1\nenwiki/List of characters in Atlas Shrugged 4\nenwiki/Academy Award for Best Art Direction 1\nenwiki/Academy Award 2\nenwiki/Aristotle 5")
    }
  }

}