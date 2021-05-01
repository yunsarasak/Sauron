import os
import boto3

s3 = boto3.resource('s3')


os.chdir("/home/pi/Sauron")
os.system("raspistill -o shot.jpg --nopreview --exposure sports --timeout 1")
data = open('shot.jpg', 'rb')
s3.Bucket('sauronfaces').put_object(Key="temp.jpg", Body=data)

