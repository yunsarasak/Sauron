import os
import boto3

def upload_temp():
	s3 = boto3.resource('s3')
	os.chdir("/home/pi/Sauron")
	data = open('shot.jpg', 'rb')
	s3.Bucket('sauronfaces').put_object(Key="temp.jpg", Body=data)

def is_valid_face():
    client = boto3.client('rekognition','ap-northeast-2')
    s3 = boto3.resource('s3')
    
    my_bucket = s3.Bucket('sauronfaces')

    for my_bucket_object in my_bucket.objects.all():
        #print(my_bucket_object.key)
        string = my_bucket_object.key
        if my_bucket_object.key == "temp.jpg":
            continue
        
        response = client.compare_faces(
        SourceImage={
            'S3Object': {
                'Bucket': 'sauronfaces',
                'Name': 'temp.jpg',
                'Version': 'null'
            }
        },
        TargetImage={
            'S3Object': {
                'Bucket': 'sauronfaces',
                'Name': string,
                'Version': 'null'
            }
        },
        SimilarityThreshold=0.7,
        QualityFilter='AUTO'
        )
        
        if(response['FaceMatches'][0]['Similarity'] >= 87):
            return True
    return False
