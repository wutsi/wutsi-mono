# Error
This module provides functionalities for handling rest exceptions

## Main Classes
- [ErrorResponse](https://github.com/wutsi/wutsi-core/blob/master/src/main/kotlin/com/wutsi/core/error/BadRequestException.kt): common response returned for all errors
- [RestControllerErrorHandler](https://github.com/wutsi/wutsi-core/blob/master/src/main/kotlin/com/wutsi/core/spring/RestControllerErrorHandler.kt): Error handler for all rest endpoints
- HTTP Rest exceptions
  - [BadRequestException](https://github.com/wutsi/wutsi-core/blob/master/src/main/kotlin/com/wutsi/core/error/exception/BadRequestException.kt) for HTTP 400 errors
  - [UnauthorizedException](https://github.com/wutsi/wutsi-core/blob/master/src/main/kotlin/com/wutsi/core/error/exception/UnauthorizedException.kt) for HTTP 401 errors
  - [ForbiddenException](https://github.com/wutsi/wutsi-core/blob/master/src/main/kotlin/com/wutsi/core/error/exception/ForbiddenException.kt) for HTTP 403 errors
  - [NotFoundException](https://github.com/wutsi/wutsi-core/blob/master/src/main/kotlin/com/wutsi/core/error/exception/NotFoundException.kt) for HTTP 404 errors
  - [ConflictException](https://github.com/wutsi/wutsi-core/blob/master/src/main/kotlin/com/wutsi/core/error/exception/ConflictException.kt) for HTTP 409 errors
  - [InternalErrorException](https://github.com/wutsi/wutsi-core/blob/master/src/main/kotlin/com/wutsi/core/error/exception/InternalErrorException.kt) for HTTP 500 errors
