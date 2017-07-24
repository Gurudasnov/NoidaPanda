Any application developed on Spring boot can be deployed on servers like JBoss(war,jar) etc. The only difference lies in the way we access 
the URL of the application:

Spring Boot URL : 'http://localhost:8080/{controllerName}'
JBOSS URL : 'http://localhost:8080/{appName}/{controllerName}'
