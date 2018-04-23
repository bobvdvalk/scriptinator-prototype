/*
 * Copyright © 2018 Scriptinator (support@scriptinator.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package main

import (
	"database/sql"
	_ "github.com/go-sql-driver/mysql"
	"github.com/streadway/amqp"
	"log"
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
	db, err := sql.Open("mysql", "root:root@tcp(localhost:3306)/scriptinator?parseTime=true")
	failOnError(err, "Could not connect to database")
	defer db.Close()

	// Validate the connection.
	err = db.Ping()
	failOnError(err, "Could not ping database")

	dbConfig := &DbConfig{db}

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

	queueConfig := &QueueConfig{Channel: channel, QueueName: queue.Name}

	NewScheduler(dbConfig, queueConfig).Run()
}

// Check if an error occurred.
func failOnError(err error, msg string) {
	if err != nil {
		log.Fatalf("%s: %s\n", msg, err)
	}
}
