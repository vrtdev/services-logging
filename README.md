
# General Concepts

* logging should be present and transparent for developer
* logging can in no case block the application flow
* logging should be easy accessible to the developer

# Components
**Collector**: The logging collector is responsible for collecting and grouping data. For example, the collector will tag the MDC (local thread) for grouping all log-statements from 1 call.

**Consumer**: The logging Consumer contains the implementations of appenders that can be configured in the logback.xml. The consumer will convert the data to JSON to be able to save it to Elasticsearch.

**Exposer**: The logging exposer is an HTML extension to easily expose the logging saved in Elasticsearch. The exposers hides the data from ES and privides a convienent JSON API & HTML visualisation based on the data.


# User information

Installation Guide (In progress)
1. Include Jars
The jars provide an extension on logback and are using slf4j for logging.

	<dependency>
		<groupId>be.vrt.services.logging</groupId>
		<artifactId>logging-collector</artifactId>
		<version>${be.vrt.services.logging-version}</version>
	</dependency>
	<dependency>
		<groupId>be.vrt.services.logging</groupId>
		<artifactId>logging-consumer</artifactId>
		<version>${be.vrt.services.logging-version}</version>
	</dependency>
	<dependency>
		<groupId>be.vrt.services.logging</groupId>
		<artifactId>logging-exposer</artifactId>
		<version>${be.vrt.services.logging-version}</version>
	</dependency>

Logback xml
Add configuration for logback.
This can be done using a system-property (-Dlogback.configurationFile=/java/vrt/git/media-encoding/config/local/logback.xml)
Here a few examples for a logback.xml

	<configuration scan="true" scanPeriod="60 seconds">
		<!-- No rockhopper needed, perfect for local development and low-activity applications -->
		<appender name="LocalElastic" class="be.vrt.services.logging.log.consumer.appender.ElasticSearchAppender">
			<host>localhost</host>
			<port>9200</port>
		</appender>

		<!-- Sends data over UDP to rockhopper who injects in ES. https://www.npmjs.com/package/rockhopper -->
		<appender name="RockHopper" class="be.vrt.services.logging.log.consumer.appender.RockHopperAppender">
			<host>localhost</host>
			<port>5514</port>
		</appender>


		<logger name="be.vrt.services" level="debug" />
		<root level="info">
			<appender-ref ref="LocalElastic" />
			<appender-ref ref="RockHopper" />
		</root>
	</configuration>

!!JBoss/Wildfly config:
In META-INF folder of your application you have to disable the default JBoss logging

	<?xml version="1.0" encoding="UTF-8"?>
	<jboss-deployment-structure>
		<deployment>
			<exclude-subsystems>
				<subsystem name="logging" />
			</exclude-subsystems>
		</deployment>
	</jboss-deployment-structure>

HTTP-Filter
Add filter to automaticly tag every request with a flow-id and a transactionId

    <filter>
        <filter-name>TransactionLoggerFilter</filter-name>
        <filter-class>be.vrt.services.log.collector.transaction.http.TransactionLoggerFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>TransactionLoggerFilter</filter-name>
        <url-pattern>/rest/*</url-pattern>
    </filter-mapping>

(Optional) Transaction Listing
Expose the latest running transactions in Json Format behind a servlet.

    <servlet>
        <servlet-name>logging</servlet-name>
        <servlet-class>be.vrt.services.log.exposer.controller.TransactionLogController</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>logging</servlet-name>
        <url-pattern>/log/*</url-pattern>
    </servlet-mapping>

(Optional) Include JS-console in page

	<script scr="services/logging/log-init.js?v=3"></script>

