# Contributing to Scriptinator

If you have any feedback, questions or feature requests please let us know by email (support@scriptinator.io) or through [slack](https://slack-invite.scriptinator.io).

## Getting Started

This setup assumes you have [maven](https://maven.apache.org) installed on your system.
Additionally [docker](https://www.docker.com/community-edition) can make your life a lot easier.

1. In the project root, run the `mvn` command to invoke maven with the default goal (`compile`).
This is needed to download the frontend (npm) dependencies.
2. Start a [MySQL](https://dev.mysql.com/downloads/mysql) and [RabbitMQ](https://www.rabbitmq.com) server.
If you're using docker, you can simply run `docker-compose up -d`.

That's it! You can now start the different services.
