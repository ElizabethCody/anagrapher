package sh.cody.anagrapher

final class Anagrammer implements Runnable {
   private final Set<Path> paths
   private final Set<String> words
   private final boolean insensitive
   private final Closure finished
   final Map<Path, Set<String>> solutions = [:]

   Anagrammer(Set<Path> paths, Set<String> words, boolean insensitive, Closure finished) {
      this.paths = paths
      this.words = words
      this.insensitive = insensitive
      this.finished = finished
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

      finished?.call(this)
   }
}
