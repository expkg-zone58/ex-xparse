xquery version "3.1" encoding "UTF-8";
(:~
: This module is designed to specify a small library of functions for parsing expressions in a number of 
: languages  into XML trees, and in the reverse direction serialising these trees into equivalent string expressions.
: Parsers are available for versions for XPath and XQuery.  
: @see https://lists.w3.org/Archives/Public/public-expath/2015Feb/att-0003/xparse.html
:)

module namespace xp="expkg-zone58:text.parse"; 


(:~ 
 : XML descriptions of the available parsers.
:)
declare variable $xp:parsers as element(xp:parser)*:=doc("parsers.xml")/xp:parsers/xp:parser;

(:~ 
 : default options for parse function.
:)
declare variable $xp:default-opts:=map{
    "lang":"xpath",
    "version":"",
    "flatten":true()
};
(:~
 :The xp:parse function returns a parse tree for an expression. 
: @return parse tree
:)
declare function xp:parse($exp as xs:string)  as element() 
{
 xp:parse($exp,map{})
};

(:~
:The xp:parse function returns a parse tree for an expression.
: @param $exp sttring to parse
: @param $options is a map of control options
: @return parse tree
:)
declare function xp:parse($exp as xs:string, $options as map(*))   as element() 
{
(: map:merge spec change force error pre basex 8.6:)
    let $opts:=map:merge(($xp:default-opts,$options),map{"duplicates":"use-last"})
    let $parser:=xp:get-parser($opts)
    let $r:= $parser($exp)
    return if($opts?flatten)then xp:flatten($r) else $r
};

(:~
 : The xp:to-expression function returns an expression tree corresponding to a given parse tree.
 :)
declare function xp:to-expression($in as element()) as xs:string
{
 xp:to-expression($in,map{})
};

(:~
 : The xp:to-expression function returns an expression tree corresponding to a given parse tree.
 :)
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
 : Get the parser function to be used for the supplied options
 : @return parser function 
 :)
declare function xp:version($opts as map(*))
as map(*){
  let $version:=$xp:parsers[@lang=lower-case($opts?lang)]/xp:version[starts-with(@version,$opts?version)]=>head()
  return map{"ebnf":$version/@ebnf/xp:java-fixup(.),
             "sym":$version/@sym/string()}
};

declare %private function xp:java-fixup($name as xs:string) 
as xs:string{
translate($name,"-","_")
};

(:~
 : Select parser function for opts
 : @return parser function as function($xq as xs:string)
 :)
declare function xp:get-parser($opts as map(*)) 
as function(*){
  let $p:= xp:version($opts)
  let $code:=``[function($src){Q{java:expkg_zone58.ex_xparse.Parse_`{$p?ebnf}`}parse`{$p?sym}`($src)}]``
  return  xquery:eval($code)
};

