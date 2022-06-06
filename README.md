# Alertmanager
Notification propagation system from prometheus alertmanager  
adopted for Flussonic Watcher
* telegram bot
* mailing
* custom camera details
## << UNDER CONSTRUCTION >>


### Installation:
From sources:
### 1. Install dependencies

### ToDo: make this as git submodules
```
https://github.com/dreamworkerln/spring-utils-common
https://github.com/dreamworkerln/spring-db-mapstruct
```
### 2. Provide properties
Place next to the root folder 'amprocessor' folder named 'amprocessor-config' (repository) 
containing properties for Spring Cloud Config server. 

### 3. Configure .properties
Define you own properties, based on default:
Use you own spring profile like 'dev', 'prod' to provide custom set of properties.
Place them like 'default' profile in 'amprocessor-config' directory structure.
<br>
Start with:  
In each subproject (alerthandler, cameradetails, configserver, ...)
copy folder 'default' to ('dev','prod','test', ...) placed next to it.  
```
amprocessor-config  
  alerthandler  
    default  
    dev
    prod
    test
  cameradetails
  ...    
```
Then customize .properties files in each profile

### 4. Copy .properties to mvn modules(subprojects) resource dirs
Run 'makelinks.sh' to create .properties files to subprojects resource dirs to be able to Run/Debug/Test from Intellij Idea/mvn.
It will ln -s .properties from 'amprocessor-config' folder.
- For main/resource and test/resource will re-create absolute symlinks (delete previous links) (maven go mad of relative symlinks).  

So you should store all your .properties in amprocessor-config.
Also this prevent from leaking sensitive data (as login info) to public git repository.

### 5. Test and Debugging

#### Run tests with mvn
```
mvn -DargLine="-Dspring.profiles.active=test" clean test
```

## Step 6 should be reconsidered, drop it away for now

### 6. Configure local host systemd postgres and rabbitmq for run/debug

**This is fully optional step, you may omit it**

It's a problem to connect from docker container to host-localhost listening only services.

So host services should be reconfigured to listen on external IP or 
docker subnet IP.


                                              

<br>

#### 6.1 Configure postgres

#### Only on development host, do not do this on production!

https://www.bigbinary.com/blog/configure-postgresql-to-allow-remote-connection
```
/etc/postgres/.../postgresql.conf
listen_addresses = '*'
```
<br>  
 
Check docker default network      
```
/etc/docker/daemon.json
```
I recommend explicitly configure it like this
```
{
  "default-address-pools": [
    {
      "base": "10.20.0.0/16",
      "size": 24
    }
  ]
}
```
To avoid it interfering with existing networks.

Identify default or configured docker network:
```
ifconfig
ip addr show
```
By default, it like 172.0.0.1/16
With configured daemon.json above: 10.20.0.0/16
And remember your external IP (here assumed 192.168.1.100)

Add to pg_hba.conf
```
/etc/postgres/.../pg_hba.conf
# docker connection
host    all             all             10.20.0.0/16            md5
# local connection from host external interface
host    all             all             192.168.1.100/32        md5
```
```
sudo systemctl restart postgres
```
> Why accept connection from 192.168.1.100/32,
> docker 10.20.0.0/16 will be enough ?

<br>  

#### 6.2 Configure rabbitmq

#### Only on development host, do not do it on production!


Detect which config file is used for rabbitmq on your system
(depends of rabbitmq version)
```
/etc/rabbitmq/rabbitmq.conf 
or
/etc/rabbitmq/rabbitmq.config
```


```
/etc/rabbitmq/rabbitmq.conf

# DANGER ZONE!
#
# allowing remote connections for default user is highly discouraged
# as it dramatically decreases the security of the system. Delete the user
# instead and create a new one with generated secure credentials.
loopback_users = none
```

```
/etc/rabbitmq/rabbitmq.config 
%% this is a comment
[
  {rabbit, [
      {loopback_users, [none]}
    ]
  }
]. 
```


```
sudo systemctl restart rabbitmq-server.service
```
> rabbitmq could be configured to accept connection from different interfaces<br>
> Use again docker 10.20.0.0/16
<br>

#### 6.2 Configure host /etc/hosts

#### Only on development host, do not do it on production!

Add it to hosts file:
```
/etc/hosts
# docker containers forwading
192.168.1.100       database
192.168.1.100       rabbitmq
192.168.1.100       configserver
```
(192.168.1.100 - your host external IP) 

