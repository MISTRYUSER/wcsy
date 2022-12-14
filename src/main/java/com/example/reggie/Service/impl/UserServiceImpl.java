package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.Service.UserService;
import com.example.reggie.Utils.SMSUtils;
import com.example.reggie.mapper.UserMapper;
import com.example.reggie.pojo.OrderDetail;
import com.example.reggie.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{
}