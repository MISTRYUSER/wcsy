package com.example.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.Service.EmployeeService;
import com.example.reggie.common.Result;
import com.example.reggie.pojo.Employee;
import com.sun.net.httpserver.Authenticator;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import org.apache.commons.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Object> login(HttpServletRequest request, @RequestBody Employee employee) {

//        1、将页面提交的密码password进行 MD5 加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());

        Employee emp = employeeService.getOne(queryWrapper);
//        3、如果没有查询到数据，则返回登录失败的结果
        if(emp==null){
            return Result.error("用户名不存在！");
        }
//        4、进行密码比对，如果不一致，则返回登录失败的结果
        if(!emp.getPassword().equals(password)){
            return Result.error("登陆失败");
        }
//        5、查看员工状态，如果为 已禁用状态，则返回被禁用的结果信息?
        if(emp.getStatus() == 0)
        {
            return Result.error("账号已禁用");
        }
//        6、登录成功，将员工id 存入Session并返回登录成功的结果
        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return Result.success("登录成功");
    }
    @PostMapping
    public Result<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //获得当前登录用户的Id
        //Long empId = (Long)request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);
        return Result.success("新增员工成功");
    }

    /**
     * 员工信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageShow(int page, int pageSize, String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        // 创建分页构造器对象
        Page pageInfo = new Page(page,pageSize);

        //  构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //   name不为null，才会 比较 getUsername方法和前端传入的name是否匹配 的过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getUsername,name);
        //  根据 更新用户的时间升序 分页展示
//        queryWrapper.orderByAsc(Employee::getUpdateTime);

        // 去数据库查询
        employeeService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     *
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        // 下面设置 公共属性的值(createTime、updateTime、createUser、updateUser)交给 MyMetaObjectHandler类处理
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);

        long id = Thread.currentThread().getId();
        log.info("线程id为:{}",id);

        employeeService.updateById(employee);
        return Result.success("员工信息修改成功！");
    }

    /**
     * 根据id查询员工信息
     *
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return Result.success(employee);
        }
        else {
            return Result.error("没有查询到员工信息");
        }
    }
}
