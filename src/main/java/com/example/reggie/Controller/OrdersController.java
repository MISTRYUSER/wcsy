package com.example.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.DTO.OrdersDto;
import com.example.reggie.Service.OrderDetailService;
import com.example.reggie.Service.OrdersService;
import com.example.reggie.common.Result;
import com.example.reggie.pojo.OrderDetail;
import com.example.reggie.pojo.Orders;
import com.example.reggie.pojo.Setmeal;
import com.sun.mail.imap.ResyncData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;


    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders)
    {
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return Result.success("下单成功");
    }

    /**
     * 查看订单详情
     * @param page
     * @param pageSize
     * @return
     */
//订单管理
//订单管理
    @Transactional
    @GetMapping("/userPage")
    public Result<Page> userPage(int page,int pageSize){
        //构造分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        Page<OrdersDto> ordersDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

        //进行分页查询
        ordersService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        List<Orders> records=pageInfo.getRecords();

        List<OrdersDto> list = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(item, ordersDto);
            Long Id = item.getId();
            //根据id查分类对象
            Orders orders = ordersService.getById(Id);
            String number = orders.getNumber();
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId,number);
            List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper);
            int num=0;

            for(OrderDetail l:orderDetailList){
                num+=l.getNumber().intValue();
            }

            ordersDto.setSumNum(num);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(list);

        return Result.success(ordersDtoPage);
    }


    //订单管理
    @GetMapping("/page")
    public Result<Page> page( int page, int pageSize, String number,
                              String beginTime,String endTime){
        //构造分页函数

        Page<Orders> pageInfo = new Page<Orders>(page, pageSize);

        Page<OrdersDto> dtoPage = new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //根据number进行模糊查询
        queryWrapper.like(!StringUtils.isEmpty(number),Orders::getNumber,number);
        log.info("开始时间：{}",beginTime);
        log.info("结束时间：{}",endTime);

        if(beginTime!=null &&endTime!=null) {
            queryWrapper.ge(Orders::getOrderTime, beginTime);
            queryWrapper.le(Orders::getOrderTime, beginTime);
        }
            //添加排序条件
            queryWrapper.orderByDesc(Orders::getOrderTime);
            //进行分页查询
            ordersService.page(pageInfo, queryWrapper);

            //对象拷贝
            BeanUtils.copyProperties(pageInfo,dtoPage,"recoreds");

            List<Orders> records = pageInfo.getRecords();

            List<OrdersDto> list = records.stream().map((item)->{
                OrdersDto dto = new OrdersDto();
                BeanUtils.copyProperties(item,dto);
                        String name = "用户"+item.getUserId();
                        dto.setUserName(name);
                        return dto;
            }).collect(Collectors.toList());

            dtoPage.setRecords(list);
            return Result.success(dtoPage);
        }

    /**
     * 派送管理
     * @param orders
     * @return
     */
    @PutMapping
    public Result<String> send(@RequestBody Orders orders){
            //获取id
            Long id = orders.getId();
            //获取更新状态
            Integer status = orders.getStatus();
            //获取构造器
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Orders::getId,id);
            Orders one = ordersService.getOne(queryWrapper);
            one.setStatus(status);
            ordersService.updateById(one);
            return Result.success("更新成功");

    }

    }

