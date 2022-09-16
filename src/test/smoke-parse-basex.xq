(: test update parsing :)
import module namespace xp="expkg-zone58:text.parse";
declare variable $opts:=map{
  "lang":"xquery",
"version":"3.1 basex",
"flatten": true()
};
let $xq:=  file:read-text(resolve-uri("resources/xquery/basex/updating.xq")) || " "


(: return xp:version($opts) :)
return  xp:parse($xq =>trace("src:"),$opts) 