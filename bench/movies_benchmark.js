import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: Number(__ENV.VUS || 30),
  duration: __ENV.DURATION || "60s",
  thresholds: {
    http_req_failed: ["rate<0.01"],
  },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8090";
const LOGIN_USER = __ENV.LOGIN_USER || "quoc";
const LOGIN_PASS = __ENV.LOGIN_PASS || "q@123";
const ENDPOINT_MODE = (__ENV.ENDPOINT_MODE || "mixed").toLowerCase();

export function setup() {
  const payload = JSON.stringify({
    username: LOGIN_USER,
    password: LOGIN_PASS,
  });

  const res = http.post(`${BASE_URL}/api/login`, payload, {
    headers: { "Content-Type": "application/json" },
  });

  check(res, {
    "login status is 200": (r) => r.status === 200,
    "token exists": (r) => !!r.body && r.body.length > 20,
  });

  return { token: res.body };
}

export default function (data) {
  const headers = {
    Authorization: `Bearer ${data.token}`,
  };

  if (ENDPOINT_MODE === "list") {
    http.get(`${BASE_URL}/api/movies`, { headers });
  } else if (ENDPOINT_MODE === "id") {
    http.get(`${BASE_URL}/api/movies/1`, { headers });
  } else if (ENDPOINT_MODE === "search") {
    http.get(`${BASE_URL}/api/movies/search?keyword=action`, { headers });
  } else {
    const roll = Math.random();
    if (roll < 0.7) {
      http.get(`${BASE_URL}/api/movies`, { headers });
    } else if (roll < 0.9) {
      http.get(`${BASE_URL}/api/movies/1`, { headers });
    } else {
      http.get(`${BASE_URL}/api/movies/search?keyword=action`, { headers });
    }
  }

  sleep(0.2);
}
