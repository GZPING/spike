package com.gd.spike.controller;


import com.gd.spike.base.beans.ResultBean;
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
@RequestMapping("/spike/orders")
public class OrdersController {

    public ResultBean<String> test(){
        return new ResultBean<>("ok");
    }
}