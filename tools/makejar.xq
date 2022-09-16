(:~
 : compile java and build jars
 :)
declare namespace xp="expkg-zone58:text.parse";
declare namespace pkg="http://www.basex.org/modules/pkg";
declare variable $base:=file:resolve-path("../",file:base-dir());


declare variable $dest:="src/java/"=>file:resolve-path($base);
declare variable $cat:="src/main/content/parsers.xml"=>file:resolve-path($base)=>doc();
declare variable $bat:="tools/compile.bat"=>file:resolve-path($base);
declare variable $bx:="src/main/basex.xml"=>file:resolve-path($base);

declare function local:basex($classes){
<package xmlns="http://www.basex.org/modules/pkg">
{$classes!<jar>{.}.jar</jar>}
<class>{$classes[1]}</class>
</package>
};
 let $classes:=$cat//xp:version/@ebnf!(replace("Parse-" || .,"-","_"))
 let $jars:= $classes! proc:execute($bat,.,map{"dir":$dest})=>trace("compile")
 return file:write($bx,local:basex( $classes))




