# Amprocessor
#### Flussonic media server streams monitoring and notification via telegram bot

## DEV NFO

#### bash-ncat webserver
```
ncat -lkv localhost 7070 -c 'tee /dev/stdout'
```

#### rabbitmq watch messages
```
watch -n1 sudo rabbitmqadmin list queues name messages messages_unacknowledged
sudo rabbitmqadmin delete queue name=test.rpc
```

#### rabbitmq watch messages web admin gui
```
http://localhost:15672/
```

#### mvn show transitive dependencies
```
mvn dependency:tree
```
#### hazelcast remove warning java-modules 9+
##### add to RUN JVM OPTIONS
```
--add-modules
java.se
--add-exports
java.base/jdk.internal.ref=ALL-UNNAMED
--add-opens
java.base/java.lang=ALL-UNNAMED
--add-opens
java.base/java.nio=ALL-UNNAMED
--add-opens
java.base/sun.nio.ch=ALL-UNNAMED
--add-opens
java.management/sun.management=ALL-UNNAMED
--add-opens
jdk.management/com.sun.management.internal=ALL-UNNAMED
```

#### check that sensitive data not in repository
```
git log --pretty=format: --name-only --diff-filter=A  | sort -u | grep "\.properties"
git log --pretty=format: --name-only --diff-filter=A  | sort -u | grep "docker"
```

#### git show all ignored files
```
git check-ignore **/*
```

#### git cleanup (delete from working dir untracked, ignored files)
```
git clean -x -X -n
```



#### remove unwanted files from all commits
```
git filter-repo --force --invert-paths --path full/path/to/unwanted.file
``` 
(install from snap)
```
sudo snap install git-filter-repo --edge
```


#### programmatically force spring cloud config client to refresh config
```
https://stackoverflow.com/questions/59105699/how-to-trigger-a-refresh-event
```

```
You can fire a RefreshEvent using an autowired ApplicationEventPublisher. 
Spring Cloud has a listener for this event in RefreshEventListener

@Autowired
private ApplicationEventPublisher eventPublisher;

public void fireRefreshEvent() {
  eventPublisher.publishEvent(new RefreshEvent(this, "RefreshEvent", "Refreshing scope");
}
```


maven get dependencies tree
```
mvn clean dependency:tree > dep.txt
```