xquery version "3.1";
(:~  test all  samples in resources/xquery/ can be parsed :)
module namespace test = 'https://github.com/expkg-zone58/ex-xparse/test';
import module namespace xp="expkg-zone58:text.parse";

(:~ xparser defaults :)
declare variable $test:xparse_opts:=map{
                                       "lang":"xquery",
                                       "version":"3.1 basex",
                                       "flatten":true()
                                     };
declare variable $test:src-folder:=resolve-uri("resources/xquery/.",static-base-uri());

(:~ Parse all xq files stops at first failure :)
declare %unit:test  function test:run() {
  let $files:=file:list( $test:src-folder,true(),"*.xq")
  for $file in $files
  let $xq:=fetch:text(resolve-uri(translate($file,"\","/"),$test:src-folder))
  let $p:=xp:parse($xq || "",$test:xparse_opts) 
  return  if(name($p) ne "XQuery") then unit:fail( $file)
};
                                     
