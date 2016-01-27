(: create o/ps
 : xar package and xqdoc
 :)
declare namespace pkg="http://expath.org/ns/pkg";
import module namespace build = "quodatum.utils.build" at "buildx.xqm";
declare variable $base:=resolve-uri("../");
declare variable $src:=resolve-uri("../src/main/");
declare variable $dest:=resolve-uri("../dist/");

(:~ 
 : the package definition as a pkg:package 
 :)
declare variable $package as element(pkg:package) :=doc(resolve-uri("expath-pkg.xml",$src))/pkg:package;

declare variable $content:=resolve-uri("content/",$src);
declare variable $dest-doc:=resolve-uri("doc/",$dest);


build:files($src) 
