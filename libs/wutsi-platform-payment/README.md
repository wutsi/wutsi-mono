[![](https://github.com/wutsi/wutsi-mono/actions/workflows/libs-wutsi-platform-payment-master.yml/badge.svg)](https://github.com/wutsi/wutsi-mono/actions/workflows/libs-wutsi-platform-payment-master.yml)

[![JDK](https://img.shields.io/badge/jdk-17-brightgreen.svg)](https://jdk.java.net/17/)
[![](https://img.shields.io/badge/maven-3.6-brightgreen.svg)](https://maven.apache.org/download.cgi)
![](https://img.shields.io/badge/language-kotlin-blue.svg)

`wutsi-platform-payment` is a library that provide payment API

## Features

| Provider    | MTN                | Orange             | Bank               | Credit Card  |
|-------------|--------------------|--------------------|--------------------|--------------|
| Flutterwave | :white_check_mark: | :white_check_mark: | :white_check_mark: | :red_circle: |
| MTN         | :white_circle:     | :red_circle:       | :red_circle:       | :red_circle: |
| Paypal      | :white_circle:     | :red_circle:       | :red_circle:       | :red_circle: |

## Flutterwave Spring Configuration

Implementation of the payment gateway based on [Flutterwawe](https://www.flutterwave.com)

| Property                                          | Default Value | Description                                                                                                                                                   |
|---------------------------------------------------|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| wutsi.platform.payment.flutterwave.enabled        | false         | `true` to enable Flutterwave payment gateway                                                                                                                  |
| wutsi.platform.payment.flutterwave.secret-key     |               | Flutterwave secret key                                                                                                                                        |
| wutsi.platform.payment.flutterwave.encryption-key |               | Flutterwave encryption key                                                                                                                                    |
| wutsi.platform.payment.flutterwave.test-mode      | true          | Run in test-mode? if `true`, the API will use the test bank codes (`044`) - See See https://developer.flutterwave.com/docs/integration-guides/testing-helpers |

## Paypal Spring Configuration

Implementation of the payment gateway based on [Paypal](https://www.paypal.com)

| Property                                     | Default Value | Description                                  |
|----------------------------------------------|---------------|----------------------------------------------|
| wutsi.platform.payment.paypal.enabled        | false         | `true` to enable Flutterwave payment gateway |
| wutsi.platform.payment.paypal.client-id      |               | Paypal secret key                            |
| wutsi.platform.payment.paypal.secret-key     |               | Paypal secret key                            |
| wutsi.platform.payment.paypal.encryption-key |               | Paypal encryption key                        |
| wutsi.platform.payment.paypal.test-mode      | true          | Run in test-mode?                            |

## MTN Spring Configuration

| Property                                                 | Default Value | Description                                              |
|----------------------------------------------------------|---------------|----------------------------------------------------------|
| wutsi.platform.payment.mtn.enabled                       | false         | `true` to enable MTN payment gateway                     |
| wutsi.platform.payment.mtn.environment                   |               | REQUIRED. `sandbox` or `production`                      |
| wutsi.platform.payment.mtn.callback-url                  |               | REQUIRED. Callback URL                                   |
| wutsi.platform.payment.mtn.collection.subscription-key   |               | REQUIRED. Subscription Key of the Collection API         |
| wutsi.platform.payment.mtn.collection.user-id            |               | Collection User ID. REQUIRED in production environment   |
| wutsi.platform.payment.mtn.collection.api-key            |               | Collection API Key. REDIURED in production environment   |
| wutsi.platform.payment.mtn.disbursement.subscription-key |               | REQUIRED. Subscription Key of the Disbursement API       |
| wutsi.platform.payment.mtn.disbursement.user-id          |               | Disbursement User ID. REQUIRED in production environment |
| wutsi.platform.payment.mtn.disbursement.api-key          |               | Disbursement API Key. REQUIRED in production environment |
