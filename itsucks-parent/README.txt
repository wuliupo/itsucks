Build ItSucks Complete
  mvn -D maven.test.skip=true package 

Build and install ItSucks Core Libs
  mvn -D maven.test.skip=true -P core install
  
Generate Java Doc for core libs
  mvn -D maven.test.skip=true -P core package javadoc:javadoc 
  
Build release
  mvn package assembly:assembly