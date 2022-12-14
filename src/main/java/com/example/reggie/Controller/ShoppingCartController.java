package com.example.reggie.Controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.Service.ShoppingCartService;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Result;
import com.example.reggie.mapper.ShoppingCartMapper;
import com.example.reggie.pojo.ShoppingCart;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift;
import org.apache.ibatis.annotations.Case;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
        @Autowired
        private ShoppingCartService shoppingCartService;

        @PostMapping("/add")
        public Result<ShoppingCart> addToCart(@RequestBody ShoppingCart shoppingCart){
            log.info("购物车中的数据:{}"+shoppingCart.toString());

            //设置用户id,指定当前是哪个用户的 购物车数据
            Long userId = BaseContext.getCurrentId();
            shoppingCart.setUserId(userId);

            // 查询当前菜品或套餐是否 在购物车中
            Long dishId = shoppingCart.getDishId();

            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getUserId,userId);  // 根据登录用户的 userId去ShoppingCart表中查询该用户的购物车数据

            if (dishId != null){ // 添加进购物车的是菜品，且 购物车中已经添加过 该菜品
                queryWrapper.eq(ShoppingCart::getDishId,dishId);
            }else {
                queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            }


            ShoppingCart oneCart = shoppingCartService.getOne(queryWrapper);
            //  如果购物车中 已经存在该菜品或套餐，其数量+1，不存在，就将该购物车数据保存到数据库中
            if (oneCart != null){
                Integer number = oneCart.getNumber();
                oneCart.setNumber(number + 1);

                shoppingCartService.updateById(oneCart);
            }else {
                shoppingCart.setNumber(1);
                shoppingCartService.save(shoppingCart);

                oneCart = shoppingCart;
            }


            return Result.success(oneCart);
        }

    @PostMapping("/sub")
    @Transactional
    public Result<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        //代表数量减少的是菜品数量
        if (dishId != null) {
            //通过dishId查出购物车对象
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            //这里必须要加两个条件，否则会出现用户互相修改对方与自己购物车中相同套餐或者是菜品的数量
            queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart cart1 = shoppingCartService.getOne(queryWrapper);
            cart1.setNumber(cart1.getNumber() - 1);
            Integer LatestNumber = cart1.getNumber();
            if (LatestNumber > 0) {
                //对数据进行更新操作
                shoppingCartService.updateById(cart1);
            } else if (LatestNumber == 0) {
                //如果购物车的菜品数量减为0，那么就把菜品从购物车删除
                shoppingCartService.removeById(cart1.getId());
            } else if (LatestNumber < 0) {
                return Result.error("操作异常");
            }

            return Result.success(cart1);
        }

        Long setmealId = shoppingCart.getSetmealId();
        if (setmealId != null) {
            //代表是套餐数量减少
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId).eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart cart2 = shoppingCartService.getOne(queryWrapper);
            cart2.setNumber(cart2.getNumber() - 1);
            Integer LatestNumber = cart2.getNumber();
            if (LatestNumber > 0) {
                //对数据进行更新操作
                shoppingCartService.updateById(cart2);
            } else if (LatestNumber == 0) {
                //如果购物车的套餐数量减为0，那么就把套餐从购物车删除
                shoppingCartService.removeById(cart2.getId());
            } else if (LatestNumber < 0) {
                return Result.error("操作异常");
            }
            return Result.success(cart2);
        }
        //如果两个大if判断都进不去
        return Result.error("操作异常");

    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list  =  shoppingCartService.list(queryWrapper);
        return Result.success(list);
    }


        @DeleteMapping("/clean")
        public Result<String> cleanCart(){
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

            shoppingCartService.remove(queryWrapper);
            return Result.success("清除成功");
        }


    }