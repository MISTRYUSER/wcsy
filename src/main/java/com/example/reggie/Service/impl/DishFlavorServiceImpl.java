package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.Service.DishFlavorService;
import com.example.reggie.mapper.DishFlavorMapper;
import com.example.reggie.pojo.DishFlavor;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}