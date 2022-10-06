(:~
 : compile java and build jars
 :)
declare namespace xp="expkg-zone58:text.parse";
declare namespace pkg="http://www.basex.org/modules/pkg";


declare variable $dest:="../src/java/"=>local:resolve();
declare variable $cat:=doc("../src/main/content/parsers.xml");
declare variable $bat:="compile.bat"=>local:resolve();
declare variable $bx:="../src/main/basex.xml"=>local:resolve();

declare function local:basex($classes){
<package xmlns="http://www.basex.org/modules/pkg">
{$classes!<jar>{.}.jar</jar>}
<class>{$classes[1]}</class>
</package>
};

declare function local:resolve($uri){
  file:resolve-path($uri,file:parent(static-base-uri()))
};

 let $classes:=$cat//xp:version/@ebnf!(replace("Parse-" || .,"-","_"))
 let $jars:= $classes! proc:execute($bat,.,map{"dir":$dest})=>trace("compile")
return file:write($bx,local:basex( $classes))




