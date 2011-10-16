package com.marklogic.ps

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.GivenWhenThen
import com.marklogic.ps.steps.MarkLogicSteps
import org.scalatest.BeforeAndAfterAll
import org.slf4j.bridge.SLF4JBridgeHandler

@RunWith(classOf[JUnitRunner])
class HelloWorldTest extends Spec with MarkLogicSteps with ShouldMatchers with GivenWhenThen with BeforeAndAfterAll {

  
  override def beforeAll() {
  val rootLogger = java.util.logging.LogManager.getLogManager().getLogger("").setLevel(java.util.logging.Level.FINEST)
  SLF4JBridgeHandler.install
    setup("01_helloworld")
    //clearDatabase()
  }

  override def afterAll() {
    println("about to run after all")
    
    //Thread.sleep(1000)
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