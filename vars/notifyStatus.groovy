#!groovy

import hudson.Util;

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

String getMessage(status) {
	String ret = "${status}: ${currentBuild.fullDisplayName}"

	String buildDurationString = Util.getTimeSpanString(currentBuild.duration)

	if (env.CHANGE_ID != null) {
		ret = "${ret} (<${env.CHANGE_URL}|#${env.CHANGE_ID}>"
		if (env.CHANGE_AUTHOR_DISPLAY_NAME != null) {
			ret = "${ret} by ${env.CHANGE_AUTHOR_DISPLAY_NAME})"
		} else if (env.CHANGE_AUTHOR != null) {
			ret = "${ret} by ${env.CHANGE_AUTHOR})"
		} else if (env.CHANGE_AUTHOR_EMAIL != null) {
			ret = "${ret} by ${env.CHANGE_AUTHOR_EMAIL})"
		} else {
			ret = "${ret} by unknown)"
		}
	} else if (env.BRANCH_NAME != null) {
		ret = "${ret} (branch: ${env.BRANCH_NAME})"
	}

	if (status != 'STARTED') {
		ret = "${ret} after ${buildDurationString}"
	}

	ret = "${ret} (<${RUN_DISPLAY_URL}|Open>)"

	return ret
}

void call(String status) {
	String color = getStatusColor(status)
	String message = getMessage(status)
	slackSend (
		channel : '#jenkins-ci',
		color   : color,
		message : message
	)
}
