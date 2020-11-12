package com.holo.holoplayer.study;

/**
 * @Author: a10943
 * @Date: 2020/11/11
 * @Desc:
 */
public class OrangeFactory implements IFactory{

    @Override
    public Fruit create() {
        return new Orange();
    }
}
