package sh.cody.anagrapher

import groovy.cli.picocli.CliBuilder

import java.util.concurrent.CountDownLatch

final class Anagrapher {
   static void main(String... args) {
      def cli = new CliBuilder(name: 'anagrapher', header: 'Construct and solve anagrams from a graph.')
      cli.h(longOpt: 'help', 'show this message and exit')
      cli.d(argName: 'depth', type: int, longOpt: 'depth', required: true, defaultValue: '4', args: 1, 'set graph traversal depth')
      cli.i(argName: 'true/false', type: boolean, longOpt: 'insensitivity', required: true, defaultValue: 'true', args: 1, 'set case insensitivity')
      cli.t(argName: 'threads', type: int, longOpt: 'threads', required: false, args: 1, 'force worker thread count')
      cli.w(argName: 'word list', type: File, longOpt: 'word-list', required: true, defaultValue: ':english.txt', args: 1, 'path to valid word list')
      cli.g(argName: 'graph', type: File, longOpt: 'graph', required: true, defaultValue: ':usa.json', args: 1, 'path to graph file')

      def options = cli.parse(args)

      if(options.h) {
         cli.usage()
         return
      }

      def threads = options.t ?: Runtime.getRuntime().availableProcessors()
      def graphSource = options.g
      def graphPath = graphSource as String

      if(graphPath[0] == ':') {
         graphSource = Thread.currentThread().contextClassLoader.getResourceAsStream("graphs/${graphPath[1..-1]}")
      }

      System.err.println('Anagrapher 1.0.0-devel by Maxwell Cody <maxwell@cody.sh>')
      System.err.println("Desired threads: ${threads}; Insensitivity: ${options.i}")
      System.err.println()

      long startTime = System.currentTimeMillis()

      System.err.println("Graphing $graphPath...")
      def grapher = new Grapher()
      grapher.addNodesFromJson(graphSource)
      def graph = grapher.graph
      System.err.println("The graph has ${graph.nodes().size()} connected nodes. (Min: $grapher.min; Max: $grapher.max)")

      System.err.println("Finding paths of length ${options.d}...")
      def pather = new Pather(graph)
      def paths = pather.findPaths(options.d) as List<Path>
      System.err.println("Found ${paths.size()} unique paths.")

      def wordSource = options.w
      def wordPath = wordSource as String

      if(wordPath[0] == ':') {
         wordSource = Thread.currentThread().contextClassLoader.getResourceAsStream("words/${wordPath[1..-1]}")
      } else {
         wordSource = new FileInputStream(wordSource)
      }

      System.err.println("Filtering word list ${wordPath}...")
      Set<String> words = [] as Set
      def charset = grapher.charset

      try(wordSource) {
         def reader = new BufferedReader(new InputStreamReader(wordSource))
         String buffer = null
outer:   while((buffer = reader.readLine()) != null) {
            if(buffer.length() <= grapher.max * options.d && buffer.length() >= grapher.min * options.d) {
               for(ch in buffer) {
                  if(options.i) {
                     if(!charset.contains(ch) && !charset.contains(ch.toUpperCase()) && !charset.contains(ch.toLowerCase())) {
                        continue outer
                     }
                  } else {
                     if(!charset.contains(ch)) {
                        continue outer
                     }
                  }
               }

               if(options.i) {
                  words << buffer.toUpperCase()
               } else {
                  words << buffer
               }
            }
         }
      }
      System.err.println("${words.size()} eligible words found.")

      List<Path>[] pathss = new List[threads]
      while(paths.size() > 0) {
         for(int i = 0; i < threads; ++i) {
            if(paths.size() == 0) break
            pathss[i] ?= []
            pathss[i].add(paths.pop())
         }
      }

      System.err.println('Starting anagram solvers...')
      def latch = new CountDownLatch(threads)
      def anagrammers = [] as List<Anagrammer>
      for(pathList in pathss) {
         def anagrammer = new Anagrammer(pathList, words, latch, options.i)
         anagrammers << anagrammer
         new Thread(anagrammer).start()
      }
      latch.await()
      System.err.println('Anagram solving finished.')
      System.err.flush()
      for(anagrammer in anagrammers) {
         for(entry in anagrammer.solutions.entrySet()) {
            println "$entry.key -> ${entry.value.join(', ')}"
         }
      }
      System.out.flush()


      System.err.println()
      System.err.println("Anagrapher has finished in ${(System.currentTimeMillis() - startTime) / 1000.0} seconds.")
   }
}
