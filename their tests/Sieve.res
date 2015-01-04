Parsed libic.sig successfully!
Parsed ic\Sieve.ic successfully!

Abstract Syntax Tree: ic\Sieve.ic

1: Declaration of class: Library, Type: Library, Symbol table: Global
    2: Declaration of library method: println, Type: {string -> void}, Symbol table: Library
        2: Parameter: s, Type: string, Symbol table: println
    3: Declaration of library method: print, Type: {string -> void}, Symbol table: Library
        3: Parameter: s, Type: string, Symbol table: print
    4: Declaration of library method: printi, Type: {int -> void}, Symbol table: Library
        4: Parameter: i, Type: int, Symbol table: printi
    5: Declaration of library method: printb, Type: {boolean -> void}, Symbol table: Library
        5: Parameter: b, Type: boolean, Symbol table: printb
    7: Declaration of library method: readi, Type: { -> int}, Symbol table: Library
    8: Declaration of library method: readln, Type: { -> string}, Symbol table: Library
    9: Declaration of library method: eof, Type: { -> boolean}, Symbol table: Library
    11: Declaration of library method: stoi, Type: {string, int -> int}, Symbol table: Library
        11: Parameter: s, Type: string, Symbol table: stoi
        11: Parameter: n, Type: int, Symbol table: stoi
    13: Declaration of library method: itos, Type: {int -> string}, Symbol table: Library
        13: Parameter: i, Type: int, Symbol table: itos
    14: Declaration of library method: stoa, Type: {string -> int[]}, Symbol table: Library
        14: Parameter: s, Type: string, Symbol table: stoa
    15: Declaration of library method: atos, Type: {int[] -> string}, Symbol table: Library
        15: Parameter: a, Type: int[], Symbol table: atos
    17: Declaration of library method: random, Type: {int -> int}, Symbol table: Library
        17: Parameter: i, Type: int, Symbol table: random
    18: Declaration of library method: time, Type: { -> int}, Symbol table: Library
    19: Declaration of library method: exit, Type: {int -> int}, Symbol table: Library
        19: Parameter: i, Type: int, Symbol table: exit
