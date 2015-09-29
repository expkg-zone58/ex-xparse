xquery version "3.1" encoding "UTF-8";
(:~
: xparse module  
: @see https://lists.w3.org/Archives/Public/public-expath/2015Feb/att-0003/xparse.html
: uses basex inspect:functions
:)
module namespace xp="http://expath.org/ns/xparse"; 


(:~ 
 :available parsers
:)
declare variable $xp:parsers as element(xp:parser)*:=(

<xp:parser lang="xpath" version="3.0" parser="xpath-30.xqm" namespace="parser" fn="p:parse-XPath"/>,
<xp:parser lang="xquery" version="3.0" parser="xquery-30.xqm"  namespace="parser" fn="p:parse-XQuery"/>,
<xp:parser lang="xquery" version="3.1 cr-20141218" parser="CR-xquery-31-20141218.xqm"  namespace="CR-xquery-31-20141218" fn="p:parse-XQuery"/>,
<xp:parser lang="xquery-update" version="3.0 wd-20150219" parser="WD-xquery-update-30-20150219.xqm"  namespace="p:parser" fn="parse-XQuery"/>

);

declare variable $xp:default-opts:=map{
    "lang":"xpath",
    "version":"3.0",
    "flatten":true()
};
(:~ 
: @return parse tree
:)
declare function xp:parse($exp as xs:string)  as element() 
{
 xp:parse($exp,map{})
};

(:~ 
: @return parse tree
:)
declare function xp:parse($exp as xs:string, $options as map(*))   as element() 
{
    let $opts:=map:merge(($xp:default-opts,$options))
    let $parser:=xp:get-parser($opts)
    let $r:= $parser($exp)
    return if($opts?flatten)then xp:flatten($r) else $r
};


declare function xp:to-expression($in as element()) as xs:string
{
 xp:to-expression($in,map{})
};


declare function xp:to-expression($in as element(), $options as map(*)) as xs:string
{
 fn:string($in)
};

(:~ 
 :simplify by omitting elements with only one child
:)
declare function xp:flatten($input as element()) as element() {
  if (1=count($input/*)) then xp:flatten($input/*)
  else element {node-name($input) }
      {$input/@*,
       for $child in $input/node()
          return
             if ($child instance of element())
                then xp:flatten($child)
                else $child
      }
};

(:~
 : @return parser function 
 :)
declare function xp:get-parser($opts as map(*)) as function(*)
{
  let $p:=$xp:parsers[@lang=$opts?lang][starts-with(@version,$opts?version)]=>head()=>exactly-one()
  return xp:parse(?,$p/@namespace,"parsers/" || $p/@parser,$p/@fn)
};

(:
 : $src as xs:string,
 : $nms,
 : $loc,
 : $fn
 :)
declare function xp:parse($src as xs:string,$namespace as xs:string,$loc,$fn){
  let $code:='import module namespace p="%s" at "%s"; declare variable $src external;%s($src)'  
  return xquery:eval(out:format($code,$namespace,resolve-uri($loc),$fn),map{"src":$src})
};