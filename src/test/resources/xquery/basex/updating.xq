(: http://docs.basex.org/wiki/XQuery_Update#User-Defined_Functions :)

let $node := <node>TO-BE-DELETED</node>
let $delete-text := %updating function($node) {
  delete node $node//text()
}
return $node update (
  invoke updating $delete-text(.)
)