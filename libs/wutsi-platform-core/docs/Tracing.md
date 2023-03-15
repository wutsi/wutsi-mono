# Tracing

## Main Classes

- [TracingContext](https://github.com/wutsi/wutsi-platform-core/blob/master/src/main/kotlin/com/wutsi/platform/core/tracing/TracingContext.kt)
  is an interface that expose the tracing information
- [DeviceIdProvider](https://github.com/wutsi/wutsi-platform-core/blob/master/src/main/kotlin/com/wutsi/platform/core/tracing/DeviceIdProvider.kt)
  is used for getting/setting the user's device identifier.
- [DeviceIdFilter](https://github.com/wutsi/wutsi-platform-core/blob/master/src/main/kotlin/com/wutsi/platform/core/tracing/servlet/DeviceIdFilter.kt)
  is a servlet filter that ensure that each HTTP request is assigned with a unique device identifier.
- [FeignTracingRequestInterceptor](https://github.com/wutsi/wutsi-platform-core/blob/master/src/main/kotlin/com/wutsi/platform/core/tracing/FeignTracingRequestInterceptor.kt)
  is an [OpenFeign](https://github.com/OpenFeign/feign) interceptor that add into the headers of each HTTP request:
    - `X-Trace-ID`: Identifier of all the requests associated to a given interaction.
    - `X-Device-ID`: Identifier of the device from where the request was initiated.
    - `X-Client-ID`: Identifier of the client that initiates the interaction.
    - `X-Tenant-ID`: Identifier of the current tenant.
    - `X-Client-Info`: Client Name + Version

## Spring Configuration

| Property                                              | Default Value | Description                                                                                           |
|-------------------------------------------------------|---------------|-------------------------------------------------------------------------------------------------------|
| wutsi.platform.tracing.client-id                      |               | **REQUIRED** - Value of the client ID associated with this application                                |
| wutsi.platform.tracing.device-id-provider.type        | header        | Type of Device ID provider: `header` or `cookie`                                                       | `cookie` |
| wutsi.platform.tracing.device-id-provider.cookie.name | _w_did        | Name of the cookie that contains the device ID. When `wutsi.platform.tracing.provider-id.type=cookie` |

## Beans

| Name                      | Type                           | Description                                       |
|---------------------------|--------------------------------|---------------------------------------------------|
| tracingContext            | TracingContext                 | Provide tracing context information               |
| deviceIdProvider          | DeviceIdProvider               | Returns the value of the device-id                |
| tracingRequestInterceptor | FeignTracingRequestInterceptor | Add tracing information into all feign HTTP calls |
