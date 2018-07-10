package com.sv.mc.service.impl;

import com.sv.mc.pojo.sysUserEntity;
import com.sv.mc.repository.UserRepository;
import com.sv.mc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService<sysUserEntity> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public int specificCalculate() {
        return 1 + 1;
    }

    @Override
    @Transactional
    @Cacheable
    public List<sysUserEntity> findAllEntities() {
        return this.userRepository.findAll();
    }

    @Override
    @Transactional
    public List<sysUserEntity> findEntitiesPager(){
        PageRequest pageRequest = new PageRequest(0,5);
        Page<sysUserEntity> userEntityPage = userRepository.findAll(pageRequest);
        return userEntityPage.getContent();
    }

    @Override
    @Transactional
    public sysUserEntity findUserByUserName(String userName) {
        return this.userRepository.findUserByUserName(userName);
    }
}