package com.example.reggie.DTO;

import com.example.reggie.pojo.OrderDetail;
import com.example.reggie.pojo.Orders;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

    private int sumNum;

    private BigDecimal amount;

}