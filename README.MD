Useful libraries for tests automation based on Java programming language and different libraries
===================

Parts
------------- 
 1. **ta-base** - base helpers, workarounds and other useful code
 2. **api-ta-base** - useful code for REST API tests automation
 3. **web-ta-base** - useful code for Web tests automation
 
Powered by
------------- 

 1. [Gradle](https://gradle.org/docs#getting-started)
 2. [TestNG](http://testng.org/doc/documentation-main.html)
 3. [Cucumber JVM](https://cucumber.io/docs) and [Kirlionik cucumber plugin for Allure](https://github.com/kirlionik/allure-cucumber-plugin)
 4. [Selenium WebDriver](http://www.seleniumhq.org/projects/webdriver/)
 5. [Selenide](http://selenide.org/)
 6. [Yandex Allure / Properties / AShot / WebDriver Matchers](http://qatools.ru/)
 7. [Rest-Assured](https://github.com/rest-assured/rest-assured/wiki/GettingStarted)
 8. [Hamcrest Matchers](http://hamcrest.org/JavaHamcrest/)
 9. [Lombok](https://projectlombok.org/)
 10. [Awaitility](https://github.com/awaitility/awaitility/wiki/Getting_started)
 
 and others

Requirements
-------------

 1. JDK 8+
 2. Gradle 3.3+
 3. Lombok plugin for IDE which you use
 4. Code style settings that matches config/checksyle/checkstyle.xml
    For Intellij IDEA you can use config/codestyle/Code style for IDEA.xml

Publishing (temporary workaround)
-------------

 - Publish only to local maven repo and Nexus (via username/password)
 - For publishing both into local Maven repository and Nexus you can use command inside folder of each module:
 
```sh
    gradle rebuildAndPublish -Dnexus.user=nexusLogin -Dnexus.password=nexusPass -Dnexus.repo.releases=http://nexusUrl/releasesRepositoryPath/ -Dnexus.repo.snapshots=http://nexusUrl/snapshotsRepositoryPath
```
 - For publishing only into local Maven repository you can use command inside folder of each module:

```sh
    gradle clean publishToMavenLocal
```
Code static analysis
-------------

Used style guides:
 1. [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with some changes

CSA frameworks:
 1. [Findbugs](http://findbugs.sourceforge.net/)
 2. [PMD](https://pmd.github.io/)
 3. [Checkstyle](http://checkstyle.sourceforge.net/)
 4. [Error Prone](http://errorprone.info/)

TODO
-------------
 - java docs
 - build and deploy to Maven Central via Travis CI
 - static analysis via SonarQube 
 - unit tests
 - module for interactions with databases via JDBI 3 (not sure)
 - module for interactions with e-mail servers (not sure)
