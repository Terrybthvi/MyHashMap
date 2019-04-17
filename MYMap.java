/**
 * Copyright: Copyright (c) 2018
 * Project:
 * Author: bthvi
 * Date: 2019/4/10 09:27
 */
public interface MYMap<K,V> {
    //取数据
     V get(K k);
     //存数据
     V put(K k,V v);
     //内部接口
     interface Entry<K,V>{
         //取值
         V getValue();
         //取键
         K getKey();
    }
}
