package com.example.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.pojo.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     */
    public void submit(Orders orders);
}
