package com.example.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.pojo.OrderDetail;

import java.math.BigDecimal;

public interface OrderDetailService extends IService<OrderDetail> {
    public BigDecimal getValue(Long id);
}
