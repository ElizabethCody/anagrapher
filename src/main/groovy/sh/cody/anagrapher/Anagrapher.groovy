package sh.cody.anagrapher

import groovy.cli.picocli.CliBuilder

class Anagrapher {
   static void main(String... args) {
      def cli = new CliBuilder(name: 'anagrapher', header: 'Construct and solve anagrams from a graph.')
      cli.h(longOpt: 'help', 'show this message')
      cli.d(argName: 'depth', type: int, longOpt: 'depth', required: true, defaultValue: '4', args: 1, 'set graph traversal depth')
      cli.i(argName: 'true/false', type: boolean, longOpt: 'insensitivity', required: true, defaultValue: 'true', args: 1, 'set case insensitivity')
      cli.t(argName: 'threads', type: int, longOpt: 'threads', required: false, args: 1, 'force worker thread count')

      def options = cli.parse(args)

      if(options.h) {
         cli.usage()
      }
   }
}
