#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
压力测试脚本：随机生成用户名/密码，对 /user/register.iapp 进行 1 万次 POST 请求。
使用方法：
    python stress_test.py
可在脚本中修改 URL、请求次数、并发线程数等参数。
"""

import requests
import json
import random
import string
import time
from concurrent.futures import ThreadPoolExecutor, as_completed

# ==================== 配置参数 ====================
URL = "http://localhost:8080/user/register.iapp"  # 目标接口地址
TOTAL_REQUESTS = 20000                             # 总请求次数
CONCURRENT_WORKERS = 100                           # 并发线程数（提高到100测试连接池优化）
TIMEOUT = 10                                       # 请求超时时间（秒）
# =================================================

# 生成随机用户名（10 位数字）
def random_username():
    return ''.join(random.choices(string.digits, k=10))

# 生成随机密码（8-12 位，包含数字和字母）
def random_password():
    length = random.randint(8, 12)
    chars = string.ascii_letters + string.digits
    return ''.join(random.choices(chars, k=length))

# 发送单个请求并记录结果
def send_request(session, request_id):
    username = random_username()
    password = random_password()
    payload = {
        "username": username,
        "password": password
    }
    headers = {
        "Content-Type": "application/json; charset=UTF-8"
    }
    start_time = time.time()
    status = "fail"
    try:
        # 使用 session 保持连接，提高性能
        response = session.post(URL, json=payload, headers=headers, timeout=TIMEOUT)
        elapsed = time.time() - start_time
        # 判断成功标准：通常 HTTP 2xx 或特定业务码可视为成功，这里以状态码 < 400 为成功
        if response.status_code < 400:
            status = "success"
        else:
            status = f"http_error_{response.status_code}"
        return {
            "id": request_id,
            "status": status,
            "elapsed": elapsed,
            "username": username,
            "status_code": response.status_code
        }
    except Exception as e:
        elapsed = time.time() - start_time
        return {
            "id": request_id,
            "status": "exception",
            "elapsed": elapsed,
            "username": username,
            "error": str(e)
        }

def main():
    print(f"开始压力测试：目标 URL = {URL}")
    print(f"总请求数 = {TOTAL_REQUESTS}, 并发线程数 = {CONCURRENT_WORKERS}\n")

    # 创建一个会话，用于连接复用
    session = requests.Session()
    results = []
    start_total = time.time()

    # 使用线程池并发执行
    with ThreadPoolExecutor(max_workers=CONCURRENT_WORKERS) as executor:
        futures = [executor.submit(send_request, session, i) for i in range(1, TOTAL_REQUESTS + 1)]
        # 实时显示进度
        for i, future in enumerate(as_completed(futures), 1):
            result = future.result()
            results.append(result)
            if i % 500 == 0 or i == TOTAL_REQUESTS:
                print(f"已完成 {i}/{TOTAL_REQUESTS} 请求...")

    total_time = time.time() - start_total

    # 统计结果
    total = len(results)
    successes = [r for r in results if r["status"] == "success"]
    http_errors = [r for r in results if r["status"].startswith("http_error")]
    exceptions = [r for r in results if r["status"] == "exception"]

    success_count = len(successes)
    http_error_count = len(http_errors)
    exception_count = len(exceptions)

    elapsed_times = [r["elapsed"] for r in results]
    avg_elapsed = sum(elapsed_times) / total if total else 0
    max_elapsed = max(elapsed_times) if elapsed_times else 0
    min_elapsed = min(elapsed_times) if elapsed_times else 0

    print("\n========== 测试结果统计 ==========")
    print(f"总请求数        : {total}")
    print(f"总耗时          : {total_time:.2f} 秒")
    print(f"平均每秒请求数  : {total / total_time:.2f} req/s")
    print(f"成功请求数      : {success_count} (成功率 {success_count/total*100:.2f}%)")
    print(f"HTTP错误数      : {http_error_count}")
    print(f"异常请求数      : {exception_count}")
    print(f"平均响应时间    : {avg_elapsed*1000:.2f} ms")
    print(f"最大响应时间    : {max_elapsed*1000:.2f} ms")
    print(f"最小响应时间    : {min_elapsed*1000:.2f} ms")

    # 可选：将详细结果保存到文件
    with open("stress_test_results.json", "w", encoding="utf-8") as f:
        json.dump(results, f, indent=2, ensure_ascii=False)
    print("\n详细结果已保存至 stress_test_results.json")

if __name__ == "__main__":
    main()