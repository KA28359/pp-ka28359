Class {
	#name : #TableMain,
	#superclass : #Object,
	#instVars : [
		'arr',
		'rows',
		'stdDir'
	],
	#category : #Table
}

{ #category : #evaluating }
TableMain >> left: args header: header cond: cond gt:gt lt:lt eq:eq dne:dne containsColNum:containsColNum containsColHeader:containsColHeader [

	|colVal constVal rowNums temp1 temp2 startRow fileOutPath writeStream|
	header ifTrue:[fileOutPath := (args substrings at:5)] ifFalse: [fileOutPath := (args substrings at:4)].
	rowNums := OrderedCollection new.
	writeStream := fileOutPath asFileReference.
	writeStream writeStreamDo: [:s | s truncate.].
	writeStream := fileOutPath asFileReference writeStream.
	containsColNum ifTrue:[colVal := cond copyFrom:((cond indexOf:$$)+1) to: ((cond indexOf:$,)-1)].
	containsColHeader ifTrue:[header ifTrue: [colVal := cond copyFrom:((cond indexOf:$@)+1) to: ((cond indexOf:$,)-1)].].
	constVal := cond copyFrom:((cond indexOf:$,)+1) to: ((cond size)).

	containsColHeader ifTrue:[header ifTrue:[colVal:= (((rows at: 1) substrings)indexOf: colVal ifAbsent: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COL INDEX ERROR' . 
			errorOut close .
			^nil.
			]
			)-1]. 
		].
	
	
	(colVal asNumber)+1 > ((arr at: 1)size ) ifTrue: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COL INDEX ERROR' . 
			errorOut close .
			^nil.
			].
	(colVal asNumber)+1 < 1 ifTrue: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COL INDEX ERROR' . 
			errorOut close .
			^nil.
			].
	"Transcript show: colVal;cr."
	startRow := 1.
	header ifTrue: [ startRow := 2 ].
	
	eq ifTrue:[
		startRow to: rows size do: [
   :x |  ((arr at: x)at:((colVal asNumber)+1)) = constVal ifTrue:[rowNums add: x-1].  
	].
		].
	
	dne ifTrue:[
		startRow to: rows size do: [
   :x |  ((arr at: x)at:((colVal asNumber)+1)) = constVal ifFalse: [rowNums add: x-1].  
	].
		].

	gt ifTrue:[
		startRow to: rows size do: [
   :x | |cNum badNum cVal| 
	badNum := false.
	cNum := [colVal asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
	temp1 := [Number readFrom: ((arr at: x)at:(cNum +1))] on: Error do:[badNum  := true]. 
	cVal := [constVal asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
		temp2 := Number readFrom: (constVal).
		temp1 > temp2  ifTrue: [rowNums add: x-1].
				].
			]. 
		].
	].

	lt ifTrue:[
		startRow to: rows size do: [
   :x | |cNum badNum cVal| 
	badNum := false.
	cNum := [colVal asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
	temp1 := [Number readFrom: ((arr at: x)at:(cNum +1))] on: Error do:[badNum  := true]. 
	cVal := [constVal asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
		temp2 := Number readFrom: (constVal).
		temp1 < temp2  ifTrue: [rowNums add: x-1].
				].
			]. 
		].
	].

	header ifTrue:[rowNums add: 0].

	0 to: ((rows size)-1) do: [
   :x | (rowNums includes: x) ifTrue:[writeStream nextPutAll: (rows at: (x+1));space . writeStream cr.]. 
	]. 
	"Transcript show: rowNums."


	writeStream close.
]