14: Declaration of class: Sieve, Type: Sieve, Symbol table: Global
    16: Declaration of field: num, Type: int[], Symbol table: Sieve
    18: Declaration of virtual method: initArray, Type: { -> void}, Symbol table: Sieve
        19: Declaration of local variable: i, with initial value, Type: int, Symbol table: initArray
            19: Integer literal: 0, Type: int, Symbol table: initArray
        20: While statement, Symbol table: initArray
            20: Logical binary operation: less than, Type: boolean, Symbol table: initArray
                20: Reference to variable: i, Type: int, Symbol table: initArray
                20: Reference to array length, Type: int, Symbol table: initArray
                    20: Reference to variable: num, Type: int[], Symbol table: initArray
            20: Block of statements, Symbol table: initArray
                21: Assignment statement, Symbol table: statement block in initArray
                    21: Reference to array, Type: int, Symbol table: statement block in initArray
                        21: Reference to variable: num, Type: int[], Symbol table: statement block in initArray
                        21: Reference to variable: i, Type: int, Symbol table: statement block in initArray
                    21: Reference to variable: i, Type: int, Symbol table: statement block in initArray
                22: Assignment statement, Symbol table: statement block in initArray
                    22: Reference to variable: i, Type: int, Symbol table: statement block in initArray
                    22: Mathematical binary operation: addition, Type: int, Symbol table: statement block in initArray
                        22: Reference to variable: i, Type: int, Symbol table: statement block in initArray
                        22: Integer literal: 1, Type: int, Symbol table: statement block in initArray
    26: Declaration of virtual method: sieveAll, Type: { -> void}, Symbol table: Sieve
        27: Declaration of local variable: i, with initial value, Type: int, Symbol table: sieveAll
            27: Integer literal: 2, Type: int, Symbol table: sieveAll
        28: While statement, Symbol table: sieveAll
            28: Logical binary operation: less than, Type: boolean, Symbol table: sieveAll
                28: Reference to variable: i, Type: int, Symbol table: sieveAll
                28: Reference to array length, Type: int, Symbol table: sieveAll
                    28: Reference to variable: num, Type: int[], Symbol table: sieveAll
            28: Block of statements, Symbol table: sieveAll
                29: Method call statement, Symbol table: statement block in sieveAll
                    29: Call to virtual method: sieve, Type: void, Symbol table: statement block in sieveAll
                        29: Reference to variable: i, Type: int, Symbol table: statement block in sieveAll
                30: Assignment statement, Symbol table: statement block in sieveAll
                    30: Reference to variable: i, Type: int, Symbol table: statement block in sieveAll
                    30: Mathematical binary operation: addition, Type: int, Symbol table: statement block in sieveAll
                        30: Reference to variable: i, Type: int, Symbol table: statement block in sieveAll
                        30: Integer literal: 1, Type: int, Symbol table: statement block in sieveAll
    34: Declaration of virtual method: sieve, Type: {int -> void}, Symbol table: Sieve
        34: Parameter: n, Type: int, Symbol table: sieve
        35: Declaration of local variable: i, with initial value, Type: int, Symbol table: sieve
            35: Mathematical binary operation: multiplication, Type: int, Symbol table: sieve
                35: Integer literal: 2, Type: int, Symbol table: sieve
                35: Reference to variable: n, Type: int, Symbol table: sieve
        36: While statement, Symbol table: sieve
            36: Logical binary operation: less than, Type: boolean, Symbol table: sieve
                36: Reference to variable: i, Type: int, Symbol table: sieve
                36: Reference to array length, Type: int, Symbol table: sieve
                    36: Reference to variable: num, Type: int[], Symbol table: sieve
            36: Block of statements, Symbol table: sieve
                37: Assignment statement, Symbol table: statement block in sieve
                    37: Reference to array, Type: int, Symbol table: statement block in sieve
                        37: Reference to variable: num, Type: int[], Symbol table: statement block in sieve
                        37: Reference to variable: i, Type: int, Symbol table: statement block in sieve
                    37: Integer literal: 0, Type: int, Symbol table: statement block in sieve
                38: Assignment statement, Symbol table: statement block in sieve
                    38: Reference to variable: i, Type: int, Symbol table: statement block in sieve
                    38: Mathematical binary operation: addition, Type: int, Symbol table: statement block in sieve
                        38: Reference to variable: i, Type: int, Symbol table: statement block in sieve
                        38: Reference to variable: n, Type: int, Symbol table: statement block in sieve
    42: Declaration of virtual method: printPrimes, Type: { -> void}, Symbol table: Sieve
        43: Declaration of local variable: i, with initial value, Type: int, Symbol table: printPrimes
            43: Integer literal: 2, Type: int, Symbol table: printPrimes
        44: Method call statement, Symbol table: printPrimes
            44: Call to static method: print, in class Library, Type: void, Symbol table: printPrimes
                44: String literal: "Primes less than ", Type: string, Symbol table: printPrimes
        45: Method call statement, Symbol table: printPrimes
            45: Call to static method: printi, in class Library, Type: void, Symbol table: printPrimes
                45: Reference to array length, Type: int, Symbol table: printPrimes
                    45: Reference to variable: num, Type: int[], Symbol table: printPrimes
        46: Method call statement, Symbol table: printPrimes
            46: Call to static method: print, in class Library, Type: void, Symbol table: printPrimes
                46: String literal: ": ", Type: string, Symbol table: printPrimes
        47: While statement, Symbol table: printPrimes
            47: Logical binary operation: less than, Type: boolean, Symbol table: printPrimes
                47: Reference to variable: i, Type: int, Symbol table: printPrimes
                47: Reference to array length, Type: int, Symbol table: printPrimes
                    47: Reference to variable: num, Type: int[], Symbol table: printPrimes
            47: Block of statements, Symbol table: printPrimes
                48: If statement, Symbol table: statement block in printPrimes
                    48: Logical binary operation: inequality, Type: boolean, Symbol table: statement block in printPrimes
                        48: Reference to array, Type: int, Symbol table: statement block in printPrimes
                            48: Reference to variable: num, Type: int[], Symbol table: statement block in printPrimes
                            48: Reference to variable: i, Type: int, Symbol table: statement block in printPrimes
                        48: Integer literal: 0, Type: int, Symbol table: statement block in printPrimes
                    48: Block of statements, Symbol table: statement block in printPrimes
                        49: Method call statement, Symbol table: statement block in statement block in printPrimes
                            49: Call to static method: printi, in class Library, Type: void, Symbol table: statement block in statement block in printPrimes
                                49: Reference to array, Type: int, Symbol table: statement block in statement block in printPrimes
                                    49: Reference to variable: num, Type: int[], Symbol table: statement block in statement block in printPrimes
                                    49: Reference to variable: i, Type: int, Symbol table: statement block in statement block in printPrimes
                        50: Method call statement, Symbol table: statement block in statement block in printPrimes
                            50: Call to static method: print, in class Library, Type: void, Symbol table: statement block in statement block in printPrimes
                                50: String literal: " ", Type: string, Symbol table: statement block in statement block in printPrimes
                52: Assignment statement, Symbol table: statement block in printPrimes
                    52: Reference to variable: i, Type: int, Symbol table: statement block in printPrimes
                    52: Mathematical binary operation: addition, Type: int, Symbol table: statement block in printPrimes
                        52: Reference to variable: i, Type: int, Symbol table: statement block in printPrimes
                        52: Integer literal: 1, Type: int, Symbol table: statement block in printPrimes
    57: Declaration of static method: main, Type: {string[] -> void}, Symbol table: Sieve
        57: Parameter: args, Type: string[], Symbol table: main
        58: Method call statement, Symbol table: main
            58: Call to virtual method: test, in external scope, Type: void, Symbol table: main
                58: Parenthesized expression, Type: Sieve, Symbol table: main
                    58: Instantiation of class: Sieve, Type: Sieve, Symbol table: main
                58: Reference to variable: args, Type: string[], Symbol table: main
    61: Declaration of virtual method: test, Type: {string[] -> void}, Symbol table: Sieve
        61: Parameter: args, Type: string[], Symbol table: test
        62: Declaration of local variable: n, Type: int, Symbol table: test
        64: If statement, Symbol table: test
            64: Logical binary operation: inequality, Type: boolean, Symbol table: test
                64: Reference to array length, Type: int, Symbol table: test
                    64: Reference to variable: args, Type: string[], Symbol table: test
                64: Integer literal: 1, Type: int, Symbol table: test
            64: Block of statements, Symbol table: test
                65: Method call statement, Symbol table: statement block in test
                    65: Call to static method: println, in class Library, Type: void, Symbol table: statement block in test
                        65: String literal: "Unspecified number.", Type: string, Symbol table: statement block in test
                66: Return statement, Symbol table: statement block in test
        69: Method call statement, Symbol table: test
            69: Call to static method: println, in class Library, Type: void, Symbol table: test
                69: String literal: "", Type: string, Symbol table: test
        70: Assignment statement, Symbol table: test
            70: Reference to variable: n, Type: int, Symbol table: test
            70: Call to static method: stoi, in class Library, Type: int, Symbol table: test
                70: Reference to array, Type: string, Symbol table: test
                    70: Reference to variable: args, Type: string[], Symbol table: test
                    70: Integer literal: 0, Type: int, Symbol table: test
                70: Integer literal: 0, Type: int, Symbol table: test
        71: If statement, Symbol table: test
            71: Logical binary operation: less than or equal to, Type: boolean, Symbol table: test
                71: Reference to variable: n, Type: int, Symbol table: test
                71: Integer literal: 0, Type: int, Symbol table: test
            71: Block of statements, Symbol table: test
                72: Method call statement, Symbol table: statement block in test
                    72: Call to static method: println, in class Library, Type: void, Symbol table: statement block in test
                        72: String literal: "Invalid array length", Type: string, Symbol table: statement block in test
                73: Return statement, Symbol table: statement block in test
        75: Assignment statement, Symbol table: test
            75: Reference to variable: num, Type: int[], Symbol table: test
            75: Array allocation, Type: int[], Symbol table: test
                75: Reference to variable: n, Type: int, Symbol table: test
        77: Method call statement, Symbol table: test
            77: Call to virtual method: initArray, Type: void, Symbol table: test
        78: Method call statement, Symbol table: test
            78: Call to virtual method: sieveAll, Type: void, Symbol table: test
        79: Method call statement, Symbol table: test
            79: Call to virtual method: printPrimes, Type: void, Symbol table: test
        80: Method call statement, Symbol table: test
            80: Call to static method: println, in class Library, Type: void, Symbol table: test
                80: String literal: "", Type: string, Symbol table: test

