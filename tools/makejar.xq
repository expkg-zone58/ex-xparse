(:~
 : compile java and build jars
 :)
declare namespace xp="expkg-zone58:text.parse";
declare namespace pkg="http://www.basex.org/modules/pkg";


declare variable $dest:="C:\Users\andy\workspace\ex-xparse\src\java\";
declare variable $cat:=doc("C:/Users/andy/workspace/ex-xparse/src/main/content/parsers.xml");
declare variable $bat:="C:/Users/andy/workspace/ex-xparse/tools/compile.bat";
declare variable $bx:="C:/Users/andy/workspace/ex-xparse/src/main/basex.xml";
declare function local:basex($classes){
<package xmlns="http://www.basex.org/modules/pkg">
{$classes!<jar>{.}.jar</jar>}
<class>{$classes[1]}</class>
</package>
};
 let $classes:=$cat//xp:version/@ebnf!(replace("Parse-" || .,"-","_"))
 let $jars:= $classes! proc:execute($bat,.,map{"dir":$dest})
 return file:write($bx,local:basex( $classes))



