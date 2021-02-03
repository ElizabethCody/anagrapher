package sh.cody.anagrapher

import groovy.cli.picocli.CliBuilder

final class Anagrapher {
   private final int depth, threads
   private final boolean insensitive
   private final InputStream graphStream, wordStream

   Anagrapher(int depth, int threads, boolean insensitive, InputStream graphStream, InputStream wordStream) {
      this.depth = depth
      this.threads = threads
      this.insensitive = insensitive
      this.graphStream = graphStream
      this.wordStream = wordStream
   }

   Map<Path, Set<String>> getSolutions(PrintStream log) {
      log?.println('Graphing...')
      def grapher = new Grapher().addNodesFromJson(graphStream)
      def graph = grapher.graph
      def stats = grapher.nodeStats
      log?.println("Graph has ${graph.nodes().size()} connected node(s).")

      log?.println("Finding path(s) of length $depth...")
      def pather = new Pather(graph)
      def paths = pather.findPaths(depth)
      log?.println("Found ${paths.size()} unique path(s).")

      log?.println('Finding words...')
      def wordsBuilder = new Words(stats.min * depth, stats.max * depth, insensitive, stats.charset)
      def words = wordsBuilder.getEligibleWords(wordStream)
      log?.println("Found ${words.size()} eligible word(s).")

      log?.println("Dispatching $threads anagram solver(s)...")
      def dispatcher = new AnagrammerDispatcher(words, paths, insensitive)
      def solutions = dispatcher.dispatchAnagrammers(threads)
      log?.println('Anagram solving complete.')

      solutions
   }

   private static InputStream openInputFile(File file, String resPrefix) {
      def str = file as String
      if(str[0] == ':') {
         Thread.currentThread().contextClassLoader.getResourceAsStream("$resPrefix/${str[1..-1]}")
      } else {
         new FileInputStream(file)
      }
   }

   static void main(String... args) {
      def timeStart = System.currentTimeMillis()

      def cli = new CliBuilder(name: 'anagrapher', header: 'Construct and solve anagrams from a graph.')
      cli.h(longOpt: 'help', 'show this message and exit')
      cli.d(argName: 'depth', type: int, longOpt: 'depth', required: true, defaultValue: '4', args: 1, 'set graph traversal depth')
      cli.i(argName: 'true/false', type: boolean, longOpt: 'insensitivity', required: true, defaultValue: 'true', args: 1, 'set case insensitivity')
      cli.t(argName: 'threads', type: int, longOpt: 'threads', required: false, args: 1, 'force worker thread count')
      cli.w(argName: 'word list', type: File, longOpt: 'word-list', required: true, defaultValue: ':english.txt', args: 1, 'path to valid word list')
      cli.g(argName: 'graph', type: File, longOpt: 'graph', required: true, defaultValue: ':usa.json', args: 1, 'path to graph file')
      cli.o(argName: 'out', type: File, longOpt: 'output', required: true, defaultValue: 'solutions.txt', args: 1, 'path to output file')

      def options = cli.parse(args)

      if(options.h) {
         cli.usage()
         return
      }

      def anagrapher = new Anagrapher(options.d, options.t ?: Runtime.getRuntime().availableProcessors(), options.i,
            openInputFile(options.g, 'graphs'), openInputFile(options.w, 'words')
      )

      System.err.println('Anagrapher 1.0.0-devel by Maxwell Cody <maxwell@cody.sh>')
      System.err.println("Desired threads: $anagrapher.threads; Case-insensitivity: $anagrapher.insensitive")
      System.err.println()

      try {
         def solutions = anagrapher.getSolutions(System.err)

         System.err.println("Found ${solutions.size()} path(s) with at least one valid solution.")
         System.err.println("Saving solution(s) to $options.o...")

         // overwrite existing file
         if(options.o.exists()) {
            options.o.delete()
         }

         def outstream = options.o.newPrintWriter()

         for(solution in solutions) {
            outstream.println("$solution.key -> ${solution.value.join(', ')}")
         }

         outstream.close()

         System.err.println("Solution(s) saved to $options.o.")
         System.err.println("Anagrapher has finished in ${(System.currentTimeMillis() - timeStart) / 1000.0} second(s).")
      } catch(exception) {
         exception.printStackTrace()
         System.exit(1)
      }
   }
}
