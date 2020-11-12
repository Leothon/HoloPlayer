package com.holo.holoplayer.study;

/**
 * @Author: a10943
 * @Date: 2020/11/11
 * @Desc:
 */
public class AppleFactory implements IFactory{
    @Override
    public Fruit create() {
        return new Apple();
    }
}
