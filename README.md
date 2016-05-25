

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
