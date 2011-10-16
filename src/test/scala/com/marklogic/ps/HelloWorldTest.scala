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
class HelloWorldTest extends Spec with MarkLogicSteps with ShouldMatchers with GivenWhenThen with BeforeAndAfterEach with BeforeAndAfterAll {

  override def beforeEach() {
    clearDatabase()
  }

  override def afterAll() {
    closeSession()
  }

  describe("The HelloWorld sample class") {
    it("should exercise the HelloWorld Hadoop Connector Example") {
      given("MarkLogic contains the two XML documents required for this example")
      markLogicHas("<data><child>hello x</child></data>", "<data><child>world y</child></data>");
      when("the Map Reduce job is executed against the database")
      new HelloWorld().executeMapReduce()
      then("the result should be available as a single text file in the database")
      markLogicDocByUri("HelloWorld.txt") should be("hello world")
    }
  }

}