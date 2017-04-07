(:~
 : ex-dotml tests
 :)
module namespace test = 'http://basex.org/modules/xqunit-tests';
import module namespace xp= "expkg-zone58:text.parse" (: at "../main/content/xparse.xqm" :);

declare %unit:test
(:~
 : check can parse javascript
 :) 
function  test:js()
{
 let $r:= "resources\apps.js"
        !unparsed-text(.)
        !xp:parse(.,map{"lang":"ecmascript"})
  return unit:assert($r)
};
