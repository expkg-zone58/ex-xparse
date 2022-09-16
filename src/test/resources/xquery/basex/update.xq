import module namespace xp="expkg-zone58:text.parse";
let $a:=file:read-text("C:\Users\andy\git\expkg-zone58\ex-xparse\src\test\resources\xquery\basex\updating.xq")
return xp:parse($a || " ",map{"lang":"xquery","version":"3.1 basex"}) 

