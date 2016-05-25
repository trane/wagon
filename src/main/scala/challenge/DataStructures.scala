package challenge

object DataStructures {
  case class Counts(values: Int, empties: Int) {
    val total = values + empties

    def apply(option: Option[String]): Counts = option match {
      case Some(_) => copy(values = this.values+1)
      case None => copy(empties = this.empties+1)
    }

    override def toString: String =
      s"counts :: non-null: $values | null: $empties"

  }

  object Counts {
    // Unit/Zero constructor
    val empty: Counts = Counts(0, 0)
  }

  /**
    * Values is meant to contain the counts and statistics associated with a Column.
    *
    * Row values contain an `append` contract to create a new
    * Values object with the appropriate stats appended to it
    */
  trait Values {
    val counts: Counts

    def append(option: Option[String]): Values
  }

  object Values {
    /**
      * Unit/Initializer for any type of Values, given a Column return a
      * unit Values object
      */
    def empty(c: Column): Values = c match {
      case t: Text => Texts(Counts.empty, TextStats.empty)
      case t: Number => Numbers(Counts.empty, NumberStats.empty)
    }
  }

  /**
    * Numbers is to be paired with a Column type of Number
    */
  case class Numbers(counts: Counts, stats: NumberStats) extends Values {

    def append(option: Option[String]): Numbers = option match {
      case None => this.copy(counts = this.counts(option))
      case Some(v) => Numbers(counts(option), stats.append(v.toDouble))
    }

    override def toString: String =
      s"  $counts\n  $stats"
  }

  /**
    * Texts is to be paired with a Column type of Text
    */
  case class Texts(counts: Counts, stats: TextStats) extends Values {
    def append(option: Option[String]): Texts = option match {
      case None => this.copy(counts = this.counts(option))
      case Some(v) => Texts(counts(option), stats.append(v))
    }
    override def toString: String =
      s"  $counts\n  $stats"
  }


  /**
    * Stats break down Text vs Number stats, allowing for a generic `append` function to add stats
    * along with an average function
    */
  trait Stats[A] {
    val sum: Double
    def avg(count: Int): Double =
      sum / count
    def append(a: A): Stats[A]
  }

  /**
    * Datastructure to keep track of Number stats
    * shortest and longest are Option[Double] since every value at the col,row could be null
    */
  case class NumberStats(min: Option[Double], max: Option[Double], sum: Double) extends Stats[Double] {
    def append(d: Double): NumberStats =
      NumberStats(Some(Math.min(min getOrElse d, d)), Some(Math.max(max getOrElse d, d)), sum+d)

    override def toString: String = {
      s"stats :: shortest: $min | longest: $max | sum: $sum"
    }
  }
  object NumberStats {
    val empty: NumberStats = NumberStats(None, None, 0.0)
  }

  /**
    * Datastructure to keep track of Text stats
    * shortest and longest are Option[String] since every value at the col,row could be null
    */
  case class TextStats(shortest: Option[String], longest: Option[String], sum: Double) extends Stats[String] {

    def append(str: String): TextStats =
      TextStats(Some(min(str)), Some(max(str)), sum + str.length)

    private def min(str: String): String = shortest match {
      case None => str
      case Some(s) => (str.length, s.length) match {
        case (a, b) if a < b => str
        case (a, b) if a > b => s
        case _ => if (str < s) str else s
      }
    }

    private def max(str: String): String = longest match {
      case None => str
      case Some(s) => (str.length, s.length) match {
        case (a, b) if a > b => str
        case (a, b) if a < b => s
        case _ => if (str < s) str else s
      }
    }

    override def toString: String = {
      s"stats :: shortest: $shortest | longest: $longest | sum: $sum"
    }
  }
  object TextStats {
    val empty: TextStats = TextStats(None, None, 0.0)
  }

}
