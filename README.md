# IC-Compiler
This is an IC compiler. it translates IC code to LIR code through these stages - <br>
Lexical analysis - breaking the code to a stream of tokens<br>
Syntax Analysis - build the AST tree for the expressions\n
Semantic checks - building the sym tables, type tables, and the overall global symbot table\n
IR Code generation - Translating all the AST tree into LIR code.\n
