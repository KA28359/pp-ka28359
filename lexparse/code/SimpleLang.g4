grammar SimpleLang;

program  : 'program ' IDENTIFIER (constDecl | varDecl | classDecl | enumDecl | interfaceDecl)* '{' (methodDecl)* '}'  EOF;         // match keyword hello followed by an identifier

constDecl : 'const' type IDENTIFIER '=' (NUMCONST|CHARCONST|BOOLCONST) (',' IDENTIFIER '=' (NUMCONST|CHARCONST|BOOLCONST))* ';';

enumDecl : 'enum ' IDENTIFIER '{' IDENTIFIER ('=' NUMCONST)? (',' IDENTIFIER ('=' NUMCONST)?)* '}';

varDecl : type IDENTIFIER ('['']')? (',' IDENTIFIER ('['']')?)* ';' ;

classDecl : 'class ' IDENTIFIER ('extends ' type)? ('implements ' type (',' type)*)? '{' (varDecl)* ('{'(methodDecl)*'}')? '}';

interfaceDecl : 'interface ' IDENTIFIER '{' (interfaceMethodDecl)* '}' ;

interfaceMethodDecl : (type | 'void') IDENTIFIER '(' formPars? ')' ';' ;

methodDecl : (type | 'void' ) IDENTIFIER '(' formPars? ')' (varDecl)* '{' (statement)* '}' ;

formPars : type IDENTIFIER ('['']')? (',' type IDENTIFIER ('['']')?)* ;

type : IDENTIFIER ;

statement : designatorStatement ';' | 'if' '(' condition ')' statement ('else' statement)? | 'for' '(' (designatorStatement)? ';' (condition)? ';' (designatorStatement)? ')' statement |
    'break' ';' | 'continue' ';' | 'return' (expr)? ';' | 'read' '(' designator ')' ';' | PRINT '(' expr (',' NUMCONST)? ')' ';' | '{' (statement)* '}' ;

PRINT : 'print';

designatorStatement : designator(Assignop expr | '(' (actPars)? ')' | PP | MM) ;

PP : '++';

MM : '--';

actPars : expr (',' expr)* ;

condition : condTerm ('||' condTerm)?;

condTerm : condFact ('&&' condFact)?;

condFact : expr (Relop expr)?;

expr : ('-')? term (Addop term)? ;

term : factor (Mulop factor)? ;

factor : designator ('(' (actPars)? ')')? | NUMCONST | CHARCONST | BOOLCONST | 'new ' type ('[' expr ']' )? | '(' expr ')' ;

designator : IDENTIFIER ('.' IDENTIFIER | '[' expr ']')* ;

//ID : [a-zA-Z]+ ;             // match lower-case identifiers

BOOLCONST : ('true' | 'false') ;

Assignop : '=' ;

Relop : '==' | '!=' | '>' | '>=' | '<' | '<=' ;

Addop : '+' | '-' ;

Mulop : '*' | '/' | '%' ;

IDENTIFIER : LETTER (LETTER|DIGIT|'_')*;

NUMCONST : [0-9]([0-9])*;

CHARCONST : '"'[a-zA-Z] '"';

LETTER : [a-zA-Z] ;

DIGIT : [0-9];

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

LC : '//' ~[\r\n]+ -> skip ;

OTHER: . { System.out.println("LEXER ERROR"); };