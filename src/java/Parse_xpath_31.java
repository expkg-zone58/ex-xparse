// This file was generated on Thu Apr 11, 2019 23:36 (UTC+02) by REx v5.49 which is Copyright (c) 1979-2019 by Gunther Rademacher <grd@gmx.net>
// REx command line: file.ebnf -tree -java -basex -name expkg-zone58.text.parse.Parse-xpath-31

package expkg_zone58.text.parse;

import java.io.IOException;
import java.util.Arrays;

import org.basex.build.MemBuilder;
import org.basex.build.SingleParser;
import org.basex.core.MainOptions;
import org.basex.io.IOContent;
import org.basex.query.value.item.Str;
import org.basex.query.value.node.ANode;
import org.basex.query.value.node.DBNode;
import org.basex.util.Atts;
import org.basex.util.Token;

public class Parse_xpath_31
{
  public static class ParseException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;
    private int begin, end, offending, expected, state;

    public ParseException(int b, int e, int s, int o, int x)
    {
      begin = b;
      end = e;
      state = s;
      offending = o;
      expected = x;
    }

    @Override
    public String getMessage()
    {
      return offending < 0
           ? "lexical analysis failed"
           : "syntax error";
    }

    public void serialize(EventHandler eventHandler)
    {
    }

    public int getBegin() {return begin;}
    public int getEnd() {return end;}
    public int getState() {return state;}
    public int getOffending() {return offending;}
    public int getExpected() {return expected;}
    public boolean isAmbiguousInput() {return false;}
  }

  public interface EventHandler
  {
    public void reset(CharSequence string);
    public void startNonterminal(String name, int begin);
    public void endNonterminal(String name, int end);
    public void terminal(String name, int begin, int end);
    public void whitespace(int begin, int end);
  }

  public static class TopDownTreeBuilder implements EventHandler
  {
    private CharSequence input = null;
    private Nonterminal[] stack = new Nonterminal[64];
    private int top = -1;

    @Override
    public void reset(CharSequence input)
    {
      this.input = input;
      top = -1;
    }

    @Override
    public void startNonterminal(String name, int begin)
    {
      Nonterminal nonterminal = new Nonterminal(name, begin, begin, new Symbol[0]);
      if (top >= 0) addChild(nonterminal);
      if (++top >= stack.length) stack = Arrays.copyOf(stack, stack.length << 1);
      stack[top] = nonterminal;
    }

    @Override
    public void endNonterminal(String name, int end)
    {
      stack[top].end = end;
      if (top > 0) --top;
    }

    @Override
    public void terminal(String name, int begin, int end)
    {
      addChild(new Terminal(name, begin, end));
    }

    @Override
    public void whitespace(int begin, int end)
    {
    }

    private void addChild(Symbol s)
    {
      Nonterminal current = stack[top];
      current.children = Arrays.copyOf(current.children, current.children.length + 1);
      current.children[current.children.length - 1] = s;
    }

    public void serialize(EventHandler e)
    {
      e.reset(input);
      stack[0].send(e);
    }
  }

  public static abstract class Symbol
  {
    public String name;
    public int begin;
    public int end;

    protected Symbol(String name, int begin, int end)
    {
      this.name = name;
      this.begin = begin;
      this.end = end;
    }

    public abstract void send(EventHandler e);
  }

  public static class Terminal extends Symbol
  {
    public Terminal(String name, int begin, int end)
    {
      super(name, begin, end);
    }

    @Override
    public void send(EventHandler e)
    {
      e.terminal(name, begin, end);
    }
  }

  public static class Nonterminal extends Symbol
  {
    public Symbol[] children;

    public Nonterminal(String name, int begin, int end, Symbol[] children)
    {
      super(name, begin, end);
      this.children = children;
    }

    @Override
    public void send(EventHandler e)
    {
      e.startNonterminal(name, begin);
      int pos = begin;
      for (Symbol c : children)
      {
        if (pos < c.begin) e.whitespace(pos, c.begin);
        c.send(e);
        pos = c.end;
      }
      if (pos < end) e.whitespace(pos, end);
      e.endNonterminal(name, end);
    }
  }

  public static ANode parseXPath(Str str) throws IOException
  {
    BaseXFunction baseXFunction = new BaseXFunction()
    {
      @Override
      public void execute(Parse_xpath_31 p) {p.parse_XPath();}
    };
    return baseXFunction.call(str);
  }

  public static abstract class BaseXFunction
  {
    protected abstract void execute(Parse_xpath_31 p);

    public ANode call(Str str) throws IOException
    {
      String input = str.toJava();
      SingleParser singleParser = new SingleParser(new IOContent(""), MainOptions.get())
      {
        @Override
        protected void parse() throws IOException {}
      };
      MemBuilder memBuilder = new MemBuilder(input, singleParser);
      memBuilder.init();
      BaseXTreeBuilder treeBuilder = new BaseXTreeBuilder(memBuilder);
      Parse_xpath_31 parser = new Parse_xpath_31();
      parser.initialize(input, treeBuilder);
      try
      {
        execute(parser);
      }
      catch (ParseException pe)
      {
        memBuilder = new MemBuilder(input, singleParser);
        memBuilder.init();
        Atts atts = new Atts();
        atts.add(Token.token("b"), Token.token(pe.getBegin() + 1));
        atts.add(Token.token("e"), Token.token(pe.getEnd() + 1));
        if (pe.getOffending() < 0)
        {
          atts.add(Token.token("s"), Token.token(pe.getState()));
        }
        else
        {
          atts.add(Token.token("o"), Token.token(pe.getOffending()));
          atts.add(Token.token("x"), Token.token(pe.getExpected()));
        }
        memBuilder.openElem(Token.token("ERROR"), atts, new Atts());
        memBuilder.text(Token.token(parser.getErrorMessage(pe)));
        memBuilder.closeElem();
      }
      return new DBNode(memBuilder.data());
    }
  }

  public static class BaseXTreeBuilder implements EventHandler
  {
    private CharSequence input;
    private MemBuilder builder;
    private Atts nsp = new Atts();
    private Atts atts = new Atts();

    public BaseXTreeBuilder(MemBuilder b)
    {
      input = null;
      builder = b;
    }

    @Override
    public void reset(CharSequence string)
    {
      input = string;
    }

    @Override
    public void startNonterminal(String name, int begin)
    {
      try
      {
        builder.openElem(Token.token(name), atts, nsp);
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void endNonterminal(String name, int end)
    {
      try
      {
        builder.closeElem();
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void terminal(String name, int begin, int end)
    {
      if (name.charAt(0) == '\'')
      {
        name = "TOKEN";
      }
      startNonterminal(name, begin);
      characters(begin, end);
      endNonterminal(name, end);
    }

    @Override
    public void whitespace(int begin, int end)
    {
      characters(begin, end);
    }

    private void characters(int begin, int end)
    {
      if (begin < end)
      {
        try
        {
          builder.text(Token.token(input.subSequence(begin, end).toString()));
        }
        catch (IOException e)
        {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public Parse_xpath_31()
  {
  }

  public Parse_xpath_31(CharSequence string, EventHandler t)
  {
    initialize(string, t);
  }

  public void initialize(CharSequence source, EventHandler parsingEventHandler)
  {
    eventHandler = parsingEventHandler;
    input = source;
    size = source.length();
    reset(0, 0, 0);
  }

  public CharSequence getInput()
  {
    return input;
  }

  public int getTokenOffset()
  {
    return b0;
  }

  public int getTokenEnd()
  {
    return e0;
  }

  public final void reset(int l, int b, int e)
  {
            b0 = b; e0 = b;
    l1 = l; b1 = b; e1 = e;
    l2 = 0;
    l3 = 0;
    end = e;
    eventHandler.reset(input);
  }

  public void reset()
  {
    reset(0, 0, 0);
  }

  public static String getOffendingToken(ParseException e)
  {
    return e.getOffending() < 0 ? null : TOKEN[e.getOffending()];
  }

  public static String[] getExpectedTokenSet(ParseException e)
  {
    String[] expected;
    if (e.getExpected() >= 0)
    {
      expected = new String[]{TOKEN[e.getExpected()]};
    }
    else
    {
      expected = getTokenSet(- e.getState());
    }
    return expected;
  }

  public String getErrorMessage(ParseException e)
  {
    String message = e.getMessage();
    String[] tokenSet = getExpectedTokenSet(e);
    String found = getOffendingToken(e);
    int size = e.getEnd() - e.getBegin();
    message += (found == null ? "" : ", found " + found)
            + "\nwhile expecting "
            + (tokenSet.length == 1 ? tokenSet[0] : java.util.Arrays.toString(tokenSet))
            + "\n"
            + (size == 0 || found != null ? "" : "after successfully scanning " + size + " characters beginning ");
    String prefix = input.subSequence(0, e.getBegin()).toString();
    int line = prefix.replaceAll("[^\n]", "").length() + 1;
    int column = prefix.length() - prefix.lastIndexOf('\n');
    return message
         + "at line " + line + ", column " + column + ":\n..."
         + input.subSequence(e.getBegin(), Math.min(input.length(), e.getBegin() + 64))
         + "...";
  }

  public void parse_XPath()
  {
    eventHandler.startNonterminal("XPath", e0);
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_Expr();
    consume(11);                    // EOF
    eventHandler.endNonterminal("XPath", e0);
  }

  private void parse_ParamList()
  {
    eventHandler.startNonterminal("ParamList", e0);
    parse_Param();
    for (;;)
    {
      lookahead1W(17);              // S^WS | '(:' | ')' | ','
      if (l1 != 21)                 // ','
      {
        break;
      }
      consume(21);                  // ','
      lookahead1W(2);               // S^WS | '$' | '(:'
      whitespace();
      parse_Param();
    }
    eventHandler.endNonterminal("ParamList", e0);
  }

  private void parse_Param()
  {
    eventHandler.startNonterminal("Param", e0);
    consume(15);                    // '$'
    lookahead1W(42);                // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_EQName();
    lookahead1W(21);                // S^WS | '(:' | ')' | ',' | 'as'
    if (l1 == 47)                   // 'as'
    {
      whitespace();
      parse_TypeDeclaration();
    }
    eventHandler.endNonterminal("Param", e0);
  }

  private void parse_FunctionBody()
  {
    eventHandler.startNonterminal("FunctionBody", e0);
    parse_EnclosedExpr();
    eventHandler.endNonterminal("FunctionBody", e0);
  }

  private void parse_EnclosedExpr()
  {
    eventHandler.startNonterminal("EnclosedExpr", e0);
    consume(104);                   // '{'
    lookahead1W(56);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union' | '}'
    if (l1 != 107)                  // '}'
    {
      whitespace();
      parse_Expr();
    }
    consume(107);                   // '}'
    eventHandler.endNonterminal("EnclosedExpr", e0);
  }

  private void parse_Expr()
  {
    eventHandler.startNonterminal("Expr", e0);
    parse_ExprSingle();
    for (;;)
    {
      if (l1 != 21)                 // ','
      {
        break;
      }
      consume(21);                  // ','
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_ExprSingle();
    }
    eventHandler.endNonterminal("Expr", e0);
  }

  private void parse_ExprSingle()
  {
    eventHandler.startNonterminal("ExprSingle", e0);
    switch (l1)
    {
    case 70:                        // 'if'
      lookahead2W(34);              // S^WS | EOF | '!' | '!=' | '(' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' | '//' |
                                    // ':' | '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' | ']' | 'and' |
                                    // 'cast' | 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' | 'idiv' |
                                    // 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' | 'return' |
                                    // 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      break;
    case 61:                        // 'every'
    case 65:                        // 'for'
    case 77:                        // 'let'
    case 96:                        // 'some'
      lookahead2W(40);              // S^WS | EOF | '!' | '!=' | '#' | '$' | '(' | '(:' | ')' | '*' | '+' | ',' | '-' |
                                    // '/' | '//' | ':' | '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' |
                                    // ']' | 'and' | 'cast' | 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' |
                                    // 'gt' | 'idiv' | 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' |
                                    // 'or' | 'return' | 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 1985:                      // 'for' '$'
      parse_ForExpr();
      break;
    case 1997:                      // 'let' '$'
      parse_LetExpr();
      break;
    case 1981:                      // 'every' '$'
    case 2016:                      // 'some' '$'
      parse_QuantifiedExpr();
      break;
    case 2118:                      // 'if' '('
      parse_IfExpr();
      break;
    default:
      parse_OrExpr();
    }
    eventHandler.endNonterminal("ExprSingle", e0);
  }

  private void parse_ForExpr()
  {
    eventHandler.startNonterminal("ForExpr", e0);
    parse_SimpleForClause();
    consume(91);                    // 'return'
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_ExprSingle();
    eventHandler.endNonterminal("ForExpr", e0);
  }

  private void parse_SimpleForClause()
  {
    eventHandler.startNonterminal("SimpleForClause", e0);
    consume(65);                    // 'for'
    lookahead1W(2);                 // S^WS | '$' | '(:'
    whitespace();
    parse_SimpleForBinding();
    for (;;)
    {
      if (l1 != 21)                 // ','
      {
        break;
      }
      consume(21);                  // ','
      lookahead1W(2);               // S^WS | '$' | '(:'
      whitespace();
      parse_SimpleForBinding();
    }
    eventHandler.endNonterminal("SimpleForClause", e0);
  }

  private void parse_SimpleForBinding()
  {
    eventHandler.startNonterminal("SimpleForBinding", e0);
    consume(15);                    // '$'
    lookahead1W(42);                // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_VarName();
    lookahead1W(10);                // S^WS | '(:' | 'in'
    consume(71);                    // 'in'
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_ExprSingle();
    eventHandler.endNonterminal("SimpleForBinding", e0);
  }

  private void parse_LetExpr()
  {
    eventHandler.startNonterminal("LetExpr", e0);
    parse_SimpleLetClause();
    consume(91);                    // 'return'
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_ExprSingle();
    eventHandler.endNonterminal("LetExpr", e0);
  }

  private void parse_SimpleLetClause()
  {
    eventHandler.startNonterminal("SimpleLetClause", e0);
    consume(77);                    // 'let'
    lookahead1W(2);                 // S^WS | '$' | '(:'
    whitespace();
    parse_SimpleLetBinding();
    for (;;)
    {
      if (l1 != 21)                 // ','
      {
        break;
      }
      consume(21);                  // ','
      lookahead1W(2);               // S^WS | '$' | '(:'
      whitespace();
      parse_SimpleLetBinding();
    }
    eventHandler.endNonterminal("SimpleLetClause", e0);
  }

  private void parse_SimpleLetBinding()
  {
    eventHandler.startNonterminal("SimpleLetBinding", e0);
    consume(15);                    // '$'
    lookahead1W(42);                // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_VarName();
    lookahead1W(8);                 // S^WS | '(:' | ':='
    consume(30);                    // ':='
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_ExprSingle();
    eventHandler.endNonterminal("SimpleLetBinding", e0);
  }

  private void parse_QuantifiedExpr()
  {
    eventHandler.startNonterminal("QuantifiedExpr", e0);
    switch (l1)
    {
    case 96:                        // 'some'
      consume(96);                  // 'some'
      break;
    default:
      consume(61);                  // 'every'
    }
    lookahead1W(2);                 // S^WS | '$' | '(:'
    consume(15);                    // '$'
    lookahead1W(42);                // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_VarName();
    lookahead1W(10);                // S^WS | '(:' | 'in'
    consume(71);                    // 'in'
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_ExprSingle();
    for (;;)
    {
      if (l1 != 21)                 // ','
      {
        break;
      }
      consume(21);                  // ','
      lookahead1W(2);               // S^WS | '$' | '(:'
      consume(15);                  // '$'
      lookahead1W(42);              // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_VarName();
      lookahead1W(10);              // S^WS | '(:' | 'in'
      consume(71);                  // 'in'
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_ExprSingle();
    }
    consume(92);                    // 'satisfies'
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_ExprSingle();
    eventHandler.endNonterminal("QuantifiedExpr", e0);
  }

  private void parse_IfExpr()
  {
    eventHandler.startNonterminal("IfExpr", e0);
    consume(70);                    // 'if'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_Expr();
    consume(18);                    // ')'
    lookahead1W(12);                // S^WS | '(:' | 'then'
    consume(99);                    // 'then'
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_ExprSingle();
    consume(58);                    // 'else'
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_ExprSingle();
    eventHandler.endNonterminal("IfExpr", e0);
  }

  private void parse_OrExpr()
  {
    eventHandler.startNonterminal("OrExpr", e0);
    parse_AndExpr();
    for (;;)
    {
      if (l1 != 86)                 // 'or'
      {
        break;
      }
      consume(86);                  // 'or'
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_AndExpr();
    }
    eventHandler.endNonterminal("OrExpr", e0);
  }

  private void parse_AndExpr()
  {
    eventHandler.startNonterminal("AndExpr", e0);
    parse_ComparisonExpr();
    for (;;)
    {
      if (l1 != 45)                 // 'and'
      {
        break;
      }
      consume(45);                  // 'and'
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_ComparisonExpr();
    }
    eventHandler.endNonterminal("AndExpr", e0);
  }

  private void parse_ComparisonExpr()
  {
    eventHandler.startNonterminal("ComparisonExpr", e0);
    parse_StringConcatExpr();
    if (l1 != 11                    // EOF
     && l1 != 18                    // ')'
     && l1 != 21                    // ','
     && l1 != 27                    // ':'
     && l1 != 42                    // ']'
     && l1 != 45                    // 'and'
     && l1 != 58                    // 'else'
     && l1 != 86                    // 'or'
     && l1 != 91                    // 'return'
     && l1 != 92                    // 'satisfies'
     && l1 != 107)                  // '}'
    {
      switch (l1)
      {
      case 60:                      // 'eq'
      case 67:                      // 'ge'
      case 68:                      // 'gt'
      case 76:                      // 'le'
      case 78:                      // 'lt'
      case 83:                      // 'ne'
        whitespace();
        parse_ValueComp();
        break;
      case 32:                      // '<<'
      case 38:                      // '>>'
      case 74:                      // 'is'
        whitespace();
        parse_NodeComp();
        break;
      default:
        whitespace();
        parse_GeneralComp();
      }
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_StringConcatExpr();
    }
    eventHandler.endNonterminal("ComparisonExpr", e0);
  }

  private void parse_StringConcatExpr()
  {
    eventHandler.startNonterminal("StringConcatExpr", e0);
    parse_RangeExpr();
    for (;;)
    {
      if (l1 != 106)                // '||'
      {
        break;
      }
      consume(106);                 // '||'
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_RangeExpr();
    }
    eventHandler.endNonterminal("StringConcatExpr", e0);
  }

  private void parse_RangeExpr()
  {
    eventHandler.startNonterminal("RangeExpr", e0);
    parse_AdditiveExpr();
    if (l1 == 100)                  // 'to'
    {
      consume(100);                 // 'to'
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_AdditiveExpr();
    }
    eventHandler.endNonterminal("RangeExpr", e0);
  }

  private void parse_AdditiveExpr()
  {
    eventHandler.startNonterminal("AdditiveExpr", e0);
    parse_MultiplicativeExpr();
    for (;;)
    {
      if (l1 != 20                  // '+'
       && l1 != 22)                 // '-'
      {
        break;
      }
      switch (l1)
      {
      case 20:                      // '+'
        consume(20);                // '+'
        break;
      default:
        consume(22);                // '-'
      }
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_MultiplicativeExpr();
    }
    eventHandler.endNonterminal("AdditiveExpr", e0);
  }

  private void parse_MultiplicativeExpr()
  {
    eventHandler.startNonterminal("MultiplicativeExpr", e0);
    parse_UnionExpr();
    for (;;)
    {
      if (l1 != 19                  // '*'
       && l1 != 55                  // 'div'
       && l1 != 69                  // 'idiv'
       && l1 != 80)                 // 'mod'
      {
        break;
      }
      switch (l1)
      {
      case 19:                      // '*'
        consume(19);                // '*'
        break;
      case 55:                      // 'div'
        consume(55);                // 'div'
        break;
      case 69:                      // 'idiv'
        consume(69);                // 'idiv'
        break;
      default:
        consume(80);                // 'mod'
      }
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_UnionExpr();
    }
    eventHandler.endNonterminal("MultiplicativeExpr", e0);
  }

  private void parse_UnionExpr()
  {
    eventHandler.startNonterminal("UnionExpr", e0);
    parse_IntersectExceptExpr();
    for (;;)
    {
      if (l1 != 103                 // 'union'
       && l1 != 105)                // '|'
      {
        break;
      }
      switch (l1)
      {
      case 103:                     // 'union'
        consume(103);               // 'union'
        break;
      default:
        consume(105);               // '|'
      }
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_IntersectExceptExpr();
    }
    eventHandler.endNonterminal("UnionExpr", e0);
  }

  private void parse_IntersectExceptExpr()
  {
    eventHandler.startNonterminal("IntersectExceptExpr", e0);
    parse_InstanceofExpr();
    for (;;)
    {
      lookahead1W(25);              // S^WS | EOF | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | ':' | '<' | '<<' |
                                    // '<=' | '=' | '>' | '>=' | '>>' | ']' | 'and' | 'div' | 'else' | 'eq' | 'except' |
                                    // 'ge' | 'gt' | 'idiv' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' |
                                    // 'return' | 'satisfies' | 'to' | 'union' | '|' | '||' | '}'
      if (l1 != 62                  // 'except'
       && l1 != 73)                 // 'intersect'
      {
        break;
      }
      switch (l1)
      {
      case 73:                      // 'intersect'
        consume(73);                // 'intersect'
        break;
      default:
        consume(62);                // 'except'
      }
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_InstanceofExpr();
    }
    eventHandler.endNonterminal("IntersectExceptExpr", e0);
  }

  private void parse_InstanceofExpr()
  {
    eventHandler.startNonterminal("InstanceofExpr", e0);
    parse_TreatExpr();
    lookahead1W(26);                // S^WS | EOF | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | ':' | '<' | '<<' |
                                    // '<=' | '=' | '>' | '>=' | '>>' | ']' | 'and' | 'div' | 'else' | 'eq' | 'except' |
                                    // 'ge' | 'gt' | 'idiv' | 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' |
                                    // 'ne' | 'or' | 'return' | 'satisfies' | 'to' | 'union' | '|' | '||' | '}'
    if (l1 == 72)                   // 'instance'
    {
      consume(72);                  // 'instance'
      lookahead1W(11);              // S^WS | '(:' | 'of'
      consume(85);                  // 'of'
      lookahead1W(44);              // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_SequenceType();
    }
    eventHandler.endNonterminal("InstanceofExpr", e0);
  }

  private void parse_TreatExpr()
  {
    eventHandler.startNonterminal("TreatExpr", e0);
    parse_CastableExpr();
    lookahead1W(27);                // S^WS | EOF | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | ':' | '<' | '<<' |
                                    // '<=' | '=' | '>' | '>=' | '>>' | ']' | 'and' | 'div' | 'else' | 'eq' | 'except' |
                                    // 'ge' | 'gt' | 'idiv' | 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' |
                                    // 'ne' | 'or' | 'return' | 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' |
                                    // '}'
    if (l1 == 101)                  // 'treat'
    {
      consume(101);                 // 'treat'
      lookahead1W(9);               // S^WS | '(:' | 'as'
      consume(47);                  // 'as'
      lookahead1W(44);              // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_SequenceType();
    }
    eventHandler.endNonterminal("TreatExpr", e0);
  }

  private void parse_CastableExpr()
  {
    eventHandler.startNonterminal("CastableExpr", e0);
    parse_CastExpr();
    lookahead1W(29);                // S^WS | EOF | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | ':' | '<' | '<<' |
                                    // '<=' | '=' | '>' | '>=' | '>>' | ']' | 'and' | 'castable' | 'div' | 'else' |
                                    // 'eq' | 'except' | 'ge' | 'gt' | 'idiv' | 'instance' | 'intersect' | 'is' | 'le' |
                                    // 'lt' | 'mod' | 'ne' | 'or' | 'return' | 'satisfies' | 'to' | 'treat' | 'union' |
                                    // '|' | '||' | '}'
    if (l1 == 50)                   // 'castable'
    {
      consume(50);                  // 'castable'
      lookahead1W(9);               // S^WS | '(:' | 'as'
      consume(47);                  // 'as'
      lookahead1W(42);              // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_SingleType();
    }
    eventHandler.endNonterminal("CastableExpr", e0);
  }

  private void parse_CastExpr()
  {
    eventHandler.startNonterminal("CastExpr", e0);
    parse_ArrowExpr();
    if (l1 == 49)                   // 'cast'
    {
      consume(49);                  // 'cast'
      lookahead1W(9);               // S^WS | '(:' | 'as'
      consume(47);                  // 'as'
      lookahead1W(42);              // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_SingleType();
    }
    eventHandler.endNonterminal("CastExpr", e0);
  }

  private void parse_ArrowExpr()
  {
    eventHandler.startNonterminal("ArrowExpr", e0);
    parse_UnaryExpr();
    for (;;)
    {
      lookahead1W(32);              // S^WS | EOF | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | ':' | '<' | '<<' |
                                    // '<=' | '=' | '=>' | '>' | '>=' | '>>' | ']' | 'and' | 'cast' | 'castable' |
                                    // 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' | 'idiv' | 'instance' |
                                    // 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' | 'return' | 'satisfies' |
                                    // 'to' | 'treat' | 'union' | '|' | '||' | '}'
      if (l1 != 35)                 // '=>'
      {
        break;
      }
      consume(35);                  // '=>'
      lookahead1W(46);              // URIQualifiedName | QName^Token | S^WS | '$' | '(' | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_ArrowFunctionSpecifier();
      lookahead1W(3);               // S^WS | '(' | '(:'
      whitespace();
      parse_ArgumentList();
    }
    eventHandler.endNonterminal("ArrowExpr", e0);
  }

  private void parse_UnaryExpr()
  {
    eventHandler.startNonterminal("UnaryExpr", e0);
    for (;;)
    {
      lookahead1W(53);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      if (l1 != 20                  // '+'
       && l1 != 22)                 // '-'
      {
        break;
      }
      switch (l1)
      {
      case 22:                      // '-'
        consume(22);                // '-'
        break;
      default:
        consume(20);                // '+'
      }
    }
    whitespace();
    parse_ValueExpr();
    eventHandler.endNonterminal("UnaryExpr", e0);
  }

  private void parse_ValueExpr()
  {
    eventHandler.startNonterminal("ValueExpr", e0);
    parse_SimpleMapExpr();
    eventHandler.endNonterminal("ValueExpr", e0);
  }

  private void parse_GeneralComp()
  {
    eventHandler.startNonterminal("GeneralComp", e0);
    switch (l1)
    {
    case 34:                        // '='
      consume(34);                  // '='
      break;
    case 13:                        // '!='
      consume(13);                  // '!='
      break;
    case 31:                        // '<'
      consume(31);                  // '<'
      break;
    case 33:                        // '<='
      consume(33);                  // '<='
      break;
    case 36:                        // '>'
      consume(36);                  // '>'
      break;
    default:
      consume(37);                  // '>='
    }
    eventHandler.endNonterminal("GeneralComp", e0);
  }

  private void parse_ValueComp()
  {
    eventHandler.startNonterminal("ValueComp", e0);
    switch (l1)
    {
    case 60:                        // 'eq'
      consume(60);                  // 'eq'
      break;
    case 83:                        // 'ne'
      consume(83);                  // 'ne'
      break;
    case 78:                        // 'lt'
      consume(78);                  // 'lt'
      break;
    case 76:                        // 'le'
      consume(76);                  // 'le'
      break;
    case 68:                        // 'gt'
      consume(68);                  // 'gt'
      break;
    default:
      consume(67);                  // 'ge'
    }
    eventHandler.endNonterminal("ValueComp", e0);
  }

  private void parse_NodeComp()
  {
    eventHandler.startNonterminal("NodeComp", e0);
    switch (l1)
    {
    case 74:                        // 'is'
      consume(74);                  // 'is'
      break;
    case 32:                        // '<<'
      consume(32);                  // '<<'
      break;
    default:
      consume(38);                  // '>>'
    }
    eventHandler.endNonterminal("NodeComp", e0);
  }

  private void parse_SimpleMapExpr()
  {
    eventHandler.startNonterminal("SimpleMapExpr", e0);
    parse_PathExpr();
    for (;;)
    {
      if (l1 != 12)                 // '!'
      {
        break;
      }
      consume(12);                  // '!'
      lookahead1W(52);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '.' |
                                    // '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' | 'and' |
                                    // 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_PathExpr();
    }
    eventHandler.endNonterminal("SimpleMapExpr", e0);
  }

  private void parse_PathExpr()
  {
    eventHandler.startNonterminal("PathExpr", e0);
    switch (l1)
    {
    case 25:                        // '/'
      consume(25);                  // '/'
      lookahead1W(57);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | EOF | '!' | '!=' | '$' | '(' |
                                    // '(:' | ')' | '*' | '+' | ',' | '-' | '.' | '..' | ':' | '<' | '<<' | '<=' | '=' |
                                    // '=>' | '>' | '>=' | '>>' | '?' | '@' | '[' | ']' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union' | '|' |
                                    // '||' | '}'
      switch (l1)
      {
      case 11:                      // EOF
      case 12:                      // '!'
      case 13:                      // '!='
      case 18:                      // ')'
      case 19:                      // '*'
      case 20:                      // '+'
      case 21:                      // ','
      case 22:                      // '-'
      case 27:                      // ':'
      case 31:                      // '<'
      case 32:                      // '<<'
      case 33:                      // '<='
      case 34:                      // '='
      case 35:                      // '=>'
      case 36:                      // '>'
      case 37:                      // '>='
      case 38:                      // '>>'
      case 42:                      // ']'
      case 105:                     // '|'
      case 106:                     // '||'
      case 107:                     // '}'
        break;
      default:
        whitespace();
        parse_RelativePathExpr();
      }
      break;
    case 26:                        // '//'
      consume(26);                  // '//'
      lookahead1W(51);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '.' |
                                    // '..' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' | 'and' | 'array' |
                                    // 'attribute' | 'cast' | 'castable' | 'child' | 'comment' | 'descendant' |
                                    // 'descendant-or-self' | 'div' | 'document-node' | 'element' | 'else' |
                                    // 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_RelativePathExpr();
      break;
    default:
      parse_RelativePathExpr();
    }
    eventHandler.endNonterminal("PathExpr", e0);
  }

  private void parse_RelativePathExpr()
  {
    eventHandler.startNonterminal("RelativePathExpr", e0);
    parse_StepExpr();
    for (;;)
    {
      if (l1 != 25                  // '/'
       && l1 != 26)                 // '//'
      {
        break;
      }
      switch (l1)
      {
      case 25:                      // '/'
        consume(25);                // '/'
        break;
      default:
        consume(26);                // '//'
      }
      lookahead1W(51);              // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '.' |
                                    // '..' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' | 'and' | 'array' |
                                    // 'attribute' | 'cast' | 'castable' | 'child' | 'comment' | 'descendant' |
                                    // 'descendant-or-self' | 'div' | 'document-node' | 'element' | 'else' |
                                    // 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_StepExpr();
    }
    eventHandler.endNonterminal("RelativePathExpr", e0);
  }

  private void parse_StepExpr()
  {
    eventHandler.startNonterminal("StepExpr", e0);
    switch (l1)
    {
    case 66:                        // 'function'
      lookahead2W(34);              // S^WS | EOF | '!' | '!=' | '(' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' | '//' |
                                    // ':' | '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' | ']' | 'and' |
                                    // 'cast' | 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' | 'idiv' |
                                    // 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' | 'return' |
                                    // 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      break;
    case 46:                        // 'array'
    case 79:                        // 'map'
      lookahead2W(36);              // S^WS | EOF | '!' | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' | '//' | ':' |
                                    // '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' | ']' | 'and' | 'cast' |
                                    // 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' | 'idiv' |
                                    // 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' | 'return' |
                                    // 'satisfies' | 'to' | 'treat' | 'union' | '{' | '|' | '||' | '}'
      break;
    case 43:                        // 'ancestor'
    case 44:                        // 'ancestor-or-self'
    case 51:                        // 'child'
    case 53:                        // 'descendant'
    case 54:                        // 'descendant-or-self'
    case 63:                        // 'following'
    case 64:                        // 'following-sibling'
    case 81:                        // 'namespace'
    case 87:                        // 'parent'
    case 88:                        // 'preceding'
    case 89:                        // 'preceding-sibling'
    case 95:                        // 'self'
      lookahead2W(41);              // S^WS | EOF | '!' | '!=' | '#' | '(' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' |
                                    // '//' | ':' | '::' | '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' |
                                    // ']' | 'and' | 'cast' | 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' |
                                    // 'gt' | 'idiv' | 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' |
                                    // 'or' | 'return' | 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      break;
    case 5:                         // URIQualifiedName
    case 7:                         // QName^Token
    case 45:                        // 'and'
    case 49:                        // 'cast'
    case 50:                        // 'castable'
    case 55:                        // 'div'
    case 58:                        // 'else'
    case 60:                        // 'eq'
    case 61:                        // 'every'
    case 62:                        // 'except'
    case 65:                        // 'for'
    case 67:                        // 'ge'
    case 68:                        // 'gt'
    case 69:                        // 'idiv'
    case 72:                        // 'instance'
    case 73:                        // 'intersect'
    case 74:                        // 'is'
    case 76:                        // 'le'
    case 77:                        // 'let'
    case 78:                        // 'lt'
    case 80:                        // 'mod'
    case 83:                        // 'ne'
    case 86:                        // 'or'
    case 91:                        // 'return'
    case 92:                        // 'satisfies'
    case 96:                        // 'some'
    case 100:                       // 'to'
    case 101:                       // 'treat'
    case 103:                       // 'union'
      lookahead2W(37);              // S^WS | EOF | '!' | '!=' | '#' | '(' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' |
                                    // '//' | ':' | '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' | ']' |
                                    // 'and' | 'cast' | 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' |
                                    // 'idiv' | 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' |
                                    // 'return' | 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 1:                         // IntegerLiteral
    case 2:                         // DecimalLiteral
    case 3:                         // DoubleLiteral
    case 4:                         // StringLiteral
    case 15:                        // '$'
    case 16:                        // '('
    case 23:                        // '.'
    case 39:                        // '?'
    case 41:                        // '['
    case 1797:                      // URIQualifiedName '#'
    case 1799:                      // QName^Token '#'
    case 1835:                      // 'ancestor' '#'
    case 1836:                      // 'ancestor-or-self' '#'
    case 1837:                      // 'and' '#'
    case 1841:                      // 'cast' '#'
    case 1842:                      // 'castable' '#'
    case 1843:                      // 'child' '#'
    case 1845:                      // 'descendant' '#'
    case 1846:                      // 'descendant-or-self' '#'
    case 1847:                      // 'div' '#'
    case 1850:                      // 'else' '#'
    case 1852:                      // 'eq' '#'
    case 1853:                      // 'every' '#'
    case 1854:                      // 'except' '#'
    case 1855:                      // 'following' '#'
    case 1856:                      // 'following-sibling' '#'
    case 1857:                      // 'for' '#'
    case 1859:                      // 'ge' '#'
    case 1860:                      // 'gt' '#'
    case 1861:                      // 'idiv' '#'
    case 1864:                      // 'instance' '#'
    case 1865:                      // 'intersect' '#'
    case 1866:                      // 'is' '#'
    case 1868:                      // 'le' '#'
    case 1869:                      // 'let' '#'
    case 1870:                      // 'lt' '#'
    case 1872:                      // 'mod' '#'
    case 1873:                      // 'namespace' '#'
    case 1875:                      // 'ne' '#'
    case 1878:                      // 'or' '#'
    case 1879:                      // 'parent' '#'
    case 1880:                      // 'preceding' '#'
    case 1881:                      // 'preceding-sibling' '#'
    case 1883:                      // 'return' '#'
    case 1884:                      // 'satisfies' '#'
    case 1887:                      // 'self' '#'
    case 1888:                      // 'some' '#'
    case 1892:                      // 'to' '#'
    case 1893:                      // 'treat' '#'
    case 1895:                      // 'union' '#'
    case 2053:                      // URIQualifiedName '('
    case 2055:                      // QName^Token '('
    case 2091:                      // 'ancestor' '('
    case 2092:                      // 'ancestor-or-self' '('
    case 2093:                      // 'and' '('
    case 2097:                      // 'cast' '('
    case 2098:                      // 'castable' '('
    case 2099:                      // 'child' '('
    case 2101:                      // 'descendant' '('
    case 2102:                      // 'descendant-or-self' '('
    case 2103:                      // 'div' '('
    case 2106:                      // 'else' '('
    case 2108:                      // 'eq' '('
    case 2109:                      // 'every' '('
    case 2110:                      // 'except' '('
    case 2111:                      // 'following' '('
    case 2112:                      // 'following-sibling' '('
    case 2113:                      // 'for' '('
    case 2114:                      // 'function' '('
    case 2115:                      // 'ge' '('
    case 2116:                      // 'gt' '('
    case 2117:                      // 'idiv' '('
    case 2120:                      // 'instance' '('
    case 2121:                      // 'intersect' '('
    case 2122:                      // 'is' '('
    case 2124:                      // 'le' '('
    case 2125:                      // 'let' '('
    case 2126:                      // 'lt' '('
    case 2128:                      // 'mod' '('
    case 2129:                      // 'namespace' '('
    case 2131:                      // 'ne' '('
    case 2134:                      // 'or' '('
    case 2135:                      // 'parent' '('
    case 2136:                      // 'preceding' '('
    case 2137:                      // 'preceding-sibling' '('
    case 2139:                      // 'return' '('
    case 2140:                      // 'satisfies' '('
    case 2143:                      // 'self' '('
    case 2144:                      // 'some' '('
    case 2148:                      // 'to' '('
    case 2149:                      // 'treat' '('
    case 2151:                      // 'union' '('
    case 13358:                     // 'array' '{'
    case 13391:                     // 'map' '{'
      parse_PostfixExpr();
      break;
    default:
      parse_AxisStep();
    }
    eventHandler.endNonterminal("StepExpr", e0);
  }

  private void parse_AxisStep()
  {
    eventHandler.startNonterminal("AxisStep", e0);
    switch (l1)
    {
    case 43:                        // 'ancestor'
    case 44:                        // 'ancestor-or-self'
    case 87:                        // 'parent'
    case 88:                        // 'preceding'
    case 89:                        // 'preceding-sibling'
      lookahead2W(35);              // S^WS | EOF | '!' | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' | '//' | ':' |
                                    // '::' | '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' | ']' | 'and' |
                                    // 'cast' | 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' | 'idiv' |
                                    // 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' | 'return' |
                                    // 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 24:                        // '..'
    case 3755:                      // 'ancestor' '::'
    case 3756:                      // 'ancestor-or-self' '::'
    case 3799:                      // 'parent' '::'
    case 3800:                      // 'preceding' '::'
    case 3801:                      // 'preceding-sibling' '::'
      parse_ReverseStep();
      break;
    default:
      parse_ForwardStep();
    }
    lookahead1W(33);                // S^WS | EOF | '!' | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' | '//' | ':' |
                                    // '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' | ']' | 'and' | 'cast' |
                                    // 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' | 'idiv' |
                                    // 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' | 'return' |
                                    // 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
    whitespace();
    parse_PredicateList();
    eventHandler.endNonterminal("AxisStep", e0);
  }

  private void parse_ForwardStep()
  {
    eventHandler.startNonterminal("ForwardStep", e0);
    switch (l1)
    {
    case 48:                        // 'attribute'
      lookahead2W(38);              // S^WS | EOF | '!' | '!=' | '(' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' | '//' |
                                    // ':' | '::' | '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' | ']' |
                                    // 'and' | 'cast' | 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' |
                                    // 'idiv' | 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' |
                                    // 'return' | 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      break;
    case 51:                        // 'child'
    case 53:                        // 'descendant'
    case 54:                        // 'descendant-or-self'
    case 63:                        // 'following'
    case 64:                        // 'following-sibling'
    case 81:                        // 'namespace'
    case 95:                        // 'self'
      lookahead2W(35);              // S^WS | EOF | '!' | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' | '//' | ':' |
                                    // '::' | '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' | ']' | 'and' |
                                    // 'cast' | 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' | 'idiv' |
                                    // 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' | 'return' |
                                    // 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 3760:                      // 'attribute' '::'
    case 3763:                      // 'child' '::'
    case 3765:                      // 'descendant' '::'
    case 3766:                      // 'descendant-or-self' '::'
    case 3775:                      // 'following' '::'
    case 3776:                      // 'following-sibling' '::'
    case 3793:                      // 'namespace' '::'
    case 3807:                      // 'self' '::'
      parse_ForwardAxis();
      lookahead1W(43);              // URIQualifiedName | QName^Token | S^WS | Wildcard | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_NodeTest();
      break;
    default:
      parse_AbbrevForwardStep();
    }
    eventHandler.endNonterminal("ForwardStep", e0);
  }

  private void parse_ForwardAxis()
  {
    eventHandler.startNonterminal("ForwardAxis", e0);
    switch (l1)
    {
    case 51:                        // 'child'
      consume(51);                  // 'child'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    case 53:                        // 'descendant'
      consume(53);                  // 'descendant'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    case 48:                        // 'attribute'
      consume(48);                  // 'attribute'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    case 95:                        // 'self'
      consume(95);                  // 'self'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    case 54:                        // 'descendant-or-self'
      consume(54);                  // 'descendant-or-self'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    case 64:                        // 'following-sibling'
      consume(64);                  // 'following-sibling'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    case 63:                        // 'following'
      consume(63);                  // 'following'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    default:
      consume(81);                  // 'namespace'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
    }
    eventHandler.endNonterminal("ForwardAxis", e0);
  }

  private void parse_AbbrevForwardStep()
  {
    eventHandler.startNonterminal("AbbrevForwardStep", e0);
    if (l1 == 40)                   // '@'
    {
      consume(40);                  // '@'
    }
    lookahead1W(43);                // URIQualifiedName | QName^Token | S^WS | Wildcard | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_NodeTest();
    eventHandler.endNonterminal("AbbrevForwardStep", e0);
  }

  private void parse_ReverseStep()
  {
    eventHandler.startNonterminal("ReverseStep", e0);
    switch (l1)
    {
    case 24:                        // '..'
      parse_AbbrevReverseStep();
      break;
    default:
      parse_ReverseAxis();
      lookahead1W(43);              // URIQualifiedName | QName^Token | S^WS | Wildcard | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_NodeTest();
    }
    eventHandler.endNonterminal("ReverseStep", e0);
  }

  private void parse_ReverseAxis()
  {
    eventHandler.startNonterminal("ReverseAxis", e0);
    switch (l1)
    {
    case 87:                        // 'parent'
      consume(87);                  // 'parent'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    case 43:                        // 'ancestor'
      consume(43);                  // 'ancestor'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    case 89:                        // 'preceding-sibling'
      consume(89);                  // 'preceding-sibling'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    case 88:                        // 'preceding'
      consume(88);                  // 'preceding'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
      break;
    default:
      consume(44);                  // 'ancestor-or-self'
      lookahead1W(7);               // S^WS | '(:' | '::'
      consume(29);                  // '::'
    }
    eventHandler.endNonterminal("ReverseAxis", e0);
  }

  private void parse_AbbrevReverseStep()
  {
    eventHandler.startNonterminal("AbbrevReverseStep", e0);
    consume(24);                    // '..'
    eventHandler.endNonterminal("AbbrevReverseStep", e0);
  }

  private void parse_NodeTest()
  {
    eventHandler.startNonterminal("NodeTest", e0);
    switch (l1)
    {
    case 48:                        // 'attribute'
    case 52:                        // 'comment'
    case 56:                        // 'document-node'
    case 57:                        // 'element'
    case 82:                        // 'namespace-node'
    case 84:                        // 'node'
    case 90:                        // 'processing-instruction'
    case 93:                        // 'schema-attribute'
    case 94:                        // 'schema-element'
    case 98:                        // 'text'
      lookahead2W(34);              // S^WS | EOF | '!' | '!=' | '(' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' | '//' |
                                    // ':' | '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' | ']' | 'and' |
                                    // 'cast' | 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' | 'idiv' |
                                    // 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' | 'return' |
                                    // 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 2096:                      // 'attribute' '('
    case 2100:                      // 'comment' '('
    case 2104:                      // 'document-node' '('
    case 2105:                      // 'element' '('
    case 2130:                      // 'namespace-node' '('
    case 2132:                      // 'node' '('
    case 2138:                      // 'processing-instruction' '('
    case 2141:                      // 'schema-attribute' '('
    case 2142:                      // 'schema-element' '('
    case 2146:                      // 'text' '('
      parse_KindTest();
      break;
    default:
      parse_NameTest();
    }
    eventHandler.endNonterminal("NodeTest", e0);
  }

  private void parse_NameTest()
  {
    eventHandler.startNonterminal("NameTest", e0);
    switch (l1)
    {
    case 10:                        // Wildcard
      consume(10);                  // Wildcard
      break;
    default:
      parse_EQName();
    }
    eventHandler.endNonterminal("NameTest", e0);
  }

  private void parse_PostfixExpr()
  {
    eventHandler.startNonterminal("PostfixExpr", e0);
    parse_PrimaryExpr();
    for (;;)
    {
      lookahead1W(39);              // S^WS | EOF | '!' | '!=' | '(' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' | '//' |
                                    // ':' | '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '?' | '[' | ']' |
                                    // 'and' | 'cast' | 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' |
                                    // 'idiv' | 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' |
                                    // 'return' | 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      if (l1 != 16                  // '('
       && l1 != 39                  // '?'
       && l1 != 41)                 // '['
      {
        break;
      }
      switch (l1)
      {
      case 41:                      // '['
        whitespace();
        parse_Predicate();
        break;
      case 16:                      // '('
        whitespace();
        parse_ArgumentList();
        break;
      default:
        whitespace();
        parse_Lookup();
      }
    }
    eventHandler.endNonterminal("PostfixExpr", e0);
  }

  private void parse_ArgumentList()
  {
    eventHandler.startNonterminal("ArgumentList", e0);
    consume(16);                    // '('
    lookahead1W(54);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | ')' | '+' |
                                    // '-' | '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    if (l1 != 18)                   // ')'
    {
      whitespace();
      parse_Argument();
      for (;;)
      {
        lookahead1W(17);            // S^WS | '(:' | ')' | ','
        if (l1 != 21)               // ','
        {
          break;
        }
        consume(21);                // ','
        lookahead1W(53);            // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
        whitespace();
        parse_Argument();
      }
    }
    consume(18);                    // ')'
    eventHandler.endNonterminal("ArgumentList", e0);
  }

  private void parse_PredicateList()
  {
    eventHandler.startNonterminal("PredicateList", e0);
    for (;;)
    {
      lookahead1W(33);              // S^WS | EOF | '!' | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | '/' | '//' | ':' |
                                    // '<' | '<<' | '<=' | '=' | '=>' | '>' | '>=' | '>>' | '[' | ']' | 'and' | 'cast' |
                                    // 'castable' | 'div' | 'else' | 'eq' | 'except' | 'ge' | 'gt' | 'idiv' |
                                    // 'instance' | 'intersect' | 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' | 'return' |
                                    // 'satisfies' | 'to' | 'treat' | 'union' | '|' | '||' | '}'
      if (l1 != 41)                 // '['
      {
        break;
      }
      whitespace();
      parse_Predicate();
    }
    eventHandler.endNonterminal("PredicateList", e0);
  }

  private void parse_Predicate()
  {
    eventHandler.startNonterminal("Predicate", e0);
    consume(41);                    // '['
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_Expr();
    consume(42);                    // ']'
    eventHandler.endNonterminal("Predicate", e0);
  }

  private void parse_Lookup()
  {
    eventHandler.startNonterminal("Lookup", e0);
    consume(39);                    // '?'
    lookahead1W(23);                // IntegerLiteral | NCName | S^WS | '(' | '(:' | '*'
    whitespace();
    parse_KeySpecifier();
    eventHandler.endNonterminal("Lookup", e0);
  }

  private void parse_KeySpecifier()
  {
    eventHandler.startNonterminal("KeySpecifier", e0);
    switch (l1)
    {
    case 6:                         // NCName
      consume(6);                   // NCName
      break;
    case 1:                         // IntegerLiteral
      consume(1);                   // IntegerLiteral
      break;
    case 16:                        // '('
      parse_ParenthesizedExpr();
      break;
    default:
      consume(19);                  // '*'
    }
    eventHandler.endNonterminal("KeySpecifier", e0);
  }

  private void parse_ArrowFunctionSpecifier()
  {
    eventHandler.startNonterminal("ArrowFunctionSpecifier", e0);
    switch (l1)
    {
    case 15:                        // '$'
      parse_VarRef();
      break;
    case 16:                        // '('
      parse_ParenthesizedExpr();
      break;
    default:
      parse_EQName();
    }
    eventHandler.endNonterminal("ArrowFunctionSpecifier", e0);
  }

  private void parse_PrimaryExpr()
  {
    eventHandler.startNonterminal("PrimaryExpr", e0);
    switch (l1)
    {
    case 5:                         // URIQualifiedName
    case 7:                         // QName^Token
    case 43:                        // 'ancestor'
    case 44:                        // 'ancestor-or-self'
    case 45:                        // 'and'
    case 49:                        // 'cast'
    case 50:                        // 'castable'
    case 51:                        // 'child'
    case 53:                        // 'descendant'
    case 54:                        // 'descendant-or-self'
    case 55:                        // 'div'
    case 58:                        // 'else'
    case 60:                        // 'eq'
    case 61:                        // 'every'
    case 62:                        // 'except'
    case 63:                        // 'following'
    case 64:                        // 'following-sibling'
    case 65:                        // 'for'
    case 67:                        // 'ge'
    case 68:                        // 'gt'
    case 69:                        // 'idiv'
    case 72:                        // 'instance'
    case 73:                        // 'intersect'
    case 74:                        // 'is'
    case 76:                        // 'le'
    case 77:                        // 'let'
    case 78:                        // 'lt'
    case 80:                        // 'mod'
    case 81:                        // 'namespace'
    case 83:                        // 'ne'
    case 86:                        // 'or'
    case 87:                        // 'parent'
    case 88:                        // 'preceding'
    case 89:                        // 'preceding-sibling'
    case 91:                        // 'return'
    case 92:                        // 'satisfies'
    case 95:                        // 'self'
    case 96:                        // 'some'
    case 100:                       // 'to'
    case 101:                       // 'treat'
    case 103:                       // 'union'
      lookahead2W(15);              // S^WS | '#' | '(' | '(:'
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 1:                         // IntegerLiteral
    case 2:                         // DecimalLiteral
    case 3:                         // DoubleLiteral
    case 4:                         // StringLiteral
      parse_Literal();
      break;
    case 15:                        // '$'
      parse_VarRef();
      break;
    case 16:                        // '('
      parse_ParenthesizedExpr();
      break;
    case 23:                        // '.'
      parse_ContextItemExpr();
      break;
    case 2053:                      // URIQualifiedName '('
    case 2055:                      // QName^Token '('
    case 2091:                      // 'ancestor' '('
    case 2092:                      // 'ancestor-or-self' '('
    case 2093:                      // 'and' '('
    case 2097:                      // 'cast' '('
    case 2098:                      // 'castable' '('
    case 2099:                      // 'child' '('
    case 2101:                      // 'descendant' '('
    case 2102:                      // 'descendant-or-self' '('
    case 2103:                      // 'div' '('
    case 2106:                      // 'else' '('
    case 2108:                      // 'eq' '('
    case 2109:                      // 'every' '('
    case 2110:                      // 'except' '('
    case 2111:                      // 'following' '('
    case 2112:                      // 'following-sibling' '('
    case 2113:                      // 'for' '('
    case 2115:                      // 'ge' '('
    case 2116:                      // 'gt' '('
    case 2117:                      // 'idiv' '('
    case 2120:                      // 'instance' '('
    case 2121:                      // 'intersect' '('
    case 2122:                      // 'is' '('
    case 2124:                      // 'le' '('
    case 2125:                      // 'let' '('
    case 2126:                      // 'lt' '('
    case 2128:                      // 'mod' '('
    case 2129:                      // 'namespace' '('
    case 2131:                      // 'ne' '('
    case 2134:                      // 'or' '('
    case 2135:                      // 'parent' '('
    case 2136:                      // 'preceding' '('
    case 2137:                      // 'preceding-sibling' '('
    case 2139:                      // 'return' '('
    case 2140:                      // 'satisfies' '('
    case 2143:                      // 'self' '('
    case 2144:                      // 'some' '('
    case 2148:                      // 'to' '('
    case 2149:                      // 'treat' '('
    case 2151:                      // 'union' '('
      parse_FunctionCall();
      break;
    case 79:                        // 'map'
      parse_MapConstructor();
      break;
    case 41:                        // '['
    case 46:                        // 'array'
      parse_ArrayConstructor();
      break;
    case 39:                        // '?'
      parse_UnaryLookup();
      break;
    default:
      parse_FunctionItemExpr();
    }
    eventHandler.endNonterminal("PrimaryExpr", e0);
  }

  private void parse_Literal()
  {
    eventHandler.startNonterminal("Literal", e0);
    switch (l1)
    {
    case 4:                         // StringLiteral
      consume(4);                   // StringLiteral
      break;
    default:
      parse_NumericLiteral();
    }
    eventHandler.endNonterminal("Literal", e0);
  }

  private void parse_NumericLiteral()
  {
    eventHandler.startNonterminal("NumericLiteral", e0);
    switch (l1)
    {
    case 1:                         // IntegerLiteral
      consume(1);                   // IntegerLiteral
      break;
    case 2:                         // DecimalLiteral
      consume(2);                   // DecimalLiteral
      break;
    default:
      consume(3);                   // DoubleLiteral
    }
    eventHandler.endNonterminal("NumericLiteral", e0);
  }

  private void parse_VarRef()
  {
    eventHandler.startNonterminal("VarRef", e0);
    consume(15);                    // '$'
    lookahead1W(42);                // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_VarName();
    eventHandler.endNonterminal("VarRef", e0);
  }

  private void parse_VarName()
  {
    eventHandler.startNonterminal("VarName", e0);
    parse_EQName();
    eventHandler.endNonterminal("VarName", e0);
  }

  private void parse_ParenthesizedExpr()
  {
    eventHandler.startNonterminal("ParenthesizedExpr", e0);
    consume(16);                    // '('
    lookahead1W(54);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | ')' | '+' |
                                    // '-' | '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    if (l1 != 18)                   // ')'
    {
      whitespace();
      parse_Expr();
    }
    consume(18);                    // ')'
    eventHandler.endNonterminal("ParenthesizedExpr", e0);
  }

  private void parse_ContextItemExpr()
  {
    eventHandler.startNonterminal("ContextItemExpr", e0);
    consume(23);                    // '.'
    eventHandler.endNonterminal("ContextItemExpr", e0);
  }

  private void parse_FunctionCall()
  {
    eventHandler.startNonterminal("FunctionCall", e0);
    parse_FunctionEQName();
    lookahead1W(3);                 // S^WS | '(' | '(:'
    whitespace();
    parse_ArgumentList();
    eventHandler.endNonterminal("FunctionCall", e0);
  }

  private void parse_Argument()
  {
    eventHandler.startNonterminal("Argument", e0);
    switch (l1)
    {
    case 39:                        // '?'
      lookahead2W(24);              // IntegerLiteral | NCName | S^WS | '(' | '(:' | ')' | '*' | ','
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 2343:                      // '?' ')'
    case 2727:                      // '?' ','
      parse_ArgumentPlaceholder();
      break;
    default:
      parse_ExprSingle();
    }
    eventHandler.endNonterminal("Argument", e0);
  }

  private void parse_ArgumentPlaceholder()
  {
    eventHandler.startNonterminal("ArgumentPlaceholder", e0);
    consume(39);                    // '?'
    eventHandler.endNonterminal("ArgumentPlaceholder", e0);
  }

  private void parse_FunctionItemExpr()
  {
    eventHandler.startNonterminal("FunctionItemExpr", e0);
    switch (l1)
    {
    case 66:                        // 'function'
      parse_InlineFunctionExpr();
      break;
    default:
      parse_NamedFunctionRef();
    }
    eventHandler.endNonterminal("FunctionItemExpr", e0);
  }

  private void parse_NamedFunctionRef()
  {
    eventHandler.startNonterminal("NamedFunctionRef", e0);
    parse_FunctionEQName();
    lookahead1W(1);                 // S^WS | '#' | '(:'
    consume(14);                    // '#'
    lookahead1W(0);                 // IntegerLiteral | S^WS | '(:'
    consume(1);                     // IntegerLiteral
    eventHandler.endNonterminal("NamedFunctionRef", e0);
  }

  private void parse_InlineFunctionExpr()
  {
    eventHandler.startNonterminal("InlineFunctionExpr", e0);
    consume(66);                    // 'function'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(16);                // S^WS | '$' | '(:' | ')'
    if (l1 == 15)                   // '$'
    {
      whitespace();
      parse_ParamList();
    }
    consume(18);                    // ')'
    lookahead1W(19);                // S^WS | '(:' | 'as' | '{'
    if (l1 == 47)                   // 'as'
    {
      consume(47);                  // 'as'
      lookahead1W(44);              // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
      whitespace();
      parse_SequenceType();
    }
    lookahead1W(13);                // S^WS | '(:' | '{'
    whitespace();
    parse_FunctionBody();
    eventHandler.endNonterminal("InlineFunctionExpr", e0);
  }

  private void parse_MapConstructor()
  {
    eventHandler.startNonterminal("MapConstructor", e0);
    consume(79);                    // 'map'
    lookahead1W(13);                // S^WS | '(:' | '{'
    consume(104);                   // '{'
    lookahead1W(56);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union' | '}'
    if (l1 != 107)                  // '}'
    {
      whitespace();
      parse_MapConstructorEntry();
      for (;;)
      {
        if (l1 != 21)               // ','
        {
          break;
        }
        consume(21);                // ','
        lookahead1W(53);            // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
        whitespace();
        parse_MapConstructorEntry();
      }
    }
    consume(107);                   // '}'
    eventHandler.endNonterminal("MapConstructor", e0);
  }

  private void parse_MapConstructorEntry()
  {
    eventHandler.startNonterminal("MapConstructorEntry", e0);
    parse_MapKeyExpr();
    consume(27);                    // ':'
    lookahead1W(53);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_MapValueExpr();
    eventHandler.endNonterminal("MapConstructorEntry", e0);
  }

  private void parse_MapKeyExpr()
  {
    eventHandler.startNonterminal("MapKeyExpr", e0);
    parse_ExprSingle();
    eventHandler.endNonterminal("MapKeyExpr", e0);
  }

  private void parse_MapValueExpr()
  {
    eventHandler.startNonterminal("MapValueExpr", e0);
    parse_ExprSingle();
    eventHandler.endNonterminal("MapValueExpr", e0);
  }

  private void parse_ArrayConstructor()
  {
    eventHandler.startNonterminal("ArrayConstructor", e0);
    switch (l1)
    {
    case 41:                        // '['
      parse_SquareArrayConstructor();
      break;
    default:
      parse_CurlyArrayConstructor();
    }
    eventHandler.endNonterminal("ArrayConstructor", e0);
  }

  private void parse_SquareArrayConstructor()
  {
    eventHandler.startNonterminal("SquareArrayConstructor", e0);
    consume(41);                    // '['
    lookahead1W(55);                // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | ']' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    if (l1 != 42)                   // ']'
    {
      whitespace();
      parse_ExprSingle();
      for (;;)
      {
        if (l1 != 21)               // ','
        {
          break;
        }
        consume(21);                // ','
        lookahead1W(53);            // IntegerLiteral | DecimalLiteral | DoubleLiteral | StringLiteral |
                                    // URIQualifiedName | QName^Token | S^WS | Wildcard | '$' | '(' | '(:' | '+' | '-' |
                                    // '.' | '..' | '/' | '//' | '?' | '@' | '[' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
        whitespace();
        parse_ExprSingle();
      }
    }
    consume(42);                    // ']'
    eventHandler.endNonterminal("SquareArrayConstructor", e0);
  }

  private void parse_CurlyArrayConstructor()
  {
    eventHandler.startNonterminal("CurlyArrayConstructor", e0);
    consume(46);                    // 'array'
    lookahead1W(13);                // S^WS | '(:' | '{'
    whitespace();
    parse_EnclosedExpr();
    eventHandler.endNonterminal("CurlyArrayConstructor", e0);
  }

  private void parse_UnaryLookup()
  {
    eventHandler.startNonterminal("UnaryLookup", e0);
    consume(39);                    // '?'
    lookahead1W(23);                // IntegerLiteral | NCName | S^WS | '(' | '(:' | '*'
    whitespace();
    parse_KeySpecifier();
    eventHandler.endNonterminal("UnaryLookup", e0);
  }

  private void parse_SingleType()
  {
    eventHandler.startNonterminal("SingleType", e0);
    parse_SimpleTypeName();
    lookahead1W(31);                // S^WS | EOF | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | ':' | '<' | '<<' |
                                    // '<=' | '=' | '>' | '>=' | '>>' | '?' | ']' | 'and' | 'castable' | 'div' |
                                    // 'else' | 'eq' | 'except' | 'ge' | 'gt' | 'idiv' | 'instance' | 'intersect' |
                                    // 'is' | 'le' | 'lt' | 'mod' | 'ne' | 'or' | 'return' | 'satisfies' | 'to' |
                                    // 'treat' | 'union' | '|' | '||' | '}'
    if (l1 == 39)                   // '?'
    {
      consume(39);                  // '?'
    }
    eventHandler.endNonterminal("SingleType", e0);
  }

  private void parse_TypeDeclaration()
  {
    eventHandler.startNonterminal("TypeDeclaration", e0);
    consume(47);                    // 'as'
    lookahead1W(44);                // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_SequenceType();
    eventHandler.endNonterminal("TypeDeclaration", e0);
  }

  private void parse_SequenceType()
  {
    eventHandler.startNonterminal("SequenceType", e0);
    switch (l1)
    {
    case 59:                        // 'empty-sequence'
      lookahead2W(30);              // S^WS | EOF | '!=' | '(' | '(:' | ')' | '*' | '+' | ',' | '-' | ':' | '<' | '<<' |
                                    // '<=' | '=' | '>' | '>=' | '>>' | '?' | ']' | 'and' | 'div' | 'else' | 'eq' |
                                    // 'except' | 'ge' | 'gt' | 'idiv' | 'instance' | 'intersect' | 'is' | 'le' | 'lt' |
                                    // 'mod' | 'ne' | 'or' | 'return' | 'satisfies' | 'to' | 'union' | '{' | '|' |
                                    // '||' | '}'
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 2107:                      // 'empty-sequence' '('
      consume(59);                  // 'empty-sequence'
      lookahead1W(3);               // S^WS | '(' | '(:'
      consume(16);                  // '('
      lookahead1W(4);               // S^WS | '(:' | ')'
      consume(18);                  // ')'
      break;
    default:
      parse_ItemType();
      lookahead1W(28);              // S^WS | EOF | '!=' | '(:' | ')' | '*' | '+' | ',' | '-' | ':' | '<' | '<<' |
                                    // '<=' | '=' | '>' | '>=' | '>>' | '?' | ']' | 'and' | 'div' | 'else' | 'eq' |
                                    // 'except' | 'ge' | 'gt' | 'idiv' | 'instance' | 'intersect' | 'is' | 'le' | 'lt' |
                                    // 'mod' | 'ne' | 'or' | 'return' | 'satisfies' | 'to' | 'union' | '{' | '|' |
                                    // '||' | '}'
      switch (l1)
      {
      case 19:                      // '*'
      case 20:                      // '+'
      case 39:                      // '?'
        whitespace();
        parse_OccurrenceIndicator();
        break;
      default:
        break;
      }
    }
    eventHandler.endNonterminal("SequenceType", e0);
  }

  private void parse_OccurrenceIndicator()
  {
    eventHandler.startNonterminal("OccurrenceIndicator", e0);
    switch (l1)
    {
    case 39:                        // '?'
      consume(39);                  // '?'
      break;
    case 19:                        // '*'
      consume(19);                  // '*'
      break;
    default:
      consume(20);                  // '+'
    }
    eventHandler.endNonterminal("OccurrenceIndicator", e0);
  }

  private void parse_ItemType()
  {
    eventHandler.startNonterminal("ItemType", e0);
    switch (l1)
    {
    case 46:                        // 'array'
    case 48:                        // 'attribute'
    case 52:                        // 'comment'
    case 56:                        // 'document-node'
    case 57:                        // 'element'
    case 66:                        // 'function'
    case 75:                        // 'item'
    case 79:                        // 'map'
    case 82:                        // 'namespace-node'
    case 84:                        // 'node'
    case 90:                        // 'processing-instruction'
    case 93:                        // 'schema-attribute'
    case 94:                        // 'schema-element'
    case 98:                        // 'text'
      lookahead2W(30);              // S^WS | EOF | '!=' | '(' | '(:' | ')' | '*' | '+' | ',' | '-' | ':' | '<' | '<<' |
                                    // '<=' | '=' | '>' | '>=' | '>>' | '?' | ']' | 'and' | 'div' | 'else' | 'eq' |
                                    // 'except' | 'ge' | 'gt' | 'idiv' | 'instance' | 'intersect' | 'is' | 'le' | 'lt' |
                                    // 'mod' | 'ne' | 'or' | 'return' | 'satisfies' | 'to' | 'union' | '{' | '|' |
                                    // '||' | '}'
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 2096:                      // 'attribute' '('
    case 2100:                      // 'comment' '('
    case 2104:                      // 'document-node' '('
    case 2105:                      // 'element' '('
    case 2130:                      // 'namespace-node' '('
    case 2132:                      // 'node' '('
    case 2138:                      // 'processing-instruction' '('
    case 2141:                      // 'schema-attribute' '('
    case 2142:                      // 'schema-element' '('
    case 2146:                      // 'text' '('
      parse_KindTest();
      break;
    case 2123:                      // 'item' '('
      consume(75);                  // 'item'
      lookahead1W(3);               // S^WS | '(' | '(:'
      consume(16);                  // '('
      lookahead1W(4);               // S^WS | '(:' | ')'
      consume(18);                  // ')'
      break;
    case 2114:                      // 'function' '('
      parse_FunctionTest();
      break;
    case 2127:                      // 'map' '('
      parse_MapTest();
      break;
    case 2094:                      // 'array' '('
      parse_ArrayTest();
      break;
    case 16:                        // '('
      parse_ParenthesizedItemType();
      break;
    default:
      parse_AtomicOrUnionType();
    }
    eventHandler.endNonterminal("ItemType", e0);
  }

  private void parse_AtomicOrUnionType()
  {
    eventHandler.startNonterminal("AtomicOrUnionType", e0);
    parse_EQName();
    eventHandler.endNonterminal("AtomicOrUnionType", e0);
  }

  private void parse_KindTest()
  {
    eventHandler.startNonterminal("KindTest", e0);
    switch (l1)
    {
    case 56:                        // 'document-node'
      parse_DocumentTest();
      break;
    case 57:                        // 'element'
      parse_ElementTest();
      break;
    case 48:                        // 'attribute'
      parse_AttributeTest();
      break;
    case 94:                        // 'schema-element'
      parse_SchemaElementTest();
      break;
    case 93:                        // 'schema-attribute'
      parse_SchemaAttributeTest();
      break;
    case 90:                        // 'processing-instruction'
      parse_PITest();
      break;
    case 52:                        // 'comment'
      parse_CommentTest();
      break;
    case 98:                        // 'text'
      parse_TextTest();
      break;
    case 82:                        // 'namespace-node'
      parse_NamespaceNodeTest();
      break;
    default:
      parse_AnyKindTest();
    }
    eventHandler.endNonterminal("KindTest", e0);
  }

  private void parse_AnyKindTest()
  {
    eventHandler.startNonterminal("AnyKindTest", e0);
    consume(84);                    // 'node'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("AnyKindTest", e0);
  }

  private void parse_DocumentTest()
  {
    eventHandler.startNonterminal("DocumentTest", e0);
    consume(56);                    // 'document-node'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(22);                // S^WS | '(:' | ')' | 'element' | 'schema-element'
    if (l1 != 18)                   // ')'
    {
      switch (l1)
      {
      case 57:                      // 'element'
        whitespace();
        parse_ElementTest();
        break;
      default:
        whitespace();
        parse_SchemaElementTest();
      }
    }
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("DocumentTest", e0);
  }

  private void parse_TextTest()
  {
    eventHandler.startNonterminal("TextTest", e0);
    consume(98);                    // 'text'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("TextTest", e0);
  }

  private void parse_CommentTest()
  {
    eventHandler.startNonterminal("CommentTest", e0);
    consume(52);                    // 'comment'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("CommentTest", e0);
  }

  private void parse_NamespaceNodeTest()
  {
    eventHandler.startNonterminal("NamespaceNodeTest", e0);
    consume(82);                    // 'namespace-node'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("NamespaceNodeTest", e0);
  }

  private void parse_PITest()
  {
    eventHandler.startNonterminal("PITest", e0);
    consume(90);                    // 'processing-instruction'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(20);                // StringLiteral | NCName | S^WS | '(:' | ')'
    if (l1 != 18)                   // ')'
    {
      switch (l1)
      {
      case 6:                       // NCName
        consume(6);                 // NCName
        break;
      default:
        consume(4);                 // StringLiteral
      }
    }
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("PITest", e0);
  }

  private void parse_AttributeTest()
  {
    eventHandler.startNonterminal("AttributeTest", e0);
    consume(48);                    // 'attribute'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(49);                // URIQualifiedName | QName^Token | S^WS | '(:' | ')' | '*' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    if (l1 != 18)                   // ')'
    {
      whitespace();
      parse_AttribNameOrWildcard();
      lookahead1W(17);              // S^WS | '(:' | ')' | ','
      if (l1 == 21)                 // ','
      {
        consume(21);                // ','
        lookahead1W(42);            // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
        whitespace();
        parse_TypeName();
      }
    }
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("AttributeTest", e0);
  }

  private void parse_AttribNameOrWildcard()
  {
    eventHandler.startNonterminal("AttribNameOrWildcard", e0);
    switch (l1)
    {
    case 19:                        // '*'
      consume(19);                  // '*'
      break;
    default:
      parse_AttributeName();
    }
    eventHandler.endNonterminal("AttribNameOrWildcard", e0);
  }

  private void parse_SchemaAttributeTest()
  {
    eventHandler.startNonterminal("SchemaAttributeTest", e0);
    consume(93);                    // 'schema-attribute'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(42);                // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_AttributeDeclaration();
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("SchemaAttributeTest", e0);
  }

  private void parse_AttributeDeclaration()
  {
    eventHandler.startNonterminal("AttributeDeclaration", e0);
    parse_AttributeName();
    eventHandler.endNonterminal("AttributeDeclaration", e0);
  }

  private void parse_ElementTest()
  {
    eventHandler.startNonterminal("ElementTest", e0);
    consume(57);                    // 'element'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(49);                // URIQualifiedName | QName^Token | S^WS | '(:' | ')' | '*' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    if (l1 != 18)                   // ')'
    {
      whitespace();
      parse_ElementNameOrWildcard();
      lookahead1W(17);              // S^WS | '(:' | ')' | ','
      if (l1 == 21)                 // ','
      {
        consume(21);                // ','
        lookahead1W(42);            // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
        whitespace();
        parse_TypeName();
        lookahead1W(18);            // S^WS | '(:' | ')' | '?'
        if (l1 == 39)               // '?'
        {
          consume(39);              // '?'
        }
      }
    }
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("ElementTest", e0);
  }

  private void parse_ElementNameOrWildcard()
  {
    eventHandler.startNonterminal("ElementNameOrWildcard", e0);
    switch (l1)
    {
    case 19:                        // '*'
      consume(19);                  // '*'
      break;
    default:
      parse_ElementName();
    }
    eventHandler.endNonterminal("ElementNameOrWildcard", e0);
  }

  private void parse_SchemaElementTest()
  {
    eventHandler.startNonterminal("SchemaElementTest", e0);
    consume(94);                    // 'schema-element'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(42);                // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_ElementDeclaration();
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("SchemaElementTest", e0);
  }

  private void parse_ElementDeclaration()
  {
    eventHandler.startNonterminal("ElementDeclaration", e0);
    parse_ElementName();
    eventHandler.endNonterminal("ElementDeclaration", e0);
  }

  private void parse_AttributeName()
  {
    eventHandler.startNonterminal("AttributeName", e0);
    parse_EQName();
    eventHandler.endNonterminal("AttributeName", e0);
  }

  private void parse_ElementName()
  {
    eventHandler.startNonterminal("ElementName", e0);
    parse_EQName();
    eventHandler.endNonterminal("ElementName", e0);
  }

  private void parse_SimpleTypeName()
  {
    eventHandler.startNonterminal("SimpleTypeName", e0);
    parse_TypeName();
    eventHandler.endNonterminal("SimpleTypeName", e0);
  }

  private void parse_TypeName()
  {
    eventHandler.startNonterminal("TypeName", e0);
    parse_EQName();
    eventHandler.endNonterminal("TypeName", e0);
  }

  private void parse_FunctionTest()
  {
    eventHandler.startNonterminal("FunctionTest", e0);
    switch (l1)
    {
    case 66:                        // 'function'
      lookahead2W(3);               // S^WS | '(' | '(:'
      switch (lk)
      {
      case 2114:                    // 'function' '('
        lookahead3W(50);            // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | ')' | '*' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
        break;
      }
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 313410:                    // 'function' '(' '*'
      parse_AnyFunctionTest();
      break;
    default:
      parse_TypedFunctionTest();
    }
    eventHandler.endNonterminal("FunctionTest", e0);
  }

  private void parse_AnyFunctionTest()
  {
    eventHandler.startNonterminal("AnyFunctionTest", e0);
    consume(66);                    // 'function'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(5);                 // S^WS | '(:' | '*'
    consume(19);                    // '*'
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("AnyFunctionTest", e0);
  }

  private void parse_TypedFunctionTest()
  {
    eventHandler.startNonterminal("TypedFunctionTest", e0);
    consume(66);                    // 'function'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(47);                // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | ')' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    if (l1 != 18)                   // ')'
    {
      whitespace();
      parse_SequenceType();
      for (;;)
      {
        lookahead1W(17);            // S^WS | '(:' | ')' | ','
        if (l1 != 21)               // ','
        {
          break;
        }
        consume(21);                // ','
        lookahead1W(44);            // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
        whitespace();
        parse_SequenceType();
      }
    }
    consume(18);                    // ')'
    lookahead1W(9);                 // S^WS | '(:' | 'as'
    consume(47);                    // 'as'
    lookahead1W(44);                // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_SequenceType();
    eventHandler.endNonterminal("TypedFunctionTest", e0);
  }

  private void parse_MapTest()
  {
    eventHandler.startNonterminal("MapTest", e0);
    switch (l1)
    {
    case 79:                        // 'map'
      lookahead2W(3);               // S^WS | '(' | '(:'
      switch (lk)
      {
      case 2127:                    // 'map' '('
        lookahead3W(45);            // URIQualifiedName | QName^Token | S^WS | '(:' | '*' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
        break;
      }
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 313423:                    // 'map' '(' '*'
      parse_AnyMapTest();
      break;
    default:
      parse_TypedMapTest();
    }
    eventHandler.endNonterminal("MapTest", e0);
  }

  private void parse_AnyMapTest()
  {
    eventHandler.startNonterminal("AnyMapTest", e0);
    consume(79);                    // 'map'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(5);                 // S^WS | '(:' | '*'
    consume(19);                    // '*'
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("AnyMapTest", e0);
  }

  private void parse_TypedMapTest()
  {
    eventHandler.startNonterminal("TypedMapTest", e0);
    consume(79);                    // 'map'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(42);                // URIQualifiedName | QName^Token | S^WS | '(:' | 'ancestor' | 'ancestor-or-self' |
                                    // 'and' | 'array' | 'attribute' | 'cast' | 'castable' | 'child' | 'comment' |
                                    // 'descendant' | 'descendant-or-self' | 'div' | 'document-node' | 'element' |
                                    // 'else' | 'empty-sequence' | 'eq' | 'every' | 'except' | 'following' |
                                    // 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' | 'idiv' | 'if' |
                                    // 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' | 'map' | 'mod' |
                                    // 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' | 'parent' | 'preceding' |
                                    // 'preceding-sibling' | 'processing-instruction' | 'return' | 'satisfies' |
                                    // 'schema-attribute' | 'schema-element' | 'self' | 'some' | 'switch' | 'text' |
                                    // 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_AtomicOrUnionType();
    lookahead1W(6);                 // S^WS | '(:' | ','
    consume(21);                    // ','
    lookahead1W(44);                // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_SequenceType();
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("TypedMapTest", e0);
  }

  private void parse_ArrayTest()
  {
    eventHandler.startNonterminal("ArrayTest", e0);
    switch (l1)
    {
    case 46:                        // 'array'
      lookahead2W(3);               // S^WS | '(' | '(:'
      switch (lk)
      {
      case 2094:                    // 'array' '('
        lookahead3W(48);            // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | '*' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
        break;
      }
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 313390:                    // 'array' '(' '*'
      parse_AnyArrayTest();
      break;
    default:
      parse_TypedArrayTest();
    }
    eventHandler.endNonterminal("ArrayTest", e0);
  }

  private void parse_AnyArrayTest()
  {
    eventHandler.startNonterminal("AnyArrayTest", e0);
    consume(46);                    // 'array'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(5);                 // S^WS | '(:' | '*'
    consume(19);                    // '*'
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("AnyArrayTest", e0);
  }

  private void parse_TypedArrayTest()
  {
    eventHandler.startNonterminal("TypedArrayTest", e0);
    consume(46);                    // 'array'
    lookahead1W(3);                 // S^WS | '(' | '(:'
    consume(16);                    // '('
    lookahead1W(44);                // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_SequenceType();
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("TypedArrayTest", e0);
  }

  private void parse_ParenthesizedItemType()
  {
    eventHandler.startNonterminal("ParenthesizedItemType", e0);
    consume(16);                    // '('
    lookahead1W(44);                // URIQualifiedName | QName^Token | S^WS | '(' | '(:' | 'ancestor' |
                                    // 'ancestor-or-self' | 'and' | 'array' | 'attribute' | 'cast' | 'castable' |
                                    // 'child' | 'comment' | 'descendant' | 'descendant-or-self' | 'div' |
                                    // 'document-node' | 'element' | 'else' | 'empty-sequence' | 'eq' | 'every' |
                                    // 'except' | 'following' | 'following-sibling' | 'for' | 'function' | 'ge' | 'gt' |
                                    // 'idiv' | 'if' | 'instance' | 'intersect' | 'is' | 'item' | 'le' | 'let' | 'lt' |
                                    // 'map' | 'mod' | 'namespace' | 'namespace-node' | 'ne' | 'node' | 'or' |
                                    // 'parent' | 'preceding' | 'preceding-sibling' | 'processing-instruction' |
                                    // 'return' | 'satisfies' | 'schema-attribute' | 'schema-element' | 'self' |
                                    // 'some' | 'switch' | 'text' | 'to' | 'treat' | 'typeswitch' | 'union'
    whitespace();
    parse_ItemType();
    lookahead1W(4);                 // S^WS | '(:' | ')'
    consume(18);                    // ')'
    eventHandler.endNonterminal("ParenthesizedItemType", e0);
  }

  private void parse_FunctionEQName()
  {
    eventHandler.startNonterminal("FunctionEQName", e0);
    switch (l1)
    {
    case 5:                         // URIQualifiedName
      consume(5);                   // URIQualifiedName
      break;
    default:
      parse_FunctionName();
    }
    eventHandler.endNonterminal("FunctionEQName", e0);
  }

  private void parse_EQName()
  {
    eventHandler.startNonterminal("EQName", e0);
    switch (l1)
    {
    case 5:                         // URIQualifiedName
      consume(5);                   // URIQualifiedName
      break;
    default:
      parse_QName();
    }
    eventHandler.endNonterminal("EQName", e0);
  }

  private void try_Whitespace()
  {
    switch (l1)
    {
    case 8:                         // S^WS
      consumeT(8);                  // S^WS
      break;
    default:
      try_Comment();
    }
  }

  private void try_Comment()
  {
    consumeT(17);                   // '(:'
    for (;;)
    {
      lookahead1(14);               // CommentContents | '(:' | ':)'
      if (l1 == 28)                 // ':)'
      {
        break;
      }
      switch (l1)
      {
      case 9:                       // CommentContents
        consumeT(9);                // CommentContents
        break;
      default:
        try_Comment();
      }
    }
    consumeT(28);                   // ':)'
  }

  private void parse_FunctionName()
  {
    eventHandler.startNonterminal("FunctionName", e0);
    switch (l1)
    {
    case 7:                         // QName^Token
      consume(7);                   // QName^Token
      break;
    case 43:                        // 'ancestor'
      consume(43);                  // 'ancestor'
      break;
    case 44:                        // 'ancestor-or-self'
      consume(44);                  // 'ancestor-or-self'
      break;
    case 45:                        // 'and'
      consume(45);                  // 'and'
      break;
    case 49:                        // 'cast'
      consume(49);                  // 'cast'
      break;
    case 50:                        // 'castable'
      consume(50);                  // 'castable'
      break;
    case 51:                        // 'child'
      consume(51);                  // 'child'
      break;
    case 53:                        // 'descendant'
      consume(53);                  // 'descendant'
      break;
    case 54:                        // 'descendant-or-self'
      consume(54);                  // 'descendant-or-self'
      break;
    case 55:                        // 'div'
      consume(55);                  // 'div'
      break;
    case 58:                        // 'else'
      consume(58);                  // 'else'
      break;
    case 60:                        // 'eq'
      consume(60);                  // 'eq'
      break;
    case 61:                        // 'every'
      consume(61);                  // 'every'
      break;
    case 62:                        // 'except'
      consume(62);                  // 'except'
      break;
    case 63:                        // 'following'
      consume(63);                  // 'following'
      break;
    case 64:                        // 'following-sibling'
      consume(64);                  // 'following-sibling'
      break;
    case 65:                        // 'for'
      consume(65);                  // 'for'
      break;
    case 67:                        // 'ge'
      consume(67);                  // 'ge'
      break;
    case 68:                        // 'gt'
      consume(68);                  // 'gt'
      break;
    case 69:                        // 'idiv'
      consume(69);                  // 'idiv'
      break;
    case 72:                        // 'instance'
      consume(72);                  // 'instance'
      break;
    case 73:                        // 'intersect'
      consume(73);                  // 'intersect'
      break;
    case 74:                        // 'is'
      consume(74);                  // 'is'
      break;
    case 76:                        // 'le'
      consume(76);                  // 'le'
      break;
    case 77:                        // 'let'
      consume(77);                  // 'let'
      break;
    case 78:                        // 'lt'
      consume(78);                  // 'lt'
      break;
    case 80:                        // 'mod'
      consume(80);                  // 'mod'
      break;
    case 81:                        // 'namespace'
      consume(81);                  // 'namespace'
      break;
    case 83:                        // 'ne'
      consume(83);                  // 'ne'
      break;
    case 86:                        // 'or'
      consume(86);                  // 'or'
      break;
    case 87:                        // 'parent'
      consume(87);                  // 'parent'
      break;
    case 88:                        // 'preceding'
      consume(88);                  // 'preceding'
      break;
    case 89:                        // 'preceding-sibling'
      consume(89);                  // 'preceding-sibling'
      break;
    case 91:                        // 'return'
      consume(91);                  // 'return'
      break;
    case 92:                        // 'satisfies'
      consume(92);                  // 'satisfies'
      break;
    case 95:                        // 'self'
      consume(95);                  // 'self'
      break;
    case 96:                        // 'some'
      consume(96);                  // 'some'
      break;
    case 100:                       // 'to'
      consume(100);                 // 'to'
      break;
    case 101:                       // 'treat'
      consume(101);                 // 'treat'
      break;
    default:
      consume(103);                 // 'union'
    }
    eventHandler.endNonterminal("FunctionName", e0);
  }

  private void parse_QName()
  {
    eventHandler.startNonterminal("QName", e0);
    switch (l1)
    {
    case 46:                        // 'array'
      consume(46);                  // 'array'
      break;
    case 48:                        // 'attribute'
      consume(48);                  // 'attribute'
      break;
    case 52:                        // 'comment'
      consume(52);                  // 'comment'
      break;
    case 56:                        // 'document-node'
      consume(56);                  // 'document-node'
      break;
    case 57:                        // 'element'
      consume(57);                  // 'element'
      break;
    case 59:                        // 'empty-sequence'
      consume(59);                  // 'empty-sequence'
      break;
    case 66:                        // 'function'
      consume(66);                  // 'function'
      break;
    case 70:                        // 'if'
      consume(70);                  // 'if'
      break;
    case 75:                        // 'item'
      consume(75);                  // 'item'
      break;
    case 79:                        // 'map'
      consume(79);                  // 'map'
      break;
    case 82:                        // 'namespace-node'
      consume(82);                  // 'namespace-node'
      break;
    case 84:                        // 'node'
      consume(84);                  // 'node'
      break;
    case 90:                        // 'processing-instruction'
      consume(90);                  // 'processing-instruction'
      break;
    case 93:                        // 'schema-attribute'
      consume(93);                  // 'schema-attribute'
      break;
    case 94:                        // 'schema-element'
      consume(94);                  // 'schema-element'
      break;
    case 97:                        // 'switch'
      consume(97);                  // 'switch'
      break;
    case 98:                        // 'text'
      consume(98);                  // 'text'
      break;
    case 102:                       // 'typeswitch'
      consume(102);                 // 'typeswitch'
      break;
    default:
      parse_FunctionName();
    }
    eventHandler.endNonterminal("QName", e0);
  }

  private void consume(int t)
  {
    if (l1 == t)
    {
      whitespace();
      eventHandler.terminal(TOKEN[l1], b1, e1);
      b0 = b1; e0 = e1; l1 = l2; if (l1 != 0) {
      b1 = b2; e1 = e2; l2 = l3; if (l2 != 0) {
      b2 = b3; e2 = e3; l3 = 0; }}
    }
    else
    {
      error(b1, e1, 0, l1, t);
    }
  }

  private void consumeT(int t)
  {
    if (l1 == t)
    {
      b0 = b1; e0 = e1; l1 = l2; if (l1 != 0) {
      b1 = b2; e1 = e2; l2 = l3; if (l2 != 0) {
      b2 = b3; e2 = e3; l3 = 0; }}
    }
    else
    {
      error(b1, e1, 0, l1, t);
    }
  }

  private void skip(int code)
  {
    int b0W = b0; int e0W = e0; int l1W = l1;
    int b1W = b1; int e1W = e1; int l2W = l2;
    int b2W = b2; int e2W = e2;

    l1 = code; b1 = begin; e1 = end;
    l2 = 0;
    l3 = 0;

    try_Whitespace();

    b0 = b0W; e0 = e0W; l1 = l1W; if (l1 != 0) {
    b1 = b1W; e1 = e1W; l2 = l2W; if (l2 != 0) {
    b2 = b2W; e2 = e2W; }}
  }

  private void whitespace()
  {
    if (e0 != b1)
    {
      eventHandler.whitespace(e0, b1);
      e0 = b1;
    }
  }

  private int matchW(int set)
  {
    int code;
    for (;;)
    {
      code = match(set);
      if (code != 8)                // S^WS
      {
        if (code != 17)             // '(:'
        {
          break;
        }
        skip(code);
      }
    }
    return code;
  }

  private void lookahead1W(int set)
  {
    if (l1 == 0)
    {
      l1 = matchW(set);
      b1 = begin;
      e1 = end;
    }
  }

  private void lookahead2W(int set)
  {
    if (l2 == 0)
    {
      l2 = matchW(set);
      b2 = begin;
      e2 = end;
    }
    lk = (l2 << 7) | l1;
  }

  private void lookahead3W(int set)
  {
    if (l3 == 0)
    {
      l3 = matchW(set);
      b3 = begin;
      e3 = end;
    }
    lk |= l3 << 14;
  }

  private void lookahead1(int set)
  {
    if (l1 == 0)
    {
      l1 = match(set);
      b1 = begin;
      e1 = end;
    }
  }

  private int error(int b, int e, int s, int l, int t)
  {
    throw new ParseException(b, e, s, l, t);
  }

  private int lk, b0, e0;
  private int l1, b1, e1;
  private int l2, b2, e2;
  private int l3, b3, e3;
  private EventHandler eventHandler = null;
  private CharSequence input = null;
  private int size = 0;
  private int begin = 0;
  private int end = 0;

  private int match(int tokenSetId)
  {
    boolean nonbmp = false;
    begin = end;
    int current = end;
    int result = INITIAL[tokenSetId];
    int state = 0;

    for (int code = result & 1023; code != 0; )
    {
      int charclass;
      int c0 = current < size ? input.charAt(current) : 0;
      ++current;
      if (c0 < 0x80)
      {
        charclass = MAP0[c0];
      }
      else if (c0 < 0xd800)
      {
        int c1 = c0 >> 4;
        charclass = MAP1[(c0 & 15) + MAP1[(c1 & 31) + MAP1[c1 >> 5]]];
      }
      else
      {
        if (c0 < 0xdc00)
        {
          int c1 = current < size ? input.charAt(current) : 0;
          if (c1 >= 0xdc00 && c1 < 0xe000)
          {
            nonbmp = true;
            ++current;
            c0 = ((c0 & 0x3ff) << 10) + (c1 & 0x3ff) + 0x10000;
          }
        }

        int lo = 0, hi = 5;
        for (int m = 3; ; m = (hi + lo) >> 1)
        {
          if (MAP2[m] > c0) {hi = m - 1;}
          else if (MAP2[6 + m] < c0) {lo = m + 1;}
          else {charclass = MAP2[12 + m]; break;}
          if (lo > hi) {charclass = 0; break;}
        }
      }

      state = code;
      int i0 = (charclass << 10) + code - 1;
      code = TRANSITION[(i0 & 15) + TRANSITION[i0 >> 4]];

      if (code > 1023)
      {
        result = code;
        code &= 1023;
        end = current;
      }
    }

    result >>= 10;
    if (result == 0)
    {
      end = current - 1;
      int c1 = end < size ? input.charAt(end) : 0;
      if (c1 >= 0xdc00 && c1 < 0xe000)
      {
        --end;
      }
      return error(begin, end, state, -1, -1);
    }
    else if (nonbmp)
    {
      for (int i = result >> 7; i > 0; --i)
      {
        --end;
        int c1 = end < size ? input.charAt(end) : 0;
        if (c1 >= 0xdc00 && c1 < 0xe000)
        {
          --end;
        }
      }
    }
    else
    {
      end -= result >> 7;
    }

    if (end > size) end = size;
    return (result & 127) - 1;
  }

  private static String[] getTokenSet(int tokenSetId)
  {
    java.util.ArrayList<String> expected = new java.util.ArrayList<>();
    int s = tokenSetId < 0 ? - tokenSetId : INITIAL[tokenSetId] & 1023;
    for (int i = 0; i < 108; i += 32)
    {
      int j = i;
      int i0 = (i >> 5) * 829 + s - 1;
      int i1 = i0 >> 2;
      int f = EXPECTED[(i0 & 3) + EXPECTED[(i1 & 3) + EXPECTED[i1 >> 2]]];
      for ( ; f != 0; f >>>= 1, ++j)
      {
        if ((f & 1) != 0)
        {
          expected.add(TOKEN[j]);
        }
      }
    }
    return expected.toArray(new String[]{});
  }

  private static final int[] MAP0 = new int[128];
  static
  {
    final String s1[] =
    {
      /*   0 */ "55, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2",
      /*  34 */ "3, 4, 5, 6, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 17, 6, 18, 19",
      /*  62 */ "20, 21, 22, 23, 23, 23, 23, 24, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 25, 23, 23, 23, 23, 23",
      /*  87 */ "23, 23, 23, 23, 26, 6, 27, 6, 23, 6, 28, 29, 30, 31, 32, 33, 34, 35, 36, 23, 23, 37, 38, 39, 40, 41",
      /* 113 */ "42, 43, 44, 45, 46, 47, 48, 49, 50, 23, 51, 52, 53, 6, 6"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 128; ++i) {MAP0[i] = Integer.parseInt(s2[i]);}
  }

  private static final int[] MAP1 = new int[455];
  static
  {
    final String s1[] =
    {
      /*   0 */ "108, 124, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 156, 181, 181, 181",
      /*  20 */ "181, 181, 214, 215, 213, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214",
      /*  40 */ "214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214",
      /*  60 */ "214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214",
      /*  80 */ "214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214, 214",
      /* 100 */ "214, 214, 214, 214, 214, 214, 214, 214, 247, 261, 277, 293, 309, 331, 370, 386, 422, 422, 422, 414",
      /* 120 */ "354, 346, 354, 346, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354",
      /* 140 */ "439, 439, 439, 439, 439, 439, 439, 315, 354, 354, 354, 354, 354, 354, 354, 354, 400, 422, 422, 423",
      /* 160 */ "421, 422, 422, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354",
      /* 180 */ "354, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422",
      /* 200 */ "422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 422, 353, 354, 354, 354, 354, 354, 354",
      /* 220 */ "354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354, 354",
      /* 240 */ "354, 354, 354, 354, 354, 354, 422, 55, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0",
      /* 269 */ "0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 16, 16, 16, 16, 16",
      /* 299 */ "16, 16, 16, 16, 17, 6, 18, 19, 20, 21, 22, 23, 23, 23, 23, 24, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23",
      /* 325 */ "23, 23, 23, 23, 6, 23, 23, 25, 23, 23, 23, 23, 23, 23, 23, 23, 23, 26, 6, 27, 6, 23, 23, 23, 23, 23",
      /* 351 */ "23, 23, 6, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 6, 28, 29, 30, 31, 32, 33",
      /* 377 */ "34, 35, 36, 23, 23, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 23, 51, 52, 53, 6, 6, 6",
      /* 403 */ "6, 6, 6, 6, 6, 6, 6, 6, 6, 23, 23, 6, 6, 6, 6, 6, 6, 6, 54, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6",
      /* 436 */ "6, 6, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 455; ++i) {MAP1[i] = Integer.parseInt(s2[i]);}
  }

  private static final int[] MAP2 = new int[18];
  static
  {
    final String s1[] =
    {
      /*  0 */ "57344, 63744, 64976, 65008, 65536, 983040, 63743, 64975, 65007, 65533, 983039, 1114111, 6, 23, 6, 23",
      /* 16 */ "23, 6"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 18; ++i) {MAP2[i] = Integer.parseInt(s2[i]);}
  }

  private static final int[] INITIAL = new int[58];
  static
  {
    final String s1[] =
    {
      /*  0 */ "1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28",
      /* 28 */ "29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54",
      /* 54 */ "55, 56, 57, 58"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 58; ++i) {INITIAL[i] = Integer.parseInt(s2[i]);}
  }

  private static final int[] TRANSITION = new int[12112];
  static
  {
    final String s1[] =
    {
      /*     0 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*    16 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*    32 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*    48 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*    64 */ "3586, 3584, 3584, 3602, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*    80 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*    96 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   112 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   128 */ "10082, 8002, 4253, 6089, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   144 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   160 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   176 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   192 */ "10082, 6957, 4468, 4288, 4341, 4468, 3869, 4154, 4817, 4366, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   208 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   224 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   240 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   256 */ "4382, 4468, 6079, 8526, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   272 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   288 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   304 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   320 */ "4398, 4467, 4451, 4485, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   336 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   352 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   368 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   384 */ "10082, 4468, 4468, 8526, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   400 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   416 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   432 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   448 */ "10082, 8521, 4468, 4514, 4550, 4468, 3869, 4154, 4817, 4575, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   464 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   480 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   496 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   512 */ "4602, 4591, 4618, 4630, 4646, 4468, 3869, 4154, 3669, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   528 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   544 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   560 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   576 */ "9354, 4671, 4679, 4695, 4729, 4468, 3869, 4154, 3769, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   592 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   608 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   624 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   640 */ "6118, 4527, 4534, 4754, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4796, 5157, 3686",
      /*   656 */ "3702, 3742, 4037, 3785, 3984, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   672 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   688 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   704 */ "10082, 5123, 5132, 6199, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   720 */ "4833, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   736 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   752 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   768 */ "9448, 4873, 4881, 5234, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   784 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   800 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*   816 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   832 */ "10082, 5963, 5972, 6882, 11986, 4468, 5001, 8766, 8953, 4468, 10869, 6395, 6395, 6562, 7682, 7682",
      /*   848 */ "10264, 5192, 6395, 6395, 7758, 7682, 7683, 4897, 6395, 6395, 10775, 7682, 7665, 7177, 8723, 4913",
      /*   864 */ "7441, 10534, 10775, 10010, 6395, 7682, 8754, 7815, 6764, 4933, 4949, 4983, 5019, 8250, 10815, 7903",
      /*   880 */ "8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   896 */ "10082, 4468, 4468, 5054, 11986, 4468, 5001, 8766, 7841, 4468, 10869, 6395, 6395, 6562, 7682, 7682",
      /*   912 */ "11867, 5192, 6395, 6395, 7758, 7682, 7683, 4897, 6395, 6395, 10775, 7682, 7665, 6395, 8723, 7682",
      /*   928 */ "11843, 6395, 10775, 11925, 6395, 7682, 4996, 8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459",
      /*   944 */ "8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*   960 */ "10082, 4468, 5113, 9435, 3631, 4468, 5148, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*   976 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*   992 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*  1008 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1024 */ "9193, 9185, 4468, 5173, 11986, 4468, 5001, 8766, 11252, 4468, 10869, 6395, 6395, 6562, 7682, 7682",
      /*  1040 */ "9323, 5192, 6395, 6395, 7758, 7682, 10517, 5208, 6395, 6395, 10775, 7682, 7665, 6395, 8723, 7682",
      /*  1056 */ "11843, 6395, 10775, 11925, 6395, 7682, 4996, 8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459",
      /*  1072 */ "8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1088 */ "3615, 6170, 5224, 6144, 5250, 4468, 5584, 5333, 5801, 4780, 5275, 5839, 5321, 5288, 5367, 5356",
      /*  1104 */ "5465, 5383, 5763, 5831, 5690, 5619, 5558, 3809, 5720, 5438, 5453, 5481, 5512, 5528, 5538, 5554",
      /*  1120 */ "5574, 5649, 5608, 5635, 5679, 5792, 5496, 5592, 5706, 5736, 5752, 5398, 5305, 5300, 5340, 5663",
      /*  1136 */ "5779, 5817, 5855, 5871, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1152 */ "10082, 6645, 6654, 6238, 5924, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*  1168 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*  1184 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*  1200 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1216 */ "10082, 6928, 5953, 7211, 5988, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*  1232 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*  1248 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*  1264 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1280 */ "10082, 8134, 8143, 7977, 6027, 8849, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*  1296 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*  1312 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*  1328 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1344 */ "10082, 4713, 4708, 6066, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*  1360 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*  1376 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*  1392 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1408 */ "10082, 4468, 4468, 6105, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*  1424 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*  1440 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*  1456 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1472 */ "10082, 9477, 4655, 8024, 11986, 4468, 5001, 8766, 8027, 4468, 6902, 6395, 6395, 9217, 7682, 7682",
      /*  1488 */ "7661, 10849, 6395, 6395, 10808, 7682, 7683, 4469, 6395, 6395, 10775, 7682, 7665, 6395, 8723, 7682",
      /*  1504 */ "11843, 6395, 10775, 11925, 6395, 7682, 4996, 8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459",
      /*  1520 */ "8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1536 */ "10082, 9477, 4655, 8024, 11986, 4468, 5001, 8766, 10300, 4468, 6902, 6395, 6395, 9217, 7682, 7682",
      /*  1552 */ "10132, 10849, 6395, 6395, 10808, 7682, 7683, 4469, 6395, 6395, 10775, 7682, 7665, 6395, 8723, 7682",
      /*  1568 */ "11843, 6395, 10775, 11925, 6395, 7682, 4996, 8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459",
      /*  1584 */ "8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1600 */ "10082, 9477, 4738, 6134, 11986, 4468, 5001, 8766, 8027, 4468, 6902, 6395, 6395, 9217, 7682, 7682",
      /*  1616 */ "7661, 10849, 6395, 6395, 10808, 7682, 7683, 4469, 6395, 6395, 10775, 7682, 7665, 6395, 8723, 7682",
      /*  1632 */ "11843, 6395, 10775, 11925, 6395, 7682, 4996, 8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459",
      /*  1648 */ "8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1664 */ "10082, 4468, 6160, 6186, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*  1680 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*  1696 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*  1712 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1728 */ "10082, 8408, 8417, 4767, 3631, 4468, 3869, 4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686",
      /*  1744 */ "3702, 3742, 4037, 3785, 4027, 4043, 3872, 3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065",
      /*  1760 */ "3859, 3894, 3888, 3910, 3942, 3940, 3924, 3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112",
      /*  1776 */ "4099, 4128, 4177, 4193, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1792 */ "9905, 10163, 10172, 6228, 11986, 9404, 6254, 6270, 6286, 4468, 6902, 6395, 6395, 9217, 7682, 7682",
      /*  1808 */ "7661, 10108, 6395, 6395, 6340, 7682, 6438, 3640, 6374, 6394, 6412, 6434, 7132, 6395, 6454, 11476",
      /*  1824 */ "11843, 7321, 7488, 11925, 6481, 6534, 6555, 8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459",
      /*  1840 */ "8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1856 */ "10082, 9477, 4655, 8024, 11986, 4468, 5001, 8766, 8027, 4468, 6902, 6395, 6395, 9217, 7682, 7682",
      /*  1872 */ "7661, 10849, 6395, 6395, 10808, 7682, 7683, 4469, 6395, 6395, 10775, 7682, 8915, 6578, 11389, 7682",
      /*  1888 */ "11843, 6395, 10775, 11925, 6395, 7682, 4996, 8724, 6764, 8722, 6465, 7063, 8722, 11056, 6596, 6459",
      /*  1904 */ "8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1920 */ "10082, 7727, 6622, 6635, 6670, 4468, 5001, 6706, 9816, 9896, 8550, 7600, 6395, 9534, 11531, 7682",
      /*  1936 */ "7661, 10849, 6727, 11315, 11137, 10230, 6758, 4469, 6395, 9236, 10775, 11106, 7665, 6395, 8723",
      /*  1951 */ "7682, 8618, 7313, 6780, 10668, 8108, 8590, 4996, 10372, 11734, 8722, 6465, 7063, 8722, 11907, 6835",
      /*  1967 */ "6459, 8724, 8055, 6518, 6869, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  1983 */ "4468, 10082, 8187, 8196, 6918, 11986, 6944, 7451, 8630, 8027, 7352, 6978, 6395, 7876, 11768, 7682",
      /*  1999 */ "10694, 7661, 10849, 6395, 6395, 10808, 7682, 7683, 4469, 7014, 6395, 7035, 7682, 7665, 6395, 7058",
      /*  2015 */ "6711, 11843, 7079, 7096, 11925, 6395, 7682, 4996, 8724, 6764, 8722, 6465, 7063, 7119, 7951, 7167",
      /*  2031 */ "6459, 8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2047 */ "4468, 10082, 4848, 4857, 7201, 11986, 7227, 10022, 7251, 7337, 7373, 10064, 10981, 7392, 7611",
      /*  2062 */ "10291, 7428, 11191, 7467, 11710, 7504, 7520, 7544, 7572, 7627, 11692, 8100, 7643, 11292, 7665, 7406",
      /*  2078 */ "8723, 7681, 9760, 7806, 7699, 9732, 7743, 7781, 7797, 7831, 7857, 10713, 4325, 11618, 8382, 7892",
      /*  2094 */ "7925, 7941, 8724, 9847, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2110 */ "4468, 4468, 10082, 9477, 11995, 7967, 7993, 4468, 11551, 6819, 8027, 4468, 6902, 6395, 6395, 9217",
      /*  2126 */ "7682, 7682, 7661, 10849, 6395, 5003, 10808, 7682, 10500, 4469, 6395, 6395, 10775, 7682, 8877, 6395",
      /*  2142 */ "8018, 9157, 11843, 6395, 10775, 11925, 6395, 7682, 4996, 8724, 6764, 8722, 6465, 7063, 8722, 8250",
      /*  2158 */ "6464, 6459, 8724, 8043, 8075, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2174 */ "4468, 4468, 10082, 10425, 10434, 8124, 11986, 4468, 5001, 8766, 8027, 4468, 6902, 6395, 6395, 9217",
      /*  2190 */ "7682, 7682, 7661, 10849, 6395, 6395, 10808, 7682, 7683, 4469, 6395, 6395, 10775, 7682, 7665, 6395",
      /*  2206 */ "8723, 7682, 11843, 6395, 10775, 11925, 6395, 7682, 10346, 8159, 8212, 6511, 8246, 7063, 8722, 8250",
      /*  2222 */ "6464, 6459, 8724, 8701, 8266, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2238 */ "4468, 4468, 10082, 9477, 4655, 8024, 8291, 4468, 8889, 8944, 8027, 8316, 6902, 6395, 6395, 8336",
      /*  2254 */ "7682, 10317, 7661, 10849, 6395, 6395, 10808, 7682, 7683, 4469, 6395, 6395, 10775, 7682, 7665, 6395",
      /*  2270 */ "7481, 7682, 8364, 6395, 10775, 11925, 6395, 7682, 4996, 8724, 6764, 8230, 10704, 7063, 8722, 8250",
      /*  2286 */ "6464, 6459, 8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2302 */ "4468, 4468, 4559, 10945, 10955, 8398, 8433, 4468, 7144, 9860, 8027, 9957, 7279, 8449, 6395, 8465",
      /*  2318 */ "8493, 7682, 8542, 5884, 6395, 11227, 10808, 7682, 7654, 4469, 8566, 6395, 8585, 7682, 7665, 7151",
      /*  2334 */ "8723, 4230, 9121, 6496, 8606, 8646, 11639, 9552, 4996, 8724, 6764, 8722, 6465, 7063, 8674, 8717",
      /*  2350 */ "6464, 6459, 8740, 8055, 8059, 8508, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2366 */ "4468, 4468, 10082, 6002, 6011, 8787, 8813, 8848, 8222, 4219, 8027, 4468, 6902, 7412, 6395, 8865",
      /*  2382 */ "6418, 7682, 8911, 10849, 8931, 6395, 11420, 5030, 7683, 4469, 6395, 6395, 10775, 7682, 7665, 6395",
      /*  2398 */ "8723, 7682, 8477, 6395, 8969, 11925, 6395, 7682, 8991, 10660, 11541, 8722, 6465, 7063, 8722, 8250",
      /*  2414 */ "7765, 9014, 10255, 11164, 9030, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2430 */ "4468, 4468, 10082, 6041, 6050, 9055, 11986, 4468, 8374, 4314, 8027, 4468, 6902, 9090, 11701, 9109",
      /*  2446 */ "9155, 11000, 9173, 10849, 9209, 9233, 11368, 9252, 7683, 9289, 6845, 10206, 9305, 10580, 7665, 6395",
      /*  2462 */ "8723, 7682, 11843, 6395, 10775, 11925, 6395, 7682, 4996, 8724, 6764, 8722, 9273, 9339, 8722, 8250",
      /*  2478 */ "6464, 6459, 8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2494 */ "4468, 4468, 10082, 6315, 6324, 9394, 9420, 9464, 9580, 9502, 9870, 4468, 6902, 6378, 6395, 9217",
      /*  2510 */ "8771, 7682, 5038, 10849, 6395, 6395, 10808, 7682, 7683, 9486, 6395, 6992, 9550, 6350, 9568, 9614",
      /*  2526 */ "9643, 9659, 11843, 9706, 9724, 11925, 6807, 11663, 8089, 9748, 9786, 8998, 7909, 8275, 9802, 9832",
      /*  2542 */ "7303, 6459, 8724, 8689, 8059, 7714, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2558 */ "4468, 4468, 7357, 3717, 3726, 9886, 11986, 6301, 8658, 9921, 9937, 4468, 6902, 6395, 8569, 9217",
      /*  2574 */ "7682, 4917, 7661, 5067, 6395, 6395, 9973, 7682, 10777, 4469, 9093, 6395, 11337, 7682, 7665, 6395",
      /*  2590 */ "8723, 7682, 8348, 10199, 9997, 11925, 6395, 7682, 4996, 8724, 5097, 10457, 6358, 11918, 10038, 8250",
      /*  2606 */ "6464, 6459, 8724, 8055, 8059, 8174, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2622 */ "4468, 4468, 10082, 9477, 5259, 10054, 11986, 4468, 5001, 8766, 8027, 4468, 6902, 9682, 4960, 6606",
      /*  2638 */ "9315, 11083, 10841, 10849, 6395, 6395, 10808, 7682, 7683, 5937, 7019, 6395, 10631, 7682, 7665, 9708",
      /*  2654 */ "8723, 7103, 11843, 6395, 10775, 11925, 6395, 7682, 4996, 8724, 6764, 8722, 6465, 7063, 8722, 8250",
      /*  2670 */ "6464, 6459, 8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2686 */ "4468, 4468, 10082, 9477, 4655, 8024, 11986, 10080, 9131, 7292, 8027, 4468, 6902, 6395, 6395, 9217",
      /*  2702 */ "7682, 7682, 7661, 10849, 6395, 6395, 10808, 7682, 7683, 4469, 6395, 6395, 10775, 7682, 7665, 6395",
      /*  2718 */ "8723, 7682, 11843, 6395, 10775, 11925, 6395, 7682, 6794, 11823, 6764, 8722, 6465, 7063, 8722, 8250",
      /*  2734 */ "6464, 6459, 8724, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2750 */ "4468, 4468, 10082, 4074, 4083, 10098, 11986, 6212, 10608, 10124, 10148, 4468, 7235, 8895, 11028",
      /*  2765 */ "9139, 8975, 6539, 7661, 10849, 10188, 6395, 11582, 10222, 7683, 4498, 6395, 10246, 10775, 10280",
      /*  2780 */ "7665, 6395, 8723, 7682, 11843, 6395, 10775, 7528, 6395, 10316, 4996, 8724, 6764, 8722, 9981, 10333",
      /*  2796 */ "8722, 11816, 10362, 6459, 8724, 11152, 8059, 7266, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468",
      /*  2812 */ "4468, 4468, 4468, 4468, 10082, 5413, 5422, 10396, 11986, 10412, 9770, 10753, 8027, 6895, 4206",
      /*  2827 */ "10450, 10473, 6853, 10492, 10516, 7661, 10849, 6395, 6395, 10808, 7682, 7683, 8300, 10533, 10550",
      /*  2842 */ "10576, 5908, 10596, 10476, 10624, 11500, 11843, 10647, 10684, 10729, 6395, 7682, 11382, 10769, 9516",
      /*  2857 */ "8722, 11008, 9039, 8722, 11433, 6464, 10793, 10831, 8055, 8059, 7266, 4468, 4468, 4468, 4468, 4468",
      /*  2873 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 8320, 4413, 4428, 4441, 11986, 10865, 10741, 9627, 8027",
      /*  2889 */ "11788, 10885, 6395, 10901, 9690, 7682, 10917, 7661, 4301, 10971, 11024, 11044, 11072, 11089, 6962",
      /*  2904 */ "6580, 7080, 10775, 11105, 11122, 9598, 7185, 11180, 11207, 11223, 11243, 10380, 11268, 11284, 11308",
      /*  2919 */ "11331, 11353, 10560, 9673, 7063, 8722, 8250, 4237, 11405, 6742, 8055, 8059, 10932, 4468, 4468, 4468",
      /*  2935 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 10082, 9369, 9378, 11449, 11986, 4468, 7867",
      /*  2951 */ "11895, 8027, 4468, 6902, 6395, 6395, 9217, 7682, 7682, 7661, 7587, 9591, 6998, 10808, 11475, 5091",
      /*  2967 */ "4469, 6395, 6395, 10775, 7682, 7665, 6395, 8723, 7682, 7556, 6395, 11492, 11925, 6395, 7682, 4996",
      /*  2983 */ "8724, 6764, 11516, 6465, 7063, 8722, 8250, 6464, 11567, 8724, 8055, 11609, 7266, 4468, 4468, 4468",
      /*  2999 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 10082, 9477, 4655, 8024, 11986, 4468, 9526",
      /*  3015 */ "5080, 8027, 5186, 6902, 11634, 6395, 9217, 11655, 7682, 7661, 11679, 6396, 6395, 10808, 7042, 7683",
      /*  3031 */ "4469, 6395, 6395, 10775, 7682, 7665, 6395, 8723, 7682, 11843, 6395, 10775, 11925, 6395, 7682, 4996",
      /*  3047 */ "8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459, 8724, 8055, 8059, 7266, 4468, 4468, 4468",
      /*  3063 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 10082, 9477, 4655, 8024, 11986, 4468, 5001",
      /*  3079 */ "11726, 11955, 4468, 6902, 6395, 6395, 9217, 7682, 7682, 7661, 10849, 6395, 6395, 10808, 7682, 7683",
      /*  3095 */ "4469, 6395, 6395, 10775, 7682, 7665, 4967, 10991, 9265, 11750, 6395, 10775, 11925, 6395, 7682, 4996",
      /*  3111 */ "8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459, 8724, 8055, 8059, 7266, 4468, 4468, 4468",
      /*  3127 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 10082, 9477, 4655, 8024, 11986, 11784, 11760",
      /*  3143 */ "5897, 8027, 4468, 6902, 6395, 6395, 11804, 7682, 7682, 11839, 10849, 6395, 6395, 10808, 7682, 7683",
      /*  3159 */ "4469, 6395, 6395, 10775, 7682, 7665, 6395, 8723, 7682, 11843, 6395, 10775, 11925, 6395, 7682, 4996",
      /*  3175 */ "8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459, 8724, 8055, 8059, 7266, 4468, 4468, 4468",
      /*  3191 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 10082, 9477, 4655, 8024, 11986, 4468, 5001",
      /*  3207 */ "11859, 11593, 4468, 6902, 6395, 6395, 9217, 7682, 7682, 7661, 10849, 6395, 6395, 10808, 7682, 7683",
      /*  3223 */ "4469, 11883, 6395, 11941, 7682, 7665, 6395, 8723, 7682, 11843, 6395, 10775, 11925, 6395, 7682, 4996",
      /*  3239 */ "8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459, 8724, 8055, 8059, 7266, 4468, 4468, 4468",
      /*  3255 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 7376, 11971, 9952, 8526, 3631, 4468, 12011",
      /*  3271 */ "4808, 4817, 4780, 12036, 5156, 3685, 3972, 5157, 3686, 3702, 3742, 4037, 3785, 4027, 4043, 3872",
      /*  3287 */ "3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065, 3859, 3894, 3888, 3910, 3942, 3940, 3924",
      /*  3303 */ "3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112, 4099, 4128, 4177, 4193, 4468, 4468, 4468",
      /*  3319 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 10082, 8823, 8832, 8797, 3631, 4350, 3869",
      /*  3335 */ "4154, 4817, 4780, 3656, 5156, 3685, 4142, 5157, 3686, 3702, 3742, 4037, 3785, 4027, 4043, 3872",
      /*  3351 */ "3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065, 3859, 3894, 3888, 3910, 3942, 3940, 3924",
      /*  3367 */ "3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112, 4099, 4128, 4177, 4193, 4468, 4468, 4468",
      /*  3383 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 10082, 9065, 9074, 11459, 3631, 4468, 3869",
      /*  3399 */ "4154, 4817, 4780, 12081, 5156, 3685, 3757, 5157, 3686, 3702, 3742, 4037, 3785, 4027, 4043, 3872",
      /*  3415 */ "3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065, 3859, 3894, 3888, 3910, 3942, 3940, 3924",
      /*  3431 */ "3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112, 4099, 4128, 4177, 4193, 4468, 4468, 4468",
      /*  3447 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 10082, 4468, 4468, 4468, 11986, 4468, 5001",
      /*  3463 */ "8766, 8027, 4468, 10869, 6395, 6395, 6562, 7682, 7682, 7661, 5192, 6395, 6395, 7758, 7682, 7683",
      /*  3479 */ "4469, 6395, 6395, 10775, 7682, 7665, 6395, 8723, 7682, 11843, 6395, 10775, 11925, 6395, 7682, 4996",
      /*  3495 */ "8724, 6764, 8722, 6465, 7063, 8722, 8250, 6464, 6459, 8724, 8055, 8059, 7266, 4468, 4468, 4468",
      /*  3511 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4263, 4272, 6690, 6681, 4468, 3869",
      /*  3527 */ "4154, 12020, 4780, 12036, 5156, 3685, 3972, 5157, 3686, 3702, 3742, 4037, 3785, 4027, 4043, 3872",
      /*  3543 */ "3809, 12096, 3827, 12094, 3825, 3843, 12060, 12049, 12065, 3859, 3894, 3888, 3910, 3942, 3940, 3924",
      /*  3559 */ "3793, 3958, 4000, 4016, 4059, 3871, 4154, 4161, 4112, 4099, 4128, 4177, 4193, 4468, 4468, 4468",
      /*  3575 */ "4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 4468, 9275, 9275, 9275, 9275, 9275, 9275, 9275",
      /*  3591 */ "9275, 9275, 9275, 9275, 9275, 9275, 9275, 9275, 9275, 68, 9275, 9275, 9275, 9275, 9275, 9275, 9275",
      /*  3608 */ "9275, 9275, 9275, 9275, 9275, 0, 133120, 0, 0, 0, 0, 0, 0, 0, 62, 63, 0, 0, 0, 0, 0, 70, 0, 0, 0, 0",
      /*  3634 */ "68, 68, 68, 71, 72, 138240, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 460, 0, 462, 462, 384, 0, 217088",
      /*  3658 */ "220160, 0, 0, 234496, 0, 0, 0, 0, 0, 0, 173, 139264, 139264, 139264, 139264, 139264, 139264, 139264",
      /*  3676 */ "0, 133120, 180224, 204800, 219136, 0, 142, 142, 0, 203776, 139264, 207872, 139264, 209920, 211968",
      /*  3691 */ "139264, 139264, 139264, 217088, 139264, 220160, 139264, 139264, 139264, 139264, 139264, 139264",
      /*  3703 */ "139264, 139264, 139264, 234496, 139264, 139264, 139264, 134144, 0, 0, 0, 0, 178176, 188416, 0, 0, 0",
      /*  3720 */ "0, 73, 0, 0, 73, 73, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 111, 130, 111, 111, 111, 111, 0, 0, 0",
      /*  3745 */ "214016, 0, 0, 0, 0, 0, 0, 0, 139264, 0, 139264, 178176, 139264, 139264, 139264, 139264, 139264",
      /*  3762 */ "234496, 139264, 139264, 139264, 0, 0, 327, 139264, 139264, 139264, 139264, 139264, 139264, 139264",
      /*  3776 */ "0, 133120, 180224, 204800, 219136, 0, 68, 272384, 272384, 139264, 139264, 139264, 210944, 212992",
      /*  3790 */ "214016, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 176128",
      /*  3802 */ "139264, 183296, 139264, 139264, 139264, 139264, 199680, 135168, 233472, 0, 0, 191488, 0, 202752, 0",
      /*  3817 */ "0, 0, 0, 0, 0, 0, 182272, 137216, 139264, 202752, 139264, 139264, 208896, 139264, 218112, 139264",
      /*  3833 */ "139264, 139264, 139264, 139264, 139264, 229376, 230400, 139264, 232448, 139264, 232448, 139264",
      /*  3845 */ "139264, 139264, 0, 0, 0, 0, 0, 0, 237568, 0, 235520, 0, 139264, 179200, 139264, 235520, 139264",
      /*  3862 */ "237568, 0, 0, 195584, 0, 225280, 0, 0, 0, 139264, 139264, 139264, 139264, 139264, 139264, 139264",
      /*  3878 */ "139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 0, 231424, 139264, 139264",
      /*  3891 */ "139264, 139264, 139264, 139264, 139264, 139264, 139264, 195584, 139264, 139264, 139264, 139264",
      /*  3903 */ "139264, 221184, 139264, 139264, 225280, 139264, 139264, 221184, 139264, 139264, 225280, 139264",
      /*  3915 */ "139264, 231424, 139264, 190464, 0, 0, 0, 0, 0, 139264, 139264, 0, 0, 0, 205824, 183296, 176128",
      /*  3932 */ "139264, 183296, 139264, 139264, 139264, 139264, 199680, 205824, 139264, 139264, 139264, 185344",
      /*  3944 */ "139264, 139264, 190464, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264",
      /*  3956 */ "139264, 139264, 205824, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 0, 206848",
      /*  3969 */ "226304, 139264, 181248, 139264, 139264, 139264, 139264, 139264, 234496, 139264, 139264, 139264, 0",
      /*  3982 */ "0, 0, 139264, 139264, 139264, 139264, 139264, 0, 11264, 139264, 178176, 139264, 139264, 139264",
      /*  3996 */ "139264, 139264, 139264, 188416, 139264, 196608, 206848, 215040, 222208, 139264, 226304, 139264",
      /*  4008 */ "139264, 139264, 139264, 181248, 139264, 139264, 139264, 196608, 206848, 215040, 222208, 139264",
      /*  4020 */ "226304, 139264, 139264, 139264, 0, 139264, 186368, 139264, 139264, 139264, 139264, 139264, 0, 0",
      /*  4034 */ "139264, 178176, 139264, 139264, 139264, 139264, 139264, 139264, 188416, 139264, 139264, 139264",
      /*  4046 */ "139264, 139264, 139264, 139264, 198656, 139264, 139264, 139264, 139264, 139264, 210944, 212992",
      /*  4058 */ "214016, 139264, 139264, 139264, 236544, 139264, 186368, 139264, 139264, 139264, 139264, 139264",
      /*  4070 */ "139264, 139264, 139264, 236544, 0, 0, 0, 0, 73, 0, 0, 73, 73, 88, 88, 88, 88, 88, 88, 88, 88, 88",
      /*  4092 */ "88, 113, 132, 113, 113, 113, 113, 139264, 192512, 139264, 216064, 139264, 139264, 139264, 228352",
      /*  4107 */ "139264, 139264, 192512, 139264, 216064, 139264, 139264, 139264, 139264, 139264, 139264, 189440",
      /*  4119 */ "139264, 139264, 139264, 139264, 139264, 139264, 139264, 228352, 139264, 228352, 139264, 139264",
      /*  4131 */ "139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 177152, 139264",
      /*  4143 */ "139264, 139264, 139264, 139264, 234496, 139264, 139264, 139264, 0, 0, 220, 139264, 139264, 139264",
      /*  4157 */ "139264, 0, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264",
      /*  4170 */ "0, 139264, 139264, 189440, 139264, 139264, 139264, 139264, 139264, 227328, 177152, 139264, 139264",
      /*  4183 */ "139264, 139264, 227328, 139264, 197632, 223232, 139264, 139264, 197632, 223232, 139264, 187392",
      /*  4195 */ "139264, 187392, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 224256, 224256, 0, 0, 0, 0",
      /*  4210 */ "0, 0, 0, 280, 0, 282, 283, 284, 173, 99, 99, 99, 99, 0, 118, 118, 118, 118, 118, 230, 118, 118, 118",
      /*  4233 */ "118, 118, 118, 557, 118, 118, 118, 118, 118, 118, 118, 118, 118, 767, 99, 99, 99, 99, 99, 99, 76",
      /*  4254 */ "13388, 13388, 13388, 13388, 13388, 13388, 13388, 13388, 13388, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12288",
      /*  4273 */ "12288, 12288, 12288, 12288, 12288, 12288, 12288, 12288, 12288, 0, 0, 0, 0, 0, 0, 0, 0, 0, 71, 71",
      /*  4293 */ "71, 71, 71, 71, 71, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 0, 380, 0, 382, 383, 284, 384, 99, 99, 99, 99",
      /*  4318 */ "0, 118, 118, 118, 118, 118, 231, 118, 118, 118, 118, 118, 118, 702, 118, 0, 99, 99, 99, 708, 99, 99",
      /*  4340 */ "99, 0, 0, 0, 68, 68, 68, 5265, 72, 138240, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 109568, 0, 0, 0, 0, 71",
      /*  4367 */ "0, 0, 0, 0, 0, 0, 193536, 0, 200704, 201728, 0, 0, 207872, 209920, 211968, 0, 15360, 0, 0, 0, 0, 0",
      /*  4389 */ "0, 0, 0, 0, 0, 0, 0, 68, 15360, 0, 0, 16384, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 0, 0, 0, 0, 73, 0",
      /*  4419 */ "0, 73, 73, 90, 90, 94, 90, 94, 90, 94, 94, 94, 94, 94, 94, 94, 94, 94, 94, 115, 134, 115, 115, 115",
      /*  4443 */ "115, 134, 134, 134, 134, 134, 134, 134, 0, 0, 0, 0, 0, 0, 0, 0, 16384, 0, 0, 0, 0, 0, 16384, 0",
      /*  4467 */ "16384, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 384, 0, 0, 0, 16384, 16384, 16384, 16384",
      /*  4492 */ "16384, 16384, 16384, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 0, 456, 457, 0, 0, 0, 0, 0, 0, 384, 0, 0, 0",
      /*  4517 */ "72, 72, 72, 72, 72, 72, 72, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 0, 20480, 20480, 20480, 20480, 20480",
      /*  4539 */ "20480, 20480, 20480, 20480, 20480, 0, 11381, 0, 20480, 0, 0, 0, 0, 0, 68, 68, 68, 71, 5266, 138240",
      /*  4559 */ "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 65, 0, 0, 0, 68, 0, 0, 72, 0, 0, 0, 0, 0, 193536, 0, 200704, 201728",
      /*  4586 */ "0, 0, 207872, 209920, 211968, 60, 60, 60, 60, 60, 60, 60, 17468, 17468, 60, 60, 60, 60, 60, 17468",
      /*  4606 */ "60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 69, 17468, 60, 60, 17468, 60, 60, 17468, 17468, 17468",
      /*  4626 */ "17468, 17468, 60, 60, 17468, 60, 17468, 17468, 17468, 17468, 17468, 17468, 17468, 17468, 0, 0",
      /*  4642 */ "133120, 0, 0, 0, 0, 0, 0, 142, 142, 142, 71, 72, 138240, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 99, 118, 99",
      /*  4668 */ "99, 99, 99, 19456, 19456, 19456, 0, 19456, 19456, 19456, 0, 19456, 19456, 19456, 19456, 19456",
      /*  4684 */ "19456, 19456, 19456, 19456, 19456, 0, 0, 0, 0, 0, 19456, 0, 19456, 19456, 0, 0, 0, 19456, 0, 0",
      /*  4704 */ "19456, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 0, 40960, 0, 0, 0, 0, 0, 0, 0, 0, 0, 40960, 0, 40960, 40960",
      /*  4729 */ "0, 0, 0, 68, 68, 29696, 71, 72, 138240, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100, 119, 100, 100, 100, 100",
      /*  4754 */ "20480, 20480, 20480, 11381, 11381, 11381, 11381, 11381, 11381, 11381, 0, 0, 133120, 0, 0, 0, 0, 0",
      /*  4772 */ "0, 0, 44032, 0, 44032, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 0, 193536, 0, 200704, 201728, 0, 0, 207872",
      /*  4794 */ "209920, 211968, 139264, 139264, 139264, 139264, 139264, 234496, 139264, 139264, 139264, 0, 11264",
      /*  4807 */ "220, 139264, 139264, 139264, 139264, 0, 139264, 139484, 139264, 139264, 139264, 139264, 139264",
      /*  4820 */ "139264, 139264, 139264, 139264, 0, 133120, 180224, 204800, 219136, 0, 68, 68, 0, 139264, 139264",
      /*  4835 */ "139264, 139264, 234496, 139264, 139264, 139264, 134144, 368, 0, 0, 0, 178176, 188416, 0, 0, 0, 0",
      /*  4852 */ "73, 0, 74, 73, 73, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 104, 123, 104, 104, 104, 104, 0, 22528",
      /*  4875 */ "0, 0, 0, 22528, 0, 0, 22528, 22528, 22528, 22528, 22528, 22528, 22528, 22528, 22528, 22528, 0, 0, 0",
      /*  4894 */ "0, 0, 0, 135168, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 384, 118, 118, 554, 118, 118, 118, 118",
      /*  4920 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 357, 118, 118, 118, 99, 686, 99, 687, 688, 99, 99, 99",
      /*  4941 */ "99, 99, 118, 118, 118, 118, 118, 697, 118, 698, 699, 118, 118, 118, 118, 118, 0, 99, 706, 99, 99",
      /*  4962 */ "99, 99, 99, 99, 309, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 534, 99, 99, 99, 99, 99, 712, 99, 99",
      /*  4986 */ "99, 118, 716, 118, 118, 118, 118, 118, 722, 118, 118, 118, 0, 0, 0, 0, 0, 99, 99, 99, 99, 99, 99",
      /*  5009 */ "99, 99, 99, 99, 99, 99, 99, 99, 412, 99, 726, 99, 99, 99, 99, 99, 99, 99, 99, 99, 736, 118, 118",
      /*  5032 */ "118, 118, 118, 118, 432, 118, 118, 118, 118, 118, 118, 118, 118, 118, 0, 0, 370, 0, 0, 0, 0, 0, 0",
      /*  5055 */ "0, 0, 24712, 24712, 24712, 24712, 24712, 24712, 24712, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 379, 0, 0, 0",
      /*  5077 */ "0, 284, 384, 99, 99, 99, 99, 0, 118, 118, 118, 118, 118, 233, 118, 118, 118, 118, 118, 443, 118",
      /*  5098 */ "118, 118, 118, 118, 118, 118, 118, 118, 0, 0, 0, 682, 99, 99, 99, 0, 26722, 26722, 26722, 26722",
      /*  5118 */ "26722, 26722, 26722, 26722, 26722, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21504, 21504, 21504, 21504, 21504",
      /*  5137 */ "21504, 21504, 21504, 21504, 21504, 0, 0, 0, 0, 0, 0, 0, 27648, 139264, 139264, 139264, 139264",
      /*  5154 */ "139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 193536, 139264",
      /*  5166 */ "139264, 139264, 139264, 200704, 201728, 139264, 203776, 0, 0, 0, 137, 137, 137, 137, 137, 137, 137",
      /*  5183 */ "0, 0, 61, 0, 0, 0, 0, 0, 271, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 284, 0, 99, 99, 99, 369, 0, 0, 0, 0",
      /*  5213 */ "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 384, 28672, 28672, 28672, 28734, 28672, 28672, 28734, 28672, 28672",
      /*  5233 */ "28734, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22528, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 143, 18576, 143, 71, 72",
      /*  5258 */ "138240, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 112, 131, 112, 112, 112, 112, 0, 217088, 220160, 0, 0, 234496",
      /*  5281 */ "0, 0, 0, 0, 0, 0, 173, 139436, 139436, 139436, 139436, 139436, 234668, 139436, 139436, 139436, 0, 0",
      /*  5299 */ "220, 139483, 139483, 139483, 139483, 0, 139436, 139436, 139436, 139436, 139436, 139436, 139436",
      /*  5312 */ "139436, 139436, 139436, 139483, 139483, 139483, 139483, 139483, 139483, 203948, 139436, 208044",
      /*  5324 */ "139436, 210092, 212140, 139436, 139436, 139436, 217260, 139436, 220332, 139436, 139436, 139436",
      /*  5336 */ "139436, 218, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 139483",
      /*  5349 */ "0, 139436, 139436, 189612, 139436, 139436, 139436, 139483, 208091, 139483, 210139, 212187, 139483",
      /*  5362 */ "139483, 139483, 217307, 139483, 220379, 139483, 139483, 139483, 139483, 139483, 139483, 139483",
      /*  5374 */ "193755, 139483, 139483, 139483, 139483, 200923, 201947, 139483, 203995, 0, 0, 0, 214016, 0, 0, 0, 0",
      /*  5391 */ "0, 0, 0, 139264, 0, 139436, 178348, 139436, 139436, 139436, 236716, 139483, 186587, 139483, 139483",
      /*  5406 */ "139483, 139483, 139483, 139483, 139483, 139483, 236763, 0, 0, 0, 0, 73, 0, 75, 73, 73, 89, 89, 89",
      /*  5425 */ "89, 89, 89, 89, 89, 89, 89, 114, 133, 114, 114, 114, 114, 139436, 139436, 209068, 139436, 218284",
      /*  5443 */ "139436, 139436, 139436, 139436, 139436, 139436, 229548, 230572, 139436, 232620, 139436, 139436",
      /*  5455 */ "139483, 139483, 139483, 182491, 139483, 139483, 139483, 139483, 139483, 191707, 139483, 139483",
      /*  5467 */ "139483, 139483, 234715, 139483, 139483, 139483, 134144, 0, 0, 0, 0, 178176, 188416, 0, 139483",
      /*  5482 */ "202971, 139483, 139483, 209115, 139483, 218331, 139483, 139483, 139483, 139483, 139483, 139483",
      /*  5494 */ "229595, 230619, 139483, 139483, 0, 0, 0, 205824, 183296, 176300, 139436, 183468, 139436, 139436",
      /*  5508 */ "139436, 139436, 199852, 205996, 232667, 139483, 139483, 139483, 0, 0, 0, 0, 0, 0, 237568, 0, 235520",
      /*  5525 */ "0, 139436, 179372, 139436, 139436, 184492, 139436, 139436, 139436, 139436, 139436, 194732, 139436",
      /*  5538 */ "139436, 139436, 139436, 139436, 139436, 139436, 235692, 139436, 237740, 139483, 179419, 139483",
      /*  5550 */ "139483, 184539, 139483, 139483, 139483, 139483, 139483, 194779, 139483, 139483, 139483, 139483",
      /*  5562 */ "139483, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 0, 139483",
      /*  5575 */ "235739, 139483, 237787, 0, 0, 195584, 0, 225280, 0, 0, 0, 139436, 139436, 139436, 139436, 139436",
      /*  5591 */ "139436, 139436, 139436, 139436, 139436, 139436, 139436, 139436, 139436, 176347, 139483, 183515",
      /*  5603 */ "139483, 139483, 139483, 139483, 199899, 231596, 139436, 139483, 139483, 139483, 139483, 139483",
      /*  5615 */ "139483, 139483, 139483, 195803, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 198875",
      /*  5627 */ "139483, 139483, 139483, 139483, 139483, 211163, 213211, 214235, 221403, 139483, 139483, 225499",
      /*  5639 */ "139483, 139483, 231643, 139483, 190464, 0, 0, 0, 0, 0, 139436, 139436, 139436, 139436, 195756",
      /*  5654 */ "139436, 139436, 139436, 139436, 139436, 221356, 139436, 139436, 225452, 139436, 139436, 139436",
      /*  5666 */ "139436, 139483, 139483, 189659, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 228352",
      /*  5678 */ "139436, 139436, 185516, 139436, 139436, 190636, 139436, 139436, 139436, 139436, 139436, 139436",
      /*  5690 */ "139436, 139436, 139436, 139436, 139436, 0, 0, 139483, 178395, 139483, 139483, 139483, 139483",
      /*  5703 */ "139483, 139483, 188635, 206043, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 139483, 0",
      /*  5716 */ "206848, 226304, 139436, 181420, 139436, 139436, 139436, 182444, 139436, 139436, 139436, 139436",
      /*  5728 */ "139436, 191660, 139436, 139436, 139436, 139436, 139436, 202924, 139436, 196780, 207020, 215212",
      /*  5740 */ "222380, 139436, 226476, 139436, 139436, 139436, 139483, 181467, 139483, 139483, 139483, 196827",
      /*  5752 */ "207067, 215259, 222427, 139483, 226523, 139483, 139483, 139483, 0, 139436, 186540, 139436, 139436",
      /*  5765 */ "139436, 139436, 139436, 188588, 139436, 139436, 139436, 139436, 139436, 139436, 139436, 198828",
      /*  5777 */ "139436, 139436, 139436, 192684, 139436, 216236, 139436, 139436, 139436, 228524, 139483, 139483",
      /*  5789 */ "192731, 139483, 216283, 139483, 139483, 139483, 185563, 139483, 139483, 190683, 139483, 139483",
      /*  5801 */ "139483, 139483, 139483, 139483, 139483, 139483, 139483, 0, 133120, 180224, 204800, 219136, 0",
      /*  5814 */ "272528, 143, 0, 228571, 139436, 139436, 139436, 139436, 139436, 139436, 139483, 139483, 139483",
      /*  5827 */ "139483, 139483, 139483, 177324, 139436, 139436, 139436, 211116, 213164, 214188, 139436, 139436",
      /*  5839 */ "139436, 139436, 139436, 139436, 139436, 139436, 139436, 139436, 193708, 139436, 139436, 139436",
      /*  5851 */ "139436, 200876, 201900, 139436, 139436, 139436, 227500, 177371, 139483, 139483, 139483, 139483",
      /*  5863 */ "227547, 139436, 197804, 223404, 139436, 139483, 197851, 223451, 139483, 187564, 139436, 187611",
      /*  5875 */ "139483, 139436, 139483, 139436, 139483, 139436, 139483, 224428, 224475, 0, 0, 0, 0, 0, 378, 0, 0, 0",
      /*  5893 */ "0, 0, 284, 384, 99, 99, 99, 99, 0, 118, 118, 118, 118, 118, 234, 118, 118, 118, 118, 118, 506, 118",
      /*  5915 */ "118, 118, 118, 118, 511, 118, 118, 118, 118, 0, 0, 0, 68, 68, 68, 71, 72, 138240, 0, 0, 0, 33792, 0",
      /*  5938 */ "0, 0, 0, 0, 455, 0, 0, 0, 0, 0, 0, 0, 0, 0, 384, 35936, 35936, 35936, 35936, 35936, 35936, 35936",
      /*  5960 */ "35936, 35936, 35936, 0, 0, 0, 0, 0, 0, 0, 0, 0, 23552, 23552, 23552, 23552, 23552, 23552, 23552",
      /*  5979 */ "23552, 23552, 23552, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 68, 68, 71, 72, 138240, 0, 0, 14336, 34816",
      /*  6001 */ "38912, 0, 0, 0, 0, 73, 0, 0, 73, 73, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 108, 127, 108, 108",
      /*  6025 */ "108, 108, 0, 0, 0, 68, 68, 68, 71, 72, 138240, 0, 0, 0, 0, 39936, 0, 0, 0, 0, 73, 0, 0, 73, 73, 85",
      /*  6051 */ "85, 85, 85, 85, 85, 85, 85, 85, 85, 109, 128, 109, 109, 109, 109, 0, 0, 0, 40960, 40960, 40960",
      /*  6072 */ "40960, 40960, 40960, 40960, 0, 0, 133120, 0, 0, 0, 0, 0, 15360, 0, 0, 15360, 15360, 0, 0, 0, 0, 0",
      /*  6094 */ "0, 0, 0, 0, 13388, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 41984, 41984, 41984, 41984, 41984, 41984, 41984",
      /*  6115 */ "0, 0, 133120, 0, 0, 0, 0, 0, 20480, 0, 0, 0, 0, 0, 0, 0, 0, 68, 0, 100, 100, 100, 119, 119, 119",
      /*  6140 */ "119, 119, 119, 119, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28672, 0, 18432, 133120, 30720, 0, 0, 0, 43008",
      /*  6162 */ "43008, 43008, 43008, 43008, 43008, 43008, 43008, 43008, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28672, 28672",
      /*  6181 */ "28672, 28672, 28672, 28672, 28672, 0, 0, 0, 43008, 43008, 43008, 43008, 43008, 43008, 43008, 0, 0",
      /*  6198 */ "133120, 0, 0, 0, 0, 0, 21504, 21504, 21504, 21504, 21504, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 163, 0, 0",
      /*  6221 */ "0, 0, 0, 0, 169, 0, 0, 101, 101, 101, 120, 120, 120, 120, 120, 120, 120, 0, 0, 0, 0, 0, 0, 0, 0, 0",
      /*  6247 */ "32845, 0, 0, 133120, 0, 0, 0, 171, 0, 99, 99, 99, 177, 99, 99, 99, 99, 99, 99, 199, 201, 99, 205",
      /*  6270 */ "99, 208, 99, 99, 0, 118, 118, 118, 224, 118, 118, 118, 118, 118, 118, 246, 248, 118, 252, 118, 255",
      /*  6291 */ "118, 118, 0, 0, 0, 0, 0, 0, 68, 68, 0, 0, 0, 0, 161, 0, 0, 0, 0, 166, 0, 0, 0, 166, 0, 0, 0, 0, 73",
      /*  6320 */ "0, 0, 73, 73, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 110, 129, 110, 110, 110, 110, 99, 99, 416, 99",
      /*  6344 */ "99, 11590, 384, 118, 118, 420, 118, 118, 118, 118, 118, 118, 118, 507, 118, 118, 118, 118, 118, 118",
      /*  6364 */ "118, 118, 0, 99, 99, 707, 99, 99, 99, 99, 99, 99, 99, 466, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99",
      /*  6388 */ "99, 99, 303, 99, 99, 99, 477, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 400",
      /*  6412 */ "99, 99, 118, 118, 118, 493, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 343, 118, 118, 118",
      /*  6432 */ "118, 118, 118, 118, 504, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 450, 118",
      /*  6452 */ "118, 0, 99, 99, 99, 99, 544, 99, 99, 99, 99, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 0",
      /*  6474 */ "99, 99, 99, 99, 99, 99, 99, 99, 99, 619, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 630, 99, 99",
      /*  6498 */ "99, 99, 99, 583, 99, 99, 99, 99, 99, 588, 99, 99, 590, 99, 99, 99, 99, 99, 689, 99, 99, 99, 99, 118",
      /*  6522 */ "118, 118, 118, 118, 118, 99, 99, 99, 819, 118, 118, 118, 118, 118, 118, 118, 636, 118, 118, 118",
      /*  6542 */ "118, 118, 118, 118, 118, 118, 118, 118, 355, 118, 118, 118, 118, 647, 118, 0, 0, 0, 0, 0, 99, 99",
      /*  6564 */ "99, 99, 99, 99, 99, 99, 99, 0, 0, 220, 118, 118, 118, 118, 526, 527, 99, 99, 99, 99, 99, 99, 99, 99",
      /*  6588 */ "99, 99, 99, 99, 99, 99, 476, 99, 118, 118, 118, 761, 118, 763, 118, 765, 118, 0, 99, 99, 99, 99, 99",
      /*  6611 */ "99, 99, 324, 99, 11590, 284, 220, 118, 118, 118, 118, 97, 97, 97, 97, 97, 97, 97, 97, 97, 97, 102",
      /*  6633 */ "121, 102, 102, 102, 102, 121, 121, 121, 121, 121, 121, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32845, 32845",
      /*  6656 */ "32845, 32845, 32845, 32845, 32845, 32845, 32845, 32845, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 68, 68, 71",
      /*  6677 */ "72, 73, 0, 148, 0, 0, 0, 0, 0, 0, 0, 0, 138240, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12288, 0, 0, 133120, 0",
      /*  6704 */ "0, 0, 99, 209, 99, 99, 0, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 562, 118, 118, 118",
      /*  6726 */ "118, 99, 99, 99, 99, 391, 99, 99, 99, 99, 99, 99, 99, 99, 99, 399, 99, 99, 99, 99, 99, 790, 791, 99",
      /*  6750 */ "118, 118, 118, 118, 118, 118, 796, 797, 118, 118, 118, 441, 442, 118, 118, 118, 118, 118, 118, 118",
      /*  6770 */ "118, 118, 118, 0, 0, 0, 99, 99, 99, 99, 99, 99, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118",
      /*  6792 */ "118, 603, 118, 118, 0, 0, 0, 0, 0, 99, 99, 99, 99, 99, 657, 99, 99, 99, 99, 99, 99, 622, 623, 99",
      /*  6816 */ "99, 99, 627, 99, 99, 99, 99, 0, 118, 118, 118, 118, 118, 118, 118, 118, 240, 118, 118, 118, 118",
      /*  6837 */ "760, 118, 118, 118, 118, 118, 118, 0, 99, 99, 99, 99, 99, 99, 99, 470, 99, 99, 99, 99, 99, 99, 99",
      /*  6860 */ "99, 99, 11590, 284, 220, 118, 118, 118, 332, 821, 99, 99, 118, 118, 99, 118, 99, 118, 99, 118, 99",
      /*  6881 */ "118, 0, 0, 0, 0, 0, 23552, 23552, 23552, 23552, 23552, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 272, 0, 0, 0",
      /*  6905 */ "0, 0, 0, 0, 0, 0, 0, 0, 284, 173, 99, 99, 99, 103, 103, 103, 122, 122, 122, 122, 122, 122, 122, 0",
      /*  6929 */ "0, 0, 0, 0, 0, 0, 0, 0, 35840, 35840, 35840, 35840, 35840, 35840, 35840, 0, 0, 156, 0, 0, 0, 0, 0",
      /*  6952 */ "0, 0, 0, 0, 156, 0, 0, 0, 0, 71, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 461, 0, 0, 384, 276, 0, 0, 0",
      /*  6982 */ "0, 0, 0, 0, 0, 0, 0, 284, 173, 287, 99, 99, 99, 99, 99, 480, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99",
      /*  7008 */ "99, 409, 99, 99, 99, 99, 99, 99, 99, 99, 467, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 474",
      /*  7032 */ "99, 99, 99, 99, 99, 118, 118, 118, 118, 494, 118, 118, 118, 118, 118, 118, 118, 118, 118, 434, 118",
      /*  7053 */ "118, 118, 118, 118, 118, 540, 99, 99, 99, 99, 99, 99, 99, 99, 118, 118, 118, 118, 118, 118, 118",
      /*  7074 */ "118, 118, 118, 118, 0, 579, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 487, 99, 99",
      /*  7098 */ "118, 118, 118, 118, 597, 118, 118, 118, 118, 118, 118, 118, 118, 118, 560, 118, 118, 118, 118, 118",
      /*  7118 */ "118, 99, 99, 728, 99, 99, 99, 99, 99, 99, 99, 118, 118, 738, 118, 118, 118, 118, 0, 518, 0, 0, 0, 0",
      /*  7142 */ "0, 0, 0, 0, 99, 99, 99, 99, 181, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 535, 99, 99, 99, 99",
      /*  7167 */ "118, 118, 118, 118, 762, 118, 118, 118, 118, 0, 99, 99, 99, 99, 99, 99, 99, 532, 99, 99, 99, 99, 99",
      /*  7190 */ "99, 99, 99, 99, 547, 118, 118, 118, 118, 118, 118, 104, 104, 104, 123, 123, 123, 123, 123, 123, 123",
      /*  7211 */ "0, 0, 0, 0, 0, 0, 0, 0, 0, 35936, 0, 0, 133120, 0, 31744, 0, 0, 154, 0, 159, 0, 162, 0, 164, 0, 0",
      /*  7237 */ "0, 0, 0, 0, 0, 0, 0, 0, 0, 284, 173, 99, 288, 99, 207, 210, 213, 99, 0, 118, 118, 118, 118, 227",
      /*  7261 */ "118, 118, 237, 118, 244, 118, 99, 99, 118, 118, 99, 118, 99, 118, 99, 118, 99, 118, 0, 0, 0, 0, 0",
      /*  7284 */ "0, 279, 0, 0, 0, 0, 284, 173, 99, 99, 99, 99, 0, 118, 118, 118, 118, 118, 232, 118, 118, 118, 118",
      /*  7307 */ "118, 118, 764, 118, 766, 0, 99, 99, 99, 99, 99, 99, 99, 585, 99, 99, 99, 99, 99, 99, 99, 99, 99",
      /*  7330 */ "587, 99, 99, 99, 99, 99, 99, 249, 118, 118, 254, 257, 260, 118, 0, 266, 0, 0, 0, 267, 68, 68, 0, 0",
      /*  7354 */ "0, 0, 270, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 66, 0, 0, 68, 0, 0, 0, 268, 0, 0, 0, 0, 0, 0, 0, 0, 0",
      /*  7385 */ "0, 0, 0, 0, 107520, 68, 0, 99, 99, 99, 307, 99, 99, 99, 99, 99, 99, 99, 99, 99, 314, 99, 99, 99, 99",
      /*  7410 */ "99, 530, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 301, 99, 99, 99, 99, 118, 118, 349, 118, 118",
      /*  7433 */ "118, 118, 118, 118, 118, 118, 118, 356, 118, 118, 118, 118, 0, 570, 0, 0, 0, 0, 0, 0, 99, 99, 99",
      /*  7456 */ "99, 99, 99, 99, 99, 192, 99, 99, 99, 99, 99, 374, 0, 376, 0, 0, 0, 0, 0, 0, 0, 0, 284, 384, 385, 99",
      /*  7482 */ "99, 99, 99, 99, 545, 99, 99, 99, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118",
      /*  7503 */ "605, 99, 402, 99, 99, 99, 99, 404, 405, 406, 99, 99, 99, 99, 411, 99, 413, 99, 99, 99, 417, 99",
      /*  7525 */ "11590, 384, 419, 118, 118, 118, 118, 118, 118, 118, 118, 0, 0, 0, 0, 0, 0, 616, 99, 118, 118, 428",
      /*  7547 */ "118, 118, 431, 118, 118, 118, 118, 118, 436, 118, 118, 118, 118, 0, 0, 0, 0, 0, 0, 0, 0, 99, 576",
      /*  7570 */ "99, 99, 438, 439, 440, 118, 118, 118, 118, 445, 118, 447, 118, 118, 118, 451, 118, 0, 0, 0, 0, 377",
      /*  7592 */ "0, 0, 0, 0, 0, 0, 284, 384, 99, 99, 99, 99, 99, 295, 99, 99, 99, 99, 300, 99, 99, 99, 99, 99, 99",
      /*  7617 */ "323, 99, 99, 11590, 284, 220, 118, 118, 118, 118, 0, 0, 453, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0",
      /*  7642 */ "384, 99, 99, 118, 118, 118, 118, 118, 495, 496, 118, 498, 118, 118, 118, 118, 118, 118, 444, 118",
      /*  7662 */ "118, 118, 118, 118, 118, 118, 118, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 99, 99, 552, 118, 118, 118, 118",
      /*  7686 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 0, 99, 99, 118, 118, 118, 118, 118, 118",
      /*  7707 */ "118, 118, 118, 118, 118, 118, 604, 118, 99, 99, 118, 118, 99, 118, 99, 118, 828, 829, 99, 118, 0, 0",
      /*  7729 */ "0, 0, 73, 0, 0, 73, 73, 0, 0, 0, 0, 95, 0, 95, 618, 99, 99, 99, 99, 621, 99, 99, 624, 99, 99, 99",
      /*  7755 */ "99, 629, 631, 99, 99, 99, 99, 99, 11590, 0, 118, 118, 118, 118, 118, 118, 118, 118, 118, 0, 99, 99",
      /*  7777 */ "99, 99, 771, 99, 118, 118, 635, 118, 118, 118, 118, 638, 118, 118, 641, 118, 118, 118, 118, 646",
      /*  7797 */ "648, 118, 0, 0, 0, 0, 0, 99, 654, 99, 99, 99, 99, 99, 99, 99, 99, 586, 99, 99, 99, 99, 99, 99, 99",
      /*  7822 */ "99, 667, 118, 118, 118, 670, 118, 118, 118, 99, 660, 99, 99, 99, 99, 99, 99, 118, 668, 118, 118",
      /*  7843 */ "118, 118, 118, 118, 118, 25600, 133385, 0, 0, 0, 0, 68, 68, 0, 118, 118, 674, 118, 118, 118, 118",
      /*  7864 */ "118, 118, 681, 0, 0, 99, 99, 99, 99, 99, 99, 189, 99, 99, 99, 99, 99, 99, 99, 310, 99, 99, 312, 99",
      /*  7888 */ "99, 99, 99, 99, 118, 118, 118, 745, 0, 99, 99, 749, 99, 99, 99, 99, 99, 99, 99, 118, 778, 118, 118",
      /*  7911 */ "118, 118, 118, 118, 118, 118, 0, 99, 99, 99, 99, 99, 710, 99, 118, 759, 118, 118, 118, 118, 118",
      /*  7932 */ "118, 118, 0, 768, 99, 99, 770, 99, 772, 99, 99, 99, 99, 777, 118, 118, 779, 118, 781, 118, 118, 118",
      /*  7954 */ "118, 0, 99, 99, 99, 99, 99, 752, 99, 99, 99, 99, 118, 105, 105, 105, 124, 124, 124, 124, 124, 124",
      /*  7976 */ "124, 0, 0, 0, 0, 0, 0, 0, 0, 0, 37966, 0, 0, 133120, 0, 0, 0, 0, 140, 0, 68, 68, 68, 71, 72, 73, 0",
      /*  8003 */ "0, 0, 0, 0, 0, 0, 0, 0, 76, 76, 76, 76, 76, 76, 76, 99, 99, 99, 543, 99, 99, 99, 99, 99, 118, 118",
      /*  8029 */ "118, 118, 118, 118, 118, 0, 0, 0, 0, 0, 0, 68, 68, 0, 118, 798, 99, 99, 99, 99, 99, 804, 118, 118",
      /*  8053 */ "118, 118, 118, 99, 99, 99, 99, 99, 99, 118, 118, 118, 118, 118, 118, 99, 99, 99, 99, 118, 118, 118",
      /*  8075 */ "99, 99, 99, 118, 118, 118, 118, 118, 118, 818, 99, 99, 99, 820, 118, 118, 0, 0, 0, 0, 0, 99, 99, 99",
      /*  8099 */ "655, 99, 99, 99, 99, 99, 99, 481, 482, 99, 99, 99, 99, 99, 99, 99, 99, 99, 625, 626, 99, 99, 99, 99",
      /*  8123 */ "99, 106, 106, 106, 125, 125, 125, 125, 125, 125, 125, 0, 0, 0, 0, 0, 0, 0, 0, 0, 37966, 37966",
      /*  8145 */ "37966, 37966, 37966, 37966, 37966, 37966, 37966, 37966, 0, 0, 0, 0, 0, 0, 99, 99, 661, 99, 99, 99",
      /*  8165 */ "99, 99, 118, 118, 118, 118, 118, 118, 672, 118, 99, 99, 118, 118, 99, 118, 826, 827, 99, 118, 99",
      /*  8186 */ "118, 0, 0, 0, 0, 73, 0, 0, 73, 73, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 103, 122, 103, 103, 103",
      /*  8211 */ "103, 118, 118, 118, 675, 118, 118, 118, 118, 118, 0, 0, 0, 99, 99, 99, 99, 99, 183, 99, 99, 99, 99",
      /*  8234 */ "99, 99, 99, 99, 99, 692, 118, 118, 118, 118, 118, 118, 118, 118, 118, 700, 118, 118, 118, 118, 0",
      /*  8255 */ "99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 118, 812, 99, 99, 118, 118, 815, 816, 118, 118, 99, 99, 99",
      /*  8278 */ "99, 118, 118, 118, 118, 118, 720, 118, 118, 118, 118, 118, 0, 0, 0, 141, 68, 68, 68, 71, 72, 73, 0",
      /*  8301 */ "0, 0, 0, 0, 0, 0, 0, 0, 458, 0, 0, 0, 0, 0, 384, 0, 0, 0, 269, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0",
      /*  8332 */ "67, 0, 68, 0, 318, 99, 99, 99, 99, 99, 99, 99, 99, 11590, 284, 220, 118, 118, 118, 118, 0, 0, 0, 0",
      /*  8356 */ "0, 0, 0, 0, 575, 99, 99, 99, 567, 118, 118, 118, 0, 0, 0, 0, 0, 0, 0, 0, 99, 99, 99, 99, 99, 184",
      /*  8382 */ "99, 99, 99, 99, 99, 99, 99, 99, 99, 735, 118, 118, 118, 118, 118, 118, 107, 107, 107, 126, 126, 126",
      /*  8404 */ "126, 126, 126, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 44032, 44032, 44032, 44032, 44032, 44032, 44032",
      /*  8424 */ "44032, 44032, 44032, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 68, 68, 71, 72, 73, 0, 0, 0, 0, 0, 0, 150, 99",
      /*  8450 */ "291, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 304, 99, 99, 99, 321, 99, 99, 99, 99, 325",
      /*  8474 */ "11590, 284, 220, 118, 118, 118, 118, 0, 0, 0, 0, 0, 0, 0, 574, 99, 99, 577, 99, 333, 118, 118, 118",
      /*  8497 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 346, 118, 99, 99, 118, 118, 824, 825, 99, 118, 99",
      /*  8518 */ "118, 99, 118, 0, 0, 0, 0, 72, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 133120, 0, 0, 0, 118, 118, 363",
      /*  8545 */ "118, 118, 118, 118, 367, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 284, 173, 286, 99, 99, 99, 99, 465, 99",
      /*  8570 */ "99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 315, 99, 99, 99, 99, 118, 118, 492, 118, 118, 118",
      /*  8593 */ "118, 118, 118, 118, 118, 118, 118, 118, 642, 643, 118, 118, 118, 99, 592, 118, 118, 118, 118, 118",
      /*  8613 */ "118, 118, 118, 118, 601, 118, 118, 118, 118, 0, 0, 0, 0, 0, 0, 573, 0, 99, 99, 99, 99, 0, 118, 118",
      /*  8637 */ "118, 118, 118, 118, 118, 118, 239, 118, 118, 118, 606, 118, 118, 608, 118, 118, 610, 0, 0, 0, 0, 0",
      /*  8659 */ "0, 99, 99, 99, 179, 182, 99, 188, 99, 99, 99, 200, 203, 99, 99, 99, 99, 99, 99, 730, 99, 732, 733",
      /*  8682 */ "734, 99, 118, 118, 118, 118, 740, 118, 99, 99, 800, 801, 99, 99, 118, 118, 806, 807, 118, 118, 99",
      /*  8703 */ "99, 99, 99, 99, 99, 118, 118, 118, 118, 118, 118, 99, 99, 811, 742, 743, 744, 118, 0, 99, 99, 99",
      /*  8725 */ "99, 99, 99, 99, 99, 99, 99, 118, 118, 118, 118, 118, 118, 118, 118, 99, 99, 788, 99, 789, 99, 99",
      /*  8747 */ "99, 118, 118, 118, 794, 118, 795, 118, 118, 0, 0, 0, 0, 0, 653, 99, 99, 99, 656, 99, 99, 99, 99, 0",
      /*  8771 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 345, 118, 118, 118, 118, 108, 108, 108, 127",
      /*  8791 */ "127, 127, 127, 127, 127, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 108636, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 68",
      /*  8817 */ "68, 68, 71, 72, 73, 147, 0, 0, 0, 0, 0, 0, 0, 0, 0, 108636, 108636, 108636, 108636, 108636, 108636",
      /*  8838 */ "108636, 108636, 108636, 108636, 0, 0, 0, 0, 0, 0, 151, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0",
      /*  8864 */ "36864, 99, 319, 99, 99, 99, 99, 99, 99, 99, 11590, 284, 220, 118, 118, 118, 118, 0, 0, 0, 0, 0, 522",
      /*  8887 */ "0, 0, 0, 0, 99, 99, 99, 178, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 302, 99, 99, 99, 99, 361",
      /*  8912 */ "118, 118, 118, 118, 118, 118, 118, 0, 0, 0, 0, 0, 0, 0, 0, 0, 524, 99, 99, 99, 99, 389, 99, 99, 99",
      /*  8937 */ "99, 99, 99, 99, 99, 99, 398, 99, 99, 99, 99, 0, 118, 118, 118, 225, 118, 118, 118, 118, 118, 118",
      /*  8959 */ "118, 0, 133120, 0, 0, 0, 0, 68, 68, 0, 99, 99, 118, 118, 595, 118, 118, 118, 118, 118, 118, 118",
      /*  8981 */ "118, 118, 118, 118, 344, 118, 118, 118, 118, 118, 118, 118, 650, 0, 0, 0, 0, 99, 99, 99, 99, 99, 99",
      /*  9004 */ "99, 99, 99, 99, 118, 118, 118, 695, 118, 118, 773, 99, 99, 99, 118, 118, 118, 118, 780, 118, 782",
      /*  9025 */ "118, 118, 118, 0, 786, 99, 99, 99, 118, 814, 118, 118, 118, 118, 99, 99, 99, 99, 118, 118, 118, 118",
      /*  9047 */ "719, 118, 721, 118, 118, 118, 118, 0, 109, 109, 109, 128, 128, 128, 128, 128, 128, 128, 0, 0, 0, 0",
      /*  9069 */ "0, 0, 0, 0, 0, 110592, 110592, 110592, 110592, 110592, 110592, 110592, 110592, 110592, 110592, 0, 0",
      /*  9086 */ "0, 0, 0, 0, 99, 99, 292, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 475, 99, 99, 99, 99",
      /*  9111 */ "320, 99, 99, 99, 99, 99, 99, 11590, 284, 220, 118, 118, 118, 118, 0, 0, 0, 0, 0, 572, 0, 0, 99, 99",
      /*  9135 */ "99, 99, 99, 185, 99, 99, 99, 99, 99, 99, 99, 99, 99, 11590, 284, 220, 118, 330, 118, 118, 118, 334",
      /*  9157 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 565, 118, 118, 362, 118, 118",
      /*  9177 */ "118, 118, 118, 118, 0, 0, 0, 371, 0, 0, 0, 0, 0, 0, 0, 61, 61, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0",
      /*  9206 */ "0, 68, 0, 99, 99, 99, 390, 99, 99, 99, 393, 99, 99, 99, 99, 99, 99, 99, 99, 99, 11590, 284, 220",
      /*  9229 */ "118, 118, 118, 118, 99, 99, 403, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 486, 99, 99",
      /*  9252 */ "118, 427, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 437, 118, 118, 118, 118, 118, 556, 118",
      /*  9272 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 704, 99, 99, 99, 99, 99, 99, 99, 0, 0, 0, 454, 0, 0, 0",
      /*  9296 */ "0, 0, 0, 0, 0, 0, 0, 0, 384, 99, 99, 118, 118, 118, 118, 118, 118, 118, 497, 118, 118, 118, 118",
      /*  9319 */ "118, 118, 340, 118, 118, 118, 118, 118, 118, 118, 118, 118, 265, 369, 0, 0, 0, 0, 0, 0, 99, 99, 714",
      /*  9342 */ "99, 118, 118, 118, 118, 118, 118, 118, 118, 118, 724, 118, 0, 0, 0, 0, 19456, 0, 0, 0, 0, 0, 0, 0",
      /*  9366 */ "0, 0, 68, 0, 0, 0, 0, 73, 0, 0, 73, 73, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 116, 135, 116, 116",
      /*  9392 */ "116, 116, 110, 110, 110, 129, 129, 129, 129, 129, 129, 129, 0, 0, 0, 0, 0, 0, 0, 0, 165, 0, 0, 0, 0",
      /*  9417 */ "0, 170, 0, 139, 0, 0, 68, 68, 68, 71, 72, 73, 0, 0, 0, 0, 0, 149, 0, 0, 0, 0, 26722, 26722, 26722",
      /*  9442 */ "26722, 26722, 0, 0, 0, 133120, 0, 0, 0, 0, 0, 0, 22528, 0, 0, 0, 0, 0, 0, 0, 68, 0, 0, 0, 157, 0, 0",
      /*  9469 */ "0, 0, 0, 0, 0, 167, 0, 168, 0, 0, 0, 0, 73, 0, 0, 73, 73, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 459, 0, 0",
      /*  9499 */ "0, 0, 384, 99, 99, 99, 217, 0, 118, 118, 221, 118, 118, 118, 118, 118, 241, 118, 118, 118, 118, 118",
      /*  9521 */ "677, 118, 118, 118, 0, 0, 0, 99, 99, 99, 99, 99, 186, 99, 99, 99, 99, 99, 99, 99, 99, 99, 11590",
      /*  9544 */ "284, 220, 328, 118, 118, 118, 99, 489, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118",
      /*  9564 */ "118, 118, 645, 118, 118, 118, 118, 516, 517, 0, 0, 0, 521, 0, 0, 523, 0, 0, 99, 99, 174, 99, 99, 99",
      /*  9588 */ "99, 99, 194, 99, 99, 99, 99, 99, 99, 392, 99, 99, 99, 99, 99, 99, 99, 99, 99, 533, 99, 99, 99, 99",
      /*  9612 */ "99, 539, 99, 99, 99, 528, 529, 99, 531, 99, 99, 99, 99, 99, 536, 99, 99, 99, 99, 0, 118, 118, 223",
      /*  9635 */ "118, 118, 118, 118, 238, 243, 245, 118, 99, 99, 542, 99, 99, 99, 99, 99, 99, 118, 118, 118, 118",
      /*  9656 */ "118, 550, 551, 118, 553, 118, 118, 118, 118, 118, 558, 118, 118, 118, 118, 118, 564, 118, 118, 118",
      /*  9676 */ "118, 118, 701, 118, 118, 0, 99, 99, 99, 99, 99, 99, 99, 298, 99, 99, 99, 99, 99, 99, 99, 99, 99",
      /*  9699 */ "11590, 284, 220, 118, 118, 331, 118, 99, 580, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99",
      /*  9721 */ "99, 538, 99, 99, 99, 118, 118, 118, 118, 118, 598, 118, 118, 118, 118, 118, 118, 118, 118, 0, 611",
      /*  9742 */ "0, 613, 614, 615, 99, 99, 99, 99, 99, 662, 99, 99, 99, 99, 118, 118, 118, 669, 118, 118, 118, 118",
      /*  9764 */ "0, 0, 0, 571, 0, 0, 0, 0, 99, 99, 99, 99, 99, 99, 99, 99, 195, 99, 99, 99, 99, 99, 118, 118, 118",
      /*  9789 */ "118, 676, 118, 118, 118, 118, 0, 0, 0, 99, 99, 99, 684, 99, 99, 99, 729, 99, 99, 99, 99, 99, 99",
      /*  9812 */ "118, 118, 118, 739, 118, 118, 118, 118, 256, 118, 118, 0, 0, 0, 0, 0, 0, 68, 68, 0, 118, 118, 118",
      /*  9835 */ "118, 746, 99, 99, 99, 99, 99, 99, 99, 754, 99, 756, 118, 99, 799, 99, 99, 99, 803, 118, 805, 118",
      /*  9857 */ "118, 118, 809, 99, 99, 99, 99, 0, 118, 118, 118, 118, 228, 118, 118, 118, 118, 118, 118, 264, 0, 0",
      /*  9879 */ "0, 0, 0, 0, 68, 68, 0, 111, 111, 111, 130, 130, 130, 130, 130, 130, 130, 0, 0, 0, 0, 0, 0, 0, 0",
      /*  9904 */ "273, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 68, 0, 99, 211, 214, 99, 0, 118, 118, 118, 226, 229",
      /*  9931 */ "118, 235, 118, 118, 118, 247, 250, 118, 118, 118, 258, 261, 118, 0, 0, 0, 0, 0, 0, 68, 68, 0, 0, 0",
      /*  9955 */ "0, 107520, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 274, 0, 0, 0, 0, 99, 99, 99, 99, 418, 11590, 384, 118",
      /*  9981 */ "118, 118, 118, 118, 118, 118, 118, 118, 0, 705, 99, 99, 99, 99, 99, 99, 99, 99, 593, 118, 118, 118",
      /* 10003 */ "118, 118, 118, 118, 118, 118, 602, 118, 118, 118, 118, 118, 609, 118, 118, 0, 0, 0, 0, 0, 0, 99, 99",
      /* 10026 */ "99, 99, 180, 99, 99, 190, 99, 197, 99, 202, 99, 99, 99, 727, 99, 99, 99, 731, 99, 99, 99, 99, 118",
      /* 10049 */ "737, 118, 118, 118, 741, 112, 112, 112, 131, 131, 131, 131, 131, 131, 131, 0, 0, 0, 0, 0, 0, 0, 0",
      /* 10072 */ "281, 0, 0, 284, 173, 99, 99, 99, 152, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 0, 113, 113",
      /* 10100 */ "113, 132, 132, 132, 132, 132, 132, 132, 0, 0, 0, 0, 0, 0, 0, 0, 381, 0, 0, 284, 384, 99, 99, 386",
      /* 10124 */ "99, 99, 215, 99, 0, 118, 118, 222, 118, 118, 118, 118, 118, 118, 118, 118, 266, 0, 0, 0, 0, 0, 0, 0",
      /* 10148 */ "118, 251, 253, 118, 118, 262, 118, 0, 0, 0, 0, 0, 0, 68, 68, 0, 0, 0, 64, 73, 64, 0, 73, 73, 79, 79",
      /* 10174 */ "79, 79, 79, 79, 79, 79, 79, 79, 101, 120, 101, 101, 101, 101, 387, 99, 99, 99, 99, 99, 99, 99, 99",
      /* 10197 */ "99, 396, 99, 99, 99, 99, 99, 99, 584, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 485, 99, 99, 99, 99",
      /* 10221 */ "99, 118, 118, 118, 118, 430, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 433, 118, 118",
      /* 10241 */ "118, 118, 118, 118, 118, 99, 478, 99, 99, 99, 99, 99, 99, 483, 99, 99, 99, 99, 99, 99, 99, 99, 792",
      /* 10264 */ "118, 118, 118, 118, 118, 118, 118, 118, 134144, 368, 0, 0, 0, 0, 0, 0, 118, 118, 118, 505, 118, 118",
      /* 10286 */ "118, 118, 118, 118, 510, 118, 118, 118, 118, 118, 338, 118, 118, 341, 118, 118, 118, 118, 118, 118",
      /* 10306 */ "118, 0, 266, 0, 0, 0, 0, 68, 68, 0, 633, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118",
      /* 10329 */ "118, 118, 118, 360, 99, 713, 99, 99, 715, 118, 118, 118, 118, 118, 118, 118, 723, 118, 118, 0, 0, 0",
      /* 10351 */ "0, 0, 99, 99, 99, 99, 99, 99, 658, 99, 99, 758, 118, 118, 118, 118, 118, 118, 118, 118, 0, 99, 99",
      /* 10374 */ "99, 99, 99, 99, 99, 666, 118, 118, 118, 118, 118, 118, 118, 118, 0, 0, 0, 0, 0, 0, 99, 617, 114",
      /* 10397 */ "114, 114, 133, 133, 133, 133, 133, 133, 133, 0, 0, 0, 0, 0, 138, 0, 0, 158, 0, 0, 0, 0, 0, 0, 0, 0",
      /* 10423 */ "0, 158, 0, 0, 0, 0, 73, 0, 0, 73, 73, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 106, 125, 106, 106",
      /* 10448 */ "106, 106, 290, 99, 99, 293, 99, 99, 297, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 693, 118, 118, 118",
      /* 10471 */ "118, 118, 99, 305, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 537, 99, 99, 118, 118",
      /* 10494 */ "335, 118, 118, 339, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 446, 118, 118, 118, 118, 118",
      /* 10514 */ "118, 0, 347, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 369, 463",
      /* 10534 */ "99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 591, 99, 99, 99, 479, 99, 99, 99, 99",
      /* 10558 */ "99, 484, 99, 99, 99, 99, 99, 99, 99, 690, 99, 99, 118, 118, 694, 118, 118, 118, 488, 99, 490, 118",
      /* 10580 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 512, 118, 118, 118, 118, 118, 515, 118",
      /* 10600 */ "0, 0, 0, 520, 0, 0, 0, 0, 0, 0, 99, 99, 175, 99, 99, 99, 99, 99, 99, 99, 99, 99, 204, 206, 99, 541",
      /* 10626 */ "99, 99, 99, 99, 99, 99, 99, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 501, 118",
      /* 10647 */ "99, 99, 99, 582, 99, 99, 99, 99, 99, 99, 99, 99, 589, 99, 99, 99, 99, 99, 99, 665, 99, 118, 118",
      /* 10670 */ "118, 118, 118, 118, 118, 118, 0, 0, 612, 0, 0, 0, 99, 99, 99, 99, 118, 118, 118, 118, 118, 118, 118",
      /* 10693 */ "600, 118, 118, 118, 118, 118, 118, 352, 118, 118, 354, 118, 118, 118, 118, 118, 118, 118, 703, 0",
      /* 10713 */ "99, 99, 99, 99, 99, 99, 99, 99, 691, 99, 118, 118, 118, 118, 118, 118, 118, 118, 607, 118, 118, 118",
      /* 10735 */ "118, 118, 0, 0, 0, 0, 0, 0, 99, 99, 176, 99, 99, 99, 99, 191, 196, 198, 99, 99, 99, 99, 0, 118, 118",
      /* 10760 */ "118, 118, 118, 118, 118, 118, 242, 118, 118, 99, 99, 99, 99, 663, 99, 99, 99, 118, 118, 118, 118",
      /* 10781 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 452, 0, 99, 774, 99, 99, 118, 118, 118, 118, 118",
      /* 10802 */ "118, 118, 783, 118, 118, 0, 99, 99, 99, 99, 99, 11590, 384, 118, 118, 118, 118, 118, 118, 118, 118",
      /* 10823 */ "118, 0, 99, 769, 99, 99, 99, 99, 787, 99, 99, 99, 99, 99, 99, 99, 118, 793, 118, 118, 118, 118, 118",
      /* 10846 */ "118, 366, 118, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 284, 384, 99, 99, 99, 0, 155, 0, 160, 0, 0, 0, 0, 0",
      /* 10874 */ "0, 0, 0, 0, 0, 0, 0, 173, 99, 99, 99, 0, 0, 0, 277, 278, 0, 0, 275, 0, 0, 0, 284, 173, 99, 99, 289",
      /* 10901 */ "99, 306, 99, 99, 308, 99, 99, 99, 99, 99, 99, 99, 99, 99, 316, 317, 348, 118, 118, 350, 118, 118",
      /* 10923 */ "118, 118, 118, 118, 118, 118, 118, 358, 359, 118, 99, 822, 118, 823, 99, 118, 99, 118, 99, 118, 99",
      /* 10944 */ "118, 0, 0, 0, 0, 73, 0, 0, 73, 73, 83, 93, 93, 93, 93, 93, 93, 93, 93, 93, 93, 107, 126, 107, 107",
      /* 10969 */ "107, 107, 99, 388, 99, 99, 99, 99, 99, 99, 99, 395, 99, 99, 99, 99, 99, 99, 296, 99, 99, 299, 99",
      /* 10992 */ "99, 99, 99, 99, 99, 99, 546, 99, 118, 118, 118, 118, 118, 118, 118, 353, 118, 118, 118, 118, 118",
      /* 11013 */ "118, 118, 118, 0, 99, 99, 99, 99, 709, 99, 711, 401, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99",
      /* 11037 */ "99, 99, 99, 313, 99, 99, 99, 414, 415, 99, 99, 99, 11590, 384, 118, 118, 118, 118, 422, 118, 118",
      /* 11058 */ "118, 118, 0, 99, 99, 99, 99, 751, 99, 753, 99, 755, 99, 118, 118, 118, 118, 429, 118, 118, 118, 118",
      /* 11080 */ "118, 118, 435, 118, 118, 118, 118, 118, 351, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 448",
      /* 11100 */ "449, 118, 118, 118, 0, 503, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118",
      /* 11120 */ "118, 513, 118, 514, 118, 118, 0, 0, 519, 0, 0, 0, 0, 0, 0, 0, 525, 99, 99, 99, 99, 99, 11590, 384",
      /* 11144 */ "118, 118, 118, 118, 118, 118, 118, 425, 118, 99, 99, 99, 99, 802, 99, 118, 118, 118, 118, 808, 118",
      /* 11165 */ "99, 99, 99, 99, 99, 99, 118, 118, 118, 118, 118, 118, 99, 810, 99, 118, 118, 118, 118, 555, 118",
      /* 11186 */ "118, 118, 118, 118, 561, 118, 118, 118, 118, 118, 365, 118, 118, 266, 0, 0, 0, 372, 0, 0, 373, 118",
      /* 11208 */ "118, 118, 118, 569, 0, 0, 0, 0, 0, 0, 0, 99, 99, 99, 578, 99, 99, 581, 99, 99, 99, 99, 99, 99, 99",
      /* 11233 */ "99, 99, 99, 99, 99, 99, 410, 99, 99, 99, 99, 99, 118, 118, 118, 596, 118, 118, 599, 118, 118, 118",
      /* 11255 */ "118, 118, 118, 118, 265, 137, 0, 0, 0, 0, 68, 68, 0, 99, 99, 99, 620, 99, 99, 99, 99, 99, 99, 99",
      /* 11279 */ "99, 99, 99, 99, 632, 118, 634, 118, 118, 118, 637, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118",
      /* 11300 */ "508, 509, 118, 118, 118, 118, 118, 118, 118, 649, 0, 651, 0, 0, 0, 99, 99, 99, 99, 99, 99, 99, 99",
      /* 11323 */ "99, 407, 408, 99, 99, 99, 99, 99, 659, 99, 99, 99, 99, 664, 99, 99, 118, 118, 118, 118, 118, 118",
      /* 11345 */ "118, 118, 118, 118, 118, 118, 118, 502, 118, 673, 118, 118, 118, 118, 678, 118, 118, 0, 0, 0, 99",
      /* 11366 */ "99, 683, 99, 99, 99, 99, 99, 11590, 384, 118, 118, 118, 118, 118, 118, 424, 118, 118, 0, 0, 652, 0",
      /* 11388 */ "0, 99, 99, 99, 99, 99, 99, 99, 99, 99, 118, 118, 548, 549, 118, 118, 118, 99, 99, 99, 776, 118, 118",
      /* 11411 */ "118, 118, 118, 118, 118, 118, 118, 785, 0, 99, 99, 99, 99, 99, 11590, 384, 118, 118, 118, 118, 118",
      /* 11432 */ "423, 118, 118, 118, 118, 0, 747, 99, 99, 99, 99, 99, 99, 99, 99, 99, 757, 116, 116, 116, 135, 135",
      /* 11454 */ "135, 135, 135, 135, 135, 0, 0, 0, 0, 0, 0, 0, 0, 110592, 110592, 0, 0, 133120, 0, 0, 0, 426, 118",
      /* 11477 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 566, 99, 99, 118, 594, 118",
      /* 11497 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 559, 118, 118, 118, 563, 118, 118, 118, 685",
      /* 11517 */ "99, 99, 99, 99, 99, 99, 99, 99, 99, 118, 118, 118, 118, 696, 118, 118, 118, 118, 337, 118, 118, 118",
      /* 11539 */ "118, 342, 118, 118, 118, 118, 118, 118, 118, 679, 118, 0, 0, 0, 99, 99, 99, 99, 99, 99, 99, 99, 193",
      /* 11562 */ "99, 99, 99, 99, 99, 99, 99, 775, 99, 118, 118, 118, 118, 118, 118, 118, 118, 784, 118, 0, 99, 99",
      /* 11584 */ "99, 99, 99, 11590, 384, 118, 118, 118, 421, 118, 118, 118, 118, 118, 263, 118, 0, 0, 0, 0, 0, 0, 68",
      /* 11607 */ "68, 0, 99, 813, 99, 118, 118, 118, 118, 817, 118, 99, 99, 99, 99, 118, 118, 118, 718, 118, 118, 118",
      /* 11629 */ "118, 118, 118, 118, 725, 99, 99, 99, 99, 294, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 628",
      /* 11652 */ "99, 99, 99, 118, 118, 118, 336, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 639",
      /* 11672 */ "640, 118, 118, 118, 644, 118, 118, 0, 375, 0, 0, 0, 0, 0, 0, 0, 0, 0, 284, 384, 99, 99, 99, 99, 99",
      /* 11697 */ "468, 469, 99, 471, 99, 99, 99, 99, 99, 99, 99, 99, 311, 99, 99, 99, 99, 99, 99, 99, 99, 394, 99, 99",
      /* 11721 */ "397, 99, 99, 99, 99, 99, 212, 99, 99, 0, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 118, 680",
      /* 11743 */ "0, 0, 0, 99, 99, 99, 99, 118, 118, 568, 118, 0, 0, 0, 0, 0, 0, 0, 0, 99, 99, 99, 99, 99, 187, 99",
      /* 11769 */ "99, 99, 99, 99, 99, 99, 99, 99, 11590, 284, 220, 329, 118, 118, 118, 153, 0, 0, 0, 0, 0, 0, 0, 0, 0",
      /* 11794 */ "0, 0, 0, 0, 0, 0, 275, 0, 0, 0, 99, 99, 99, 99, 322, 99, 99, 99, 99, 11590, 284, 220, 118, 118, 118",
      /* 11819 */ "118, 0, 99, 748, 99, 99, 99, 99, 99, 99, 99, 99, 118, 118, 118, 118, 118, 671, 118, 118, 118, 118",
      /* 11841 */ "118, 364, 118, 118, 118, 118, 0, 0, 0, 0, 0, 0, 0, 0, 99, 99, 99, 99, 99, 99, 216, 99, 0, 118, 118",
      /* 11866 */ "118, 118, 118, 118, 118, 118, 118, 118, 118, 134144, 0, 0, 0, 0, 0, 0, 0, 99, 464, 99, 99, 99, 99",
      /* 11889 */ "99, 99, 99, 99, 472, 473, 99, 99, 99, 99, 0, 118, 118, 118, 118, 118, 118, 236, 118, 118, 118, 118",
      /* 11911 */ "0, 99, 99, 99, 750, 99, 99, 99, 99, 99, 99, 118, 118, 717, 118, 118, 118, 118, 118, 118, 118, 118",
      /* 11933 */ "0, 0, 0, 0, 0, 0, 99, 99, 99, 99, 118, 491, 118, 118, 118, 118, 118, 118, 118, 118, 499, 500, 118",
      /* 11956 */ "118, 118, 118, 259, 118, 118, 0, 0, 0, 0, 0, 0, 68, 68, 0, 0, 0, 0, 107520, 0, 0, 0, 0, 0, 0, 0, 0",
      /* 11983 */ "107520, 0, 107520, 0, 0, 0, 68, 68, 68, 71, 72, 73, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 105, 124, 105",
      /* 12008 */ "105, 105, 105, 0, 0, 139264, 139437, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264",
      /* 12023 */ "139264, 139264, 139264, 139264, 0, 133120, 180224, 204800, 219136, 0, 0, 0, 0, 0, 217088, 220160, 0",
      /* 12040 */ "0, 234496, 0, 0, 0, 0, 0, 0, 0, 139264, 139264, 139264, 139264, 139264, 139264, 235520, 139264",
      /* 12057 */ "237568, 139264, 179200, 139264, 139264, 184320, 139264, 139264, 139264, 139264, 139264, 194560",
      /* 12069 */ "139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 139264, 0",
      /* 12082 */ "217088, 220160, 0, 0, 234496, 0, 0, 0, 0, 0, 0, 285, 139264, 139264, 139264, 139264, 139264, 182272",
      /* 12100 */ "139264, 139264, 139264, 139264, 139264, 191488, 139264, 139264, 139264, 139264, 139264, 202752"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 12112; ++i) {TRANSITION[i] = Integer.parseInt(s2[i]);}
  }

  private static final int[] EXPECTED = new int[1277];
  static
  {
    final String s1[] =
    {
      /*    0 */ "208, 212, 216, 220, 224, 247, 231, 236, 242, 246, 562, 232, 232, 252, 278, 278, 261, 562, 232, 232",
      /*   20 */ "259, 278, 279, 691, 232, 232, 277, 278, 255, 232, 291, 278, 294, 232, 277, 238, 232, 278, 265, 267",
      /*   40 */ "272, 275, 283, 268, 275, 284, 288, 297, 267, 301, 302, 306, 310, 314, 318, 322, 326, 680, 330, 466",
      /*   60 */ "336, 340, 527, 346, 247, 414, 353, 247, 426, 543, 359, 247, 504, 397, 520, 372, 376, 478, 546, 247",
      /*   80 */ "385, 396, 389, 378, 393, 402, 409, 406, 418, 424, 430, 420, 577, 443, 437, 447, 454, 494, 440, 450",
      /*  100 */ "491, 501, 473, 477, 412, 482, 485, 488, 227, 511, 498, 368, 433, 508, 519, 598, 524, 247, 660, 530",
      /*  120 */ "536, 555, 349, 540, 247, 550, 532, 554, 591, 560, 571, 566, 575, 581, 248, 585, 589, 595, 703, 569",
      /*  140 */ "608, 602, 469, 606, 612, 616, 620, 625, 632, 621, 636, 628, 639, 643, 647, 651, 657, 664, 668, 672",
      /*  160 */ "247, 678, 653, 355, 684, 556, 247, 247, 332, 247, 247, 342, 674, 247, 247, 362, 247, 247, 689, 695",
      /*  180 */ "247, 700, 247, 365, 695, 685, 247, 463, 247, 460, 696, 247, 381, 513, 379, 398, 247, 515, 380, 457",
      /*  200 */ "247, 247, 247, 247, 247, 247, 247, 227, 707, 711, 715, 717, 721, 725, 729, 731, 735, 739, 743, 747",
      /*  220 */ "751, 755, 758, 762, 992, 773, 1273, 765, 764, 1136, 1004, 785, 888, 888, 888, 888, 888, 1028, 789",
      /*  239 */ "789, 765, 886, 789, 794, 798, 803, 775, 765, 765, 765, 765, 766, 888, 888, 1250, 789, 765, 765, 886",
      /*  259 */ "888, 1239, 789, 789, 815, 765, 893, 885, 888, 888, 789, 789, 892, 789, 789, 894, 888, 888, 890, 789",
      /*  279 */ "789, 789, 789, 790, 789, 789, 887, 888, 889, 789, 789, 1243, 888, 888, 891, 789, 765, 765, 888, 789",
      /*  299 */ "789, 1242, 1036, 889, 789, 1036, 891, 1031, 1034, 1034, 894, 765, 1133, 765, 885, 821, 1214, 1217",
      /*  317 */ "829, 831, 835, 838, 838, 842, 846, 765, 1135, 765, 1199, 854, 858, 899, 904, 765, 765, 768, 1247",
      /*  336 */ "765, 1133, 765, 1198, 825, 910, 765, 765, 769, 1078, 971, 808, 905, 765, 767, 1065, 1069, 972, 809",
      /*  355 */ "765, 765, 799, 1229, 970, 930, 811, 765, 767, 1077, 765, 768, 1078, 765, 861, 1016, 1021, 936, 765",
      /*  374 */ "1011, 1207, 917, 929, 810, 765, 765, 765, 882, 765, 765, 947, 765, 1263, 923, 765, 1206, 924, 942",
      /*  393 */ "947, 765, 955, 941, 932, 765, 765, 765, 881, 962, 765, 765, 1081, 765, 1200, 1263, 968, 960, 905",
      /*  412 */ "765, 869, 765, 765, 850, 917, 943, 765, 765, 966, 979, 765, 941, 981, 765, 765, 824, 937, 1262, 956",
      /*  432 */ "980, 765, 869, 765, 1224, 765, 1142, 879, 765, 874, 878, 765, 874, 978, 905, 1143, 880, 765, 876",
      /*  451 */ "880, 765, 986, 880, 874, 878, 765, 883, 765, 765, 895, 884, 765, 895, 1078, 765, 900, 905, 765, 766",
      /*  471 */ "1119, 1123, 990, 1130, 1130, 1130, 991, 765, 765, 765, 1081, 911, 996, 996, 996, 1000, 1000, 1000",
      /*  489 */ "765, 867, 765, 985, 765, 875, 879, 765, 877, 860, 1015, 1020, 765, 990, 875, 765, 921, 917, 928, 817",
      /*  509 */ "1025, 1040, 1008, 1046, 765, 765, 884, 765, 765, 882, 1044, 765, 765, 765, 1198, 1071, 1089, 1102",
      /*  527 */ "765, 1011, 849, 916, 1090, 1103, 765, 765, 1224, 765, 1223, 906, 806, 1097, 1101, 905, 765, 1011",
      /*  545 */ "1082, 915, 941, 931, 905, 1075, 1067, 1086, 1099, 1257, 872, 765, 765, 765, 1233, 1100, 1104, 765",
      /*  563 */ "765, 885, 1236, 1098, 1102, 765, 1222, 951, 765, 765, 1171, 1094, 1108, 872, 765, 765, 976, 980, 765",
      /*  582 */ "1118, 1150, 1259, 1119, 1099, 1260, 1222, 1108, 873, 765, 766, 1065, 1096, 1119, 1099, 1260, 765",
      /*  599 */ "1050, 1055, 1060, 1170, 1120, 1127, 950, 764, 765, 1118, 1122, 1261, 765, 1112, 1140, 766, 1121",
      /*  616 */ "1111, 765, 1147, 1123, 1113, 766, 780, 764, 778, 777, 1154, 1224, 777, 1154, 764, 779, 1154, 765",
      /*  634 */ "779, 1160, 1159, 765, 779, 763, 778, 1155, 1164, 1169, 781, 1164, 1169, 781, 1165, 1165, 1175, 1177",
      /*  652 */ "1178, 765, 765, 1228, 765, 817, 873, 871, 765, 1051, 1056, 1061, 1182, 1184, 1187, 1186, 1191, 1192",
      /*  670 */ "1192, 1193, 1197, 765, 1204, 765, 765, 1254, 1211, 1221, 765, 765, 1262, 865, 817, 765, 765, 765",
      /*  688 */ "1267, 1075, 1079, 816, 765, 765, 906, 1254, 765, 765, 765, 1271, 766, 1076, 1080, 765, 1117, 1149",
      /*  706 */ "1101, 131330, 147712, 164096, 196864, 393472, 655616, 2228480, 537002240, 1073873152, 131328, 131328",
      /*  718 */ "131328, 268567040, 213248, 426240, 2490624, 393472, 131328, 393552, 2490624, 393472, 721218, 3080514",
      /*  730 */ "-2004997888, -2004997888, -2004997888, -2004932352, -2004997888, -2004997888, -1904330496",
      /*  737 */ "-1904264960, -1367459584, -1904330496, -1904248576, -1367394048, -1904264960, -1904215808",
      /*  744 */ "-1367377664, 131488, 132512, 197024, 655776, 229792, 459168, 721312, 917920, 983456, 25396670",
      /*  755 */ "126059966, 131302846, 131564990, 131302846, -1980252738, 256, 131072, 2, 536870912, 1073741824, 0, 0",
      /*  767 */ "0, 0, 1, 2, 4, 16, 131584, 268435968, 16, 16, 0, 0, 1, 262144, 33554432, 67108864, 536870912, 0, 0",
      /*  786 */ "67108864, 128, 160, 1152, 1152, 1152, 1152, 8, 1152, 1152, 1152, 16777228, 14, 0, 0, 0, 3, 0, 512",
      /*  805 */ "512, 512, 65536, 134217728, 268435456, 536870912, 1073741824, -2147483648, 0, 0, 12, 8, 0, 0, 0, 8",
      /*  821 */ "32768, 0, 32768, 33554432, 0, 8192, 8388608, 67108864, 1417946359, 1418077311, 1418077823",
      /*  832 */ "1418077823, 1418077823, 1418077823, 1418077951, 1418077823, 1418077823, -34816, -34816, -34816",
      /*  841 */ "-34816, -33920, -33920, -33920, -33920, -32896, -33920, -32769, 0, 0, 14336, 16384, 65536, 0, 3, 96",
      /*  857 */ "8192, 8388608, 1409286144, 0, 0, 7, 24, 3936, 8, 393216, 0, 0, 128, 2097152, 0, 0, 256, 0, 0, 0",
      /*  877 */ "4096, 4194304, 16777216, 134217728, 0, 0, 0, 64, 0, 0, 0, 128, 128, 128, 128, 1152, 1152, 1152, 0, 0",
      /*  897 */ "0, 2, 0, 96256, 1966080, 31457280, 2113929216, 2113929216, -2147483648, 0, 0, 0, 32, 268435456",
      /*  911 */ "1073741824, 0, 0, 407459384, 16384, 65536, 393216, 524288, 1048576, 6291456, 6144, 8192, 16384",
      /*  924 */ "65536, 262144, 524288, 1048576, 8388608, 16777216, 33554432, 67108864, 134217728, 536870912",
      /*  934 */ "1073741824, -2147483648, 0, 67108864, 1073741824, 0, 0, 1048576, 6291456, 16777216, 33554432",
      /*  945 */ "134217728, -2147483648, 0, 33554432, 0, 1073741824, 512, 268435456, 256, 0, 262144, 6144, 65536",
      /*  958 */ "262144, 6291456, 16777216, 33554432, 134217728, 1073741824, -2147483648, 0, 0, 6144, 65536, 262144",
      /*  970 */ "1048576, 6291456, 8388608, 16777216, 100663296, 134217728, 0, 4096, 65536, 6291456, 16777216",
      /*  981 */ "134217728, -2147483648, 0, 0, 0, 4096, 4194304, 134217728, 0, 4096, 4194304, 0, 0, 0, 512, 407459640",
      /*  997 */ "407459640, 407459640, 407459640, -2097281, -2097281, -2097281, -2097281, 1568, 20480, 65536, 524288",
      /* 1008 */ "4194304, 134217728, 268435456, 0, 0, 262144, 393216, 3936, 28672, 98304, 1966080, 4194304, 4194304",
      /* 1021 */ "125829120, 134217728, -268435456, 0, 16, 32, 512, 1024, 1152, 1184, 1152, 128, 128, 1152, 128, 1152",
      /* 1037 */ "128, 128, 128, 4096, 16384, 65536, 524288, 0, 768, 0, 0, 1824, 0, 0, 3, 4, 8, 16, 16, 32, 64, 768",
      /* 1059 */ "1024, 1024, 2048, 12288, 16384, 32768, 4, 32, 256, 512, 2048, 8192, 32768, 65536, 393216, 524288, 1",
      /* 1076 */ "2, 4, 32, 64, 128, 0, 0, 0, 6144, 8192, 32768, 65536, 393216, 1048576, 4194304, 8388608, 117440512",
      /* 1093 */ "134217728, 256, 512, 2048, 393216, 1048576, 8388608, 50331648, 67108864, 134217728, 268435456",
      /* 1104 */ "1610612736, -2147483648, 0, 0, 0, 512, 134217728, 268435456, 536870912, 1073741824, 0, 1073741824, 0",
      /* 1117 */ "0, 1, 4, 256, 512, 393216, 50331648, 67108864, 268435456, 536870912, 67108864, 268435456, 1610612736",
      /* 1130 */ "0, 0, 4194304, 0, 0, 32768, 0, 0, 0, 24, 512, 268435456, 0, 0, 4096, 6291456, 16777216, 0, 1, 512",
      /* 1150 */ "393216, 8388608, 50331648, 67108864, 33554432, 67108864, 536870912, 1073741824, 0, 67108864",
      /* 1160 */ "536870912, 1073741824, 1073741824, 0, 0, 1, 33554432, 67108864, 0, 536870912, 0, 0, 1, 4, 32",
      /* 1175 */ "67108864, 0, 67108864, 67108864, 67108864, 67108864, 0, 3728, 3728, 3760, 3984, 3760, 3760, 3760",
      /* 1189 */ "3760, 4016, 3760, 247, 247, 247, 247, 2295, 3831, 0, 0, 0, 33554432, 0, 0, 0, 8, 0, 0, 6144, 16384",
      /* 1210 */ "65536, 16, 128, 1024, 0, 0, 1417684087, 1417684087, 1417684215, 1417946231, 1417684215, 48, 0, 0, 0",
      /* 1225 */ "1073741824, 0, 0, 3, 116, 128, 0, 0, 16, 128, 0, 32, 128, 128, 128, 1024, 1056, 1152, 1152, 0, 128",
      /* 1246 */ "128, 16, 32, 64, 128, 1024, 1152, 1056, 0, 128, 0, 32, 512, 134217728, 268435456, 1610612736, 0, 0",
      /* 1264 */ "0, 262144, 6144, 2, 32, 64, 128, 0, 2, 64, 0, 0, 8192"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 1277; ++i) {EXPECTED[i] = Integer.parseInt(s2[i]);}
  }

  private static final String[] TOKEN =
  {
    "(0)",
    "IntegerLiteral",
    "DecimalLiteral",
    "DoubleLiteral",
    "StringLiteral",
    "URIQualifiedName",
    "NCName",
    "QName",
    "S",
    "CommentContents",
    "Wildcard",
    "EOF",
    "'!'",
    "'!='",
    "'#'",
    "'$'",
    "'('",
    "'(:'",
    "')'",
    "'*'",
    "'+'",
    "','",
    "'-'",
    "'.'",
    "'..'",
    "'/'",
    "'//'",
    "':'",
    "':)'",
    "'::'",
    "':='",
    "'<'",
    "'<<'",
    "'<='",
    "'='",
    "'=>'",
    "'>'",
    "'>='",
    "'>>'",
    "'?'",
    "'@'",
    "'['",
    "']'",
    "'ancestor'",
    "'ancestor-or-self'",
    "'and'",
    "'array'",
    "'as'",
    "'attribute'",
    "'cast'",
    "'castable'",
    "'child'",
    "'comment'",
    "'descendant'",
    "'descendant-or-self'",
    "'div'",
    "'document-node'",
    "'element'",
    "'else'",
    "'empty-sequence'",
    "'eq'",
    "'every'",
    "'except'",
    "'following'",
    "'following-sibling'",
    "'for'",
    "'function'",
    "'ge'",
    "'gt'",
    "'idiv'",
    "'if'",
    "'in'",
    "'instance'",
    "'intersect'",
    "'is'",
    "'item'",
    "'le'",
    "'let'",
    "'lt'",
    "'map'",
    "'mod'",
    "'namespace'",
    "'namespace-node'",
    "'ne'",
    "'node'",
    "'of'",
    "'or'",
    "'parent'",
    "'preceding'",
    "'preceding-sibling'",
    "'processing-instruction'",
    "'return'",
    "'satisfies'",
    "'schema-attribute'",
    "'schema-element'",
    "'self'",
    "'some'",
    "'switch'",
    "'text'",
    "'then'",
    "'to'",
    "'treat'",
    "'typeswitch'",
    "'union'",
    "'{'",
    "'|'",
    "'||'",
    "'}'"
  };
}

// End
