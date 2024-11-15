# 접근 주소
설정 정보 데이터를 얻기 위해 Config Client가 Config Server에 접근하는 주소는 아래와 같다.

```bash
# 저장소 이름과 저장소 환경은 config yml에서 설정하였던 파일의 저장소이름-저장소환경을 가져옴
http://ip:port/저장소이름/저장소환경

# ex 
http://localhost:9000/ms1/dev
```

IP와 포트는 Config Server의 값을 입력해주지만 저장소이름과 저장소환경 경로는 Config Repository에 해당하는 깃허브 리포지토리 내부 파일 명을 넣어야 한다.

이때 파일명에 대한 주소 변환은 아래와 같다.

```PLAINTEXT
이름-환경.properties
이름-환경.yml

/이름/환경
```

# 결론
| **위의 이름-환경.yml의 파일이 바뀌어도 항상 저 url의 요청을 통해서 바뀐 값을 실시간으로 가져올 수 있다.**

