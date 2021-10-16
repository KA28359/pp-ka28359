Class {
	#name : #BaselineOfTable,
	#superclass : #BaselineOf,
	#category : #BaselineOfTable
}

{ #category : #baselines }
BaselineOfTable >> baseline: spec [ 
	<baseline>
	spec
		for: #common
		do: [ spec package: 'Table' ]
]

{ #category : #accessing }
BaselineOfTable >> projectClass [  
	^ MetacelloCypressBaselineProject
]
