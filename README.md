# Beyond gOOD

A simple spreadsheet editor.

## Getting Started

### Prerequisites

Java version between 8 and 19.

### Installing and running

Download the repository and unzip it.

Build the project

```
./gradlew build
```

Run the project

Arguments:
* -edit or -gui
  * run a gui in edit mode or read only mode
* -in *filename*      
  * file to open (*.txt* or *.gOOD*)

```
./gradlew run --args="-edit"
```

## Running tests

Tests will run and get logged to the console and are ran when when building. You can also run the tests only.

```
./gradlew test
```

## Mechanics

Click on a cell, enter a value in the top formula bar. A value can be a number, boolean, string, reference, or function.

Refer to another cell:

```
=A1
```

Use a predefined function:

```
=(SUM A1 5 B2)
```

Use rectangular references with formulas:

```
=(PRODUCT A1:B3)
```

Defined errors:
* #CIRC! circular reference
* #REF! multiple cells referenced in a singular reference (e.g =A1:B3), multi-references can only be used by functions, not standalone
* #ARG! illegal argument type in function
* #NAME? undefined function
* #DIV/0! division by zero
* #NUM! invalid number in function

## View

* Header panel, which contains 2 text fields.
  * A text field which displays the current cell coordinate.
  * A text field which shows the "raw contents" (string formula) of the current cell.
* View action where entering a valid coordinate in the coordinate text field and pressing enter shifts view to that coordinate being the top left coordinate on the screen (does nothing if invalid).
* Infinite scrolling with custom scrollbar event listeners.
* Selecting a cell highlights it and the coordinate row and column.

## Built With

* [Amazon Coretto 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html) - distribution of OpenJDK used
* [Gradle 8](https://gradle.org/) - build automation tool
* [JUnit 4](https://junit.org/junit4/) - testing framework

## Authors

* **Sina Soltanieh**
* **Nithin Chintala**