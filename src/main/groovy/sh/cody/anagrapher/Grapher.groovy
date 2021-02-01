package sh.cody.anagrapher

import com.google.common.collect.ImmutableSet
import com.google.common.graph.*
import groovy.json.JsonSlurper

final class Grapher {
   private final MutableGraph<String> graph = GraphBuilder.undirected().build()

   // these values may be used to optimize the word list
   private int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE
   private final Set<Character> charset = [] as Set<Character>

   Grapher addNodesFromJson(source) {
      def slurper = new JsonSlurper()
      def mgraph = slurper.parse(source)

      assert 'nodes' in mgraph: 'Must be a valid graph JSON.'

      for(node in mgraph['nodes']) {
         assert 'node' in node: 'Invalid node definition.'

         def nodeName = node['node']
         updateStats(nodeName)

         for(neighbor in node['neighbors']) {
            graph.putEdge(nodeName, neighbor)
            updateStats(neighbor)
         }
      }

      this
   }

   private void updateStats(String nodeVal) {
      def len = nodeVal.length()

      if(len < min) {
         min = len
      }

      if(len > max) {
         max = len
      }

      for(ch in nodeVal) {
         charset << ch
      }
   }

   long getMin() {
      min
   }

   long getMax() {
      max
   }
   
   Set<Character> getCharset() {
      ImmutableSet.copyOf(charset)
   }

   Graph<String> getGraph() {
      ImmutableGraph.copyOf(graph)
   }
}
