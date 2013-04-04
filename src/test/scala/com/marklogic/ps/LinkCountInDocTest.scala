package com.marklogic.ps

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.GivenWhenThen
import com.marklogic.ps.steps.MarkLogicSteps
import org.scalatest.BeforeAndAfterEach
import org.scalatest.BeforeAndAfterAll

@RunWith(classOf[JUnitRunner])
class LinkCountInDocTest extends Spec with MarkLogicSteps with ShouldMatchers with GivenWhenThen with BeforeAndAfterEach with BeforeAndAfterAll {

  override def beforeAll() {
    setup("02_linkcountindoc")
    clearDatabase()
    loadSampleData()
  }

  override def afterAll() {
    closeSession()
    cleanupSampleData()
    teardown("02_linkcountindoc")
  }

  describe("The LinkCountInDoc sample class") {
    it("should exercise the LinkCountInDoc Hadoop Connector Example") {
      val referencesQuery = "for $ref in //ref-count\nreturn fn:concat(xdmp:node-uri($ref),' ',$ref/text())";
      given("MarkLogic contains the sample XML documents required for this example")
      // set up in the beforeAll method
      when("the Map Reduce job is executed against the database")
      new LinkCountInDoc().executeMapReduce()
      then("the result should match the reference string held by this test")
      theResultFromTheGivenQuery(referencesQuery) should be("enwiki/Ayn Rand 1\nenwiki/List of characters in Atlas Shrugged 4\nenwiki/Academy Award for Best Art Direction 1\nenwiki/Academy Award 2\nenwiki/Aristotle 5")
    }
  }

}