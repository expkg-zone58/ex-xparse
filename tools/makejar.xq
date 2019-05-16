(:~
 : compile java and build jars
 :)
declare namespace xp="expkg-zone58:text.parse";
declare namespace pkg="http://www.basex.org/modules/pkg";

(: @TODO relative paths :)
declare variable $dest:="C:\Users\andy\git\ex-xparse\src\java\";
declare variable $cat:=doc("../src/main/content/parsers.xml");
declare variable $bat:="C:/Users/andy/git/ex-xparse/tools/compile.bat";
declare variable $bx:="C:/Users/andy/git/ex-xparse/src/main/basex.xml";
declare function local:basex($classes){
<package xmlns="http://www.basex.org/modules/pkg">
{$classes!<jar>{.}.jar</jar>}
<class>{$classes[1]}</class>
</package>
};
 let $classes:=$cat//xp:version/@ebnf!(replace("Parse-" || .,"-","_"))
 let $jars:= $classes! proc:execute($bat,.,map{"dir":$dest})=>trace("compile")
 return file:write($bx,local:basex( $classes))




