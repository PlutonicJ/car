#!/bin/bash

# Check required variables are set
if [ -z $DB_USER ]; then
  echo "Variable DB_USER is not set."
  exit 1
fi
if [ -z $DB_PASSWORD ]; then
  echo "Variable DB_PASSWORD is not set."
  exit 1
fi
if [ -z $DB_NAME ]; then
  echo "Variable DB_NAME is not set."
  exit 1
fi
if [ -z $DB_HOST ]; then
  echo "Variable DB_HOST is not set."
  exit 1
fi
if [ -z $DB_PORT ]; then
  echo "Variable DB_PORT is not set."
  exit 1
fi
if [ -z $POSTBOOT_COMMANDS ]; then
  echo "Variable POSTBOOT_COMMANDS is not set."
  exit 1
fi

# Create post boot command files if it doesn't exist
touch $POSTBOOT_COMMANDS

echo "Adding creation of datasource to post boot commands"
echo "create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --restype javax.sql.ConnectionPoolDataSource --property user=${DB_USER}:password=${DB_PASSWORD}:DatabaseName=${DB_NAME}:ServerName=${DB_HOST}:port=${DB_PORT} carPool" >>$POSTBOOT_COMMANDS
echo "ping-connection-pool carPool" >>$POSTBOOT_COMMANDS
echo "create-jdbc-resource --connectionpoolid=carPool jdbc/car" >>$POSTBOOT_COMMANDS
echo "list-jdbc-resources" >>$POSTBOOT_COMMANDS

export DB_PASSWORD=""
echo $DB_USER
echo $DB_PASSWORD
