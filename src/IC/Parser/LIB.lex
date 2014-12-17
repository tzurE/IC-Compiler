/* FIRST PART */
package IC.Parser;
import java_cup.runtime.Symbol;


%%
/* SECOND PART */

%class Liblexer
%public
%function next_token
%type Token
%scanerror LexicalError
%unicode
%cup
%line
%column
%{
  StringBuffer string = new StringBuffer();
  int templine = 0, tempcol = 0;
%}

%eofval{
	if(yystate()==COMMENTS) { 
		throw new LexicalError((yyline + 1) +":" + (yycolumn + 1) + ": lexical error; unclosed comment. expected: '*/' but found 'EOF'"); 
	}
	if(yystate()==STRING) { 
		throw new LexicalError((yyline + 1) +":" + (yycolumn + 1) + ": lexical error; unclosed string. expected: '\"' but found 'EOF'");
	}
  	return new Token(sym.EOF, "EOF","EOF", (yyline + 1), (yycolumn + 1) );
%eofval}

alpha = [a-z]
ALPHA = [A-Z]
Letters = {alpha}|{ALPHA}
Digits = [0-9]

/* whitespaces */
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]

/* identifiers */
Identifier = {alpha}({Letters}|{Digits}|_)*
ClassIdentifier =  {ALPHA}({Letters}|{Digits}|_)*
DecIntegerLiteral = 0 | [0-9][0-9]*

%state STRING 
%state COMMENTS 
%state LINECOMMENTS
 
%%
/* THIRD PART */

 /* keywords */
 
<YYINITIAL> "class"		{ return new Token(Libsym.CLASS, yytext(), "CLASS", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "static"	{ return new Token(Libsym.STATIC, yytext(), "static", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "int"		{ return new Token(Libsym.INT, yytext(), "int",(yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "boolean"	{ return new Token(Libsym.BOOLEAN, yytext(), "boolean", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "string"	{ return new Token(Libsym.STRING, yytext(), "string", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "void"		{ return new Token(Libsym.VOID, yytext(), "void", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "(" 		{ return new Token(Libsym.LP, yytext(), "(", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> ")" 		{ return new Token(Libsym.RP, yytext(), ")", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "{" 		{ return new Token(Libsym.LB, yytext(), "{", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "}" 		{ return new Token(Libsym.RB, yytext(), "}", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "[" 		{ return new Token(Libsym.LSQUARE, yytext(), "[", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "]" 		{ return new Token(Libsym.RSQUARE, yytext(), "]", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "," 		{ return new Token(Libsym.COMMA, yytext(), ",", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> ";" 		{ return new Token(Libsym.SEMI_COL, yytext(), ";", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "Library" 		{ return new Token(Libsym.LIBRARY, yytext(), "Library", (yyline + 1), (yycolumn + 1)); }


 <YYINITIAL> {Identifier}			{ return new Token(Libsym.ID, yytext(), "ID", (yyline + 1), (yycolumn + 1)); }
  <YYINITIAL> {ClassIdentifier}		{ return new Token(Libsym.CLASSIDENT, yytext(), "CLASSIDENT", (yyline + 1), (yycolumn + 1)); }
 <YYINITIAL> "\""                   { 
 	tempcol = yycolumn + 1;
 	templine = yyline + 1; 
 	string.setLength(0); 
 	yybegin(STRING); 
 }
 <YYINITIAL> {WhiteSpace}           { /* ignore */ }
<STRING> {
  {LineTerminator}				{ throw new LexicalError((yyline + 1) +":" + (yycolumn + 1) + ": lexical error; unclosed string. expected: '\"' but found '\\n'"); }
  \\							{ throw new LexicalError((yyline + 1) +":" + (yycolumn + 1) + ": lexical error; illegal character '\\'"); }
  \"                            { yybegin(YYINITIAL); 
                                   return new Token(Libsym.STRING_LITERAL, string.toString(), "STRING", templine, tempcol); }
  \\t                           { string.append("\t"); }
  \\n                           { string.append("\n"); }
  \\r                           { string.append("\r"); }
  \\\"                          { string.append("\\"); }
  \\\\	                        { string.append("\\"); }
  [ -~]							{ string.append( yytext() ); }
  [^]							{ throw new LexicalError((yyline + 1) +":" + (yycolumn + 1) + ": lexical error; illegal character '" +yytext() + "'"); }
  
}

<YYINITIAL> "//" { yybegin(LINECOMMENTS); }
<LINECOMMENTS> [^\n] { }
<LINECOMMENTS> ["\r"]?["\n"] { yybegin(YYINITIAL); }

<YYINITIAL> "/*" {templine = (yyline + 1); tempcol = (yycolumn + 1); yybegin(COMMENTS); }
<COMMENTS> {
	"*/"  	{ yybegin(YYINITIAL); }
	[^]		{}
}


 /* error fallback */
[^]   { throw new LexicalError((yyline + 1) +":" + (yycolumn + 1) + ": lexical error; illegal character '" +yytext() + "'"); }



