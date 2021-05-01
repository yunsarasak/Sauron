import paho.mqtt.client as mqtt
import RPi.GPIO as GPIO
import time
import subprocess


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("connected OK")
    else:
        print("Bad connection Returned code=", rc)


def on_disconnect(client, userdata, flags, rc=0):
    print(str(rc))


def on_subscribe(client, userdata, mid, granted_qos):
    print("subscribed: " + str(mid) + " " + str(granted_qos))


def on_message(client, userdata, msg):
    start = time.time()
    command = msg.payload.decode("utf-8")
    if command == "open":
        subprocess.call(["python", "gpio_18_enable.py"])
        end = time.time()
        time.sleep(1.0)
        subprocess.call(["python", "gpio_18_disable.py"])
        print(end-start)
    elif command == "cctv":
        subprocess.call(["bash", "cctv.sh"])
    elif command == "quit":
        subprocess.call(["killall", "mjpg_streamer"])


# ??? ????? ??
client = mqtt.Client()
# ?? ?? ?? on_connect(???? ??), on_disconnect(???? ????), on_subscribe(topic ??),
# on_message(??? ???? ???? ?)
client.on_connect = on_connect
client.on_disconnect = on_disconnect
client.on_subscribe = on_subscribe
client.on_message = on_message
# address : localhost, port: 1883 ? ??
client.connect('localhost', 1883)
# common topic ?? ??? ??
client.subscribe('sauron/yun', 1)
client.loop_forever()
