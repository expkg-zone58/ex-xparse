(:~
 : build utils 
 : @author Andy Bunce
 : @copyright Quodatum Ltd
 : @licence Apache 2
 : @since may-2015
 :)
module namespace build = 'quodatum.utils.build';
declare default function namespace 'quodatum.utils.build'; 
declare namespace pkg="http://expath.org/ns/pkg";
(:~
 : file paths below $src
 : $src typically from resolve-uri
 : @return sequences of relative file paths "content/ebnf/CR-xquery-31-20141218.ebnf" "..."
 :)
 declare function files($src as xs:string) as xs:string*
 {
   fn:filter(file:list($src,fn:true()),
          function ($f){($src || $f)=>fn:translate("\","/")=>file:is-file()}
        )
          !fn:translate(.,"\","/") 
 };

(:~ 
 : write xqdoc for $src/$path to $dest
 :)
declare %updating  function write-xqdoc($path,$src,$dest){
  let $url:=fn:resolve-uri( $path,$src)=>fn:trace()
  let $type:=fetch:content-type($url)
 
  return  switch($type)
    case "application/xquery"
      return file:write(
          fn:resolve-uri($path || ".xml",$dest),
           inspect:xqdoc($url)
         )
    default 
      return ()

};
(:~ 
: name of dist xar file eg "fred-0.1.0.xar"
:)
declare function xar-name($package as element(pkg:package)) as xs:string
{
fn:concat($package/@abbrev , "-" ,$package/@version, ".xar")
};

(:~ 
: update package.xml located at $cxan to ensure has entry for package $pkg
:)
declare %updating function publish($pkg as element(pkg:package),$cxan)
{
let $doc:=copy  $c:=fn:doc($cxan)
          modify(
          let $pack:=$c/repo/pkg[name=$pkg/@name]
          let $hit:= $pack/version[@num=$pkg/@version]
          let $new:=<version num="{$pkg/@version}">
                    <!-- generated: {fn:current-dateTime()} -->
                    </version>
          return if($hit)then () 
                 else insert node $new into $pack
               )
          return $c
return fn:put($doc,$cxan)
};