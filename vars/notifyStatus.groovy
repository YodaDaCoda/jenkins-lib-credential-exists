#!groovy

String getStatusColor(String status) {
	if (status == null) {
		return 'good'
	}
	if (status == 'STARTED') {
		return getStatusColor(currentBuild.previousBuild?.result)
	}
	if (status == 'SUCCESS') {
		return 'good'
	}
	if (status == 'UNSTABLE') {
		return 'warning'
	}
	if (status == 'FAILURE') {
		return 'danger'
	}
	// this should never happen - return a blue color because that's different to any other status color
	echo "Unknown build status ${status}"
	return '#0000FF'
}

void call(String status) {
	String color = getStatusColor(status)
	slackSend (
		channel : '#jenkins-ci',
		color   : color,
		message : "${status}: '${currentBuild.fullDisplayName} (<${CHANGE_URL}|${CHANGE_ID}> by ${CHANGE_AUTHOR_DISPLAY_NAME})' after ${currentBuild.durationString} (<${RUN_DISPLAY_URL}|Open>)"
	)
}