Global Symbol Table: ic\Sieve.ic
    Class: Library
    Class: Sieve
Children tables: Library, Sieve

Class Symbol Table: Library
    Static method: println {string -> void}
    Static method: print {string -> void}
    Static method: printi {int -> void}
    Static method: printb {boolean -> void}
    Static method: readi { -> int}
    Static method: readln { -> string}
    Static method: eof { -> boolean}
    Static method: stoi {string, int -> int}
    Static method: itos {int -> string}
    Static method: stoa {string -> int[]}
    Static method: atos {int[] -> string}
    Static method: random {int -> int}
    Static method: time { -> int}
    Static method: exit {int -> int}
Children tables: println, print, printi, printb, readi, readln, eof, stoi, itos, stoa, atos, random, time, exit

Method Symbol Table: println
    Parameter: string s

Method Symbol Table: print
    Parameter: string s

Method Symbol Table: printi
    Parameter: int i

Method Symbol Table: printb
    Parameter: boolean b

Method Symbol Table: readi

Method Symbol Table: readln

Method Symbol Table: eof

Method Symbol Table: stoi
    Parameter: string s
    Parameter: int n

Method Symbol Table: itos
    Parameter: int i

Method Symbol Table: stoa
    Parameter: string s

Method Symbol Table: atos
    Parameter: int[] a

