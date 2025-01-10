import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { SharedArray } from 'k6/data';

// 配置负载阶段，逐步增加虚拟用户数
export let options = {
    stages: [
        { duration: '10s', target: 5 },  // 在10秒内达到5个虚拟用户
        { duration: '20s', target: 10 }, // 在20秒内将负载增加到10个虚拟用户
        { duration: '30s', target: 15 }, // 在30秒内将负载增加到15个虚拟用户
        { duration: '1m', target: 20 },  // 在1分钟内将负载增加到20个虚拟用户
        { duration: '10s', target: 0 },  // 在10秒内将负载降低到0个虚拟用户
    ],
    thresholds: {
        'http_req_duration': ['p(95)<300'], // 95% of requests should complete below 500ms
    },
};

const BASE_URL = 'http://localhost:8080';
const NUM_TRANSACTIONS = 100;  // 模拟交易数量
const transactionIds = new SharedArray('transactionIds', () => []);


export default function () {
    group('Create Transactions', () => {
        for (let i = 0; i < NUM_TRANSACTIONS; i++) {
            let createPayload = JSON.stringify({
                fromAccountId: `FromAccount${i}`,
                toAccountId: `ToAccount${i}`,
                amount: (Math.random() * 1000).toFixed(2), // 转换为字符串
                currency: 'USD',
                description: `Transaction ${i} description`,
                type: 'DEBIT',
                status: 'COMPLETED',
                scene: 'ONLINE_BANKING',
                createdTime: Date.now(),
                initiatedTime: Date.now(),
                processedTime: Date.now(),
                completedTime: Date.now(),
                failedTime: 0,
                extParams: '{}' // 假设为空 JSON
            });

            let headers = {
                'Content-Type': 'application/json'
            };

            let createResponse = http.post(`${BASE_URL}/api/v1/transactions/create`, createPayload, { headers: headers });

            let checkSuccess = check(createResponse, {
                'transaction created': (res) => res.status === 201
            });

            if (checkSuccess) {
                let responseBody = createResponse.json();
                if (responseBody.id) {
                    transactionIds.push(responseBody.id);

                    // 更新交易
                    let updatePayload = JSON.stringify({
                        id: responseBody.id,
                        fromAccountId: `FromAccount${i}`,
                        toAccountId: `ToAccount${i}`,
                        amount: (Math.random() * 1000).toFixed(2),
                        currency: 'USD',
                        description: `Updated Transaction ${i} description`,
                        type: 'DEBIT',
                        status: 'COMPLETED',
                        scene: 'ONLINE_BANKING',
                        createdTime: Date.now(),
                        initiatedTime: Date.now(),
                        processedTime: Date.now(),
                        completedTime: Date.now(),
                        failedTime: 0,
                        extParams: '{}'
                    });

                    let updateResponse = http.post(`${BASE_URL}/api/v1/transactions/update`, updatePayload, { headers: headers });

                    check(updateResponse, {
                        'transaction updated': (res) => res.status === 200
                    });

                    // 通过ID获取交易
                    let getByIdResponse = http.get(`${BASE_URL}/api/v1/transactions/getById?id=${responseBody.id}`, { headers: headers });

                    check(getByIdResponse, {
                        'transaction retrieved': (res) => res.status === 200
                    });

                    // 删除交易
                    let deleteResponse = http.del(`${BASE_URL}/api/v1/transactions/delete?id=${responseBody.id}`, null, { headers: headers });

                    check(deleteResponse, {
                        'transaction deleted': (res) => res.status === 200
                    });
                }
            } else {
                console.log(`Transaction creation failed for Transaction ${i}. Response: ${createResponse.status}, Body: ${createResponse.body}`);
            }

            sleep(0.1); // 模拟一些等待时间
        }
    });

    // 获取所有交易
    group('Get All Transactions', () => {
        let getAllResponse = http.get(`${BASE_URL}/api/v1/transactions/getAll`, { headers: { 'Content-Type': 'application/json' } });
        check(getAllResponse, {
            'transactions fetched': (res) => res.status === 200
        });
    });

    // 查询交易
    group('Query Transactions', () => {
        let queryPayload = JSON.stringify({
            page: 1,
            size: 10,
            sortField: 'createdTime',
            sortOrder: 'desc',
            fromAccountId: `FromAccount${Math.floor(Math.random() * NUM_TRANSACTIONS / 2)}`,
            toAccountId: `ToAccount${Math.floor(Math.random() * NUM_TRANSACTIONS / 2)}`,
            currency: 'USD',
            type: 'DEBIT',
            status: 'COMPLETED',
            scene: 'ONLINE_BANKING',
            createdTimeStart: Date.now() - 1000000,
            createdTimeEnd: Date.now(),
            initiatedTimeStart: Date.now() - 1000000,
            initiatedTimeEnd: Date.now(),
            processedTimeStart: Date.now() - 1000000,
            processedTimeEnd: Date.now(),
            completedTimeStart: Date.now() - 1000000,
            completedTimeEnd: Date.now(),
            failedTimeStart: 0,
            failedTimeEnd: Date.now(),
        });

        let queryResponse = http.post(`${BASE_URL}/api/v1/transactions/query`, queryPayload, { headers: { 'Content-Type': 'application/json' } });

        check(queryResponse, {
            'transactions queried': (res) => res.status === 200
        });
    });

    sleep(1); // 总结性等待时间
}