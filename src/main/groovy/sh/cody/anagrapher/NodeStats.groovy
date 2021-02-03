package sh.cody.anagrapher

import groovy.transform.Immutable

@Immutable
final class NodeStats {
   final int min, max
   final Set<Character> charset
}
