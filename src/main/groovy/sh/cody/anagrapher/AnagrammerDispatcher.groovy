package sh.cody.anagrapher

import java.util.concurrent.CountDownLatch

final class AnagrammerDispatcher {
   private final Set<String> words
   private final Set<Path> paths
   private final boolean insensitive

   AnagrammerDispatcher(Set<String> words, Set<Path> paths, boolean insensitive) {
      this.words = words
      this.paths = paths
      this.insensitive = insensitive
   }

   Map<Path, Set<String>> dispatchAnagrammers(int threads) {
      def allottedPaths = new Set<Path>[threads]
      def remainingPaths = new ArrayList(this.paths)

      for(int i = 0; remainingPaths.size() > 0; i = (i + 1) % threads) {
         allottedPaths[i] ?= [] as Set<Path>
         allottedPaths[i] << remainingPaths.pop()
      }

      def latch = new CountDownLatch(threads)
      def anagrammers = new Anagrammer[threads]

      for(i in 0..<threads) {
         anagrammers[i] = new Anagrammer(allottedPaths[i], words, insensitive, { latch.countDown() })
         new Thread(anagrammers[i]).start()
      }

      latch.await()

      Map<Path, Set<String>> solutions = [:]

      for(anagrammer in anagrammers) {
         for(pathSolutions in anagrammer.solutions) {
            solutions[pathSolutions.key] ?= [] as Set<String>
            solutions[pathSolutions.key].addAll(pathSolutions.value)
         }
      }

      solutions
   }
}
