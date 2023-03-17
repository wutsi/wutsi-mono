#/bin/sh

CODEGEN_JAR=~/wutsi-codegen/wutsi-codegen.jar

API_NAME=security-manager
API_URL=https://raw.githubusercontent.com/wutsi-mono/api/wutsi-openapi/master/src/openapi/v2/security-manager.yaml
GITHUB_USER=wutsi


echo "Generating code from ${API_URL}"
java -jar ${CODEGEN_JAR} sdk \
    -in ${API_URL} \
    -out . \
    -name ${API_NAME} \
    -package com.wutsi.security.manager \
    -jdk 11 \
    -github_user ${GITHUB_USER} \
    -github_project ${API_NAME}-sdk-kotlin

if [ $? -eq 0 ]
then
    echo Code Cleanup...
    mvn antrun:run@ktlint-format
    mvn antrun:run@ktlint-format

else
    echo "FAILED"
    exit -1
fi
