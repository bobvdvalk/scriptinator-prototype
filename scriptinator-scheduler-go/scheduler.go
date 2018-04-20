package main

import (
	"github.com/streadway/amqp"
	"strconv"
	"log"
	"fmt"
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
	rows, err := dbConfig.Db.Query("SELECT id, project_id, argument, script_name FROM schedule WHERE enabled = true AND next_run < now()")
	failOnError(err, "Could not find schedules to run")
	defer rows.Close()

	// Convert all rows to schedules.
	var schedules []Schedule
	for rows.Next() {
		nextSchedule := Schedule{}
		err := rows.Scan(&nextSchedule.Id, &nextSchedule.ProjectId, &nextSchedule.Argument, &nextSchedule.ScriptName)
		failOnError(err, "Could not get row from result set")
		schedules = append(schedules, nextSchedule)
	}

	failOnError(rows.Err(), "An error occurred with the result set")

	return schedules
}

func runSchedule(schedule Schedule, dbConfig DbConfig, queueConfig QueueConfig) {
	// Get the id of the script to run.
	scriptIdStmt, err := dbConfig.Db.Prepare("SELECT id FROM script WHERE project_id = ? AND name = ?")
	failOnError(err, "Could not prepare statement")

	var scriptId int64
	err = scriptIdStmt.QueryRow(schedule.ProjectId, schedule.ScriptName).Scan(&scriptId)
	if err != nil {
		log.Fatalf("Could not find script '%s' for schedule %d.\n", schedule.ScriptName, schedule.Id)
		return
	}

	// Create the job.
	jobName := fmt.Sprintf("%s #jobId", schedule.ScriptName)
	job := Job{
		DisplayName:           jobName,
		ScriptId:              scriptId,
		Argument:              schedule.Argument,
		TriggeredByScheduleId: schedule.Id,
	}
	log.Printf("Creating job: %s.\n", job.DisplayName)

	// publishJob(job, queueConfig)
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
	failOnError(err, "Could not publish job")
}
