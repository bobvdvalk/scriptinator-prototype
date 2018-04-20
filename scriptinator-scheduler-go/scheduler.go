package main

import (
	"github.com/streadway/amqp"
	"strconv"
)

func startScheduler(channel *amqp.Channel, queueName string) {
	// TODO

	testJob := Job{Id: 50}
	publishJob(&testJob, channel, queueName)
}

func publishJob(job *Job, channel *amqp.Channel, queueName string) {
	// Set the TypeId header so the receiver knows what type the body is.
	headers := amqp.Table{}
	headers["__TypeId__"] = "java.lang.Long"

	// Publish the job.
	err := channel.Publish(
		"scriptinator-exchange",
		queueName,
		false,
		false,
		amqp.Publishing{
			DeliveryMode:    2,
			ContentType:     "application/json",
			ContentEncoding: "UTF-8",
			Body:            []byte(strconv.FormatInt(job.Id, 10)),
			Headers:         headers,
		})
	logError(err, "Could not publish job")
}
