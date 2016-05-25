package object challenge {

  /**
    * Named columns are defined as either: text or number.
    */
  sealed trait Column
  case class Text(name: String) extends Column
  case class Number(name: String) extends Column

  /**
    * For some reason the standard collection library doesn't have `sequence`,
    * so this is ad-hoc and probably terribly inefficient.
    *
    * Also, it is specialized to `List` and `Option`
    */
  def sequence[A](l: List[Option[A]]): Option[List[A]] =
    l.foldRight(Option(List.empty[A]))((op, b) => {
      op.flatMap(a => b.map(as => a :: as)) orElse (None)
    })

}
