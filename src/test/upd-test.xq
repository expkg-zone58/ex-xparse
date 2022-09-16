import module namespace p="XQuery" at "XQuery.xquery";

let $xq:='
let $f := fn:put#2
return invoke updating $f(<newnode/>,"newnode.xml")
'
return p:parse-XQuery($xq)