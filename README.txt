# QueueWebService

QueueWebService application has been built in Eclipse with JBoss 7.1 runtime. The application features a 
Restful web service to add numbers to a JMS queue and to list all numbers from the same queue. To simplify 
the solution I have used the default JMS connection factory and queue of JBoss Application Server with 
"standalone-all" configuration. The default connection and queue names are as below:

JMS_CONNECTION_FACTORY = "ConnectionFactory"
JMS_QUEUE = "queue/test"

For the GcdCalculator SOAP service that calculates GCD and returns GCD list and sum, I have used an ArrayList 
to store all calculated GCDs. As a future improvement, the calculated GCDs can be added to another queue and 
the list function can use that queue as a data source instead of local ArrayList.

Due to time limitation, I could not add test cases to this project. However, I have used web browser and soapUI 
to test functionality of both services. I assumed that the Application Server would be configured to handle 
MAX concurrent user request as per requirement.  