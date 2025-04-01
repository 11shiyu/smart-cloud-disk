package com.yupi.yupicturebackend.manager.sharding;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * 图片分表算法，要自定义算法就要实现StandardShardingAlgorithm接口
 */
public class PictureShardingAlgorithm implements StandardShardingAlgorithm<Long> {

    /**
     *
     * @param availableTargetNames 所有支持的分表，这个字段是根据actual-data-nodes: yu_picture.picture来的
     * 因为spaceId是long类型 不能静态定义分表的范围，只能动态定义，所以这里只能在运行时动态更改actual-data-nodes这个值
     * @param preciseShardingValue 按照分表的属性
     * @return
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> preciseShardingValue) {
        Long spaceId = preciseShardingValue.getValue();
        String logicTableName = preciseShardingValue.getLogicTableName(); // 获得逻辑表
        // spaceId 为 null 表示查询所有图片
        if (spaceId == null) {
            return logicTableName;
        }
        // 根据 spaceId 动态生成分表名
        String realTableName = "picture_" + spaceId;
        if (availableTargetNames.contains(realTableName)) {
            return realTableName; //如果要查的表名在分表列表里，则返回实际表名
        } else { // 否则返回逻辑表名 再挨个遍历搜索
            return logicTableName;
        }
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        return new ArrayList<>();
    }

    @Override
    public Properties getProps() {
        return null;
    }

    @Override
    public void init(Properties properties) {

    }
}
