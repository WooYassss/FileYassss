# FileYassss
그저 개발중인 파일 저장소. ~~자기 전에 30분씩 개발..~~

### 실행 시 필요한 것
- 개쩌는 컴퓨터
- MongoDB

### MongoDB with Docker
```
docker run -dp 27017:27017 --name mongodb mongo
```

### TODO
`다음날 할 것을 적어보자..`

- ~~파일 아이디 생성 방법 찾기 (함수 생성)~~
  - ~~timestamp + uploader + randInt (설마 이게 겹치겠어..)~~
  - timestamp + randInt 로 구현
  
- 싱글 파일 업로드 함수
  - 컨트롤러 단에서 해당 함수를 재사용하는 방식 구현


`언젠가 할 것`
- sse (길게 보자)
- 파일 암호화s