Method Symbol Table: random
    Parameter: int i

Method Symbol Table: time

Method Symbol Table: exit
    Parameter: int i

Class Symbol Table: Sieve
    Field: int[] num
    Virtual method: initArray { -> void}
    Virtual method: sieveAll { -> void}
    Virtual method: sieve {int -> void}
    Virtual method: printPrimes { -> void}
    Static method: main {string[] -> void}
    Virtual method: test {string[] -> void}
Children tables: initArray, sieveAll, sieve, printPrimes, main, test

Method Symbol Table: initArray
    Local variable: int i
Children tables: statement block in initArray

Statement Block Symbol Table ( located in initArray )

Method Symbol Table: sieveAll
    Local variable: int i
Children tables: statement block in sieveAll

Statement Block Symbol Table ( located in sieveAll )

Method Symbol Table: sieve
    Parameter: int n
    Local variable: int i
Children tables: statement block in sieve

Statement Block Symbol Table ( located in sieve )

Method Symbol Table: printPrimes
    Local variable: int i
Children tables: statement block in printPrimes

Statement Block Symbol Table ( located in printPrimes )
Children tables: statement block in statement block in printPrimes

Statement Block Symbol Table ( located in statement block in printPrimes )

Method Symbol Table: main
    Parameter: string[] args

Method Symbol Table: test
    Parameter: string[] args
    Local variable: int n
Children tables: statement block in test, statement block in test

Statement Block Symbol Table ( located in test )

Statement Block Symbol Table ( located in test )

Type Table: ic\Sieve.ic
    1: Primitive type: int
    2: Primitive type: boolean
    3: Primitive type: null
    4: Primitive type: string
    5: Primitive type: void
    8: Class: Library
    21: Class: Sieve
    6: Array type: string[]
    17: Array type: int[]
    7: Method type: {string[] -> void}
    9: Method type: {string -> void}
    10: Method type: {int -> void}
    11: Method type: {boolean -> void}
    12: Method type: { -> int}
    13: Method type: { -> string}
    14: Method type: { -> boolean}
    15: Method type: {string, int -> int}
    16: Method type: {int -> string}
    18: Method type: {string -> int[]}
    19: Method type: {int[] -> string}
    20: Method type: {int -> int}
    22: Method type: { -> void}
