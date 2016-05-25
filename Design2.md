# Retro on version 1

## Funky apis

I wasn't happy with the underlying datastructures for stats having Option[_] in
their constructors. This smells like I approached the problem from a wrong
angle. I was hoping to encode the idea that there might not be a min if all
values at that index in the row are null.

## A more generic approach

### Shared structure

There is some shared structure between Text stats and Number stats that seems
to be asking to be explored - namely that `min ~= shortest` and `max ~=
longest` for Number and Text respectively. So, while I utilized an `append`
method that was parametric to the underlying `Stat` type, it appears that I can
further share structure to remove some boilerplate.

### Type coersion

I played with this idea in my head before starting out: given an Option[String]
and a Column, we should be able to determine which function to apply to get the
value (if there is one). Perhaps I should be transforming my stream of
`(Column, Option[String])` into flatter data types to make the api nicer.

# What I'm playing with now

Before I move to generalizing the data into either a flatter or more type-safe
implementation via type classes, I want to dig into the PULL model. I should be
able to share almost all of the infrastructure and only change the way I create
the source of tuples.
