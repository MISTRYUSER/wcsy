package com.example.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie.pojo.Dish;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.ibatis.annotations.Mapper;

import javax.tools.Diagnostic;
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}
