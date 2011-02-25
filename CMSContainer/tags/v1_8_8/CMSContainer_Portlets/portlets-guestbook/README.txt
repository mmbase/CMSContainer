DESCRIPTION:
A simple guestbook implementation

Intro:
The module portlets-guestbook includes an optional validation using jcaptcha: http://jcaptcha.sourceforge.net/
See LCM-94.

In the CMS Admin area add the following system property:
name: "guestbook.usevalidation"
value: "true" (or "false" to disable it.) 
Publish the system properties to actualize them also on the live environment.
   
Name: configuration for using the guestbook validation
Goal: configuration for using the guestbook validation with jcaptcha
Type: manual action  

Add the following in the maven-base/project.xml

<dependency>
   <groupId>jcaptcha</groupId>
   <artifactId>jcaptcha-all</artifactId>        
   <type>jar</type>
   <version>1.0-RC6</version>
   <properties>
      <war.bundle>${war.bundle}</war.bundle>
   </properties>
</dependency>

Add the following in the web.xml files from both war-live and war-staging:

   <servlet>
        <servlet-name>jcaptcha</servlet-name>
        <servlet-class>com.finalist.captcha.CaptchaServlet</servlet-class>
        <load-on-startup>0</load-on-startup>
    </servlet>
   
   <servlet-mapping>
        <servlet-name>jcaptcha</servlet-name>
        <url-pattern>/jcaptcha</url-pattern>
    </servlet-mapping>
   
