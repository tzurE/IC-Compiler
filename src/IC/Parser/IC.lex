/* FIRST PART */
package IC.Parser;
import java_cup.runtime.Symbol;


%%
/* SECOND PART */

%class Lexer
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
 
<YYINITIAL> "class"		{ return new Token(sym.CLASS, yytext(), "class", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "extends"	{ return new Token(sym.EXTENDS, yytext(),"extends", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "static"	{ return new Token(sym.STATIC, yytext(), "static", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "void"		{ return new Token(sym.VOID, yytext(),"void", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "int"		{ return new Token(sym.INT, yytext(), "int",(yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "boolean"	{ return new Token(sym.BOOLEAN, yytext(), "boolean", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "string"	{ return new Token(sym.STRING, yytext(), "string", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "return"	{ return new Token(sym.RETURN, yytext(), "return", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "if"        { return new Token(sym.IF, yytext(), "if", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "else"      { return new Token(sym.ELSE, yytext(), "else", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "while"     { return new Token(sym.WHILE, yytext(), "while", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "break"     { return new Token(sym.BREAK, yytext(), "break", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "continue"  { return new Token(sym.CONTINUE, yytext(), "continue", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "this"      { return new Token(sym.THIS, yytext(), "this", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "new"       { return new Token(sym.NEW, yytext(), "new", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "length"    { return new Token(sym.LENGTH, yytext(), "length", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "true"      { return new Token(sym.TRUE, yytext(), "true", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "false"     { return new Token(sym.FALSE, yytext(), "false", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "null"      { return new Token(sym.NULL, yytext(), "null", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "-" 		{ return new Token(sym.MINUS, yytext(), "-", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "+" 		{ return new Token(sym.PLUS, yytext(), "+", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "*" 		{ return new Token(sym.TIMES, yytext(), "*", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "/" 		{ return new Token(sym.DIVIDE, yytext(), "/", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "%" 		{ return new Token(sym.MOD, yytext(), "/", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "!=" 		{ return new Token(sym.NOTEQUAL, yytext(), "!=", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "!" 		{ return new Token(sym.NOT, yytext(), "!", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "<=" 		{ return new Token(sym.LESSEQUAL, yytext(), "<=", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "<" 		{ return new Token(sym.LESS, yytext(), "<", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> ">=" 		{ return new Token(sym.GREATEREQUAL, yytext(), ">=", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> ">" 		{ return new Token(sym.GREATER, yytext(), ">", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "==" 		{ return new Token(sym.EQUAL, yytext(), "==", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "&&" 		{ return new Token(sym.AND, yytext(),"&&",  (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "||" 		{ return new Token(sym.OR, yytext(), "||", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "(" 		{ return new Token(sym.LP, yytext(), "(", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> ")" 		{ return new Token(sym.RP, yytext(), ")", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "{" 		{ return new Token(sym.LB, yytext(), "{", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "}" 		{ return new Token(sym.RB, yytext(), "}", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "[" 		{ return new Token(sym.LSQUARE, yytext(), "[", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "]" 		{ return new Token(sym.RSQUARE, yytext(), "]", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "." 		{ return new Token(sym.DOT, yytext(), ".", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "," 		{ return new Token(sym.COMMA, yytext(), ",", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> ";" 		{ return new Token(sym.SEMI_COL, yytext(), ";", (yyline + 1), (yycolumn + 1)); }
<YYINITIAL> "=" 		{ return new Token(sym.ASSIGN, yytext(), "=", (yyline + 1), (yycolumn + 1)); }
// TODO: add binary op

 <YYINITIAL> {Identifier}			{ return new Token(sym.ID, yytext(), "ID", (yyline + 1), (yycolumn + 1)); }
 <YYINITIAL> {ClassIdentifier}		{ return new Token(sym.CLASSIDENT, yytext(), "CLASS_ID", (yyline + 1), (yycolumn + 1)); }
 <YYINITIAL> {DecIntegerLiteral}	{ return new Token(sym.NUMBER, yytext(), "INTEGER", (yyline + 1), (yycolumn + 1));}
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
                                   return new Token(sym.STRING_LITERAL,string.toString(), "STRING", templine, tempcol); }
  \\t                           { string.append("\t"); }
  \\n                           { string.append("\n"); }
  \\r                           { string.append("\r"); }
  \\\"                          { string.append("\""); } 
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



