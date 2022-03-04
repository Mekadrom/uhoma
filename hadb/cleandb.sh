#!/bin/bash

error_string="Usage: $0 -u username -p password -U URL -P port -d domain -s schema"

username=

password=

url=

port=

domain=

schema=

while getopts u:p:U:P:d:s: o
do
	case "${o}" in
		u)   username=${OPTARG};;
		p)   password=${OPTARG};;
		U)   url=${OPTARG};;
	  P)   port=${OPTARG};;
		d)   domain=${OPTARG};;
    s)   schema=${OPTARG};;
		[?]) print >&2 error_string
			 exit 1;;
	esac
done

connection_url="postgres://$username:$password@$url:$port/$domain"

if [ -z "$username" ] || [ -z "$password" ]; then
	echo "$error_string"
else
  psql "$connection_url" -c "DROP SCHEMA IF EXISTS $schema CASCADE ;"
  exec ./initdb.sh -u "$username" -p "$password" -U "$url" -P "$port" -d "$domain" -s "$schema"
fi