{ #category : #accessing }
TableMain >> main: args [
	|readStream contents sizeGood numOfCols header filePath	|
	header := false.
	(args substrings at: 1) = '-header' ifTrue:[header := true]. "checking if header given"
	filePath  := (args substrings at: 3).
	header ifTrue: [ filePath  := (args substrings at: 4) ].
	readStream := filePath asFileReference readStream.
	stdDir := (args substrings at: 3) asFileReference.
	header ifTrue: [ stdDir := (args substrings at: 4) asFileReference.].
	stdDir := stdDir  parent pathString.
	arr := OrderedCollection new.
	rows := OrderedCollection new.
	"Transcript show: (readStream upTo: Character cr)."
	contents := (readStream contents). "reading file"
	"arr := contents substrings." "getting all tokens"
	
	rows := contents findTokens: String cr, String lf. "getting all rows"
	sizeGood := true.
	numOfCols  := (rows at: 1) substrings size. "getting num of cols in first row to use to check"
	
	1 to: rows size do: [
   :x |  arr add: (rows at: x) substrings 
	]. 

	1 to: rows size do: [
   :x |  (rows at: x) substrings size = numOfCols ifFalse:[sizeGood := false.].
	]. "checking to see if all rows have same number of columns"
	readStream close.
	
	sizeGood ifFalse:[
		|errorOut| 
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'NUM COLS ERROR' . 
			errorOut close .
			^nil.
		]. "num of cols dont match in every row"
	
	header = true ifTrue:[(args substrings at: 2) = '-print' ifTrue:[self print:args header: header] ].
	header = false ifTrue:[(args substrings at: 1) = '-print' ifTrue:[self print:args header: header] ].
	header = true ifTrue:[(args substrings at: 2) = '-sum' ifTrue:[self sum:args header: header] ].
	header = false ifTrue:[(args substrings at: 1) = '-sum' ifTrue:[self sum:args header: header] ].
	header = true ifTrue:[(args substrings at: 2) = '-when' ifTrue:[self when:args header: header] ].
	header = false ifTrue:[(args substrings at: 1) = '-when' ifTrue:[self when:args header: header] ].

]

{ #category : #evaluating }
TableMain >> none:args header: header cond: cond gt:gt lt:lt eq:eq dne:dne containsColNum:containsColNum containsColHeader:containsColHeader [
	
	|constVal1 constVal2 rowNums temp1 temp2 fileOutPath writeStream|
	rowNums := OrderedCollection new.
	header ifTrue:[fileOutPath := (args substrings at:5)] ifFalse: [fileOutPath := (args substrings at:4)].
	writeStream := fileOutPath asFileReference.
	writeStream writeStreamDo: [:s | s truncate.].
	writeStream := fileOutPath asFileReference writeStream.
	constVal2 := cond copyFrom:((cond indexOf:$,)+1) to: ((cond size)).
	eq ifTrue:[constVal1 := cond copyFrom:3 to: ((cond indexOf:$,)-1).].
	dne ifTrue:[constVal1 := cond copyFrom:3 to: ((cond indexOf:$,)-1).].
	gt ifTrue:[constVal1 := cond copyFrom:2 to: ((cond indexOf:$,)-1).].
	lt ifTrue:[constVal1 := cond copyFrom:2 to: ((cond indexOf:$,)-1).].
	
	eq ifTrue:[
		1 to: rows size do: [
   :x |  constVal1 = constVal2 ifTrue:[rowNums add: x-1].  
	].
		].
	
	dne ifTrue:[
		1 to: rows size do: [
   :x |  constVal1 = constVal2 ifFalse: [rowNums add: x-1].  
	].
		].

	gt ifTrue:[
		1 to: rows size do: [
   :x | |cNum badNum cVal| 
	badNum := false.
	cNum := [constVal1 asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
	temp1 := [Number readFrom: constVal1] on: Error do:[badNum  := true]. 
	cVal := [constVal2 asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
		temp2 := Number readFrom: (constVal2).
		temp1 > temp2  ifTrue: [rowNums add: x-1].
				].
			]. 
		].
	].

	lt ifTrue:[
		1 to: rows size do: [
   :x | |cNum badNum cVal| 
	badNum := false.
	cNum := [constVal1 asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
	temp1 := [Number readFrom: constVal1] on: Error do:[badNum  := true]. 
	cVal := [constVal2 asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
		temp2 := Number readFrom: (constVal2).
		temp1 < temp2  ifTrue: [rowNums add: x-1].
				].
			]. 
		].
	].

	header ifTrue:[rowNums add: 0].

	0 to: ((rows size)-1) do: [
   :x | (rowNums includes: x) ifTrue:[writeStream nextPutAll: (rows at: (x+1));space . writeStream cr.]. 
	]. 
	"Transcript show: rowNums."


	writeStream close.
]

{ #category : #printing }
TableMain >> print: args header: header [
	"Transcript show: 'print'; cr."
	|colNum colVals fileOutPath writeStream|
	header ifTrue:[colNum := (args substrings at: 3) ]  ifFalse: [colNum := (args substrings at: 2) ].
	header ifTrue:[fileOutPath := (args substrings at:5)] ifFalse: [fileOutPath := (args substrings at:4)].
	writeStream := fileOutPath asFileReference.
	writeStream writeStreamDo: [:s | s truncate.].
	writeStream := fileOutPath asFileReference writeStream.
	"Transcript show: colNum ; cr."
	colVals := OrderedCollection new.
	colVals := colNum splitOn: ','. "getting all column values"
	
	"checking values"
	colVals do:[:a| (a asNumber)+1 > ((arr at: 1)size ) ifTrue: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COL INDEX ERROR' . 
			errorOut close .
			^nil.
			].
		].
	colVals do:[:a| (a asNumber)+1 < 1 ifTrue: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COL INDEX ERROR' . 
			errorOut close .
			^nil.
			].
		].
	
	1 to: rows size do: [
   :x | colVals do: [:a | writeStream nextPutAll: ((arr at: x)at:((a asNumber)+1)) ;space ]. writeStream cr.
	].  "printing all values that were asked for"
			
	writeStream close.
	

]

{ #category : #evaluating }
TableMain >> right:args header: header cond: cond gt:gt lt:lt eq:eq dne:dne containsColNum:containsColNum containsColHeader:containsColHeader [

	|colVal constVal rowNums temp1 temp2 startRow fileOutPath writeStream|
	header ifTrue:[fileOutPath := (args substrings at:5)] ifFalse: [fileOutPath := (args substrings at:4)].
	rowNums := OrderedCollection new.
	writeStream := fileOutPath asFileReference.
	writeStream writeStreamDo: [:s | s truncate.].
	writeStream := fileOutPath asFileReference writeStream.
	containsColNum ifTrue:[colVal := cond copyFrom:((cond indexOf:$$)+1) to: ((cond size))].
	containsColHeader ifTrue:[header ifTrue: [colVal := cond copyFrom:((cond indexOf:$@)+1) to: ((cond size))].].
	eq ifTrue:[constVal := cond copyFrom:3 to: ((cond indexOf:$,)-1).].
	dne ifTrue:[constVal := cond copyFrom:3 to: ((cond indexOf:$,)-1).].
	gt ifTrue:[constVal := cond copyFrom:2 to: ((cond indexOf:$,)-1).].
	lt ifTrue:[constVal := cond copyFrom:2 to: ((cond indexOf:$,)-1).].
	
	containsColHeader ifTrue:[header ifTrue:[colVal:= (((rows at: 1) substrings)indexOf: colVal ifAbsent: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COL INDEX ERROR' . 
			errorOut close .
			^nil.
			]
			)-1]. 
		].
	
	
	(colVal asNumber)+1 > ((arr at: 1)size ) ifTrue: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COL INDEX ERROR' . 
			errorOut close .
			^nil.
			].
	(colVal asNumber)+1 < 1 ifTrue: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COL INDEX ERROR' . 
			errorOut close .
			^nil.
			].
	"Transcript show: colVal;cr."
	startRow := 1.
	header ifTrue: [ startRow := 2 ].
	
	eq ifTrue:[
		startRow to: rows size do: [
   :x |  ((arr at: x)at:((colVal asNumber)+1)) = constVal ifTrue:[rowNums add: x-1].  
	].
		].
	
	dne ifTrue:[
		startRow to: rows size do: [
   :x |  ((arr at: x)at:((colVal asNumber)+1)) = constVal ifFalse: [rowNums add: x-1].  
	].
		].

	gt ifTrue:[
		startRow to: rows size do: [
   :x | |cNum badNum cVal| 
	badNum := false.
	cNum := [colVal asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
	temp1 := [Number readFrom: ((arr at: x)at:(cNum +1))] on: Error do:[badNum  := true]. 
	cVal := [constVal asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
		temp2 := Number readFrom: (constVal).
		temp2 > temp1  ifTrue: [rowNums add: x-1].
				].
			]. 
		].
	].

	lt ifTrue:[
		startRow to: rows size do: [
   :x | |cNum badNum cVal| 
	badNum := false.
	cNum := [colVal asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
	temp1 := [Number readFrom: ((arr at: x)at:(cNum +1))] on: Error do:[badNum  := true]. 
	cVal := [constVal asNumber ]on: Error do:[badNum  := true].
	badNum ifFalse: [
		temp2 := Number readFrom: (constVal).
		temp2 < temp1  ifTrue: [rowNums add: x-1].
				].
			]. 
		].
	].
	
	header ifTrue:[rowNums add: 0].

	0 to: ((rows size)-1) do: [
   :x | (rowNums includes: x) ifTrue:[writeStream nextPutAll: (rows at: (x+1));space . writeStream cr.]. 
	]. 
	"Transcript show: rowNums."


	writeStream close.
]

