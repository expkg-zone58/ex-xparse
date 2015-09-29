import module namespace xp="http://expath.org/ns/xparse" at "../main/content/xparse.xqm";
declare variable $opt1:=map{"lang":"xpath","flatten":true()};
xp:parse("2+2",$opt1)  
 