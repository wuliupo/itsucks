#!/bin/bash

FROM=$1
TO=$2

echo "Moving from \'$FROM\' to \'$TO\'".

for POM_FILE in ../itsucks-*/pom.xml
do
	echo "Change release for $POM_FILE"
	
	REGEXP="s;${FROM};${TO};" 
	sed $REGEXP ${POM_FILE} > ${POM_FILE}.new
	mv ${POM_FILE} ${POM_FILE}~
	mv ${POM_FILE}.new ${POM_FILE}

done

