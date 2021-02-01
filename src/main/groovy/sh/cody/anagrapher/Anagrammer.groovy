package sh.cody.anagrapher

import java.util.concurrent.CountDownLatch

final class Anagrammer implements Runnable {
   private final List<Path> paths
   private final Set<String> words
   private final CountDownLatch latch
   private final boolean insensitive
   final Map<Path, Set<String>> solutions = [:]

   Anagrammer(List<Path> paths, Set<String> words, CountDownLatch latch, boolean insensitive) {
      this.paths = paths
      this.words = words
      this.latch = latch
      this.insensitive = insensitive
   }

   private void anagram(Path current, String left, String right) {
      if(right.length() == 0) {
         if(!solutions[current]?.contains(left) && words.contains(insensitive ? left.toUpperCase() : left)) {
            solutions[current] ?= [] as Set
            solutions[current] << left
         }
      } else {
         for(ch in right) {
            anagram(current, left + ch, right.replaceFirst(ch, ''))
         }
      }
   }

   @Override
   void run() {
      for(path in paths) {
         anagram(path, '', path.compactString())
      }

      latch.countDown()
   }
}
