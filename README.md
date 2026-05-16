# Velox Email Spring Boot Starter

## Semantic Boundaries

This module keeps the existing implementation behavior unchanged and only restructures the starter by semantic responsibility.

- Maven module name: `velox-email-spring-boot-starter`
- Runtime package root: `com.velox.email`
- Spring Boot auto-configuration entry: `com.velox.email.autoconfigure.VeloxEmailAutoConfiguration`
- Configuration prefix: `velox.email`

## Package Layout

- `com.velox.email.api.builder`: public builder contracts and compatibility-facing `IEmailBuilder`
- `com.velox.email.api.message`: request, response, attachment, and failure context models
- `com.velox.email.api.sender`: sender-facing contracts and compatibility-facing `IEmailSender`
- `com.velox.email.api.channel`: compatibility-facing `IEmailChannel`
- `com.velox.email.common.channel`: channel identifiers shared across starter layers
- `com.velox.email.common.error`: shared error code enums used to remove implementation hardcoding
- `com.velox.email.common.message`: shared validation and invariant messages
- `com.velox.email.common.provider`: shared SMTP provider host, domain, and port constants
- `com.velox.email.spi.builder`: reusable abstract builder base for custom implementations
- `com.velox.email.spi.sender`: reusable abstract sender base for custom implementations
- `com.velox.email.spi.channel`: channel SPI and reusable abstract channel base
- `com.velox.email.spi.policy`: retry and exception translation SPI
- `com.velox.email.spi.hook`: interception and listener extension SPI
- `com.velox.email.properties`: starter configuration models and validation
- `com.velox.email.autoconfigure`: Spring Boot starter auto-configuration
- `com.velox.email.core.builder`: default builder factory implementation
- `com.velox.email.core.sender`: default sender and exception translation implementation
- `com.velox.email.core.policy`: default retry policy implementation
- `com.velox.email.support.channel`: SMTP channel support implementation
- `com.velox.email.support.meta`: SMTP metadata support types
- `com.velox.email.support.type`: protocol and logging related support enums
- `com.velox.email.support.util`: internal support utilities
- `com.velox.email.noop`: reserved semantic slot for no-op implementations
- `com.velox.email.exception`: module exceptions

## Refactor Note

This refactor preserves the existing implementation strategy:

- `IEmail*` remains available for compatibility and method reuse
- `AbstractEmail*` remains available for developers who want to reuse behavior while fully rewriting concrete implementations
- no behavior, configuration key, conditional wiring rule, or send flow was changed
