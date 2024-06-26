<img src="https://raw.githubusercontent.com/CREDITSCOM/Documentation/master/Src/Logo_Credits_horizontal_black.png" align="center">

[Documentation](https://developers.credits.com/en/Articles/Platform) \|
[Guides](https://developers.credits.com/en/Articles/Guides) \|
[News](https://credits.com/en/Home/News)

[![Twitter](https://img.shields.io/twitter/follow/creditscom.svg?label=Follow&style=social)](https://twitter.com/intent/follow?screen_name=creditscom)
[![AGPL License](https://img.shields.io/github/license/CREDITSCOM/ewa.svg?color=green&style=plastic)](contract-executor/LICENSE)
[![Build Status](http://89.111.33.166:8080/buildStatus/icon?job=ewa&lastBuild)](http://89.111.33.166:8080/job/ewa//lastBuild/)

## Contract Executor
Contract Executor is app for deploy and execute methods of smart-contract


##### Used environment
- [JDK 11](https://openjdk.java.net/projects/jdk/11/)
- [Maven version 3.6.1](https://maven.apache.org/docs/3.6.1/release-notes.html) 

##### How to build
##### Maven build compilation from sources files for contract-executor

Maven - Is a tool that can be used for building and managing any Java-based project. It is a command-line tool for building Java (and other) programs developed by Apache.

It's up to you to set up your prerequisites and environment to run Maven on Windows. Therefore, the major prerequisite needed is Java SDK installation because Maven is written in Java and primarily it is used to build Java programs.

The installation of Apache Maven implies extracting the archive and adding the bin folder with the mvn command to the PATH.

Detailed steps are:



1. Download and install JDK and Add ‘JAVA_HOME’ Environment Variable. After installing Java you must add JAVA_HOME variable to JDK install folder. In addition, ‘java/bin’ directory must be included in ‘PATH’ variable.

    

<img src="https://github.com/CREDITSCOM/contract-executor/blob/master/.github/readme-images/11.PNG" align="center">


2. Download and install Maven and set the ‘M2_HOME’ and ‘MAVEN_HOME’ variables to maven installation folder. You can download Maven [here](https://maven.apache.org/download.cgi).

    

<img src="https://github.com/CREDITSCOM/contract-executor/blob/master/.github/readme-images/22.PNG" align="center">



3. Afterward, ‘maven/bin’ directory must be included in ‘PATH’ variable also. To run maven from command prompt, this is necessary so update the PATH variable with 'maven/bin' directory. 

    

<img src="https://github.com/CREDITSCOM/contract-executor/blob/master/.github/readme-images/33.PNG" align="center">



4. Check the installation by typing the following command in the console:
``` mvn -version```

    
<img src="https://github.com/CREDITSCOM/contract-executor/blob/master/.github/readme-images/44.PNG" align="center">



5. Install [Git](https://git-scm.com/download/win) and clone the thrift-api-client repository from GitHub: [https://github.com/CREDITSCOM/thrift-api-client](https://github.com/CREDITSCOM/thrift-api-client)
6. Change to the directory by using command:_ cd_
7. Change the working brach to dev: git checkout dev
8. Once on the directory write the following command in the prompt to build compilation from sources files: 
```mvn clean install```



<img src=".github/readme-images/5.PNG" align="center">


9. Now, repeat the process for smart-contract-api repository. Clone it from the repository: [https://github.com/CREDITSCOM/smart-contract-api](https://github.com/CREDITSCOM/smart-contract-api)
10. Change the working brach to dev: git checkout dev
11. Change to the directory by using command:_ cd. _Then build compilation from sources by using the command: 
```mvn clean install```

<img src=".github/readme-images/6.PNG" align="center">


12. Finally clone contract-executor repository from: [https://github.com/CREDITSCOM/contract-executor](https://github.com/CREDITSCOM/contract-executor)
13. Change the working brach to dev: git checkout dev
14. Change to the contract-executor directory and build it by using:```mvn clean install```. If all the steps were performed properly and secuently, errors won’t be.

    

<img src="https://github.com/CREDITSCOM/contract-executor/blob/master/.github/readme-images/77.PNG" align="center">

## Build
### Windows
```shell
git clone https://github.com/CREDITSCOM/thrift-api-client.git
cd thrift-api-client
git checkout dev
call mvn clean install
cd..

git clone https://github.com/CREDITSCOM/smart-contract-api.git
cd smart-contract-api
git checkout dev
call mvn clean install
cd..

git clone https://github.com/CREDITSCOM/contract-executor.git
cd contract-executor
git checkout dev
cd sc-api-support
call mvn clean install
cd..
cd contract-executor
call mvn clean install
cd..
cd..

rem Warning!!!
rem get these files with the same suffix in the target folders
rem example
rem sc-api-support-v0.8.4-b35.jar
rem contract-executor-v0.8.4-b35.jar 
rem rename them to sc-api-support.jar and contract-executor.jar -> node directory
rem DO NOT FORGET to change next lines in [api] department of node config.ini file to:
rem executor_commit_min=1518
rem executor_commit_max=1518

```

### Ubuntu 18.04
```shell
git clone https://github.com/CREDITSCOM/thrift-api-client.git
cd thrift-api-client
git checkout dev
mvn clean install
cd ..

git clone https://github.com/CREDITSCOM/smart-contract-api.git
cd smart-contract-api
git checkout dev
mvn clean install
cd ..

git clone https://github.com/CREDITSCOM/contract-executor.git
cd contract-executor
git checkout dev
cd sc-api-support
mvn clean install
cd ..
cd contract-executor
mvn clean install
cd ..
cd ..

# Warning!!!
# get these files with the same suffix in the target folders
# example
# sc-api-support-v0.8.4-b35.jar
# contract-executor-v0.8.4-b35.jar 
# rename them to sc-api-support.jar and contract-executor.jar -> node directory
# DO NOT FORGET to change next lines in [api] department of node config.ini file to:
# executor_commit_min=1518
# executor_commit_max=1518
```

<h3>Resources</h3>

<a href="https://credits.com//">CREDITS Website</a>

<a href="https://github.com/CREDITSCOM/DOCUMENTATION">Documentation</a>

<a href="https://credits.com/Content/Docs/TechnicalWhitePaperCREDITSEng.pdf">Whitepaper</a>

<a href="https://credits.com/Content/Docs/TechnicalPaperENG.pdf">Technical paper</a>

<a href="https://developers.credits.com/">Developers portal</a>

<a href="http://forum.credits.com/">Credits forum</a>
<h3>Community links</h3>
   <a href="https://t.me/creditscom"><img src ="https://simpleicons.org/icons/telegram.svg" height=40 widht=40 ></a>
   <a href="https://twitter.com/creditscom"><img src ="https://simpleicons.org/icons/twitter.svg" height=40 widht=40 ></a>
   <a href="https://www.reddit.com/r/CreditsOfficial/"><img src ="https://simpleicons.org/icons/reddit.svg" height=40 widht=40></a> 
   <a href="https://medium.com/@credits"><img src="https://simpleicons.org/icons/medium.svg" height=40 widht=40></a>
   <a href="https://www.instagram.com/credits_com/"><img src="https://simpleicons.org/icons/facebook.svg" height=40 widht=40></a>
   <a href="https://www.facebook.com/creditscom"><img src="https://simpleicons.org/icons/instagram.svg" height=40 widht=40></a>
   <a href="https://www.youtube.com/channel/UC7kjX_jgauCqmf_a4fqLGOQ"><img src="https://simpleicons.org/icons/youtube.svg" height=40 widht=40></a>
