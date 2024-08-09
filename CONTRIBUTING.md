# Setting up your environment

Wutsi is a system built using:

- [Kotlin](https://kotlinlang.org/): The programing language
- [MySQL](https://www.mysql.org): The database

### Setup Github

- Request access to [wutsi-mono](https://www.wutsi.com/wutsi/wutsi-mono)
- Create your Github Personal Access Token, and store it in a secure location
- Create the following environment variable on your computer:
    - ``GITHUB_USER``: This variable contains your Github user's name.
    - ``GITHUB_TOKEN``: This variable contains your Github Personal Access Token.

### Install Java

- Run the following commands on your CLI (command line interface):

```
brew install openjdk@17
```

### Install Maven

- Run the following commands on your CLI:

```
brew install maven
```

- Create the file ```~/.m2/setting.xml``` with the following content:

```xml

<settings>
    <repositories>
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/wutsi/wutsi-mono</url>
            <updatePolicy>always</updatePolicy>
        </repository>
    </repositories>

    <servers>
        <server>
            <id>github</id>
            <username>${env.GITHUB_USER}</username>
            <password>${env.GITHUB_TOKEN}</password>
        </server>
    </servers>
</settings>

```

### Install MySQL

- Run the following commands on your CLI:

```
brew install mysql@8.0
brew postinstall mysql@8.0

mysql -uroot
create database wutsi
```
