(: test update parsing :)
import module namespace xp="expkg-zone58:text.parse";
declare variable $opts:=map{
  "lang":"xquery",
"version":"3.1 basex",
"flatten": true()
};
let $xq:= unparsed-text("resources/xquery/basex/updating2.xq") || " "


return  xp:parse($xq,$opts) 