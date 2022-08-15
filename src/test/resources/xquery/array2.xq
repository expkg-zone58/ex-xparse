let $f:=function($v,$this){
  typeswitch($v)
  case xs:anyAtomicType return $v
  case   map(*) return map:for-each($v,
                     function($k,$v){ if(starts-with($k,".")) then () else element {$k} { $this($v,$this)}
                })
  case   array(*) return "array"  (: <--here-- :)
  default return $v!<_>{.}</_>
}
return $f(42,$f)