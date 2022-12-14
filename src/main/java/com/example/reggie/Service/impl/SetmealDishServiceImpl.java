package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.DTO.SetmealDto;
import com.example.reggie.Service.SetmealDishService;
import com.example.reggie.Service.SetmealService;
import com.example.reggie.mapper.SetmealDishMapper;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.pojo.SetmealDish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish>
        implements SetmealDishService {

}