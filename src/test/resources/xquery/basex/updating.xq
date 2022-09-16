(:~ 
@see  http://docs.basex.org/wiki/XQuery_Update#User-Defined_Functions 
@see https://www.mail-archive.com/basex-talk@mailman.uni-konstanz.de/msg04107.html
:)

let $node := <node>TO-BE-DELETED</node>
let $delete-text := %updating function($node) {
  delete node $node//text()
}
return $node update {
  invoke updating $delete-text(.)
}