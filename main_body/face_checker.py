import RPi.GPIO as GPIO
import time
import subprocess as sp
import rekognition_module as rm
import doorLockModule as dlm

GPIO.setwarnings(False)

myCounter =0

try:
	while True:
		GPIO.setmode(GPIO.BCM)
		trig = 23
		echo = 21
		GPIO.setup(trig, GPIO.OUT)
		GPIO.setup(echo, GPIO.IN)
		GPIO.output(trig, False)#trig pin set low
		time.sleep(1.0)
		
		##send sonar by setting trig pin out to high. hold 10ms
		GPIO.output(trig, True)
		time.sleep(0.00001)
		GPIO.output(trig, False)
		
		while GPIO.input(echo) == False:#print time when echo pinout is low
			pulse_start = time.time()
		
		while GPIO.input(echo) == True:#print when high
			pulse_end = time.time()
		
		pulse_duration = pulse_end - pulse_start
		distance = pulse_duration * 17000
		distance = round(distance, 2)
		
		#print("Distance : ", distance, "cm")
		if distance <= 10:
			#print("Distance : ", distance, "cm")
			if myCounter == 2:
				myCounter=0
				#start = time.time()
				sp.call(["python", "upload_temp.py"])
				#print("upload : ", time.time()-start)
				if rm.is_valid_face() == True:
					#print("check : ", time.time()-start)
					dlm.open_the_door()
					#print("open : ", time.time()-start)
					time.sleep(2.0)
			else:
				myCounter=myCounter+1
		
except Exception as e:
	print(e)
	GPIO.cleanup()
        sp.call(["python", "face_checker.py"])
