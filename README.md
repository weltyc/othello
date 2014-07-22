Othello Utils
=============

This project contains Othello utilities.

* Reading and writing of Othello file formats
  * GGF format
  * NBoard log files

* NBoard protocol communication

* NTest utilities
  * Timer
  * Windows deployment program
  * Log analyzer
  * Opening assignment calculator
  * 32-to-64 bit flip function converter
  * Automatic book adder

* CBinaryReader and CBinaryWriter to make porting C programs easier

Building
--------

This depends on Orbanova Common Library 0.09, available from 
<http://www.orbanova.com/maven2/com/orbanova/common/0.09/common-0.09.jar>, and its dependencies.
Maven should automatically pick up the transitive dependencies.

It also depends on proguard 4.8, which is not available in any maven repository. Instead,
download it from the Proguard web site and install it in your local repository using the command

    mvn install:install-file -Dfile=lib/proguard.jar -Dversion=4.8 -DgroupId=net.sf.proguard  -DartifactId=proguard -Dpackaging=jar
