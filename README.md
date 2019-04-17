# MyHashMap
手写HashMap
## 1、定义接口
我们知道HashMap是实现了Map接口的，我们也需要定义一个类似的接口，主要包括存取数据，及存储数据的结构接口（包括取键和取值）。我们定义如下：
 
```
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
```
## 2、自定义Map实现自定义的接口

下面我们需要实现自定义的接口,并且我们要定义默认的容量，默认加载因子（0.75），以及数组使用的长度，还有存储的数组。
```
import java.util.ArrayList;
import java.util.List;
 
/**
 * Copyright: Copyright (c) 2018
 * Project:
 * Author: bthvi
 * Date: 2019/4/10 09:32
 */
public class MYHashMap<K,V> implements MYMap<K,V> {
 
    //默认容量
    private  int DEFAULT_LENGTH = 16;
    //加载因子
    private  float LOADER = 0.75f;
    //Map使用数组长度
    private int useSize = 0;
    //数组
    private Entry<K,V>[] table;
 
    public MYHashMap() {
        this(16,0.75f);
    }
 
    public MYHashMap(int defaultLength, float loader) {
        if (defaultLength < 0){
            throw new IllegalArgumentException("数组异常");
        }
        if (loader <= 0 || Float.isNaN(loader)){
            throw  new IllegalArgumentException("负载因子异常");
        }
        this.DEFAULT_LENGTH = defaultLength;
        this.LOADER = loader;
        table = new Entry[DEFAULT_LENGTH];
    }
 
    /**
     * 取
     * @param k 键值
     * @return
     */
    @Override
    public V get(K k) {
        return null;
    }
 
    /**
     * 存
     * @param k 键值
     * @param v 值
     * @return
     */
    @Override
    public V put(K k, V v) {
        return null;
    }
 
}
```
## HashMap的存储结构
下面我们需要实现内部接口，来自定义它每个点的存储结构，需要有键，值，及指向下一节点的对象
```
   //存储结构
    class Entry<K,V> implements MYMap.Entry<K,V>{
 
        //键
        private K key;
        //值
        private V value;
 
        //链表指向下一个
        private Entry<K,V> next;
 
        public Entry(K k,V v,Entry<K,V> entry){
            this.key = k;
            this.value = v;
            this.next = entry;
        }
 
        @Override
        public V getValue() {
            return value;
        }
 
        @Override
        public K getKey() {
            return key;
        }
    }
```
## 实现HashMap的HashCode来计算下标
我们需要自己实现HashCode来自定义确定key所对应的下标。
```
    /**
     * 使用每个object的hashCode计算hashCode
     * @param hashCode
     * @return  hashCode
     */
    private int hash(int hashCode) {
        hashCode = hashCode ^ ((hashCode >>> 20) ^ (hashCode >>> 12));
        return hashCode ^ ((hashCode >>> 7) ^ hashCode >>> 4);
    }
 
    /**
     * 获取下标
     * @param k 键值
     * @param length  数组长度
     * @return 下标
     */
    private int getIndex(K k,int length){
        int m = length - 1;
        int index = hash(k.hashCode()) & m;
        return index >= 0 ? index : -index;
    }
```
## 实现HashMap的put方法
HashMap 的put方法就是通过key确定对于数组的下标，再确定有没有哈希冲突，有则往后追加，没有则直接存放。
```
/**
     * 存
     * @param k 键值
     * @param v 值
     * @return
     */
    @Override
    public V put(K k, V v) {
        //扩容
        if (useSize >= DEFAULT_LENGTH * LOADER){
            resize();
        }
        //计算下标
        int index = getIndex(k,table.length);
        Entry<K,V> entry = table[index];
        Entry<K,V> newEntry = new Entry<K,V>(k,v,null);
        if (null == entry){//直接插入第一个
            table[index] = newEntry;
            useSize ++;
        }else {
            Entry<K,V> t= entry;
            if (t.getKey() == k || (t.getKey() != null && t.getKey().equals(k))){//已经存在，替换
                t.value = v;
            }else {
                while (t.next != null){
                    if (t.getKey() == k || (t.getKey() != null && t.getKey().equals(k))) {//已经存在替换
                        t.value = v;
                        break;
                    }else {
                        t = t.next;
                    }
                }
                if (t.next == null){//不存在，追加到链表后面
                    t.next = newEntry;
                }
            }
        }
        return newEntry.getValue();
    }
```    
当然我们存储的时候还要考虑到HashMap的扩容，下面我们实现HashMap的扩容方法

## 实现HashMap的扩容resize
首先，我们将数组的长度乘以2，再将原来的数据重新塞到新数组中，注意这个时候存储的会变掉（因为计算下标和数组长度有关）。
```
    /**
     * 扩容
     */
   private void resize() {
      Entry<K,V>[]  newTable = new Entry[DEFAULT_LENGTH * 2];
       List<Entry<K,V>> list = new ArrayList<>();
       //循环遍历取出旧数组的数据
       for (int i = 0; i < table.length; i++){
           if (null == table[i]){
               continue;
           }
           Entry<K,V> entry = table[i];
           while (null != entry){
               list.add(entry);
               entry = entry.next;
           }
       }
 
       //重新塞进数组
       if (list.size() > 0){
           useSize = 0;
           DEFAULT_LENGTH = DEFAULT_LENGTH * 2;
           table = newTable;
           for (Entry<K,V> entry : list){
               if (entry.next != null){
                   entry.next = null;
               }
               put(entry.getKey(),entry.getValue());
           }
       }
 
   }
```
## 实现HashMap的get方法
上面我们实现了HashMap的put方法，下面我们来实现HashMap的get方法。
```
    /**
     * 取
     * @param k 键值
     * @return
     */
    @Override
    public V get(K k) {
        int index = getIndex(k,table.length);
        Entry<K,V> entry = table[index];
        if (null != entry){
            while (entry.next != null){
                if (entry.getKey() == k || (entry.getKey() != null && entry.getKey().equals(k))){
                    return entry.getValue();
                }else {
                    entry = entry.next;
                }
            }
            if (entry.next == null ){
                if (entry.getKey() == k || (entry.getKey() != null && entry.getKey().equals(k))){
                    return entry.getValue();
                }
            }
        }else {
            throw new NullPointerException();
        }
        return null;
    }
```    
## 总结
至此，缩减版的HashMap就实现了。
