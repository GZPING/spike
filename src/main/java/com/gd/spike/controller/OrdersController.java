package com.gd.spike.controller;


import com.gd.spike.base.beans.ResultBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author GD
 * @since 2019-04-15
 */
@RestController
@RequestMapping("orders")
public class OrdersController {

    @GetMapping("/")
    public ResultBean<String> test(){
        System.out.println(this);
        return new ResultBean<>("ok");
    }
}
