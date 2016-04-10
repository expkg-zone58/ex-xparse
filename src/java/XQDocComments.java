// This file was generated on Sun Apr 10, 2016 19:04 (UTC+01) by REx v5.38 which is Copyright (c) 1979-2016 by Gunther Rademacher <grd@gmx.net>
// REx command line: XQDocComments.ebnf -java -basex -tree

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

public class XQDocComments
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
      return offending < 0 ? "lexical analysis failed" : "syntax error";
    }

    public int getBegin() {return begin;}
    public int getEnd() {return end;}
    public int getState() {return state;}
    public int getOffending() {return offending;}
    public int getExpected() {return expected;}
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

  public static ANode parseComments(Str str) throws IOException
  {
    BaseXFunction baseXFunction = new BaseXFunction()
    {
      @Override
      public void execute(XQDocComments p) {p.parse_Comments();}
    };
    return baseXFunction.call(str);
  }

  public static abstract class BaseXFunction
  {
    protected abstract void execute(XQDocComments p);

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
      XQDocComments parser = new XQDocComments(input, treeBuilder);
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

  public XQDocComments(CharSequence string, EventHandler t)
  {
    initialize(string, t);
  }

  public void initialize(CharSequence string, EventHandler eh)
  {
    eventHandler = eh;
    input = string;
    size = input.length();
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
    if (e.getExpected() < 0)
    {
      expected = getTokenSet(- e.getState());
    }
    else
    {
      expected = new String[]{TOKEN[e.getExpected()]};
    }
    return expected;
  }

  public String getErrorMessage(ParseException e)
  {
    String[] tokenSet = getExpectedTokenSet(e);
    String found = getOffendingToken(e);
    String prefix = input.subSequence(0, e.getBegin()).toString();
    int line = prefix.replaceAll("[^\n]", "").length() + 1;
    int column = prefix.length() - prefix.lastIndexOf('\n');
    int size = e.getEnd() - e.getBegin();
    return e.getMessage()
         + (found == null ? "" : ", found " + found)
         + "\nwhile expecting "
         + (tokenSet.length == 1 ? tokenSet[0] : java.util.Arrays.toString(tokenSet))
         + "\n"
         + (size == 0 || found != null ? "" : "after successfully scanning " + size + " characters beginning ")
         + "at line " + line + ", column " + column + ":\n..."
         + input.subSequence(e.getBegin(), Math.min(input.length(), e.getBegin() + 64))
         + "...";
  }

  public void parse_Comments()
  {
    eventHandler.startNonterminal("Comments", e0);
    for (;;)
    {
      lookahead1(10);               // END | S | '(:' | '(:~'
      if (l1 == 1)                  // END
      {
        break;
      }
      switch (l1)
      {
      case 9:                       // S
        consume(9);                 // S
        break;
      case 12:                      // '(:'
        parse_Comment();
        break;
      default:
        parse_XQDocComment();
      }
    }
    eventHandler.endNonterminal("Comments", e0);
  }

  private void parse_Comment()
  {
    eventHandler.startNonterminal("Comment", e0);
    consume(12);                    // '(:'
    for (;;)
    {
      lookahead1(12);               // CommentContents | '(:' | ':)' | '@'
      if (l1 == 15)                 // ':)'
      {
        break;
      }
      switch (l1)
      {
      case 3:                       // CommentContents
        consume(3);                 // CommentContents
        break;
      case 12:                      // '(:'
        parse_Comment();
        break;
      default:
        consume(20);                // '@'
      }
    }
    consume(15);                    // ':)'
    eventHandler.endNonterminal("Comment", e0);
  }

  private void parse_XQDocComment()
  {
    eventHandler.startNonterminal("XQDocComment", e0);
    consume(13);                    // '(:~'
    parse_Contents();
    for (;;)
    {
      if (l1 != 20)                 // '@'
      {
        break;
      }
      parse_TaggedContents();
    }
    consume(15);                    // ':)'
    eventHandler.endNonterminal("XQDocComment", e0);
  }

  private void parse_Contents()
  {
    eventHandler.startNonterminal("Contents", e0);
    for (;;)
    {
      lookahead1(14);               // Char | Trim | ':)' | '<' | '@'
      if (l1 == 15                  // ':)'
       || l1 == 20)                 // '@'
      {
        break;
      }
      switch (l1)
      {
      case 4:                       // Char
        consume(4);                 // Char
        break;
      case 5:                       // Trim
        consume(5);                 // Trim
        break;
      default:
        parse_DirElemConstructor();
      }
    }
    eventHandler.endNonterminal("Contents", e0);
  }

  private void parse_TaggedContents()
  {
    eventHandler.startNonterminal("TaggedContents", e0);
    consume(20);                    // '@'
    lookahead1(0);                  // Tag
    consume(2);                     // Tag
    parse_Contents();
    eventHandler.endNonterminal("TaggedContents", e0);
  }

  private void parse_DirElemConstructor()
  {
    eventHandler.startNonterminal("DirElemConstructor", e0);
    consume(16);                    // '<'
    lookahead1(0);                  // Tag
    consume(2);                     // Tag
    for (;;)
    {
      lookahead1(9);                // S | '/>' | '>'
      if (l1 != 9)                  // S
      {
        break;
      }
      consume(9);                   // S
      lookahead1(11);               // Tag | S | '/>' | '>'
      if (l1 == 2)                  // Tag
      {
        parse_DirAttrConstructor();
      }
    }
    switch (l1)
    {
    case 14:                        // '/>'
      consume(14);                  // '/>'
      break;
    default:
      consume(19);                  // '>'
      for (;;)
      {
        lookahead1(13);             // Trim | ElementContentChar | '<' | '</'
        if (l1 == 17)               // '</'
        {
          break;
        }
        switch (l1)
        {
        case 16:                    // '<'
          parse_DirElemConstructor();
          break;
        case 6:                     // ElementContentChar
          consume(6);               // ElementContentChar
          break;
        default:
          consume(5);               // Trim
        }
      }
      consume(17);                  // '</'
      lookahead1(0);                // Tag
      consume(2);                   // Tag
      lookahead1(4);                // S | '>'
      if (l1 == 9)                  // S
      {
        consume(9);                 // S
      }
      lookahead1(2);                // '>'
      consume(19);                  // '>'
    }
    eventHandler.endNonterminal("DirElemConstructor", e0);
  }

  private void parse_DirAttrConstructor()
  {
    eventHandler.startNonterminal("DirAttrConstructor", e0);
    consume(2);                     // Tag
    lookahead1(3);                  // S | '='
    if (l1 == 9)                    // S
    {
      consume(9);                   // S
    }
    lookahead1(1);                  // '='
    consume(18);                    // '='
    lookahead1(8);                  // S | '"' | "'"
    if (l1 == 9)                    // S
    {
      consume(9);                   // S
    }
    parse_DirAttributeValue();
    eventHandler.endNonterminal("DirAttrConstructor", e0);
  }

  private void parse_DirAttributeValue()
  {
    eventHandler.startNonterminal("DirAttributeValue", e0);
    lookahead1(5);                  // '"' | "'"
    switch (l1)
    {
    case 10:                        // '"'
      consume(10);                  // '"'
      for (;;)
      {
        lookahead1(6);              // Trim | QuotAttrContentChar | '"'
        if (l1 == 10)               // '"'
        {
          break;
        }
        switch (l1)
        {
        case 7:                     // QuotAttrContentChar
          consume(7);               // QuotAttrContentChar
          break;
        default:
          consume(5);               // Trim
        }
      }
      consume(10);                  // '"'
      break;
    default:
      consume(11);                  // "'"
      for (;;)
      {
        lookahead1(7);              // Trim | AposAttrContentChar | "'"
        if (l1 == 11)               // "'"
        {
          break;
        }
        switch (l1)
        {
        case 8:                     // AposAttrContentChar
          consume(8);               // AposAttrContentChar
          break;
        default:
          consume(5);               // Trim
        }
      }
      consume(11);                  // "'"
    }
    eventHandler.endNonterminal("DirAttributeValue", e0);
  }

  private void consume(int t)
  {
    if (l1 == t)
    {
      eventHandler.terminal(TOKEN[l1], b1, e1);
      b0 = b1; e0 = e1; l1 = 0;
    }
    else
    {
      error(b1, e1, 0, l1, t);
    }
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

  private int     b0, e0;
  private int l1, b1, e1;
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

    for (int code = result & 31; code != 0; )
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
        charclass = MAP1[(c0 & 15) + MAP1[(c1 & 63) + MAP1[c1 >> 6]]];
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
          else
          {
            c0 = -1;
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
      int i0 = (charclass << 5) + code - 1;
      code = TRANSITION[(i0 & 3) + TRANSITION[i0 >> 2]];

      if (code > 31)
      {
        result = code;
        code &= 31;
        end = current;
      }
    }

    result >>= 5;
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
      for (int i = result >> 5; i > 0; --i)
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
      end -= result >> 5;
    }

    if (end > size) end = size;
    return (result & 31) - 1;
  }

  private static String[] getTokenSet(int tokenSetId)
  {
    java.util.ArrayList<String> expected = new java.util.ArrayList<>();
    int s = tokenSetId < 0 ? - tokenSetId : INITIAL[tokenSetId] & 31;
    for (int i = 0; i < 21; i += 32)
    {
      int j = i;
      int i0 = (i >> 5) * 28 + s - 1;
      int f = EXPECTED[i0];
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
    /*   0 */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 3, 4,
    /*  35 */ 3, 3, 3, 3, 5, 6, 7, 3, 3, 3, 3, 3, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 3, 11, 12, 13, 3, 14, 9, 9, 9, 9,
    /*  69 */ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 3, 3, 3, 3, 3, 3, 9, 9, 9, 9, 9, 9, 9,
    /* 104 */ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 3, 3, 3, 15, 3
  };

  private static final int[] MAP1 =
  {
    /*   0 */ 54, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62,
    /*  26 */ 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62, 62,
    /*  52 */ 62, 62, 133, 126, 149, 165, 216, 221, 195, 200, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180,
    /*  73 */ 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180,
    /*  94 */ 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180,
    /* 115 */ 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16,
    /* 143 */ 1, 0, 0, 2, 0, 0, 16, 3, 4, 3, 3, 3, 3, 5, 6, 7, 3, 3, 3, 3, 3, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 3,
    /* 177 */ 11, 12, 13, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
    /* 211 */ 3, 3, 3, 15, 3, 14, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 3, 3, 3, 3, 3
  };

  private static final int[] MAP2 =
  {
    /* 0 */ 57344, 65536, 65533, 1114111, 3, 3
  };

  private static final int[] INITIAL =
  {
    /*  0 */ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 75, 12, 13, 14, 15
  };

  private static final int[] TRANSITION =
  {
    /*   0 */ 155, 155, 155, 155, 155, 155, 155, 155, 196, 235, 184, 232, 136, 148, 146, 155, 196, 187, 184, 181, 136,
    /*  21 */ 148, 146, 155, 155, 174, 155, 139, 136, 148, 146, 155, 155, 164, 154, 139, 136, 148, 146, 155, 155, 203,
    /*  42 */ 206, 139, 136, 148, 146, 155, 155, 174, 150, 161, 168, 172, 178, 155, 155, 174, 155, 139, 156, 191, 157,
    /*  63 */ 155, 155, 174, 142, 139, 136, 245, 146, 155, 195, 174, 207, 139, 136, 148, 146, 155, 155, 174, 155, 200,
    /*  84 */ 211, 218, 215, 155, 155, 174, 155, 252, 136, 148, 146, 155, 255, 174, 155, 139, 136, 148, 146, 155, 220,
    /* 105 */ 225, 221, 139, 229, 148, 146, 155, 155, 174, 155, 239, 136, 148, 146, 155, 155, 174, 155, 139, 136, 148,
    /* 126 */ 243, 155, 196, 187, 184, 181, 249, 148, 146, 155, 1216, 0, 0, 22, 224, 160, 0, 18, 0, 18, 1216, 0, 22, 22,
    /* 150 */ 0, 0, 19, 0, 352, 0, 0, 0, 0, 22, 0, 20, 224, 160, 0, 352, 352, 288, 1216, 0, 0, 1179, 22, 1179, 0, 0,
    /* 176 */ 256, 288, 1216, 0, 1179, 22, 224, 160, 336, 336, 336, 336, 0, 256, 288, 512, 22, 0, 512, 97, 0, 0, 0, 336,
    /* 200 */ 21, 224, 184, 0, 384, 256, 384, 0, 0, 0, 97, 1241, 0, 442, 416, 1216, 0, 0, 1180, 1180, 0, 0, 640, 0, 640,
    /* 225 */ 640, 0, 256, 288, 1216, 480, 0, 22, 241, 17, 336, 0, 273, 305, 694, 224, 672, 0, 1216, 448, 22, 22, 576,
    /* 248 */ 0, 1233, 0, 0, 22, 567, 544, 0, 608, 0, 608
  };

  private static final int[] EXPECTED =
  {
    /*  0 */ 4, 262144, 524288, 262656, 524800, 3072, 1184, 2336, 3584, 541184, 12800, 541188, 1085448, 196704, 1146928,
    /* 15 */ 512, 32, 16384, 12288, 4104, 32776, 8, 131072, 32768, 32, 8192, 8, 8
  };

  private static final String[] TOKEN =
  {
    "(0)",
    "END",
    "Tag",
    "CommentContents",
    "Char",
    "Trim",
    "ElementContentChar",
    "QuotAttrContentChar",
    "AposAttrContentChar",
    "S",
    "'\"'",
    "''''",
    "'(:'",
    "'(:~'",
    "'/>'",
    "':)'",
    "'<'",
    "'</'",
    "'='",
    "'>'",
    "'@'"
  };
}

// End
