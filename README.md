# ex-xparse
Parses text into syntax trees based using a parser selected from a set of built-in parsers.  
The [REx](http://www.bottlecaps.de/rex/) parser generator was used to generate the parsers.
This release uses the -basex option of REx to generate Java for BaseX.

It can be used to implement the EXPath [xparse proposal](https://lists.w3.org/Archives/Public/public-expath/2015Feb/att-0003/xparse.html) .

## Requirements
This release requires BaseX 8.6. This is a result of XQuery spec changes to `map:merge`

## Usage

```xquery
import module namespace xp="expkg-zone58:text.parse";
xp:parse("2+3",map{"lang":"xquery","version":"3.1 basex-20161204"}) 
```
The second argument provides options to control the parsing of the text from the first argument.
## Options

| Option | Type---  | Values      |Default  |Notes                                            |
|--------|----------|-------------|---------|-------------------------------------------------|
|lang    |xs:string |XPath, XQuery|XPath    |The language to be parsed                        |
|version |xs:string |             |""       |                                                 |
|flatten |xs:boolean|             |true()   |Flatten the parse tree                           |
The selected parser is chosen from the list in `parser.xml`. where the lang matches and version starts-with. see `xp:parser($opts)`

## Available parsers
```xml
  <parsers xmlns="proposal">
    <!-- available languages and versions with REx parser implementation options. 
        add "-tree -java -basex" -->

    <parser lang="xpath">
        <version version="3.0" ebnf="xpath-30" sym="XPath" />
    </parser>

    <parser lang="xquery">
        <version version="3.1 basex-20161204" ebnf="BaseX" sym="XQuery"
            options="-ll 2 -backtrack " />
            
        <version version="3.1 cr-20151217" ebnf="CR-xquery-31-20151217"
            sym="XQuery" />

        <version version="3.0" ebnf="xquery-30" sym="XQuery" />

        <version version="1.0" ebnf="xquery-10" sym="XQuery" />

        <version version="3.0 ML" ebnf="XQueryML30" sym="XQuery"
            options="-backtrack" />
    </parser>

    <parser lang="xqdoc-comments">
        <version version="20160405" ebnf="XQDocComments" sym="Comments" />
    </parser>

    <parser lang="ecmascript">
        <version version="5" ebnf="EcmaScript" sym="Program"
            options="-ll 1 -backtrack -asi" />
    </parser>
    <parser lang="rex">
        <version version="5.41" ebnf="REx" sym="Grammar" />
    </parser>
    <!-- <parser lang="xquery-update"> -->
    <!-- <version version="3.0 wd-20150219" ebnf="WD-xquery-update-30-20150219" -->
    <!-- sym="XQuery" /> -->
    <!-- </parser> -->

</parsers>
```
 
## Examples
```xquery
import module namespace xp="http://expath.org/ns/xparse";
xp:parse("2+3",map{"lang":"xquery"}) 
````
result
````
<XQuery>
  <MainModule>
    <Prolog/>
    <AdditiveExpr>
      <IntegerLiteral>2</IntegerLiteral>
      <TOKEN>+</TOKEN>
      <IntegerLiteral>3</IntegerLiteral>
    </AdditiveExpr>
  </MainModule>
  <EOF/>
</XQuery>
````
With options
```xquery
import module namespace xp="http://expath.org/ns/xparse";

xp:parse("1+2",map{"lang":"xpath","flatten":false()})
```

Result:
````
<XPath>
  <Expr>
    <ExprSingle>
      <OrExpr>
        <AndExpr>
          <ComparisonExpr>
            <StringConcatExpr>
              <RangeExpr>
                <AdditiveExpr>
                  <MultiplicativeExpr>
                    <UnionExpr>
                      <IntersectExceptExpr>
                        <InstanceofExpr>
                          <TreatExpr>
                            <CastableExpr>
                              <CastExpr>
                                <UnaryExpr>
                                  <ValueExpr>
                                    <SimpleMapExpr>
                                      <PathExpr>
                                        <RelativePathExpr>
                                          <StepExpr>
                                            <PostfixExpr>
                                              <PrimaryExpr>
                                                <Literal>
                                                  <NumericLiteral>
                                                    <IntegerLiteral>1</IntegerLiteral>
                                                  </NumericLiteral>
                                                </Literal>
                                              </PrimaryExpr>
                                            </PostfixExpr>
                                          </StepExpr>
                                        </RelativePathExpr>
                                      </PathExpr>
                                    </SimpleMapExpr>
                                  </ValueExpr>
                                </UnaryExpr>
                              </CastExpr>
                            </CastableExpr>
                          </TreatExpr>
                        </InstanceofExpr>
                      </IntersectExceptExpr>
                    </UnionExpr>
                  </MultiplicativeExpr>
                  <TOKEN>+</TOKEN>
                  <MultiplicativeExpr>
                    <UnionExpr>
                      <IntersectExceptExpr>
                        <InstanceofExpr>
                          <TreatExpr>
                            <CastableExpr>
                              <CastExpr>
                                <UnaryExpr>
                                  <ValueExpr>
                                    <SimpleMapExpr>
                                      <PathExpr>
                                        <RelativePathExpr>
                                          <StepExpr>
                                            <PostfixExpr>
                                              <PrimaryExpr>
                                                <Literal>
                                                  <NumericLiteral>
                                                    <IntegerLiteral>2</IntegerLiteral>
                                                  </NumericLiteral>
                                                </Literal>
                                              </PrimaryExpr>
                                            </PostfixExpr>
                                          </StepExpr>
                                        </RelativePathExpr>
                                      </PathExpr>
                                    </SimpleMapExpr>
                                  </ValueExpr>
                                </UnaryExpr>
                              </CastExpr>
                            </CastableExpr>
                          </TreatExpr>
                        </InstanceofExpr>
                      </IntersectExceptExpr>
                    </UnionExpr>
                  </MultiplicativeExpr>
                </AdditiveExpr>
              </RangeExpr>
            </StringConcatExpr>
          </ComparisonExpr>
        </AndExpr>
      </OrExpr>
    </ExprSingle>
  </Expr>
  <EOF/>
</XPath>
````
Versions
````
import module namespace xp="http://expath.org/ns/xparse";
xp:parse("map{'a':42}",map{"lang":"xquery"}) 
````
Result
````
<XQuery>
  <MainModule>
    <Prolog/>
    <MapConstructor>
      <TOKEN>map</TOKEN>
      <TOKEN>{</TOKEN>
      <MapConstructorEntry>
        <StringLiteral>'a'</StringLiteral>
        <TOKEN>:</TOKEN>
        <IntegerLiteral>42</IntegerLiteral>
      </MapConstructorEntry>
      <TOKEN>}</TOKEN>
    </MapConstructor>
  </MainModule>
  <EOF/>
</XQuery>
````

````
import module namespace xp="http://expath.org/ns/xparse";
xp:parse("map{'a':42}",map{"lang":"xquery","version":"3.0"}) 
````
Result
````
<ERROR b="4" e="4" s="158">lexical analysis failed
while expecting [S, EOF, '!', '!=', '#', '(', '(:', ')', '*', '+', ',', '-', '/', '//', ';', '&lt;', '&lt;&lt;', '&lt;=', '=', '&gt;', '&gt;=', '&gt;&gt;', '[', ']', 'and', 'ascending', 'case', 'cast', 'castable', 'collation', 'count', 'default', 'descending', 'div', 'else', 'empty', 'end', 'eq', 'except', 'for', 'ge', 'group', 'gt', 'idiv', 'instance', 'intersect', 'is', 'le', 'let', 'lt', 'mod', 'ne', 'only', 'or', 'order', 'return', 'satisfies', 'stable', 'start', 'to', 'treat', 'union', 'where', '|', '||', '}']
at line 1, column 4:
...{'a':42}...</ERROR>
````

## History
* versions before 0.5 used the namespace `http://expath.org/ns/xparse`
