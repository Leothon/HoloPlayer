package com.holo.holoplayer.study;

/**
 * @Author: a10943
 * @Date: 2020/11/11
 * @Desc:
 */
public class FruitFactory {

    public Fruit createFruit(String type) {
        switch (type) {
            case "苹果" :
                return new Apple();
            case "橙子":
                return new Orange();
            default:
                throw new IllegalArgumentException("吃个屁");
        }
    }
}
