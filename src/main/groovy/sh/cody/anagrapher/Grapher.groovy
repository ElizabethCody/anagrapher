package sh.cody.anagrapher

import com.google.common.graph.*
import groovy.json.JsonSlurper

final class Grapher {
   final MutableGraph<String> graph = GraphBuilder.undirected().build()

   // the min and max node length is used to optimize the word list
   private int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE

   Grapher addNodesFromJson(source) {
      def slurper = new JsonSlurper()
      def mgraph = slurper.parse(source)

      assert 'nodes' in mgraph: 'Must be a valid graph JSON.'

      for(node in mgraph['nodes']) {
         assert 'node' in node && 'neighbors' in node: 'Invalid node definition.'

         def nodeName = node['node']
         updateMinMax(nodeName)

         for(neighbor in node['neighbors']) {
            graph.putEdge(nodeName, neighbor)
            updateMinMax(neighbor)
         }
      }

      this
   }

   private void updateMinMax(String nodeVal) {
      def len = nodeVal.length()

      if(len < min) {
         min = len
      }

      if(len > max) {
         max = len
      }
   }

   long getMin() {
      min
   }

   long getMax() {
      max
   }
}
