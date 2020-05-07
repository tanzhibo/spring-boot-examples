package com.neo.service;

import com.neo.model.User;
import com.neo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServcie {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private User2Servcie user2Servcie;
    public User save(User user) throws InterruptedException, ExecutionException {
        user.setId(4l);
        User userS = userRepository.save(user);
        user2Servcie.save(userS);
        User userUpdate = userRepository.getOne(userS.getId());
        userUpdate.setEmail("tzb");
        User userS2 = userRepository.save(userUpdate);
        return userS2;
    }


    public void getOne(User userP, CountDownLatch countDownLatch, ConcurrentLinkedQueue<User> resulitList) {
        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        User user = userRepository.getOne(userP.getId());
        user.setNickName("tzb_test");
        resulitList.add(user);
        countDownLatch.countDown();
    }
}
