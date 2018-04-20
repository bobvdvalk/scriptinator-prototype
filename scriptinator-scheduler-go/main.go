package main

import (
	"log"
	"github.com/streadway/amqp"
)

func main() {
	// Connect to the queue.
	conn, err := amqp.Dial("amqp://guest:guest@localhost:5672")
	logError(err, "Could not connect to RabbitMQ")
	defer conn.Close()

	// Get the jobs channel.
	channel, err := conn.Channel()
	logError(err, "Could not open channel")
	defer channel.Close()

	queue, err := channel.QueueDeclare(
		"execute_job",
		true,
		false,
		false,
		false,
		nil,
	)
	logError(err, "Could not declare queue")

	startScheduler(channel, queue.Name)
}

func logError(err error, msg string) {
	if err != nil {
		log.Fatalf("%s: %s\n", msg, err)
	}
}