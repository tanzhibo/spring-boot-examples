package com.neo.web;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.neo.service.User2Servcie;
import com.neo.service.UserServcie;
import com.neo.util.ExecutorServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neo.model.User;
import com.neo.repository.UserRepository;

@RestController
@RequestMapping("/user")
@Slf4j
//@Transactional(rollbackFor = Exception.class)
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServcie userServcie;
    @Autowired
    private User2Servcie user2Servcie;

    @PostMapping
    public User getUser(User user) {
        try {
            return userServcie.save(user);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping
    public List<User> getUsers1() {
        List<User> users = userRepository.findAll();
        List<User> resulitList = new ArrayList<>();
        for (User user : users) {
            resulitList.add(getOne(user.getId()));
        }
        return resulitList;
    }

    @GetMapping("future")
    public List<User> future() {
        List<User> users = userRepository.findAll();
        List<Future<User>> futures = new ArrayList<>();
        getThreadPoolInfo();
        for (User user : users) {
            Future<User> future = ExecutorServiceUtil.EXECUTOR.submit(() -> getOne(user.getId()));
            futures.add(future);
        }
        List<User> resulitList = new ArrayList<>();
        User result;
        for (int k = 0; k < futures.size(); k++) {
            try {
                result = (User) futures.get(k).get();
                resulitList.add(result);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return resulitList;
    }

    @GetMapping("execute")
    public ConcurrentLinkedQueue<User> execute() {
        List<User> users = userRepository.findAll();
        List<Future<User>> futures = new ArrayList<>();
        getThreadPoolInfo();
        final CountDownLatch countDownLatch = new CountDownLatch(users.size());
        ConcurrentLinkedQueue<User> resulitList = new ConcurrentLinkedQueue<>();
        for (User user : users) {
            ExecutorServiceUtil.EXECUTOR.execute(() -> getOne(user, countDownLatch, resulitList));
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resulitList;
    }

    @GetMapping("Async")
    public ConcurrentLinkedQueue<User> async() {
        List<User> users = userRepository.findAll();
        List<Future<User>> futures = new ArrayList<>();
        final CountDownLatch countDownLatch = new CountDownLatch(users.size());
        ConcurrentLinkedQueue<User> resulitList = new ConcurrentLinkedQueue<>();
        for (User user : users) {
            user2Servcie.getOne(user, countDownLatch, resulitList);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resulitList;
    }

    public void getOne(User userP, CountDownLatch countDownLatch, ConcurrentLinkedQueue<User> resulitList) {
        /*try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        User user = userRepository.getOne(userP.getId());
        user.setNickName("tzb_test");
        resulitList.add(user);
        countDownLatch.countDown();
    }

    public User getOne(long id) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user = userRepository.getOne(id);
        user.setNickName("tzb_test");
        return user;
    }

    private void getThreadPoolInfo(){
        ThreadPoolExecutor tpe = ((ThreadPoolExecutor) ExecutorServiceUtil.EXECUTOR);
        System.out.println();
        int queueSize = tpe.getQueue().size();
        System.out.println("当前排队线程数："+ queueSize);

        int activeCount = tpe.getActiveCount();
        System.out.println("当前活动线程数："+ activeCount);

        long completedTaskCount = tpe.getCompletedTaskCount();
        System.out.println("执行完成线程数："+ completedTaskCount);

        long taskCount = tpe.getTaskCount();
        System.out.println("总线程数："+ taskCount);
    }
}