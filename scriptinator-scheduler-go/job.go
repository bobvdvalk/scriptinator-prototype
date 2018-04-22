package main

import (
	"fmt"
	"github.com/streadway/amqp"
	"strconv"
	"time"
)

type Job struct {
	Id                    int64
	DisplayName           string
	ScriptId              int64
	Argument              string
	TriggeredByScheduleId int64
}

type JobService struct {
	dbConfig    *DbConfig
	queueConfig *QueueConfig
}

// Create and publish a new job.
// This updates the new job id and display name.
func (jobService *JobService) CreateJob(job *Job) {
	// Insert the job into the database.
	insertStmt, err := jobService.dbConfig.Db.Prepare("INSERT INTO job(display_name, script_id, argument, triggered_by_schedule_id, queued_time, output, status) VALUES(?, ?, ?, ?, ?, '', 0)")
	failOnError(err, "Could not prepare insert job statement")
	defer insertStmt.Close()

	insertResult, err := insertStmt.Exec(job.DisplayName, job.ScriptId, job.Argument, job.TriggeredByScheduleId, time.Now())
	failOnError(err, "Could not insert job")

	// Set the job id and display name.
	lastId, err := insertResult.LastInsertId()
	failOnError(err, "Could not get last id of inserted job")
	job.Id = lastId
	job.DisplayName = fmt.Sprintf("%s #%d", job.DisplayName, job.Id)

	// Update the display name in the database.
	updateStmt, err := jobService.dbConfig.Db.Prepare("UPDATE job SET display_name = ? WHERE id = ?")
	failOnError(err, "Could not prepare update job statement")
	defer updateStmt.Close()

	_, err = updateStmt.Exec(job.DisplayName, job.Id)
	failOnError(err, "Could not update job")

	jobService.publishJob(job)
}

// Publish the job to the message queue.
func (jobService *JobService) publishJob(job *Job) {
	// Publish the job.
	err := jobService.queueConfig.Channel.Publish(
		"scriptinator-exchange",
		jobService.queueConfig.QueueName,
		false,
		false,
		amqp.Publishing{
			DeliveryMode:    2,
			ContentType:     "application/json",
			ContentEncoding: "UTF-8",
			Body:            []byte(strconv.FormatInt(job.Id, 10)),
			// Set the TypeId header so the receiver can deserialize it to the right type.
			Headers: amqp.Table{"__TypeId__": "java.lang.Long"},
		})
	failOnError(err, "Could not publish job")
}
