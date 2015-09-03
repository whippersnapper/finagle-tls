# Finagle example for SSL/TLS

Dependencies:

At present, this version of the finagle-thrift-tls example requires a 6.28.0-SNAPSHOT version of
finagle-thrift, available from https://github.com/DecipherNow/finagle/tree/netty-listener-construction-fix
which in turn requires the SNAPSHOT versions of twitter's util, ostrich and scrooge.

Build:

`mvn clean package`

Run:

```
java -cp target/finagle-tls-1.0-SNAPSHOT.jar com.twitter.finagle.example.tls.TLSServer

java -cp target/finagle-tls-1.0-SNAPSHOT.jar com.twitter.finagle.example.tls.TLSClient
```


