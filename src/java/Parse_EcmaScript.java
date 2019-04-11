// This file was generated on Thu Apr 11, 2019 23:36 (UTC+02) by REx v5.49 which is Copyright (c) 1979-2019 by Gunther Rademacher <grd@gmx.net>
// REx command line: file.ebnf -ll 1 -backtrack -asi -tree -java -basex -name expkg-zone58.text.parse.Parse-EcmaScript

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

public class Parse_EcmaScript
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

  public static ANode parseProgram(Str str) throws IOException
  {
    BaseXFunction baseXFunction = new BaseXFunction()
    {
      @Override
      public void execute(Parse_EcmaScript p) {p.parse_Program();}
    };
    return baseXFunction.call(str);
  }

  public static abstract class BaseXFunction
  {
    protected abstract void execute(Parse_EcmaScript p);

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
      Parse_EcmaScript parser = new Parse_EcmaScript();
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

  public Parse_EcmaScript()
  {
  }

  public Parse_EcmaScript(CharSequence string, EventHandler t)
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
    end = e;
    ex = -1;
    memo.clear();
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

  public void parse_Program()
  {
    eventHandler.startNonterminal("Program", e0);
    lookahead1W(31);                // Shebang | EOF | Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
    if (l1 == 2)                    // Shebang
    {
      consume(2);                   // Shebang
    }
    for (;;)
    {
      lookahead1W(29);              // EOF | Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      if (l1 == 3)                  // EOF
      {
        break;
      }
      whitespace();
      parse_SourceElement();
    }
    consume(3);                     // EOF
    eventHandler.endNonterminal("Program", e0);
  }

  private void parse_SourceElement()
  {
    eventHandler.startNonterminal("SourceElement", e0);
    parse_Statement();
    eventHandler.endNonterminal("SourceElement", e0);
  }

  private void try_SourceElement()
  {
    try_Statement();
  }

  private void parse_Statement()
  {
    eventHandler.startNonterminal("Statement", e0);
    if (l1 == 4                     // Identifier
     || l1 == 68                    // 'function'
     || l1 == 85)                   // '{'
    {
      lk = memoized(0, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          try_FunctionDeclaration();
          lk = -1;
        }
        catch (ParseException p1A)
        {
          try
          {
            b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
            b1 = b1A; e1 = e1A; end = e1A; }
            try_Block();
            lk = -2;
          }
          catch (ParseException p2A)
          {
            try
            {
              b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
              b1 = b1A; e1 = e1A; end = e1A; }
              try_ExpressionStatement();
              lk = -5;
            }
            catch (ParseException p5A)
            {
              lk = -12;
            }
          }
        }
        b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
        b1 = b1A; e1 = e1A; end = e1A; }
        memoize(0, e0, lk);
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case -1:
      parse_FunctionDeclaration();
      break;
    case -2:
      parse_Block();
      break;
    case 81:                        // 'var'
      parse_VariableStatement();
      break;
    case 38:                        // ';'
      parse_EmptyStatement();
      break;
    case 70:                        // 'if'
      parse_IfStatement();
      break;
    case 64:                        // 'do'
    case 67:                        // 'for'
    case 83:                        // 'while'
      parse_IterationStatement();
      break;
    case 60:                        // 'continue'
      parse_ContinueStatement();
      break;
    case 57:                        // 'break'
      parse_BreakStatement();
      break;
    case 74:                        // 'return'
      parse_ReturnStatement();
      break;
    case 84:                        // 'with'
      parse_WithStatement();
      break;
    case -12:
      parse_LabelledStatement();
      break;
    case 76:                        // 'switch'
      parse_SwitchStatement();
      break;
    case 78:                        // 'throw'
      parse_ThrowStatement();
      break;
    case 79:                        // 'try'
      parse_TryStatement();
      break;
    case 61:                        // 'debugger'
      parse_DebuggerStatement();
      break;
    default:
      parse_ExpressionStatement();
    }
    eventHandler.endNonterminal("Statement", e0);
  }

  private void try_Statement()
  {
    if (l1 == 4                     // Identifier
     || l1 == 68                    // 'function'
     || l1 == 85)                   // '{'
    {
      lk = memoized(0, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          try_FunctionDeclaration();
          memoize(0, e0A, -1);
          lk = -17;
        }
        catch (ParseException p1A)
        {
          try
          {
            b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
            b1 = b1A; e1 = e1A; end = e1A; }
            try_Block();
            memoize(0, e0A, -2);
            lk = -17;
          }
          catch (ParseException p2A)
          {
            try
            {
              b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
              b1 = b1A; e1 = e1A; end = e1A; }
              try_ExpressionStatement();
              memoize(0, e0A, -5);
              lk = -17;
            }
            catch (ParseException p5A)
            {
              lk = -12;
              b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
              b1 = b1A; e1 = e1A; end = e1A; }
              memoize(0, e0A, -12);
            }
          }
        }
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case -1:
      try_FunctionDeclaration();
      break;
    case -2:
      try_Block();
      break;
    case 81:                        // 'var'
      try_VariableStatement();
      break;
    case 38:                        // ';'
      try_EmptyStatement();
      break;
    case 70:                        // 'if'
      try_IfStatement();
      break;
    case 64:                        // 'do'
    case 67:                        // 'for'
    case 83:                        // 'while'
      try_IterationStatement();
      break;
    case 60:                        // 'continue'
      try_ContinueStatement();
      break;
    case 57:                        // 'break'
      try_BreakStatement();
      break;
    case 74:                        // 'return'
      try_ReturnStatement();
      break;
    case 84:                        // 'with'
      try_WithStatement();
      break;
    case -12:
      try_LabelledStatement();
      break;
    case 76:                        // 'switch'
      try_SwitchStatement();
      break;
    case 78:                        // 'throw'
      try_ThrowStatement();
      break;
    case 79:                        // 'try'
      try_TryStatement();
      break;
    case 61:                        // 'debugger'
      try_DebuggerStatement();
      break;
    case -17:
      break;
    default:
      try_ExpressionStatement();
    }
  }

  private void parse_Block()
  {
    eventHandler.startNonterminal("Block", e0);
    consume(85);                    // '{'
    for (;;)
    {
      lookahead1W(30);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
      if (l1 == 89)                 // '}'
      {
        break;
      }
      whitespace();
      parse_Statement();
    }
    consume(89);                    // '}'
    eventHandler.endNonterminal("Block", e0);
  }

  private void try_Block()
  {
    consumeT(85);                   // '{'
    for (;;)
    {
      lookahead1W(30);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
      if (l1 == 89)                 // '}'
      {
        break;
      }
      try_Statement();
    }
    consumeT(89);                   // '}'
  }

  private void parse_VariableStatement()
  {
    eventHandler.startNonterminal("VariableStatement", e0);
    consume(81);                    // 'var'
    lookahead1W(0);                 // Identifier | WhiteSpace | Comment
    whitespace();
    parse_VariableDeclarationList();
    whitespace();
    parse_Semicolon();
    eventHandler.endNonterminal("VariableStatement", e0);
  }

  private void try_VariableStatement()
  {
    consumeT(81);                   // 'var'
    lookahead1W(0);                 // Identifier | WhiteSpace | Comment
    try_VariableDeclarationList();
    try_Semicolon();
  }

  private void parse_VariableDeclarationList()
  {
    eventHandler.startNonterminal("VariableDeclarationList", e0);
    parse_VariableDeclaration();
    for (;;)
    {
      if (l1 != 30)                 // ','
      {
        break;
      }
      consume(30);                  // ','
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      whitespace();
      parse_VariableDeclaration();
    }
    eventHandler.endNonterminal("VariableDeclarationList", e0);
  }

  private void try_VariableDeclarationList()
  {
    try_VariableDeclaration();
    for (;;)
    {
      if (l1 != 30)                 // ','
      {
        break;
      }
      consumeT(30);                 // ','
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      try_VariableDeclaration();
    }
  }

  private void parse_VariableDeclaration()
  {
    eventHandler.startNonterminal("VariableDeclaration", e0);
    consume(4);                     // Identifier
    lookahead1W(18);                // END | EOF | WhiteSpace | Comment | ',' | ';' | '=' | '}'
    if (l1 == 43)                   // '='
    {
      whitespace();
      parse_Initialiser();
    }
    eventHandler.endNonterminal("VariableDeclaration", e0);
  }

  private void try_VariableDeclaration()
  {
    consumeT(4);                    // Identifier
    lookahead1W(18);                // END | EOF | WhiteSpace | Comment | ',' | ';' | '=' | '}'
    if (l1 == 43)                   // '='
    {
      try_Initialiser();
    }
  }

  private void parse_Initialiser()
  {
    eventHandler.startNonterminal("Initialiser", e0);
    consume(43);                    // '='
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    whitespace();
    parse_AssignmentExpression();
    eventHandler.endNonterminal("Initialiser", e0);
  }

  private void try_Initialiser()
  {
    consumeT(43);                   // '='
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    try_AssignmentExpression();
  }

  private void parse_AssignmentExpression()
  {
    eventHandler.startNonterminal("AssignmentExpression", e0);
    if (l1 != 15                    // '!'
     && l1 != 27                    // '+'
     && l1 != 28                    // '++'
     && l1 != 31                    // '-'
     && l1 != 32                    // '--'
     && l1 != 63                    // 'delete'
     && l1 != 80                    // 'typeof'
     && l1 != 82                    // 'void'
     && l1 != 90)                   // '~'
    {
      lk = memoized(1, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          try_LeftHandSideExpression();
          try_AssignmentOperator();
          lookahead1W(22);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
          try_AssignmentExpression();
          lk = -1;
        }
        catch (ParseException p1A)
        {
          lk = -2;
        }
        b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
        b1 = b1A; e1 = e1A; end = e1A; }
        memoize(1, e0, lk);
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case -1:
      parse_LeftHandSideExpression();
      whitespace();
      parse_AssignmentOperator();
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_AssignmentExpression();
      break;
    default:
      parse_ConditionalExpression();
    }
    eventHandler.endNonterminal("AssignmentExpression", e0);
  }

  private void try_AssignmentExpression()
  {
    if (l1 != 15                    // '!'
     && l1 != 27                    // '+'
     && l1 != 28                    // '++'
     && l1 != 31                    // '-'
     && l1 != 32                    // '--'
     && l1 != 63                    // 'delete'
     && l1 != 80                    // 'typeof'
     && l1 != 82                    // 'void'
     && l1 != 90)                   // '~'
    {
      lk = memoized(1, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          try_LeftHandSideExpression();
          try_AssignmentOperator();
          lookahead1W(22);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
          try_AssignmentExpression();
          memoize(1, e0A, -1);
          lk = -3;
        }
        catch (ParseException p1A)
        {
          lk = -2;
          b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
          b1 = b1A; e1 = e1A; end = e1A; }
          memoize(1, e0A, -2);
        }
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case -1:
      try_LeftHandSideExpression();
      try_AssignmentOperator();
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_AssignmentExpression();
      break;
    case -3:
      break;
    default:
      try_ConditionalExpression();
    }
  }

  private void parse_ConditionalExpression()
  {
    eventHandler.startNonterminal("ConditionalExpression", e0);
    parse_LogicalORExpression();
    if (l1 == 52)                   // '?'
    {
      consume(52);                  // '?'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_AssignmentExpression();
      consume(37);                  // ':'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_AssignmentExpression();
    }
    eventHandler.endNonterminal("ConditionalExpression", e0);
  }

  private void try_ConditionalExpression()
  {
    try_LogicalORExpression();
    if (l1 == 52)                   // '?'
    {
      consumeT(52);                 // '?'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_AssignmentExpression();
      consumeT(37);                 // ':'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_AssignmentExpression();
    }
  }

  private void parse_LogicalORExpression()
  {
    eventHandler.startNonterminal("LogicalORExpression", e0);
    parse_LogicalANDExpression();
    for (;;)
    {
      if (l1 != 88)                 // '||'
      {
        break;
      }
      consume(88);                  // '||'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_LogicalANDExpression();
    }
    eventHandler.endNonterminal("LogicalORExpression", e0);
  }

  private void try_LogicalORExpression()
  {
    try_LogicalANDExpression();
    for (;;)
    {
      if (l1 != 88)                 // '||'
      {
        break;
      }
      consumeT(88);                 // '||'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_LogicalANDExpression();
    }
  }

  private void parse_LogicalANDExpression()
  {
    eventHandler.startNonterminal("LogicalANDExpression", e0);
    parse_BitwiseORExpression();
    for (;;)
    {
      if (l1 != 21)                 // '&&'
      {
        break;
      }
      consume(21);                  // '&&'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_BitwiseORExpression();
    }
    eventHandler.endNonterminal("LogicalANDExpression", e0);
  }

  private void try_LogicalANDExpression()
  {
    try_BitwiseORExpression();
    for (;;)
    {
      if (l1 != 21)                 // '&&'
      {
        break;
      }
      consumeT(21);                 // '&&'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_BitwiseORExpression();
    }
  }

  private void parse_BitwiseORExpression()
  {
    eventHandler.startNonterminal("BitwiseORExpression", e0);
    parse_BitwiseXORExpression();
    for (;;)
    {
      if (l1 != 86)                 // '|'
      {
        break;
      }
      consume(86);                  // '|'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_BitwiseXORExpression();
    }
    eventHandler.endNonterminal("BitwiseORExpression", e0);
  }

  private void try_BitwiseORExpression()
  {
    try_BitwiseXORExpression();
    for (;;)
    {
      if (l1 != 86)                 // '|'
      {
        break;
      }
      consumeT(86);                 // '|'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_BitwiseXORExpression();
    }
  }

  private void parse_BitwiseXORExpression()
  {
    eventHandler.startNonterminal("BitwiseXORExpression", e0);
    parse_BitwiseANDExpression();
    for (;;)
    {
      if (l1 != 55)                 // '^'
      {
        break;
      }
      consume(55);                  // '^'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_BitwiseANDExpression();
    }
    eventHandler.endNonterminal("BitwiseXORExpression", e0);
  }

  private void try_BitwiseXORExpression()
  {
    try_BitwiseANDExpression();
    for (;;)
    {
      if (l1 != 55)                 // '^'
      {
        break;
      }
      consumeT(55);                 // '^'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_BitwiseANDExpression();
    }
  }

  private void parse_BitwiseANDExpression()
  {
    eventHandler.startNonterminal("BitwiseANDExpression", e0);
    parse_EqualityExpression();
    for (;;)
    {
      if (l1 != 20)                 // '&'
      {
        break;
      }
      consume(20);                  // '&'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_EqualityExpression();
    }
    eventHandler.endNonterminal("BitwiseANDExpression", e0);
  }

  private void try_BitwiseANDExpression()
  {
    try_EqualityExpression();
    for (;;)
    {
      if (l1 != 20)                 // '&'
      {
        break;
      }
      consumeT(20);                 // '&'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_EqualityExpression();
    }
  }

  private void parse_EqualityExpression()
  {
    eventHandler.startNonterminal("EqualityExpression", e0);
    parse_RelationalExpression();
    for (;;)
    {
      if (l1 != 16                  // '!='
       && l1 != 17                  // '!=='
       && l1 != 44                  // '=='
       && l1 != 45)                 // '==='
      {
        break;
      }
      switch (l1)
      {
      case 44:                      // '=='
        consume(44);                // '=='
        break;
      case 16:                      // '!='
        consume(16);                // '!='
        break;
      case 45:                      // '==='
        consume(45);                // '==='
        break;
      default:
        consume(17);                // '!=='
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_RelationalExpression();
    }
    eventHandler.endNonterminal("EqualityExpression", e0);
  }

  private void try_EqualityExpression()
  {
    try_RelationalExpression();
    for (;;)
    {
      if (l1 != 16                  // '!='
       && l1 != 17                  // '!=='
       && l1 != 44                  // '=='
       && l1 != 45)                 // '==='
      {
        break;
      }
      switch (l1)
      {
      case 44:                      // '=='
        consumeT(44);               // '=='
        break;
      case 16:                      // '!='
        consumeT(16);               // '!='
        break;
      case 45:                      // '==='
        consumeT(45);               // '==='
        break;
      default:
        consumeT(17);               // '!=='
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_RelationalExpression();
    }
  }

  private void parse_RelationalExpression()
  {
    eventHandler.startNonterminal("RelationalExpression", e0);
    parse_ShiftExpression();
    for (;;)
    {
      if (l1 != 39                  // '<'
       && l1 != 42                  // '<='
       && l1 != 46                  // '>'
       && l1 != 47                  // '>='
       && l1 != 71                  // 'in'
       && l1 != 72)                 // 'instanceof'
      {
        break;
      }
      switch (l1)
      {
      case 39:                      // '<'
        consume(39);                // '<'
        break;
      case 46:                      // '>'
        consume(46);                // '>'
        break;
      case 42:                      // '<='
        consume(42);                // '<='
        break;
      case 47:                      // '>='
        consume(47);                // '>='
        break;
      case 72:                      // 'instanceof'
        consume(72);                // 'instanceof'
        break;
      default:
        consume(71);                // 'in'
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_ShiftExpression();
    }
    eventHandler.endNonterminal("RelationalExpression", e0);
  }

  private void try_RelationalExpression()
  {
    try_ShiftExpression();
    for (;;)
    {
      if (l1 != 39                  // '<'
       && l1 != 42                  // '<='
       && l1 != 46                  // '>'
       && l1 != 47                  // '>='
       && l1 != 71                  // 'in'
       && l1 != 72)                 // 'instanceof'
      {
        break;
      }
      switch (l1)
      {
      case 39:                      // '<'
        consumeT(39);               // '<'
        break;
      case 46:                      // '>'
        consumeT(46);               // '>'
        break;
      case 42:                      // '<='
        consumeT(42);               // '<='
        break;
      case 47:                      // '>='
        consumeT(47);               // '>='
        break;
      case 72:                      // 'instanceof'
        consumeT(72);               // 'instanceof'
        break;
      default:
        consumeT(71);               // 'in'
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_ShiftExpression();
    }
  }

  private void parse_ShiftExpression()
  {
    eventHandler.startNonterminal("ShiftExpression", e0);
    parse_AdditiveExpression();
    for (;;)
    {
      if (l1 != 40                  // '<<'
       && l1 != 48                  // '>>'
       && l1 != 50)                 // '>>>'
      {
        break;
      }
      switch (l1)
      {
      case 40:                      // '<<'
        consume(40);                // '<<'
        break;
      case 48:                      // '>>'
        consume(48);                // '>>'
        break;
      default:
        consume(50);                // '>>>'
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_AdditiveExpression();
    }
    eventHandler.endNonterminal("ShiftExpression", e0);
  }

  private void try_ShiftExpression()
  {
    try_AdditiveExpression();
    for (;;)
    {
      if (l1 != 40                  // '<<'
       && l1 != 48                  // '>>'
       && l1 != 50)                 // '>>>'
      {
        break;
      }
      switch (l1)
      {
      case 40:                      // '<<'
        consumeT(40);               // '<<'
        break;
      case 48:                      // '>>'
        consumeT(48);               // '>>'
        break;
      default:
        consumeT(50);               // '>>>'
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_AdditiveExpression();
    }
  }

  private void parse_AdditiveExpression()
  {
    eventHandler.startNonterminal("AdditiveExpression", e0);
    parse_MultiplicativeExpression();
    for (;;)
    {
      if (l1 != 27                  // '+'
       && l1 != 31)                 // '-'
      {
        break;
      }
      switch (l1)
      {
      case 27:                      // '+'
        consume(27);                // '+'
        break;
      default:
        consume(31);                // '-'
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_MultiplicativeExpression();
    }
    eventHandler.endNonterminal("AdditiveExpression", e0);
  }

  private void try_AdditiveExpression()
  {
    try_MultiplicativeExpression();
    for (;;)
    {
      if (l1 != 27                  // '+'
       && l1 != 31)                 // '-'
      {
        break;
      }
      switch (l1)
      {
      case 27:                      // '+'
        consumeT(27);               // '+'
        break;
      default:
        consumeT(31);               // '-'
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_MultiplicativeExpression();
    }
  }

  private void parse_MultiplicativeExpression()
  {
    eventHandler.startNonterminal("MultiplicativeExpression", e0);
    parse_UnaryExpression();
    for (;;)
    {
      lookahead1W(27);              // END | EOF | WhiteSpace | Comment | '!=' | '!==' | '%' | '&' | '&&' | ')' | '*' |
                                    // '+' | ',' | '-' | '/' | ':' | ';' | '<' | '<<' | '<=' | '==' | '===' | '>' |
                                    // '>=' | '>>' | '>>>' | '?' | ']' | '^' | 'in' | 'instanceof' | '|' | '||' | '}'
      if (l1 != 18                  // '%'
       && l1 != 25                  // '*'
       && l1 != 35)                 // '/'
      {
        break;
      }
      switch (l1)
      {
      case 25:                      // '*'
        consume(25);                // '*'
        break;
      case 35:                      // '/'
        consume(35);                // '/'
        break;
      default:
        consume(18);                // '%'
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_UnaryExpression();
    }
    eventHandler.endNonterminal("MultiplicativeExpression", e0);
  }

  private void try_MultiplicativeExpression()
  {
    try_UnaryExpression();
    for (;;)
    {
      lookahead1W(27);              // END | EOF | WhiteSpace | Comment | '!=' | '!==' | '%' | '&' | '&&' | ')' | '*' |
                                    // '+' | ',' | '-' | '/' | ':' | ';' | '<' | '<<' | '<=' | '==' | '===' | '>' |
                                    // '>=' | '>>' | '>>>' | '?' | ']' | '^' | 'in' | 'instanceof' | '|' | '||' | '}'
      if (l1 != 18                  // '%'
       && l1 != 25                  // '*'
       && l1 != 35)                 // '/'
      {
        break;
      }
      switch (l1)
      {
      case 25:                      // '*'
        consumeT(25);               // '*'
        break;
      case 35:                      // '/'
        consumeT(35);               // '/'
        break;
      default:
        consumeT(18);               // '%'
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_UnaryExpression();
    }
  }

  private void parse_UnaryExpression()
  {
    eventHandler.startNonterminal("UnaryExpression", e0);
    switch (l1)
    {
    case 63:                        // 'delete'
      consume(63);                  // 'delete'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_UnaryExpression();
      break;
    case 82:                        // 'void'
      consume(82);                  // 'void'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_UnaryExpression();
      break;
    case 80:                        // 'typeof'
      consume(80);                  // 'typeof'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_UnaryExpression();
      break;
    case 28:                        // '++'
      consume(28);                  // '++'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_UnaryExpression();
      break;
    case 32:                        // '--'
      consume(32);                  // '--'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_UnaryExpression();
      break;
    case 27:                        // '+'
      consume(27);                  // '+'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_UnaryExpression();
      break;
    case 31:                        // '-'
      consume(31);                  // '-'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_UnaryExpression();
      break;
    case 90:                        // '~'
      consume(90);                  // '~'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_UnaryExpression();
      break;
    case 15:                        // '!'
      consume(15);                  // '!'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_UnaryExpression();
      break;
    default:
      parse_PostfixExpression();
    }
    eventHandler.endNonterminal("UnaryExpression", e0);
  }

  private void try_UnaryExpression()
  {
    switch (l1)
    {
    case 63:                        // 'delete'
      consumeT(63);                 // 'delete'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_UnaryExpression();
      break;
    case 82:                        // 'void'
      consumeT(82);                 // 'void'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_UnaryExpression();
      break;
    case 80:                        // 'typeof'
      consumeT(80);                 // 'typeof'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_UnaryExpression();
      break;
    case 28:                        // '++'
      consumeT(28);                 // '++'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_UnaryExpression();
      break;
    case 32:                        // '--'
      consumeT(32);                 // '--'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_UnaryExpression();
      break;
    case 27:                        // '+'
      consumeT(27);                 // '+'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_UnaryExpression();
      break;
    case 31:                        // '-'
      consumeT(31);                 // '-'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_UnaryExpression();
      break;
    case 90:                        // '~'
      consumeT(90);                 // '~'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_UnaryExpression();
      break;
    case 15:                        // '!'
      consumeT(15);                 // '!'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_UnaryExpression();
      break;
    default:
      try_PostfixExpression();
    }
  }

  private void parse_PostfixExpression()
  {
    eventHandler.startNonterminal("PostfixExpression", e0);
    parse_LeftHandSideExpression();
    if ((l1 == 28 || l1 == 32) && followsLineTerminator()) // '++' | '--'
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    if (l1 == 28                    // '++'
     || l1 == 32)                   // '--'
    {
      switch (l1)
      {
      case 28:                      // '++'
        consume(28);                // '++'
        break;
      default:
        consume(32);                // '--'
      }
    }
    eventHandler.endNonterminal("PostfixExpression", e0);
  }

  private void try_PostfixExpression()
  {
    try_LeftHandSideExpression();
    if ((l1 == 28 || l1 == 32) && followsLineTerminator()) // '++' | '--'
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    if (l1 == 28                    // '++'
     || l1 == 32)                   // '--'
    {
      switch (l1)
      {
      case 28:                      // '++'
        consumeT(28);               // '++'
        break;
      default:
        consumeT(32);               // '--'
      }
    }
  }

  private void parse_LeftHandSideExpression()
  {
    eventHandler.startNonterminal("LeftHandSideExpression", e0);
    lk = memoized(2, e0);
    if (lk == 0)
    {
      int b0A = b0; int e0A = e0; int l1A = l1;
      int b1A = b1; int e1A = e1;
      try
      {
        try_CallExpression();
        lk = -1;
      }
      catch (ParseException p1A)
      {
        lk = -2;
      }
      b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
      b1 = b1A; e1 = e1A; end = e1A; }
      memoize(2, e0, lk);
    }
    switch (lk)
    {
    case -1:
      parse_CallExpression();
      break;
    default:
      parse_NewExpression();
    }
    eventHandler.endNonterminal("LeftHandSideExpression", e0);
  }

  private void try_LeftHandSideExpression()
  {
    lk = memoized(2, e0);
    if (lk == 0)
    {
      int b0A = b0; int e0A = e0; int l1A = l1;
      int b1A = b1; int e1A = e1;
      try
      {
        try_CallExpression();
        memoize(2, e0A, -1);
        lk = -3;
      }
      catch (ParseException p1A)
      {
        lk = -2;
        b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
        b1 = b1A; e1 = e1A; end = e1A; }
        memoize(2, e0A, -2);
      }
    }
    switch (lk)
    {
    case -1:
      try_CallExpression();
      break;
    case -3:
      break;
    default:
      try_NewExpression();
    }
  }

  private void parse_NewExpression()
  {
    eventHandler.startNonterminal("NewExpression", e0);
    if (l1 == 73)                   // 'new'
    {
      lk = memoized(3, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          try_MemberExpression();
          lk = -1;
        }
        catch (ParseException p1A)
        {
          lk = -2;
        }
        b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
        b1 = b1A; e1 = e1A; end = e1A; }
        memoize(3, e0, lk);
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case -2:
      consume(73);                  // 'new'
      lookahead1W(21);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '(' | '[' | 'function' | 'new' |
                                    // 'this' | '{'
      whitespace();
      parse_NewExpression();
      break;
    default:
      parse_MemberExpression();
    }
    eventHandler.endNonterminal("NewExpression", e0);
  }

  private void try_NewExpression()
  {
    if (l1 == 73)                   // 'new'
    {
      lk = memoized(3, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          try_MemberExpression();
          memoize(3, e0A, -1);
          lk = -3;
        }
        catch (ParseException p1A)
        {
          lk = -2;
          b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
          b1 = b1A; e1 = e1A; end = e1A; }
          memoize(3, e0A, -2);
        }
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case -2:
      consumeT(73);                 // 'new'
      lookahead1W(21);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '(' | '[' | 'function' | 'new' |
                                    // 'this' | '{'
      try_NewExpression();
      break;
    case -3:
      break;
    default:
      try_MemberExpression();
    }
  }

  private void parse_MemberExpression()
  {
    eventHandler.startNonterminal("MemberExpression", e0);
    switch (l1)
    {
    case 68:                        // 'function'
      parse_FunctionExpression();
      break;
    case 73:                        // 'new'
      consume(73);                  // 'new'
      lookahead1W(21);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '(' | '[' | 'function' | 'new' |
                                    // 'this' | '{'
      whitespace();
      parse_MemberExpression();
      whitespace();
      parse_Arguments();
      break;
    default:
      parse_PrimaryExpression();
    }
    for (;;)
    {
      lookahead1W(36);              // END | EOF | WhiteSpace | Comment | '!=' | '!==' | '%' | '%=' | '&' | '&&' |
                                    // '&=' | '(' | ')' | '*' | '*=' | '+' | '++' | '+=' | ',' | '-' | '--' | '-=' |
                                    // '.' | '/' | '/=' | ':' | ';' | '<' | '<<' | '<<=' | '<=' | '=' | '==' | '===' |
                                    // '>' | '>=' | '>>' | '>>=' | '>>>' | '>>>=' | '?' | '[' | ']' | '^' | '^=' |
                                    // 'in' | 'instanceof' | '|' | '|=' | '||' | '}'
      if (l1 != 34                  // '.'
       && l1 != 53)                 // '['
      {
        break;
      }
      switch (l1)
      {
      case 53:                      // '['
        consume(53);                // '['
        lookahead1W(22);            // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
        whitespace();
        parse_Expression();
        consume(54);                // ']'
        break;
      default:
        consume(34);                // '.'
        lookahead1W(13);            // IdentifierName^Token | WhiteSpace | Comment | 'get' | 'set'
        whitespace();
        parse_IdentifierName();
      }
    }
    eventHandler.endNonterminal("MemberExpression", e0);
  }

  private void try_MemberExpression()
  {
    switch (l1)
    {
    case 68:                        // 'function'
      try_FunctionExpression();
      break;
    case 73:                        // 'new'
      consumeT(73);                 // 'new'
      lookahead1W(21);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '(' | '[' | 'function' | 'new' |
                                    // 'this' | '{'
      try_MemberExpression();
      try_Arguments();
      break;
    default:
      try_PrimaryExpression();
    }
    for (;;)
    {
      lookahead1W(36);              // END | EOF | WhiteSpace | Comment | '!=' | '!==' | '%' | '%=' | '&' | '&&' |
                                    // '&=' | '(' | ')' | '*' | '*=' | '+' | '++' | '+=' | ',' | '-' | '--' | '-=' |
                                    // '.' | '/' | '/=' | ':' | ';' | '<' | '<<' | '<<=' | '<=' | '=' | '==' | '===' |
                                    // '>' | '>=' | '>>' | '>>=' | '>>>' | '>>>=' | '?' | '[' | ']' | '^' | '^=' |
                                    // 'in' | 'instanceof' | '|' | '|=' | '||' | '}'
      if (l1 != 34                  // '.'
       && l1 != 53)                 // '['
      {
        break;
      }
      switch (l1)
      {
      case 53:                      // '['
        consumeT(53);               // '['
        lookahead1W(22);            // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
        try_Expression();
        consumeT(54);               // ']'
        break;
      default:
        consumeT(34);               // '.'
        lookahead1W(13);            // IdentifierName^Token | WhiteSpace | Comment | 'get' | 'set'
        try_IdentifierName();
      }
    }
  }

  private void parse_PrimaryExpression()
  {
    eventHandler.startNonterminal("PrimaryExpression", e0);
    switch (l1)
    {
    case 77:                        // 'this'
      consume(77);                  // 'this'
      break;
    case 4:                         // Identifier
      consume(4);                   // Identifier
      break;
    case 53:                        // '['
      parse_ArrayLiteral();
      break;
    case 85:                        // '{'
      parse_ObjectLiteral();
      break;
    case 23:                        // '('
      consume(23);                  // '('
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_Expression();
      consume(24);                  // ')'
      break;
    default:
      parse_Literal();
    }
    eventHandler.endNonterminal("PrimaryExpression", e0);
  }

  private void try_PrimaryExpression()
  {
    switch (l1)
    {
    case 77:                        // 'this'
      consumeT(77);                 // 'this'
      break;
    case 4:                         // Identifier
      consumeT(4);                  // Identifier
      break;
    case 53:                        // '['
      try_ArrayLiteral();
      break;
    case 85:                        // '{'
      try_ObjectLiteral();
      break;
    case 23:                        // '('
      consumeT(23);                 // '('
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_Expression();
      consumeT(24);                 // ')'
      break;
    default:
      try_Literal();
    }
  }

  private void parse_Literal()
  {
    eventHandler.startNonterminal("Literal", e0);
    switch (l1)
    {
    case 5:                         // NullLiteral
      consume(5);                   // NullLiteral
      break;
    case 6:                         // BooleanLiteral
      consume(6);                   // BooleanLiteral
      break;
    case 8:                         // StringLiteral
      consume(8);                   // StringLiteral
      break;
    case 9:                         // RegularExpressionLiteral
      consume(9);                   // RegularExpressionLiteral
      break;
    default:
      parse_NumericLiteral();
    }
    eventHandler.endNonterminal("Literal", e0);
  }

  private void try_Literal()
  {
    switch (l1)
    {
    case 5:                         // NullLiteral
      consumeT(5);                  // NullLiteral
      break;
    case 6:                         // BooleanLiteral
      consumeT(6);                  // BooleanLiteral
      break;
    case 8:                         // StringLiteral
      consumeT(8);                  // StringLiteral
      break;
    case 9:                         // RegularExpressionLiteral
      consumeT(9);                  // RegularExpressionLiteral
      break;
    default:
      try_NumericLiteral();
    }
  }

  private void parse_NumericLiteral()
  {
    eventHandler.startNonterminal("NumericLiteral", e0);
    switch (l1)
    {
    case 10:                        // DecimalLiteral
      consume(10);                  // DecimalLiteral
      break;
    case 11:                        // HexIntegerLiteral
      consume(11);                  // HexIntegerLiteral
      break;
    default:
      consume(12);                  // OctalIntegerLiteral
    }
    eventHandler.endNonterminal("NumericLiteral", e0);
  }

  private void try_NumericLiteral()
  {
    switch (l1)
    {
    case 10:                        // DecimalLiteral
      consumeT(10);                 // DecimalLiteral
      break;
    case 11:                        // HexIntegerLiteral
      consumeT(11);                 // HexIntegerLiteral
      break;
    default:
      consumeT(12);                 // OctalIntegerLiteral
    }
  }

  private void parse_ArrayLiteral()
  {
    eventHandler.startNonterminal("ArrayLiteral", e0);
    consume(53);                    // '['
    lookahead1W(25);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | ',' | '-' |
                                    // '--' | '[' | ']' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
    if (l1 != 30                    // ','
     && l1 != 54)                   // ']'
    {
      whitespace();
      parse_AssignmentExpression();
    }
    for (;;)
    {
      if (l1 != 30)                 // ','
      {
        break;
      }
      consume(30);                  // ','
      lookahead1W(25);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | ',' | '-' |
                                    // '--' | '[' | ']' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
      if (l1 != 30                  // ','
       && l1 != 54)                 // ']'
      {
        whitespace();
        parse_AssignmentExpression();
      }
    }
    consume(54);                    // ']'
    eventHandler.endNonterminal("ArrayLiteral", e0);
  }

  private void try_ArrayLiteral()
  {
    consumeT(53);                   // '['
    lookahead1W(25);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | ',' | '-' |
                                    // '--' | '[' | ']' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
    if (l1 != 30                    // ','
     && l1 != 54)                   // ']'
    {
      try_AssignmentExpression();
    }
    for (;;)
    {
      if (l1 != 30)                 // ','
      {
        break;
      }
      consumeT(30);                 // ','
      lookahead1W(25);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | ',' | '-' |
                                    // '--' | '[' | ']' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
      if (l1 != 30                  // ','
       && l1 != 54)                 // ']'
      {
        try_AssignmentExpression();
      }
    }
    consumeT(54);                   // ']'
  }

  private void parse_ObjectLiteral()
  {
    eventHandler.startNonterminal("ObjectLiteral", e0);
    consume(85);                    // '{'
    lookahead1W(20);                // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set' | '}'
    if (l1 != 89)                   // '}'
    {
      whitespace();
      parse_PropertyAssignment();
      for (;;)
      {
        lookahead1W(11);            // WhiteSpace | Comment | ',' | '}'
        if (l1 == 30)               // ','
        {
          lk = memoized(4, e0);
          if (lk == 0)
          {
            int b0A = b0; int e0A = e0; int l1A = l1;
            int b1A = b1; int e1A = e1;
            try
            {
              consumeT(30);         // ','
              lookahead1W(19);      // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set'
              try_PropertyAssignment();
              lk = -1;
            }
            catch (ParseException p1A)
            {
              lk = -2;
            }
            b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
            b1 = b1A; e1 = e1A; end = e1A; }
            memoize(4, e0, lk);
          }
        }
        else
        {
          lk = l1;
        }
        if (lk != -1)
        {
          break;
        }
        consume(30);                // ','
        lookahead1W(19);            // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set'
        whitespace();
        parse_PropertyAssignment();
      }
      if (l1 == 30)                 // ','
      {
        consume(30);                // ','
      }
    }
    lookahead1W(7);                 // WhiteSpace | Comment | '}'
    consume(89);                    // '}'
    eventHandler.endNonterminal("ObjectLiteral", e0);
  }

  private void try_ObjectLiteral()
  {
    consumeT(85);                   // '{'
    lookahead1W(20);                // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set' | '}'
    if (l1 != 89)                   // '}'
    {
      try_PropertyAssignment();
      for (;;)
      {
        lookahead1W(11);            // WhiteSpace | Comment | ',' | '}'
        if (l1 == 30)               // ','
        {
          lk = memoized(4, e0);
          if (lk == 0)
          {
            int b0A = b0; int e0A = e0; int l1A = l1;
            int b1A = b1; int e1A = e1;
            try
            {
              consumeT(30);         // ','
              lookahead1W(19);      // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set'
              try_PropertyAssignment();
              memoize(4, e0A, -1);
              continue;
            }
            catch (ParseException p1A)
            {
              b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
              b1 = b1A; e1 = e1A; end = e1A; }
              memoize(4, e0A, -2);
              break;
            }
          }
        }
        else
        {
          lk = l1;
        }
        if (lk != -1)
        {
          break;
        }
        consumeT(30);               // ','
        lookahead1W(19);            // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set'
        try_PropertyAssignment();
      }
      if (l1 == 30)                 // ','
      {
        consumeT(30);               // ','
      }
    }
    lookahead1W(7);                 // WhiteSpace | Comment | '}'
    consumeT(89);                   // '}'
  }

  private void parse_PropertyAssignment()
  {
    eventHandler.startNonterminal("PropertyAssignment", e0);
    if (l1 == 69                    // 'get'
     || l1 == 75)                   // 'set'
    {
      lk = memoized(5, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          try_PropertyName();
          lookahead1W(3);           // WhiteSpace | Comment | ':'
          consumeT(37);             // ':'
          lookahead1W(22);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
          try_AssignmentExpression();
          lk = -1;
        }
        catch (ParseException p1A)
        {
          try
          {
            b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
            b1 = b1A; e1 = e1A; end = e1A; }
            consumeT(69);           // 'get'
            lookahead1W(19);        // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set'
            try_PropertyName();
            lookahead1W(1);         // WhiteSpace | Comment | '('
            consumeT(23);           // '('
            lookahead1W(2);         // WhiteSpace | Comment | ')'
            consumeT(24);           // ')'
            lookahead1W(6);         // WhiteSpace | Comment | '{'
            consumeT(85);           // '{'
            lookahead1W(30);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
            try_FunctionBody();
            consumeT(89);           // '}'
            lk = -2;
          }
          catch (ParseException p2A)
          {
            lk = -3;
          }
        }
        b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
        b1 = b1A; e1 = e1A; end = e1A; }
        memoize(5, e0, lk);
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case -2:
      consume(69);                  // 'get'
      lookahead1W(19);              // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set'
      whitespace();
      parse_PropertyName();
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consume(23);                  // '('
      lookahead1W(2);               // WhiteSpace | Comment | ')'
      consume(24);                  // ')'
      lookahead1W(6);               // WhiteSpace | Comment | '{'
      consume(85);                  // '{'
      lookahead1W(30);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
      whitespace();
      parse_FunctionBody();
      consume(89);                  // '}'
      break;
    case -3:
      consume(75);                  // 'set'
      lookahead1W(19);              // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set'
      whitespace();
      parse_PropertyName();
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consume(23);                  // '('
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      whitespace();
      parse_PropertySetParameterList();
      lookahead1W(2);               // WhiteSpace | Comment | ')'
      consume(24);                  // ')'
      lookahead1W(6);               // WhiteSpace | Comment | '{'
      consume(85);                  // '{'
      lookahead1W(30);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
      whitespace();
      parse_FunctionBody();
      consume(89);                  // '}'
      break;
    default:
      parse_PropertyName();
      lookahead1W(3);               // WhiteSpace | Comment | ':'
      consume(37);                  // ':'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_AssignmentExpression();
    }
    eventHandler.endNonterminal("PropertyAssignment", e0);
  }

  private void try_PropertyAssignment()
  {
    if (l1 == 69                    // 'get'
     || l1 == 75)                   // 'set'
    {
      lk = memoized(5, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          try_PropertyName();
          lookahead1W(3);           // WhiteSpace | Comment | ':'
          consumeT(37);             // ':'
          lookahead1W(22);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
          try_AssignmentExpression();
          memoize(5, e0A, -1);
          lk = -4;
        }
        catch (ParseException p1A)
        {
          try
          {
            b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
            b1 = b1A; e1 = e1A; end = e1A; }
            consumeT(69);           // 'get'
            lookahead1W(19);        // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set'
            try_PropertyName();
            lookahead1W(1);         // WhiteSpace | Comment | '('
            consumeT(23);           // '('
            lookahead1W(2);         // WhiteSpace | Comment | ')'
            consumeT(24);           // ')'
            lookahead1W(6);         // WhiteSpace | Comment | '{'
            consumeT(85);           // '{'
            lookahead1W(30);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
            try_FunctionBody();
            consumeT(89);           // '}'
            memoize(5, e0A, -2);
            lk = -4;
          }
          catch (ParseException p2A)
          {
            lk = -3;
            b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
            b1 = b1A; e1 = e1A; end = e1A; }
            memoize(5, e0A, -3);
          }
        }
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case -2:
      consumeT(69);                 // 'get'
      lookahead1W(19);              // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set'
      try_PropertyName();
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consumeT(23);                 // '('
      lookahead1W(2);               // WhiteSpace | Comment | ')'
      consumeT(24);                 // ')'
      lookahead1W(6);               // WhiteSpace | Comment | '{'
      consumeT(85);                 // '{'
      lookahead1W(30);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
      try_FunctionBody();
      consumeT(89);                 // '}'
      break;
    case -3:
      consumeT(75);                 // 'set'
      lookahead1W(19);              // IdentifierName^Token | StringLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | 'get' | 'set'
      try_PropertyName();
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consumeT(23);                 // '('
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      try_PropertySetParameterList();
      lookahead1W(2);               // WhiteSpace | Comment | ')'
      consumeT(24);                 // ')'
      lookahead1W(6);               // WhiteSpace | Comment | '{'
      consumeT(85);                 // '{'
      lookahead1W(30);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
      try_FunctionBody();
      consumeT(89);                 // '}'
      break;
    case -4:
      break;
    default:
      try_PropertyName();
      lookahead1W(3);               // WhiteSpace | Comment | ':'
      consumeT(37);                 // ':'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_AssignmentExpression();
    }
  }

  private void parse_PropertyName()
  {
    eventHandler.startNonterminal("PropertyName", e0);
    switch (l1)
    {
    case 7:                         // IdentifierName^Token
    case 69:                        // 'get'
    case 75:                        // 'set'
      parse_IdentifierName();
      break;
    case 8:                         // StringLiteral
      consume(8);                   // StringLiteral
      break;
    default:
      parse_NumericLiteral();
    }
    eventHandler.endNonterminal("PropertyName", e0);
  }

  private void try_PropertyName()
  {
    switch (l1)
    {
    case 7:                         // IdentifierName^Token
    case 69:                        // 'get'
    case 75:                        // 'set'
      try_IdentifierName();
      break;
    case 8:                         // StringLiteral
      consumeT(8);                  // StringLiteral
      break;
    default:
      try_NumericLiteral();
    }
  }

  private void parse_FunctionBody()
  {
    eventHandler.startNonterminal("FunctionBody", e0);
    for (;;)
    {
      lookahead1W(30);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
      if (l1 == 89)                 // '}'
      {
        break;
      }
      whitespace();
      parse_SourceElement();
    }
    eventHandler.endNonterminal("FunctionBody", e0);
  }

  private void try_FunctionBody()
  {
    for (;;)
    {
      lookahead1W(30);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
      if (l1 == 89)                 // '}'
      {
        break;
      }
      try_SourceElement();
    }
  }

  private void parse_PropertySetParameterList()
  {
    eventHandler.startNonterminal("PropertySetParameterList", e0);
    consume(4);                     // Identifier
    eventHandler.endNonterminal("PropertySetParameterList", e0);
  }

  private void try_PropertySetParameterList()
  {
    consumeT(4);                    // Identifier
  }

  private void parse_Expression()
  {
    eventHandler.startNonterminal("Expression", e0);
    parse_AssignmentExpression();
    for (;;)
    {
      if (l1 != 30)                 // ','
      {
        break;
      }
      consume(30);                  // ','
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_AssignmentExpression();
    }
    eventHandler.endNonterminal("Expression", e0);
  }

  private void try_Expression()
  {
    try_AssignmentExpression();
    for (;;)
    {
      if (l1 != 30)                 // ','
      {
        break;
      }
      consumeT(30);                 // ','
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_AssignmentExpression();
    }
  }

  private void parse_FunctionExpression()
  {
    eventHandler.startNonterminal("FunctionExpression", e0);
    consume(68);                    // 'function'
    lookahead1W(8);                 // Identifier | WhiteSpace | Comment | '('
    if (l1 == 4)                    // Identifier
    {
      consume(4);                   // Identifier
    }
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consume(23);                    // '('
    lookahead1W(9);                 // Identifier | WhiteSpace | Comment | ')'
    if (l1 == 4)                    // Identifier
    {
      whitespace();
      parse_FormalParameterList();
    }
    consume(24);                    // ')'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    consume(85);                    // '{'
    lookahead1W(30);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
    whitespace();
    parse_FunctionBody();
    consume(89);                    // '}'
    eventHandler.endNonterminal("FunctionExpression", e0);
  }

  private void try_FunctionExpression()
  {
    consumeT(68);                   // 'function'
    lookahead1W(8);                 // Identifier | WhiteSpace | Comment | '('
    if (l1 == 4)                    // Identifier
    {
      consumeT(4);                  // Identifier
    }
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consumeT(23);                   // '('
    lookahead1W(9);                 // Identifier | WhiteSpace | Comment | ')'
    if (l1 == 4)                    // Identifier
    {
      try_FormalParameterList();
    }
    consumeT(24);                   // ')'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    consumeT(85);                   // '{'
    lookahead1W(30);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
    try_FunctionBody();
    consumeT(89);                   // '}'
  }

  private void parse_FormalParameterList()
  {
    eventHandler.startNonterminal("FormalParameterList", e0);
    consume(4);                     // Identifier
    for (;;)
    {
      lookahead1W(10);              // WhiteSpace | Comment | ')' | ','
      if (l1 != 30)                 // ','
      {
        break;
      }
      consume(30);                  // ','
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      consume(4);                   // Identifier
    }
    eventHandler.endNonterminal("FormalParameterList", e0);
  }

  private void try_FormalParameterList()
  {
    consumeT(4);                    // Identifier
    for (;;)
    {
      lookahead1W(10);              // WhiteSpace | Comment | ')' | ','
      if (l1 != 30)                 // ','
      {
        break;
      }
      consumeT(30);                 // ','
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      consumeT(4);                  // Identifier
    }
  }

  private void parse_Arguments()
  {
    eventHandler.startNonterminal("Arguments", e0);
    consume(23);                    // '('
    lookahead1W(23);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | ')' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    if (l1 != 24)                   // ')'
    {
      whitespace();
      parse_AssignmentExpression();
      for (;;)
      {
        if (l1 != 30)               // ','
        {
          break;
        }
        consume(30);                // ','
        lookahead1W(22);            // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
        whitespace();
        parse_AssignmentExpression();
      }
    }
    consume(24);                    // ')'
    eventHandler.endNonterminal("Arguments", e0);
  }

  private void try_Arguments()
  {
    consumeT(23);                   // '('
    lookahead1W(23);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | ')' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    if (l1 != 24)                   // ')'
    {
      try_AssignmentExpression();
      for (;;)
      {
        if (l1 != 30)               // ','
        {
          break;
        }
        consumeT(30);               // ','
        lookahead1W(22);            // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
        try_AssignmentExpression();
      }
    }
    consumeT(24);                   // ')'
  }

  private void parse_CallExpression()
  {
    eventHandler.startNonterminal("CallExpression", e0);
    parse_MemberExpression();
    whitespace();
    parse_Arguments();
    for (;;)
    {
      lookahead1W(36);              // END | EOF | WhiteSpace | Comment | '!=' | '!==' | '%' | '%=' | '&' | '&&' |
                                    // '&=' | '(' | ')' | '*' | '*=' | '+' | '++' | '+=' | ',' | '-' | '--' | '-=' |
                                    // '.' | '/' | '/=' | ':' | ';' | '<' | '<<' | '<<=' | '<=' | '=' | '==' | '===' |
                                    // '>' | '>=' | '>>' | '>>=' | '>>>' | '>>>=' | '?' | '[' | ']' | '^' | '^=' |
                                    // 'in' | 'instanceof' | '|' | '|=' | '||' | '}'
      if (l1 != 23                  // '('
       && l1 != 34                  // '.'
       && l1 != 53)                 // '['
      {
        break;
      }
      switch (l1)
      {
      case 23:                      // '('
        whitespace();
        parse_Arguments();
        break;
      case 53:                      // '['
        consume(53);                // '['
        lookahead1W(22);            // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
        whitespace();
        parse_Expression();
        consume(54);                // ']'
        break;
      default:
        consume(34);                // '.'
        lookahead1W(13);            // IdentifierName^Token | WhiteSpace | Comment | 'get' | 'set'
        whitespace();
        parse_IdentifierName();
      }
    }
    eventHandler.endNonterminal("CallExpression", e0);
  }

  private void try_CallExpression()
  {
    try_MemberExpression();
    try_Arguments();
    for (;;)
    {
      lookahead1W(36);              // END | EOF | WhiteSpace | Comment | '!=' | '!==' | '%' | '%=' | '&' | '&&' |
                                    // '&=' | '(' | ')' | '*' | '*=' | '+' | '++' | '+=' | ',' | '-' | '--' | '-=' |
                                    // '.' | '/' | '/=' | ':' | ';' | '<' | '<<' | '<<=' | '<=' | '=' | '==' | '===' |
                                    // '>' | '>=' | '>>' | '>>=' | '>>>' | '>>>=' | '?' | '[' | ']' | '^' | '^=' |
                                    // 'in' | 'instanceof' | '|' | '|=' | '||' | '}'
      if (l1 != 23                  // '('
       && l1 != 34                  // '.'
       && l1 != 53)                 // '['
      {
        break;
      }
      switch (l1)
      {
      case 23:                      // '('
        try_Arguments();
        break;
      case 53:                      // '['
        consumeT(53);               // '['
        lookahead1W(22);            // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
        try_Expression();
        consumeT(54);               // ']'
        break;
      default:
        consumeT(34);               // '.'
        lookahead1W(13);            // IdentifierName^Token | WhiteSpace | Comment | 'get' | 'set'
        try_IdentifierName();
      }
    }
  }

  private void parse_EmptyStatement()
  {
    eventHandler.startNonterminal("EmptyStatement", e0);
    consume(38);                    // ';'
    eventHandler.endNonterminal("EmptyStatement", e0);
  }

  private void try_EmptyStatement()
  {
    consumeT(38);                   // ';'
  }

  private void parse_AssignmentOperator()
  {
    eventHandler.startNonterminal("AssignmentOperator", e0);
    switch (l1)
    {
    case 43:                        // '='
      consume(43);                  // '='
      break;
    case 26:                        // '*='
      consume(26);                  // '*='
      break;
    case 36:                        // '/='
      consume(36);                  // '/='
      break;
    case 19:                        // '%='
      consume(19);                  // '%='
      break;
    case 29:                        // '+='
      consume(29);                  // '+='
      break;
    case 33:                        // '-='
      consume(33);                  // '-='
      break;
    case 41:                        // '<<='
      consume(41);                  // '<<='
      break;
    case 49:                        // '>>='
      consume(49);                  // '>>='
      break;
    case 51:                        // '>>>='
      consume(51);                  // '>>>='
      break;
    case 22:                        // '&='
      consume(22);                  // '&='
      break;
    case 56:                        // '^='
      consume(56);                  // '^='
      break;
    default:
      consume(87);                  // '|='
    }
    eventHandler.endNonterminal("AssignmentOperator", e0);
  }

  private void try_AssignmentOperator()
  {
    switch (l1)
    {
    case 43:                        // '='
      consumeT(43);                 // '='
      break;
    case 26:                        // '*='
      consumeT(26);                 // '*='
      break;
    case 36:                        // '/='
      consumeT(36);                 // '/='
      break;
    case 19:                        // '%='
      consumeT(19);                 // '%='
      break;
    case 29:                        // '+='
      consumeT(29);                 // '+='
      break;
    case 33:                        // '-='
      consumeT(33);                 // '-='
      break;
    case 41:                        // '<<='
      consumeT(41);                 // '<<='
      break;
    case 49:                        // '>>='
      consumeT(49);                 // '>>='
      break;
    case 51:                        // '>>>='
      consumeT(51);                 // '>>>='
      break;
    case 22:                        // '&='
      consumeT(22);                 // '&='
      break;
    case 56:                        // '^='
      consumeT(56);                 // '^='
      break;
    default:
      consumeT(87);                 // '|='
    }
  }

  private void parse_ExpressionStatement()
  {
    eventHandler.startNonterminal("ExpressionStatement", e0);
    parse_Expression();
    whitespace();
    parse_Semicolon();
    eventHandler.endNonterminal("ExpressionStatement", e0);
  }

  private void try_ExpressionStatement()
  {
    try_Expression();
    try_Semicolon();
  }

  private void parse_IfStatement()
  {
    eventHandler.startNonterminal("IfStatement", e0);
    consume(70);                    // 'if'
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consume(23);                    // '('
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    whitespace();
    parse_Expression();
    consume(24);                    // ')'
    lookahead1W(28);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
    whitespace();
    parse_Statement();
    lookahead1W(34);                // EOF | Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'case' | 'continue' | 'debugger' | 'default' |
                                    // 'delete' | 'do' | 'else' | 'for' | 'function' | 'if' | 'new' | 'return' |
                                    // 'switch' | 'this' | 'throw' | 'try' | 'typeof' | 'var' | 'void' | 'while' |
                                    // 'with' | '{' | '}' | '~'
    if (l1 == 65)                   // 'else'
    {
      lk = memoized(6, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          consumeT(65);             // 'else'
          lookahead1W(28);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
          try_Statement();
          lk = -1;
        }
        catch (ParseException p1A)
        {
          lk = -2;
        }
        b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
        b1 = b1A; e1 = e1A; end = e1A; }
        memoize(6, e0, lk);
      }
    }
    else
    {
      lk = l1;
    }
    if (lk == -1)
    {
      consume(65);                  // 'else'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      whitespace();
      parse_Statement();
    }
    eventHandler.endNonterminal("IfStatement", e0);
  }

  private void try_IfStatement()
  {
    consumeT(70);                   // 'if'
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consumeT(23);                   // '('
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    try_Expression();
    consumeT(24);                   // ')'
    lookahead1W(28);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
    try_Statement();
    lookahead1W(34);                // EOF | Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'case' | 'continue' | 'debugger' | 'default' |
                                    // 'delete' | 'do' | 'else' | 'for' | 'function' | 'if' | 'new' | 'return' |
                                    // 'switch' | 'this' | 'throw' | 'try' | 'typeof' | 'var' | 'void' | 'while' |
                                    // 'with' | '{' | '}' | '~'
    if (l1 == 65)                   // 'else'
    {
      lk = memoized(6, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          consumeT(65);             // 'else'
          lookahead1W(28);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
          try_Statement();
          memoize(6, e0A, -1);
        }
        catch (ParseException p1A)
        {
          b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
          b1 = b1A; e1 = e1A; end = e1A; }
          memoize(6, e0A, -2);
        }
        lk = -2;
      }
    }
    else
    {
      lk = l1;
    }
    if (lk == -1)
    {
      consumeT(65);                 // 'else'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      try_Statement();
    }
  }

  private void parse_IterationStatement()
  {
    eventHandler.startNonterminal("IterationStatement", e0);
    if (l1 == 67)                   // 'for'
    {
      lk = memoized(7, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          consumeT(67);             // 'for'
          lookahead1W(1);           // WhiteSpace | Comment | '('
          consumeT(23);             // '('
          lookahead1W(24);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
          if (l1 != 38)             // ';'
          {
            try_ExpressionNoIn();
          }
          consumeT(38);             // ';'
          lookahead1W(24);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
          if (l1 != 38)             // ';'
          {
            try_Expression();
          }
          consumeT(38);             // ';'
          lookahead1W(23);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | ')' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
          if (l1 != 24)             // ')'
          {
            try_Expression();
          }
          consumeT(24);             // ')'
          lookahead1W(28);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
          try_Statement();
          lk = -3;
        }
        catch (ParseException p3A)
        {
          try
          {
            b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
            b1 = b1A; e1 = e1A; end = e1A; }
            consumeT(67);           // 'for'
            lookahead1W(1);         // WhiteSpace | Comment | '('
            consumeT(23);           // '('
            lookahead1W(4);         // WhiteSpace | Comment | 'var'
            consumeT(81);           // 'var'
            lookahead1W(0);         // Identifier | WhiteSpace | Comment
            try_VariableDeclarationListNoIn();
            consumeT(38);           // ';'
            lookahead1W(24);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
            if (l1 != 38)           // ';'
            {
              try_Expression();
            }
            consumeT(38);           // ';'
            lookahead1W(23);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | ')' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
            if (l1 != 24)           // ')'
            {
              try_Expression();
            }
            consumeT(24);           // ')'
            lookahead1W(28);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
            try_Statement();
            lk = -4;
          }
          catch (ParseException p4A)
          {
            try
            {
              b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
              b1 = b1A; e1 = e1A; end = e1A; }
              consumeT(67);         // 'for'
              lookahead1W(1);       // WhiteSpace | Comment | '('
              consumeT(23);         // '('
              lookahead1W(21);      // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '(' | '[' | 'function' | 'new' |
                                    // 'this' | '{'
              try_LeftHandSideExpression();
              consumeT(71);         // 'in'
              lookahead1W(22);      // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
              try_Expression();
              consumeT(24);         // ')'
              lookahead1W(28);      // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
              try_Statement();
              lk = -5;
            }
            catch (ParseException p5A)
            {
              lk = -6;
            }
          }
        }
        b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
        b1 = b1A; e1 = e1A; end = e1A; }
        memoize(7, e0, lk);
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case 64:                        // 'do'
      consume(64);                  // 'do'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      whitespace();
      parse_Statement();
      lookahead1W(5);               // WhiteSpace | Comment | 'while'
      consume(83);                  // 'while'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consume(23);                  // '('
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_Expression();
      consume(24);                  // ')'
      lookahead1W(15);              // END | EOF | WhiteSpace | Comment | ';' | '}'
      whitespace();
      parse_Semicolon();
      break;
    case -3:
      consume(67);                  // 'for'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consume(23);                  // '('
      lookahead1W(24);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
      if (l1 != 38)                 // ';'
      {
        whitespace();
        parse_ExpressionNoIn();
      }
      consume(38);                  // ';'
      lookahead1W(24);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
      if (l1 != 38)                 // ';'
      {
        whitespace();
        parse_Expression();
      }
      consume(38);                  // ';'
      lookahead1W(23);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | ')' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      if (l1 != 24)                 // ')'
      {
        whitespace();
        parse_Expression();
      }
      consume(24);                  // ')'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      whitespace();
      parse_Statement();
      break;
    case -4:
      consume(67);                  // 'for'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consume(23);                  // '('
      lookahead1W(4);               // WhiteSpace | Comment | 'var'
      consume(81);                  // 'var'
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      whitespace();
      parse_VariableDeclarationListNoIn();
      consume(38);                  // ';'
      lookahead1W(24);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
      if (l1 != 38)                 // ';'
      {
        whitespace();
        parse_Expression();
      }
      consume(38);                  // ';'
      lookahead1W(23);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | ')' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      if (l1 != 24)                 // ')'
      {
        whitespace();
        parse_Expression();
      }
      consume(24);                  // ')'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      whitespace();
      parse_Statement();
      break;
    case -5:
      consume(67);                  // 'for'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consume(23);                  // '('
      lookahead1W(21);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '(' | '[' | 'function' | 'new' |
                                    // 'this' | '{'
      whitespace();
      parse_LeftHandSideExpression();
      consume(71);                  // 'in'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_Expression();
      consume(24);                  // ')'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      whitespace();
      parse_Statement();
      break;
    case -6:
      consume(67);                  // 'for'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consume(23);                  // '('
      lookahead1W(4);               // WhiteSpace | Comment | 'var'
      consume(81);                  // 'var'
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      whitespace();
      parse_VariableDeclarationNoIn();
      consume(71);                  // 'in'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_Expression();
      consume(24);                  // ')'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      whitespace();
      parse_Statement();
      break;
    default:
      consume(83);                  // 'while'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consume(23);                  // '('
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_Expression();
      consume(24);                  // ')'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      whitespace();
      parse_Statement();
    }
    eventHandler.endNonterminal("IterationStatement", e0);
  }

  private void try_IterationStatement()
  {
    if (l1 == 67)                   // 'for'
    {
      lk = memoized(7, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          consumeT(67);             // 'for'
          lookahead1W(1);           // WhiteSpace | Comment | '('
          consumeT(23);             // '('
          lookahead1W(24);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
          if (l1 != 38)             // ';'
          {
            try_ExpressionNoIn();
          }
          consumeT(38);             // ';'
          lookahead1W(24);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
          if (l1 != 38)             // ';'
          {
            try_Expression();
          }
          consumeT(38);             // ';'
          lookahead1W(23);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | ')' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
          if (l1 != 24)             // ')'
          {
            try_Expression();
          }
          consumeT(24);             // ')'
          lookahead1W(28);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
          try_Statement();
          memoize(7, e0A, -3);
          lk = -7;
        }
        catch (ParseException p3A)
        {
          try
          {
            b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
            b1 = b1A; e1 = e1A; end = e1A; }
            consumeT(67);           // 'for'
            lookahead1W(1);         // WhiteSpace | Comment | '('
            consumeT(23);           // '('
            lookahead1W(4);         // WhiteSpace | Comment | 'var'
            consumeT(81);           // 'var'
            lookahead1W(0);         // Identifier | WhiteSpace | Comment
            try_VariableDeclarationListNoIn();
            consumeT(38);           // ';'
            lookahead1W(24);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
            if (l1 != 38)           // ';'
            {
              try_Expression();
            }
            consumeT(38);           // ';'
            lookahead1W(23);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | ')' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
            if (l1 != 24)           // ')'
            {
              try_Expression();
            }
            consumeT(24);           // ')'
            lookahead1W(28);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
            try_Statement();
            memoize(7, e0A, -4);
            lk = -7;
          }
          catch (ParseException p4A)
          {
            try
            {
              b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
              b1 = b1A; e1 = e1A; end = e1A; }
              consumeT(67);         // 'for'
              lookahead1W(1);       // WhiteSpace | Comment | '('
              consumeT(23);         // '('
              lookahead1W(21);      // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '(' | '[' | 'function' | 'new' |
                                    // 'this' | '{'
              try_LeftHandSideExpression();
              consumeT(71);         // 'in'
              lookahead1W(22);      // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
              try_Expression();
              consumeT(24);         // ')'
              lookahead1W(28);      // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
              try_Statement();
              memoize(7, e0A, -5);
              lk = -7;
            }
            catch (ParseException p5A)
            {
              lk = -6;
              b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
              b1 = b1A; e1 = e1A; end = e1A; }
              memoize(7, e0A, -6);
            }
          }
        }
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case 64:                        // 'do'
      consumeT(64);                 // 'do'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      try_Statement();
      lookahead1W(5);               // WhiteSpace | Comment | 'while'
      consumeT(83);                 // 'while'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consumeT(23);                 // '('
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_Expression();
      consumeT(24);                 // ')'
      lookahead1W(15);              // END | EOF | WhiteSpace | Comment | ';' | '}'
      try_Semicolon();
      break;
    case -3:
      consumeT(67);                 // 'for'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consumeT(23);                 // '('
      lookahead1W(24);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
      if (l1 != 38)                 // ';'
      {
        try_ExpressionNoIn();
      }
      consumeT(38);                 // ';'
      lookahead1W(24);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
      if (l1 != 38)                 // ';'
      {
        try_Expression();
      }
      consumeT(38);                 // ';'
      lookahead1W(23);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | ')' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      if (l1 != 24)                 // ')'
      {
        try_Expression();
      }
      consumeT(24);                 // ')'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      try_Statement();
      break;
    case -4:
      consumeT(67);                 // 'for'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consumeT(23);                 // '('
      lookahead1W(4);               // WhiteSpace | Comment | 'var'
      consumeT(81);                 // 'var'
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      try_VariableDeclarationListNoIn();
      consumeT(38);                 // ';'
      lookahead1W(24);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '~'
      if (l1 != 38)                 // ';'
      {
        try_Expression();
      }
      consumeT(38);                 // ';'
      lookahead1W(23);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | ')' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      if (l1 != 24)                 // ')'
      {
        try_Expression();
      }
      consumeT(24);                 // ')'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      try_Statement();
      break;
    case -5:
      consumeT(67);                 // 'for'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consumeT(23);                 // '('
      lookahead1W(21);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '(' | '[' | 'function' | 'new' |
                                    // 'this' | '{'
      try_LeftHandSideExpression();
      consumeT(71);                 // 'in'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_Expression();
      consumeT(24);                 // ')'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      try_Statement();
      break;
    case -6:
      consumeT(67);                 // 'for'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consumeT(23);                 // '('
      lookahead1W(4);               // WhiteSpace | Comment | 'var'
      consumeT(81);                 // 'var'
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      try_VariableDeclarationNoIn();
      consumeT(71);                 // 'in'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_Expression();
      consumeT(24);                 // ')'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      try_Statement();
      break;
    case -7:
      break;
    default:
      consumeT(83);                 // 'while'
      lookahead1W(1);               // WhiteSpace | Comment | '('
      consumeT(23);                 // '('
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_Expression();
      consumeT(24);                 // ')'
      lookahead1W(28);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
      try_Statement();
    }
  }

  private void parse_ExpressionNoIn()
  {
    eventHandler.startNonterminal("ExpressionNoIn", e0);
    parse_AssignmentExpressionNoIn();
    for (;;)
    {
      if (l1 != 30)                 // ','
      {
        break;
      }
      consume(30);                  // ','
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_AssignmentExpressionNoIn();
    }
    eventHandler.endNonterminal("ExpressionNoIn", e0);
  }

  private void try_ExpressionNoIn()
  {
    try_AssignmentExpressionNoIn();
    for (;;)
    {
      if (l1 != 30)                 // ','
      {
        break;
      }
      consumeT(30);                 // ','
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_AssignmentExpressionNoIn();
    }
  }

  private void parse_AssignmentExpressionNoIn()
  {
    eventHandler.startNonterminal("AssignmentExpressionNoIn", e0);
    if (l1 != 15                    // '!'
     && l1 != 27                    // '+'
     && l1 != 28                    // '++'
     && l1 != 31                    // '-'
     && l1 != 32                    // '--'
     && l1 != 63                    // 'delete'
     && l1 != 80                    // 'typeof'
     && l1 != 82                    // 'void'
     && l1 != 90)                   // '~'
    {
      lk = memoized(8, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          try_LeftHandSideExpression();
          try_AssignmentOperator();
          lookahead1W(22);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
          try_AssignmentExpressionNoIn();
          lk = -1;
        }
        catch (ParseException p1A)
        {
          lk = -2;
        }
        b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
        b1 = b1A; e1 = e1A; end = e1A; }
        memoize(8, e0, lk);
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case -1:
      parse_LeftHandSideExpression();
      whitespace();
      parse_AssignmentOperator();
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_AssignmentExpressionNoIn();
      break;
    default:
      parse_ConditionalExpressionNoIn();
    }
    eventHandler.endNonterminal("AssignmentExpressionNoIn", e0);
  }

  private void try_AssignmentExpressionNoIn()
  {
    if (l1 != 15                    // '!'
     && l1 != 27                    // '+'
     && l1 != 28                    // '++'
     && l1 != 31                    // '-'
     && l1 != 32                    // '--'
     && l1 != 63                    // 'delete'
     && l1 != 80                    // 'typeof'
     && l1 != 82                    // 'void'
     && l1 != 90)                   // '~'
    {
      lk = memoized(8, e0);
      if (lk == 0)
      {
        int b0A = b0; int e0A = e0; int l1A = l1;
        int b1A = b1; int e1A = e1;
        try
        {
          try_LeftHandSideExpression();
          try_AssignmentOperator();
          lookahead1W(22);          // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
          try_AssignmentExpressionNoIn();
          memoize(8, e0A, -1);
          lk = -3;
        }
        catch (ParseException p1A)
        {
          lk = -2;
          b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
          b1 = b1A; e1 = e1A; end = e1A; }
          memoize(8, e0A, -2);
        }
      }
    }
    else
    {
      lk = l1;
    }
    switch (lk)
    {
    case -1:
      try_LeftHandSideExpression();
      try_AssignmentOperator();
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_AssignmentExpressionNoIn();
      break;
    case -3:
      break;
    default:
      try_ConditionalExpressionNoIn();
    }
  }

  private void parse_ConditionalExpressionNoIn()
  {
    eventHandler.startNonterminal("ConditionalExpressionNoIn", e0);
    parse_LogicalORExpressionNoIn();
    for (;;)
    {
      if (l1 == 52)                 // '?'
      {
        lk = memoized(9, e0);
        if (lk == 0)
        {
          int b0A = b0; int e0A = e0; int l1A = l1;
          int b1A = b1; int e1A = e1;
          try
          {
            consumeT(52);           // '?'
            lookahead1W(22);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
            try_AssignmentExpression();
            consumeT(37);           // ':'
            lookahead1W(22);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
            try_AssignmentExpressionNoIn();
            lk = -1;
          }
          catch (ParseException p1A)
          {
            lk = -2;
          }
          b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
          b1 = b1A; e1 = e1A; end = e1A; }
          memoize(9, e0, lk);
        }
      }
      else
      {
        lk = l1;
      }
      if (lk != -1)
      {
        break;
      }
      consume(52);                  // '?'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_AssignmentExpression();
      consume(37);                  // ':'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_AssignmentExpressionNoIn();
    }
    eventHandler.endNonterminal("ConditionalExpressionNoIn", e0);
  }

  private void try_ConditionalExpressionNoIn()
  {
    try_LogicalORExpressionNoIn();
    for (;;)
    {
      if (l1 == 52)                 // '?'
      {
        lk = memoized(9, e0);
        if (lk == 0)
        {
          int b0A = b0; int e0A = e0; int l1A = l1;
          int b1A = b1; int e1A = e1;
          try
          {
            consumeT(52);           // '?'
            lookahead1W(22);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
            try_AssignmentExpression();
            consumeT(37);           // ':'
            lookahead1W(22);        // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
            try_AssignmentExpressionNoIn();
            memoize(9, e0A, -1);
            continue;
          }
          catch (ParseException p1A)
          {
            b0 = b0A; e0 = e0A; l1 = l1A; if (l1 == 0) {end = e0A;} else {
            b1 = b1A; e1 = e1A; end = e1A; }
            memoize(9, e0A, -2);
            break;
          }
        }
      }
      else
      {
        lk = l1;
      }
      if (lk != -1)
      {
        break;
      }
      consumeT(52);                 // '?'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_AssignmentExpression();
      consumeT(37);                 // ':'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_AssignmentExpressionNoIn();
    }
  }

  private void parse_LogicalORExpressionNoIn()
  {
    eventHandler.startNonterminal("LogicalORExpressionNoIn", e0);
    parse_LogicalANDExpressionNoIn();
    for (;;)
    {
      if (l1 != 88)                 // '||'
      {
        break;
      }
      consume(88);                  // '||'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_LogicalANDExpressionNoIn();
    }
    eventHandler.endNonterminal("LogicalORExpressionNoIn", e0);
  }

  private void try_LogicalORExpressionNoIn()
  {
    try_LogicalANDExpressionNoIn();
    for (;;)
    {
      if (l1 != 88)                 // '||'
      {
        break;
      }
      consumeT(88);                 // '||'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_LogicalANDExpressionNoIn();
    }
  }

  private void parse_LogicalANDExpressionNoIn()
  {
    eventHandler.startNonterminal("LogicalANDExpressionNoIn", e0);
    parse_BitwiseORExpressionNoIn();
    for (;;)
    {
      if (l1 != 21)                 // '&&'
      {
        break;
      }
      consume(21);                  // '&&'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_BitwiseORExpressionNoIn();
    }
    eventHandler.endNonterminal("LogicalANDExpressionNoIn", e0);
  }

  private void try_LogicalANDExpressionNoIn()
  {
    try_BitwiseORExpressionNoIn();
    for (;;)
    {
      if (l1 != 21)                 // '&&'
      {
        break;
      }
      consumeT(21);                 // '&&'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_BitwiseORExpressionNoIn();
    }
  }

  private void parse_BitwiseORExpressionNoIn()
  {
    eventHandler.startNonterminal("BitwiseORExpressionNoIn", e0);
    parse_BitwiseXORExpressionNoIn();
    for (;;)
    {
      if (l1 != 86)                 // '|'
      {
        break;
      }
      consume(86);                  // '|'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_BitwiseXORExpressionNoIn();
    }
    eventHandler.endNonterminal("BitwiseORExpressionNoIn", e0);
  }

  private void try_BitwiseORExpressionNoIn()
  {
    try_BitwiseXORExpressionNoIn();
    for (;;)
    {
      if (l1 != 86)                 // '|'
      {
        break;
      }
      consumeT(86);                 // '|'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_BitwiseXORExpressionNoIn();
    }
  }

  private void parse_BitwiseXORExpressionNoIn()
  {
    eventHandler.startNonterminal("BitwiseXORExpressionNoIn", e0);
    parse_BitwiseANDExpressionNoIn();
    for (;;)
    {
      if (l1 != 55)                 // '^'
      {
        break;
      }
      consume(55);                  // '^'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_BitwiseANDExpressionNoIn();
    }
    eventHandler.endNonterminal("BitwiseXORExpressionNoIn", e0);
  }

  private void try_BitwiseXORExpressionNoIn()
  {
    try_BitwiseANDExpressionNoIn();
    for (;;)
    {
      if (l1 != 55)                 // '^'
      {
        break;
      }
      consumeT(55);                 // '^'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_BitwiseANDExpressionNoIn();
    }
  }

  private void parse_BitwiseANDExpressionNoIn()
  {
    eventHandler.startNonterminal("BitwiseANDExpressionNoIn", e0);
    parse_EqualityExpressionNoIn();
    for (;;)
    {
      if (l1 != 20)                 // '&'
      {
        break;
      }
      consume(20);                  // '&'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_EqualityExpressionNoIn();
    }
    eventHandler.endNonterminal("BitwiseANDExpressionNoIn", e0);
  }

  private void try_BitwiseANDExpressionNoIn()
  {
    try_EqualityExpressionNoIn();
    for (;;)
    {
      if (l1 != 20)                 // '&'
      {
        break;
      }
      consumeT(20);                 // '&'
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_EqualityExpressionNoIn();
    }
  }

  private void parse_EqualityExpressionNoIn()
  {
    eventHandler.startNonterminal("EqualityExpressionNoIn", e0);
    parse_RelationalExpressionNoIn();
    for (;;)
    {
      if (l1 != 16                  // '!='
       && l1 != 17                  // '!=='
       && l1 != 44                  // '=='
       && l1 != 45)                 // '==='
      {
        break;
      }
      switch (l1)
      {
      case 44:                      // '=='
        consume(44);                // '=='
        break;
      case 16:                      // '!='
        consume(16);                // '!='
        break;
      case 45:                      // '==='
        consume(45);                // '==='
        break;
      default:
        consume(17);                // '!=='
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_RelationalExpressionNoIn();
    }
    eventHandler.endNonterminal("EqualityExpressionNoIn", e0);
  }

  private void try_EqualityExpressionNoIn()
  {
    try_RelationalExpressionNoIn();
    for (;;)
    {
      if (l1 != 16                  // '!='
       && l1 != 17                  // '!=='
       && l1 != 44                  // '=='
       && l1 != 45)                 // '==='
      {
        break;
      }
      switch (l1)
      {
      case 44:                      // '=='
        consumeT(44);               // '=='
        break;
      case 16:                      // '!='
        consumeT(16);               // '!='
        break;
      case 45:                      // '==='
        consumeT(45);               // '==='
        break;
      default:
        consumeT(17);               // '!=='
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_RelationalExpressionNoIn();
    }
  }

  private void parse_RelationalExpressionNoIn()
  {
    eventHandler.startNonterminal("RelationalExpressionNoIn", e0);
    parse_ShiftExpression();
    for (;;)
    {
      if (l1 != 39                  // '<'
       && l1 != 42                  // '<='
       && l1 != 46                  // '>'
       && l1 != 47                  // '>='
       && l1 != 72)                 // 'instanceof'
      {
        break;
      }
      switch (l1)
      {
      case 39:                      // '<'
        consume(39);                // '<'
        break;
      case 46:                      // '>'
        consume(46);                // '>'
        break;
      case 42:                      // '<='
        consume(42);                // '<='
        break;
      case 47:                      // '>='
        consume(47);                // '>='
        break;
      default:
        consume(72);                // 'instanceof'
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      whitespace();
      parse_ShiftExpression();
    }
    eventHandler.endNonterminal("RelationalExpressionNoIn", e0);
  }

  private void try_RelationalExpressionNoIn()
  {
    try_ShiftExpression();
    for (;;)
    {
      if (l1 != 39                  // '<'
       && l1 != 42                  // '<='
       && l1 != 46                  // '>'
       && l1 != 47                  // '>='
       && l1 != 72)                 // 'instanceof'
      {
        break;
      }
      switch (l1)
      {
      case 39:                      // '<'
        consumeT(39);               // '<'
        break;
      case 46:                      // '>'
        consumeT(46);               // '>'
        break;
      case 42:                      // '<='
        consumeT(42);               // '<='
        break;
      case 47:                      // '>='
        consumeT(47);               // '>='
        break;
      default:
        consumeT(72);               // 'instanceof'
      }
      lookahead1W(22);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
      try_ShiftExpression();
    }
  }

  private void parse_VariableDeclarationListNoIn()
  {
    eventHandler.startNonterminal("VariableDeclarationListNoIn", e0);
    parse_VariableDeclarationNoIn();
    for (;;)
    {
      if (l1 != 30)                 // ','
      {
        break;
      }
      consume(30);                  // ','
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      whitespace();
      parse_VariableDeclarationNoIn();
    }
    eventHandler.endNonterminal("VariableDeclarationListNoIn", e0);
  }

  private void try_VariableDeclarationListNoIn()
  {
    try_VariableDeclarationNoIn();
    for (;;)
    {
      if (l1 != 30)                 // ','
      {
        break;
      }
      consumeT(30);                 // ','
      lookahead1W(0);               // Identifier | WhiteSpace | Comment
      try_VariableDeclarationNoIn();
    }
  }

  private void parse_VariableDeclarationNoIn()
  {
    eventHandler.startNonterminal("VariableDeclarationNoIn", e0);
    consume(4);                     // Identifier
    lookahead1W(16);                // WhiteSpace | Comment | ',' | ';' | '=' | 'in'
    if (l1 == 43)                   // '='
    {
      whitespace();
      parse_InitialiserNoIn();
    }
    eventHandler.endNonterminal("VariableDeclarationNoIn", e0);
  }

  private void try_VariableDeclarationNoIn()
  {
    consumeT(4);                    // Identifier
    lookahead1W(16);                // WhiteSpace | Comment | ',' | ';' | '=' | 'in'
    if (l1 == 43)                   // '='
    {
      try_InitialiserNoIn();
    }
  }

  private void parse_InitialiserNoIn()
  {
    eventHandler.startNonterminal("InitialiserNoIn", e0);
    consume(43);                    // '='
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    whitespace();
    parse_AssignmentExpressionNoIn();
    eventHandler.endNonterminal("InitialiserNoIn", e0);
  }

  private void try_InitialiserNoIn()
  {
    consumeT(43);                   // '='
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    try_AssignmentExpressionNoIn();
  }

  private void parse_ContinueStatement()
  {
    eventHandler.startNonterminal("ContinueStatement", e0);
    consume(60);                    // 'continue'
    lookahead1W(17);                // END | EOF | Identifier | WhiteSpace | Comment | ';' | '}'
    if (l1 != 38 && l1 >= 0 && followsLineTerminator()) // ';'
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    if (l1 == 4)                    // Identifier
    {
      consume(4);                   // Identifier
    }
    lookahead1W(15);                // END | EOF | WhiteSpace | Comment | ';' | '}'
    whitespace();
    parse_Semicolon();
    eventHandler.endNonterminal("ContinueStatement", e0);
  }

  private void try_ContinueStatement()
  {
    consumeT(60);                   // 'continue'
    lookahead1W(17);                // END | EOF | Identifier | WhiteSpace | Comment | ';' | '}'
    if (l1 != 38 && l1 >= 0 && followsLineTerminator()) // ';'
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    if (l1 == 4)                    // Identifier
    {
      consumeT(4);                  // Identifier
    }
    lookahead1W(15);                // END | EOF | WhiteSpace | Comment | ';' | '}'
    try_Semicolon();
  }

  private void parse_BreakStatement()
  {
    eventHandler.startNonterminal("BreakStatement", e0);
    consume(57);                    // 'break'
    lookahead1W(17);                // END | EOF | Identifier | WhiteSpace | Comment | ';' | '}'
    if (l1 != 38 && l1 >= 0 && followsLineTerminator()) // ';'
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    if (l1 == 4)                    // Identifier
    {
      consume(4);                   // Identifier
    }
    lookahead1W(15);                // END | EOF | WhiteSpace | Comment | ';' | '}'
    whitespace();
    parse_Semicolon();
    eventHandler.endNonterminal("BreakStatement", e0);
  }

  private void try_BreakStatement()
  {
    consumeT(57);                   // 'break'
    lookahead1W(17);                // END | EOF | Identifier | WhiteSpace | Comment | ';' | '}'
    if (l1 != 38 && l1 >= 0 && followsLineTerminator()) // ';'
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    if (l1 == 4)                    // Identifier
    {
      consumeT(4);                  // Identifier
    }
    lookahead1W(15);                // END | EOF | WhiteSpace | Comment | ';' | '}'
    try_Semicolon();
  }

  private void parse_ReturnStatement()
  {
    eventHandler.startNonterminal("ReturnStatement", e0);
    consume(74);                    // 'return'
    lookahead1W(26);                // END | EOF | Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '}' | '~'
    if (l1 != 38 && l1 >= 0 && followsLineTerminator()) // ';'
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    if (l1 != 1                     // END
     && l1 != 3                     // EOF
     && l1 != 38                    // ';'
     && l1 != 89)                   // '}'
    {
      whitespace();
      parse_Expression();
    }
    whitespace();
    parse_Semicolon();
    eventHandler.endNonterminal("ReturnStatement", e0);
  }

  private void try_ReturnStatement()
  {
    consumeT(74);                   // 'return'
    lookahead1W(26);                // END | EOF | Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' |
                                    // '{' | '}' | '~'
    if (l1 != 38 && l1 >= 0 && followsLineTerminator()) // ';'
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    if (l1 != 1                     // END
     && l1 != 3                     // EOF
     && l1 != 38                    // ';'
     && l1 != 89)                   // '}'
    {
      try_Expression();
    }
    try_Semicolon();
  }

  private void parse_WithStatement()
  {
    eventHandler.startNonterminal("WithStatement", e0);
    consume(84);                    // 'with'
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consume(23);                    // '('
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    whitespace();
    parse_Expression();
    consume(24);                    // ')'
    lookahead1W(28);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
    whitespace();
    parse_Statement();
    eventHandler.endNonterminal("WithStatement", e0);
  }

  private void try_WithStatement()
  {
    consumeT(84);                   // 'with'
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consumeT(23);                   // '('
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    try_Expression();
    consumeT(24);                   // ')'
    lookahead1W(28);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
    try_Statement();
  }

  private void parse_LabelledStatement()
  {
    eventHandler.startNonterminal("LabelledStatement", e0);
    consume(4);                     // Identifier
    lookahead1W(3);                 // WhiteSpace | Comment | ':'
    consume(37);                    // ':'
    lookahead1W(28);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
    whitespace();
    parse_Statement();
    eventHandler.endNonterminal("LabelledStatement", e0);
  }

  private void try_LabelledStatement()
  {
    consumeT(4);                    // Identifier
    lookahead1W(3);                 // WhiteSpace | Comment | ':'
    consumeT(37);                   // ':'
    lookahead1W(28);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '~'
    try_Statement();
  }

  private void parse_SwitchStatement()
  {
    eventHandler.startNonterminal("SwitchStatement", e0);
    consume(76);                    // 'switch'
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consume(23);                    // '('
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    whitespace();
    parse_Expression();
    consume(24);                    // ')'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    whitespace();
    parse_CaseBlock();
    eventHandler.endNonterminal("SwitchStatement", e0);
  }

  private void try_SwitchStatement()
  {
    consumeT(76);                   // 'switch'
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consumeT(23);                   // '('
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    try_Expression();
    consumeT(24);                   // ')'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    try_CaseBlock();
  }

  private void parse_CaseBlock()
  {
    eventHandler.startNonterminal("CaseBlock", e0);
    consume(85);                    // '{'
    for (;;)
    {
      lookahead1W(14);              // WhiteSpace | Comment | 'case' | 'default' | '}'
      if (l1 != 58)                 // 'case'
      {
        break;
      }
      whitespace();
      parse_CaseClause();
    }
    if (l1 == 62)                   // 'default'
    {
      whitespace();
      parse_DefaultClause();
      for (;;)
      {
        if (l1 != 58)               // 'case'
        {
          break;
        }
        whitespace();
        parse_CaseClause();
      }
    }
    consume(89);                    // '}'
    eventHandler.endNonterminal("CaseBlock", e0);
  }

  private void try_CaseBlock()
  {
    consumeT(85);                   // '{'
    for (;;)
    {
      lookahead1W(14);              // WhiteSpace | Comment | 'case' | 'default' | '}'
      if (l1 != 58)                 // 'case'
      {
        break;
      }
      try_CaseClause();
    }
    if (l1 == 62)                   // 'default'
    {
      try_DefaultClause();
      for (;;)
      {
        if (l1 != 58)               // 'case'
        {
          break;
        }
        try_CaseClause();
      }
    }
    consumeT(89);                   // '}'
  }

  private void parse_CaseClause()
  {
    eventHandler.startNonterminal("CaseClause", e0);
    consume(58);                    // 'case'
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    whitespace();
    parse_Expression();
    consume(37);                    // ':'
    for (;;)
    {
      lookahead1W(33);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'case' | 'continue' | 'debugger' | 'default' |
                                    // 'delete' | 'do' | 'for' | 'function' | 'if' | 'new' | 'return' | 'switch' |
                                    // 'this' | 'throw' | 'try' | 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' |
                                    // '}' | '~'
      if (l1 == 58                  // 'case'
       || l1 == 62                  // 'default'
       || l1 == 89)                 // '}'
      {
        break;
      }
      whitespace();
      parse_Statement();
    }
    eventHandler.endNonterminal("CaseClause", e0);
  }

  private void try_CaseClause()
  {
    consumeT(58);                   // 'case'
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    try_Expression();
    consumeT(37);                   // ':'
    for (;;)
    {
      lookahead1W(33);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'case' | 'continue' | 'debugger' | 'default' |
                                    // 'delete' | 'do' | 'for' | 'function' | 'if' | 'new' | 'return' | 'switch' |
                                    // 'this' | 'throw' | 'try' | 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' |
                                    // '}' | '~'
      if (l1 == 58                  // 'case'
       || l1 == 62                  // 'default'
       || l1 == 89)                 // '}'
      {
        break;
      }
      try_Statement();
    }
  }

  private void parse_DefaultClause()
  {
    eventHandler.startNonterminal("DefaultClause", e0);
    consume(62);                    // 'default'
    lookahead1W(3);                 // WhiteSpace | Comment | ':'
    consume(37);                    // ':'
    for (;;)
    {
      lookahead1W(32);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'case' | 'continue' | 'debugger' | 'delete' | 'do' |
                                    // 'for' | 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' |
                                    // 'try' | 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
      if (l1 == 58                  // 'case'
       || l1 == 89)                 // '}'
      {
        break;
      }
      whitespace();
      parse_Statement();
    }
    eventHandler.endNonterminal("DefaultClause", e0);
  }

  private void try_DefaultClause()
  {
    consumeT(62);                   // 'default'
    lookahead1W(3);                 // WhiteSpace | Comment | ':'
    consumeT(37);                   // ':'
    for (;;)
    {
      lookahead1W(32);              // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'case' | 'continue' | 'debugger' | 'delete' | 'do' |
                                    // 'for' | 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' |
                                    // 'try' | 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
      if (l1 == 58                  // 'case'
       || l1 == 89)                 // '}'
      {
        break;
      }
      try_Statement();
    }
  }

  private void parse_ThrowStatement()
  {
    eventHandler.startNonterminal("ThrowStatement", e0);
    consume(78);                    // 'throw'
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    if (l1 != 38 && l1 >= 0 && followsLineTerminator()) // ';'
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    whitespace();
    parse_Expression();
    whitespace();
    parse_Semicolon();
    eventHandler.endNonterminal("ThrowStatement", e0);
  }

  private void try_ThrowStatement()
  {
    consumeT(78);                   // 'throw'
    lookahead1W(22);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | '[' | 'delete' | 'function' | 'new' | 'this' | 'typeof' | 'void' | '{' |
                                    // '~'
    if (l1 != 38 && l1 >= 0 && followsLineTerminator()) // ';'
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    try_Expression();
    try_Semicolon();
  }

  private void parse_TryStatement()
  {
    eventHandler.startNonterminal("TryStatement", e0);
    consume(79);                    // 'try'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    whitespace();
    parse_Block();
    lookahead1W(12);                // WhiteSpace | Comment | 'catch' | 'finally'
    switch (l1)
    {
    case 59:                        // 'catch'
      whitespace();
      parse_Catch();
      lookahead1W(35);              // EOF | Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'case' | 'continue' | 'debugger' | 'default' |
                                    // 'delete' | 'do' | 'else' | 'finally' | 'for' | 'function' | 'if' | 'new' |
                                    // 'return' | 'switch' | 'this' | 'throw' | 'try' | 'typeof' | 'var' | 'void' |
                                    // 'while' | 'with' | '{' | '}' | '~'
      if (l1 == 66)                 // 'finally'
      {
        whitespace();
        parse_Finally();
      }
      break;
    default:
      whitespace();
      parse_Finally();
    }
    eventHandler.endNonterminal("TryStatement", e0);
  }

  private void try_TryStatement()
  {
    consumeT(79);                   // 'try'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    try_Block();
    lookahead1W(12);                // WhiteSpace | Comment | 'catch' | 'finally'
    switch (l1)
    {
    case 59:                        // 'catch'
      try_Catch();
      lookahead1W(35);              // EOF | Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'case' | 'continue' | 'debugger' | 'default' |
                                    // 'delete' | 'do' | 'else' | 'finally' | 'for' | 'function' | 'if' | 'new' |
                                    // 'return' | 'switch' | 'this' | 'throw' | 'try' | 'typeof' | 'var' | 'void' |
                                    // 'while' | 'with' | '{' | '}' | '~'
      if (l1 == 66)                 // 'finally'
      {
        try_Finally();
      }
      break;
    default:
      try_Finally();
    }
  }

  private void parse_Catch()
  {
    eventHandler.startNonterminal("Catch", e0);
    consume(59);                    // 'catch'
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consume(23);                    // '('
    lookahead1W(0);                 // Identifier | WhiteSpace | Comment
    consume(4);                     // Identifier
    lookahead1W(2);                 // WhiteSpace | Comment | ')'
    consume(24);                    // ')'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    whitespace();
    parse_Block();
    eventHandler.endNonterminal("Catch", e0);
  }

  private void try_Catch()
  {
    consumeT(59);                   // 'catch'
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consumeT(23);                   // '('
    lookahead1W(0);                 // Identifier | WhiteSpace | Comment
    consumeT(4);                    // Identifier
    lookahead1W(2);                 // WhiteSpace | Comment | ')'
    consumeT(24);                   // ')'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    try_Block();
  }

  private void parse_Finally()
  {
    eventHandler.startNonterminal("Finally", e0);
    consume(66);                    // 'finally'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    whitespace();
    parse_Block();
    eventHandler.endNonterminal("Finally", e0);
  }

  private void try_Finally()
  {
    consumeT(66);                   // 'finally'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    try_Block();
  }

  private void parse_DebuggerStatement()
  {
    eventHandler.startNonterminal("DebuggerStatement", e0);
    consume(61);                    // 'debugger'
    lookahead1W(15);                // END | EOF | WhiteSpace | Comment | ';' | '}'
    whitespace();
    parse_Semicolon();
    eventHandler.endNonterminal("DebuggerStatement", e0);
  }

  private void try_DebuggerStatement()
  {
    consumeT(61);                   // 'debugger'
    lookahead1W(15);                // END | EOF | WhiteSpace | Comment | ';' | '}'
    try_Semicolon();
  }

  private void parse_FunctionDeclaration()
  {
    eventHandler.startNonterminal("FunctionDeclaration", e0);
    consume(68);                    // 'function'
    lookahead1W(0);                 // Identifier | WhiteSpace | Comment
    consume(4);                     // Identifier
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consume(23);                    // '('
    lookahead1W(9);                 // Identifier | WhiteSpace | Comment | ')'
    if (l1 == 4)                    // Identifier
    {
      whitespace();
      parse_FormalParameterList();
    }
    consume(24);                    // ')'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    consume(85);                    // '{'
    lookahead1W(30);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
    whitespace();
    parse_FunctionBody();
    consume(89);                    // '}'
    eventHandler.endNonterminal("FunctionDeclaration", e0);
  }

  private void try_FunctionDeclaration()
  {
    consumeT(68);                   // 'function'
    lookahead1W(0);                 // Identifier | WhiteSpace | Comment
    consumeT(4);                    // Identifier
    lookahead1W(1);                 // WhiteSpace | Comment | '('
    consumeT(23);                   // '('
    lookahead1W(9);                 // Identifier | WhiteSpace | Comment | ')'
    if (l1 == 4)                    // Identifier
    {
      try_FormalParameterList();
    }
    consumeT(24);                   // ')'
    lookahead1W(6);                 // WhiteSpace | Comment | '{'
    consumeT(85);                   // '{'
    lookahead1W(30);                // Identifier | NullLiteral | BooleanLiteral | StringLiteral |
                                    // RegularExpressionLiteral | DecimalLiteral | HexIntegerLiteral |
                                    // OctalIntegerLiteral | WhiteSpace | Comment | '!' | '(' | '+' | '++' | '-' |
                                    // '--' | ';' | '[' | 'break' | 'continue' | 'debugger' | 'delete' | 'do' | 'for' |
                                    // 'function' | 'if' | 'new' | 'return' | 'switch' | 'this' | 'throw' | 'try' |
                                    // 'typeof' | 'var' | 'void' | 'while' | 'with' | '{' | '}' | '~'
    try_FunctionBody();
    consumeT(89);                   // '}'
  }

  private void parse_Semicolon()
  {
    eventHandler.startNonterminal("Semicolon", e0);
    if (l1 == 89                    // '}'
     || l1 == 3                     // EOF
     || l1 != 38 && l1 >= 0 && followsLineTerminator())
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    consume(38);                    // ';'
    eventHandler.endNonterminal("Semicolon", e0);
  }

  private void try_Semicolon()
  {
    if (l1 == 89                    // '}'
     || l1 == 3                     // EOF
     || l1 != 38 && l1 >= 0 && followsLineTerminator())
    {
      l1 = 38;                      // ';'
      e1 = b1;
      end = e1;
    }
    consumeT(38);                   // ';'
  }

  private void parse_IdentifierName()
  {
    eventHandler.startNonterminal("IdentifierName", e0);
    switch (l1)
    {
    case 7:                         // IdentifierName^Token
      consume(7);                   // IdentifierName^Token
      break;
    case 69:                        // 'get'
      consume(69);                  // 'get'
      break;
    default:
      consume(75);                  // 'set'
    }
    eventHandler.endNonterminal("IdentifierName", e0);
  }

  private void try_IdentifierName()
  {
    switch (l1)
    {
    case 7:                         // IdentifierName^Token
      consumeT(7);                  // IdentifierName^Token
      break;
    case 69:                        // 'get'
      consumeT(69);                 // 'get'
      break;
    default:
      consumeT(75);                 // 'set'
    }
  }

  private boolean followsLineTerminator()
  {
    for (int i = e0 == b1 ? b0 : e0; i < b1; ++i)
    {
      int c = input.charAt(i);
      if (c == 0xA || c == 0xD || c == 0x2028 || c == 0x2029) return true;
    }
    return false;
  }

  private void consume(int t)
  {
    if (l1 == t)
    {
      whitespace();
      eventHandler.terminal(TOKEN[l1], b1, e1);
      b0 = b1; e0 = e1; l1 = 0;
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
      b0 = b1; e0 = e1; l1 = 0;
    }
    else
    {
      error(b1, e1, 0, l1, t);
    }
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
      if (code != 13                // WhiteSpace
       && code != 14)               // Comment
      {
        break;
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

  private int error(int b, int e, int s, int l, int t)
  {
    if (e >= ex)
    {
      bx = b;
      ex = e;
      sx = s;
      lx = l;
      tx = t;
    }
    throw new ParseException(bx, ex, sx, lx, tx);
  }

  private void memoize(int i, int e, int v)
  {
    memo.put((e << 4) + i, v);
  }

  private int memoized(int i, int e)
  {
    Integer v = memo.get((e << 4) + i);
    return v == null ? 0 : v;
  }

  private int lk, b0, e0;
  private int l1, b1, e1;
  private int bx, ex, sx, lx, tx;
  private EventHandler eventHandler = null;
  private java.util.Map<Integer, Integer> memo = new java.util.HashMap<Integer, Integer>();
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

    for (int code = result & 511; code != 0; )
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
        int c1 = c0 >> 3;
        charclass = MAP1[(c0 & 7) + MAP1[(c1 & 31) + MAP1[c1 >> 5]]];
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

        int lo = 0, hi = 3;
        for (int m = 2; ; m = (hi + lo) >> 1)
        {
          if (MAP2[m] > c0) {hi = m - 1;}
          else if (MAP2[4 + m] < c0) {lo = m + 1;}
          else {charclass = MAP2[8 + m]; break;}
          if (lo > hi) {charclass = 0; break;}
        }
      }

      state = code;
      int i0 = (charclass << 9) + code - 1;
      code = TRANSITION[(i0 & 7) + TRANSITION[i0 >> 3]];

      if (code > 511)
      {
        result = code;
        code &= 511;
        end = current;
      }
    }

    result >>= 9;
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
    int s = tokenSetId < 0 ? - tokenSetId : INITIAL[tokenSetId] & 511;
    for (int i = 0; i < 91; i += 32)
    {
      int j = i;
      int i0 = (i >> 5) * 368 + s - 1;
      int f = EXPECTED[(i0 & 3) + EXPECTED[i0 >> 2]];
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
      /*   0 */ "66, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 5",
      /*  34 */ "6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 21, 21, 21, 21, 21, 21, 22, 22, 23, 24",
      /*  60 */ "25, 26, 27, 28, 29, 30, 30, 30, 30, 31, 30, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 32, 8",
      /*  90 */ "8, 33, 34, 35, 36, 8, 29, 37, 38, 39, 40, 41, 42, 43, 44, 45, 8, 46, 47, 48, 49, 50, 51, 8, 52, 53",
      /* 116 */ "54, 55, 56, 57, 58, 59, 8, 60, 61, 62, 63, 29"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 128; ++i) {MAP0[i] = Integer.parseInt(s2[i]);}
  }

  private static final int[] MAP1 = new int[1536];
  static
  {
    final String s1[] =
    {
      /*    0 */ "216, 335, 247, 367, 432, 695, 303, 400, 400, 464, 496, 528, 560, 592, 640, 669, 899, 727, 400, 400",
      /*   20 */ "400, 400, 608, 400, 398, 400, 400, 400, 400, 400, 759, 791, 823, 275, 400, 400, 400, 400, 400, 400",
      /*   40 */ "400, 400, 400, 400, 400, 400, 400, 400, 855, 887, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400",
      /*   60 */ "400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 931, 931",
      /*   80 */ "931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931",
      /*  100 */ "931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931",
      /*  120 */ "931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931",
      /*  140 */ "931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 943",
      /*  160 */ "400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 931, 931, 931, 931, 931, 931, 931, 931",
      /*  180 */ "931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931",
      /*  200 */ "931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 931, 975, 1007, 1016, 1008",
      /*  219 */ "1008, 1024, 1032, 1040, 1048, 1056, 1315, 1315, 1075, 1096, 1104, 1112, 1120, 1199, 1196, 1196, 1196",
      /*  236 */ "1137, 1196, 1346, 1196, 1315, 1315, 1316, 1315, 1315, 1315, 1316, 1315, 1315, 1315, 1196, 1196, 1196",
      /*  253 */ "1196, 1196, 1196, 1196, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1195, 1196",
      /*  270 */ "1450, 1194, 1196, 1397, 1196, 1196, 1196, 1196, 1196, 1139, 1495, 1196, 1196, 1196, 1196, 1196, 1196",
      /*  287 */ "1196, 1196, 1196, 1196, 1193, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196",
      /*  304 */ "1196, 1196, 1196, 1314, 1315, 1315, 1193, 1412, 1066, 1396, 1196, 1391, 1397, 1412, 1315, 1315, 1315",
      /*  321 */ "1315, 1315, 1315, 1315, 1315, 1451, 1315, 1316, 1207, 1391, 1372, 1270, 1391, 1397, 1315, 1315, 1315",
      /*  338 */ "1315, 1315, 1315, 1311, 1316, 1314, 1298, 1315, 1315, 1315, 1315, 1315, 1316, 1315, 1315, 1315, 1315",
      /*  355 */ "1315, 1315, 1315, 1315, 1460, 1448, 1315, 1315, 1315, 1315, 1257, 1313, 1391, 1391, 1391, 1391, 1391",
      /*  372 */ "1391, 1391, 1391, 1393, 1196, 1196, 1196, 1397, 1196, 1196, 1196, 1173, 1517, 1315, 1315, 1297, 1315",
      /*  389 */ "1315, 1315, 1315, 1316, 1316, 1426, 1298, 1315, 1460, 1196, 1198, 1196, 1196, 1196, 1196, 1196, 1196",
      /*  406 */ "1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196",
      /*  423 */ "1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1314, 1318, 1315, 1315, 1315, 1315, 1315, 1315",
      /*  440 */ "1315, 1315, 1314, 1318, 1315, 1315, 1315, 1315, 1147, 1196, 1315, 1315, 1315, 1315, 1315, 1315, 1415",
      /*  457 */ "1160, 1315, 1315, 1315, 1416, 1414, 1194, 1528, 1315, 1315, 1315, 1315, 1315, 1315, 1223, 1391, 1393",
      /*  474 */ "1271, 1315, 1241, 1391, 1196, 1196, 1528, 1415, 1312, 1315, 1315, 1298, 1255, 1266, 1184, 1244, 1346",
      /*  491 */ "1281, 1241, 1391, 1194, 1196, 1291, 1461, 1312, 1315, 1315, 1298, 1306, 1266, 1247, 1244, 1196, 1515",
      /*  508 */ "1347, 1391, 1213, 1196, 1528, 1516, 1297, 1315, 1315, 1298, 1501, 1223, 1327, 1152, 1196, 1196, 1336",
      /*  525 */ "1391, 1196, 1196, 1528, 1415, 1312, 1315, 1315, 1298, 1309, 1223, 1272, 1244, 1347, 1281, 1163, 1391",
      /*  542 */ "1196, 1196, 1487, 1215, 1360, 1356, 1258, 1215, 1317, 1163, 1273, 1270, 1346, 1196, 1346, 1391, 1196",
      /*  559 */ "1196, 1528, 1318, 1298, 1315, 1315, 1298, 1319, 1163, 1328, 1270, 1348, 1196, 1163, 1391, 1196, 1196",
      /*  576 */ "1487, 1318, 1298, 1315, 1315, 1298, 1319, 1163, 1328, 1270, 1348, 1139, 1163, 1391, 1196, 1196, 1487",
      /*  593 */ "1318, 1298, 1315, 1315, 1298, 1315, 1163, 1185, 1270, 1346, 1196, 1163, 1391, 1196, 1196, 1196, 1196",
      /*  610 */ "1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1137, 1196, 1196",
      /*  627 */ "1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1314, 1315, 1315, 1315",
      /*  644 */ "1315, 1316, 1368, 1396, 1063, 1392, 1391, 1397, 1196, 1196, 1196, 1196, 1436, 1086, 1449, 1314, 1127",
      /*  661 */ "1283, 1368, 1233, 1380, 1393, 1391, 1397, 1196, 1196, 1196, 1196, 1397, 1391, 1397, 1405, 1385, 1315",
      /*  678 */ "1314, 1315, 1315, 1315, 1194, 1390, 1391, 1328, 1395, 1327, 1390, 1391, 1393, 1390, 1344, 1196, 1196",
      /*  695 */ "1196, 1196, 1196, 1196, 1196, 1196, 1314, 1315, 1315, 1315, 1316, 1171, 1314, 1315, 1315, 1315, 1316",
      /*  712 */ "1196, 1390, 1391, 1181, 1391, 1391, 1229, 1341, 1196, 1315, 1315, 1315, 1193, 1193, 1196, 1501, 1500",
      /*  729 */ "1193, 1196, 1196, 1196, 1196, 1424, 1195, 1424, 1257, 1446, 1129, 1256, 1435, 1196, 1196, 1196, 1196",
      /*  746 */ "1139, 1196, 1088, 1138, 1358, 1193, 1196, 1196, 1196, 1196, 1444, 1195, 1171, 1315, 1315, 1315, 1315",
      /*  763 */ "1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1460, 1315",
      /*  780 */ "1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1194, 1315, 1315, 1414, 1414, 1315, 1315",
      /*  797 */ "1315, 1315, 1414, 1414, 1315, 1427, 1315, 1315, 1315, 1414, 1315, 1315, 1315, 1315, 1315, 1315, 1318",
      /*  814 */ "1082, 1294, 1459, 1416, 1460, 1315, 1459, 1294, 1459, 1476, 1481, 1196, 1196, 1196, 1469, 1196, 1196",
      /*  831 */ "1196, 1196, 1196, 1197, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196",
      /*  848 */ "1196, 1391, 1394, 1344, 1196, 1196, 1196, 1509, 1196, 1196, 1196, 1314, 1067, 1183, 1196, 1314, 1315",
      /*  865 */ "1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1459, 1484, 1314, 1315, 1315, 1315, 1315, 1315, 1315",
      /*  882 */ "1315, 1315, 1315, 1315, 1525, 1448, 1315, 1315, 1315, 1315, 1459, 1196, 1196, 1196, 1196, 1196, 1196",
      /*  899 */ "1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196",
      /*  916 */ "1196, 1196, 1196, 1315, 1315, 1315, 1315, 1414, 1196, 1315, 1315, 1315, 1315, 1316, 1196, 1315, 1315",
      /*  933 */ "1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315",
      /*  950 */ "1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1414, 1196, 1196, 1196",
      /*  967 */ "1196, 1196, 1196, 1196, 1196, 1196, 1196, 1196, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315",
      /*  984 */ "1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1315, 1460, 1196, 1196, 1196, 1196, 1196",
      /* 1001 */ "1196, 1196, 1196, 1196, 1196, 1196, 66, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 4, 0, 0, 1, 5, 6, 7",
      /* 1028 */ "8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 21, 21, 21, 21, 21, 21, 22, 22, 23, 24, 25, 26",
      /* 1054 */ "27, 28, 29, 30, 30, 30, 30, 31, 30, 8, 8, 8, 8, 8, 8, 64, 64, 64, 64, 64, 64, 32, 8, 8, 33, 34, 35",
      /* 1081 */ "36, 8, 8, 8, 8, 8, 29, 8, 29, 29, 8, 29, 29, 8, 8, 29, 37, 38, 39, 40, 41, 42, 43, 44, 45, 8, 46, 47",
      /* 1109 */ "48, 49, 50, 51, 8, 52, 53, 54, 55, 56, 57, 58, 59, 8, 60, 61, 62, 63, 29, 8, 8, 8, 29, 8, 29, 8, 29",
      /* 1136 */ "8, 1, 29, 29, 29, 29, 29, 29, 29, 8, 29, 8, 8, 29, 64, 64, 64, 64, 29, 64, 64, 64, 29, 29, 8, 29, 29",
      /* 1163 */ "8, 8, 29, 29, 29, 29, 64, 64, 29, 8, 29, 29, 29, 29, 29, 29, 8, 64, 64, 64, 29, 64, 64, 64, 64, 64",
      /* 1189 */ "29, 29, 64, 64, 8, 8, 8, 29, 29, 29, 29, 29, 29, 29, 29, 1, 29, 29, 8, 8, 8, 8, 29, 8, 64, 64, 8, 8",
      /* 1217 */ "8, 29, 29, 29, 8, 8, 8, 8, 29, 29, 64, 8, 64, 64, 29, 64, 64, 64, 29, 64, 64, 8, 29, 29, 8, 8, 64",
      /* 1244 */ "64, 29, 29, 64, 64, 64, 29, 29, 29, 29, 64, 8, 29, 8, 29, 29, 29, 8, 8, 29, 29, 29, 8, 8, 29, 29, 64",
      /* 1271 */ "29, 64, 64, 64, 64, 29, 29, 29, 64, 64, 29, 29, 29, 29, 8, 8, 29, 8, 8, 29, 29, 29, 64, 29, 29, 8, 8",
      /* 1298 */ "8, 29, 8, 8, 8, 8, 8, 8, 8, 29, 8, 8, 29, 8, 8, 29, 29, 8, 8, 8, 8, 8, 8, 8, 8, 29, 8, 8, 8, 64, 64",
      /* 1329 */ "64, 64, 64, 64, 29, 64, 64, 8, 29, 29, 29, 29, 29, 64, 64, 29, 64, 29, 29, 29, 29, 29, 29, 29, 64",
      /* 1354 */ "64, 29, 29, 8, 8, 29, 8, 29, 8, 8, 8, 8, 29, 29, 8, 64, 8, 8, 64, 64, 64, 64, 64, 8, 8, 64, 8, 8, 8",
      /* 1383 */ "8, 8, 29, 64, 29, 29, 29, 29, 64, 64, 64, 64, 64, 64, 64, 64, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29",
      /* 1409 */ "29, 64, 29, 64, 8, 8, 8, 8, 8, 8, 8, 29, 29, 8, 8, 29, 29, 29, 29, 8, 29, 8, 29, 8, 29, 8, 29, 29, 8",
      /* 1438 */ "8, 29, 8, 29, 29, 8, 29, 29, 29, 8, 29, 29, 29, 29, 29, 8, 8, 8, 8, 8, 29, 8, 8, 8, 8, 8, 29, 29, 29",
      /* 1467 */ "29, 8, 65, 65, 29, 29, 29, 29, 29, 1, 1, 1, 1, 1, 1, 1, 1, 29, 64, 64, 29, 29, 64, 64, 29, 8, 8, 8",
      /* 1495 */ "29, 29, 8, 8, 29, 29, 8, 29, 8, 8, 29, 8, 8, 8, 1, 29, 29, 29, 29, 64, 29, 8, 8, 8, 8, 29, 8, 29, 8",
      /* 1524 */ "8, 8, 8, 8, 29, 64, 64, 64, 29, 8, 8, 8"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 1536; ++i) {MAP1[i] = Integer.parseInt(s2[i]);}
  }

  private static final int[] MAP2 = new int[12];
  static
  {
    final String s1[] =
    {
      /*  0 */ "57344, 65279, 65280, 65536, 65278, 65279, 65533, 1114111, 29, 1, 29, 29"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 12; ++i) {MAP2[i] = Integer.parseInt(s2[i]);}
  }

  private static final int[] INITIAL = new int[37];
  static
  {
    final String s1[] =
    {
      /*  0 */ "1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 1040, 17, 1042, 1043, 20, 21, 22, 23, 24, 25, 26",
      /* 26 */ "1051, 1052, 29, 30, 31, 32, 33, 34, 35, 36, 1061"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 37; ++i) {INITIAL[i] = Integer.parseInt(s2[i]);}
  }

  private static final int[] TRANSITION = new int[7456];
  static
  {
    final String s1[] =
    {
      /*    0 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*   17 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*   34 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*   51 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 4288, 4288, 4288, 4288",
      /*   68 */ "4298, 4432, 6773, 4312, 4325, 4563, 6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433",
      /*   85 */ "4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459",
      /*  102 */ "4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  119 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 4288, 4288, 4288, 4288, 4298, 4432, 6773, 4312",
      /*  136 */ "6639, 4563, 6640, 4433, 6774, 6061, 4443, 4433, 4418, 6775, 4339, 6639, 4433, 4352, 4370, 5084, 4432",
      /*  153 */ "4418, 4418, 4625, 4393, 4483, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555",
      /*  170 */ "4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  187 */ "6786, 6786, 6786, 6786, 6786, 4288, 4288, 4288, 4288, 4290, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  204 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  221 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  238 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  255 */ "6786, 4288, 4288, 4288, 4288, 4298, 4432, 6773, 4312, 6639, 4563, 6640, 4433, 6774, 6061, 4443, 4433",
      /*  272 */ "4418, 6775, 4503, 6639, 4433, 4352, 4370, 5084, 4432, 4418, 4418, 4625, 6786, 4483, 4445, 4412, 4560",
      /*  289 */ "4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786",
      /*  306 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6704",
      /*  323 */ "4522, 4526, 4432, 6773, 4312, 4325, 4563, 6640, 4419, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761",
      /*  340 */ "4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427",
      /*  357 */ "4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  374 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 5606, 4534, 5610, 4432, 6773",
      /*  391 */ "4312, 4549, 4563, 6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379",
      /*  408 */ "4432, 4418, 4418, 4625, 4636, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016",
      /*  425 */ "4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  442 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6787, 5035, 4432, 6773, 4312, 4325, 4563, 6640",
      /*  459 */ "4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625",
      /*  476 */ "4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467",
      /*  493 */ "4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  510 */ "6786, 6786, 6785, 5564, 5698, 6469, 4602, 6472, 6107, 4574, 5280, 6048, 5836, 6473, 6108, 6257, 6472",
      /*  527 */ "4840, 4840, 6109, 6299, 6497, 6473, 4399, 4840, 4585, 6472, 4840, 4840, 5936, 4393, 4593, 6468, 4840",
      /*  544 */ "6045, 4840, 6109, 5835, 6467, 4840, 6470, 6108, 7316, 4840, 7061, 6471, 7365, 5502, 6786, 6786, 6786",
      /*  561 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  578 */ "6786, 5579, 5519, 4432, 6773, 4312, 4325, 4563, 6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339",
      /*  595 */ "6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416",
      /*  612 */ "4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  629 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 5668, 7124, 4432",
      /*  646 */ "6773, 4312, 4325, 7387, 6640, 4433, 7166, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370",
      /*  663 */ "4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489",
      /*  680 */ "5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  697 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 5850, 4610, 5854, 4432, 6773, 4312, 4618, 4563",
      /*  714 */ "6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418",
      /*  731 */ "4625, 4633, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453",
      /*  748 */ "4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  765 */ "6786, 6786, 6786, 4644, 4645, 6217, 4654, 4658, 4432, 6773, 4312, 4325, 4563, 6640, 4433, 6774, 6257",
      /*  782 */ "4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445",
      /*  799 */ "4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786",
      /*  816 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 5783",
      /*  833 */ "4666, 5193, 5782, 5217, 4432, 6773, 4312, 4325, 4563, 6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775",
      /*  850 */ "4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566",
      /*  867 */ "7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  884 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 5909, 6552",
      /*  901 */ "4432, 6773, 4312, 4344, 4563, 6640, 4433, 6774, 4385, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352",
      /*  918 */ "4370, 4677, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441",
      /*  935 */ "4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /*  952 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6809, 4685, 4689, 4432, 6773, 4312, 4325",
      /*  969 */ "4359, 6640, 4433, 5048, 6257, 4443, 4433, 4418, 6775, 4339, 4697, 4433, 4352, 4370, 4379, 4432, 4418",
      /*  986 */ "4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362",
      /* 1003 */ "4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1020 */ "6786, 6786, 6786, 6786, 6786, 4705, 4722, 4721, 6616, 4432, 6773, 4312, 4325, 4563, 6640, 4433, 6774",
      /* 1037 */ "6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184",
      /* 1054 */ "4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786",
      /* 1071 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1088 */ "6786, 6786, 6903, 4730, 4734, 4432, 6773, 4312, 4325, 7382, 6640, 4433, 6774, 4742, 4443, 4433, 4418",
      /* 1105 */ "6775, 4339, 4697, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418",
      /* 1122 */ "4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786",
      /* 1139 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6075, 4750",
      /* 1156 */ "6079, 4432, 6773, 4312, 4758, 4563, 6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433",
      /* 1173 */ "4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459",
      /* 1190 */ "4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1207 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 4766, 4766, 4769, 4777, 4781, 4432, 6773, 4312",
      /* 1224 */ "4789, 4563, 6640, 4433, 6774, 4797, 4443, 4433, 4418, 6775, 4339, 7234, 4433, 4352, 4370, 4805, 4432",
      /* 1241 */ "4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555",
      /* 1258 */ "4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1275 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 7300, 4813, 7304, 6472, 6107, 4574, 4821, 6048, 5836, 6473",
      /* 1292 */ "6108, 6257, 4839, 4840, 4840, 6350, 6231, 4850, 6473, 4399, 4840, 4585, 4874, 4840, 4840, 6374, 4884",
      /* 1309 */ "4892, 6468, 4840, 6572, 4840, 6051, 4903, 6467, 4840, 4840, 5894, 4916, 4840, 5703, 6471, 4936, 4947",
      /* 1326 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1343 */ "6786, 6786, 6786, 4510, 4965, 4514, 6472, 6107, 4574, 4821, 6048, 5836, 6473, 6108, 6257, 4839, 4840",
      /* 1360 */ "4840, 6350, 6231, 4850, 6473, 4399, 4840, 4585, 4874, 4840, 4840, 6374, 4884, 4892, 6468, 4840, 6572",
      /* 1377 */ "4840, 6051, 4903, 6467, 4840, 4840, 5894, 4916, 4840, 5703, 6471, 4936, 4947, 6786, 6786, 6786, 6786",
      /* 1394 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 4510",
      /* 1411 */ "4965, 4514, 6472, 6107, 4574, 6236, 6048, 5836, 6473, 6108, 6257, 4839, 4840, 4840, 6350, 4577, 4973",
      /* 1428 */ "6473, 4399, 4840, 4585, 4874, 4840, 4840, 6374, 4884, 4892, 6468, 4840, 6572, 4840, 6051, 4903, 6467",
      /* 1445 */ "4840, 4840, 5894, 4916, 4840, 5703, 6471, 4936, 4947, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1462 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6167, 6786, 6786, 6167, 4831, 4432, 6773",
      /* 1479 */ "4312, 4325, 4563, 6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379",
      /* 1496 */ "4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016",
      /* 1513 */ "4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1530 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 5592, 4991, 5002, 5005, 4432, 6773, 4312, 4325, 4563, 6640",
      /* 1547 */ "4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625",
      /* 1564 */ "4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467",
      /* 1581 */ "4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1598 */ "6786, 6786, 6786, 6786, 6786, 6314, 7090, 4432, 6773, 4312, 4325, 4563, 5013, 4433, 6774, 5024, 4443",
      /* 1615 */ "4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412",
      /* 1632 */ "4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786",
      /* 1649 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1666 */ "5032, 6213, 5337, 4432, 6773, 4312, 4325, 4304, 5043, 4433, 4331, 5056, 4443, 4433, 4418, 6775, 4339",
      /* 1683 */ "6761, 4371, 5064, 4370, 5072, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 6767, 4418, 4566, 7416",
      /* 1700 */ "4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1717 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6662, 7340, 4432",
      /* 1734 */ "6773, 4312, 4325, 4563, 5080, 4433, 6774, 7286, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 5092, 4370",
      /* 1751 */ "5100, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489",
      /* 1768 */ "5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1785 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 7431, 4317, 4432, 6773, 4312, 4325, 4563",
      /* 1802 */ "6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418",
      /* 1819 */ "4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453",
      /* 1836 */ "4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1853 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 5035, 4432, 6773, 4312, 4325, 4563, 6640, 4433, 6774, 6257",
      /* 1870 */ "4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445",
      /* 1887 */ "4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786",
      /* 1904 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6785",
      /* 1921 */ "5564, 5698, 6469, 4602, 6472, 6107, 4574, 5280, 6048, 5836, 6473, 6108, 6257, 4839, 4840, 4840, 6350",
      /* 1938 */ "6299, 5108, 6473, 4399, 4840, 4585, 4874, 4840, 4840, 6374, 4884, 4593, 6468, 4840, 6572, 4840, 6051",
      /* 1955 */ "4903, 6467, 4840, 4840, 5894, 4916, 4840, 5703, 6471, 4936, 4947, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 1972 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6785, 5564, 5698, 6469, 4602",
      /* 1989 */ "6472, 6107, 4574, 5122, 6048, 5836, 6473, 6108, 6257, 4839, 4840, 4840, 6350, 7049, 5108, 6473, 4399",
      /* 2006 */ "4840, 4585, 4874, 4840, 4840, 6374, 4884, 4593, 6468, 4840, 6572, 4840, 6051, 4903, 6467, 4840, 4840",
      /* 2023 */ "5894, 4916, 4840, 5703, 6471, 4936, 4947, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2040 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6785, 5564, 5698, 6469, 4602, 6472, 6107, 4574, 5152",
      /* 2057 */ "6048, 5836, 6473, 6108, 6257, 6472, 4840, 4840, 6109, 6299, 6497, 6473, 4399, 4840, 4585, 6472, 4840",
      /* 2074 */ "4840, 5936, 4393, 4593, 6468, 4840, 6045, 4840, 6109, 5835, 6467, 4840, 6470, 6108, 7316, 4840, 7061",
      /* 2091 */ "6471, 7365, 5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2108 */ "6786, 6786, 6786, 6786, 6786, 6786, 6666, 5169, 5173, 4432, 6773, 4312, 5181, 4563, 6640, 4433, 6774",
      /* 2125 */ "6257, 4443, 4433, 4418, 6775, 4339, 6823, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184",
      /* 2142 */ "4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786",
      /* 2159 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2176 */ "5192, 5201, 5225, 5230, 5234, 6927, 5351, 5242, 5255, 5264, 5462, 6928, 5352, 6257, 5403, 5305, 5304",
      /* 2193 */ "5353, 5275, 6917, 6928, 5288, 5303, 5313, 6927, 5304, 5304, 5295, 5321, 5345, 6923, 5361, 5261, 5304",
      /* 2210 */ "5267, 5583, 5375, 5367, 5401, 5381, 5465, 5417, 5422, 5411, 5430, 5438, 6786, 6786, 6786, 6786, 6786",
      /* 2227 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 5446",
      /* 2244 */ "5247, 4432, 6773, 4312, 4325, 4563, 6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6271, 4433",
      /* 2261 */ "4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459",
      /* 2278 */ "4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2295 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 5458, 5736, 4432, 6773, 4312",
      /* 2312 */ "4325, 4563, 6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432",
      /* 2329 */ "4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555",
      /* 2346 */ "4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2363 */ "6786, 6786, 6786, 6786, 6786, 6785, 5564, 5698, 6469, 4602, 5917, 6403, 5473, 6984, 5507, 5913, 5496",
      /* 2380 */ "5515, 6257, 5527, 4840, 4840, 6350, 6299, 5108, 6473, 4399, 4840, 4585, 5539, 6457, 4840, 5488, 5547",
      /* 2397 */ "4593, 6507, 4840, 5555, 4840, 5572, 4903, 5161, 4840, 4840, 5894, 4916, 4840, 5703, 6471, 4936, 4947",
      /* 2414 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2431 */ "6786, 5591, 5600, 5618, 5623, 5627, 6472, 6107, 4574, 5280, 6048, 5836, 6473, 6108, 6257, 5635, 4840",
      /* 2448 */ "4840, 6350, 6299, 5108, 5636, 5387, 4840, 5644, 4874, 4840, 4840, 6374, 4884, 4593, 6468, 4840, 6572",
      /* 2465 */ "4840, 6051, 4903, 6467, 4840, 4840, 5894, 4916, 4840, 5703, 6471, 4936, 4947, 6786, 6786, 6786, 6786",
      /* 2482 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 5652, 5661, 5680",
      /* 2499 */ "5685, 5693, 6472, 6107, 4574, 5280, 6048, 5836, 6473, 6108, 6257, 4839, 4840, 4840, 6350, 6299, 5108",
      /* 2516 */ "6473, 4399, 4840, 4585, 5711, 5747, 4840, 4957, 4884, 5726, 6468, 4840, 6572, 4840, 6051, 4903, 6467",
      /* 2533 */ "5744, 4840, 5894, 4916, 4840, 5144, 5755, 4936, 4947, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2550 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 5765, 5775, 5791, 5797, 5805, 6472, 6107",
      /* 2567 */ "4574, 5280, 6048, 5836, 6473, 6108, 6257, 4839, 4840, 4840, 6350, 6299, 5108, 6473, 4399, 4840, 4585",
      /* 2584 */ "4874, 4840, 4842, 6374, 4884, 4593, 4862, 4840, 6572, 4840, 6051, 4903, 6467, 4840, 5757, 5894, 4916",
      /* 2601 */ "4840, 5703, 6471, 4936, 4947, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2618 */ "6786, 6786, 6786, 6786, 6786, 6786, 5833, 5844, 5862, 5867, 5875, 6363, 5893, 5902, 5925, 4952, 5672",
      /* 2635 */ "6042, 5933, 6257, 5944, 4840, 4840, 6350, 7049, 5108, 6473, 7180, 4840, 4585, 5954, 5965, 6584, 6374",
      /* 2652 */ "5977, 4593, 5985, 4840, 5997, 4840, 5114, 4903, 6467, 7073, 4840, 5894, 6014, 4840, 5703, 5881, 6036",
      /* 2669 */ "4947, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2686 */ "6786, 6786, 6059, 6069, 6087, 6092, 6097, 4401, 6107, 4574, 5280, 6048, 5836, 6105, 6108, 6257, 6117",
      /* 2703 */ "4840, 4840, 6350, 6126, 5108, 6118, 5207, 4840, 6139, 4874, 4840, 4840, 6374, 4884, 4593, 6468, 4840",
      /* 2720 */ "6572, 4840, 6051, 4903, 6467, 4840, 4840, 5825, 6147, 4840, 5703, 6471, 4936, 6160, 6786, 6786, 6786",
      /* 2737 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6785, 6006",
      /* 2754 */ "5810, 6469, 4602, 6472, 6107, 4574, 5280, 6048, 5836, 6473, 6108, 6257, 6472, 4840, 4840, 6109, 6299",
      /* 2771 */ "6497, 6473, 4399, 4840, 4585, 6472, 4840, 4840, 5936, 4393, 4593, 6468, 4840, 6045, 6179, 6109, 5835",
      /* 2788 */ "6467, 6189, 6398, 6108, 7316, 6198, 7061, 6471, 7365, 5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2805 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6785, 5564, 5698, 6469, 4602, 6472",
      /* 2822 */ "5393, 4574, 5280, 6207, 5836, 6799, 6108, 6257, 6472, 4840, 4840, 6109, 6299, 6497, 6473, 4399, 4840",
      /* 2839 */ "4585, 6472, 4840, 4840, 6225, 4393, 4593, 6468, 6190, 6045, 4840, 5731, 5835, 6467, 4840, 6470, 6108",
      /* 2856 */ "7316, 6244, 7061, 6471, 7365, 5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2873 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6255, 6265, 6279, 6284, 6288, 7148, 4983, 6296, 6131, 6048",
      /* 2890 */ "5836, 6307, 7120, 6257, 6472, 4840, 6028, 6326, 6299, 6497, 6655, 4399, 6681, 4585, 6472, 4840, 4840",
      /* 2907 */ "5936, 4393, 4593, 6468, 4840, 6045, 6339, 6109, 5835, 4928, 4840, 6470, 6349, 6358, 4840, 7061, 6471",
      /* 2924 */ "7365, 5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 2941 */ "6786, 6786, 6786, 6785, 5564, 5698, 6469, 4602, 6472, 6107, 4574, 5280, 6048, 5836, 6473, 6108, 6257",
      /* 2958 */ "6472, 4840, 4840, 6109, 6299, 6497, 6473, 4399, 4840, 4585, 6472, 4840, 4840, 5936, 4393, 4593, 6468",
      /* 2975 */ "4840, 5715, 4840, 6109, 5835, 7271, 4840, 6470, 6108, 7316, 4840, 7061, 6471, 7365, 5502, 6786, 6786",
      /* 2992 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6785",
      /* 3009 */ "5564, 5698, 6469, 4602, 4713, 6107, 4574, 5280, 6048, 4709, 6473, 6371, 6257, 7220, 5158, 6382, 6109",
      /* 3026 */ "6299, 6718, 6393, 5327, 4840, 6411, 6472, 4841, 4875, 6688, 4393, 6419, 6468, 6181, 6045, 6341, 7107",
      /* 3043 */ "5835, 6467, 4840, 6439, 7184, 6447, 6385, 7061, 6471, 7365, 5502, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3060 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6785, 5564, 5698, 6469, 4602",
      /* 3077 */ "4866, 6107, 4574, 5280, 6048, 5836, 6465, 6108, 6257, 6472, 4840, 4840, 6109, 6299, 6497, 6473, 4399",
      /* 3094 */ "4840, 4585, 6472, 5955, 4840, 5936, 4393, 4593, 6468, 4840, 6045, 4840, 6109, 5835, 6467, 4840, 6470",
      /* 3111 */ "6108, 7316, 4840, 7061, 6471, 7365, 5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3128 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6481, 6491, 6515, 6520, 6524, 4921, 6107, 6532, 5280",
      /* 3145 */ "6048, 7144, 4926, 5213, 6257, 6019, 6431, 4840, 7261, 6299, 6604, 6473, 5479, 4840, 6540, 6472, 4840",
      /* 3162 */ "4840, 5936, 4393, 4593, 6468, 4840, 6045, 6247, 6109, 5835, 6467, 4840, 6503, 6548, 6560, 6568, 7061",
      /* 3179 */ "6471, 7216, 5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3196 */ "6786, 6786, 6786, 6786, 6785, 5564, 5698, 6469, 4602, 6580, 5484, 4574, 6865, 5718, 6171, 6592, 6612",
      /* 3213 */ "6257, 6472, 4840, 4840, 6109, 6299, 6497, 6473, 4399, 4840, 4585, 6472, 6624, 6628, 5936, 4393, 4593",
      /* 3230 */ "6468, 4979, 6045, 4840, 6636, 5835, 5128, 4840, 6470, 6108, 7316, 4840, 6648, 6471, 7365, 6598, 6786",
      /* 3247 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3264 */ "6785, 5564, 5698, 6469, 4602, 6472, 6107, 4574, 5280, 6048, 5836, 6473, 6108, 6257, 6472, 6674, 5139",
      /* 3281 */ "6109, 6299, 6497, 4895, 4399, 4840, 4585, 6472, 4840, 4840, 5936, 4393, 4593, 6468, 4840, 6045, 4840",
      /* 3298 */ "6109, 5835, 6467, 4840, 6470, 6108, 7316, 4840, 7061, 6471, 7365, 5502, 6786, 6786, 6786, 6786, 6786",
      /* 3315 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6702, 6712, 6726, 6731",
      /* 3332 */ "6735, 6743, 5561, 4574, 5280, 6755, 6318, 6877, 6108, 6257, 6472, 4403, 4596, 4827, 6299, 6497, 4599",
      /* 3349 */ "4495, 4856, 4585, 6472, 4840, 4840, 5936, 4393, 4593, 6468, 4840, 6045, 7030, 6783, 5835, 6467, 6795",
      /* 3366 */ "6470, 6108, 7316, 4840, 7061, 6471, 7448, 5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3383 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6807, 6817, 6831, 6836, 6840, 6472, 6107, 4574",
      /* 3400 */ "5280, 6048, 5836, 6473, 6108, 6257, 6848, 5957, 4840, 6109, 6860, 6497, 6473, 6873, 4840, 6885, 6152",
      /* 3417 */ "4840, 6024, 5936, 4393, 6893, 5885, 4840, 6045, 4840, 6109, 5835, 6467, 4840, 6470, 6108, 7316, 4840",
      /* 3434 */ "7061, 6471, 7365, 5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3451 */ "6786, 6786, 6786, 6786, 6786, 6901, 6911, 6936, 6942, 6946, 6472, 6107, 4574, 5280, 6048, 5836, 6473",
      /* 3468 */ "6108, 6257, 6954, 6967, 7026, 7138, 6979, 6497, 6473, 4399, 6992, 7000, 4908, 5946, 6453, 5936, 4393",
      /* 3485 */ "4593, 7012, 7008, 5531, 6852, 6109, 6483, 7020, 4840, 6470, 6108, 7316, 4840, 5815, 4939, 7365, 5502",
      /* 3502 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3519 */ "6786, 6785, 5564, 5698, 6469, 4602, 7038, 7059, 7046, 4541, 6048, 5836, 7057, 7086, 6257, 6472, 7069",
      /* 3536 */ "4876, 6109, 6694, 6497, 6959, 4399, 6958, 4585, 5989, 4840, 7081, 5936, 4393, 4593, 5969, 7098, 6045",
      /* 3553 */ "7115, 6109, 7132, 6467, 6199, 6470, 6108, 7316, 4875, 7061, 6426, 7365, 5502, 6786, 6786, 6786, 6786",
      /* 3570 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 7156, 7174, 7192",
      /* 3587 */ "7198, 7202, 6472, 6107, 4574, 5280, 6048, 5836, 6473, 6108, 6257, 6472, 4840, 4840, 6109, 6299, 6497",
      /* 3604 */ "6473, 4399, 4840, 4585, 6472, 4840, 4840, 5936, 4393, 4593, 6468, 4840, 6045, 4840, 6109, 5835, 6467",
      /* 3621 */ "4840, 6470, 6108, 7316, 4840, 7061, 6471, 7365, 5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3638 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 7210, 7228, 7242, 7247, 7251, 6472, 7259",
      /* 3655 */ "4574, 5280, 6048, 5836, 7103, 6108, 6257, 6472, 4840, 5956, 6109, 6299, 6497, 7269, 4399, 4840, 4585",
      /* 3672 */ "6472, 4840, 4840, 5936, 4393, 4593, 6468, 4840, 6045, 4840, 6109, 5835, 6467, 5134, 6470, 6108, 7316",
      /* 3689 */ "4840, 7061, 6471, 7365, 5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3706 */ "6786, 6786, 6786, 6786, 6786, 6786, 6785, 5564, 5698, 6469, 4602, 5820, 6107, 4574, 5152, 6048, 5836",
      /* 3723 */ "6473, 5333, 6257, 6472, 4840, 4840, 6109, 7279, 6497, 6473, 4399, 4840, 4585, 6472, 4840, 4840, 5936",
      /* 3740 */ "4393, 4593, 6468, 4840, 6045, 4840, 6109, 5835, 6467, 4840, 6470, 6108, 7316, 4840, 7061, 6471, 7365",
      /* 3757 */ "5502, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3774 */ "6786, 6786, 6785, 5564, 5698, 6469, 4602, 6472, 6003, 4574, 5280, 7294, 5836, 6747, 6108, 6257, 6472",
      /* 3791 */ "4840, 4842, 6109, 6299, 6497, 4404, 4399, 6971, 4585, 6472, 4840, 4840, 5936, 4393, 4593, 6468, 4840",
      /* 3808 */ "6045, 4840, 6109, 5835, 6467, 4840, 6470, 6108, 7316, 4840, 7312, 7368, 7365, 5502, 6786, 6786, 6786",
      /* 3825 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 4669, 6786",
      /* 3842 */ "7435, 7324, 7328, 4432, 6773, 4312, 4325, 4563, 6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339",
      /* 3859 */ "6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416",
      /* 3876 */ "4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3893 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 7336, 6331, 4432",
      /* 3910 */ "6773, 4312, 4325, 4563, 7161, 4433, 6774, 5450, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370",
      /* 3927 */ "4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489",
      /* 3944 */ "5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 3961 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 5653, 7348, 7360, 7352, 7376, 4432, 6773, 4312, 4325, 4563",
      /* 3978 */ "6640, 4433, 6774, 6257, 4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418",
      /* 3995 */ "4625, 4393, 5184, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453",
      /* 4012 */ "4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 4029 */ "6786, 6786, 6786, 6786, 6786, 4994, 7395, 7399, 4432, 6773, 4312, 4325, 4563, 6640, 4433, 6774, 6257",
      /* 4046 */ "4443, 4433, 4418, 6775, 4339, 6761, 4433, 4352, 4370, 4379, 4432, 4418, 4418, 4625, 4393, 5184, 4445",
      /* 4063 */ "4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786",
      /* 4080 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 4097 */ "6786, 6786, 6786, 5767, 6472, 6107, 4574, 5280, 6048, 5836, 6473, 6108, 6257, 6472, 4840, 4840, 6109",
      /* 4114 */ "6299, 6497, 6473, 4399, 4840, 4585, 6472, 4840, 4840, 5936, 4393, 4593, 6468, 4840, 6045, 4840, 6109",
      /* 4131 */ "5835, 6467, 4840, 6470, 6108, 7316, 4840, 7061, 6471, 7365, 5502, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 4148 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 4288, 4288, 4288, 4288, 4298",
      /* 4165 */ "4432, 6773, 4312, 6639, 4563, 6640, 4433, 6774, 6061, 4443, 4433, 4418, 6775, 4339, 6639, 4433, 4352",
      /* 4182 */ "4370, 5084, 4432, 4418, 4418, 4625, 6786, 4483, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441",
      /* 4199 */ "4489, 5016, 4555, 4362, 4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 4216 */ "6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 4646, 7413, 7407, 7424, 4432, 6773, 4312, 6639",
      /* 4233 */ "4563, 6640, 4433, 6774, 6786, 4443, 4433, 4418, 6775, 7443, 6639, 4433, 4352, 4370, 5047, 4432, 4418",
      /* 4250 */ "4418, 4625, 6786, 4483, 4445, 4412, 4560, 4418, 4566, 7416, 4427, 4459, 4441, 4489, 5016, 4555, 4362",
      /* 4267 */ "4453, 4467, 4475, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786, 6786",
      /* 4284 */ "6786, 6786, 6786, 6786, 7206, 7206, 7206, 7206, 7206, 7206, 7206, 7206, 0, 0, 7206, 7206, 7206, 7206",
      /* 4302 */ "7206, 7206, 68096, 0, 0, 68096, 68096, 68096, 8872, 0, 0, 69632, 0, 69632, 69632, 0, 0, 0, 0, 27136",
      /* 4322 */ "0, 68096, 0, 65, 66, 0, 0, 0, 156, 68096, 68096, 68096, 68096, 10240, 11776, 13824, 15360, 69632",
      /* 4340 */ "69632, 0, 0, 102400, 65, 66, 0, 0, 0, 111, 68096, 68096, 0, 0, 102400, 68096, 68096, 68096, 98816",
      /* 4359 */ "68096, 14848, 0, 68096, 68096, 68096, 0, 0, 0, 68096, 98304, 101888, 68096, 68096, 68096, 68096",
      /* 4375 */ "68096, 68096, 68096, 9216, 1721, 68096, 68096, 68096, 68096, 0, 0, 111, 0, 0, 0, 0, 192, 7792, 0, 0",
      /* 4395 */ "65, 0, 0, 66, 0, 0, 0, 39, 39, 39, 39, 39, 119, 39, 39, 39, 0, 100352, 68096, 68096, 68096, 106496",
      /* 4417 */ "107520, 68096, 68096, 68096, 68096, 68096, 68096, 68096, 68096, 1721, 105472, 69120, 68096, 68096",
      /* 4431 */ "108032, 0, 68096, 68096, 68096, 68096, 68096, 68096, 68096, 0, 99328, 68096, 0, 68096, 68096, 68096",
      /* 4447 */ "68096, 68096, 0, 68096, 68096, 68096, 107008, 0, 68096, 68096, 103936, 104960, 68096, 68096, 68096",
      /* 4462 */ "68096, 68096, 109056, 95744, 68096, 99840, 97792, 0, 68096, 0, 68096, 68096, 97792, 99840, 0, 100864",
      /* 4478 */ "0, 96768, 97280, 0, 102912, 0, 0, 0, 68096, 68096, 103424, 68096, 68096, 68096, 68096, 108544, 96256",
      /* 4495 */ "0, 0, 0, 39, 39, 39, 39, 249, 69632, 69632, 0, 0, 102400, 227, 230, 0, 0, 0, 5701, 5701, 5701, 5701",
      /* 4517 */ "5701, 0, 0, 39, 0, 8192, 8192, 8192, 79, 8192, 8192, 8192, 8192, 79, 0, 68096, 0, 65, 65, 65, 0, 65",
      /* 4539 */ "65, 65, 65, 66, 0, 0, 0, 156, 160, 162, 4608, 66, 0, 0, 0, 156, 68096, 68096, 68096, 105984, 108544",
      /* 4560 */ "68096, 68096, 68096, 68096, 0, 0, 68096, 68096, 68096, 0, 0, 0, 0, 95744, 0, 58, 0, 58, 58, 0, 0, 0",
      /* 4582 */ "0, 0, 5784, 1721, 39, 39, 39, 39, 0, 0, 111, 0, 5354, 157, 39, 39, 39, 39, 39, 213, 39, 39, 39, 39",
      /* 4606 */ "0, 0, 39, 0, 66, 66, 66, 0, 66, 66, 66, 66, 65, 4608, 0, 0, 0, 156, 68096, 68096, 107520, 0, 0, 0, 0",
      /* 4631 */ "101376, 104448, 0, 0, 65, 0, 0, 4608, 0, 0, 66, 0, 0, 0, 12288, 0, 0, 0, 0, 0, 0, 0, 2048, 12288",
      /* 4655 */ "12288, 12288, 0, 12288, 12288, 12288, 12288, 12288, 0, 68096, 0, 0, 12800, 12800, 0, 0, 0, 0, 0, 0",
      /* 4675 */ "44032, 0, 1721, 68096, 68096, 68096, 68096, 0, 0, 192, 14410, 14410, 14410, 14336, 14410, 14410",
      /* 4691 */ "14410, 14410, 14440, 0, 68096, 0, 0, 233, 0, 156, 157, 156, 68096, 68096, 0, 0, 15872, 15872, 0, 0",
      /* 4711 */ "0, 0, 0, 39, 116, 39, 120, 39, 39, 39, 0, 15872, 0, 15872, 0, 0, 0, 0, 0, 16459, 16459, 16459, 16384",
      /* 4734 */ "16459, 16459, 16459, 16459, 16489, 0, 68096, 0, 16896, 0, 0, 0, 0, 0, 111, 7792, 67, 67, 67, 0, 67",
      /* 4755 */ "67, 67, 67, 65, 66, 0, 5784, 5784, 156, 68096, 68096, 40, 40, 40, 40, 40, 40, 40, 40, 70, 70, 70, 70",
      /* 4778 */ "70, 70, 18472, 70, 70, 70, 70, 18538, 0, 68096, 7792, 65, 66, 0, 0, 0, 7792, 68096, 68096, 0, 7792",
      /* 4799 */ "0, 0, 0, 0, 111, 7792, 1721, 68096, 68096, 68096, 68096, 0, 0, 7680, 5700, 5700, 5700, 0, 5700, 5700",
      /* 4819 */ "5700, 5700, 65, 66, 5784, 6809, 5701, 156, 39, 39, 39, 218, 0, 0, 0, 0, 19456, 0, 68096, 0, 193, 39",
      /* 4841 */ "39, 39, 39, 39, 39, 39, 39, 119, 39, 6809, 5865, 6299, 156, 157, 156, 39, 39, 39, 252, 39, 254, 39",
      /* 4863 */ "39, 39, 293, 0, 39, 39, 39, 39, 39, 127, 39, 262, 39, 39, 39, 39, 39, 39, 39, 195, 39, 0, 0, 65, 282",
      /* 4888 */ "283, 66, 284, 285, 5865, 5354, 157, 39, 39, 39, 39, 39, 243, 39, 0, 0, 229, 65, 232, 66, 0, 39, 39",
      /* 4911 */ "39, 39, 265, 39, 39, 0, 342, 39, 39, 39, 0, 39, 39, 39, 121, 39, 128, 39, 39, 39, 39, 39, 0, 39, 328",
      /* 4936 */ "0, 0, 362, 39, 0, 39, 39, 39, 39, 360, 39, 39, 5354, 39, 0, 39, 39, 0, 0, 165, 39, 39, 0, 0, 277, 0",
      /* 4962 */ "279, 58, 58, 5701, 5701, 5701, 0, 5701, 5701, 5701, 5701, 0, 5865, 6299, 156, 157, 156, 39, 39, 39",
      /* 4982 */ "300, 39, 39, 39, 39, 139, 0, 0, 0, 19968, 19968, 19968, 0, 0, 0, 0, 0, 0, 46592, 46592, 19968, 0",
      /* 5004 */ "19968, 19968, 19968, 19968, 19968, 19968, 0, 68096, 0, 20992, 0, 0, 0, 0, 68096, 68096, 68096, 0",
      /* 5022 */ "95232, 68096, 0, 0, 21182, 0, 0, 0, 111, 7792, 22528, 0, 22528, 0, 0, 0, 0, 0, 0, 68096, 0, 22016",
      /* 5044 */ "23209, 24576, 0, 0, 68096, 68096, 68096, 68096, 0, 0, 0, 14848, 17408, 18944, 22016, 24576, 29184",
      /* 5061 */ "45056, 111, 7792, 23552, 0, 102400, 68096, 68096, 68096, 98816, 68096, 1721, 68096, 68096, 68096",
      /* 5076 */ "68096, 21504, 25600, 111, 0, 0, 25258, 0, 0, 68096, 68096, 68096, 68096, 0, 0, 111, 0, 26112, 102400",
      /* 5095 */ "68096, 68096, 68096, 98816, 68096, 1721, 68096, 68096, 68096, 68096, 0, 26373, 111, 0, 0, 6299, 156",
      /* 5112 */ "157, 156, 39, 39, 39, 317, 0, 0, 320, 0, 65, 66, 0, 154, 154, 156, 39, 39, 39, 325, 39, 0, 39, 39",
      /* 5136 */ "39, 332, 39, 39, 39, 39, 210, 39, 39, 39, 214, 0, 0, 355, 39, 39, 65, 66, 0, 155, 0, 156, 39, 39",
      /* 5160 */ "120, 39, 39, 39, 39, 39, 326, 39, 39, 27648, 27648, 27648, 0, 27648, 27648, 27648, 27648, 27648, 0",
      /* 5179 */ "68096, 0, 65, 66, 0, 0, 0, 157, 68096, 68096, 103424, 68096, 68096, 41, 0, 0, 0, 0, 0, 0, 0, 12800",
      /* 5201 */ "41, 41, 0, 0, 0, 59, 0, 0, 0, 39, 39, 200, 39, 39, 121, 39, 0, 0, 0, 0, 12800, 0, 68096, 0, 0, 41, 0",
      /* 5228 */ "59, 59, 41, 41, 41, 0, 41, 41, 41, 41, 0, 0, 68137, 0, 0, 69691, 0, 69691, 69691, 0, 0, 0, 0, 28160",
      /* 5252 */ "0, 68096, 0, 150, 151, 0, 0, 0, 158, 68137, 68137, 68137, 68137, 0, 0, 68137, 68137, 68137, 0, 0, 0",
      /* 5273 */ "0, 95744, 69691, 69691, 0, 0, 102400, 65, 66, 0, 0, 0, 156, 39, 39, 0, 0, 102400, 68137, 68137",
      /* 5293 */ "68137, 98857, 68137, 107520, 0, 0, 0, 0, 101435, 104507, 101929, 68137, 68137, 68137, 68137, 68137",
      /* 5309 */ "68137, 68137, 68137, 41, 1721, 68137, 68137, 68137, 68137, 0, 0, 111, 0, 0, 150, 0, 0, 151, 0, 0, 0",
      /* 5330 */ "39, 39, 242, 39, 39, 122, 39, 0, 0, 0, 0, 22610, 0, 68096, 0, 0, 286, 157, 68137, 68137, 103465",
      /* 5351 */ "68137, 68137, 68137, 68137, 68137, 0, 0, 0, 0, 0, 100393, 68137, 68137, 68137, 106537, 107561, 68137",
      /* 5368 */ "68137, 68137, 68137, 68137, 109097, 95785, 68137, 105513, 69161, 68137, 68137, 108073, 0, 68137",
      /* 5382 */ "68137, 68137, 68137, 108544, 96256, 0, 0, 0, 39, 39, 248, 39, 39, 134, 39, 138, 0, 141, 0, 99369",
      /* 5402 */ "68137, 0, 68137, 68137, 68137, 68137, 68137, 41, 68137, 107049, 0, 68137, 68137, 103977, 105001",
      /* 5417 */ "68137, 68137, 68137, 106025, 108585, 68137, 68137, 68137, 0, 0, 0, 68137, 98345, 99840, 97792, 0",
      /* 5433 */ "68137, 0, 68137, 68137, 97833, 99881, 0, 100905, 0, 96809, 97321, 0, 102912, 0, 28160, 0, 28160, 0",
      /* 5451 */ "0, 0, 0, 0, 45568, 111, 7792, 0, 0, 0, 28672, 0, 0, 0, 0, 0, 68137, 68137, 68137, 0, 95273, 68137, 0",
      /* 5474 */ "58, 0, 58, 58, 147, 0, 0, 0, 39, 247, 39, 39, 39, 137, 39, 0, 0, 0, 278, 279, 58, 58, 159, 39, 39",
      /* 5499 */ "39, 39, 182, 39, 0, 39, 0, 39, 39, 0, 0, 39, 39, 125, 0, 0, 186, 39, 39, 159, 0, 0, 0, 0, 9829, 0",
      /* 5525 */ "68096, 0, 193, 39, 39, 197, 39, 39, 39, 39, 0, 0, 39, 119, 262, 263, 39, 39, 39, 39, 39, 267, 0, 281",
      /* 5549 */ "65, 282, 283, 66, 284, 285, 39, 304, 39, 306, 0, 307, 39, 39, 135, 39, 39, 0, 0, 0, 58, 0, 0, 314",
      /* 5573 */ "39, 39, 0, 0, 0, 320, 0, 0, 0, 9728, 0, 0, 0, 0, 0, 0, 68137, 68649, 42, 0, 0, 0, 0, 0, 0, 0, 19968",
      /* 5600 */ "42, 42, 0, 0, 0, 58, 0, 0, 0, 65, 65, 65, 65, 65, 0, 0, 68096, 0, 0, 42, 0, 58, 58, 42, 42, 42, 0",
      /* 5627 */ "86, 86, 86, 86, 0, 0, 39, 0, 193, 39, 39, 39, 39, 199, 39, 39, 0, 1721, 39, 248, 39, 39, 0, 0, 111",
      /* 5652 */ "43, 0, 0, 0, 0, 0, 0, 0, 46080, 43, 43, 0, 0, 56, 58, 62, 0, 0, 0, 10832, 0, 0, 0, 0, 0, 39, 39, 174",
      /* 5680 */ "0, 43, 0, 58, 58, 43, 43, 43, 0, 87, 87, 87, 87, 97, 97, 97, 97, 0, 0, 39, 0, 58, 58, 39, 39, 39, 0",
      /* 5707 */ "0, 355, 39, 39, 262, 39, 39, 217, 39, 39, 39, 39, 0, 0, 119, 39, 167, 0, 0, 0, 5354, 157, 39, 287",
      /* 5731 */ "39, 39, 39, 0, 318, 0, 0, 0, 0, 28781, 0, 68096, 0, 39, 39, 331, 39, 39, 39, 39, 39, 272, 39, 39, 39",
      /* 5756 */ "357, 39, 39, 39, 39, 39, 39, 212, 39, 44, 0, 0, 0, 0, 0, 0, 0, 39, 0, 44, 44, 0, 0, 0, 58, 63, 0, 0",
      /* 5784 */ "0, 12800, 0, 0, 0, 0, 0, 0, 44, 0, 58, 58, 44, 76, 76, 76, 0, 88, 88, 88, 88, 88, 98, 98, 98, 0, 0",
      /* 5811 */ "39, 0, 60, 60, 39, 39, 39, 0, 354, 0, 39, 39, 39, 122, 39, 39, 39, 119, 0, 0, 0, 58, 45, 0, 0, 0, 0",
      /* 5838 */ "0, 0, 0, 39, 39, 39, 45, 45, 0, 0, 0, 58, 0, 0, 0, 66, 66, 66, 66, 66, 0, 0, 68096, 0, 0, 45, 0, 58",
      /* 5866 */ "58, 45, 45, 45, 0, 45, 45, 45, 45, 45, 45, 99, 99, 0, 0, 39, 0, 39, 359, 39, 39, 39, 39, 0, 39, 264",
      /* 5892 */ "39, 131, 39, 39, 39, 39, 0, 0, 0, 58, 0, 58, 0, 145, 146, 0, 148, 0, 0, 0, 13312, 0, 0, 0, 0, 0, 39",
      /* 5919 */ "115, 39, 39, 123, 39, 39, 65, 66, 0, 154, 154, 156, 39, 161, 39, 187, 39, 39, 0, 0, 0, 0, 0, 58, 58",
      /* 5944 */ "193, 194, 39, 39, 39, 39, 39, 39, 273, 39, 262, 39, 119, 39, 39, 39, 39, 39, 39, 39, 207, 268, 39",
      /* 5967 */ "39, 270, 39, 39, 39, 39, 0, 39, 39, 297, 280, 0, 65, 282, 283, 66, 284, 285, 290, 291, 292, 39, 0",
      /* 5990 */ "39, 39, 39, 39, 39, 266, 39, 303, 39, 305, 39, 0, 307, 39, 39, 136, 39, 39, 0, 0, 0, 60, 0, 0, 0",
      /* 6015 */ "342, 39, 344, 39, 0, 39, 39, 39, 198, 39, 39, 39, 119, 39, 39, 39, 39, 211, 212, 39, 39, 0, 0, 362",
      /* 6039 */ "39, 364, 365, 39, 39, 178, 39, 39, 39, 39, 0, 0, 39, 39, 39, 0, 0, 0, 320, 0, 46, 0, 0, 0, 0, 0, 0",
      /* 6066 */ "0, 111, 0, 46, 46, 0, 0, 57, 58, 0, 0, 0, 67, 67, 67, 67, 67, 17920, 0, 68096, 0, 0, 46, 0, 58, 58",
      /* 6092 */ "71, 71, 71, 0, 89, 89, 89, 89, 100, 0, 0, 39, 0, 39, 177, 39, 39, 39, 39, 39, 0, 0, 0, 0, 0, 193, 39",
      /* 6119 */ "39, 39, 39, 200, 39, 39, 0, 58, 58, 0, 226, 0, 65, 66, 0, 0, 0, 156, 124, 39, 1721, 39, 258, 39, 39",
      /* 6144 */ "0, 0, 111, 0, 342, 39, 39, 345, 0, 39, 39, 39, 212, 264, 39, 39, 39, 5354, 39, 0, 39, 39, 368, 0, 0",
      /* 6169 */ "0, 19456, 0, 0, 0, 0, 0, 39, 173, 175, 39, 309, 39, 39, 39, 39, 39, 39, 301, 39, 329, 39, 39, 39, 39",
      /* 6194 */ "39, 39, 39, 302, 348, 39, 39, 39, 39, 39, 39, 39, 334, 163, 0, 0, 39, 163, 39, 0, 0, 0, 82, 0, 0, 0",
      /* 6220 */ "0, 0, 12288, 12288, 12288, 119, 0, 0, 0, 0, 0, 58, 58, 0, 0, 0, 65, 66, 5784, 0, 5701, 156, 39, 39",
      /* 6244 */ "39, 39, 350, 39, 39, 39, 39, 39, 311, 39, 39, 47, 0, 0, 0, 0, 0, 0, 0, 111, 7792, 47, 47, 0, 0, 0",
      /* 6270 */ "58, 0, 0, 0, 156, 156, 156, 68096, 68096, 64, 47, 0, 58, 58, 47, 47, 47, 84, 90, 90, 90, 90, 84, 0",
      /* 6294 */ "39, 0, 143, 58, 0, 58, 58, 0, 0, 0, 65, 66, 0, 124, 39, 39, 39, 39, 39, 184, 0, 0, 0, 20561, 0, 0, 0",
      /* 6321 */ "0, 0, 172, 39, 39, 215, 216, 39, 0, 219, 0, 0, 0, 0, 44654, 0, 68096, 0, 308, 39, 39, 39, 39, 39, 39",
      /* 6346 */ "39, 312, 39, 338, 39, 39, 39, 0, 0, 0, 0, 222, 0, 0, 343, 39, 39, 0, 39, 39, 118, 39, 39, 39, 129",
      /* 6371 */ "116, 39, 188, 39, 0, 0, 0, 0, 279, 58, 58, 39, 208, 39, 39, 39, 39, 39, 39, 351, 352, 39, 39, 239",
      /* 6395 */ "39, 39, 242, 39, 39, 0, 39, 210, 39, 39, 39, 125, 39, 140, 0, 142, 1721, 39, 242, 39, 39, 0, 0, 111",
      /* 6419 */ "0, 5354, 157, 39, 39, 39, 288, 39, 0, 358, 39, 39, 39, 39, 39, 205, 39, 206, 39, 39, 39, 335, 0, 39",
      /* 6443 */ "39, 264, 39, 337, 341, 0, 39, 39, 39, 0, 39, 39, 196, 39, 39, 39, 39, 39, 271, 39, 39, 39, 39, 127",
      /* 6467 */ "39, 39, 39, 39, 39, 0, 39, 39, 39, 39, 39, 39, 39, 0, 48, 0, 0, 0, 0, 0, 0, 0, 323, 39, 48, 48, 0, 0",
      /* 6495 */ "0, 58, 0, 0, 0, 156, 157, 156, 39, 39, 0, 336, 39, 39, 39, 39, 0, 295, 39, 39, 0, 48, 0, 58, 58, 72",
      /* 6521 */ "72, 72, 0, 72, 72, 72, 72, 0, 0, 39, 0, 0, 58, 0, 58, 58, 0, 0, 149, 1721, 39, 39, 39, 260, 0, 0",
      /* 6547 */ "111, 39, 339, 119, 39, 0, 0, 0, 0, 13415, 0, 68096, 111, 0, 0, 39, 39, 39, 346, 39, 347, 39, 349, 39",
      /* 6571 */ "39, 39, 39, 39, 39, 0, 307, 39, 39, 0, 39, 117, 119, 39, 125, 39, 39, 39, 275, 39, 39, 176, 39, 39",
      /* 6595 */ "39, 39, 167, 39, 0, 39, 367, 39, 39, 0, 0, 0, 156, 157, 156, 39, 237, 173, 175, 39, 176, 0, 0, 0, 0",
      /* 6620 */ "15872, 0, 68096, 0, 39, 39, 269, 39, 39, 39, 39, 39, 129, 39, 39, 39, 39, 39, 316, 0, 0, 0, 0, 0, 0",
      /* 6645 */ "68096, 68096, 68096, 39, 315, 39, 0, 0, 0, 356, 39, 39, 240, 39, 39, 39, 244, 0, 0, 0, 24147, 0, 0",
      /* 6668 */ "0, 0, 0, 27648, 27648, 27648, 39, 203, 39, 39, 39, 39, 203, 39, 39, 251, 240, 39, 39, 255, 39, 0",
      /* 6690 */ "276, 0, 0, 0, 58, 58, 0, 0, 0, 228, 231, 0, 49, 0, 0, 0, 0, 0, 0, 0, 8192, 8192, 49, 49, 0, 0, 0, 58",
      /* 6718 */ "0, 0, 0, 156, 157, 156, 236, 39, 0, 49, 0, 58, 58, 49, 49, 49, 0, 91, 91, 91, 91, 0, 0, 39, 0, 0",
      /* 6744 */ "114, 39, 39, 39, 39, 39, 39, 166, 39, 39, 0, 164, 0, 0, 39, 164, 39, 0, 0, 0, 156, 157, 156, 68096",
      /* 6768 */ "68096, 68096, 68096, 26624, 0, 68096, 68096, 68096, 68096, 68096, 0, 0, 0, 0, 0, 39, 315, 39, 0, 0",
      /* 6788 */ "0, 0, 0, 0, 0, 0, 96, 39, 330, 39, 39, 39, 39, 39, 39, 180, 39, 183, 0, 50, 0, 0, 0, 0, 0, 0, 0",
      /* 6815 */ "14410, 14410, 50, 50, 0, 0, 0, 61, 0, 0, 0, 157, 157, 156, 68096, 68096, 0, 50, 0, 61, 61, 50, 50",
      /* 6838 */ "50, 0, 92, 92, 92, 92, 0, 0, 39, 0, 0, 39, 195, 39, 39, 39, 39, 195, 39, 39, 39, 313, 58, 58, 225, 0",
      /* 6864 */ "0, 65, 66, 0, 0, 0, 156, 125, 39, 0, 0, 245, 39, 39, 39, 39, 39, 181, 39, 39, 0, 1721, 257, 39, 259",
      /* 6889 */ "39, 0, 0, 111, 0, 5354, 157, 241, 39, 39, 39, 289, 51, 0, 0, 0, 0, 0, 0, 0, 16459, 16459, 51, 51, 0",
      /* 6914 */ "0, 0, 58, 0, 0, 0, 158, 235, 156, 68137, 68137, 68137, 68137, 0, 68137, 68137, 68137, 68137, 68137",
      /* 6933 */ "68137, 68137, 0, 0, 51, 0, 58, 58, 73, 77, 77, 77, 0, 93, 93, 93, 93, 0, 0, 39, 0, 0, 39, 196, 39",
      /* 6958 */ "39, 39, 39, 39, 241, 39, 39, 39, 0, 39, 204, 39, 39, 39, 39, 39, 39, 253, 39, 39, 39, 223, 224, 0, 0",
      /* 6983 */ "0, 65, 66, 0, 0, 0, 156, 159, 39, 39, 250, 39, 39, 39, 39, 39, 256, 1721, 196, 39, 39, 39, 0, 0, 111",
      /* 7008 */ "39, 39, 299, 39, 39, 39, 39, 39, 294, 39, 296, 39, 39, 39, 324, 39, 39, 0, 39, 39, 209, 39, 39, 39",
      /* 7032 */ "39, 39, 264, 39, 39, 39, 113, 39, 39, 39, 39, 126, 39, 130, 0, 58, 144, 58, 58, 0, 0, 0, 65, 66, 154",
      /* 7057 */ "160, 39, 39, 132, 39, 39, 39, 0, 0, 0, 39, 39, 202, 39, 39, 39, 39, 39, 39, 39, 333, 39, 39, 39, 274",
      /* 7082 */ "39, 39, 39, 39, 39, 39, 39, 160, 0, 0, 0, 0, 20587, 0, 68096, 0, 39, 298, 39, 39, 39, 39, 39, 39",
      /* 7106 */ "179, 39, 39, 39, 0, 0, 319, 0, 0, 39, 39, 310, 39, 39, 39, 39, 39, 189, 0, 0, 0, 0, 10854, 0, 68096",
      /* 7131 */ "0, 321, 0, 0, 0, 0, 322, 39, 39, 217, 0, 0, 220, 0, 0, 0, 171, 0, 39, 39, 39, 39, 124, 39, 39, 52, 0",
      /* 7158 */ "0, 0, 54, 0, 0, 0, 0, 45568, 68096, 68096, 68096, 68096, 0, 11264, 0, 0, 52, 52, 0, 0, 0, 58, 0, 0",
      /* 7182 */ "0, 246, 39, 39, 39, 39, 0, 0, 340, 0, 0, 52, 0, 58, 58, 52, 78, 78, 78, 0, 94, 94, 94, 94, 0, 0, 39",
      /* 7209 */ "0, 53, 0, 0, 0, 0, 55, 0, 0, 0, 363, 0, 39, 39, 39, 39, 201, 39, 39, 53, 53, 0, 0, 0, 58, 0, 0, 0",
      /* 7237 */ "5354, 157, 156, 68096, 68096, 0, 53, 0, 58, 58, 53, 53, 53, 0, 95, 95, 95, 95, 0, 0, 39, 0, 39, 133",
      /* 7261 */ "39, 39, 39, 0, 0, 0, 221, 0, 238, 39, 39, 39, 39, 39, 39, 0, 327, 39, 58, 58, 0, 0, 0, 229, 232, 0",
      /* 7287 */ "0, 0, 25279, 0, 0, 111, 7792, 136, 0, 0, 39, 166, 39, 0, 0, 0, 5700, 5700, 5700, 5700, 5700, 0, 0",
      /* 7310 */ "39, 0, 119, 39, 39, 353, 0, 0, 39, 39, 39, 0, 39, 39, 44032, 44032, 44032, 0, 44032, 44032, 44032",
      /* 7331 */ "44032, 0, 0, 68096, 0, 0, 0, 0, 44629, 0, 0, 0, 0, 24172, 0, 68096, 0, 0, 0, 0, 46080, 0, 0, 46080",
      /* 7355 */ "46080, 0, 0, 46080, 0, 0, 46080, 46080, 0, 46080, 0, 0, 0, 39, 0, 39, 39, 39, 39, 39, 361, 46080",
      /* 7377 */ "46080, 46080, 46080, 46080, 0, 68096, 0, 16896, 68096, 68096, 68096, 0, 0, 68096, 68096, 68096, 0",
      /* 7394 */ "11264, 46592, 46592, 46592, 0, 46592, 46592, 46592, 46592, 0, 0, 68096, 0, 0, 0, 2048, 2048, 0, 2048",
      /* 7413 */ "0, 2048, 2048, 0, 0, 0, 0, 0, 0, 68096, 68608, 0, 0, 2048, 2048, 2048, 0, 68096, 0, 0, 0, 27136, 0",
      /* 7436 */ "0, 0, 0, 0, 44032, 44032, 44032, 69632, 69632, 0, 0, 102400, 0, 0, 0, 39, 0, 39, 366, 39"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 7456; ++i) {TRANSITION[i] = Integer.parseInt(s2[i]);}
  }

  private static final int[] EXPECTED = new int[625];
  static
  {
    final String s1[] =
    {
      /*   0 */ "276, 279, 286, 282, 289, 292, 296, 300, 304, 308, 321, 321, 321, 324, 411, 414, 471, 312, 316, 331",
      /*  20 */ "325, 320, 335, 364, 336, 340, 605, 371, 321, 321, 321, 321, 321, 321, 322, 432, 413, 470, 344, 354",
      /*  40 */ "359, 349, 403, 321, 321, 335, 348, 370, 321, 321, 321, 321, 321, 321, 324, 411, 421, 423, 353, 358",
      /*  60 */ "335, 320, 321, 321, 321, 320, 321, 321, 322, 433, 422, 621, 363, 319, 321, 321, 321, 321, 321, 432",
      /*  80 */ "463, 319, 321, 321, 322, 463, 319, 321, 462, 320, 368, 323, 446, 325, 325, 375, 379, 386, 390, 394",
      /* 100 */ "398, 402, 325, 325, 325, 517, 325, 440, 325, 325, 529, 325, 408, 504, 325, 325, 419, 325, 427, 431",
      /* 120 */ "325, 325, 325, 325, 325, 325, 325, 437, 439, 325, 325, 325, 325, 445, 450, 460, 325, 325, 467, 541",
      /* 140 */ "325, 325, 325, 325, 325, 325, 517, 325, 441, 325, 325, 325, 444, 452, 325, 325, 441, 510, 325, 325",
      /* 160 */ "325, 438, 552, 325, 443, 503, 455, 439, 325, 325, 325, 437, 442, 503, 455, 551, 325, 442, 523, 550",
      /* 180 */ "551, 524, 325, 454, 325, 475, 502, 479, 483, 487, 489, 493, 497, 501, 325, 325, 325, 508, 614, 572",
      /* 200 */ "325, 598, 516, 521, 511, 528, 533, 538, 382, 325, 325, 548, 325, 325, 325, 325, 325, 325, 553, 557",
      /* 220 */ "571, 414, 325, 403, 577, 587, 512, 607, 534, 563, 326, 559, 325, 325, 325, 325, 325, 325, 508, 569",
      /* 240 */ "325, 325, 325, 576, 586, 592, 581, 563, 327, 325, 325, 325, 456, 558, 325, 404, 585, 591, 544, 565",
      /* 260 */ "596, 325, 325, 557, 404, 602, 544, 557, 415, 404, 602, 611, 597, 592, 618, 421, 24592, 8413184",
      /* 278 */ "16801792, 24576, 24576, 24576, 24576, 24704, 24576, 24584, 8413200, 16801808, 1090543616, 1073766400",
      /* 290 */ "24600, 1073766408, 32128, 8421232, -1736376464, -1719599248, -1736376464, -662634640, -1736376456",
      /* 299 */ "-885563384, -1736376464, -1736376456, -1736376464, -1736376452, -1736376464, -1736376464, -1736376456",
      /* 307 */ "-1736376456, -40952, 8192, 16, 16384, 1024, 16896, 80, 48, 80, 268435456, 0, 16, 0, 16, 16, 16, 16, 0",
      /* 326 */ "0, 0, 0, 2, 4, 80, 16, 196608, 2097152, 80, 16, 16, 16, 80, 524288, 6291456, 67108864, 805306368",
      /* 344 */ "4096, 1024, 2048, 512, 4, 16, 16, 16, 131072, 1024, 512, 512, 80, 16, 16, 16, 48, 16, 80, 16, 80, 16",
      /* 366 */ "16, 4, 16, 512, 16, 0, 0, 16384, 16384, 134217728, 0, 1140850688, 64, 2112, 64, 2112, 0, 1, 2, 28, 0",
      /* 387 */ "2097152, -2145386495, -2145386495, -2145386431, -2141192191, -2145386431, 14022120, -1306525631",
      /* 395 */ "-1306525631, -1306525631, -1306525631, -1239416767, -165674943, -165674943, -165674943, 33554431, 0",
      /* 404 */ "0, 0, 16, 0, 1280, 12288, 360448, 0, 128, 128, 128, 0, 0, 0, 4, 335544320, -536870912, 0, 0, 256, 256",
      /* 425 */ "256, 256, 3, 16, 1792, 1015808, 16777216, 0, 0, 0, 128, 0, 0, 134217728, 0, 0, 67108864, 1073741824",
      /* 443 */ "0, 0, -2147483648, 0, 0, 0, 32, 8192, 262144, 0, 33554432, 268435456, 536870912, 0, 0, 0, 524288",
      /* 460 */ "268435456, -1610612736, 0, 0, 512, 16, 16, 0, 67108864, -536870912, 0, 256, 256, 1024, 7168, 131072",
      /* 476 */ "524288, 2097152, 33554432, 4, 2080, 33554432, 33554432, 128, 33554432, 33554432, 2080, 33556512",
      /* 488 */ "2105872, 69542416, 69542416, 103096848, 54526336, 71300697, 71300697, 104855129, 71300697, 104855129",
      /* 498 */ "104855129, 104855131, 104855135, 62914944, 0, 0, 0, 33554432, 268435456, -1610612736, 0, 131072",
      /* 510 */ "524288, 0, 0, 0, 384, 0, 8192, 0, 0, 0, 134217728, 73728, 262144, 0, 0, 268435456, 536870912",
      /* 527 */ "1073741824, 16777216, 0, 0, 1, -2147483648, 24, 64, 1024, 4096, 24576, 122880, 393216, 1572864, 0",
      /* 542 */ "512, 917504, 0, 1024, 4096, 16384, 0, 25165824, 0, 0, 1073741824, 0, 0, 0, 131072, 524288, 0, 4, 0, 0",
      /* 562 */ "0, 32768, 131072, 524288, 1048576, 0, 0, 4, 0, 32, 2048, 0, 0, 128, 16, 512, 0, 8192, 0, 8, 1024",
      /* 583 */ "4096, 16384, 8192, 0, 0, 65536, 262144, 0, 262144, 256, 0, 0, 0, 2, 4, 0, 0, 16, 512, 65536, 256, 0",
      /* 605 */ "0, 16384, 0, 0, 1, 8, 1024, 4096, 0, 4, 0, 0, 32, 4, 0, 16, 256, 512, 16, 48"
    };
    String[] s2 = java.util.Arrays.toString(s1).replaceAll("[ \\[\\]]", "").split(",");
    for (int i = 0; i < 625; ++i) {EXPECTED[i] = Integer.parseInt(s2[i]);}
  }

  private static final String[] TOKEN =
  {
    "(0)",
    "END",
    "Shebang",
    "EOF",
    "Identifier",
    "'null'",
    "BooleanLiteral",
    "IdentifierName",
    "StringLiteral",
    "RegularExpressionLiteral",
    "DecimalLiteral",
    "HexIntegerLiteral",
    "OctalIntegerLiteral",
    "WhiteSpace",
    "Comment",
    "'!'",
    "'!='",
    "'!=='",
    "'%'",
    "'%='",
    "'&'",
    "'&&'",
    "'&='",
    "'('",
    "')'",
    "'*'",
    "'*='",
    "'+'",
    "'++'",
    "'+='",
    "','",
    "'-'",
    "'--'",
    "'-='",
    "'.'",
    "'/'",
    "'/='",
    "':'",
    "';'",
    "'<'",
    "'<<'",
    "'<<='",
    "'<='",
    "'='",
    "'=='",
    "'==='",
    "'>'",
    "'>='",
    "'>>'",
    "'>>='",
    "'>>>'",
    "'>>>='",
    "'?'",
    "'['",
    "']'",
    "'^'",
    "'^='",
    "'break'",
    "'case'",
    "'catch'",
    "'continue'",
    "'debugger'",
    "'default'",
    "'delete'",
    "'do'",
    "'else'",
    "'finally'",
    "'for'",
    "'function'",
    "'get'",
    "'if'",
    "'in'",
    "'instanceof'",
    "'new'",
    "'return'",
    "'set'",
    "'switch'",
    "'this'",
    "'throw'",
    "'try'",
    "'typeof'",
    "'var'",
    "'void'",
    "'while'",
    "'with'",
    "'{'",
    "'|'",
    "'|='",
    "'||'",
    "'}'",
    "'~'"
  };
}

// End
