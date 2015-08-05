# ex-xparse
parsing expressions in XPath ([XPath3.0]) or XQuery ([XQuery3.0]) into XML trees [1]

 
## Example
````
import module namespace xp="http://expath.org/ns/xparse";

xp:parse("2+2")
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

