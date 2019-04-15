package com.gd.spike.service.impl;

import com.gd.spike.entity.Orders;
import com.gd.spike.mapper.OrdersMapper;
import com.gd.spike.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author GD
 * @since 2019-04-15
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}
