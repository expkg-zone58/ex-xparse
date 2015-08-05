(:~
: xparse module
: @see https://lists.w3.org/Archives/Public/public-expath/2015Feb/att-0003/xparse.html
:)
module namespace xp="http://expath.org/ns/xparse";
import module namespace p="xquery-30" at "xquery-30.xqm";


(:~ 
: @return parse tree
:)
declare function xp:parse($exp as xs:string) (: as element() :)
{
 xp:parse($exp,map{})
};

(:~ 
: @return parse tree
:)
declare function xp:parse($exp as xs:string, $options as map(*))  (: as element() :)
{
p:parse-XQuery($exp)
};


declare function xp:to-expression($in as element()) as xs:string
{
 xp:to-expression($in,map{})
};


declare function xp:to-expression($in as element(), $options as map(*)) as xs:string
{
 fn:string($in)
};