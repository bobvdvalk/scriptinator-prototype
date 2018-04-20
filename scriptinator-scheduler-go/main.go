package main

import (
	"database/sql"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"github.com/streadway/amqp"
)

type DbConfig struct {
	Db *sql.DB
}

type QueueConfig struct {
	Channel   *amqp.Channel
	QueueName string
}

func main() {
	// Connect to the database.
	db, err := sql.Open("mysql", "root:root@tcp(localhost:3306)/scriptinator")
	failOnError(err, "Could not connect to database")
	defer db.Close()

	// Validate the connection.
	err = db.Ping()
	failOnError(err, "Could not ping database")

	dbConfig := DbConfig{db}

	// Connect to the queue.
	conn, err := amqp.Dial("amqp://guest:guest@localhost:5672")
	failOnError(err, "Could not connect to message queue")
	defer conn.Close()

	// Get the jobs channel.
	channel, err := conn.Channel()
	failOnError(err, "Could not open channel")
	defer channel.Close()

	// Declare the queue.
	queue, err := channel.QueueDeclare(
		"execute_job",
		true,
		false,
		false,
		false,
		nil,
	)
	failOnError(err, "Could not declare queue")

	queueConfig := QueueConfig{Channel: channel, QueueName: queue.Name}

	startScheduler(dbConfig, queueConfig)
}

// Check if an error occurred. If so: panic.
func failOnError(err error, msg string) {
	if err != nil {
		panic(fmt.Sprintf("%s: %s\n", msg, err))
	}
}
