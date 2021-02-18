FROM tomcat:9.0
MAINTAINER Evolved Binary

COPY opentheso-4.5.9.war /usr/local/tomcat/webapps/opentheso.war

RUN mkdir /usr/local/tomcat/webapps/opentheso \
	&& unzip -d /usr/local/tomcat/webapps/opentheso/ /usr/local/tomcat/webapps/opentheso.war \
	&& mkdir -p /var/www/images/resize


# Modify the config for Opentheso
COPY preferences.properties /usr/local/tomcat/webapps/opentheso/WEB-INF/classes/
COPY hikari.properties /usr/local/tomcat/webapps/opentheso/WEB-INF/classes/
