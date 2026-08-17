# Valkey Connector for Camunda 8

A Camunda 8 outbound connector for interacting with
[Valkey](https://valkey.io/), an open-source, Redis-compatible in-memory
data store.

The connector supports both **standalone** and **cluster** Valkey
deployments and provides common key-value, hash, list, set, counter,
key-management, and Pub/Sub operations.

------------------------------------------------------------------------

## 1. What is Valkey?

**Valkey** is an open-source, high-performance data store that supports:

-   Key-value storage
-   Hashes
-   Lists
-   Sets
-   Counters
-   Expiration/TTL
-   Key scanning and management
-   Publish/Subscribe messaging
-   Standalone and cluster deployments
-   ACL-based authentication
-   TLS/SSL connections

This connector allows these capabilities to be used directly from
**Camunda 8 BPMN workflows**.

------------------------------------------------------------------------

# 2. Main Features

## Connection

-   Standalone Valkey
-   Valkey Cluster

## Authentication

-   Username
-   Password
-   ACL authentication
-   SSL/TLS option

## Key-Value Operations

-   GET
-   SET
-   DELETE
-   EXISTS
-   EXPIRE
-   TTL

## Counter Operations

-   INCR
-   DECR
-   INCRBY
-   DECRBY

## Hash Operations

-   HGET
-   HSET
-   HGETALL
-   HDEL

## List Operations

-   LPUSH
-   RPUSH
-   LPOP
-   RPOP

## Set Operations

-   SADD
-   SREM
-   SMEMBERS
-   SISMEMBER

## Key Management

-   SCAN
-   TYPE
-   RENAME

## Pub/Sub

-   PUBLISH
-   SUBSCRIBE


------------------------------------------------------------------------

# 3. Prerequisites

Before using the connector, install:

### Required

-   Java 21 or compatible Java version used by your connector project
-   Maven 3.9+
-   Camunda 8 / Camunda 8 Run
-   Docker Desktop (recommended for local Valkey)
-   Camunda Modeler

### Optional

-   IntelliJ IDEA
-   Valkey CLI

------------------------------------------------------------------------

# 4. Install Valkey Locally with Docker

The easiest way to test the connector is to run Valkey using Docker.

## Pull Valkey

``` bash
docker pull valkey/valkey:9.1.1
```

## Start Valkey

``` bash
docker run -d \
  --name valkey \
  -p 6379:6379 \
  valkey/valkey:9.1.1
```

On Windows PowerShell, the equivalent can be written on one line:

``` powershell
docker run -d --name valkey -p 6379:6379 valkey/valkey:9.1.1
```

Check the container:

``` powershell
docker ps
```

Expected result:

``` text
valkey    valkey/valkey:9.1.1    0.0.0.0:6379->6379/tcp
```


------------------------------------------------------------------------

# 5. Test the Host and Port

The default standalone configuration is:

``` text
Host: localhost
Port: 6379
```

From Windows PowerShell:

``` powershell
Test-NetConnection 127.0.0.1 -Port 6379
```

Expected:

``` text
TcpTestSucceeded : True
```

------------------------------------------------------------------------

# 6. Authentication with Valkey ACL

Valkey supports ACL users.

For example, create a user named `demo`:

``` text
ACL SETUSER demo on >demo123 ~* +@all
```

Check the user:

``` text
ACL GETUSER demo
```

For Pub/Sub, the user also needs channel permissions.

A safe development example is:

``` text
ACL SETUSER demo resetchannels &* +@all
```

Then verify:

``` text
ACL GETUSER demo
```

You should see channel permissions similar to:

``` text
channels
&*
```

> `+@all`, `~*`, and `&*` are broad permissions and are suitable for
> local development/testing. For production, use the minimum permissions
> required by your application.

------------------------------------------------------------------------

# 7. Test Authentication

From PowerShell:

``` powershell
docker exec -it valkey valkey-cli -u redis://demo:demo123@127.0.0.1:6379
```

Then:

``` text
PING
```

Expected:

``` text
PONG
```

Test:

``` text
SET test "Hello"
GET test
```

Expected:

``` text
"Hello"
```

------------------------------------------------------------------------

# 8. Build the Connector

Clone or open the connector project.

From the project directory:

``` powershell
mvn clean package
```

For a complete build:

``` powershell
mvn clean install
```

The generated JAR will normally be available under:

``` text
target/
```

Example:

``` text
target/valkey-connector.jar
```

------------------------------------------------------------------------

# 9. Standalone Configuration

For local Valkey:

``` text
Connection Type: STANDALONE
Host: localhost
Port: 6379
Username: demo
Password: demo123
SSL/TLS: false
```

Example:

``` json
{
  "connectionType": "STANDALONE",
  "host": "localhost",
  "port": 6379,
  "username": "demo",
  "password": "demo123",
  "ssl": false
}
```

------------------------------------------------------------------------

# 10. Cluster Configuration

For cluster mode, provide the cluster nodes.

Example:

``` json
{
  "connectionType": "CLUSTER",
  "clusterNodes": [
    "node1:6379",
    "node2:6379",
    "node3:6379"
  ],
  "username": "demo",
  "password": "demo123",
  "ssl": false
}
```

The exact node addresses depend on your Valkey cluster deployment.

------------------------------------------------------------------------

# 11. Supported Operations

## GET

Returns the value stored against a key.

``` text
Key: customer:100
```

Equivalent Valkey command:

``` text
GET customer:100
```

Example result:

``` text
Suresh
```

------------------------------------------------------------------------

## SET

Stores a value.

``` text
Key: customer:100
Value: Suresh
```

Equivalent command:

``` text
SET customer:100 Suresh
```

------------------------------------------------------------------------

## DELETE

Deletes a key.

``` text
Key: customer:100
```

Equivalent command:

``` text
DEL customer:100
```

------------------------------------------------------------------------

## EXISTS

Checks whether a key exists.

Equivalent command:

``` text
EXISTS customer:100
```

Example:

``` text
1
```

------------------------------------------------------------------------

## EXPIRE

Sets an expiration time.

Example:

``` text
Key: session:100
Expiration Seconds: 300
```

Equivalent command:

``` text
EXPIRE session:100 300
```

The key expires after 300 seconds.

------------------------------------------------------------------------

## TTL

Returns the remaining expiration time.

``` text
TTL session:100
```

Example:

``` text
245
```

------------------------------------------------------------------------

# 12. Counter Operations

## INCR

Increases a numeric value by 1.

``` text
INCR loginCount
```

Example:

``` text
1
```

Run again:

``` text
2
```

------------------------------------------------------------------------

## DECR

Decreases a numeric value by 1.

``` text
DECR loginCount
```

------------------------------------------------------------------------

## INCRBY

Increases a value by a specified amount.

``` text
Key: score
Amount: 10
```

Equivalent:

``` text
INCRBY score 10
```

------------------------------------------------------------------------

## DECRBY

Decreases a value by a specified amount.

``` text
DECRBY score 5
```

------------------------------------------------------------------------

# 13. Hash Operations

Hashes are useful for storing object-like data.

Example:

``` text
customer:100
    name = Suresh
    city = Mumbai
    status = ACTIVE
```

## HSET

``` text
Key: customer:100
Field: name
Value: Suresh
```

Equivalent:

``` text
HSET customer:100 name Suresh
```

------------------------------------------------------------------------

## HGET

``` text
HGET customer:100 name
```

Result:

``` text
Suresh
```

------------------------------------------------------------------------

## HGETALL

``` text
HGETALL customer:100
```

Returns all fields and values.

------------------------------------------------------------------------

## HDEL

``` text
HDEL customer:100 city
```

Deletes a hash field.

------------------------------------------------------------------------

# 14. List Operations

Lists are useful for queues and ordered collections.

## LPUSH

Adds an element to the left.

``` text
LPUSH orders order1
```

## RPUSH

Adds an element to the right.

``` text
RPUSH orders order2
```

## LPOP

Removes the first element:

``` text
LPOP orders
```

## RPOP

Removes the last element:

``` text
RPOP orders
```

------------------------------------------------------------------------

# 15. Set Operations

Sets store unique values.

## SADD

``` text
SADD roles ADMIN
```

## SREM

``` text
SREM roles ADMIN
```

## SMEMBERS

``` text
SMEMBERS roles
```

## SISMEMBER

Checks membership:

``` text
SISMEMBER roles ADMIN
```

------------------------------------------------------------------------

# 16. Key Management

## TYPE

Returns the type of a key.

``` text
TYPE customer:100
```

Possible results include:

``` text
string
hash
list
set
```

------------------------------------------------------------------------

## RENAME

Renames a key.

``` text
Key: oldKey
New Key: newKey
```

Equivalent:

``` text
RENAME oldKey newKey
```

------------------------------------------------------------------------

## SCAN

SCAN is used to iterate through keys without blocking the entire
database.

Example:

``` text
Cursor: 0
Pattern: user:*
Count: 100
```

Equivalent concept:

``` text
SCAN 0 MATCH user:* COUNT 100
```

The response contains:

-   Next cursor
-   Matching keys

Continue scanning with the returned cursor until the cursor becomes `0`.

Do not use `KEYS *` for large production databases because it can block
the server.

------------------------------------------------------------------------

# 17. Pub/Sub

Pub/Sub allows one application to publish messages and another
application to receive them.

Architecture:

``` text
Publisher
    |
    | PUBLISH
    v
Valkey
    |
    | channel
    v
Subscriber
```

------------------------------------------------------------------------

# 18. PUBLISH

The connector supports:

``` text
Operation: PUBLISH
Channel: Test
Message: Hello Valkey
```

Equivalent Valkey command:

``` text
PUBLISH Test "Hello Valkey"
```

The return value is the number of subscribers that received the message.

Example:

``` text
1
```

------------------------------------------------------------------------

# 19. SUBSCRIBE

The connector supports:

``` text
Operation: SUBSCRIBE
Channel: Test
```

Equivalent Valkey command:

``` text
SUBSCRIBE Test
```

The connector creates a Pub/Sub connection and registers a listener.

Conceptually:

``` java
connection.addListener(new RedisPubSubAdapter<String, String>() {

    @Override
    public void message(String channel, String message) {
        System.out.println("Channel: " + channel);
        System.out.println("Message: " + message);
    }
});
```

After subscribing, messages can arrive asynchronously.

------------------------------------------------------------------------

# 20. Testing Pub/Sub

First start the Camunda `SUBSCRIBE` operation:

``` text
Channel: Test
```

The application should log:

``` text
SUBSCRIBED: Test, count=1
```

Then open another terminal:

``` powershell
docker exec -it valkey valkey-cli -u redis://demo:demo123@127.0.0.1:6379
```

Publish:

``` text
PUBLISH Test "Hello Valkey"
```

The subscriber application should receive:

``` text
Received message from Valkey
Channel : Test
Message : Hello Valkey
```

You can publish multiple messages:

``` text
PUBLISH Test "Message 1"
PUBLISH Test "Message 2"
PUBLISH Test "Message 3"
```

The registered listener receives them automatically.

------------------------------------------------------------------------

# 21. Example BPMN Process

A simple process can be:

``` text
Start
  |
  v
Valkey SET
  |
  v
Valkey GET
  |
  v
End
```

For example:

### SET

``` text
Operation: SET
Key: customer:100
Value: Suresh
Result Variable: setResult
```

### GET

``` text
Operation: GET
Key: customer:100
Result Variable: customerName
```

The process variable can then contain:

``` json
{
  "customerName": "Suresh"
}
```

------------------------------------------------------------------------

# 22. Result Variable

Use the connector's result variable to store the operation response.

Example:

``` text
Result Variable: result
```

For GET:

``` text
result = "Suresh"
```

For EXISTS:

``` text
result = true
```

For TTL:

``` text
result = 245
```

For PUBLISH:

``` text
result = 1
```

The exact result type depends on the Valkey operation.

------------------------------------------------------------------------

# 23. Error Handling

Typical errors include:

### Authentication failure

``` text
WRONGPASS invalid username-password pair
```

Check:

``` text
Username
Password
```

and verify the ACL user.

### Pub/Sub permission failure

``` text
NOPERM No permissions to access a channel
```

Check the user's ACL channel permissions.

For development:

``` text
ACL SETUSER demo resetchannels &* +@all
```

### Connection failure

``` text
RedisConnectionException:
Unable to connect to localhost:6379
```

Check:

``` text
Valkey container
Port mapping
Host
Docker networking
Firewall
```

------------------------------------------------------------------------