{ #category : #adding }
TableMain >> sum: args header: header [
	|currentSum colNum colVal temp startRow fileOutPath writeStream|
	
	currentSum  := 0.
	header = true ifTrue:[colNum := (args substrings at: 3) ]  ifFalse: [colNum := (args substrings at: 2) ].
	header ifTrue:[fileOutPath := (args substrings at:5)] ifFalse: [fileOutPath := (args substrings at:4)].
	colVal := colNum asNumber.
	writeStream := fileOutPath asFileReference.
	writeStream writeStreamDo: [:s | s truncate. s close.].
	writeStream := fileOutPath asFileReference writeStream.
	"checking value"
	(colVal asNumber)+1 > ((arr at: 1)size ) ifTrue: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COL INDEX ERROR' . 
			errorOut close .
			^nil.
			].
	(colVal asNumber)+1 < 1 ifTrue: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COL INDEX ERROR' . 
			errorOut close .
			^nil.
			].
	
	startRow := 1.
	header ifTrue: [ startRow := 2 ].
		
	startRow to: rows size do: [
   :x |  temp := Number readFrom: ((arr at: x)at:((colVal asNumber)+1)) 
	ifFail: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'TYPE ERROR' . 
			errorOut close .
			^nil.
			].
	currentSum := currentSum + temp.
	].  "visiting all values in the column"
		
	
	"Transcript show:currentSum asString."
	writeStream nextPutAll: currentSum asString.
	
	writeStream close.
]

