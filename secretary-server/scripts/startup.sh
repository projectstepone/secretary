#!/bin/bash

SERVICE_CONFIG_FILE=/config/docker.yml

if [ -z "${CONFIG_PATH}" ]; then
    echo "No CONFIG_PATH defined. We shall be using default config from ${SERVICE_CONFIG_FILE}"
    if [ -z "${SHARDS}" ]; then
        :
    else
        echo "You cannot override shards unless you change the config file. Volume mount a new config and set the path in CONFIG_PATH env variable"
        exit 1
    fi
else
    if [ -f ${SERVICE_CONFIG_FILE} ]; then
        SERVICE_CONFIG_FILE=${CONFIG_PATH}
    else
        echo "Defined CONFIG_PATH (${CONFIG_PATH}) doesn ot look like a proper file. Using default: ${SERVICE_CONFIG_FILE}"
    fi
fi

EXEC_CMD="java -Ddb.shards=${SHARDS-2} -Dfile.encoding=utf-8 -XX:+${GC_ALGO-UseG1GC} -Xms${JAVA_PROCESS_MIN_HEAP-1g} -Xmx${JAVA_PROCESS_MAX_HEAP-1g} ${DEBUG_OPTS_JAVA} ${JAVA_OPTS} -jar server.jar server ${SERVICE_CONFIG_FILE}"

echo "Number of database shards: ${SHARDS}"

if [ -z "${SLEEP_TIME_IN_SEC}" ]; then
    echo "Skip delayed startup"
else
    echo "Delayed launch for service. Sleeping for some time"
    sleep ${SLEEP_TIME_IN_SEC}
fi

echo "Starting server with command line: ${EXEC_CMD}"
$EXEC_CMD
