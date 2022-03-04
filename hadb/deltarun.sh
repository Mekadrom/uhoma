#!/bin/bash

error_string="Usage: $0 -u username -p password -U URL -P port -d domain"

username=

password=

url=

port=

domain=

set -e

while getopts u:p:U:P:d: o
do
	case "${o}" in
		u)   username=${OPTARG};;
		p)   password=${OPTARG};;
		U)   url=${OPTARG};;
	  P)   port=${OPTARG};;
		d)   domain=${OPTARG};;
		[?]) print >&2 error_string
			 exit 1;;
	esac
done

connection_url="postgres://$username:$password@$url:$port/$domain"

if [ -z "$username" ] || [ -z "$password" ]; then
	echo "$error_string"
else
  runorder=$(eval cat runorder)
	for delta in $runorder; do
		# shellcheck disable=SC2034
		delta_path=$(realpath delta/"$delta")
		echo running delta "$delta"
		psql -v ON_ERROR_STOP=1 -v AUTOCOMMIT=ON "$connection_url" -f "$delta_path"
	done
fi
