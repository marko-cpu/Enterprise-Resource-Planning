#!/bin/bash


# Promenljive

LOCAL_PORT=8888
REMOTE_PORT=8080
USERNAME=kube
REMOTE_HOST=142.132.213.5
SSH_PORT=10822

ssh -N -L localhost:$LOCAL_PORT:localhost:$REMOTE_PORT $USERNAME@$REMOTE_HOST -p $SSH_PORT

