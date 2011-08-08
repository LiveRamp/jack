// Copyright 2011 Rapleaf
package com.rapleaf.jack.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;


public class MysqlToJavaScriptTranslator {

  public static String translate(String original, Set<String> referencedFields) throws IOException {
    StringBuilder sb = new StringBuilder();
    MysqlFlex yylex = new MysqlFlex(new StringReader(original));
    Token token = null;
    while(true) {
      token = yylex.yylex();
      if(token == null) break;
      switch(token.index) {
      case 0: // ','
      case 3: // '('
      case 4: // ')'
      case 10: // '+'
      case 11: // '-'
      case 12: // '*'
      case 13: // '/'
      case 16: // '<'
      case 17: // '<='
      case 18: // '>'
      case 19: // '>='
      case 20: // '&'
      case 21: // '|'
      case 22: // '&&'
      case 23: // '||'
      case 39: // !=
      case 60: // Double quoted String
      case 62: // Single quoted String
      case 64: // Float
        sb.append(token.text);
        break;
      case 14: // '='
        sb.append("==");
        break;
      case 24: // LIKE
      {
        Token next = yylex.yylex();
        if(next.index == 60 || next.index == 62) {
          String regex = next.text.substring(1, next.text.length()-1);
          if(regex.contains("\\")) {
            throw new IOException("Escape characters in the LIKE regex are not currently supported");
          }
          regex = regex.replaceAll("%", ".*");
          regex = regex.replaceAll("_", ".");
          regex = regex.replaceAll("/", "\\\\/");
          regex = '^' + regex + '$';
          sb.append(".match(/");
          sb.append(regex);
          sb.append("/) != null");
        } else {
          throw new IOException("LIKE token must be followed by a string");
        }
      }
        break;
      case 25: // OR
        sb.append("||");
        break;
      case 26: // AND
        sb.append("&&");
        break;
      case 28: // IN
        throw new IOException("IN must be preceded by an identifier");
      case 32: // IS NULL
        sb.append("== null");
        break;
      case 40: // IS NOT NULL
        sb.append("!= null");
        break;
      case 41: // IS NOT {BOOLEAN}
        sb.append("!=");
        if(token.text.toLowerCase().endsWith("true")) {
          sb.append("true");
        } else if(token.text.toLowerCase().endsWith("false")) {
          sb.append("false");
        } else if(token.text.toLowerCase().endsWith("unknown")) {
          sb.append("unknown");
        }
      case 42: // IS {BOOLEAN}
        sb.append("==");
        if(token.text.toLowerCase().endsWith("true")) {
          sb.append("true");
        } else if(token.text.toLowerCase().endsWith("false")) {
          sb.append("false");
        } else if(token.text.toLowerCase().endsWith("unknown")) {
          sb.append("unknown");
        }
        break;
      case 43: // NOT IN
        throw new IOException("NOT IN must be preceded by an identifier");
      case 44: // NOT LIKE 
      {
        Token next = yylex.yylex();
        if(next.index == 60 || next.index == 62) {
          String regex = next.text.substring(1, next.text.length()-1);
          if(regex.contains("\\")) {
            throw new IOException("Escape characters in the NOT LIKE regex are not currently supported");
          }
          regex = regex.replaceAll("%", ".*");
          regex = regex.replaceAll("_", ".");
          regex = regex.replaceAll("/", "\\/");
          regex = '^' + regex + '$';
          sb.append(".match(/");
          sb.append(regex);
          sb.append("/) == null");
        } else {
          throw new IOException("NOT LIKE token must be followed by a string");
        }
      }
        break;
      case 66: // Identifier
        referencedFields.add(token.text.toLowerCase());
        Token next = yylex.yylex();
        if(next == null) {
          break;
        }
        if(next.index == 28) { // IN
          sb.append("(");
          next = yylex.yylex();
          if(next == null || next.index != 3) { // '('
            throw new IOException("IN must be followed by '('");
          }
          next = yylex.yylex();
          if(next == null) {
            throw new IOException("Unmatched open parenthases");
          }
          sb.append(token.text + "==" + next.text);
          while(true) {
            next = yylex.yylex();
            if(next == null) {
              throw new IOException("Unmatched open parenthases");
            }
            if(next.index == 4) { // ')'
              break;
            }
            if(next.index != 0) {// ','
              throw new IOException("Values in () must be comma separated");
            }
            Token nextValue = yylex.yylex();
            sb.append("||" + token.text + "==" + nextValue.text);
          }
          sb.append(")");
        } else if(next.index == 43) { // NOT IN
          sb.append("(");
          next = yylex.yylex();
          if(next == null || next.index != 3) { // '('
            throw new IOException("IN must be followed by '('");
          }
          next = yylex.yylex();
          if(next == null) {
            throw new IOException("Unmatched open parenthases");
          }
          sb.append(token.text + "!=" + next.text);
          while(true) {
            next = yylex.yylex();
            if(next == null) {
              throw new IOException("Unmatched open parenthases");
            }
            if(next.index == 4) { // ')'
              break;
            }
            if(next.index != 0) {// ','
              throw new IOException("Values in () must be comma separated");
            }
            Token nextValue = yylex.yylex();
            sb.append("&&" + token.text + "!=" + nextValue.text);
          }
          sb.append(")");
        } else {
          sb.append(token.text.toLowerCase());
          yylex.yypushback(next.text.length());
        } 
        break;
      default: // Not supported 1,2,5-9,15,24,27, etc
        throw new IOException("Unsupported token: " + token.text);
      }
    }
    return sb.toString();
  }
  
  private static final void stringifyToken(Token token, StringBuilder sb, Set<String> referencedFields) throws IOException {
    switch(token.index) {
    case 66:
      sb.append(token.text);
      referencedFields.add(token.text);
      break;
    default:
      throw new IOException("Unsupported token");
    }
  }
}
