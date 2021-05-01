import RPi.GPIO as GPIO
import time
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)


def open_the_door():
	GPIO.setup(18,GPIO.OUT)
	GPIO.output(18,True)
	time.sleep(2.0)
	GPIO.output(18,False)
	GPIO.cleanup()


