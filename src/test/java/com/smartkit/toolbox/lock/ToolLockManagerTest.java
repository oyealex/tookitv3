package com.smartkit.toolbox.lock;

import com.smartkit.toolbox.service.DeviceLockService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ToolLockManager 单元测试
 */
class ToolLockManagerTest {

    @Test
    void testTryLockSuccess() {
        ToolLockManager lockManager = new ToolLockManager();

        ToolLockManager.LockResult result = lockManager.tryLock("tool-a", List.of("192.168.1.1"), 1000);

        assertTrue(result.success());
        assertTrue(lockManager.isRunning());
        assertNotNull(lockManager.getRunningToolInfo());

        // 释放锁
        lockManager.unlock();
        assertFalse(lockManager.isRunning());
    }

    @Test
    void testTryLockTwice() throws InterruptedException {
        ToolLockManager lockManager = new ToolLockManager();

        // 第一次获取锁
        ToolLockManager.LockResult result1 = lockManager.tryLock("tool-a", List.of("192.168.1.1"), 1000);
        assertTrue(result1.success());
        assertTrue(lockManager.isRunning());

        // 使用另一个线程尝试获取锁
        final boolean[] secondLockResult = {false};
        Thread thread = new Thread(() -> {
            ToolLockManager.LockResult r = lockManager.tryLock("tool-b", List.of("192.168.1.2"), 200);
            secondLockResult[0] = r.success();
        });
        thread.start();
        thread.join();

        // 第二个线程应该无法获取锁
        assertFalse(secondLockResult[0], "另一个线程应该无法获取锁");

        // 释放锁
        lockManager.unlock();
        assertFalse(lockManager.isRunning());
    }

    @Test
    void testTryLockWithDeviceLockService() {
        // 模拟设备锁服务
        DeviceLockService mockService = new DeviceLockService() {
            @Override
            public void lockDevices(List<String> ips) throws LockException {
                // 模拟锁定成功
            }

            @Override
            public void unlockDevices(List<String> ips) {
                // 模拟解锁成功
            }

            @Override
            public boolean isLocked(String ip) {
                return false;
            }
        };

        ToolLockManager lockManager = new ToolLockManager(mockService);

        ToolLockManager.LockResult result = lockManager.tryLock("tool-a", List.of("192.168.1.1"), 1000);
        assertTrue(result.success());

        lockManager.unlock();
    }

    @Test
    void testTryLockWithDeviceLockServiceFailure() {
        // 模拟设备锁服务失败
        DeviceLockService mockService = new DeviceLockService() {
            @Override
            public void lockDevices(List<String> ips) throws LockException {
                throw new LockException("设备已被锁定");
            }

            @Override
            public void unlockDevices(List<String> ips) {
            }

            @Override
            public boolean isLocked(String ip) {
                return true;
            }
        };

        ToolLockManager lockManager = new ToolLockManager(mockService);

        ToolLockManager.LockResult result = lockManager.tryLock("tool-a", List.of("192.168.1.1"), 1000);
        assertFalse(result.success());
        assertTrue(result.message().contains("设备已被锁定"));
    }

    @Test
    void testUnlockWithoutLock() {
        ToolLockManager lockManager = new ToolLockManager();

        // 未获取锁时调用 unlock 不应抛异常
        assertDoesNotThrow(() -> lockManager.unlock());
        assertFalse(lockManager.isRunning());
    }
}