{ #category : #evaluating }
TableMain >> when: args header: header [

	"Transcript show: 'when'."
	|cond matcher gt lt eq dne containsColNum containsColHeader leftSide|
	header = true ifTrue:[cond := (args substrings at: 3) ]  ifFalse: [cond := (args substrings at: 2) ].
	
	gt := false.
	lt := false.
	eq := false.
	dne := false.
	containsColNum := false.
	containsColHeader := false.
	leftSide := false.
	
	matcher := '((<|==|>|<>)[$@]?[-_.a-zA-Z0-9+]+,[$@]?[-_.a-zA-Z0-9+]+)' asRegex.
   (matcher matches: cond) ifFalse: [
			|errorOut|
			errorOut := (stdDir,'/stdout.txt') asFileReference.
			errorOut writeStreamDo: [:s | s truncate.].
			errorOut := (stdDir,'/stdout.txt') asFileReference writeStream. 
			errorOut nextPutAll: 'COND ERROR' . 
			errorOut close .
			^nil.
			].
	
	matcher := '((>)(.*))' asRegex.
	(matcher matches: cond) ifTrue: [gt := true].
	matcher := '((<)[^>](.*))' asRegex.
	(matcher matches: cond) ifTrue: [lt := true].
	matcher := '((<>)(.*))' asRegex.
	(matcher matches: cond) ifTrue: [dne := true].
	matcher := '((==)(.*))' asRegex.
	(matcher matches: cond) ifTrue: [eq := true].
	
	"Transcript show: gt.
	Transcript show: lt.
	Transcript show: eq.
	Transcript show: dne."
	
	(cond includes: $$) ifTrue:[containsColNum := true].
	(cond includes: $@) ifTrue:[containsColHeader := true].

	containsColNum ifTrue: [((cond indexOf:$$) < (cond indexOf: $,)) ifTrue: [leftSide := true]].
	containsColHeader ifTrue: [((cond indexOf:$@) < (cond indexOf: $,)) ifTrue: [leftSide := true]].
	
	containsColNum ifTrue: [ leftSide ifTrue:[self left:args header:header cond:cond gt:gt lt:lt eq:eq dne:dne containsColNum:containsColNum containsColHeader: containsColHeader ] ifFalse: [self right:args header:header cond:cond gt:gt lt:lt eq:eq dne:dne containsColNum:containsColNum containsColHeader: containsColHeader ] ].
	containsColHeader ifTrue: [ leftSide ifTrue:[self left:args header:header cond:cond gt:gt lt:lt eq:eq dne:dne containsColNum:containsColNum containsColHeader: containsColHeader ] ifFalse: [self right:args header:header cond:cond gt:gt lt:lt eq:eq dne:dne containsColNum:containsColNum containsColHeader: containsColHeader ] ].
	containsColNum ifFalse: [ containsColHeader ifFalse: [ self none:args header:header cond:cond gt:gt lt:lt eq:eq dne:dne containsColNum:containsColNum containsColHeader: containsColHeader] ].


]
