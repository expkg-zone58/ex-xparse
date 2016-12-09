(:~
 : ebnf to Java parser for BaseX using REx 
 :)
declare namespace xp="http://expath.org/ns/xparse";
declare variable $src:="C:\Users\andy\workspace\ex-xparse\src\main\content\ebnf\";
declare variable $dest:="C:\Users\andy\workspace\ex-xparse\src\java\";
declare variable $cat:=doc("../src/main/content/parsers.xml");
(:~ 
 : Generate code for ebnf using http://www.bottlecaps.de/rex/
 : @param $ebnf the ebnf text
 :)
declare function local:rex-request($ebnf as xs:string,$command as xs:string)as item()+{
let $req:=
<request xmlns="http://expath.org/ns/http-client" href='http://www.bottlecaps.de/rex/' method="post">
    <multipart media-type="multipart/form-data" boundary="xyzBouNDarYxyz">  
       <header name="Content-Disposition" value='form-data; name="command"'/>    
       <body media-type="text/plain"/>       
       <header name="Content-Disposition" value='form-data; name="input"; filename="file.ebnf"'/>   
       <body media-type="text/plain"/>       
    </multipart>
</request>
return http:send-request($req,(),($command,$ebnf))[2]
};

(:~ 
 : Generate code for ebnf using http://www.bottlecaps.de/rex/
 : @param $ebnf the ebnf text
 :)
declare function local:save-parser($file as xs:string,$command as xs:string?) as empty-sequence()
{
    let $class:="Parse-" ||$file
    let $ebnf:="" || file:read-text($src || $file || ".ebnf")
    let $command:=``[`{$command}` -tree -java -basex -name expkg-zone58.ex-xparse.`{$class}`]``
    let $java:= local:rex-request($ebnf,$command )
    return file:write-text($dest || replace($class,"-","_") || ".java",$java)
};

 
for $ebnf in $cat//xp:version
return local:save-parser($ebnf/@ebnf,$ebnf/@options)
