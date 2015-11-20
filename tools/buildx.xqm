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
 :)
 declare function files($src as xs:string) as xs:string*
 {
   fn:filter(file:list($src,fn:true()),
          function ($f){file:is-file($src || $f)}
        )
          !fn:translate(.,"\","/") 
 };

(:~
 : read data with uri from items in $paths using $fetch
 : then save using $write
 :) 
declare function transform($paths  as xs:string*,
                           $fetch,
                           $write )
{
 for $path in $paths
 let $data:=$fetch($path)
 return $write($path,$data)
};

(:~
 : read data with uri from items in $paths using $fetch
 : then apply  $merge to paths and data matching sequences
 :) 
declare function merge($paths  as xs:string*,
                      $fetch  ,
                      $merge)
{
 let $data:= $paths!$fetch(.)
 return $merge($paths,$data)
};

declare function xar($package as element(pkg:package)) as xs:string
{
fn:concat($package/@abbrev , "-" ,$package/@version, ".xar")
};

(:
: update package.xml located at $cxan to ensure has entry for package $pkg
:)
declare function publish($pkg as element(pkg:package),$cxan)
{
copy  $c:=fn:doc($cxan)
modify(
let $pack:=$c/repo/pkg[name=$pkg/@name]
let $hit:= $pack/version[@num=$pkg/@version]
let $new:=<version num="{$pkg/@version}">
          <!-- generated: {fn:current-dateTime()} -->
          </version>
return if($hit)then () 
       else insert node $new into $pack
     )
return fn:put($c,$cxan)
};