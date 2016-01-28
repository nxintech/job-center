package com.nxin.framework.sharing;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by petzold on 2015/10/17.
 */
public class AverageAllocationJobShardingStrategy implements IJobShardingStrategy
{
    @Override
    public Map<String, List<Integer>> sharding(List<String> serversList, int shardingTotalCount)
    {
        if(serversList.size() > shardingTotalCount)
        {
            Map<String,List<Integer>> map = Maps.newHashMapWithExpectedSize(shardingTotalCount);
            for (int i=0;i<shardingTotalCount;i++)
            {
                map.put(serversList.get(i), Lists.newArrayList(i + 1));
            }
            return map;
        }
        Map<String, List<Integer>> result = shardingAliquot(serversList, shardingTotalCount);
        return result;
    }

    private Map<String, List<Integer>> shardingAliquot(List<String> serversList, int shardingTotalCount)
    {
        int sz = serversList.size();
        Map<String, List<Integer>> result = Maps.newHashMapWithExpectedSize(sz);
        int itemCountPerSharding = shardingTotalCount / sz;
        int remain = shardingTotalCount % sz;
        for (int i=0;i<sz;i++)
        {
            List<Integer> ls = Lists.newArrayList();
            for (int j=0;j<itemCountPerSharding;j++)
            {
                ls.add(i*itemCountPerSharding + j + 1);
            }
            if(i<remain)
            {
                ls.add(itemCountPerSharding * sz + i + 1);
            }
            result.put(serversList.get(i),ls);
        }
        return result;
    }
}
