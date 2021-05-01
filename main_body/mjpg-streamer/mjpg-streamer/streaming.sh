#!/bin/bash
./mjpg_streamer -i "./input_uvc.so -y" -o "./output_http.so -w ./www" &
