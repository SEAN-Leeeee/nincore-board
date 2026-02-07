<img width="32" height="17" alt="image" src="https://github.com/user-attachments/assets/04dff5bd-0a09-4395-ae24-8d2b08b25528" />![header](https://capsule-render.vercel.app/api?type=blur&height=300&color=gradient&text=NINcoreBoard&strokeWidth=2&section=footer&reversal=true&fontAlign=50&stroke=E0E0E0&fontSize=55&textBg=false)

# 🏀 NincoreBoard

> 실시간 점수판 원격 제어 시스템  
> **WebSocket 양방향 통신**을 활용한 Remote-Display 연동 프로젝트

**배포 링크:** [www.nincoreboard.cloud](http://www.nincoreboard.cloud)  
**개발 기간:** 2025.12 - 2026.01 (2개월)

---

## 📌 프로젝트 소개

### 💡 왜 만들었나요?

동호회 농구 경기를 진행할 때, **전문 점수판 장비는 수십만 원대로 부담**스럽습니다.  
"아이패드로 점수판을 보고, 핸드폰으로 조작할 수 있다면?"이라는 아이디어에서 시작했습니다.

이 프로젝트는 **비용 부담 없이 실시간 점수판 시스템을 구현**하여,  
누구나 쉽게 경기 운영을 할 수 있도록 만든 서비스입니다.

---

## 🎯 주요 기능

### 1. 실시간 Remote-Display 양방향 연동
- **WebSocket 통신**으로 리모트(핸드폰) ↔ 서버 ↔ 디스플레이(아이패드) 구조 구현
- 리모트에서 점수 조작 시 **0.1초 이내 디스플레이 반영**
- 여러 기기가 동시에 접속해도 **동기화 유지**

### 2. 선수 개인 기록 PDF 다운로드
- 경기 종료 후 선수별 득점, 리바운드, 어시스트 등 **통계 자동 생성**
- PDF 파일로 변환하여 다운로드 가능
- 향후 NinNong 프로젝트와 연동하여 **자동 기록 관리 예정**

### 3. AWS 프리티어 무료 배포
- EC2, RDS 등 프리티어로 **월 비용 0원 운영**
- Nginx 웹서버 설정으로 안정적인 서비스 제공

---


## 🛠 기술 스택

### Backend
- **Java 17** - 백엔드 로직 구현
- **Spring Boot ** - REST API 및 WebSocket 서버 구축
- **WebSocket** - 실시간 양방향 통신

### Frontend
- **Vue.js** - Remote/Display UI 구현
- **JavaScript (ES6+)** - WebSocket 클라이언트 연동

### Infra
- **AWS EC2** - 서버 배포
- **Nginx** - 웹서버 및 리버스 프록시


---


## 🏗 아키텍처
```
[핸드폰 Remote] ←→ [WebSocket Server] ←→ [아이패드 Display]
                          ↓
                      [in-memory DB]
```

1. **Remote(핸드폰)**: 점수 조작 UI
2. **WebSocket Server**: 실시간 메시지 브로커 역할
3. **Display(아이패드)**: 점수판 화면 출력
4. **in-memory DB (H2)**: 경기 기록 단기 저장

---

## 🚀 기술적 도전과 해결

### 1️⃣ WebSocket 동시 접속 처리
**문제:** 여러 기기가 동시에 접속하면 메시지 중복 또는 누락 발생  
**해결:** 
- `SimpMessagingTemplate`을 활용한 메시지 브로드캐스팅
- 세션 ID 기반 클라이언트 구분으로 정확한 메시지 전달

### 2️⃣ 실시간 동기화 정확도
**문제:** 네트워크 지연으로 점수 업데이트가 늦게 반영됨  
**해결:**
- WebSocket 연결 상태 실시간 모니터링
- 연결 끊김 시 자동 재연결 로직 구현

### 3️⃣ AWS 프리티어 제약 극복
**문제:** 프리티어 성능(t2.micro, 1GB RAM)에서 안정성 확보  
**해결:**
- Nginx 캐싱으로 정적 파일 서빙 최적화

## 📊 성과 및 개선 사항

### 현재
- ✅ 배포 완료 및 서비스 운영 중
- ✅ WebSocket 실시간 통신 안정적 동작
- ✅ 비용 0원으로 서비스 유지

### 개선 예정
- [ ] 실사용자 확보 및 피드백 수집
- [ ] NinNong 프로젝트와 API 연동
- [ ] 경기 영상 녹화 기능 추가


  
---

<img width="1852" height="1007" alt="image" src="https://github.com/user-attachments/assets/9ad7198f-5750-4a2f-81a3-58378781c469" />
<img width="1073" height="601" alt="Screenshot 2026-01-14 at 8 49 31 PM" src="https://github.com/user-attachments/assets/195f5190-f6df-4cf4-ae19-50d714d2e9a4" />
<img width="1073" height="601" alt="Screenshot 2026-01-14 at 9 31 05 PM" src="https://github.com/user-attachments/assets/222fcf41-1721-404f-9b36-dbdf43334d51" />

