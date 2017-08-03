<ul>
<li> <b>How to build with Maven</b>: 
<p>1. Unzip the project into local folder on windows machine <br>
   2. Goto Command Prompt window <br>
   3. mvn clean <br>
   4. mvn install <br>
   5. mvn package : used to copy the war file to specified path<br>
</p>
<li> <b>How to deploy using Maven</b>: 
<p> 1. mvn package command will place war file generated onto folder/server path. <br>
    2. If any changes are required to change the path, <b>pom.xml </b>has to be modified and follow build steps again. <br>
    3. Run the server onto which the war file is deployed and test.
</p>
</ul>
