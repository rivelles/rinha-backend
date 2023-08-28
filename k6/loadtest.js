import http from 'k6/http';
import { check, group } from "k6";

let locations = [];
const stacks = ["Swift", "Ruby", "Go", "Java", "Rust", "Swift", "Postgres", "Javascript", "Perl", "Java", "C"];

function randomString(length, charset = '') {
    if (!charset) charset = 'abcdefghijklmnopqrstuvwxyz';
    let res = '';
    while (length--) res += charset[(Math.random() * charset.length) | 0];
    return res;
}

function randomDate() {
    const startDate = new Date(1970, 0, 1);
    const endDate = new Date(2000, 0, 1);
    const timeDiff = endDate.getTime() - startDate.getTime();
    const randomTime = Math.random() * timeDiff;
    const randomDate = new Date(startDate.getTime() + randomTime);
    return randomDate.toISOString().slice(0, 10);
}

function generateTerm() {
    let useStacks = Math.random() > 0.5;
    if (useStacks) {
        return stacks[Math.floor(Math.random() * stacks.length)];
    }
    return locations[Math.floor(Math.random() * locations.length)].split("/")[2];
}

export let options = {
    stages: [
        // Ramp-up from 1 to 5 VUs in 5s
        { duration: "20s", target: 10 },
        // Stay at rest on 5 VUs for 10s
        { duration: "40s", target: 50 },
        // Ramp-down from 5 to 0 VUs for 5s
        { duration: "20s", target: 10 }
    ]
};

export default function () {
    const params = {
        headers: {
            "Content-Type": "application/json",
        },
    };

    group('Create pessoa', () => {
        const payload = JSON.stringify({
            apelido: randomString(10),
            nome: randomString(10),
            nascimento: randomDate(),
            stack: stacks.slice(Math.floor(Math.random() * stacks.length), Math.floor(Math.random() * stacks.length) + 1),
        });

        const response = http.post("http://nginx:9999/pessoas", payload, params);
        check(response, { "Create pessoa: status is 201": (r) => r.status === 201 });

        if (response.status === 201) {
            locations.push(response.headers["Location"]);
        }
    });

    group('Get pessoa', () => {
        const response = http.get("http://nginx:9999"+locations[Math.floor(Math.random() * locations.length)], params);

        check(response, { "Get pessoa: status is 200": (r) => r.status === 200 });
    });

    group('Search by term', () => {
        const response = http.get("http://nginx:9999/pessoas?t="+generateTerm(), params);

        check(response, { "Search by term: status is 200": (r) => r.status === 200 });
    });
};