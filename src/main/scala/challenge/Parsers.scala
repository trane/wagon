package challenge

object Parsers {

  /**
    * Non-production-ready parser that makes some assumptions about the input header:
    * - always be of form: "name (type)","name (type)"
    * - name and type do *not* contain " or , or space characters
    *
    * It will fail silently if those assumptions are not meant, 'cause that seems reasonable for this challenge exercise
    *
    */
  def headerParser(header: String): Option[List[Column]] = {
    val cols = header.replaceAll("\"", "")
        .split(",")
        .toList
    sequence(cols.foldRight(List.empty[Option[Column]])((h, hs) => parseColumn(h) :: hs))
  }

  /**
    * Given the string: ":name (:type)", it will give some Column or None
    */
  def parseColumn(col: String): Option[Column] =
    col.split(" ").toList match {
      case name :: typ :: Nil => typ match {
        case "(text)" => Some(Text(name))
        case "(number)" => Some(Number(name))
        case _ => None
      }
      case _ => None
    }


  def rowStreamParser(cols: List[Column], rows: Iterator[String]): Iterator[(Column, Option[String])] =
    rows.flatMap(row => rowParser(cols, row).toIterator)

  /**
    * Given expected column types, and row values, it will return a new list of those zipped
    */
  def rowParser(cols: List[Column], row: String): List[(Column, Option[String])] =
    cols zip (rowParser(row))

  /**
    * Transform a csv line into a list
    */
  def rowParser(row: String): List[Option[String]] =
    row.split(",", -1).toList map (columnValue)

  /**
    * Convert empty strings to None, otherwise Some(...)
    */
  def columnValue(str: String): Option[String] =
    if (str.isEmpty) None
    else Some(str)

}
