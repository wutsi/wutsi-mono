# Logging

This module for logging information in a key/value pair format.

## Beans

| Name             | Type           | Description                                                                    |
|------------------|----------------|--------------------------------------------------------------------------------|
| KVLogger         | KVLogger       | Instance of the current logger                                                 |
| KVLoggerFilter   | KVLoggerFilter | Servlet filter that add standard logging information for each request/response |

## HTTP Request Logging Information

Here are the list of key/pair value that will be included in all HTTP request logs

- `http_endpoint`: The request URI (Ex. `http_endpoint=/v1/user`)
- `http_method`: The request HTTP method (Ex. `http_method=GET`)
- `http_status`: The HTTP status code
- `http_param_<name>`: HTTP request parameter. (where `<name>` is the request parameter)
- `client_id`: The name of the client
- `device_id`: The user device identifier
- `trace_id`: ID of the current trace
- `tenant_id`: ID of the current tenant
- `success`: `true`|`false` if the request is successful or not
- `latency_millis`: The duration of the request in milliseconds
- `X-Api-Key`: Hidden value header `X-Api-Key`
- `Authorization`: Hidden value header `Authorization`
