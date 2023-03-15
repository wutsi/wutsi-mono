# Stream

This module provides support for event streaming. Event streaming us used for publishing event to Messaging
infrastructure. The Messaging infrastructure supported are:

- `none`: No event streaming
- `local`: Event streaming using local filesystem. To use for testing purpose in local environment.
- `rabbitmq`: Event streaming using [RabbitMQ](https://www.rabbitmq.com/). To use in PROD environment

## Main Classes

- [EventStream](https://github.com/wutsi/wutsi-core/blob/master/src/main/kotlin/com/wutsi/stream/EventStream.kt): for
  publishing events

## Spring Configuration

| Property                            | Default Value  | Description                                                  |
|-------------------------------------|----------------|--------------------------------------------------------------|
| wutsi.platform.stream.type          | none           | Type of implementation: `none` or `local` or `rabbitmq`      |
| wutsi.platform.stream.subscriptions |                | String array of the streams that pushes event to this stream |

### Local Configuration

These are the additional configurations when `wutsi.platform.stream.type=local`

| Property                              | Default Value             | Description |
|---------------------------------------|---------------------------|-------------|
| wutsi.platform.stream.name            |                           | REQUIRED - Name of the stream |
| wutsi.platform.stream.local.directory | ${user.home}/wutsi/stream | Directory where events are stored |

### RabbitMQ Configuration

These are the additional configurations when `wutsi.platform.stream.type=rabbitmq`

| Property                                         | Default Value  | Description                                                                                |
|--------------------------------------------------|----------------|--------------------------------------------------------------------------------------------|
| wutsi.platform.stream.name                       |                | **REQUIRED** - Name of the stream                                                          |
| wutsi.platform.stream.rabbitmq.url               |                | **REQUIRED** - URL RabbitMQ server                                                         |
| wutsi.platform.stream.rabbitmq.thread-pool-size  | 8              | RabbitMQ consumer threadpool size                                                          |
| wutsi.platform.stream.rabbitmq.queue-ttl-seconds | 86400          | Time to Leave of the messages in the queue                                                 |
| wutsi.platform.stream.rabbitmq.dlq.max-retries   | 10             | Number of retries for consuming the Dead-Letter-Queue, after what the message is discarded |
| wutsi.platform.stream.rabbitmq.dlq.replay-cron   | 0 */15 * * * * | Cron schedule of the job that consume the DLQ                                              |

## Beans

| Name                    | Type              | Description                                 |
|-------------------------|-------------------|---------------------------------------------|
| eventStream             | EventStream       | Used for publishing events                  |
| rabbitMQHealthIndicator | HealthIndicator   | Cache heath indicator (For `rabbitmq` only) |
