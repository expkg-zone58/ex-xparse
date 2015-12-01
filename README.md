# ex-xparse
Parses expressions in XPath  or XQuery into XML trees. 
An implementation of the EXpath [xparse](https://lists.w3.org/Archives/Public/public-expath/2015Feb/att-0003/xparse.html) proposal.
The [REX](http://www.bottlecaps.de/rex/) parser generator was used to generate the parsers.

## Note: Stackoverflow error
If you get a stackoverflow error - then the stack size may be increased via an JVM option in the BaseX script. E.g. `-Xss2m`

## Available parsers
Selected parser is first where lang matches and version starts-with. see `xp:parser($opts)`
````
<parsers xmlns="http://expath.org/ns/xparse">
<!-- 
 available languages and versions with  parser implementation details.
 -->
    <parser lang="xpath">
        <version version= "3.0" parser="xpath-30.xqm" namespace="parser" fn="p:parse-XPath" />
    </parser>
    
    <parser lang="xquery">

        <version version="3.0" parser="xquery-30.xquery" namespace="xquery-30"
            fn="p:parse-XQuery" />
            
        <version version="3.0" parser="xquery-30.xqm" namespace="parser"
            fn="p:parse-XQuery" />
            
        <version lang="xquery" version="3.1 cr-20141218" parser="CR-xquery-31-20141218.xqm"
            namespace="CR-xquery-31-20141218" fn="p:parse-XQuery" />
            
    </parser>
    
    <parser lang="xquery-update">
    
        <version version="3.0 wd-20150219" parser="WD-xquery-update-30-20150219.xqm"
            namespace="p:parser" fn="parse-XQuery" />
            
    </parser>

</parsers>
````
## Options

| Option | Type---  | Values      |Default  |Notes                                            |
|--------|----------|-------------|---------|-------------------------------------------------|
|lang    |xs:string |XPath, XQuery|XPath    |The language to be parsed (case insensitive)     |
|version |xs:string          |             |3.0      |                                        |
|flatten |xs:boolean|             |true()   |Flatten the parse tree                           |
 
## Example
````
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
````
import module namespace xp="http://expath.org/ns/xparse";

xp:parse("1+2",map{"lang":"xpath","flatten":false()})
````

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

