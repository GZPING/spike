package com.gd.spike.base.beans;

/**
 * Created by GD on 2019/4/22.
 */

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Description TODO
 * @Author GD
 * @Date 2019/4/22 21:22
 * @Version 1.0V
 */

@Data
public class ResultBean<T> implements Serializable {

    private boolean success;

    private T data;

    public ResultBean(T data) {
        this.success = true;
        this.data = data;
    }

    public ResultBean(boolean success, T data) {
        this.success = success;
        this.data = data;
    }
}
