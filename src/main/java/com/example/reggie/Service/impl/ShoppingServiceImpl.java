package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.Service.ShoppingCartService;
import com.example.reggie.mapper.ShoppingCartMapper;
import com.example.reggie.pojo.ShoppingCart;
import org.springframework.stereotype.Service;

@Service
public class ShoppingServiceImpl extends ServiceImpl<ShoppingCartMapper,ShoppingCart> implements ShoppingCartService{
}
