(: test update parsing :)
import module namespace xp="expkg-zone58:text.parse";
declare variable $opts:=map{
  "lang":"xquery",
"version":"3.1 basex",
"flatten": true()
};
let $xq:= "" || file:read-text("C:\Users\andy\git\ex-xparse\src\test\resources\xquery\basex\update2.xq")

let $ar:=``[
 ()
]``
(: return xp:version($opts) :)
return  xp:parse($xq,$opts) 