# Security

Support message delivery via email, SMS, WhatApp

## Configuration

| Property                                                  | Default | Description                                                                                                                 |
|-----------------------------------------------------------|---------|-----------------------------------------------------------------------------------------------------------------------------|
| wutsi.platform.messaging.push.type                        | none    | Type of Push notification gateway: `none` or `firebase`                                                                     |
| wutsi.platform.messaging.push.firebase.credentials        | none    | Content of the Google Application Credential (in JSON format) - (REQUIRED if `wutsi.platform.messaging.push.type=firebase`) |
| wutsi.platform.messaging.sms.type                         | none    | Type of SMS gateway: `none` or `sns`                                                                                        |
| wutsi.platform.messaging.sms.aws.sns.region               |         | AWS SNS Region (REQUIRED if `wutsi.platform.messaging.sms.type=sns`)                                                        |
| wutsi.platform.messaging.whatsapp.type                    | none    | Whatsapp Implementation: `none` or `cloud`                                                                                  |
| wutsi.platform.messaging.whatsapp.cloud.access-token      |         | Whatsapp Cloud API access token. (REQUIRED if `wutsi.platform.messaging.whatsapp.type=cloud`)                               |
| wutsi.platform.messaging.whatsapp.cloud.phone-id          |         | Whatsapp Cloud API phone ID. (REQUIRED if `wutsi.platform.messaging.whatsapp.type=cloud`)                                   |
| wutsi.platform.messaging.url-shortener.type               | none    | Type of URL shortener: `none` or `bitly`                                                                                    |
| wutsi.platform.messaging.url-shortener.bitly.access-token |         | (REQUIRED) Bitly access token                                                                                               |

## Beans

| Name                     | Type                      | Description                                              |
|--------------------------|---------------------------|----------------------------------------------------------|
| urlShortener             | UrlShortener              | Instance of the service that shorten the URLs            |
| messagingServiceProvider | MessagingServiceProvider  | Service that returns implementation of messaging service |
