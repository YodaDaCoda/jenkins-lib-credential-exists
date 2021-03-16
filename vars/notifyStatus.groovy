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

String getMessage() {
	out.println('begin getMessage()')

	String ret = "${status}: ${currentBuild.fullDisplayName}"

	out.println(ret)
	out.println("${env.CHANGE_ID}")
	out.println("${env.BRANCH_NAME}")
	out.println(env.CHANGE_ID)
	out.println(env.BRANCH_NAME)
	out.println(env)

	if ("${env.CHANGE_ID}" != null) {
		ret = "${ret} (<${env.CHANGE_URL}|${env.CHANGE_ID}> by ${env.CHANGE_AUTHOR_DISPLAY_NAME})"
		out.println('change_id not null')
		out.println(ret)
	} else if ("${env.BRANCH_NAME}" != null) {
		out.println('branch_name not null')
		out.println(ret)
		ret = "${ret} (branch: ${env.BRANCH_NAME})"
	}

	out.println(ret)

	ret = "${ret} after ${currentBuild.durationString} (<${RUN_DISPLAY_URL}|Open>)"

	out.println(ret)

	out.println('end getMessage()')

	return ret
}

void call(String status) {
	out.println('notifyStatus')
	String color = getStatusColor(status)
	out.println(color)
	String message = getMessage()
	out.println(message)
	slackSend (
		channel : '#jenkins-ci',
		color   : color,
		message : message
	)
}
