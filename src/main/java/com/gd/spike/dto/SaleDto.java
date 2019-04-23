package com.gd.spike.dto;

/**
 * Created by GD on 2019/4/23.
 */

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @Description TODO
 * @Author GD
 * @Date 2019/4/23 23:10
 * @Version 1.0V
 */
@Data
public class SaleDto {

    private String name;

    private BigDecimal price;

    private String fee;

    private String sales;

    private String comment;

    private String city;

    private String nick;

    private String img;

    private String detailUrl;
}
