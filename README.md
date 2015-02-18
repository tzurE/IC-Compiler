# IC-Compiler
This is an IC compiler. it translates IC code to LIR code through these stages - 
Lexical analysis - breaking the code to a stream of tokens
Syntax Analysis - build the AST tree for the expressions
Semantic checks - building the sym tables, type tables, and the overall global symbot table
IR Code generation - Translating all the AST tree into LIR code.
