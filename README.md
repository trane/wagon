# Notes

This was a lot of fun.

## Some fun things I learned

  - It turns out that 64-bit Doubles allow for *many* *many* *many* rows before
    they overflow. I was able to do 100,000,000 rows without an overflow
  - Using immutable datastructures really didn't cause any noticeable GC
    cycles, it turns out that the JVM has an O(1) algorithm for GC of "eden"
    phase objects. Since almost all of the objects are created and then
    destroyed it had 0 impact for the pause-the-world gc. AWESOME!
  - Turns out scala's collections don't have `sequence`, but it was
    straight-forward to add in the package object
  - Running long-running processes that interface with stdin and stdout are not
    as straight-forward in java/scala as I would have thought. But, in the end
    it wasn't much code to make it work

## Things that would be fun to work towards (with more time)

  - Build with parser combinators for a more functional, type-safe approach
  - Explore that specific Stats are parametric on the Column type, along with
    parser combinators we could have a nice type-class approach to both value
    coersion and computations.
  - The computations are easily convergent (simple adding), parallelizing the
    computations could speed things up

## Runtime performance visuals

### Pull Mode

![10,000,000](pull_mode_10000000.png =640x480)

### Push Mode

![10,000,000](push_mode_10000000.png =640x480)

### 100,000,000 rows in Pull mode

![100,000,000](pull_mode_100000000.png =640x480)

# Design

I went through a few iterations of work on this project, which are detailed in
Design.md and Design2.md.

# Usage

This is an sbt 0.13.8+ project and example usage can be found by running the
following command from the root of this directory:

```
$ sbt "run --help"
Usage: <command> [options] where options would be:
 --pull <num>    <num> is number of records to pull
 --path <path>   <path> is the path to the generator
 --help          Print this usage

 If no options are given, it will assume it is taking input from
 STDIN.

 Example Usage:
   # run this program using stdin
   ./generator 10000 | <command>

   # run this with the generator in PULL mode
   <command> --pull 1000000 --path "./generator"
```

From my laptop I run it the following ways:

```
# pull mode
$ sbt "run --pull 100000 --path /Users/akuhnhausen/workspace/wagon/generator

# pipe mode
$ ./generator 100000 | sbt run
```

# Problem 1: Streaming statistics computation

Wagon computes histograms, distributions, and other statistics on streaming query results. Given a stream of numbers and text, let’s calculate some stats!

Input

We’ve built a generator that outputs random web log data in CSV format. You can download it here:

Mac: https://s3.amazonaws.com/challenge.wagon/generator.zip

Linux: https://s3.amazonaws.com/challenge.wagon/generator-linux.zip

The CSV header includes column names and column types, like this:

`"col1name (col1type)","col2name (col2type)",...`

The generator takes in a number of rows you’d like returned as a command line argument:

`./generator 10000`

Consume and compute

Build an executable that consumes ~10000 rows from the generator and calculates the following basic statistics:

* For all columns, compute:
	* count
	* null count

* For number columns, compute:
  - minimum
	- maximum
	- average

* For text columns, compute:
	* count(shortest value)
	* count(longest value)
	* average length
	* break ties alphabetically as needed
