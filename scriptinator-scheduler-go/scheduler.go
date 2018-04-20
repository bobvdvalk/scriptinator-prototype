package main

import (
	"github.com/streadway/amqp"
	"strconv"
	"log"
)

func startScheduler(dbConfig DbConfig, queueConfig QueueConfig) {
	// Get and run all runnable schedules.
	schedules := findSchedules(dbConfig)
	log.Printf("Running %d schedule(s).\n", len(schedules))
	for _, schedule := range schedules {
		runSchedule(schedule, dbConfig, queueConfig)
	}
}

func findSchedules(dbConfig DbConfig) []Schedule {
	// Find all enabled schedules with a next run before now.
	rows, err := dbConfig.Db.Query("SELECT id, argument, script_name FROM schedule WHERE enabled = true AND next_run < now()")
	logError(err, "Could not query database")
	defer rows.Close()

	var schedules []Schedule

	// Convert all rows to schedules.
	for rows.Next() {
		nextSchedule := Schedule{}
		err := rows.Scan(&nextSchedule.Id, &nextSchedule.Argument, &nextSchedule.ScriptName)
		logError(err, "Could not get row from result set")
		schedules = append(schedules, nextSchedule)
	}

	logError(rows.Err(), "An error occurred with the result set")

	return schedules
}

func runSchedule(schedule Schedule, dbConfig DbConfig, queueConfig QueueConfig) {
	// TODO

	//testJob := Job{Id: 50}
	//publishJob(testJob, queueConfig)
}

func publishJob(job Job, queueConfig QueueConfig) {
	// Set the TypeId header so the receiver knows what type the body is.
	headers := amqp.Table{}
	headers["__TypeId__"] = "java.lang.Long"

	// Publish the job.
	err := queueConfig.Channel.Publish(
		"scriptinator-exchange",
		queueConfig.QueueName,
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
