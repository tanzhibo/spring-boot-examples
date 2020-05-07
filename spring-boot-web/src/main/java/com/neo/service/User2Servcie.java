package com.neo.service;

import com.neo.model.User;
import com.neo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

@Service
@Transactional(rollbackFor = Exception.class)
public class User2Servcie {
    @Autowired
    private UserRepository userRepository;
    @Async
    public Future<User> save(User user) {
        User userUpdate = userRepository.getOne(user.getId());
        userUpdate.setNickName("tzb");
        User userS2 = userRepository.save(userUpdate);
        return new AsyncResult<User>(userS2);
    }

    @Async
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
}
