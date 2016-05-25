package challenge

import challenge.DataStructures.{Numbers, Texts}
import org.scalatest._

class SolutionSpec extends FlatSpec with Matchers {
  val testInput =
    """
      |"sessionId (text)","page (text)","latency (number)","timeOnPage (number)"
      |b92e18fa,welcome,56,24.614
      |b7bee5d8,query,23,415.703
      |b7fc776a,query,60,330.024
      |b8278588,welcome,47,26.123
      |b7cec397,explore,31,130.888
      |388a5cc2,,234,
      |39981fbd,,90,
    """.stripMargin.trim

  val testHeader =
    """
      |"sessionId (text)","page (text)","latency (number)","timeOnPage (number)"
    """.stripMargin.trim

  val testRows =
    """
      |b92e18fa,welcome,56,24.614
      |b7bee5d8,query,23,415.703
      |b7fc776a,query,60,330.024
      |b8278588,welcome,47,26.123
      |b7cec397,explore,31,130.888
      |388a5cc2,,234,
      |39981fbd,,90,
    """.stripMargin.trim

  "headerParser" should "give back expected headers" in {
    val expected = List(Text("sessionId"), Text("page"), Number("latency"), Number("timeOnPage"))
    Parsers.headerParser(testHeader) should be(Some(expected))
    Parsers.headerParser("hi there!") should be(None)
  }

  "main" should "give back expected stats" in {
    val headers = List(Text("sessionId"), Text("page"), Number("latency"), Number("timeOnPage"))
    val colCount = headers.size
    val rows = Parsers.rowStreamParser(headers, io.Source.fromString(testRows).getLines)
    val stats = Solution.calculateStats(rows)

    val pageStats: Texts = stats(Text("page")).asInstanceOf[Texts]
    val pageVals = List("welcome", "query", "query", "welcome", "explore")
    pageStats.stats.longest should be(Some("explore"))
    pageStats.stats.shortest should be(Some("query"))
    pageStats.stats.avg(pageStats.counts.total) should be(pageVals.foldRight(0)((a, b) => b + a.length)/7.0)
    pageStats.counts.values should be (pageVals.size)
    pageStats.counts.empties should be (pageStats.counts.total - pageVals.size)

    val timeStats: Numbers = stats(Number("timeOnPage")).asInstanceOf[Numbers]
    val timeVals = List("24.614", "415.703", "330.024", "26.123", "130.888")
    timeStats.stats.max should be(Some(415.703))
    timeStats.stats.min should be(Some(24.614))
    timeStats.stats.avg(timeStats.counts.total) should be(timeVals.foldRight(0.0)((a, b) => b + a.toDouble)/7.0)
    timeStats.counts.values should be (timeVals.size)
    timeStats.counts.empties should be (timeStats.counts.total - timeVals.size)
  }

}