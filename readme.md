### Gradle build

```
./gradlew clean build -x test
```

### Release build

```
[zany@titan netty4-test-server]$ ./release.sh

BUILD SUCCESSFUL in 2s
6 actionable tasks: 6 executed

[zany@titan netty4-test-server]$ cd _release/
[zany@titan _release]$ ls -al
합계 20
drwxr-xr-x 4 zany mysql 4096 2017-12-01 18:59 .
drwxr-xr-x 8 zany mysql 4096 2017-12-01 18:59 ..
drwxr-xr-x 2 zany mysql 4096 2017-12-01 18:59 config
drwxr-xr-x 2 zany mysql 4096 2017-12-01 18:59 lib
-rwxr-xr-x 1 zany mysql 1523 2017-12-01 18:38 runner
```

### Usage

```
[zany@titan _release]$ ./runner

Linux 2.6.32-696.13.2.el6.x86_64 #1 SMP Thu Oct 5 21:22:16 UTC 2017
--------------------------------------------
>>> Server started at port 10038
    - Disconnect after complete - true
    - Disconnect delay millis   - 10
--------------------------------------------
```

### application.yml

* server configuration
```
netty4.server:
  port: 10038
  backlog-size: 128
  send-message: "Message Received."
  send-delay-millis: 0
  disconnect-after-complete: true
  disconnect-delay-millis: 10
```
