// This file was generated on Fri Sep 16, 2022 19:34 (UTC+02) by REx v5.55 which is Copyright (c) 1979-2022 by Gunther Rademacher <grd@gmx.net>
// REx command line: file.ebnf -tree -java -basex -name expkg-zone58.text.parse.Parse-REx

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

public class Parse_REx
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

  public static ANode parseGrammar(Str str) throws IOException
  {
    BaseXFunction baseXFunction = new BaseXFunction()
    {
      @Override
      public void execute(Parse_REx p) {p.parse_Grammar();}
    };
    return baseXFunction.call(str);
  }

  public static abstract class BaseXFunction
  {
    protected abstract void execute(Parse_REx p);

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
      Parse_REx parser = new Parse_REx();
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

  public Parse_REx()
  {
  }

  public Parse_REx(CharSequence string, EventHandler t)
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
    l2 = 0; b2 = 0; e2 = 0;
    l3 = 0; b3 = 0; e3 = 0;
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

  public void parse_Grammar()
  {
    eventHandler.startNonterminal("Grammar", e0);
    lookahead1W(11);                // Whitespace | Name | '<?'
    whitespace();
    parse_Prolog();
    whitespace();
    parse_SyntaxDefinition();
    if (l1 == 29)                   // '<?TOKENS?>'
    {
      whitespace();
      parse_LexicalDefinition();
    }
    if (l1 == 28)                   // '<?ENCORE?>'
    {
      whitespace();
      parse_Encore();
    }
    consume(11);                    // EOF
    eventHandler.endNonterminal("Grammar", e0);
  }

  private void parse_Prolog()
  {
    eventHandler.startNonterminal("Prolog", e0);
    for (;;)
    {
      lookahead1W(11);              // Whitespace | Name | '<?'
      if (l1 != 27)                 // '<?'
      {
        break;
      }
      whitespace();
      parse_ProcessingInstruction();
    }
    eventHandler.endNonterminal("Prolog", e0);
  }

  private void parse_ProcessingInstruction()
  {
    eventHandler.startNonterminal("ProcessingInstruction", e0);
    consume(27);                    // '<?'
    lookahead1(0);                  // Name
    consume(2);                     // Name
    lookahead1(8);                  // Space | '?>'
    if (l1 == 3)                    // Space
    {
      for (;;)
      {
        consume(3);                 // Space
        lookahead1(17);             // Space | DirPIContents | '?>'
        if (l1 != 3)                // Space
        {
          break;
        }
      }
      if (l1 == 4)                  // DirPIContents
      {
        consume(4);                 // DirPIContents
      }
    }
    lookahead1(2);                  // '?>'
    consume(33);                    // '?>'
    eventHandler.endNonterminal("ProcessingInstruction", e0);
  }

  private void parse_SyntaxDefinition()
  {
    eventHandler.startNonterminal("SyntaxDefinition", e0);
    for (;;)
    {
      whitespace();
      parse_SyntaxProduction();
      if (l1 != 2)                  // Name
      {
        break;
      }
    }
    eventHandler.endNonterminal("SyntaxDefinition", e0);
  }

  private void parse_SyntaxProduction()
  {
    eventHandler.startNonterminal("SyntaxProduction", e0);
    consume(2);                     // Name
    lookahead1W(4);                 // Whitespace | '::='
    consume(25);                    // '::='
    lookahead1W(34);                // Whitespace | Name | StringLiteral | EOF | '(' | '/' | '/*' | '<?' |
                                    // '<?ENCORE?>' | '<?TOKENS?>' | '|'
    whitespace();
    parse_SyntaxChoice();
    for (;;)
    {
      lookahead1W(21);              // Whitespace | Name | EOF | '/*' | '<?ENCORE?>' | '<?TOKENS?>'
      if (l1 != 23)                 // '/*'
      {
        break;
      }
      whitespace();
      parse_Option();
    }
    eventHandler.endNonterminal("SyntaxProduction", e0);
  }

  private void parse_SyntaxChoice()
  {
    eventHandler.startNonterminal("SyntaxChoice", e0);
    parse_SyntaxSequence();
    if (l1 == 22                    // '/'
     || l1 == 41)                   // '|'
    {
      switch (l1)
      {
      case 41:                      // '|'
        for (;;)
        {
          consume(41);              // '|'
          lookahead1W(33);          // Whitespace | Name | StringLiteral | EOF | '(' | ')' | '/*' | '<?' |
                                    // '<?ENCORE?>' | '<?TOKENS?>' | '|'
          whitespace();
          parse_SyntaxSequence();
          if (l1 != 41)             // '|'
          {
            break;
          }
        }
        break;
      default:
        for (;;)
        {
          consume(22);              // '/'
          lookahead1W(32);          // Whitespace | Name | StringLiteral | EOF | '(' | ')' | '/' | '/*' | '<?' |
                                    // '<?ENCORE?>' | '<?TOKENS?>'
          whitespace();
          parse_SyntaxSequence();
          if (l1 != 22)             // '/'
          {
            break;
          }
        }
      }
    }
    eventHandler.endNonterminal("SyntaxChoice", e0);
  }

  private void parse_SyntaxSequence()
  {
    eventHandler.startNonterminal("SyntaxSequence", e0);
    for (;;)
    {
      lookahead1W(35);              // Whitespace | Name | StringLiteral | EOF | '(' | ')' | '/' | '/*' | '<?' |
                                    // '<?ENCORE?>' | '<?TOKENS?>' | '|'
      switch (l1)
      {
      case 2:                       // Name
        lookahead2W(40);            // Whitespace | Name | StringLiteral | CaretName | EOF | '(' | ')' | '*' | '+' |
                                    // '/' | '/*' | '::=' | '<?' | '<?ENCORE?>' | '<?TOKENS?>' | '?' | '|'
        break;
      default:
        lk = l1;
      }
      if (lk == 11                  // EOF
       || lk == 16                  // ')'
       || lk == 22                  // '/'
       || lk == 23                  // '/*'
       || lk == 28                  // '<?ENCORE?>'
       || lk == 29                  // '<?TOKENS?>'
       || lk == 41                  // '|'
       || lk == 1602)               // Name '::='
      {
        break;
      }
      whitespace();
      parse_SyntaxItem();
    }
    eventHandler.endNonterminal("SyntaxSequence", e0);
  }

  private void parse_SyntaxItem()
  {
    eventHandler.startNonterminal("SyntaxItem", e0);
    parse_SyntaxPrimary();
    lookahead1W(38);                // Whitespace | Name | StringLiteral | EOF | '(' | ')' | '*' | '+' | '/' | '/*' |
                                    // '<?' | '<?ENCORE?>' | '<?TOKENS?>' | '?' | '|'
    if (l1 == 17                    // '*'
     || l1 == 19                    // '+'
     || l1 == 32)                   // '?'
    {
      switch (l1)
      {
      case 32:                      // '?'
        consume(32);                // '?'
        break;
      case 17:                      // '*'
        consume(17);                // '*'
        break;
      default:
        consume(19);                // '+'
      }
    }
    eventHandler.endNonterminal("SyntaxItem", e0);
  }

  private void parse_SyntaxPrimary()
  {
    eventHandler.startNonterminal("SyntaxPrimary", e0);
    switch (l1)
    {
    case 15:                        // '('
      consume(15);                  // '('
      lookahead1W(25);              // Whitespace | Name | StringLiteral | '(' | ')' | '/' | '<?' | '|'
      whitespace();
      parse_SyntaxChoice();
      consume(16);                  // ')'
      break;
    case 27:                        // '<?'
      parse_ProcessingInstruction();
      break;
    default:
      parse_NameOrString();
    }
    eventHandler.endNonterminal("SyntaxPrimary", e0);
  }

  private void parse_LexicalDefinition()
  {
    eventHandler.startNonterminal("LexicalDefinition", e0);
    consume(29);                    // '<?TOKENS?>'
    for (;;)
    {
      lookahead1W(22);              // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' |
                                    // '<?ENCORE?>'
      if (l1 == 11                  // EOF
       || l1 == 28)                 // '<?ENCORE?>'
      {
        break;
      }
      switch (l1)
      {
      case 2:                       // Name
        lookahead2W(23);            // Whitespace | CaretName | '::=' | '<<' | '>>' | '?' | '\\'
        break;
      default:
        lk = l1;
      }
      switch (lk)
      {
      case 21:                      // '.'
      case 1602:                    // Name '::='
      case 2050:                    // Name '?'
        whitespace();
        parse_LexicalProduction();
        break;
      case 2306:                    // Name '\\'
        whitespace();
        parse_Delimiter();
        break;
      case 12:                      // EquivalenceLookAhead
        whitespace();
        parse_Equivalence();
        break;
      default:
        whitespace();
        parse_Preference();
      }
    }
    eventHandler.endNonterminal("LexicalDefinition", e0);
  }

  private void parse_LexicalProduction()
  {
    eventHandler.startNonterminal("LexicalProduction", e0);
    switch (l1)
    {
    case 2:                         // Name
      consume(2);                   // Name
      break;
    default:
      consume(21);                  // '.'
    }
    lookahead1W(15);                // Whitespace | '::=' | '?'
    if (l1 == 32)                   // '?'
    {
      consume(32);                  // '?'
    }
    lookahead1W(4);                 // Whitespace | '::='
    consume(25);                    // '::='
    lookahead1W(37);                // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | '.' | '/*' | '<?ENCORE?>' | '[' | '[^' | '|'
    whitespace();
    parse_ContextChoice();
    for (;;)
    {
      lookahead1W(24);              // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' | '/*' |
                                    // '<?ENCORE?>'
      if (l1 != 23)                 // '/*'
      {
        break;
      }
      whitespace();
      parse_Option();
    }
    eventHandler.endNonterminal("LexicalProduction", e0);
  }

  private void parse_ContextChoice()
  {
    eventHandler.startNonterminal("ContextChoice", e0);
    parse_ContextExpression();
    for (;;)
    {
      lookahead1W(27);              // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' | '/*' |
                                    // '<?ENCORE?>' | '|'
      if (l1 != 41)                 // '|'
      {
        break;
      }
      consume(41);                  // '|'
      lookahead1W(37);              // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | '.' | '/*' | '<?ENCORE?>' | '[' | '[^' | '|'
      whitespace();
      parse_ContextExpression();
    }
    eventHandler.endNonterminal("ContextChoice", e0);
  }

  private void parse_LexicalChoice()
  {
    eventHandler.startNonterminal("LexicalChoice", e0);
    parse_LexicalSequence();
    for (;;)
    {
      lookahead1W(14);              // Whitespace | ')' | '|'
      if (l1 != 41)                 // '|'
      {
        break;
      }
      consume(41);                  // '|'
      lookahead1W(31);              // Whitespace | Name | StringLiteral | CharCode | '$' | '(' | ')' | '.' | '[' |
                                    // '[^' | '|'
      whitespace();
      parse_LexicalSequence();
    }
    eventHandler.endNonterminal("LexicalChoice", e0);
  }

  private void parse_ContextExpression()
  {
    eventHandler.startNonterminal("ContextExpression", e0);
    parse_LexicalSequence();
    lookahead1W(30);                // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '&' | '.' |
                                    // '/*' | '<?ENCORE?>' | '|'
    if (l1 == 14)                   // '&'
    {
      consume(14);                  // '&'
      lookahead1W(26);              // Whitespace | Name | StringLiteral | CharCode | '$' | '(' | '.' | '[' | '[^'
      whitespace();
      parse_LexicalItem();
    }
    eventHandler.endNonterminal("ContextExpression", e0);
  }

  private void parse_LexicalSequence()
  {
    eventHandler.startNonterminal("LexicalSequence", e0);
    switch (l1)
    {
    case 2:                         // Name
      lookahead2W(51);              // Whitespace | Name | StringLiteral | CaretName | CharCode | EOF |
                                    // EquivalenceLookAhead | '$' | '&' | '(' | ')' | '*' | '+' | '-' | '.' | '/*' |
                                    // '::=' | '<<' | '<?ENCORE?>' | '>>' | '?' | '[' | '[^' | '\\' | '|'
      switch (lk)
      {
      case 2050:                    // Name '?'
        lookahead3W(43);            // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | ')' | '-' | '.' | '/*' | '::=' | '<?ENCORE?>' | '[' | '[^' | '|'
        break;
      }
      break;
    case 5:                         // StringLiteral
      lookahead2W(49);              // Whitespace | Name | StringLiteral | CaretName | CharCode | EOF |
                                    // EquivalenceLookAhead | '$' | '&' | '(' | ')' | '*' | '+' | '-' | '.' | '/*' |
                                    // '<<' | '<?ENCORE?>' | '>>' | '?' | '[' | '[^' | '|'
      break;
    case 21:                        // '.'
      lookahead2W(47);              // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | ')' | '*' | '+' | '-' | '.' | '/*' | '::=' | '<?ENCORE?>' | '?' |
                                    // '[' | '[^' | '|'
      switch (lk)
      {
      case 2069:                    // '.' '?'
        lookahead3W(43);            // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | ')' | '-' | '.' | '/*' | '::=' | '<?ENCORE?>' | '[' | '[^' | '|'
        break;
      }
      break;
    default:
      lk = l1;
    }
    switch (lk)
    {
    case 11:                        // EOF
    case 12:                        // EquivalenceLookAhead
    case 14:                        // '&'
    case 16:                        // ')'
    case 23:                        // '/*'
    case 28:                        // '<?ENCORE?>'
    case 41:                        // '|'
    case 386:                       // Name CaretName
    case 389:                       // StringLiteral CaretName
    case 1602:                      // Name '::='
    case 1621:                      // '.' '::='
    case 1666:                      // Name '<<'
    case 1669:                      // StringLiteral '<<'
    case 1986:                      // Name '>>'
    case 1989:                      // StringLiteral '>>'
    case 2306:                      // Name '\\'
    case 104450:                    // Name '?' '::='
    case 104469:                    // '.' '?' '::='
      break;
    default:
      parse_LexicalItem();
      lookahead1W(41);              // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | ')' | '-' | '.' | '/*' | '<?ENCORE?>' | '[' | '[^' | '|'
      switch (l1)
      {
      case 20:                      // '-'
        consume(20);                // '-'
        lookahead1W(26);            // Whitespace | Name | StringLiteral | CharCode | '$' | '(' | '.' | '[' | '[^'
        whitespace();
        parse_LexicalItem();
        break;
      default:
        for (;;)
        {
          lookahead1W(39);          // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | ')' | '.' | '/*' | '<?ENCORE?>' | '[' | '[^' | '|'
          switch (l1)
          {
          case 2:                   // Name
            lookahead2W(50);        // Whitespace | Name | StringLiteral | CaretName | CharCode | EOF |
                                    // EquivalenceLookAhead | '$' | '&' | '(' | ')' | '*' | '+' | '.' | '/*' | '::=' |
                                    // '<<' | '<?ENCORE?>' | '>>' | '?' | '[' | '[^' | '\\' | '|'
            switch (lk)
            {
            case 2050:              // Name '?'
              lookahead3W(42);      // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | ')' | '.' | '/*' | '::=' | '<?ENCORE?>' | '[' | '[^' | '|'
              break;
            }
            break;
          case 5:                   // StringLiteral
            lookahead2W(48);        // Whitespace | Name | StringLiteral | CaretName | CharCode | EOF |
                                    // EquivalenceLookAhead | '$' | '&' | '(' | ')' | '*' | '+' | '.' | '/*' | '<<' |
                                    // '<?ENCORE?>' | '>>' | '?' | '[' | '[^' | '|'
            break;
          case 21:                  // '.'
            lookahead2W(46);        // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | ')' | '*' | '+' | '.' | '/*' | '::=' | '<?ENCORE?>' | '?' | '[' |
                                    // '[^' | '|'
            switch (lk)
            {
            case 2069:              // '.' '?'
              lookahead3W(42);      // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | ')' | '.' | '/*' | '::=' | '<?ENCORE?>' | '[' | '[^' | '|'
              break;
            }
            break;
          default:
            lk = l1;
          }
          if (lk == 11              // EOF
           || lk == 12              // EquivalenceLookAhead
           || lk == 14              // '&'
           || lk == 16              // ')'
           || lk == 23              // '/*'
           || lk == 28              // '<?ENCORE?>'
           || lk == 41              // '|'
           || lk == 386             // Name CaretName
           || lk == 389             // StringLiteral CaretName
           || lk == 1602            // Name '::='
           || lk == 1621            // '.' '::='
           || lk == 1666            // Name '<<'
           || lk == 1669            // StringLiteral '<<'
           || lk == 1986            // Name '>>'
           || lk == 1989            // StringLiteral '>>'
           || lk == 2306            // Name '\\'
           || lk == 104450          // Name '?' '::='
           || lk == 104469)         // '.' '?' '::='
          {
            break;
          }
          whitespace();
          parse_LexicalItem();
        }
      }
    }
    eventHandler.endNonterminal("LexicalSequence", e0);
  }

  private void parse_LexicalItem()
  {
    eventHandler.startNonterminal("LexicalItem", e0);
    parse_LexicalPrimary();
    lookahead1W(45);                // Whitespace | Name | StringLiteral | CharCode | EOF | EquivalenceLookAhead | '$' |
                                    // '&' | '(' | ')' | '*' | '+' | '-' | '.' | '/*' | '<?ENCORE?>' | '?' | '[' |
                                    // '[^' | '|'
    if (l1 == 17                    // '*'
     || l1 == 19                    // '+'
     || l1 == 32)                   // '?'
    {
      switch (l1)
      {
      case 32:                      // '?'
        consume(32);                // '?'
        break;
      case 17:                      // '*'
        consume(17);                // '*'
        break;
      default:
        consume(19);                // '+'
      }
    }
    eventHandler.endNonterminal("LexicalItem", e0);
  }

  private void parse_LexicalPrimary()
  {
    eventHandler.startNonterminal("LexicalPrimary", e0);
    switch (l1)
    {
    case 2:                         // Name
    case 21:                        // '.'
      switch (l1)
      {
      case 2:                       // Name
        consume(2);                 // Name
        break;
      default:
        consume(21);                // '.'
      }
      break;
    case 5:                         // StringLiteral
      consume(5);                   // StringLiteral
      break;
    case 15:                        // '('
      consume(15);                  // '('
      lookahead1W(31);              // Whitespace | Name | StringLiteral | CharCode | '$' | '(' | ')' | '.' | '[' |
                                    // '[^' | '|'
      whitespace();
      parse_LexicalChoice();
      consume(16);                  // ')'
      break;
    case 13:                        // '$'
      consume(13);                  // '$'
      break;
    case 7:                         // CharCode
      consume(7);                   // CharCode
      break;
    default:
      parse_CharClass();
    }
    eventHandler.endNonterminal("LexicalPrimary", e0);
  }

  private void parse_NameOrString()
  {
    eventHandler.startNonterminal("NameOrString", e0);
    switch (l1)
    {
    case 2:                         // Name
      consume(2);                   // Name
      lookahead1W(44);              // Whitespace | Name | StringLiteral | CaretName | EOF | EquivalenceLookAhead |
                                    // '(' | ')' | '*' | '+' | '.' | '/' | '/*' | '<<' | '<?' | '<?ENCORE?>' |
                                    // '<?TOKENS?>' | '>>' | '?' | '|'
      if (l1 == 6)                  // CaretName
      {
        whitespace();
        parse_Context();
      }
      break;
    default:
      consume(5);                   // StringLiteral
      lookahead1W(44);              // Whitespace | Name | StringLiteral | CaretName | EOF | EquivalenceLookAhead |
                                    // '(' | ')' | '*' | '+' | '.' | '/' | '/*' | '<<' | '<?' | '<?ENCORE?>' |
                                    // '<?TOKENS?>' | '>>' | '?' | '|'
      if (l1 == 6)                  // CaretName
      {
        whitespace();
        parse_Context();
      }
    }
    eventHandler.endNonterminal("NameOrString", e0);
  }

  private void parse_Context()
  {
    eventHandler.startNonterminal("Context", e0);
    consume(6);                     // CaretName
    eventHandler.endNonterminal("Context", e0);
  }

  private void parse_CharClass()
  {
    eventHandler.startNonterminal("CharClass", e0);
    switch (l1)
    {
    case 34:                        // '['
      consume(34);                  // '['
      break;
    default:
      consume(35);                  // '[^'
    }
    for (;;)
    {
      lookahead1(19);               // CharCode | Char | CharRange | CharCodeRange
      switch (l1)
      {
      case 8:                       // Char
        consume(8);                 // Char
        break;
      case 7:                       // CharCode
        consume(7);                 // CharCode
        break;
      case 9:                       // CharRange
        consume(9);                 // CharRange
        break;
      default:
        consume(10);                // CharCodeRange
      }
      lookahead1(20);               // CharCode | Char | CharRange | CharCodeRange | ']'
      if (l1 == 37)                 // ']'
      {
        break;
      }
    }
    consume(37);                    // ']'
    eventHandler.endNonterminal("CharClass", e0);
  }

  private void parse_Option()
  {
    eventHandler.startNonterminal("Option", e0);
    consume(23);                    // '/*'
    for (;;)
    {
      lookahead1(9);                // Space | 'ws'
      if (l1 != 3)                  // Space
      {
        break;
      }
      consume(3);                   // Space
    }
    consume(40);                    // 'ws'
    lookahead1(1);                  // ':'
    consume(24);                    // ':'
    for (;;)
    {
      lookahead1(18);               // Space | 'definition' | 'explicit'
      if (l1 != 3)                  // Space
      {
        break;
      }
      consume(3);                   // Space
    }
    switch (l1)
    {
    case 39:                        // 'explicit'
      consume(39);                  // 'explicit'
      break;
    default:
      consume(38);                  // 'definition'
    }
    for (;;)
    {
      lookahead1(7);                // Space | '*/'
      if (l1 != 3)                  // Space
      {
        break;
      }
      consume(3);                   // Space
    }
    consume(18);                    // '*/'
    eventHandler.endNonterminal("Option", e0);
  }

  private void parse_Preference()
  {
    eventHandler.startNonterminal("Preference", e0);
    parse_NameOrString();
    lookahead1W(16);                // Whitespace | '<<' | '>>'
    switch (l1)
    {
    case 31:                        // '>>'
      consume(31);                  // '>>'
      for (;;)
      {
        lookahead1W(10);            // Whitespace | Name | StringLiteral
        whitespace();
        parse_NameOrString();
        lookahead1W(22);            // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' |
                                    // '<?ENCORE?>'
        switch (l1)
        {
        case 2:                     // Name
          lookahead2W(36);          // Whitespace | Name | StringLiteral | CaretName | EOF | EquivalenceLookAhead |
                                    // '.' | '::=' | '<<' | '<?ENCORE?>' | '>>' | '?' | '\\'
          switch (lk)
          {
          case 386:                 // Name CaretName
            lookahead3W(28);        // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' | '<<' |
                                    // '<?ENCORE?>' | '>>'
            break;
          }
          break;
        case 5:                     // StringLiteral
          lookahead2W(29);          // Whitespace | Name | StringLiteral | CaretName | EOF | EquivalenceLookAhead |
                                    // '.' | '<<' | '<?ENCORE?>' | '>>'
          switch (lk)
          {
          case 389:                 // StringLiteral CaretName
            lookahead3W(28);        // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' | '<<' |
                                    // '<?ENCORE?>' | '>>'
            break;
          }
          break;
        default:
          lk = l1;
        }
        if (lk == 11                // EOF
         || lk == 12                // EquivalenceLookAhead
         || lk == 21                // '.'
         || lk == 28                // '<?ENCORE?>'
         || lk == 1602              // Name '::='
         || lk == 1666              // Name '<<'
         || lk == 1669              // StringLiteral '<<'
         || lk == 1986              // Name '>>'
         || lk == 1989              // StringLiteral '>>'
         || lk == 2050              // Name '?'
         || lk == 2306              // Name '\\'
         || lk == 106882            // Name CaretName '<<'
         || lk == 106885            // StringLiteral CaretName '<<'
         || lk == 127362            // Name CaretName '>>'
         || lk == 127365)           // StringLiteral CaretName '>>'
        {
          break;
        }
      }
      break;
    default:
      consume(26);                  // '<<'
      for (;;)
      {
        lookahead1W(10);            // Whitespace | Name | StringLiteral
        whitespace();
        parse_NameOrString();
        lookahead1W(22);            // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' |
                                    // '<?ENCORE?>'
        switch (l1)
        {
        case 2:                     // Name
          lookahead2W(36);          // Whitespace | Name | StringLiteral | CaretName | EOF | EquivalenceLookAhead |
                                    // '.' | '::=' | '<<' | '<?ENCORE?>' | '>>' | '?' | '\\'
          switch (lk)
          {
          case 386:                 // Name CaretName
            lookahead3W(28);        // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' | '<<' |
                                    // '<?ENCORE?>' | '>>'
            break;
          }
          break;
        case 5:                     // StringLiteral
          lookahead2W(29);          // Whitespace | Name | StringLiteral | CaretName | EOF | EquivalenceLookAhead |
                                    // '.' | '<<' | '<?ENCORE?>' | '>>'
          switch (lk)
          {
          case 389:                 // StringLiteral CaretName
            lookahead3W(28);        // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' | '<<' |
                                    // '<?ENCORE?>' | '>>'
            break;
          }
          break;
        default:
          lk = l1;
        }
        if (lk == 11                // EOF
         || lk == 12                // EquivalenceLookAhead
         || lk == 21                // '.'
         || lk == 28                // '<?ENCORE?>'
         || lk == 1602              // Name '::='
         || lk == 1666              // Name '<<'
         || lk == 1669              // StringLiteral '<<'
         || lk == 1986              // Name '>>'
         || lk == 1989              // StringLiteral '>>'
         || lk == 2050              // Name '?'
         || lk == 2306              // Name '\\'
         || lk == 106882            // Name CaretName '<<'
         || lk == 106885            // StringLiteral CaretName '<<'
         || lk == 127362            // Name CaretName '>>'
         || lk == 127365)           // StringLiteral CaretName '>>'
        {
          break;
        }
      }
    }
    eventHandler.endNonterminal("Preference", e0);
  }

  private void parse_Delimiter()
  {
    eventHandler.startNonterminal("Delimiter", e0);
    consume(2);                     // Name
    lookahead1W(6);                 // Whitespace | '\\'
    consume(36);                    // '\\'
    for (;;)
    {
      lookahead1W(10);              // Whitespace | Name | StringLiteral
      whitespace();
      parse_NameOrString();
      lookahead1W(22);              // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' |
                                    // '<?ENCORE?>'
      switch (l1)
      {
      case 2:                       // Name
        lookahead2W(36);            // Whitespace | Name | StringLiteral | CaretName | EOF | EquivalenceLookAhead |
                                    // '.' | '::=' | '<<' | '<?ENCORE?>' | '>>' | '?' | '\\'
        switch (lk)
        {
        case 386:                   // Name CaretName
          lookahead3W(28);          // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' | '<<' |
                                    // '<?ENCORE?>' | '>>'
          break;
        }
        break;
      case 5:                       // StringLiteral
        lookahead2W(29);            // Whitespace | Name | StringLiteral | CaretName | EOF | EquivalenceLookAhead |
                                    // '.' | '<<' | '<?ENCORE?>' | '>>'
        switch (lk)
        {
        case 389:                   // StringLiteral CaretName
          lookahead3W(28);          // Whitespace | Name | StringLiteral | EOF | EquivalenceLookAhead | '.' | '<<' |
                                    // '<?ENCORE?>' | '>>'
          break;
        }
        break;
      default:
        lk = l1;
      }
      if (lk == 11                  // EOF
       || lk == 12                  // EquivalenceLookAhead
       || lk == 21                  // '.'
       || lk == 28                  // '<?ENCORE?>'
       || lk == 1602                // Name '::='
       || lk == 1666                // Name '<<'
       || lk == 1669                // StringLiteral '<<'
       || lk == 1986                // Name '>>'
       || lk == 1989                // StringLiteral '>>'
       || lk == 2050                // Name '?'
       || lk == 2306                // Name '\\'
       || lk == 106882              // Name CaretName '<<'
       || lk == 106885              // StringLiteral CaretName '<<'
       || lk == 127362              // Name CaretName '>>'
       || lk == 127365)             // StringLiteral CaretName '>>'
      {
        break;
      }
    }
    eventHandler.endNonterminal("Delimiter", e0);
  }

  private void parse_Equivalence()
  {
    eventHandler.startNonterminal("Equivalence", e0);
    consume(12);                    // EquivalenceLookAhead
    lookahead1W(12);                // Whitespace | StringLiteral | '['
    whitespace();
    parse_EquivalenceCharRange();
    lookahead1W(5);                 // Whitespace | '=='
    consume(30);                    // '=='
    lookahead1W(12);                // Whitespace | StringLiteral | '['
    whitespace();
    parse_EquivalenceCharRange();
    eventHandler.endNonterminal("Equivalence", e0);
  }

  private void parse_EquivalenceCharRange()
  {
    eventHandler.startNonterminal("EquivalenceCharRange", e0);
    switch (l1)
    {
    case 5:                         // StringLiteral
      consume(5);                   // StringLiteral
      break;
    default:
      consume(34);                  // '['
      lookahead1(19);               // CharCode | Char | CharRange | CharCodeRange
      switch (l1)
      {
      case 8:                       // Char
        consume(8);                 // Char
        break;
      case 7:                       // CharCode
        consume(7);                 // CharCode
        break;
      case 9:                       // CharRange
        consume(9);                 // CharRange
        break;
      default:
        consume(10);                // CharCodeRange
      }
      lookahead1(3);                // ']'
      consume(37);                  // ']'
    }
    eventHandler.endNonterminal("EquivalenceCharRange", e0);
  }

  private void parse_Encore()
  {
    eventHandler.startNonterminal("Encore", e0);
    consume(28);                    // '<?ENCORE?>'
    for (;;)
    {
      lookahead1W(13);              // Whitespace | EOF | '<?'
      if (l1 != 27)                 // '<?'
      {
        break;
      }
      whitespace();
      parse_ProcessingInstruction();
    }
    eventHandler.endNonterminal("Encore", e0);
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

  private void whitespace()
  {
    if (e0 != b1)
    {
      eventHandler.whitespace(e0, b1);
      e0 = b1;
    }
  }

  private int matchW(int tokenSetId)
  {
    int code;
    for (;;)
    {
      code = match(tokenSetId);
      if (code != 1)                // Whitespace
      {
        break;
      }
    }
    return code;
  }

  private void lookahead1W(int tokenSetId)
  {
    if (l1 == 0)
    {
      l1 = matchW(tokenSetId);
      b1 = begin;
      e1 = end;
    }
  }

  private void lookahead2W(int tokenSetId)
  {
    if (l2 == 0)
    {
      l2 = matchW(tokenSetId);
      b2 = begin;
      e2 = end;
    }
    lk = (l2 << 6) | l1;
  }

  private void lookahead3W(int tokenSetId)
  {
    if (l3 == 0)
    {
      l3 = matchW(tokenSetId);
      b3 = begin;
      e3 = end;
    }
    lk |= l3 << 12;
  }

  private void lookahead1(int tokenSetId)
  {
    if (l1 == 0)
    {
      l1 = match(tokenSetId);
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

    for (int code = result & 255; code != 0; )
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

        int lo = 0, hi = 1;
        for (int m = 1; ; m = (hi + lo) >> 1)
        {
          if (MAP2[m] > c0) {hi = m - 1;}
          else if (MAP2[2 + m] < c0) {lo = m + 1;}
          else {charclass = MAP2[4 + m]; break;}
          if (lo > hi) {charclass = 0; break;}
        }
      }

      state = code;
      int i0 = (charclass << 8) + code - 1;
      code = TRANSITION[(i0 & 7) + TRANSITION[i0 >> 3]];

      if (code > 255)
      {
        result = code;
        code &= 255;
        end = current;
      }
    }

    result >>= 8;
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
    else if ((result & 64) != 0)
    {
      end = begin;
      if (nonbmp)
      {
        for (int i = result >> 7; i > 0; --i)
        {
          int c1 = end < size ? input.charAt(end) : 0;
          ++end;
          if (c1 >= 0xd800 && c1 < 0xdc000)
          {
            ++end;
          }
        }
      }
      else
      {
        end += (result >> 7);
      }
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
    return (result & 63) - 1;
  }

  private static String[] getTokenSet(int tokenSetId)
  {
    java.util.ArrayList<String> expected = new java.util.ArrayList<>();
    int s = tokenSetId < 0 ? - tokenSetId : INITIAL[tokenSetId] & 255;
    for (int i = 0; i < 42; i += 32)
    {
      int j = i;
      int i0 = (i >> 5) * 177 + s - 1;
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

  private static final int[] MAP0 =
  {
    /*   0 */ 52, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 4, 5,
    /*  35 */ 6, 7, 4, 8, 9, 10, 11, 12, 13, 4, 14, 15, 16, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 18, 4, 19, 20, 21,
    /*  63 */ 22, 4, 23, 23, 24, 23, 25, 23, 26, 26, 26, 26, 27, 26, 26, 28, 29, 26, 26, 30, 31, 32, 26, 26, 26, 26, 26,
    /*  90 */ 26, 33, 34, 35, 36, 26, 4, 23, 23, 37, 38, 39, 40, 26, 26, 41, 26, 26, 42, 26, 43, 44, 45, 26, 26, 46, 47,
    /* 117 */ 26, 26, 48, 49, 26, 26, 4, 50, 4, 4, 4
  };

  private static final int[] MAP1 =
  {
    /*    0 */ 216, 291, 323, 383, 415, 908, 351, 815, 815, 447, 479, 511, 543, 575, 621, 882, 589, 681, 815, 815, 815,
    /*   21 */ 815, 815, 815, 815, 815, 815, 815, 815, 815, 713, 745, 821, 649, 815, 815, 815, 815, 815, 815, 815, 815,
    /*   42 */ 815, 815, 815, 815, 815, 815, 777, 809, 815, 815, 815, 815, 815, 815, 815, 815, 815, 815, 815, 815, 815,
    /*   63 */ 815, 815, 815, 815, 815, 815, 815, 815, 815, 815, 815, 815, 815, 815, 815, 247, 247, 247, 247, 247, 247,
    /*   84 */ 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247,
    /*  105 */ 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247,
    /*  126 */ 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247,
    /*  147 */ 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 259, 815, 815, 815, 815, 815, 815, 815, 815,
    /*  168 */ 815, 815, 815, 815, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247,
    /*  189 */ 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247,
    /*  210 */ 247, 247, 247, 247, 247, 853, 940, 949, 941, 941, 957, 965, 973, 979, 987, 1010, 1018, 1035, 1053, 1071,
    /*  230 */ 1079, 1087, 1262, 1262, 1262, 1262, 1262, 1262, 1433, 1262, 1254, 1254, 1255, 1254, 1254, 1254, 1255,
    /*  247 */ 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254,
    /*  264 */ 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1256, 1262,
    /*  281 */ 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1254, 1254, 1254, 1254, 1254, 1254, 1342,
    /*  298 */ 1255, 1253, 1252, 1254, 1254, 1254, 1254, 1254, 1255, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254,
    /*  315 */ 1258, 1418, 1254, 1254, 1254, 1254, 1062, 1421, 1254, 1254, 1254, 1262, 1262, 1262, 1262, 1262, 1262,
    /*  332 */ 1262, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1261, 1262, 1420, 1260, 1262,
    /*  349 */ 1388, 1262, 1262, 1262, 1262, 1262, 1253, 1254, 1254, 1259, 1131, 1308, 1387, 1262, 1382, 1388, 1131,
    /*  366 */ 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1344, 1254, 1255, 1142, 1382, 1297, 1196, 1382, 1388,
    /*  383 */ 1382, 1382, 1382, 1382, 1382, 1382, 1382, 1382, 1384, 1262, 1262, 1262, 1388, 1262, 1262, 1262, 1367,
    /*  400 */ 1231, 1254, 1254, 1251, 1254, 1254, 1254, 1254, 1255, 1255, 1407, 1252, 1254, 1258, 1262, 1253, 1100,
    /*  417 */ 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1253, 1100, 1254, 1254, 1254, 1254, 1109, 1262, 1254,
    /*  434 */ 1254, 1254, 1254, 1254, 1254, 1122, 1042, 1254, 1254, 1254, 1123, 1256, 1260, 1446, 1254, 1254, 1254,
    /*  451 */ 1254, 1254, 1254, 1160, 1382, 1384, 1197, 1254, 1178, 1382, 1262, 1262, 1446, 1122, 1343, 1254, 1254,
    /*  468 */ 1252, 1060, 1192, 1169, 1181, 1433, 1207, 1178, 1382, 1260, 1262, 1218, 1241, 1343, 1254, 1254, 1252,
    /*  485 */ 1397, 1192, 1184, 1181, 1262, 1229, 1434, 1382, 1239, 1262, 1446, 1230, 1251, 1254, 1254, 1252, 1249,
    /*  502 */ 1160, 1272, 1114, 1262, 1262, 994, 1382, 1262, 1262, 1446, 1122, 1343, 1254, 1254, 1252, 1340, 1160,
    /*  519 */ 1198, 1181, 1434, 1207, 1045, 1382, 1262, 1262, 1002, 1023, 1285, 1281, 1063, 1023, 1133, 1045, 1199,
    /*  536 */ 1196, 1433, 1262, 1433, 1382, 1262, 1262, 1446, 1100, 1252, 1254, 1254, 1252, 1101, 1045, 1273, 1196,
    /*  553 */ 1435, 1262, 1045, 1382, 1262, 1262, 1002, 1100, 1252, 1254, 1254, 1252, 1101, 1045, 1273, 1196, 1435,
    /*  570 */ 1264, 1045, 1382, 1262, 1262, 1002, 1100, 1252, 1254, 1254, 1252, 1254, 1045, 1170, 1196, 1433, 1262,
    /*  587 */ 1045, 1382, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262,
    /*  604 */ 1262, 1262, 1262, 1262, 1262, 1254, 1254, 1254, 1254, 1256, 1262, 1254, 1254, 1254, 1254, 1255, 1262,
    /*  621 */ 1253, 1254, 1254, 1254, 1254, 1255, 1293, 1387, 1305, 1383, 1382, 1388, 1262, 1262, 1262, 1262, 1210,
    /*  638 */ 1317, 1419, 1253, 1327, 1337, 1293, 1152, 1352, 1384, 1382, 1388, 1262, 1262, 1262, 1262, 1264, 1027,
    /*  655 */ 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1259, 1262, 1262, 1262, 1262, 1262, 1262,
    /*  672 */ 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1249, 1396, 1259, 1262, 1262, 1262, 1262, 1405,
    /*  689 */ 1261, 1405, 1062, 1416, 1329, 1061, 1209, 1262, 1262, 1262, 1262, 1264, 1262, 1319, 1263, 1283, 1259,
    /*  706 */ 1262, 1262, 1262, 1262, 1429, 1261, 1431, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254,
    /*  723 */ 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1258, 1254, 1254, 1254, 1254, 1254, 1254, 1254,
    /*  740 */ 1254, 1254, 1254, 1254, 1260, 1254, 1254, 1256, 1256, 1254, 1254, 1254, 1254, 1256, 1256, 1254, 1408,
    /*  757 */ 1254, 1254, 1254, 1256, 1254, 1254, 1254, 1254, 1254, 1254, 1100, 1134, 1221, 1257, 1123, 1258, 1254,
    /*  774 */ 1257, 1221, 1257, 1092, 1262, 1262, 1262, 1253, 1309, 1168, 1262, 1253, 1254, 1254, 1254, 1254, 1254,
    /*  791 */ 1254, 1254, 1254, 1254, 1257, 999, 1253, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254,
    /*  808 */ 1443, 1418, 1254, 1254, 1254, 1254, 1257, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262,
    /*  825 */ 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262,
    /*  842 */ 1262, 1262, 1262, 1262, 1262, 1382, 1385, 1365, 1262, 1262, 1262, 1254, 1254, 1254, 1254, 1254, 1254,
    /*  859 */ 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1254, 1258, 1262, 1262,
    /*  876 */ 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1262, 1388, 1382, 1388, 1375, 1357, 1254, 1253, 1254,
    /*  893 */ 1254, 1254, 1260, 1381, 1382, 1273, 1386, 1272, 1381, 1382, 1384, 1381, 1365, 1262, 1262, 1262, 1262,
    /*  910 */ 1262, 1262, 1262, 1262, 1253, 1254, 1254, 1254, 1255, 1431, 1253, 1254, 1254, 1254, 1255, 1262, 1381,
    /*  927 */ 1382, 1166, 1382, 1382, 1148, 1362, 1262, 1254, 1254, 1254, 1259, 1259, 1262, 52, 0, 0, 0, 0, 0, 0, 0, 0,
    /*  949 */ 0, 1, 2, 0, 0, 1, 0, 0, 3, 4, 5, 6, 7, 4, 8, 9, 10, 11, 12, 13, 4, 14, 15, 16, 17, 17, 17, 17, 17, 17,
    /*  979 */ 17, 17, 18, 4, 19, 20, 21, 22, 4, 23, 23, 24, 23, 25, 23, 26, 4, 4, 4, 4, 4, 51, 51, 4, 4, 51, 51, 4, 26,
    /* 1008 */ 26, 26, 26, 26, 26, 27, 26, 26, 28, 29, 26, 26, 30, 31, 32, 26, 26, 26, 4, 4, 4, 26, 26, 4, 4, 26, 4, 26,
    /* 1036 */ 26, 26, 33, 34, 35, 36, 26, 4, 4, 26, 26, 4, 4, 4, 4, 51, 51, 4, 23, 23, 37, 38, 39, 40, 26, 4, 26, 4, 4,
    /* 1065 */ 4, 26, 26, 4, 4, 4, 26, 41, 26, 26, 42, 26, 43, 44, 45, 26, 26, 46, 47, 26, 26, 48, 49, 26, 26, 4, 50, 4,
    /* 1093 */ 4, 4, 4, 4, 51, 4, 26, 26, 26, 26, 26, 26, 4, 26, 26, 26, 26, 26, 4, 51, 51, 51, 51, 4, 51, 51, 51, 4, 4,
    /* 1122 */ 26, 26, 26, 26, 26, 4, 4, 26, 26, 51, 26, 26, 26, 26, 26, 26, 26, 4, 26, 4, 26, 26, 26, 26, 4, 26, 51,
    /* 1149 */ 51, 4, 51, 51, 51, 4, 51, 51, 26, 4, 4, 26, 26, 4, 4, 51, 26, 51, 51, 4, 51, 51, 51, 51, 51, 4, 4, 51,
    /* 1177 */ 51, 26, 26, 51, 51, 4, 4, 51, 51, 51, 4, 4, 4, 4, 51, 26, 26, 4, 4, 51, 4, 51, 51, 51, 51, 4, 4, 4, 51,
    /* 1206 */ 51, 4, 4, 4, 4, 26, 26, 4, 26, 4, 4, 26, 4, 4, 51, 4, 4, 26, 26, 26, 4, 26, 26, 4, 26, 26, 26, 26, 4, 26,
    /* 1236 */ 4, 26, 26, 51, 51, 26, 26, 26, 4, 4, 4, 4, 26, 26, 4, 26, 26, 4, 26, 26, 26, 26, 26, 26, 26, 26, 4, 4, 4,
    /* 1265 */ 4, 4, 4, 4, 4, 26, 4, 51, 51, 51, 51, 51, 51, 4, 51, 51, 4, 26, 26, 4, 26, 4, 26, 26, 26, 26, 4, 4, 26,
    /* 1294 */ 51, 26, 26, 51, 51, 51, 51, 51, 26, 26, 51, 26, 26, 26, 26, 26, 26, 51, 51, 51, 51, 51, 51, 26, 4, 26, 4,
    /* 1321 */ 4, 26, 4, 4, 26, 26, 4, 26, 26, 26, 4, 26, 4, 26, 4, 26, 4, 4, 26, 26, 4, 26, 26, 4, 4, 26, 26, 26, 26,
    /* 1350 */ 26, 4, 26, 26, 26, 26, 26, 4, 51, 4, 4, 4, 4, 51, 51, 4, 51, 4, 4, 4, 4, 4, 4, 26, 51, 4, 4, 4, 4, 4, 51,
    /* 1381 */ 4, 51, 51, 51, 51, 51, 51, 51, 51, 4, 4, 4, 4, 4, 4, 4, 26, 4, 26, 26, 4, 26, 26, 4, 4, 4, 4, 4, 26, 4,
    /* 1411 */ 26, 4, 26, 4, 26, 4, 26, 4, 4, 4, 4, 4, 26, 26, 26, 26, 26, 26, 4, 4, 4, 26, 4, 4, 4, 4, 4, 4, 4, 51, 51,
    /* 1442 */ 4, 26, 26, 26, 4, 51, 51, 51, 4, 26, 26, 26
  };

  private static final int[] MAP2 =
  {
    /* 0 */ 57344, 65536, 65533, 1114111, 4, 4
  };

  private static final int[] INITIAL =
  {
    /*  0 */ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
    /* 29 */ 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52
  };

  private static final int[] TRANSITION =
  {
    /*    0 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*   17 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3275, 1696,
    /*   34 */ 1706, 1698, 1698, 1698, 1813, 2663, 2078, 1728, 2455, 2082, 2150, 2220, 1738, 2805, 3305, 1747, 1758,
    /*   51 */ 3076, 1775, 3257, 1797, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3086, 1809, 1713, 1698,
    /*   68 */ 1698, 1698, 1813, 1729, 2078, 1728, 2125, 2082, 2959, 2220, 1821, 2895, 2405, 1831, 3001, 3270, 1842,
    /*   85 */ 2439, 1797, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3275, 1696, 1720, 1698, 1698, 1698,
    /*  102 */ 1813, 2827, 2078, 1872, 1897, 2188, 2150, 2863, 1738, 2805, 3305, 1747, 1758, 3076, 1775, 3257, 1797,
    /*  119 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2347, 1729, 1729, 1729, 1729, 1823,
    /*  136 */ 2078, 1872, 2067, 2188, 2150, 2880, 2811, 2205, 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729,
    /*  153 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2902, 3230, 1921, 1921, 1921, 1925, 2685, 2078, 1872,
    /*  170 */ 2067, 2188, 2150, 2880, 2811, 2205, 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729,
    /*  187 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2073, 1849, 3286, 1855, 1860, 1823, 2078, 1937, 2341, 2188,
    /*  204 */ 2150, 2880, 2507, 2205, 1834, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  221 */ 1729, 1729, 1729, 1729, 1729, 2347, 3048, 1864, 3054, 3059, 1823, 2078, 1872, 2067, 2188, 2150, 2880,
    /*  238 */ 2811, 2205, 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  255 */ 1729, 1729, 1729, 2347, 2108, 2109, 1950, 1955, 1823, 2078, 1872, 2067, 2188, 2150, 2880, 2811, 2205,
    /*  272 */ 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  289 */ 3008, 2326, 2470, 2470, 2470, 2474, 2813, 2078, 1872, 2067, 2188, 2150, 2880, 2811, 2205, 1750, 2776,
    /*  306 */ 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2347,
    /*  323 */ 1967, 1974, 1979, 1983, 1823, 2078, 1872, 2067, 2188, 2150, 2880, 2811, 2205, 1750, 2776, 1890, 3416,
    /*  340 */ 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2368, 2347, 1995, 2002,
    /*  357 */ 2008, 2012, 1823, 2078, 1872, 2067, 2188, 2150, 2880, 2811, 2205, 1750, 2776, 1890, 3416, 3384, 3117,
    /*  374 */ 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2083, 1729, 2347, 1729, 2642, 2024, 2028,
    /*  391 */ 1823, 2078, 2036, 2248, 2188, 2294, 3363, 2402, 2396, 2052, 2049, 2060, 2954, 2091, 2591, 2106, 1729,
    /*  408 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2347, 1729, 3033, 2117, 2121, 1823, 2078,
    /*  425 */ 1872, 2067, 2188, 2150, 2880, 2811, 2205, 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729,
    /*  442 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2347, 1729, 1729, 2133, 2137, 1823, 1942, 2145, 2067,
    /*  459 */ 2167, 2174, 2200, 2811, 2213, 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729,
    /*  476 */ 1729, 1729, 1729, 1729, 1729, 1729, 3341, 2357, 2352, 2358, 2228, 1823, 2078, 1872, 2067, 2188, 2241,
    /*  493 */ 2880, 2811, 2205, 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  510 */ 1729, 1729, 3063, 1782, 1789, 2262, 2270, 2276, 2281, 1882, 2078, 2289, 3348, 2188, 2233, 2971, 2632,
    /*  527 */ 2626, 2779, 2302, 2313, 2931, 2334, 3177, 2366, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  544 */ 1729, 1729, 2347, 1729, 1729, 1729, 1877, 1823, 2078, 2537, 2067, 2376, 2389, 2413, 2811, 2418, 1750,
    /*  561 */ 3378, 1890, 2734, 2426, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2434, 1730,
    /*  578 */ 2254, 1729, 1959, 2447, 2451, 2463, 2078, 1872, 2067, 2188, 2150, 2754, 2811, 2205, 1750, 2155, 3171,
    /*  595 */ 3081, 3384, 2159, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2912, 2486, 2494,
    /*  612 */ 2519, 2525, 2533, 1823, 2545, 1872, 2557, 2188, 2150, 2880, 2811, 2205, 1750, 2776, 1890, 3416, 3384,
    /*  629 */ 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3105, 1729, 2347, 1729, 1729, 1729,
    /*  646 */ 1729, 2571, 2078, 1872, 2181, 2188, 2150, 3189, 2811, 2579, 2305, 2776, 2599, 3411, 2619, 1913, 2640,
    /*  663 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2650, 1987, 2478, 2478, 2657, 1823,
    /*  680 */ 3204, 1872, 2098, 2841, 2150, 2880, 2811, 2205, 1750, 2776, 1890, 3416, 3384, 2676, 2683, 1729, 1729,
    /*  697 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1765, 1767, 2693, 1729, 3421, 2700, 2704, 1823, 2716, 2729,
    /*  714 */ 2742, 2750, 2150, 2880, 2811, 2205, 1750, 2776, 1890, 3209, 2762, 3117, 2683, 1729, 1729, 1729, 1729,
    /*  731 */ 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2845, 2067, 2376,
    /*  748 */ 2389, 2413, 2811, 2418, 1750, 3378, 1890, 2734, 2426, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  765 */ 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2845, 2067, 2376, 2389, 2413,
    /*  782 */ 2926, 2418, 1750, 3378, 1890, 2734, 2426, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  799 */ 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2845, 2067, 2798, 2389, 2413, 2811, 2418,
    /*  816 */ 2381, 3378, 1890, 2821, 2426, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031,
    /*  833 */ 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2835, 2067, 2188, 2241, 2880, 2811, 2205, 1750, 2776,
    /*  850 */ 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399,
    /*  867 */ 2787, 2787, 2787, 2790, 1823, 2078, 2835, 2067, 2188, 2241, 2880, 2585, 2205, 1750, 2776, 1890, 3416,
    /*  884 */ 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787,
    /*  901 */ 2787, 2790, 1823, 2078, 2835, 2067, 2188, 2241, 2606, 2811, 2205, 1750, 2994, 1890, 3416, 3384, 3117,
    /*  918 */ 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790,
    /*  935 */ 1823, 2078, 2835, 2067, 2188, 2241, 2936, 2811, 2205, 2769, 2776, 1890, 3416, 3384, 3117, 2683, 1729,
    /*  952 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078,
    /*  969 */ 2835, 2067, 2188, 2241, 2880, 2811, 2205, 1750, 3140, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729,
    /*  986 */ 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2835, 2067,
    /* 1003 */ 2188, 2241, 2880, 2811, 2205, 1750, 2776, 1890, 3406, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729,
    /* 1020 */ 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2835, 2067, 2853, 2241,
    /* 1037 */ 2880, 2811, 2205, 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1054 */ 1729, 1729, 1729, 2907, 3320, 3326, 2016, 2871, 2876, 1823, 2078, 1872, 2067, 2188, 2150, 2880, 2811,
    /* 1071 */ 2205, 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1088 */ 1799, 1729, 3354, 1729, 1801, 1729, 3359, 3245, 2078, 1872, 2067, 2188, 2150, 2880, 2811, 2205, 1750,
    /* 1105 */ 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2859, 1729,
    /* 1122 */ 1903, 1729, 1729, 1729, 1729, 1823, 2078, 1728, 2708, 2549, 2888, 1739, 2219, 2920, 1750, 2776, 1890,
    /* 1139 */ 3416, 3146, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2563, 2667,
    /* 1156 */ 2668, 2949, 2967, 1823, 2078, 2979, 3224, 2188, 2150, 2880, 2811, 2205, 1750, 2776, 1890, 3416, 3384,
    /* 1173 */ 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787,
    /* 1190 */ 2790, 1823, 2078, 2845, 2067, 2376, 2389, 2413, 2811, 2418, 2987, 3378, 1890, 2734, 2426, 3117, 2683,
    /* 1207 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3021, 2787, 2787, 2787, 2790, 1823,
    /* 1224 */ 2078, 2845, 2067, 2376, 2389, 2413, 2811, 2418, 1750, 3378, 1890, 2734, 2426, 3117, 2683, 1729, 1729,
    /* 1241 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3041, 2787, 2787, 2787, 2790, 1823, 2041, 2845,
    /* 1258 */ 2067, 2376, 2389, 2413, 2811, 2418, 1750, 3378, 1890, 2734, 2426, 3117, 2683, 1729, 1729, 1729, 1729,
    /* 1275 */ 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2845, 2067, 3071,
    /* 1292 */ 2389, 2413, 2811, 2418, 1750, 3378, 1890, 2734, 2426, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1309 */ 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2835, 2067, 2188, 3094, 2880,
    /* 1326 */ 3102, 2941, 1750, 3113, 3125, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1343 */ 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2835, 2067, 2188, 2241, 3133, 2811, 2205,
    /* 1360 */ 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031,
    /* 1377 */ 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2835, 2067, 2188, 2241, 2880, 3154, 2205, 1750, 2776,
    /* 1394 */ 1890, 3416, 3384, 3164, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399,
    /* 1411 */ 2787, 2787, 2787, 2790, 1823, 2078, 2835, 2067, 2188, 2241, 2880, 2811, 2205, 1750, 2776, 1890, 2721,
    /* 1428 */ 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787,
    /* 1445 */ 2787, 2790, 1823, 2078, 2835, 2067, 3185, 2241, 2880, 2811, 2205, 1750, 2776, 1890, 3416, 3384, 3117,
    /* 1462 */ 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790,
    /* 1479 */ 2511, 2078, 2835, 2067, 2188, 3197, 2880, 2811, 2611, 1750, 2776, 3217, 3416, 3238, 3117, 2683, 1729,
    /* 1496 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 2078,
    /* 1513 */ 2835, 2067, 2188, 2241, 2880, 2811, 2205, 1750, 3253, 1890, 3265, 3384, 3117, 2683, 1729, 1729, 1729,
    /* 1530 */ 1729, 1729, 1729, 1729, 1729, 1729, 3031, 3301, 3399, 2787, 2787, 2787, 2790, 1823, 2078, 2835, 2320,
    /* 1547 */ 2188, 2241, 2880, 3283, 2501, 3447, 2776, 1890, 1908, 3294, 3117, 2683, 1729, 1729, 1729, 1729, 1729,
    /* 1564 */ 1729, 1729, 1729, 1729, 3031, 3028, 3399, 2787, 2787, 2787, 2790, 1823, 3013, 3313, 2067, 2188, 3334,
    /* 1581 */ 2880, 2811, 2205, 3371, 2776, 3392, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1598 */ 1729, 1729, 1729, 3156, 2347, 3429, 3434, 3439, 3443, 1823, 2078, 1872, 2067, 2188, 2150, 2880, 2811,
    /* 1615 */ 2205, 1750, 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1632 */ 1729, 1729, 2347, 1729, 1729, 1729, 1877, 1823, 2078, 1872, 2067, 2188, 2241, 2880, 2811, 2205, 1750,
    /* 1649 */ 2776, 1890, 3416, 3384, 3117, 2683, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2192,
    /* 1666 */ 1929, 3462, 3455, 3455, 3465, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1683 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1084, 1084, 567, 567, 567,
    /* 1701 */ 567, 567, 567, 567, 567, 567, 1084, 1084, 0, 0, 567, 567, 567, 1024, 1024, 0, 0, 567, 567, 567, 1084,
    /* 1722 */ 1084, 2376, 2376, 567, 567, 567, 35072, 0, 0, 0, 0, 0, 0, 0, 0, 57, 625, 0, 0, 0, 0, 0, 0, 0, 100, 649,
    /* 1748 */ 650, 0, 0, 0, 0, 0, 144, 134, 0, 100, 144, 144, 134, 134, 0, 100, 663, 0, 0, 54, 0, 0, 0, 0, 0, 0, 8448,
    /* 1775 */ 0, 162, 163, 163, 163, 163, 163, 0, 0, 56, 56, 56, 56, 56, 56, 68, 0, 2376, 2376, 74, 56, 56, 177, 0, 0,
    /* 1800 */ 0, 0, 0, 0, 0, 59, 0, 0, 0, 1024, 1024, 567, 567, 567, 567, 567, 567, 0, 0, 567, 0, 635, 0, 0, 0, 0, 0,
    /* 1827 */ 0, 0, 63, 64, 649, 663, 0, 0, 0, 0, 0, 144, 134, 149, 100, 0, 162, 174, 174, 174, 174, 174, 0, 0, 79, 0,
    /* 1853 */ 0, 0, 0, 79, 79, 79, 0, 79, 79, 79, 79, 0, 0, 0, 0, 0, 3584, 0, 3584, 35072, 0, 0, 0, 96, 0, 0, 0, 0,
    /* 1881 */ 821, 0, 0, 0, 0, 4864, 0, 63, 64, 144, 144, 134, 134, 0, 100, 100, 0, 0, 96, 0, 85, 598, 0, 68, 0, 0,
    /* 1907 */ 9728, 0, 0, 0, 144, 167, 0, 0, 0, 144, 19856, 173, 166, 144, 63, 63, 63, 63, 63, 63, 63, 63, 0, 0, 0, 0,
    /* 1933 */ 0, 3072, 3072, 0, 35072, 0, 0, 0, 97, 0, 0, 0, 68, 68, 0, 0, 92, 0, 3840, 3840, 3840, 0, 3840, 3840,
    /* 1957 */ 3840, 3840, 0, 0, 0, 0, 57, 0, 0, 0, 0, 4096, 4096, 0, 0, 0, 0, 4096, 4096, 4096, 4096, 0, 4096, 4096,
    /* 1981 */ 4096, 4096, 4096, 4096, 4096, 4096, 0, 0, 0, 0, 67, 67, 0, 0, 0, 4352, 0, 0, 0, 0, 0, 4352, 4352, 0,
    /* 2005 */ 4352, 0, 0, 4352, 4352, 4352, 4352, 4352, 4352, 4352, 4352, 0, 0, 0, 0, 77, 9043, 0, 9043, 4608, 0, 0, 0,
    /* 2028 */ 4608, 4608, 4608, 4608, 0, 0, 0, 85, 35072, 6229, 0, 0, 96, 0, 0, 0, 68, 68, 90, 0, 0, 101, 637, 0, 0, 0,
    /* 2054 */ 0, 0, 145, 134, 0, 150, 145, 145, 157, 134, 0, 101, 101, 0, 0, 96, 0, 100, 598, 0, 68, 0, 73, 73, 0, 0,
    /* 2080 */ 0, 68, 68, 0, 0, 0, 0, 0, 0, 0, 61, 0, 145, 164, 164, 163, 164, 164, 0, 0, 96, 0, 100, 598, 0, 66816,
    /* 2106 */ 145, 0, 0, 0, 0, 0, 0, 0, 3840, 0, 3840, 5120, 0, 0, 0, 5120, 5120, 5120, 5120, 0, 0, 0, 0, 85, 567, 0,
    /* 2132 */ 68, 0, 5376, 0, 5376, 0, 5376, 0, 5376, 821, 0, 0, 0, 35164, 0, 0, 0, 96, 0, 0, 0, 100, 100, 100, 598, 0,
    /* 2158 */ 0, 0, 0, 0, 144, 144, 144, 134, 144, 68, 0, 0, 2560, 0, 0, 0, 109, 1890, 0, 100, 100, 100, 598, 0, 0, 96,
    /* 2184 */ 0, 100, 598, 6656, 68, 0, 0, 2560, 0, 0, 0, 0, 0, 3072, 0, 0, 0, 116, 0, 0, 119, 0, 0, 100, 636, 636,
    /* 2210 */ 636, 636, 0, 0, 135, 100, 636, 636, 636, 636, 0, 0, 0, 0, 0, 110, 0, 112, 5632, 5632, 5632, 5632, 821, 0,
    /* 2234 */ 0, 0, 100, 567, 100, 615, 0, 0, 1890, 0, 100, 100, 100, 598, 0, 0, 96, 0, 101, 598, 0, 68, 0, 2376, 2376,
    /* 2259 */ 0, 0, 57, 74, 5944, 56, 74, 56, 56, 74, 56, 5962, 74, 5962, 5962, 56, 74, 5962, 74, 74, 74, 5962, 74, 74,
    /* 2283 */ 74, 74, 0, 0, 56, 598, 35072, 598, 0, 0, 96, 0, 0, 0, 101, 101, 101, 625, 0, 136, 638, 0, 0, 0, 0, 0,
    /* 2309 */ 144, 148, 0, 100, 110, 144, 147, 147, 0, 649, 136, 0, 0, 96, 0, 102, 598, 0, 68, 0, 2376, 2376, 0, 64, 0,
    /* 2334 */ 0, 172, 165, 147, 165, 165, 165, 0, 0, 97, 0, 100, 598, 0, 68, 0, 2376, 2376, 0, 0, 0, 0, 5632, 5632, 0,
    /* 2359 */ 5632, 5632, 5632, 5632, 5632, 5632, 5632, 172, 0, 0, 0, 0, 0, 0, 0, 4352, 0, 68, 0, 0, 2560, 2154, 0, 0,
    /* 2383 */ 0, 143, 144, 134, 0, 100, 0, 1890, 2147, 100, 100, 100, 598, 0, 0, 101, 637, 637, 636, 637, 0, 0, 0, 0,
    /* 2407 */ 0, 0, 133, 110, 0, 100, 0, 2154, 0, 0, 119, 0, 122, 100, 636, 636, 636, 636, 0, 0, 144, 163, 163, 163,
    /* 2431 */ 163, 163, 168, 0, 6400, 0, 0, 57, 0, 0, 0, 144, 144, 174, 177, 144, 57, 0, 57, 57, 0, 0, 57, 57, 0, 0, 0,
    /* 2458 */ 0, 85, 598, 0, 68, 87, 0, 0, 0, 0, 0, 63, 64, 64, 64, 64, 64, 64, 64, 64, 0, 0, 0, 0, 67, 0, 0, 0, 66,
    /* 2487 */ 68, 0, 2376, 2376, 75, 76, 66, 76, 65, 0, 76, 81, 81, 76, 0, 0, 102, 636, 636, 636, 636, 0, 0, 130, 0, 0,
    /* 2513 */ 0, 0, 0, 10496, 63, 64, 82, 82, 82, 82, 81, 76, 82, 76, 76, 76, 84, 76, 76, 76, 81, 81, 81, 81, 0, 0, 0,
    /* 2540 */ 0, 96, 0, 0, 0, 0, 6912, 0, 68, 68, 0, 0, 0, 0, 0, 0, 110, 6912, 0, 96, 6912, 100, 598, 0, 68, 0, 2376,
    /* 2567 */ 2376, 0, 0, 1870, 0, 7936, 0, 0, 0, 0, 63, 64, 19712, 0, 100, 636, 636, 636, 636, 0, 0, 0, 0, 132, 0, 0,
    /* 2593 */ 0, 176, 145, 145, 164, 145, 144, 144, 148, 19860, 0, 100, 100, 0, 0, 117, 0, 119, 0, 0, 100, 636, 636,
    /* 2616 */ 636, 650, 0, 0, 173, 166, 166, 166, 19878, 166, 0, 0, 136, 638, 615, 638, 638, 0, 0, 0, 0, 0, 0, 134,
    /* 2640 */ 173, 0, 0, 0, 0, 0, 0, 0, 4608, 0, 67, 68, 0, 2376, 2376, 0, 0, 67, 67, 67, 67, 0, 8704, 0, 0, 0, 1084,
    /* 2667 */ 0, 0, 0, 0, 0, 1870, 0, 0, 0, 0, 7424, 7680, 144, 144, 144, 163, 144, 0, 0, 0, 0, 0, 0, 0, 1536, 64, 0,
    /* 2694 */ 69, 0, 2376, 2376, 0, 0, 8448, 0, 0, 0, 8448, 8448, 8448, 8448, 0, 0, 0, 0, 100, 598, 0, 68, 7168, 0, 0,
    /* 2719 */ 88, 89, 0, 0, 0, 144, 163, 0, 169, 0, 35072, 0, 94, 95, 96, 0, 0, 0, 144, 163, 168, 0, 0, 95, 7262, 96,
    /* 2745 */ 7262, 100, 598, 0, 88, 89, 0, 0, 2560, 0, 0, 0, 0, 119, 0, 0, 0, 171, 144, 163, 163, 163, 163, 163, 0, 0,
    /* 2771 */ 142, 0, 144, 134, 0, 100, 636, 0, 0, 0, 0, 0, 144, 147, 0, 636, 821, 821, 821, 821, 821, 821, 821, 821,
    /* 2795 */ 0, 0, 0, 68, 0, 0, 2560, 2154, 107, 107, 0, 0, 635, 636, 636, 636, 636, 0, 0, 0, 0, 0, 0, 0, 63, 1536, 0,
    /* 2822 */ 160, 0, 144, 163, 168, 0, 0, 0, 1084, 0, 0, 63, 64, 35072, 0, 0, 0, 96, 1890, 0, 0, 0, 2560, 0, 0, 0, 0,
    /* 2849 */ 96, 1890, 0, 0, 68, 0, 0, 2560, 0, 108, 0, 0, 0, 9728, 0, 0, 0, 0, 119, 110, 0, 112, 0, 9043, 9043, 9043,
    /* 2875 */ 77, 9043, 9043, 9043, 9043, 0, 0, 0, 0, 119, 0, 0, 100, 110, 0, 0, 100, 100, 100, 598, 0, 0, 635, 649,
    /* 2899 */ 649, 649, 649, 0, 0, 63, 0, 63, 0, 0, 0, 0, 8960, 0, 0, 0, 65, 0, 65, 0, 0, 0, 110, 100, 636, 636, 636,
    /* 2926 */ 636, 0, 0, 0, 131, 0, 0, 0, 144, 165, 0, 0, 0, 118, 119, 0, 0, 100, 636, 636, 636, 636, 139, 1870, 0, 0,
    /* 2952 */ 0, 1870, 0, 0, 0, 145, 164, 0, 0, 0, 100, 100, 100, 567, 0, 1870, 1870, 1870, 1870, 0, 0, 0, 0, 119, 120,
    /* 2977 */ 0, 100, 35072, 0, 0, 0, 96, 0, 0, 9216, 140, 0, 0, 0, 144, 134, 0, 100, 636, 0, 0, 0, 0, 155, 144, 144,
    /* 3003 */ 110, 110, 0, 100, 663, 0, 0, 64, 0, 64, 0, 0, 0, 68, 68, 0, 91, 0, 0, 68, 70, 2376, 2376, 821, 821, 0, 0,
    /* 3030 */ 821, 821, 0, 0, 0, 0, 0, 0, 0, 5120, 0, 0, 68, 71, 2376, 2376, 821, 821, 0, 0, 3584, 0, 0, 0, 0, 3584,
    /* 3056 */ 3584, 3584, 0, 3584, 3584, 3584, 3584, 0, 0, 0, 0, 56, 56, 56, 0, 68, 104, 0, 2560, 2154, 0, 0, 0, 156,
    /* 3080 */ 157, 0, 0, 0, 0, 163, 0, 0, 0, 0, 567, 567, 567, 1024, 0, 1890, 0, 100, 100, 100, 598, 114, 636, 0, 129,
    /* 3105 */ 0, 0, 0, 0, 0, 58, 0, 0, 100, 636, 0, 153, 0, 0, 0, 144, 144, 144, 163, 144, 144, 144, 134, 134, 0, 100,
    /* 3131 */ 100, 159, 115, 0, 0, 0, 119, 0, 0, 100, 636, 0, 0, 0, 154, 0, 144, 163, 163, 163, 163, 163, 110, 636,
    /* 3155 */ 128, 0, 0, 0, 0, 0, 0, 10752, 0, 9984, 0, 0, 144, 144, 144, 163, 144, 144, 134, 134, 0, 100, 0, 0, 0,
    /* 3180 */ 163, 144, 172, 165, 174, 68, 0, 105, 2560, 0, 0, 0, 0, 119, 121, 0, 100, 0, 1890, 0, 100, 100, 112, 598,
    /* 3204 */ 0, 0, 8192, 68, 8704, 0, 0, 0, 144, 163, 0, 0, 170, 144, 156, 134, 134, 0, 100, 100, 0, 0, 9312, 0, 100,
    /* 3229 */ 598, 0, 68, 0, 2376, 2376, 0, 63, 0, 0, 144, 163, 163, 163, 163, 175, 0, 0, 9472, 0, 0, 0, 63, 64, 100,
    /* 3254 */ 636, 152, 0, 0, 0, 0, 144, 144, 174, 175, 144, 10240, 0, 0, 144, 163, 0, 0, 0, 156, 162, 0, 0, 0, 0, 567,
    /* 3280 */ 567, 567, 1084, 639, 0, 0, 0, 0, 0, 0, 0, 79, 0, 79, 0, 146, 163, 163, 163, 163, 163, 0, 62, 821, 821, 0,
    /* 3306 */ 0, 0, 0, 133, 134, 0, 100, 35165, 0, 0, 0, 96, 1890, 99, 0, 68, 0, 2376, 2376, 0, 77, 0, 9040, 77, 77,
    /* 3331 */ 77, 77, 9040, 111, 1890, 0, 100, 100, 100, 598, 0, 68, 0, 2376, 2376, 0, 5632, 0, 0, 96, 0, 100, 615, 0,
    /* 3355 */ 68, 0, 2376, 2376, 0, 0, 59, 59, 0, 0, 0, 0, 119, 0, 0, 101, 0, 141, 0, 0, 144, 134, 0, 100, 636, 0, 0,
    /* 3382 */ 2957, 0, 0, 144, 163, 163, 163, 163, 163, 0, 144, 144, 134, 134, 158, 100, 100, 0, 68, 0, 2376, 2376,
    /* 3404 */ 821, 821, 0, 0, 161, 144, 163, 0, 0, 0, 144, 166, 0, 0, 0, 144, 163, 0, 0, 0, 0, 8448, 0, 8448, 0, 0,
    /* 3430 */ 10752, 0, 10752, 0, 0, 10752, 10752, 10752, 0, 10752, 10752, 10752, 10752, 10752, 10752, 10752, 10752, 0,
    /* 3448 */ 0, 0, 0, 146, 134, 0, 100, 3072, 3072, 3072, 3072, 3072, 3072, 3072, 3072, 0, 0, 3072, 3072, 3072, 3072,
    /* 3469 */ 0, 0, 0, 0
  };

  private static final int[] EXPECTED =
  {
    /*   0 */ 89, 99, 156, 96, 103, 110, 114, 118, 122, 126, 130, 134, 138, 142, 150, 160, 164, 167, 106, 179, 187, 203,
    /*  22 */ 167, 191, 200, 144, 207, 215, 146, 213, 216, 144, 211, 215, 145, 212, 221, 217, 225, 223, 214, 221, 225,
    /*  43 */ 221, 194, 234, 255, 258, 229, 233, 238, 170, 242, 173, 175, 246, 248, 252, 183, 264, 182, 196, 182, 182,
    /*  64 */ 153, 182, 92, 182, 182, 182, 180, 182, 92, 182, 182, 182, 180, 182, 91, 181, 182, 182, 180, 91, 182, 182,
    /*  86 */ 262, 182, 182, 4, 16777216, 0, 0, 0, 64, 128, 34, 134219778, 65538, 33554434, 1073741826, 2, 262152,
    /* 103 */ -2080374782, 24, 8, 1920, 8388610, 805306368, 268435456, 1920, 813697030, 270538790, -2046820286,
    /* 114 */ 278927398, 138510374, 2138278, 278927398, -1809835994, -1809835930, 278943782, 2203814, 952207398,
    /* 123 */ 948013094, 952141862, 952207398, -1776281498, 278984870, 952862758, 279050406, 986417254, 280098982,
    /* 132 */ 312604838, 313653414, -1125410714, 280754342, 313260198, 314308774, -1800668954, -1799620378, -1767114522,
    /* 141 */ -1766065946, 4, 0, 2, 2, 2, 0, 0, 1024, 33554432, 1073741824, 0, 8, 0, 0, 8, 8, 38, 134217734, 262144, 0,
    /* 162 */ 32, 32, 134217728, 67108864, -2147483648, 16, 0, 0, 512, 0, 0, 512, 17, 524, 513, 524, 524, 4096, 64, 128,
    /* 182 */ 0, 0, 0, 0, 16, 335544320, 939524096, 4096, 1006632960, 1152, 805306368, 268435456, 4096, 0, 0, 2, 64,
    /* 199 */ 128, 4096, 64, 128, 2, 2, 33554432, 16, 0, 1152, 268435456, 536870912, 0, 1024, 268435456, 536870912,
    /* 215 */ 4096, 4096, 4096, 2, 2, 0, 4096, 4096, 4096, 4096, 0, 268435456, 536870912, 4096, 1, 0, 2, 192, 0, 32, 0,
    /* 236 */ 0, 16, 17, 0, 512, 12, 524, 0, 512, 512, 524, 513, 525, 525, 525, 541, 541, 0, 2, 0, 2, 256, 0, 4, 0, 512,
    /* 262 */ 0, 64, 0, 0, 256, 0
  };

  private static final String[] TOKEN =
  {
    "(0)",
    "Whitespace",
    "Name",
    "Space",
    "DirPIContents",
    "StringLiteral",
    "CaretName",
    "CharCode",
    "Char",
    "CharRange",
    "CharCodeRange",
    "EOF",
    "EquivalenceLookAhead",
    "'$'",
    "'&'",
    "'('",
    "')'",
    "'*'",
    "'*/'",
    "'+'",
    "'-'",
    "'.'",
    "'/'",
    "'/*'",
    "':'",
    "'::='",
    "'<<'",
    "'<?'",
    "'<?ENCORE?>'",
    "'<?TOKENS?>'",
    "'=='",
    "'>>'",
    "'?'",
    "'?>'",
    "'['",
    "'[^'",
    "'\\\\'",
    "']'",
    "'definition'",
    "'explicit'",
    "'ws'",
    "'|'"
  };
}

// End
