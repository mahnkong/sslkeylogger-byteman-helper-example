# About 
This project demonstrates how to capture SSL connection keys using the byteman helper & rule provided by the project [sslkeylogger-byteman-helper](https://github.com/mahnkong/sslkeylogger-byteman-helper).

The configuration of the byteman agent and the helper can be seen inside the definition of the task 'runClientWithSSLKeyLogging' inside the build.gradle file

The captured keys can be used to decrypt traffic using Wireshark.

# Build fat jar
```
./gradlew shadowJar
```

# Run server
```
java -cp build/libs/sslkeylogger-byteman-helper-example-all.jar org.mahnkong.byteman.helper.sslkeylogger.examples.HTTPSWebServer
```

# Run client
## Dump keys to STDOUT:
```
./gradlew runClientWithSSLKeyLogging -DHTTPS_SERVER_URL=https://server:443
```

## Dump keys to log file:
```
SSL_KEYLOG_PATH=/path/to/ssl.log ./gradlew runClientWithSSLKeyLogging -DHTTPS_SERVER_URL=https://server:443
```
