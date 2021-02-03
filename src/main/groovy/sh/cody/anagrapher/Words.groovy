package sh.cody.anagrapher

final class Words {
   private final int min, max
   private final Set<Character> charset
   private final boolean insensitive

   Words(int min, int max, boolean insensitive, Set<Character> charset) {
      this.min = min
      this.max = max
      this.insensitive = insensitive
      this.charset = charset
   }

   Set<String> getEligibleWords(InputStream source) {
      Set<String> words = []

      source.eachLine {
         if(it.length() >= min && it.length() <= max && isValidForCharset(it)) {
            words << (insensitive ? it.toUpperCase() : it)
         }
      }

      words
   }

   private boolean isValidForCharset(String str) {
      for(ch in str) {
         if(insensitive && !charset.contains(ch) && !charset.contains(ch.toUpperCase()) && !charset.contains(ch.toLowerCase())) {
            return false
         } else if(!insensitive && !charset.contains(ch)) {
            return false
         }
      }

      return true
   }
}
