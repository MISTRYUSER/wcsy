package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.Service.OrderDetailService;
import com.example.reggie.mapper.OrderDetailMapper;
import com.example.reggie.pojo.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderDeatilServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    @Autowired
    private OrderDetailService orderDetailService;
    @Override
    public BigDecimal getValue(Long id) {
        LambdaQueryWrapper<OrderDetail>  queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(OrderDetail::getId,id);
        OrderDetail one = orderDetailService.getOne(queryWrapper);

        return one.getAmount();
    }
}
