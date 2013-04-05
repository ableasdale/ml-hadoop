package com.marklogic.ps

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{FunSpec, GivenWhenThen, BeforeAndAfterAll}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.marklogic.ps.steps.MarkLogicSteps

@RunWith(classOf[JUnitRunner])
class HelloWorldTest extends FunSpec with MarkLogicSteps with ShouldMatchers with GivenWhenThen with BeforeAndAfterAll {

  override def beforeAll() {
    println("HelloWorld Test :: about to run 'before all'")
    setup("01_helloworld")
    //clearDatabase()
  }

  override def afterAll() {
    println("HelloWorld Test :: about to run 'after all - currently not closing session'")
    Thread.sleep(120000)
    teardown("01_helloworld")
    //closeSession()
  }

  describe("The HelloWorld sample class") {
    it("should exercise the HelloWorld Hadoop Connector Example") {
      Given("MarkLogic contains the two XML documents required for this example")
      Thread.sleep(5000)
      markLogicHas("<data><child>hello x</child></data>", "<data><child>world y</child></data>")
      When("the Map Reduce job is executed against the database")
      new HelloWorld().executeMapReduce()
      Then("the result should be available as a single text file in the database")
      println(markLogicDocByUri("HelloWorld.txt"))
      markLogicDocByUri("HelloWorld.txt") should be("hello world")
    }
  }

}