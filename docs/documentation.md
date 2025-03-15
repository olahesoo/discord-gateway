
# Table of Contents

1.  [Architecture](#org9884d41)
    1.  [State management](#org4c5cfcf)
    2.  [Concepts](#orgdf05378)
        1.  [Tasks](#org37064b9)
        2.  [Parsers](#orgfee1f45)
        3.  [Matchers](#org9001f67)
        4.  [Handlers](#org5605fc6)
        5.  [Dispatchers](#orge8f6bcd)


<a id="org9884d41"></a>

# Architecture


<a id="org4c5cfcf"></a>

## State management

There is a single variable in the socket controller that holds mutable
state. State is modified using tasks, functions which take a state as
input and return a new state. Immutable state is passed around using
function closures.


<a id="orgdf05378"></a>

## Concepts


<a id="org37064b9"></a>

### Tasks

Tasks are functions that take state as input and return a new
state. Tasks are the only functions that may perform side effects.


<a id="orgfee1f45"></a>

### Parsers

Parsers are pure functions that take an event as input and return a
task.


<a id="org9001f67"></a>

### Matchers

Matchers are pure functions that take an event as input and return a
boolean.


<a id="org5605fc6"></a>

### Handlers

A handler is a combination of a matcher and a parser.


<a id="orge8f6bcd"></a>

### Dispatchers

A dispatcher is a function that takes an event, a sequence of handlers
and an optional default parser as input. The dispatcher collects
handlers that match the event and combines their parsers with the
event, outputting a single task. The dispatcher uses its default
parser if no handlers match the event.

