#Custom Undertow Filter for CSRF attack

#Overview

It is a simple implementation of custom Undertow filter that is deployed as a WildFly module and it is configured in the &lt;filters&gt; section of the Undertow subsystem's configuration. This filter checks each HTTP request for the referer details and if it is not a valid one, then stops proceeding with the request.

Valid Referer details - It can be a string separated with multiple URLs such as login URL, application url.

#Build

```
mvn clean install
```

#WildFly Configuration

```
<subsystem xmlns="urn:jboss:domain:undertow:9.0" default-server="default-server" default-virtual-host="default-host" default-servlet-container="default" default-security-domain="other" statistics-enabled="true">
	<buffer-cache name="default"/>
	<server name="default-server">
		<ajp-listener name="ajp" socket-binding="ajp"/>
		<http-listener name="default" socket-binding="http" >
		<https-listener name="https" socket-binding="https" />
		<host name="default-host" alias="localhost">
			<location name="/" handler="welcome-content"/>
			<filter-ref name="anticorsfilter"/>
		</host>
	</server>
	<servlet-container name="default">
		<jsp-config/>
		<websockets/>
	</servlet-container>
	<handlers>
		<file name="welcome-content" path="${jboss.home.dir}/welcome-content"/>
	</handlers>
	<filters>
		<filter name="anticorsfilter" class-name="com.jayaprabahar.wildfly.filter.anticors.AntiCorsCustomFilter" module="com.jayaprabahar.wildfly.filter.anticors"/>
	</filters>
</subsystem>       

```

#Deploy

Update ./bin/deploy configuration and run:

```
mvn clean install deploy -Dmaven.test.skip=true
```


