Generates the code for an API server, from an OpenAPIV3 specification.

The code generated includes:

- The code shell for each of the API endpoint in [Kotlin](https://kotlinlang.org/)
  and [Springboot](https://spring.io/projects/spring-boot)
- The [Maven](https://maven.apache.org/) build configuration files
- The [Github Actions](https://github.com/features/actions) scripts for building and deploying the API
  to [Heroku](https://www.heroku.com)
- The configuration files to launch the API in [Heroku](https://www.heroku.com):
    - Profile
    - system.properties
- The API documentation in [Swagger](https://swagger.io/)
- Others:
    - ``.editorconfig`` for the formatting rules
    - ``.gitignore`` for the Github exclusion files
    - ``renovate.json`` for automating dependencies upgrades
      with [Renovate Bot](https://github.com/renovatebot/renovate)

## Usage

```
java wutsi-codegen-<version>.jar server [options]
 -github_project <github-project>   The github project name
 -github_user <github-user>         Your github username
 -heroku <heroku-app>               Heroku application name. This will trigger the deployment when merging to `master` branch.
                                    IMPORTANT: The github secret HEROKU_API_KEY must be configured.
 -in <openapi-file-url>             (REQUIRED) URL of the OpenAPIV3 file that describe the API
 -jdk <jdk-version>                 Version of the JDK of the project. Default: 1.8
 -name <api-name>                   (REQUIRED) Name of the API. Ex: like
 -out <output-dir>                  (REQUIRED) Output directory. Where to store the generated files
 -package <base-package>            (REQUIRED) Base package of the api. Ex: com.foo.bar
 -service_cache                     Attach a cache to the API server
 -service_database                  Attach a database to the API server
 -service_logger                    Attach a logger to the API server
 -service_mqueue                    Attach a message queue to the API server
 -service_aws                       Attach AWS environment to the API server
 -service_aws_mysql                 Use AWS MySQL
 -service_aws_postgres              Use AWS Postgres
 -service_api-key                   Attach API-Key information
 -service_slack                     Integrate Slack notification + logging
 -service_swagger                   Generate swagger documentation
```
