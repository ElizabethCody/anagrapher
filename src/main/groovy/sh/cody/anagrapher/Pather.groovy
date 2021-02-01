package sh.cody.anagrapher

import com.google.common.graph.Graph

final class Pather {
   private final Graph<String> graph

   Pather(Graph<String> graph) {
      this.graph = graph
   }

   Set<Path> findPaths(int depth) {
      Set<Path> fullPaths = [] as Set
      Stack<String> path = [] as Stack

      for(node in graph.nodes()) {
         buildPaths(depth, [node] as Stack, fullPaths)
      }

      fullPaths
   }

   private void buildPaths(int depth, Stack<String> path, Set<Path> fullPaths) {
      if(path.size() == depth) {
         fullPaths << new Path(path as String[])
      } else {
         for(node in graph.adjacentNodes(path.peek())) {
            if(node !in path) {
               path << node

               buildPaths(depth, path, fullPaths)

               path.pop()
            }
         }
      }
   }

}
