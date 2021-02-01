package sh.cody.anagrapher

final class Path {
   private final String[] path, pathInv
   private String string = null
   private Integer hashCode = null

   Path(String... nodes) {
      path = nodes
      pathInv = path.reverse()
   }

   @Override
   boolean equals(Object o) {
      if(o instanceof Path) {
         def other = o as Path
         Arrays.equals(path, other.path) || Arrays.equals(pathInv, other.path)
      } else {
         false
      }
   }

   @Override
   int hashCode() {
      if(hashCode == null) {
         hashCode = Arrays.hashCode(path) ^ Arrays.hashCode(pathInv)
      }

      hashCode
   }

   @Override
   String toString() {
      if(!string) {
         string = path.join(', ')
      }

      string
   }

   String compactString() {
      path.join('')
   }
}
