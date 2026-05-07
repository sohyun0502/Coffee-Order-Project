import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 20,          // 20명의 가상 유저가 동시에 접속
    duration: '10s',  // 10초 동안 반복 실행
};

export default function () {
    // 내부 네트워크 망을 이용하므로 container_name인 app-1, app-2 사용
    const targets = ['http://app-1:8080', 'http://app-2:8080'];
    const url = targets[Math.floor(Math.random() * targets.length)] + '/api/orders';

    const payload = JSON.stringify({
        userId: 1,
        menuId: 1,
        quantity: 1
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
    };

    let res = http.post(url, payload, params);

    // 분산 락이 정상이라면, 성공(200) 외에도 락 획득 실패(400 등)가 섞여 나와야 함
    check(res, {
        'is status 200 or 400': (r) => r.status === 200 || r.status === 400,
    });

    sleep(0.1);
}