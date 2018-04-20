package main

import (
	"fmt"
	"github.com/streadway/amqp"
	"log"
	"strconv"
)

// Start the scheduler.
func startScheduler(dbConfig DbConfig, queueConfig QueueConfig) {
	tick(dbConfig, queueConfig)
}

// Run the scheduler.
func tick(dbConfig DbConfig, queueConfig QueueConfig) {
	// Get and run all runnable schedules.
	schedules := findRunnableSchedules(dbConfig)
	log.Printf("Running %d schedule(s).\n", len(schedules))
	for _, schedule := range schedules {
		runSchedule(schedule, dbConfig, queueConfig)
	}
}

// Find all enabled schedules whose next run is before now.
func findRunnableSchedules(dbConfig DbConfig) []Schedule {
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

// Run a schedule.
// This creates a job in the database and publish the created job in the message queue.
func runSchedule(schedule Schedule, dbConfig DbConfig, queueConfig QueueConfig) {
	// Get the id of the script to run.
	scriptIdStmt, err := dbConfig.Db.Prepare("SELECT id FROM script WHERE project_id = ? AND name = ?")
	failOnError(err, "Could not prepare select script id statement")
	defer scriptIdStmt.Close()

	var scriptId int64
	err = scriptIdStmt.QueryRow(schedule.ProjectId, schedule.ScriptName).Scan(&scriptId)
	if err != nil {
		log.Fatalf("Could not find script '%s' for schedule %d.\n", schedule.ScriptName, schedule.Id)
		return
	}

	// Create and publish the job.
	job := Job{
		DisplayName:           schedule.ScriptName,
		ScriptId:              scriptId,
		Argument:              schedule.Argument,
		TriggeredByScheduleId: schedule.Id,
	}

	createJob(&job, dbConfig)
	publishJob(job, queueConfig)
	log.Printf("Created job: %s.\n", job.DisplayName)
}

// Create a job in the database.
// This also sets the new job id and display name.
func createJob(job *Job, dbConfig DbConfig) {
	// Insert the job into the database.
	insertStmt, err := dbConfig.Db.Prepare("INSERT INTO job(display_name, script_id, argument, triggered_by_schedule_id, queued_time, output, status) VALUES(?, ?, ?, ?, now(), '', 0)")
	failOnError(err, "Could not prepare insert job statement")
	defer insertStmt.Close()

	insertResult, err := insertStmt.Exec(job.DisplayName, job.ScriptId, job.Argument, job.TriggeredByScheduleId)
	failOnError(err, "Could not insert job")

	// Set the job id and display name.
	lastId, err := insertResult.LastInsertId()
	failOnError(err, "Could not get last id of inserted job")
	job.Id = lastId
	job.DisplayName = fmt.Sprintf("%s #%d", job.DisplayName, job.Id)

	// Update the display name in the database.
	updateStmt, err := dbConfig.Db.Prepare("UPDATE job SET display_name = ? WHERE id = ?")
	failOnError(err, "Could not prepare update job statement")
	defer updateStmt.Close()

	_, err = updateStmt.Exec(job.DisplayName, job.Id)
	failOnError(err, "Could not update job")
}

// Publish the job to the message queue.
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
