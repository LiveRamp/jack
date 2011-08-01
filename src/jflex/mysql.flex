package com.rapleaf.jack.util;
import java.io.IOException;

%%

%{
%} 

%class MysqlFlex
%full
%caseless
%type Token


ALPHA=[A-Za-z]
DIGIT=[0-9]
BOOLEAN=[Tt][Rr][Uu][Ee]|[Ff][Aa][Ll][Ss][Ee]|[Uu][Nn][Kk][Nn][Oo][Ww][Nn]
NONNEWLINE_WHITE_SPACE_CHAR=[\ \t\b\012]
NEWLINE=\r|\n|\r\n
WHITE_SPACE_CHAR=[\n\r\ \t\b\012]
STRING_TEXT=(\\\"|[^\n\r\"]|\\{WHITE_SPACE_CHAR}+\\)*
SINGLE_QUOTED_STRING_TEXT=(\\\'|[^\n\r\']|\\{WHITE_SPACE_CHAR}+\\)*
EXPONENT=[e|E][+|=]?{DIGIT}+ 
FLOAT={DIGIT}+\.{DIGIT}*{EXPONENT}?|\.{DIGIT}+{EXPONENT}?|{DIGIT}+{EXPONENT}?
Ident={ALPHA}({ALPHA}|{DIGIT}|_)*

%% 

<YYINITIAL> {
  "," { return (new Token(0,yytext())); }
  ":" { return (new Token(1,yytext())); }
  ";" { return (new Token(2,yytext())); }
  "(" { return (new Token(3,yytext())); }
  ")" { return (new Token(4,yytext())); }
  "[" { return (new Token(5,yytext())); }
  "]" { return (new Token(6,yytext())); }
  "{" { return (new Token(7,yytext())); }
  "}" { return (new Token(8,yytext())); }
  "." { return (new Token(9,yytext())); }
  "+" { return (new Token(10,yytext())); }
  "-" { return (new Token(11,yytext())); }
  "*" { return (new Token(12,yytext())); }
  "/" { return (new Token(13,yytext())); }
  "=" { return (new Token(14,yytext())); }
  "<>" { return (new Token(15,yytext())); }
  "<"  { return (new Token(16,yytext())); }
  "<=" { return (new Token(17,yytext())); }
  ">"  { return (new Token(18,yytext())); }
  ">=" { return (new Token(19,yytext())); }
  "&"  { return (new Token(20,yytext())); }
  "|"  { return (new Token(21,yytext())); }
  "&&" { return (new Token(22,yytext())); }
  "||" { return (new Token(23,yytext())); }
  "LIKE" { return (new Token(24,yytext())); }
  "OR" { return (new Token(25,yytext())); }
  "AND" { return (new Token(26,yytext())); }
  "XOR" { return (new Token(27,yytext())); }
  "IN" { return (new Token(28,yytext())); }
  "NOT" { return (new Token(29,yytext())); }
  "IS NULL" { return (new Token(32,yytext())); }
  "!" { return (new Token(33,yytext())); }
  "^" { return (new Token(34,yytext())); }
  "<<" { return (new Token(35,yytext())); }
  ">>" { return (new Token(36,yytext())); }
  ":=" { return (new Token(37,yytext())); }
  "==" { return (new Token(38,yytext())); }
  "!=" { return (new Token(39,yytext())); }
  "IS NOT NULL" { return (new Token(40,yytext())); }
  "IS NOT "{BOOLEAN} { return (new Token(41,yytext())); }
  "IS "{BOOLEAN} { return (new Token(42,yytext())); }
  "NOT IN" { return (new Token(43,yytext())); }
  "NOT LIKE" { return (new Token(44,yytext())); }

  {NONNEWLINE_WHITE_SPACE_CHAR}+ { }

  \"{STRING_TEXT}\" {
    String str =  yytext().substring(1,yylength()-1);
    return (new Token(60,str));
  }
  
  \"{STRING_TEXT} {
    String str =  yytext().substring(1,yytext().length());
    throw new IOException("Unclosed string");
  } 

  \'{SINGLE_QUOTED_STRING_TEXT}\' {
    String str =  yytext().substring(1,yylength()-1);
    return (new Token(62,str));
  }
  
  \'{SINGLE_QUOTED_STRING_TEXT} {
    String str =  yytext().substring(1,yytext().length());
    throw new IOException("Unclosed string");
  } 
  
  {FLOAT} { return (new Token(64,yytext())); }

  \`{Ident}\` { 
    String str = yytext().substring(1,yytext().length()-1);
    return (new Token(66,str)); 
  }

  {Ident} { return (new Token(66,yytext())); }  

}


{NEWLINE} { }

. {
  throw new IOException("Unmatched character");
}

