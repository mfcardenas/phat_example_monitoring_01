# PHAT Example Monitoring Activity [01]
Use of acceleration sensors to monitor basic activities in a patient suffering from tremors in both hands.

The sensors are located in:
- Chest
- Left hand
- Right hand.
<table>
<tr>
    <td>  
To run the demo

```
mvn clean compile
mvn exec:java -Dexec.mainClass=phat.ActivityMonitoringDemo
```
In case of running into memory problems
```
export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m"
```
And then run the previous command
    </td>
    <td>
        <img src="https://github.com/mfcardenas/phat_example_monitoring_01/blob/master/img/img_older_people_home.png" />
    </td>
</tr>
</table>