#!/bin/bash

export DELIM

if [[ "$OSTYPE" == 'msys' ]]; then
  DELIM=';'
else
  DELIM=':'
fi