> Why not use 10.20.0.1 instead of 192.168.1.100
<br>
<br>

#### 6.3 Finish configuring
This configuration allows you to run on development host  
services from Intellij Idea, from docker containers,
and use postgres/rabbitmq from local systemd services or from docker containers. 


#### 7. Configure Alertmanager

Disable alerts grouping:
```
.../alermanager.yml

# The root route on which each incoming alert enters.
route:
  # The labels by which incoming alerts are grouped together. For example,
  # multiple alerts coming in for cluster=A and alertname=LatencyHigh would
  # be batched into a single group.
  # ['...'] - alerts disable grouping
  group_by: ['...']

  # How long to initially wait to send a notification for a group
  # of alerts. Allows to wait for an inhibiting alert to arrive or collect
  # more initial alerts for the same group. (Usually ~0s to few minutes.)
  group_wait: 1s

  # How long to wait before sending a notification about new alerts that
  # are added to a group of alerts for which an initial notification has
  # already been sent. (Usually ~5m or more.)
  group_interval: 1s

  # How long to wait before sending a notification again if it has already
  # been sent successfully for an alert. (Usually ~3h or more).
  # set to max to practically disable alert repeating (will fire only once in 1 year)
  repeat_interval: 8737h

  # A default receiver
  receiver: webhook

receivers:
  - name: 'webhook'
    webhook_configs:
      - url: 'http://amprocessor-host:8016'
        http_config:
          basic_auth:
            username: alertmanager
            password: PASSWORD      

```
change PASSWORD on your own

<br>

#### 8. Configure nginx reverse proxy authorization for Prometheus and Alertmanager (optional)

Create .htpasswd
```
sudo htpasswd -c /etc/nginx/.htpasswd USERNAME
```

nginx configs:
```
/etc/nginx/sites-available/prometheus_vhost
##
# You should look at the following URL's in order to grasp a solid understanding
# of Nginx configuration files in order to fully unleash the power of Nginx.
# http://wiki.nginx.org/Pitfalls
# http://wiki.nginx.org/QuickStart
# http://wiki.nginx.org/Configuration
#
# Generally, you will want to move this file somewhere, and start with a clean
# file but keep this around for reference. Or just disable in sites-enabled.
#
# Please see /usr/share/doc/nginx-doc/examples/ for more detailed examples.
##

server {
  listen 12321;
  server_name prometheus.my.domain.ru;
  location / {
    auth_basic           "Prometheus";
    auth_basic_user_file /etc/nginx/.htpasswd;
    proxy_pass           http://localhost:9090/;
  }
}

server {
  listen 12321;
  server_name alertmanager.my.domain.ru;
  location / {
    auth_basic           "Alertmanager";
    auth_basic_user_file /etc/nginx/.htpasswd;
    proxy_pass           http://localhost:9093/;
  }
}
```
where supposed proxy will listen on 0.0.0.0.:12321 and 
Prometheus listening on localhost:9090 and Alertmanager on  localhost:9093

```
cd /etc/nginx/sites-enabled

ln -s ../sites-available/prometheus_vhost 
```
test config
```
nginx -t
```
reload service
```
systemctl reload nginx.service 
```







###################### DEPRECATED - CLEAN UP THIS ###############################
    





##### Optionally.
##### Edit sgrabber configurations.
Sgrabber can use custom data for alert details, provided from third-party services
(For information that not present in prometheus, if you don't want use "label_replace(...)" and dynamically reload prometheus configurations)
instead of default alert annotation (that has been configured in prometheus/rules.yml).<br>
For example sgrabber service use camera stream name as key and put it into hazelcast Map<name,subject>
```
class Subject
    String key;
    String title;
    String description;
   
``` 

Sgrabber periodically collecting Cameras as Subjects from Watcher streaming service.<br>
(Cameras may be dynamically added/removed.)<br>
Prometheus also store metrics about cameras through custom exporter with same stream label.
When bot receive camera alert push with stream name it tries to find in hazelcast entry with that stream name as key and use
Subject fields instead default alert annotation. So user get alert like:
``` 
Camera <address> down
```
instead of
``` 
Camera 4512127d-7ed7-49ee-8e3b-1c3d0340e963 down
```

```
cd sgrabber/src/main/resources/
application.properties
sgrabber.properties
```
watcher.token got from 
```
https://flussonic.github.io/watcher-docs/api-examples.html#login
```