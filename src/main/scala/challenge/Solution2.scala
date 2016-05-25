package challenge

import scala.collection.mutable
import DataStructures._
import Parsers._

object Solution2 {

  def main(args: Array[String]): Unit = {
    val argMap = getArgs(Map(), args.toList)
    val stats = if (argMap.isEmpty) runStdin
                else runPull(argMap('pull).toInt, argMap('path))
    printStats(stats)
  }

  def runStdin: Map[Column, Values] = {
    val inpt = scala.io.Source.stdin.getLines()
    val headers = headerParser(inpt.take(1).next()) getOrElse List.empty[Column]
    val rows = rowStreamParser(headers, inpt)
    calculateStats(rows)
  }

  def runPull(num: Int, path: String): Map[Column, Values] = {
    val rows = RowPullParser(path).take(num)
    calculateStats(rows)
  }

  def usage: String = {
    """
      |Usage: <command> [options] where options would be:
      | --pull <num>    <num> is number of records to pull
      | --path <path>   <path> is the path to the generator
      | --help          Print this usage
      |
      | If no options are given, it will assume it is taking input from
      | STDIN.
      |
      | Example Usage:
      |   # run this program using stdin
      |   ./generator 10000 | <command>
      |
      |   # run this with the generator in PULL mode
      |   <command> --pull 1000000 --path "./generator"
    """.stripMargin
  }

  def getArgs(map: Map[Symbol, String], list: List[String]): Map[Symbol, String] =
    list match {
      case "--pull" :: num :: tail => getArgs(map ++ Map('pull -> num), tail)
      case "--path" :: path :: tail => getArgs(map ++ Map('path -> s"$path PULL"), tail)
      case "--help" :: _ => { println(usage); sys.exit(0) }
      case o :: _ => { println(s"Unknown option: $o"); sys.exit(1) }
      case _ => map
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

  def calculateStats(rows: Iterator[(Column, Option[String])], limit: Int): Map[Column, Values] = {
    val values = mutable.Map[Column, Values]() // keep side-effects local
    rows.take(limit).foreach(t => t match {
      case (col, str) => {
        val vs = values.get(col) getOrElse Values.empty(col)
        values.put(col, vs.append(str))
      }
    })
    values.toMap
  }

  def printStats(stats: Map[Column, Values]): Unit =
    stats.keys.foreach(k =>
      println(s"$k:\n${stats(k)}")
    )

}
