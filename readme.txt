Example how to run and results are in 'result.txt' file.
Unzip and go to the folder with main class Main.java.
Required 3 jar files, following with:
	*gson-2.8.2.jar
	*json-simple-1.1.jar
	*mongo-java-driver-3.9.1.jar


To compile and run from Windows (testad on 'cmd' and 'sigwin'):
javac -cp "gson-2.8.2.jar;json-simple-1.1.jar;mongo-java-driver-3.9.1.jar;." Main.java && java -cp "gson-2.8.2.jar;json-simple-1.1.jar;mongo-java-driver-3.9.1.jar;." Main

To compile and run from Linux (not testad):
javac -cp gson-2.8.2.jar:json-simple-1.1.jar:mongo-java-driver-3.9.1.jar:. Main.java && java -cp gson-2.8.2.jar:json-simple-1.1.jar:mongo-java-driver-3.9.1.jar:. Main
