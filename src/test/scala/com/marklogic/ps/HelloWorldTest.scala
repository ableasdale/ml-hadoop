package com.marklogic.ps

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{FunSpec, GivenWhenThen, BeforeAndAfterAll}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.marklogic.ps.steps.MarkLogicSteps
import org.slf4j.bridge.SLF4JBridgeHandler

@RunWith(classOf[JUnitRunner])
class HelloWorldTest extends FunSpec with MarkLogicSteps with ShouldMatchers with GivenWhenThen with BeforeAndAfterAll {

  val rootLogger = java.util.logging.LogManager.getLogManager().getLogger("").setLevel(java.util.logging.Level.FINEST)

  override def beforeAll() {
    println("HelloWorld Test :: about to run 'before all'")
    //SLF4JBridgeHandler.install
    setup("01_helloworld")
    //clearDatabase()
  }

  override def afterAll() {
    println("HelloWorld Test :: about to run 'after all'")

    // Until properly fixed
    //Thread.sleep(9000)
    teardown("01_helloworld")
    closeSession()
  }

  describe("The HelloWorld sample class") {
    it("should exercise the HelloWorld Hadoop Connector Example") {
      given("MarkLogic contains the two XML documents required for this example")
      markLogicHas("<data><child>hello x</child></data>", "<data><child>world y</child></data>");
      when("the Map Reduce job is executed against the database")
      new HelloWorld().executeMapReduce()
      then("the result should be available as a single text file in the database")
      println(markLogicDocByUri("HelloWorld.txt"))
      markLogicDocByUri("HelloWorld.txt") should be("hello world")
    }
  }

}