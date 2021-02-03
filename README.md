# Anagrapher
*Construct and solve anagrams from a graph.* 

This project was initially created to aid in the solution of
[this puzzle](https://www.npr.org/2021/01/31/962412357/sunday-puzzle-game-of-words) from NPR's Weekend Edition.

While this project *should* serve to solve any problem of this class (given enough compute time), the command-line
interface will default to the NPR puzzle. See [Usage](#Usage) to solve other puzzles.

## Usage

```
Usage: anagrapher [-h] [-d=<depth>] [-g=<graph>] [-i=<true/false>] [-o=<out>]
                  [-t=<threads>] [-w=<word list>]
Construct and solve anagrams from a graph.
  -d, --depth=<depth>               set graph traversal depth
  -g, --graph=<graph>               path to graph file
  -h, --help                        show this message and exit
  -i, --insensitivity=<true/false>  set case insensitivity
  -o, --output=<out>                path to output file
  -t, --threads=<threads>           force worker thread count
  -w, --word-list=<word list>       path to valid word list
```

If no command-line parameters are supplied, Anagrapher will default to solving NPR's four-state postal abbreviation
challenge.

## Build
For building, the `shadowJar` task in Gradle is recommended.

Linux, BSD, macOS, etc:
```
$ ./gradlew shadowJar
```

Windows and DOS:
```
> gradlew.bat shadowJar
```

## Copyright
Anagrapher is protected by copyright and is licensed under the MIT License, see `LICENSE.txt` for more details.

Copyright &copy; 2021 - Maxwell Cody [<maxwell&commat;cody&period;sh>](mailto:maxwell&commat;cody&period;sh)