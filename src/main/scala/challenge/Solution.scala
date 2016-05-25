package challenge

import scala.collection.mutable
import DataStructures._
import Parsers._

object Solution {

  /**
    * Take input from stdin, collect stats and print them to stdout
    */
  def main(args: Array[String]): Unit = {
    val inpt = scala.io.Source.stdin.getLines()
    val headers = headerParser(inpt.take(1).next()) getOrElse List.empty[Column]
    val rows = rowStreamParser(headers, inpt)
    val stats = calculateStats(rows)
    printStats(stats)
  }

  /**
    * Calculates the stats, returning an immutable Map
    */
  def calculateStats(rows: Iterator[(Column, Option[String])]): Map[Column, Values] = {
    val values = mutable.Map[Column, Values]() // keep side-effects local
    rows.foreach(t => t match {
      case (col, str) => {
        val vs = values.get(col) getOrElse Values.empty(col)
        values.put(col, vs.append(str))
      }
    })
    values.toMap
  }

  def printStats(stats: Map[Column, Values]): Unit = {
    stats.keys.foreach(k => println(s"$k:\n${stats(k)}"))
  }

}

