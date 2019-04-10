// This file was generated on Tue Apr 9, 2019 13:18 (UTC+02) by REx v5.49 which is Copyright (c) 1979-2019 by Gunther Rademacher <grd@gmx.net>
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

  private int matchW(int set)
  {
    int code;
    for (;;)
    {
      code = match(set);
      if (code != 1)                // Whitespace
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

  private void lookahead2W(int set)
  {
    if (l2 == 0)
    {
      l2 = matchW(set);
      b2 = begin;
      e2 = end;
    }
    lk = (l2 << 6) | l1;
  }

  private void lookahead3W(int set)
  {
    if (l3 == 0)
    {
      l3 = matchW(set);
      b3 = begin;
      e3 = end;
    }
    lk |= l3 << 12;
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
      int i0 = (i >> 5) * 150 + s - 1;
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
    /*   17 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2766, 1696,
    /*   34 */ 1706, 1698, 1698, 1698, 1742, 2121, 2672, 1728, 2197, 2676, 2601, 2448, 1729, 2983, 2406, 1755, 1729,
    /*   51 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2922, 1738, 1713, 1698,
    /*   68 */ 1698, 1698, 1742, 1729, 2672, 1728, 2896, 2676, 2601, 2448, 1729, 2307, 2406, 1755, 1729, 1729, 1729,
    /*   85 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2766, 1696, 1720, 1698, 1698, 1698,
    /*  102 */ 1742, 2244, 2672, 1750, 2911, 3116, 2601, 3135, 1729, 2983, 2406, 1755, 1729, 1729, 1729, 1729, 1729,
    /*  119 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1798, 1729, 1729, 1729, 1729, 2512,
    /*  136 */ 2672, 1750, 2946, 3116, 2601, 3017, 1729, 1889, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  153 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2815, 3065, 1763, 1763, 1763, 1767, 2125, 2672, 1750,
    /*  170 */ 2946, 3116, 2601, 3017, 1729, 1889, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  187 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2952, 2863, 1946, 2869, 2874, 2512, 2672, 1779, 1792, 3116,
    /*  204 */ 2601, 3017, 1812, 2708, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  221 */ 1729, 1729, 1729, 1729, 1729, 1798, 1824, 1784, 1830, 1835, 2512, 2672, 1750, 2946, 3116, 2601, 3017,
    /*  238 */ 1729, 1889, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  255 */ 1729, 1729, 1729, 1798, 3120, 3121, 1847, 1852, 2512, 2672, 1750, 2946, 3116, 2601, 3017, 1729, 1889,
    /*  272 */ 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  289 */ 2843, 1864, 2251, 2251, 2251, 2255, 2278, 2672, 1750, 2946, 3116, 2601, 3017, 1729, 1889, 2406, 2342,
    /*  306 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1798,
    /*  323 */ 1897, 1904, 1909, 1913, 2512, 2672, 1750, 2946, 3116, 2601, 3017, 1729, 1889, 2406, 2342, 1729, 1729,
    /*  340 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1771, 1798, 1925, 1932,
    /*  357 */ 1938, 1942, 2512, 2672, 1750, 2946, 3116, 2601, 3017, 1729, 1889, 2406, 2342, 1729, 1729, 1729, 1729,
    /*  374 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2677, 1729, 1798, 1729, 2878, 1954, 1958,
    /*  391 */ 2512, 2672, 1966, 1979, 3116, 2730, 3094, 2001, 3003, 3051, 2581, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  408 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1798, 1729, 1839, 1993, 1997, 2512, 2672,
    /*  425 */ 1750, 2946, 3116, 2601, 3017, 1729, 1889, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  442 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1798, 1729, 1729, 2009, 2013, 2512, 2229, 2021, 2946,
    /*  459 */ 2044, 2051, 2085, 2931, 1889, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  476 */ 1729, 1729, 1729, 1729, 1729, 1729, 2107, 2741, 2736, 2742, 2133, 2512, 2672, 1750, 2946, 3116, 2146,
    /*  493 */ 3017, 1729, 1889, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  510 */ 1729, 1729, 1971, 2639, 2646, 2165, 2173, 2179, 2184, 2607, 2672, 2192, 2946, 3116, 2660, 1871, 2368,
    /*  527 */ 1889, 2502, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  544 */ 1729, 1729, 1798, 1729, 1729, 1729, 2891, 2512, 2672, 2210, 2946, 2205, 2223, 2237, 2524, 1889, 2267,
    /*  561 */ 2778, 2275, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2286, 1730,
    /*  578 */ 1985, 1729, 2026, 2299, 2303, 2315, 2672, 1750, 2946, 3116, 2601, 2979, 1729, 1889, 2406, 1729, 1729,
    /*  595 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2666, 2323, 2331,
    /*  612 */ 2350, 2356, 2364, 2512, 2376, 1750, 2391, 3116, 2601, 3017, 1729, 1889, 2406, 2342, 1729, 1729, 1729,
    /*  629 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1917, 1729, 1798, 1729, 1729, 1729,
    /*  646 */ 1729, 2414, 2672, 1750, 3109, 3116, 2601, 1878, 2259, 1889, 2406, 2342, 1729, 1729, 1729, 1729, 1729,
    /*  663 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2422, 2036, 2031, 2031, 2429, 2512,
    /*  680 */ 2443, 1750, 3080, 2338, 2601, 3017, 1729, 1889, 2406, 2342, 2057, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  697 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2622, 2624, 2456, 1729, 2063, 2069, 2073, 2512, 2464, 2477,
    /*  714 */ 2490, 2498, 2601, 3017, 1729, 1889, 2406, 2342, 2510, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  731 */ 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2215, 2946, 2205,
    /*  748 */ 2223, 2237, 2524, 1889, 2267, 2778, 2275, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  765 */ 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2215, 2946, 2205, 2223, 2237,
    /*  782 */ 2562, 1889, 2267, 2778, 2275, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  799 */ 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2215, 2946, 2570, 2223, 2237, 2524, 2469,
    /*  816 */ 2267, 2114, 2275, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523,
    /*  833 */ 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2589, 2946, 3116, 2146, 3017, 1729, 1889, 2406, 2342,
    /*  850 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532,
    /*  867 */ 2551, 2551, 2551, 2554, 2512, 2672, 2589, 2946, 3116, 2146, 3017, 1885, 1889, 2406, 2342, 1729, 1729,
    /*  884 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551,
    /*  901 */ 2551, 2554, 2512, 2672, 2589, 2946, 3116, 2146, 2615, 1729, 1889, 2482, 2342, 1729, 1729, 1729, 1729,
    /*  918 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554,
    /*  935 */ 2512, 2672, 2589, 2946, 3116, 2146, 2632, 1729, 2092, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  952 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672,
    /*  969 */ 2589, 2946, 3116, 2146, 3017, 1729, 1889, 2099, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /*  986 */ 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2589, 2946,
    /* 1003 */ 3116, 2146, 3017, 1729, 1889, 2406, 2157, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1020 */ 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2589, 2946, 2654, 2146,
    /* 1037 */ 3017, 1729, 1889, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1054 */ 1729, 1729, 1729, 2152, 2685, 2691, 2138, 2699, 2704, 2512, 2672, 1750, 2946, 3116, 2601, 3017, 1729,
    /* 1071 */ 1889, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1088 */ 1814, 1729, 2397, 1729, 1816, 1729, 2402, 2716, 2672, 1750, 2946, 3116, 2601, 3017, 1729, 1889, 2406,
    /* 1105 */ 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2539, 1729,
    /* 1122 */ 2917, 1729, 1729, 1729, 1729, 2512, 2672, 1728, 2291, 2380, 2724, 2753, 2383, 1889, 2406, 2342, 2750,
    /* 1139 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2800, 1803,
    /* 1156 */ 1804, 2761, 2774, 2512, 2672, 2786, 2794, 3116, 2601, 3017, 1729, 1889, 2406, 2342, 1729, 1729, 1729,
    /* 1173 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551,
    /* 1190 */ 2554, 2512, 2672, 2215, 2946, 2205, 2223, 2237, 2524, 2808, 2267, 2778, 2275, 1729, 1729, 1729, 1729,
    /* 1207 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2836, 2551, 2551, 2551, 2554, 2512,
    /* 1224 */ 2672, 2215, 2946, 2205, 2223, 2237, 2524, 1889, 2267, 2778, 2275, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1241 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2856, 2551, 2551, 2551, 2554, 2512, 2595, 2215,
    /* 1258 */ 2946, 2205, 2223, 2237, 2524, 1889, 2267, 2778, 2275, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1275 */ 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2215, 2946, 2886,
    /* 1292 */ 2223, 2237, 2524, 1889, 2267, 2778, 2275, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1309 */ 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2589, 2946, 3116, 2904, 3017,
    /* 1326 */ 2930, 2939, 2965, 2973, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1343 */ 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2589, 2946, 3116, 2991, 3017, 1729, 1889,
    /* 1360 */ 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523,
    /* 1377 */ 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2589, 2946, 3116, 2146, 3039, 1729, 1889, 2406, 2342,
    /* 1394 */ 2577, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532,
    /* 1411 */ 2551, 2551, 2551, 2554, 2512, 2672, 2589, 2946, 3116, 2146, 3017, 1729, 1889, 2406, 2077, 1729, 1729,
    /* 1428 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551,
    /* 1445 */ 2551, 2554, 2512, 2672, 2589, 2946, 2999, 2146, 3017, 1729, 1889, 2406, 2342, 1729, 1729, 1729, 1729,
    /* 1462 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554,
    /* 1479 */ 2848, 2672, 2589, 2946, 3116, 3011, 3017, 1729, 1889, 2543, 2342, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1496 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2672,
    /* 1513 */ 2589, 2946, 3116, 2146, 3017, 1729, 1889, 3025, 3033, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1530 */ 1729, 1729, 1729, 1729, 1729, 1729, 2523, 3047, 2532, 2551, 2551, 2551, 2554, 2512, 2672, 2589, 3059,
    /* 1547 */ 3116, 2146, 3017, 1729, 3161, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1564 */ 1729, 1729, 1729, 1729, 2523, 2520, 2532, 2551, 2551, 2551, 2554, 2512, 2435, 3073, 2946, 3116, 3088,
    /* 1581 */ 3017, 1729, 3102, 2406, 3129, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1598 */ 1729, 1729, 1729, 1856, 1798, 3143, 3148, 3153, 3157, 2512, 2672, 1750, 2946, 3116, 2601, 3017, 1729,
    /* 1615 */ 1889, 2406, 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1632 */ 1729, 1729, 1798, 1729, 1729, 1729, 2891, 2512, 2672, 1750, 2946, 3116, 2146, 3017, 1729, 1889, 2406,
    /* 1649 */ 2342, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 2820,
    /* 1666 */ 2957, 2825, 3169, 3169, 2828, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729,
    /* 1683 */ 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1729, 1084, 1084, 567, 567, 567,
    /* 1701 */ 567, 567, 567, 567, 567, 567, 1084, 1084, 0, 0, 567, 567, 567, 1024, 1024, 0, 0, 567, 567, 567, 1084,
    /* 1722 */ 1084, 2376, 2376, 567, 567, 567, 35072, 0, 0, 0, 0, 0, 0, 0, 0, 57, 1024, 1024, 567, 567, 567, 567, 567,
    /* 1745 */ 567, 0, 0, 567, 0, 35072, 0, 0, 0, 96, 0, 0, 0, 0, 0, 142, 0, 0, 63, 63, 63, 63, 63, 63, 63, 63, 0, 0, 0,
    /* 1774 */ 0, 0, 0, 4352, 0, 35072, 0, 0, 0, 97, 0, 0, 0, 0, 0, 3584, 0, 3584, 0, 0, 97, 0, 100, 86, 0, 68, 0, 2376,
    /* 1802 */ 2376, 0, 0, 0, 0, 0, 1870, 0, 0, 0, 0, 123, 0, 0, 0, 0, 0, 0, 59, 0, 0, 0, 0, 0, 3584, 0, 0, 0, 0, 3584,
    /* 1832 */ 3584, 3584, 0, 3584, 3584, 3584, 3584, 0, 0, 0, 0, 0, 0, 5120, 0, 0, 3840, 3840, 3840, 0, 3840, 3840,
    /* 1854 */ 3840, 3840, 0, 0, 0, 0, 0, 0, 10752, 0, 0, 68, 0, 2376, 2376, 0, 64, 0, 0, 0, 117, 118, 0, 100, 0, 0, 0,
    /* 1881 */ 117, 119, 0, 100, 0, 0, 0, 125, 0, 0, 0, 0, 0, 134, 127, 0, 0, 4096, 4096, 0, 0, 0, 0, 4096, 4096, 4096,
    /* 1907 */ 4096, 0, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 0, 0, 0, 0, 0, 58, 0, 0, 0, 4352, 0, 0, 0, 0, 0,
    /* 1932 */ 4352, 4352, 0, 4352, 0, 0, 4352, 4352, 4352, 4352, 4352, 4352, 4352, 4352, 0, 0, 0, 0, 0, 79, 0, 79,
    /* 1954 */ 4608, 0, 0, 0, 4608, 4608, 4608, 4608, 0, 0, 0, 85, 35072, 6229, 0, 0, 96, 0, 0, 0, 0, 56, 56, 56, 0, 0,
    /* 1980 */ 0, 96, 0, 101, 86, 0, 68, 0, 2376, 2376, 0, 0, 57, 5120, 0, 0, 0, 5120, 5120, 5120, 5120, 0, 0, 0, 0, 0,
    /* 2006 */ 126, 0, 0, 0, 5376, 0, 5376, 0, 5376, 0, 5376, 821, 0, 0, 0, 35164, 0, 0, 0, 96, 0, 0, 0, 0, 57, 0, 0, 0,
    /* 2034 */ 0, 67, 0, 0, 0, 0, 67, 67, 0, 0, 68, 0, 0, 2560, 0, 0, 0, 108, 1890, 0, 100, 100, 100, 0, 0, 0, 0, 7424,
    /* 2062 */ 7680, 0, 0, 0, 0, 8448, 0, 8448, 0, 0, 0, 8448, 8448, 8448, 8448, 0, 0, 0, 0, 0, 134, 0, 148, 114, 0, 0,
    /* 2088 */ 117, 0, 0, 100, 0, 0, 0, 132, 0, 134, 127, 0, 0, 0, 140, 0, 134, 134, 134, 0, 68, 0, 2376, 2376, 0, 5632,
    /* 2114 */ 0, 0, 0, 145, 0, 134, 147, 0, 0, 0, 1084, 0, 0, 0, 0, 0, 0, 1536, 64, 5632, 5632, 5632, 5632, 821, 0, 0,
    /* 2140 */ 0, 0, 77, 9043, 0, 9043, 0, 1890, 0, 100, 100, 100, 0, 0, 0, 0, 8960, 0, 0, 0, 0, 146, 134, 0, 0, 74,
    /* 2166 */ 5944, 56, 74, 56, 56, 74, 56, 5962, 74, 5962, 5962, 56, 74, 5962, 74, 74, 74, 5962, 74, 74, 74, 74, 0, 0,
    /* 2190 */ 56, 86, 35072, 86, 0, 0, 96, 0, 0, 0, 0, 85, 86, 0, 68, 68, 0, 0, 2560, 2153, 0, 0, 0, 0, 96, 0, 0, 0, 0,
    /* 2219 */ 96, 1890, 0, 0, 0, 1890, 2147, 100, 100, 100, 0, 0, 0, 68, 68, 0, 0, 92, 2153, 0, 0, 117, 0, 120, 100, 0,
    /* 2245 */ 0, 0, 1084, 0, 0, 63, 64, 64, 64, 64, 64, 64, 64, 64, 0, 0, 0, 0, 0, 0, 19712, 0, 0, 0, 2947, 0, 0, 134,
    /* 2273 */ 134, 134, 0, 0, 147, 0, 0, 0, 0, 0, 0, 63, 1536, 0, 6400, 0, 0, 57, 0, 0, 0, 0, 100, 86, 0, 68, 57, 0,
    /* 2301 */ 57, 57, 0, 0, 57, 57, 0, 0, 0, 0, 0, 126, 109, 0, 87, 0, 0, 0, 0, 0, 63, 64, 66, 68, 0, 2376, 2376, 75,
    /* 2329 */ 76, 66, 76, 65, 0, 76, 81, 81, 76, 0, 0, 0, 2560, 0, 0, 0, 0, 0, 134, 0, 0, 82, 82, 82, 82, 81, 76, 82,
    /* 2357 */ 76, 76, 76, 84, 76, 76, 76, 81, 81, 81, 81, 0, 0, 0, 0, 0, 127, 0, 0, 0, 6912, 0, 68, 68, 0, 0, 0, 0, 0,
    /* 2386 */ 0, 109, 0, 0, 109, 6912, 0, 96, 6912, 100, 86, 0, 68, 0, 2376, 2376, 0, 0, 59, 59, 0, 0, 0, 0, 0, 134,
    /* 2412 */ 134, 134, 0, 7936, 0, 0, 0, 0, 63, 64, 67, 68, 0, 2376, 2376, 0, 0, 67, 67, 67, 67, 0, 8704, 0, 0, 0, 68,
    /* 2439 */ 68, 0, 91, 0, 0, 0, 8192, 68, 8704, 0, 0, 0, 0, 109, 0, 111, 0, 0, 69, 0, 2376, 2376, 0, 0, 8448, 7168,
    /* 2465 */ 0, 0, 88, 89, 0, 0, 0, 0, 133, 134, 127, 0, 35072, 0, 94, 95, 96, 0, 0, 0, 0, 141, 134, 134, 134, 95,
    /* 2491 */ 7262, 96, 7262, 100, 86, 0, 88, 89, 0, 0, 2560, 0, 0, 0, 0, 0, 134, 109, 134, 149, 150, 0, 0, 0, 0, 0, 0,
    /* 2518 */ 63, 64, 0, 0, 821, 821, 0, 0, 0, 0, 0, 0, 0, 120, 0, 68, 0, 2376, 2376, 821, 821, 0, 0, 0, 9728, 0, 0, 0,
    /* 2546 */ 0, 0, 134, 134, 142, 821, 821, 821, 821, 821, 821, 821, 821, 0, 0, 0, 0, 0, 124, 0, 0, 0, 0, 120, 68, 0,
    /* 2572 */ 0, 2560, 2153, 106, 106, 0, 0, 0, 9984, 0, 0, 0, 0, 0, 135, 0, 0, 35072, 0, 0, 0, 96, 1890, 0, 0, 0, 68,
    /* 2599 */ 68, 90, 0, 0, 0, 100, 100, 100, 0, 0, 0, 0, 4864, 0, 63, 64, 0, 115, 0, 117, 0, 0, 100, 0, 0, 54, 0, 0,
    /* 2627 */ 0, 0, 0, 0, 8448, 0, 0, 116, 117, 0, 0, 100, 0, 0, 56, 56, 56, 56, 56, 56, 68, 0, 2376, 2376, 74, 56, 56,
    /* 2654 */ 68, 0, 0, 2560, 0, 107, 0, 0, 0, 100, 567, 100, 0, 0, 0, 65, 0, 65, 0, 0, 0, 68, 68, 0, 0, 0, 0, 0, 0, 0,
    /* 2684 */ 61, 0, 68, 0, 2376, 2376, 0, 77, 0, 9040, 77, 77, 77, 77, 9040, 0, 9043, 9043, 9043, 77, 9043, 9043,
    /* 2706 */ 9043, 9043, 0, 0, 0, 0, 0, 134, 127, 137, 0, 0, 9472, 0, 0, 0, 63, 64, 109, 0, 0, 100, 100, 100, 0, 0, 0,
    /* 2733 */ 101, 101, 101, 0, 0, 0, 0, 5632, 5632, 0, 5632, 5632, 5632, 5632, 5632, 5632, 5632, 0, 0, 109, 0, 0, 0,
    /* 2756 */ 0, 0, 0, 100, 0, 1870, 0, 0, 0, 1870, 0, 0, 0, 0, 567, 567, 567, 1084, 1870, 1870, 1870, 1870, 0, 0, 0,
    /* 2781 */ 0, 0, 134, 147, 0, 35072, 0, 0, 0, 96, 0, 0, 9216, 0, 0, 9312, 0, 100, 86, 0, 68, 0, 2376, 2376, 0, 0,
    /* 2807 */ 1870, 0, 130, 0, 0, 0, 134, 127, 0, 0, 63, 0, 63, 0, 0, 0, 0, 0, 3072, 0, 0, 3072, 3072, 3072, 3072, 0,
    /* 2833 */ 0, 0, 0, 0, 68, 70, 2376, 2376, 821, 821, 0, 0, 64, 0, 64, 0, 0, 0, 0, 0, 10496, 63, 64, 0, 68, 71, 2376,
    /* 2860 */ 2376, 821, 821, 0, 0, 79, 0, 0, 0, 0, 79, 79, 79, 0, 79, 79, 79, 79, 0, 0, 0, 0, 0, 0, 4608, 0, 68, 103,
    /* 2888 */ 0, 2560, 2153, 0, 0, 0, 0, 821, 0, 0, 0, 0, 85, 567, 0, 68, 0, 1890, 0, 100, 100, 100, 112, 0, 0, 96, 0,
    /* 2915 */ 85, 86, 0, 68, 0, 0, 9728, 0, 0, 0, 0, 567, 567, 567, 1024, 122, 0, 0, 0, 0, 0, 0, 0, 128, 129, 0, 0, 0,
    /* 2943 */ 0, 134, 127, 0, 0, 96, 0, 100, 86, 0, 68, 0, 73, 73, 0, 0, 0, 0, 0, 3072, 3072, 0, 0, 139, 0, 0, 0, 134,
    /* 2971 */ 134, 134, 0, 144, 0, 0, 0, 134, 0, 0, 0, 117, 0, 0, 0, 0, 0, 126, 127, 0, 0, 1890, 0, 100, 100, 100, 0,
    /* 2998 */ 113, 68, 0, 104, 2560, 0, 0, 0, 0, 0, 135, 127, 0, 0, 1890, 0, 100, 100, 111, 0, 0, 0, 117, 0, 0, 100, 0,
    /* 3025 */ 138, 0, 0, 0, 0, 134, 134, 134, 0, 0, 10240, 0, 0, 134, 0, 0, 0, 117, 0, 0, 100, 121, 0, 62, 821, 821, 0,
    /* 3052 */ 0, 0, 0, 0, 135, 135, 135, 0, 0, 96, 0, 102, 86, 0, 68, 0, 2376, 2376, 0, 63, 0, 35165, 0, 0, 0, 96,
    /* 3078 */ 1890, 99, 0, 0, 96, 0, 100, 86, 0, 66816, 110, 1890, 0, 100, 100, 100, 0, 0, 0, 117, 0, 0, 101, 0, 0, 0,
    /* 3104 */ 131, 0, 0, 134, 127, 0, 0, 96, 0, 100, 86, 6656, 68, 0, 0, 2560, 0, 0, 0, 0, 0, 0, 3840, 0, 3840, 143, 0,
    /* 3131 */ 0, 0, 0, 134, 0, 0, 0, 117, 109, 0, 111, 0, 0, 10752, 0, 10752, 0, 0, 10752, 10752, 10752, 0, 10752,
    /* 3154 */ 10752, 10752, 10752, 10752, 10752, 10752, 10752, 0, 0, 0, 0, 0, 136, 127, 0, 3072, 3072, 3072, 3072,
    /* 3173 */ 3072, 3072, 3072, 3072
  };

  private static final int[] EXPECTED =
  {
    /*   0 */ 75, 88, 81, 85, 92, 105, 109, 113, 117, 121, 125, 129, 133, 137, 145, 152, 156, 163, 95, 183, 176, 169,
    /*  22 */ 163, 180, 193, 139, 212, 217, 101, 225, 100, 224, 100, 224, 231, 229, 213, 98, 196, 172, 141, 166, 235,
    /*  43 */ 199, 239, 243, 202, 247, 205, 208, 251, 186, 159, 186, 77, 185, 186, 147, 148, 187, 185, 186, 186, 184,
    /*  64 */ 186, 189, 186, 188, 186, 188, 186, 187, 185, 220, 220, 4, 16777216, 0, 0, 2, 64, 8, 8, 38, 134217734, 34,
    /*  86 */ 134219778, 65538, 33554434, 1073741826, 2, 262152, -2080374782, 24, 8, 1920, 8388610, 805306368,
    /*  98 */ 268435456, 536870912, 0, 0, 1024, 268435456, 536870912, 1920, 813697030, 270538790, -2046820286,
    /* 109 */ 278927398, 138510374, 2138278, 278927398, -1809835994, -1809835930, 278943782, 2203814, 952207398,
    /* 118 */ 948013094, 952141862, 952207398, -1776281498, 278984870, 952862758, 279050406, 986417254, 280098982,
    /* 127 */ 312604838, 313653414, -1125410714, 280754342, 313260198, 314308774, -1800668954, -1799620378, -1767114522,
    /* 136 */ -1766065946, 4, 0, 2, 2, 0, 0, 4, 0, 33554432, 1073741824, 0, 8, 0, 0, 0, 262144, 0, 32, 32, 134217728,
    /* 157 */ 67108864, -2147483648, 16, 0, 0, 256, 16, 0, 0, 512, 1, 0, 2, 2, 33554432, 16, 0, 2, 256, 335544320,
    /* 177 */ 939524096, 4096, 1006632960, 1152, 805306368, 268435456, 4096, 64, 128, 0, 0, 0, 0, 64, 128, 0, 4096, 64,
    /* 195 */ 128, 2, 32, 0, 0, 17, 0, 512, 512, 17, 524, 524, 513, 525, 525, 525, 525, 1152, 268435456, 536870912,
    /* 215 */ 4096, 0, 4096, 4096, 2, 0, 64, 0, 0, 536870912, 4096, 4096, 4096, 4096, 536870912, 4096, 4096, 0, 0,
    /* 234 */ 268435456, 192, 0, 32, 0, 12, 512, 0, 0, 512, 524, 0, 512, 513, 524, 513, 524, 541, 541, 0, 2
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
