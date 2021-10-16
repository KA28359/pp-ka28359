Class {
	#name : #BaselineOfTable,
	#superclass : #Object,
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
