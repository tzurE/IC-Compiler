package IC;

import java.io.FileReader;

import java_cup.runtime.Symbol;
import IC.AST.*;
import IC.Parser.Lexer;
import IC.Parser.LibParser;
import IC.Parser.Liblexer;
import IC.Parser.Parser;
import IC.Parser.SyntaxError;
import LIR.LirTranslatorVisitor;
import SemanticCheckerVisitor.*;
import SymbolTables.*;
import TypeTable.*;

public class Compiler {
	
	public static void main(String[] args) {
		LibParser library_parser;
		Parser Program_Parser;	//the IC language parser
		FileReader libFile, programFile;	//the IC lIBRARY paser
		PrettyPrinter Library_printer;		//prints the ast+sym
		PrettyPrinter Program_printer;		// 	"		"	"
		Symbol Library_tree;
		Symbol Prog_tree;
		String library_location = "libic.sig";
		boolean PrintSymTables = false;
		boolean LibPathGiven = false;
		boolean printAST = false;
		boolean printLIR = false;
		
		if(args.length == 0){
			System.out.println("can't get args < 0, give a path to a file!");
			return;
		}
			
		
		for (int i = 0; i < args.length; i++){
			String current = args[i];
			
			if(current.equals("-dump-symtab")){
				PrintSymTables = true;
			}
			
			else if(current.startsWith("-L")){
				LibPathGiven = true;
				library_location = current.substring(2);
			}
			
			else if(current.equals("-print-ast")){
				printAST = true;
			}
			else if (current.equals("-print-lir")){
				printLIR = true;
			}
					
		}
		try{
		programFile = new FileReader(args[0]);
		IC.Parser.Lexer Program_scanner = new Lexer(programFile);
		Program_printer = new PrettyPrinter(args[0]);
		Program_Parser = new Parser(Program_scanner);
		Prog_tree = Program_Parser.parse();
		Program prog2_root = (Program)Prog_tree.value;
		if(LibPathGiven){
			libFile = new FileReader(library_location);
			Liblexer Library_scanner = new Liblexer(libFile);
			library_parser = new LibParser(Library_scanner);
			//run the library paser
			Library_tree = library_parser.parse();
			Program lib_root = (Program)Library_tree.value;
			prog2_root.addLibraryClass(lib_root.getClasses().get(0));
			System.out.println("Parsed " + library_location + " successfully!");
		}
		
		SymbolVisitorBuilder symTableCreate = new SymbolVisitorBuilder(args[0]);
		GlobalSymbolTable glbTable = (GlobalSymbolTable)prog2_root.accept(symTableCreate, null);
		SymbolVisitorChecker semanticCheckV = new SymbolVisitorChecker(glbTable);
		prog2_root.accept(semanticCheckV, null);
		System.out.println("Parsed " + args[0] +" successfully!");
		
		if(printAST){
			PrettyPrinter printer = new PrettyPrinter(args[0]);
			String myString = (String) prog2_root.accept(printer);
			if(PrintSymTables){
				System.out.println(myString);
			}
			else{
				System.out.print(myString);
			}
		}
		
		else if(PrintSymTables){
			
			System.out.println();
			glbTable.print();

			// Prints Program Type Table something like this
			TypeTable.printTypeTable(args[0]);
		}
		
		LirTranslatorVisitor lirTrans = new LirTranslatorVisitor();
		int regNum = 0;
		String str = prog2_root.accept(lirTrans, regNum).toString();
		
		if (printLIR){
			System.out.println(str);
		}
		
		
		}catch (SemanticError e1){
			e1.getErrorMessage();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		/////////////////////////////////////////////////pretty printer visitor////////////////////////////////////////////
		
		

		
		

		
		
		
		//////////////////////////////////////////////////parse library////////////////////////////////////////////////////////////
		
//		try {//if we got an alternative path to a library file, we take it
//			if(args.length != 1 && args.length != 2){
//				throw new IllegalArgumentException("Need 1 or 2 arguments. got: " + args.length);
//			}
//			if(args.length == 2 && args[1] != null){
//				if( ! args[1].startsWith("-L"))
//					throw new IllegalArgumentException("Library name provided: " +args[1]+ " doesn't start with -L");
//				libFile = new FileReader(args[1].substring(2));
//				Library_printer = new PrettyPrinter(args[1]);
//				Liblexer Library_scanner = new Liblexer(libFile);
//				library_parser = new LibParser(Library_scanner);
//				//run the library paser
//				Library_tree = library_parser.parse();
//				
//				/////////////////////////////////////////////////pretty printer visitor////////////////////////////////////////////
//				//library ast root in prog1
//				Program prog1 = (Program)Library_tree.value;
//				//keeping all of this, if we want to print the library too.
//				String str_prog1 = (String)prog1.accept(Library_printer);
//				System.out.println("Parsed libic.sig successfully!");
//			}
			//////////////////////////////////////////////////parse the program////////////////////////////////////////////////////////

				//String str_prog2 = (String)prog2_root.accept(Program_printer);
				
				//thats the ast from pa2
				//	System.out.println("Parsed " +  args[0] + " successfully!");
				//	System.out.println(str_prog2);
			//}
//		} catch (SyntaxError e){
//			System.out.println(e.getErrorMessage());
//			//TODO: COMMENTOUT BEFORE SENDINGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
//			e.printStackTrace();
//			}
//		catch (Exception e1){
//			//TODO: COMMENTOUT BEFORE SENDINGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
//			e1.printStackTrace();
//			return;
//		}
	}
}
