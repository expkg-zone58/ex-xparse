(:~
 : ex-dotml tests
 :)

import module namespace p="XQuery" at "XQuery.xquery";
declare variable $file:="resources\xquery\basex\updating.xq";

let $xq:= unparsed-text($file) || " "

return p:parse-XQuery("2+3")