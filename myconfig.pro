-injars       target/othello-1.0-SNAPSHOT-jar-with-dependencies.jar(!**/package.html,!**/pom.xml,!**/pom.properties,!**/Luthor.luth)
-outjars      target/othello.jar
-libraryjars  <java.home>/lib/rt.jar
-libraryjars  C:\Users\Chris\.m2\repository\junit\junit\4.4\junit-4.4.jar
-libraryjars  C:\Users\Chris\.m2\repository\org\apache\ant\ant\1.8.1
-printmapping myapplication.map

-keep public class com.welty.othello.timer.timer.OthelloTimer { public *; public static *; }
-keep public class com.welty.othello.deploy.Deployer { public *; public static *; }