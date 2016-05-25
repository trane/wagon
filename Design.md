This is meant to document my thought process as I work through the problem.

## Columns

Since we have two column types, we can describe this as the types:

```scala
trait Column {
  val name: String
}
case class Text(name: String) extends Column
case class Number(name: String) extends Column
```

If we wanted to embed the computations that go along with these values, we
could add things like `min`, `max`, `avg`, to the `Number` data type. But, I'm
thinking I would prefer to use these as simple types that can be packages along
with their context, i.e. their value.

I'm thinking that we can essentially derive a log data-structure as an artifact
of traversing the stream that will append the context of these calculations
done so far (sort of like the implicit log you get via successive application
of applicatives). Not sure I'll get there, but it is certainly on my mind.

The only weirdness there is that we don't know the structure of the rows
before-hand, which suggests that we need a monadic bind to work with. Maybe,
I'll get around that by defining a row as a list of columns, which can then
probably get us back to the domain of applicatives.

One other thing to note is that `number`, on inspection, should probably
represented as a `Double` - even though `latency (number)` in the generator
gives integer values.

## Rows

If we had knowledge of the types (and order of types) each of the columns are
before runtime, we would be able to define an applicative interface to the row
values. However, since we are dependent on the outcome of the header to let us
know what the types of the values in each row, we'll have to move to a monadic
interface so that we can build the row parser accordingly.

Alternatively, we might be able to get away with simply keeping a one-to-one
mapping of indexed header to indexed row value, where given the tuple of
`(Column, Option[String])` we *do* know the computations to perform based on
the type of the `Column` and the value of the `Option[String]`.

Let's define a row as follows:

```
row :: List[Column] -> List[Option[String]] -> List[(Column, Option[String])]
```

The rows can be treated as a few transformations:

Please excuse my pseudo Scala/Haskell syntax...

```
-- this might not be needed, because this transformation probably happens while
-- manipulating the stream
break_up_lines :: String -> List[Option[String]]

-- Some row values will be null, might as well use algebra to define that
column_value :: String -> Option[String]

-- Determine what internal repr we would use for the Column type
column_type :: Column -> A

-- Change a string into the type we need to do computations on it
coerse :: String -> (String -> A) -> A
```

The functions for computing different values based on these column/row
transformations are the place to have some fun and be creative!

So far we have two structures to inform our computations:
`List[Column]` and `Stream[List[Option[String]]]`, where the `List[Column]` and
`List[Option[String]]` can be composed to give the context for values and
transformations of those values.
`List[Column] zip List[Option[String]] -> List[(Column, Option[String])]`

We probably won't need to actually zip these structures together, though that
might be useful from an understandability p.o.v..

## Computations

Given a stream of tuples of `(Column, Option[String])` we can calculate some
of the statistics without keeping much state around on the stream of data: max,
min, counts of both `Number` and `Text`. Also the null and non-null counts are
possible for every column type. Average will be a problem as we grow the
number of rows, since average is the sum of all values divided by the count of
values, we can run into overflow problems on the sum.

It will probably be helpful to use some data structures to help with the
terrible tuple syntax in Scala (readability).
