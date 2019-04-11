(: test update parsing :)
import module namespace xp="expkg-zone58:text.parse";
declare variable $opts:=map{
  "lang":"xquery",
"version":"3.1 basex",
"flatten": true()
};
let $xq:= "" || file:read-text("C:\Users\andy\git\vue-poc\src\vue-poc\lib\history.xqm")
(: let $xq:=``[
  db:replace("!ASYNC",``[run/`{ $id }`.xml]``,$pipe),
        replace value of node $dbid with ($dbid +1)
]`` :)
let $ar:=``[
 ()
]``
(: return xp:version($opts) :)
return  xp:parse($xq,$opts) 