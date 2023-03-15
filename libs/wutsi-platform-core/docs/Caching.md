# Caching

This module provides the following Spring Cache implementations:

- `none`: No caching
- `local`: Cache running in the application memory space, using a concurent `HashMap`. To use for testing purpose in
  local environment.
- `memcached`: Distributed cached based on Memcached. To use in PROD environment.

## Configuration

| Property                  | Default Value | Description                                     |
|---------------------------|---------------|-------------------------------------------------|
| wutsi.platform.cache.type | none          | Type of cache: `none` or `local` or `memcached` |
| wutsi.platform.cache.name |               | **REQUIRED** Name of the cache                  |

### Memcached Configuration

These are the additional configurations when `wutsi.platform.cache.type=memcached`

| Property                                | Default Value | Description                       |
|-----------------------------------------|---------------|-----------------------------------|
| wutsi.platform.cache.name               | default       | Name of the cache                 |
| wutsi.platform.cache.memcached.username |               | **REQUIRED** - Memcached username |
| wutsi.platform.cache.memcached.password |               | **REQUIRED** - Memcached password |
| wutsi.platform.cache.memcached.servers  |               | **REQUIRED** - List of server IPs |
| wutsi.platform.cache.memcached.ttl      | 86400         | Cache Time-To-Live in seconds     |

## Beans

| Name                      | Type             | Description                                  |
|---------------------------|------------------|----------------------------------------------|
| cache                     | Cache            | Instance of the cache                        |
| memcachedHealthIndicator  | HealthIndicator  | Cache heath indicator (For `memcached` only) |

