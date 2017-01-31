Useful libraries for tests automation based on Java (Groovy) programming language
===================

Parts
------------- 
 1. **ta-base** - base helpers, workarounds and other useful code
 2. **api-ta-base** - useful code for REST API tests automation
 3. **web-ta-base** - useful code for Web tests automation
 
Powered by
------------- 

 1. Gradle
 2. TestNG
 3. Cucumber JVM
 4. Selenium WebDriver / WebDriver manager
 5. Selenide
 6. Yandex Allure / Properties / AShot
 7. Rest-Assured
 8. Jackson
 9. Hamcrest
 10. Lombok
 
 and others

Requirements
-------------

 1. JDK 8+
 2. Gradle 3.3+
 3. Lombok plugin for IDE that you use
 4. Code style settings that matches config/checksyle/checkstyle.xml
    For Intellij IDEA you can use config/codestyle/Code style for IDEA.xml

Publishing 
-------------

For publishing into local Maven repository you can use command:

    gradle rebuildAndPublish


For publishing api and web parts into local Maven repository with ta-base (because it dependency for api and web):

    gradle rebuildAndPublishWithSubs



Code static analysis
-------------

Used style guides:
 1. Google Java Style Guide with some changes

CSA frameworks:
 1. Findbugs
 2. PMD
 3. Checkstyle

TODO
-------------

 - build and deploy to Maven Central via Travis CI
 - java docs
 - unit tests
 - module for interactions with e-mail servers
 - module for interactions with databases (not sure)
