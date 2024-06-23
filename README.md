# Sauron
Face recognition door lock using AWS rekognition, RaspberryPi, RabbitMQ, paho

## 💻프로젝트 소개
"AWS Rekognition"서비스를 활용한 안면인식 도어락입니다.


## ⏱️개발 기간
2019.06 ~ 2019.11 (창의설계프로젝트)

## 📄구현기능
- 도어락 원격 제어
- 실시간 스트리밍
- 사용자 추가
- 얼굴 인식 및 도어락 해제

## 🔎시스템 구성도
![시스템_구성도](https://github.com/yunsarasak/Sauron/blob/main/System_Architecture.png)

## 🔌회로 구성도
![회로 구성도](https://github.com/yunsarasak/Sauron/blob/main/Circuit_configuration_diagram.png)


## 🔬how to run
1. how_to_set_mqtt.pptx 파일 참고하여 rabbitMQ 데몬 기동
2. Sauron_start.sh 스크립트실행
   - mqtt message 구독, 메시지 수신시 처리
   - 거리 센서 확인, 카메라로 이미지 캡쳐, 업로드 및 유사도 비교하여 도어락 해제
