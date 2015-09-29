# ex-xparse
parsing expressions in XPath ([XPath3.0]) or XQuery ([XQuery3.0]) into XML trees. 
An implementation of the EXpath proposal [1] using the REX parser generator [2]

## Options

| Option | Type---  | Values      |Default  |Notes                                            |
|--------|----------|-------------|---------|-------------------------------------------------|
|lang    |xs:string |XPath, XQuery|XPath    |The language to be parsed (case insensitive)     |
|version |xs:string          |             |3.0      |                                        |
|flatten |xs:boolean|             |true()   |Flatten the parse tree                           |

## Available parsers
Selected parser is first where lang matches and version starts-with
````
<xp:parser lang="xpath" version="3.0" parser="xpath-30.xqm" namespace="parser" fn="p:parse-XPath"/>,
<xp:parser lang="xquery" version="3.0" parser="xquery-30.xqm"  namespace="parser" fn="p:parse-XQuery"/>,
<xp:parser lang="xquery" version="3.1 cr-20141218" parser="xquery-31"  namespace="CR-xquery-31-20141218" fn="p:parse-XQuery"/>,
<xp:parser lang="xquery-update" version="3.0 wd-20150219" parser="WD-xquery-update-30-20150219.xqm"  namespace="p:parser" fn="parse-XQuery"/>
````

 
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

xp:parse("2+2",map{"lang":"xquery",flatten:false()})
````

Result:
````
<XQuery>
  <Module>
    <MainModule>
      <Prolog/>
      <QueryBody>
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
      </QueryBody>
    </MainModule>
  </Module>
  <EOF/>
</XQuery>
````

## References
[1] https://lists.w3.org/Archives/Public/public-expath/2015Feb/att-0003/xparse.html
[2] http://www.bottlecaps.de/rex/

